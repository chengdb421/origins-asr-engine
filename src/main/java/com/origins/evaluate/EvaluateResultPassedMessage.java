/**
 * @(#) EvaluateResultPassedMessage.java ASR引擎
 */
package com.origins.evaluate;

import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
public class EvaluateResultPassedMessage extends EvaluateResult {
	@Getter
	private final String state = "pass";

	/**
	 * @param modelnfo
	 * @param uuid
	 * @param text
	 * @param timestamp
	 */
	public EvaluateResultPassedMessage(EvaluateModelMetaData modelnfo, String uuid, String text, long timestamp) {
		super(modelnfo, uuid, text, timestamp);
		}

}
