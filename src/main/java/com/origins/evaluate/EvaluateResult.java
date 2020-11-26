/**
 * @(#) EvaluateResult.java ASR引擎
 */
package com.origins.evaluate;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 测评结果
 * 
 * @author 智慧工厂@M
 *
 */
@Data
public class EvaluateResult {
	@JSONField(name = "model_info")
	@JsonProperty("model_info")
	private final EvaluateModelMetaData modelnfo;
	private final String uuid;
	private final String text;
	private final long timestamp;
}
