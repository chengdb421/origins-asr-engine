/**
 * @(#) AsrResultOkMessage.java ASR引擎
 */
package com.origins.asr.engine;

import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
@Getter
public class AsrResultOkMessage extends AsrResultMessage {
	private final String resultText;

	/**
	 * @param requestId
	 * @param result
	 */
	public AsrResultOkMessage(String requestId, String resultText) {
		super(requestId);
		this.resultText = resultText;
	}

}
