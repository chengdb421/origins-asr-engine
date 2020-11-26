/**
 * @(#) AsrTaskCreationRequestMessage.java ASR引擎
 */
package com.origins.asr.api;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 创建ASR任务请求实体
 * 
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Data
public class AsrTaskCreationRequestMessage {
	@JSONField(name = "project_uuid")
	@JsonProperty("project_uuid")
	private String projectUUID;
	
	@JSONField(name = "uuid")
	@JsonProperty("uuid")
	private String uuid;
	@JSONField(name = "media_url")
	@JsonProperty("media_url")
	private String mediaUrl;
	@JSONField(name = "engine_name")
	@JsonProperty("engine_name")
	private String engineName;
}
