/**
 * @(#) EvaluateResultWarningMessage.java ASR引擎
 */
package com.origins.evaluate;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * 报警信息
 * 
 * @author 智慧工厂@M
 *
 */
@Getter
public class EvaluateResultWarningMessage extends EvaluateResult {
	private final String state = "warning";
	@JSONField(name = "model_info")
	@JsonProperty("model_info")
	private final EvaluateModelMetaData modelInfo;
	/**
	 * 测评结果的证据链
	 */
	@JSONField(name = "evidence_chain")
	@JsonProperty("evidence_chain")
	private final List<EvaluateWarningEvidenceChain> evidenceChain = new ArrayList<>();

	/**
	 * @param modelnfo
	 * @param uuid
	 * @param text
	 * @param timestamp
	 */
	public EvaluateResultWarningMessage(EvaluateModelMetaData modellnfo, String uuid, String text, long timestamp) {
		super(modellnfo, uuid, text, timestamp);
		this.modelInfo = modellnfo;
	}

	public void addEvaluateWarningEvidence(EvaluateWarningEvidenceChain evidence) {
		this.evidenceChain.add(evidence);
	}

}
