/**
 * @(#) BaiduAsrGetTaskResultErrorMessage.java ASR引擎
 */
package com.origins.asr.engine.baidu;

import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
@Getter
public class BaiduAsrGetTaskResultErrorMessage extends BaiduAsrGetTaskResultMessage {
	private final String errorMessage;

	/**
	 * @param requestId
	 */
	public BaiduAsrGetTaskResultErrorMessage(String requestId, String errorMessage) {
		super(requestId);
		this.errorMessage = errorMessage;
	}
}
