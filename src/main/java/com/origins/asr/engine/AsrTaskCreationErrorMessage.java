/**
 * @(#) AsrTaskCreationErrorMessage.java ASR引擎
 */
package com.origins.asr.engine;

import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
@Getter
public class AsrTaskCreationErrorMessage extends AsrTaskCreationMessage {
	private String errorMessage;

	/**
	 * @param task
	 * @param message
	 */
	public AsrTaskCreationErrorMessage(AsrTask task, String errorMessage) {
		super(task);
		this.errorMessage = errorMessage;
	}

}
