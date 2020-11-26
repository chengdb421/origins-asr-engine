/**
 * @(#) EvaluateWarningEvidence.java ASR引擎
 */
package com.origins.evaluate;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

/**
 * 评估证据
 * 
 * @author 智慧工厂@M
 *
 */
@Data
@Builder
public class EvaluateWarningEvidence {
	private String keyword;
	@JSONField(name = "word_start_at")
	@JsonProperty("word_start_at")
	private int startAt;

	@JSONField(name = "word_end_at")
	@JsonProperty("word_end_at")
	private int endAt;
	private long timestamp;

	@JSONField(name = "issue_type")
	@JsonProperty("issue_type")
	private EvaluateIssueTypes issueType;
}
