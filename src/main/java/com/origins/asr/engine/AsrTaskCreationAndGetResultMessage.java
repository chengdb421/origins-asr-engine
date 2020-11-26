/**
 * @(#) AsrTaskCreationAndGetResultMessage.java ASR引擎
 */
package com.origins.asr.engine;

import org.joda.time.LocalTime;

import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
@Getter
public class AsrTaskCreationAndGetResultMessage extends AsrTaskCreationMessage {
	private final String resultText;
	private final LocalTime startAt;
	private final LocalTime endAt;
	private final String requestId;

	/**
	 * @param task
	 */
	public AsrTaskCreationAndGetResultMessage(String requestId, AsrTask task, String resultText, LocalTime startAt,
			LocalTime endAt) {
		super(task);
		this.resultText = resultText;
		this.startAt = startAt;
		this.endAt = endAt;
		this.requestId = requestId;
	}

}
