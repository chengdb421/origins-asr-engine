/**
 * @(#) EvaluateWarningEvidenceChain.java ASR引擎
 */
package com.origins.evaluate;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Getter
public class EvaluateWarningEvidenceChain {
	@JsonProperty("issue_type")
	@JSONField(name = "issue_type")
	private final EvaluateIssueTypes issueType;

	@JSONField(name = "evidences")
	@JsonProperty("evidences")
	private List<EvaluateWarningEvidence> evidenceChain = new ArrayList<>();
}
