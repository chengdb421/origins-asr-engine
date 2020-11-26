/**
 * @(#) AsrTaskCreationOkMessage.java ASR引擎
 */
package com.origins.asr.engine;

import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
@Getter
public class AsrTaskCreationOkMessage extends AsrTaskCreationMessage {
	private final String requestId;

	/**
	 * @param task
	 * @param message
	 */
	public AsrTaskCreationOkMessage(AsrTask task, String requestId) {
		super(task);
		this.requestId = requestId;
	}

}
