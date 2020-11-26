/**
 * @(#) AsrTaskHandler.java ASR引擎
 */
package com.origins.asr.engine;

/**
 * @author 智慧工厂@M
 *
 */
@FunctionalInterface
public interface AsrTaskCreationHandler {
	void callback(AsrTaskCreationMessage message);
}
