/**
 * @(#) AsrEngineVendorInfo.java ASR引擎
 */
package com.origins.asr.engine;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * ASR引擎供应商信息
 * 
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Data
@ToString
@Builder
public class AsrEngineVendorInfo {
	@JsonProperty("vendor_name")
	private final String vendorName;

	@JsonProperty("vendor_version")
	private final String vendorVersion;

	@JsonProperty("meta_info")
	private final String metaInfo;
}
