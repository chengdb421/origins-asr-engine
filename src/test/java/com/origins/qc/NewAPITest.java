/**
 * @(#) NewAPITest.java ASR引擎
 */
package com.origins.qc;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.baidu.acu.pie.client.AsrClient;
import com.baidu.acu.pie.client.AsrClientFactory;
import com.baidu.acu.pie.model.AsrConfig;
import com.baidu.acu.pie.model.AsrProduct;
import com.baidu.acu.pie.model.RecognitionResult;
import com.baidu.acu.pie.model.RequestMetaData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Slf4j
public class NewAPITest {
	private final String appName;
	private final String ip;
	private final int port;
	private final AsrProduct pid;
	private final String username;
	private final String password;

	void performAsr(String audioPath) {
		AsrConfig asrConfig = AsrConfig.builder().appName(appName).serverIp(ip).serverPort(port).product(pid)
				.userName(username).password(password).build();
		AsrClient asrClient = AsrClientFactory.buildClient(asrConfig);
		// 创建RequestMetaData
		RequestMetaData requestMetaData = new RequestMetaData();
		requestMetaData.setEnableFlushData(false);// 是否返回中间翻译结果
		requestMetaData.setSendPackageRatio(20);
//		requestMetaData.setTimeoutMinutes(30);
		List<RecognitionResult> results = asrClient.syncRecognize(new File(audioPath), requestMetaData);
		log.info("###########" + results.size());
		asrClient.shutdown();
		printResult(audioPath, results);
	}

	void printResult(String audioPath, List<RecognitionResult> results) {
		for (RecognitionResult result : results) {
			System.out.println(String.format(AsrConfig.TITLE_FORMAT, "file_name", "trace_id", "serial_num",
					"start_time", "end_time", "result"));
			System.out.println(String.format(AsrConfig.TITLE_FORMAT, audioPath, result.getTraceId(),
					result.getSerialNum(), result.getStartTime(), result.getEndTime(), result.getResult()));
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("###");
		log.debug("1234");
		Executor exec = Executors.newFixedThreadPool(1);
		for (int i = 0; i < 2; i++) {
			try {
				NewAPITest api = new NewAPITest("sample", "192.168.66.194", 8051, AsrProduct.CUSTOMER_SERVICE, "admin",
						"1234567809");
				exec.execute(() -> api.performAsr("e3fed53f-70e2-41ba-9786-88c836c4e029.pcm"));
			} catch (Exception e) {
				System.out.println("错误:::" + e);
			}
		}
	}

}
