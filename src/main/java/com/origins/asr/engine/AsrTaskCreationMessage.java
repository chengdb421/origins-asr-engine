/**
 * @(#) AsrTaskCreationMessage.java ASR引擎
 */
package com.origins.asr.engine;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Data
public class AsrTaskCreationMessage {
	private final AsrTask task;
}
