/**
 * @(#) GenericChannel.java ASR引擎
 */
package com.origins.asr.api;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;

import com.alibaba.fastjson.JSON;
import com.origins.asr.api.models.AsrTaskModel;
import com.origins.asr.api.models.AsrTaskStates;
import com.origins.asr.engine.AsrEngine;
import com.origins.asr.engine.AsrEngineFactory;
import com.origins.asr.engine.AsrEngineVendorInfo;
import com.origins.asr.engine.AsrTask;
import com.origins.asr.engine.AsrTaskCreationAndGetResultMessage;
import com.origins.asr.engine.AsrTaskCreationErrorMessage;
import com.origins.asr.engine.AsrTaskCreationOkMessage;
import com.origins.asr.engine.utils.TimeUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@Data
@Slf4j
public abstract class GenericChannel implements Channel {
	/**
	 * 获得老系统
	 * 
	 * @return
	 */
	public abstract OldSystem getOldSystem();

	private final List<AsrEngineVendorInfo> engineVendors = Stream.of(
			AsrEngineVendorInfo.builder().vendorName("baidu").vendorVersion("1.0").metaInfo("百度呼叫中心语音转写接口(云端)").build(),
			AsrEngineVendorInfo.builder().vendorName("wansun").vendorVersion("1.0").metaInfo("万乘语音识别引擎ASR识别接口(本地)")
					.build())
			.collect(Collectors.toList());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.api.Channel#createAsrTask(com.cnwansun.asr.api.
	 * AsrTaskCreationRequestMessage)
	 */
	protected void createAsrTask(MediaStreamBuilder mediaStreamBuilder, AsrEngineFactory asrEngineFactory,
			LocalRepository repository, AmqpTemplate amqpTemplate, AsrTaskCreationRequestMessage message) {
		AsrEngine engine = null;
		try {
			if (message.getUuid() == null) {
				throw new IllegalArgumentException("uuid不能为空,请检查协议");
			}

			/**
			 * 根据目前的测试场景：暂时停掉百度云端线上的呼叫中心语音转写接口，直接交由本地化引擎进行处理
			 */
			engine = asrEngineFactory.getAsrEngineByName("wansun");

			log.info("当前引擎为::" + engine);

			AsrTask asrTask = repository.findAsrTaskByUuid(message.getUuid(), model -> {
				if (model == null) {
					/**
					 * 不存在的直接创建新的任务
					 */
					AsrTask newTask = new AsrTask(message.getProjectUUID(), message.getUuid(), message.getMediaUrl(),
							message.getEngineName());
					/**
					 * 本地引擎暂时不需要做文件中转
					 */
					mediaStreamBuilder.rebuildAsrTaskExternalFileURL(newTask);
					repository.createAsrTask(newTask);
					return newTask;
				}
				/**
				 * 已经存在的进行更新
				 */
				model.setRawMediaUrl(message.getMediaUrl());
				model.setTemporaryMediaUrl(mediaStreamBuilder.rebuildAsrTaskExternalFileURL(message.getMediaUrl()));
				repository.getAsrModelsMapper().updateAsrTaskMediaUrl(model);
				log.info("更新：：" + model.getUuid());
				AsrTask exist = new AsrTask(model.getProjectUUID(), model.getUuid(), model.getRawMediaUrl(),
						model.getModelName());
				mediaStreamBuilder.rebuildAsrTaskExternalFileURL(exist);
				exist.setTemporaryMediaUrl(model.getTemporaryMediaUrl());
				return exist;
			});

			final AsrEngine asrEngine = engine;
			engine.acceptNewAsrTask(asrTask, e -> {
				AsrTaskModel model = repository.findAsrTaskByUuid(asrTask.getUuid(), x -> x);
				if (e instanceof AsrTaskCreationOkMessage) {
					AsrTaskCreationOkMessage ok = (AsrTaskCreationOkMessage) e;
					/**
					 * 更新任务状态
					 */
					model.setCompletedAt(new Date());
					model.setRequestId(ok.getRequestId());
					model.setState(AsrTaskStates.RUNNING);
					repository.getAsrModelsMapper().updateAsrTaskModelRequestId(model);
				} else if (e instanceof AsrTaskCreationErrorMessage) {
					AsrTaskCreationErrorMessage error = (AsrTaskCreationErrorMessage) e;
					model.setErrorMessage(error.getErrorMessage());
					model.setState(AsrTaskStates.ERROR);
					model.setCompletedAt(new Date());
					repository.getAsrModelsMapper().recordErrorWithAsrTaskModel(model);

					fireRabbitMessage(amqpTemplate,
							new GenericAsrResponseMessage(GenericAsrResponseMessage.INTERNAL_ERROR,
									asrTask.getProjectUUID(), error.getErrorMessage(), asrTask.getUuid(), new Date(),
									new Date(), null, asrEngine.getAsrEngineVendorInfo()));
					getOldSystem().notify(error);
				} else if (e instanceof AsrTaskCreationAndGetResultMessage) {
					AsrTaskCreationAndGetResultMessage ok = (AsrTaskCreationAndGetResultMessage) e;
					model.setResultText(ok.getResultText());
					log.info("转译结果为:" + ok.getResultText());
					model.setRunningAt(TimeUtils.localTimeToDate(ok.getStartAt()));
					model.setCompletedAt(TimeUtils.localTimeToDate(ok.getEndAt()));
					model.setState(AsrTaskStates.COMPLETED);
					repository.getAsrModelsMapper().completeAsrTaskModel(model);

					getOldSystem().notify(ok);
					/**
					 * 发送完成转译的MQ消息
					 */
					fireRabbitMessage(amqpTemplate,
							GenericAsrResponseMessage.builder().code(GenericAsrResponseMessage.OK).message("ok")
									.endAsr(model.getCompletedAt()).startAsr(model.getRunningAt())
									.projectUUID(model.getProjectUUID()).resultText(ok.getResultText())
									.engineInfo(asrEngine.getAsrEngineVendorInfo()).uuid(model.getUuid()).build());
				}
			});

		} catch (Exception e) {
			getOldSystem().feedbackErrorMessage(message, e.getMessage() == null ? e.toString() : e.getMessage());
			getOldSystem().syncErrorMessage(message, e.getMessage() == null ? e.toString() : e.getMessage());

			fireRabbitMessage(amqpTemplate,
					new GenericAsrResponseMessage(GenericAsrResponseMessage.INTERNAL_ERROR, message.getProjectUUID(),
							e.getMessage() == null ? e.toString() : e.getMessage(), message.getUuid(), new Date(),
							new Date(), null, engine == null ? null : engine.getAsrEngineVendorInfo()));
		}
	}

	protected void fireRabbitMessage(AmqpTemplate amqpTemplate, Object payload) {
		fireRabbitMessage(amqpTemplate, "asr_task_result_notify_queue", payload);
	}

	/**
	 * 向RabbitMQ发送
	 * 
	 * @param payload
	 */
	protected void fireRabbitMessage(AmqpTemplate amqpTemplate, String queueName, Object payload) {
		Message rabbitMessage = MessageBuilder.withBody(JSON.toJSONString(payload).getBytes())
				.setContentType(org.springframework.amqp.core.MessageProperties.CONTENT_TYPE_BYTES)
				.setContentEncoding("utf-8").setMessageId(UUID.randomUUID() + "").build();
		amqpTemplate.convertAndSend(queueName, rabbitMessage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.api.Channel#getAvailableAsrEngineNames()
	 */
	@Override
	public List<AsrEngineVendorInfo> getAvailableAsrEngineNames() {
		return this.engineVendors;
	}

}
