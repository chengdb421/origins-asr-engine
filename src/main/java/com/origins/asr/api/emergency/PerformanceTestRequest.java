/**
 * @(#) PerformanceTestRequest.java ASR引擎
 */
package com.origins.asr.api.emergency;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

/**
 * 压力测试请求实体
 * 
 * @author 智慧工厂@M
 *
 */
@Data
@ToString
public class PerformanceTestRequest {
	@JsonProperty("media_url")
	private String mediaURL;

	@JsonProperty("count")
	private int count;

	@JsonProperty("engine_name")
	private String engineName;

	@JsonProperty("project_uuid")
	private String projectUUID;
}
