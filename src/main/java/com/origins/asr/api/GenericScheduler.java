/**
 * @(#) GenericScheduler.java ASR引擎
 */
package com.origins.asr.api;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.origins.asr.api.models.AsrResultFeedbackMessage;
import com.origins.asr.api.models.AsrResultSyncMessage;
import com.origins.asr.api.models.AsrTaskModel;
import com.origins.asr.api.models.AsrTaskStates;
import com.origins.asr.api.original.OriginalFeedbackRequest;
import com.origins.asr.engine.AsrEngine;
import com.origins.asr.engine.AsrEngineFactory;
import com.origins.asr.engine.AsrResultErrorMessage;
import com.origins.asr.engine.AsrResultOkMessage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@Component
@Slf4j
public class GenericScheduler {
	@Autowired
	private LocalRepository repository;

	@Autowired
	private AsrEngineFactory asrEngineFactory;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private File temporaryMediaFolder;

	@Value("${original.qc.feedback.url}")
	private String originalQCSystemFeedbackURL;

	@Value("${original.sync.enabled}")
	private boolean originalSyncEnabled;

//	private Executor executors = Executors.newFixedThreadPool(100);

	/**
	 * 每5秒发送100个请求,每个请求50个任务 合计为每秒： 5000/5 = 1000个任务
	 */
	@Scheduled(fixedRate = 5000)
	public void performRetrieveAsrResult() {
//		for (int i = 0; i < 100; i++) {
//			List<AsrTaskModel> matches = repository.getAsrModelsMapper().findUnCompletedTasks().stream()
//					.filter(x -> x.getRequestId() != null).collect(Collectors.toList());
//			if (matches.isEmpty()) {
//				continue;
//			}
//			repository.getAsrModelsMapper().incrementSequence(matches);
//			executors.execute(() -> performAction(matches));
//		}
	}

	public void performAction(List<AsrTaskModel> matches) {
		if (matches.isEmpty()) {
			log.warn("No enough uncompletable asr tasks");
			return;
		}

		log.info("#抓取任务长度为:" + matches.size());

		/**
		 * 按照模型分组
		 */
		Map<String, List<AsrTaskModel>> groups = matches.stream()
				.collect(Collectors.groupingBy(AsrTaskModel::getModelName));
		Iterator<String> modelNames = groups.keySet().iterator();
		while (modelNames.hasNext()) {
			String modelName = modelNames.next();
			try {
				AsrEngine engine = asrEngineFactory.getAsrEngineByName(modelName);
				log.info("当前选中的引擎为:" + engine.getAsrEngineVendorInfo());

				engine.retriveAsrResult(groups.get(modelName).stream().map(m -> m.getRequestId()).filter(x -> x != null)
						.collect(Collectors.toList()), message -> {
							AsrTaskModel model = repository.getAsrModelsMapper()
									.findAsrTaskModelByRequestId(message.getRequestId());
							if (message instanceof AsrResultOkMessage) {
								AsrResultOkMessage ok = (AsrResultOkMessage) message;
								model.setState(AsrTaskStates.COMPLETED);
								model.setCompletedAt(new Date());
								model.setResultText(ok.getResultText());
								repository.getAsrModelsMapper().completeAsrTaskModel(model);
								cleanup(model);
								fireRabbitMessage(GenericAsrResponseMessage.builder().code(GenericAsrResponseMessage.OK)
										.endAsr(model.getCompletedAt()).startAsr(model.getRunningAt()).message("ok")
										.resultText(ok.getResultText()).uuid(model.getUuid()).build());
							} else if (message instanceof AsrResultErrorMessage) {
								AsrResultErrorMessage error = (AsrResultErrorMessage) message;
								model.setState(AsrTaskStates.ERROR);
								model.setCompletedAt(new Date());
								model.setErrorMessage(error.getErrorMessage());
								repository.getAsrModelsMapper().recordErrorWithAsrTaskModel(model);
								cleanup(model);
								fireRabbitMessage(GenericAsrResponseMessage.builder()
										.code(GenericAsrResponseMessage.INTERNAL_ERROR).message(error.getErrorMessage())
										.uuid(model.getUuid()).startAsr(new Date()).endAsr(new Date()).build());
							}
							if (originalSyncEnabled) {
								feedbackWithOriginalQCSystem(model);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Unknown model:" + modelName);
			}
		}
	}

	/**
	 * 向老版本的质检系统回馈转译结果
	 * 
	 * @param amqpTemplate
	 * @param model
	 */
	protected void feedbackWithOriginalQCSystem(AsrTaskModel model) {
		log.info("反馈+++++++++++++++++++++++");
		AsrResultSyncMessage payload = new AsrResultSyncMessage();
		payload.setErrorMessage(model.getErrorMessage());
		payload.setRequestId(model.getRequestId());
		payload.setResultText(model.getResultText());
		payload.setState(model.getState().toString());
		payload.setUuid(model.getUuid());
		fireRabbitMessage("asr_task_result_sync_queue", payload);

		AsrResultFeedbackMessage feedback = AsrResultFeedbackMessage.builder().audioUrl(model.getRawMediaUrl())
				.status(model.getState().toString()).projectUUID(model.getProjectUUID()).build();
		if (model.getState() == AsrTaskStates.ERROR) {
			feedback.setMessage(model.getErrorMessage());
		} else {
			feedback.setMessage("ok");
		}

		// asr_callback_queue
		fireRabbitMessage("asr_callback_queue", feedback);
		fireRabbitMessage("asr_callback_queue_test", feedback);
		// OriginalFeedbackRequest request = null;
		// if (model.getState() == AsrTaskStates.COMPLETED) {
		// request =
		// OriginalFeedbackRequest.builder().uuid(model.getUuid()).code(200).message("ok")
		// .requestId(model.getRequestId()).resultText(model.getResultText()).build();
		// } else if (model.getState() == AsrTaskStates.ERROR) {
		// request =
		// OriginalFeedbackRequest.builder().uuid(model.getUuid()).code(500).message(model.getErrorMessage())
		// .resultText("NULL").requestId(model.getRequestId()).build();
		// }
		//
		// if (request == null) {
		// log.warn("Task status illegalled:" + model.getState());
		// return;
		// }
		//
		// feedbackWithOriginalQCSystem(originalQCSystemFeedbackURL, request);
	}

	/**
	 * 向老系统发起反馈
	 * 
	 * @param prefix
	 * @param request
	 */
	public void feedbackWithOriginalQCSystem(String prefix, OriginalFeedbackRequest request) {
		log.info("向老系统反馈");
		Response response = null;
		try {
			response = Jsoup.connect(prefix).method(Method.POST).ignoreContentType(true)
					.header("Content-Type", "application/json;charset=UTF-8").requestBody(JSON.toJSONString(request))
					.execute();
		} catch (Exception e) {
			log.error("Feedback faild", e);
			return;
		}

		if (response.statusCode() != 200) {
			log.error("Feedback faild", response.statusMessage());
		}
	}

	/**
	 * 清理残余的文件
	 * 
	 * @param model
	 */
	@SneakyThrows
	void cleanup(AsrTaskModel model) {
		URL url = new URL(model.getTemporaryMediaUrl());
		File directedFile = new File(temporaryMediaFolder, url.getFile());
		if (directedFile.exists()) {
			directedFile.delete();
		}
	}

	void fireRabbitMessage(String queueName, Object payload) {
		Message rabbitMessage = MessageBuilder.withBody(JSON.toJSONString(payload).getBytes())
				.setContentType(org.springframework.amqp.core.MessageProperties.CONTENT_TYPE_BYTES)
				.setContentEncoding("utf-8").setMessageId(UUID.randomUUID() + "").build();
		amqpTemplate.convertAndSend(queueName, rabbitMessage);
	}

	/**
	 * 发送RabbitMQ消息
	 * 
	 * @param payload
	 */
	void fireRabbitMessage(GenericAsrResponseMessage payload) {
		Message rabbitMessage = MessageBuilder.withBody(JSON.toJSONString(payload).getBytes())
				.setContentType(org.springframework.amqp.core.MessageProperties.CONTENT_TYPE_BYTES)
				.setContentEncoding("utf-8").setMessageId(UUID.randomUUID() + "").build();
		amqpTemplate.convertAndSend("asr_task_result_notify_queue", rabbitMessage);
	}
}
