/**
 * @(#) LocalAsrProc.java ASR引擎
 */
package com.origins.asr.engine.wsbd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baidu.acu.pie.client.AsrClient;
import com.baidu.acu.pie.client.Consumer;
import com.baidu.acu.pie.exception.AsrException;
import com.baidu.acu.pie.model.RecognitionResult;
import com.baidu.acu.pie.model.StreamContext;
import com.origins.asr.api.AsrClientFactory;
import com.origins.utils.Ref;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@Component
@Slf4j
public class LocalAsrProc {
	@Autowired
	private AsrClientFactory asrFactory;

	public List<RecognitionResult> recognize(byte[] payload) {
		return recognize(new ByteArrayInputStream(payload));
	}

	public List<RecognitionResult> recognize(InputStream audioStream) {
		log.info("本地引擎开始工作");
		AsrClient asrClient = asrFactory.createAsrClient();
		List<RecognitionResult> results = new ArrayList<>();
		Ref<AsrException> err = new Ref<>();
		final AtomicReference<DateTime> beginSend = new AtomicReference<DateTime>();
		final StreamContext streamContext = asrClient.asyncRecognize(new Consumer<RecognitionResult>() {
			public void accept(RecognitionResult recognitionResult) {
				if (recognitionResult.isCompleted()) {
					log.info("Adding: " + recognitionResult);
					results.add(recognitionResult);
				}
			}
		});
		// 异常回调
		streamContext.enableCallback(new Consumer<AsrException>() {
			public void accept(AsrException e) {
				e.printStackTrace();
				log.error("Exception recognition for asr ：", e);
				err.setReference(e);
			}
		});

		try {
			// 这里从文件中得到一个输入流InputStream，实际场景下，也可以从麦克风或者其他音频源来得到InputStream
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

		if (!err.isFree()) {
			throw err.getReference();
		}

		return results;
	}
}
