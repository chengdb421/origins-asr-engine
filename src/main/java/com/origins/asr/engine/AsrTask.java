/**
 * @(#) AsrTask.java ASR引擎
 */
package com.origins.asr.engine;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * ASR任务
 * 
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Data
public class AsrTask {
	private final String projectUUID;
	private final String uuid;
	private final String rawMediaUrl;
	private final String modelName;
	private String temporaryMediaUrl;
	private byte[] mediaStream;
}
