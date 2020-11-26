/**
 * @(#) BaiduAsrGetTaskResultOkMessage.java ASR引擎
 */
package com.origins.asr.engine.baidu;

import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
@Getter
public class BaiduAsrGetTaskResultOkMessage extends BaiduAsrGetTaskResultMessage {
	private final String resultText;

	/**
	 * @param requestId
	 */
	public BaiduAsrGetTaskResultOkMessage(String requestId, String resultText) {
		super(requestId);
		this.resultText = resultText;
	}

}
