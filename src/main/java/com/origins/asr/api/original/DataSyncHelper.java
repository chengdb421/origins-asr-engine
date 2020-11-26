/**
 * @(#) DataSyncHelper.java ASR引擎
 */
package com.origins.asr.api.original;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.origins.asr.api.GenericScheduler;
import com.origins.asr.api.mapper.AsrModelsMapper;
import com.origins.asr.api.models.AsrResultFeedbackMessage;
import com.origins.asr.api.models.AsrResultSyncMessage;
import com.origins.asr.api.models.AsrTaskStates;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@Slf4j
@Component
public class DataSyncHelper {
	@Value("${original.qc.feedback.url}")
	private String originalQCSystemFeedbackURL;

	@Autowired
	@Qualifier("major-disptcher-threadpool")
	private ExecutorService pool;

	@Autowired
	private GenericScheduler scheduler;

	@Autowired
	private AsrModelsMapper mapper;

	@Autowired
	private AmqpTemplate amqpTemplate;

	public void process(Date beginAt) {
		mapper.findAllTasks(beginAt).forEach(task -> pool.execute(() -> {
			log.info("sync -> " + task);
			AsrResultSyncMessage payload = new AsrResultSyncMessage();
			payload.setErrorMessage(task.getErrorMessage());
			payload.setRequestId(task.getRequestId());
			payload.setResultText(task.getResultText());
			payload.setState(task.getState().toString());
			payload.setUuid(task.getUuid());
			log.info("发送MQ: " + payload);
			fireRabbitMessage("asr_task_result_sync_queue", payload);

			AsrResultFeedbackMessage feedback = AsrResultFeedbackMessage.builder().audioUrl(task.getRawMediaUrl())
					.status(task.getState().toString()).projectUUID(task.getProjectUUID()).build();
			if (task.getState() == AsrTaskStates.ERROR) {
				feedback.setMessage(task.getErrorMessage());
			} else {
				feedback.setMessage("ok");
			}

			// asr_callback_queue
			fireRabbitMessage("asr_callback_queue", feedback);
		}));
	}

	void fireRabbitMessage(String queueName, Object payload) {
		Message rabbitMessage = MessageBuilder.withBody(JSON.toJSONString(payload).getBytes())
				.setContentType(org.springframework.amqp.core.MessageProperties.CONTENT_TYPE_BYTES)
				.setContentEncoding("utf-8").setMessageId(UUID.randomUUID() + "").build();
		amqpTemplate.convertAndSend(queueName, rabbitMessage);
	}

	@SneakyThrows
	public void process(InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line = reader.readLine()) != null) {
			OriginalFeedbackRequest request = OriginalFeedbackRequest.valueOf(line);
			pool.execute(() -> postData(request));
		}

	}

	@SneakyThrows
	void postData(OriginalFeedbackRequest request) {
		log.info("发送到老系统");
		scheduler.feedbackWithOriginalQCSystem(originalQCSystemFeedbackURL, request);
	}

	/**
	 * @param args
	 */
	@SneakyThrows
	public static void main(String[] args) {
		new DataSyncHelper().process(Thread.currentThread().getContextClassLoader().getResourceAsStream("static/data.csv"));
	}

}
