/**
 * @(#) GenericAsrResponseMessage.java ASR引擎
 */
package com.origins.asr.api;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.origins.asr.engine.AsrEngineVendorInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Getter
@Builder
public class GenericAsrResponseMessage implements GenericAsrResponseMessageCodes {
	private final int code;
	@JSONField(name = "project_uuid")
	@JsonProperty("project_uuid")
	private final String projectUUID;
	private final String message;
	private final String uuid;
	@JSONField(name = "start_asr")
	@JsonProperty("start_asr")
	private final Date startAsr;
	@JSONField(name = "end_asr")
	@JsonProperty("end_asr")
	private final Date endAsr;
	private final long timestamp = System.currentTimeMillis();
	private final String resultText;

	@JSONField(name = "asr_engine")
	@JsonProperty("asr_engine")
	private final AsrEngineVendorInfo engineInfo;
}
