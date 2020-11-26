/**
 * @(#) ApplicationContextConfiguration.java ASR引擎
 */
package com.origins.asr.api;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.origins.evaluate.keyword.SimpleKeywordEvaluateModelConfiguration;

/**
 * @author 智慧工厂@M
 *
 */
@Configuration
public class ApplicationContextConfiguration {
	@Value("${concurrent.max.threads}")
	private int maxThreads;

	@Value("${wansun.asr.server.concurrent}")
	private int wansunAsrMaxThreads;

	@Value("${evaluate.concurrent}")
	private int evaluateMaxConrrent;

	@Value("${evaluate.model.simple.config}")
	private String evaluateSimpleKeywordModelConfiguration;

	@Value("${baidu.app.id}")
	private String baiduAppId;

	@Value("${baidu.api.key}")
	private String baiduApiKey;

	@Value("${baidu.secret.key}")
	private String baiduSecretKey;

	@Value("${file.temporary.path}")
	private String temporaryMediaPath;

	@Value("${wansun.asr.server.ip}")
	private String wansunAsrServerIp;

	@Value("${wansun.asr.server.port}")
	private int wansunAsrServerPort;

	@Value("${wansun.asr.server.username}")
	private String wansunAsrServerUsername;

	@Value("${wansun.asr.server.password}")
	private String wansunAsrServerPassword;

	@Bean(name = "major-disptcher-threadpool")
	public ExecutorService createExecutorMajor() {
		return Executors.newFixedThreadPool(maxThreads);
	}

	@Bean(name = "wansun-asr-disptcher-threadpool")
	public ExecutorService createExecutorWansunAsr() {
		return Executors.newFixedThreadPool(wansunAsrMaxThreads);
	}

	@Bean(name = "evaluate-disptcher-threadpool")
	public ExecutorService createExecutorEvaluate() {
		return Executors.newFixedThreadPool(evaluateMaxConrrent);
	}

	@Bean
	public Queue createQueue() {
		return new Queue("asr_task_result_notify_queue", true);
	}

	@Bean
	public File createTemporaryMediaPath() {
		File file = new File(temporaryMediaPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	@Bean
	public ForkJoinPool createForkJoinPool() {
		return new ForkJoinPool(200);
	}

	@Bean
	public SimpleKeywordEvaluateModelConfiguration createSimpleKeywordEvaluateModelConfiguration() {
		return new SimpleKeywordEvaluateModelConfiguration(this.evaluateSimpleKeywordModelConfiguration);
	}
}
