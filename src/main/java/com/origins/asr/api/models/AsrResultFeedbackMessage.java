/**
 * @(#) AsrResultFeedbackMessage.java ASR引擎
 */
package com.origins.asr.api.models;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Builder;
import lombok.Data;

/**
 * @author 智慧工厂@M
 *
 */
@Data
@Builder
public class AsrResultFeedbackMessage {
	@JSONField(name = "project_uuid")
	private String projectUUID;
	@JSONField(name = "audio_url")
	private String audioUrl;
	@JSONField(name = "message")
	private String message;
	@JSONField(name = "status")
	private String status;
}
