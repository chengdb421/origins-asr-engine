/**
 * @(#) AsrEngine.java ASR引擎
 */
package com.origins.asr.engine;

import java.util.List;

/**
 * ASR引擎
 * 
 * @author 智慧工厂@M
 *
 */
public interface AsrEngine {
	/**
	 * 获取引擎的供应商信息
	 * 
	 * @return
	 */
	AsrEngineVendorInfo getAsrEngineVendorInfo();

	/**
	 * 受理一个新的ASR任务
	 * 
	 * @param incoming
	 * @param handler
	 */
	void acceptNewAsrTask(AsrTask incoming, AsrTaskCreationHandler handler);

	/**
	 * 接收ASR结果
	 * 
	 * @param requestIds
	 * @param handler
	 */
	void retriveAsrResult(List<String> requestIds, AsrResultHandler handler);
}
