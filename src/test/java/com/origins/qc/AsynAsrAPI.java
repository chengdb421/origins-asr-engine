/**
 * @(#) AsynAsrAPI.java ASR引擎
 */
package com.origins.qc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.joda.time.DateTime;

import com.baidu.acu.pie.client.AsrClient;
import com.baidu.acu.pie.client.AsrClientFactory;
import com.baidu.acu.pie.client.Consumer;
import com.baidu.acu.pie.exception.AsrException;
import com.baidu.acu.pie.model.AsrConfig;
import com.baidu.acu.pie.model.AsrProduct;
import com.baidu.acu.pie.model.RecognitionResult;
import com.baidu.acu.pie.model.StreamContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@Slf4j
public class AsynAsrAPI {
	private static String appName = "hello";
	private static String ip = "192.168.66.194"; // asr服务的ip地址
	private static Integer port = 8051; // asr服务的端口
	private static AsrProduct pid = AsrProduct.CUSTOMER_SERVICE_FINANCE; // asr模型编号(不同的模型在不同的场景下asr识别的最终结果可能会存在很大差异)
	private static String userName = "admin"; // 用户名, 请联系百度相关人员进行申请
	private static String passWord = "1234567809"; // 密码, 请联系百度相关人员进行申请
	private static String audioPath = "e3fed53f-70e2-41ba-9786-88c836c4e029.pcm"; // 音频文件路径

	public static void main(String[] args) {
		asyncRecognizeWithStream(createAsrClient());
	}

	private static AsrClient createAsrClient() {
		// 创建调用asr服务的客户端
		// asrConfig构造后就不可修改
		AsrConfig asrConfig = AsrConfig.builder().appName(appName).serverIp(ip).serverPort(port).product(pid)
				.userName(userName).password(passWord).build();
		return AsrClientFactory.buildClient(asrConfig);
	}

	private static void asyncRecognizeWithStream(AsrClient asrClient) {
		final AtomicReference<DateTime> beginSend = new AtomicReference<DateTime>();
		final StreamContext streamContext = asrClient.asyncRecognize(new Consumer<RecognitionResult>() {
			public void accept(RecognitionResult recognitionResult) {
				DateTime now = DateTime.now();
				System.out.println(now.toString() + "\ttime_used=" + (now.getMillis() - beginSend.get().getMillis())
						+ "ms" + "\tfragment=" + recognitionResult + "\tthread_id=" + Thread.currentThread().getId());
			}
		});
		// 异常回调
		streamContext.enableCallback(new Consumer<AsrException>() {
			public void accept(AsrException e) {
				log.error("Exception recognition for asr ：", e);
			}
		});

		try {
			// 这里从文件中得到一个输入流InputStream，实际场景下，也可以从麦克风或者其他音频源来得到InputStream
			final FileInputStream audioStream = new FileInputStream(audioPath);
			// 实时音频流的情况下，8k音频用320， 16k音频用640
			final byte[] data = new byte[asrClient.getFragmentSize()];
			// 创建延时精确的定时任务
			ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
			final CountDownLatch sendFinish = new CountDownLatch(1);
			// 控制台打印每次发包大小
			System.out.println(new DateTime().toString() + "\t" + Thread.currentThread().getId()
					+ " start to send with package size=" + asrClient.getFragmentSize());
			// 设置发送开始时间
			beginSend.set(DateTime.now());
			// 开始执行定时任务
			executor.scheduleAtFixedRate(new Runnable() {
				public void run() {
					try {
						int count = 0;
						// 判断音频有没有发送和处理完成
						if ((count = audioStream.read(data)) != -1 && !streamContext.getFinishLatch().finished()) {
							// 发送音频数据包
							streamContext.send(data);
						} else {
							// 音频处理完成，置0标记，结束所有线程任务
							sendFinish.countDown();
						}
					} catch (AsrException | IOException e) {
						e.printStackTrace();
						// 异常时，置0标记，结束所有线程任务
						sendFinish.countDown();
					}

				}
			}, 0, 20, TimeUnit.MILLISECONDS); // 0:第一次发包延时； 20:每次任务间隔时间; 单位：ms
			// 阻塞主线程，直到CountDownLatch的值为0时停止阻塞
			sendFinish.await();
			System.out.println(new DateTime().toString() + "\t" + Thread.currentThread().getId() + " send finish");

			// 结束定时任务
			executor.shutdown();
			streamContext.complete();
			// 等待最后输入的音频流识别的结果返回完毕（如果略掉这行代码会造成音频识别不完整!）
			streamContext.await();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			asrClient.shutdown();
		}
		System.out.println("all task finished");
	}
}
