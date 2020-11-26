/**
 * @(#) AsrResultErrorMessage.java ASR引擎
 */
package com.origins.asr.engine;

import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
@Getter
public class AsrResultErrorMessage extends AsrResultMessage {
	private final String errorMessage;

	/**
	 * @param requestId
	 * @param result
	 */
	public AsrResultErrorMessage(String requestId, String errorMessage) {
		super(requestId);
		this.errorMessage = errorMessage;
	}

}
