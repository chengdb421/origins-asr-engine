/**
 * @(#) OriginalFeedbackRequest.java ASR引擎
 */
package com.origins.asr.api.original;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * 向老版的语音质检内核进行转译反馈
 * 
 * @author 智慧工厂@M
 *
 */
@Data
@Builder
@ToString
public class OriginalFeedbackRequest {
	@JSONField(name = "uuid")
	private String uuid;

	@JSONField(name = "code")
	private int code;

	@JSONField(name = "message")
	private String message;

	@JSONField(name = "request_id")
	private String requestId;

	@JSONField(name = "result_text")
	private String resultText;

	public static OriginalFeedbackRequest valueOf(String line) {
		String[] buffer = line.split("\t");
		OriginalFeedbackRequest request = new OriginalFeedbackRequest(buffer[1], 200, "ok", buffer[6], buffer[4]);
		return request;
	}
}
