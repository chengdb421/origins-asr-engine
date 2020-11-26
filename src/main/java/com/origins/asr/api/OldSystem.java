/**
 * @(#) OldSystem.java ASR引擎
 */
package com.origins.asr.api;

import java.util.UUID;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.origins.asr.api.models.AsrResultFeedbackMessage;
import com.origins.asr.api.models.AsrResultSyncMessage;
import com.origins.asr.api.models.AsrTaskStates;
import com.origins.asr.engine.AsrTaskCreationAndGetResultMessage;
import com.origins.asr.engine.AsrTaskCreationErrorMessage;
import com.origins.asr.engine.AsrTaskCreationMessage;
import com.origins.asr.engine.utils.TimeUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 老系统
 * 
 * @author 智慧工厂@M
 *
 */
@Component
@Slf4j
public class OldSystem {
	@Autowired
	private AmqpTemplate amqpTemplate;

	@Value("${original.sync.enabled}")
	private boolean originalSyncEnabled;

	AsrResultFeedbackMessage feedbackErrorMessage(AsrTaskCreationRequestMessage error, String errorMessage) {
		return AsrResultFeedbackMessage.builder().audioUrl(error.getMediaUrl()).projectUUID(error.getProjectUUID())
				.message(errorMessage).status(AsrTaskStates.ERROR.toString()).build();
	}

	AsrResultFeedbackMessage feedbackMessage(AsrTaskCreationMessage message) {
		AsrResultFeedbackMessage feedback = null;
		if (message instanceof AsrTaskCreationAndGetResultMessage) {
			AsrTaskCreationAndGetResultMessage ok = (AsrTaskCreationAndGetResultMessage) message;
			feedback = AsrResultFeedbackMessage.builder().audioUrl(ok.getTask().getRawMediaUrl())
					.projectUUID(ok.getTask().getProjectUUID()).message("ok").status("COMPLETION").build();
		} else if (message instanceof AsrTaskCreationErrorMessage) {
			AsrTaskCreationErrorMessage error = (AsrTaskCreationErrorMessage) message;
			feedback = AsrResultFeedbackMessage.builder().audioUrl(error.getTask().getRawMediaUrl())
					.projectUUID(error.getTask().getProjectUUID()).message(error.getErrorMessage())
					.status(AsrTaskStates.ERROR.toString()).build();
		} else {
			throw new RuntimeException("Unknown message type:" + message.getClass());
		}
		return feedback;
	}

	AsrResultSyncMessage syncErrorMessage(AsrTaskCreationRequestMessage error, String errorMessage) {
		AsrResultSyncMessage sync = new AsrResultSyncMessage();
		sync.setErrorMessage(errorMessage);
		sync.setResultText(null);
		sync.setState(AsrTaskStates.ERROR.toString());
		sync.setUuid(error.getUuid());
		return sync;
	}

	AsrResultSyncMessage syncMessage(AsrTaskCreationMessage message) {
		AsrResultSyncMessage sync = new AsrResultSyncMessage();
		if (message instanceof AsrTaskCreationAndGetResultMessage) {
			AsrTaskCreationAndGetResultMessage ok = (AsrTaskCreationAndGetResultMessage) message;
			sync.setErrorMessage(null);
			sync.setResultText(ok.getResultText());
			sync.setState(AsrTaskStates.COMPLETED.toString());
			sync.setUuid(message.getTask().getUuid());
			sync.setRequestId(ok.getRequestId());
			sync.setStartAsrAt(TimeUtils.localTimeToDate(ok.getStartAt()));
			sync.setEndAsrAt(TimeUtils.localTimeToDate(ok.getEndAt()));
		} else if (message instanceof AsrTaskCreationErrorMessage) {
			AsrTaskCreationErrorMessage error = (AsrTaskCreationErrorMessage) message;
			sync.setErrorMessage(error.getErrorMessage());
			sync.setResultText(null);
			sync.setState(AsrTaskStates.ERROR.toString());
			sync.setUuid(message.getTask().getUuid());
		} else {
			throw new RuntimeException("Unknown message type:" + message.getClass());
		}
		return sync;
	}

	/**
	 * 向老系统通知消息
	 * 
	 * @param message
	 */
	public void notify(AsrTaskCreationMessage message) {
		if (!originalSyncEnabled) {
			return;
		}

		log.info("向老系统同步数据");

		AsrResultFeedbackMessage feedback = feedbackMessage(message);
		fireRabbitMessage("asr_callback_queue", feedback);
		fireRabbitMessage("asr_callback_queue_test", feedback);

		AsrResultSyncMessage payload = syncMessage(message);
		fireRabbitMessage("asr_task_result_sync_queue", payload);
	}

	public void notifyError(AsrTaskCreationRequestMessage message, String errorMessage) {
		if (!originalSyncEnabled) {
			return;
		}

		log.debug("向老系统同步数据");
		AsrResultFeedbackMessage feedback = feedbackErrorMessage(message, errorMessage);
		fireRabbitMessage("asr_callback_queue", feedback);
		fireRabbitMessage("asr_callback_queue_test", feedback);

		AsrResultSyncMessage sync = syncErrorMessage(message, errorMessage);
		fireRabbitMessage("asr_task_result_sync_queue", sync);
	}

	public void notifySyncError(AsrTaskCreationRequestMessage message, String errorMessage) {
		if (!originalSyncEnabled) {
			return;
		}
		log.info("向老系统同步错误数据");
		AsrResultSyncMessage sync = syncErrorMessage(message, errorMessage);
		fireRabbitMessage("asr_task_result_sync_queue", sync);
	}

	private void fireRabbitMessage(String queueName, Object payload) {
		Message rabbitMessage = MessageBuilder.withBody(JSON.toJSONString(payload).getBytes())
				.setContentType(org.springframework.amqp.core.MessageProperties.CONTENT_TYPE_BYTES)
				.setContentEncoding("utf-8").setMessageId(UUID.randomUUID() + "").build();
		amqpTemplate.convertAndSend(queueName, rabbitMessage);
	}
}
