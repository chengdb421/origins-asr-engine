/**
 * @(#) BaiduAsrGetTaskResultHandler.java ASR引擎
 */
package com.origins.asr.engine.baidu;

/**
 * @author 智慧工厂@M
 *
 */
@FunctionalInterface
public interface BaiduAsrGetTaskResultHandler {
	void handleMessage(BaiduAsrGetTaskResultMessage message);
}
