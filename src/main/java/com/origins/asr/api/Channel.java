/**
 * @(#) Channel.java ASR引擎
 */
package com.origins.asr.api;

import java.util.List;

import com.origins.asr.engine.AsrEngineVendorInfo;

/**
 * @author 智慧工厂@M
 *
 */
public interface Channel {
	public void createAsrTask(AsrTaskCreationRequestMessage message);

	/**
	 * 获取系统内有效的ASR引擎名称
	 * 
	 * @return
	 */
	List<AsrEngineVendorInfo> getAvailableAsrEngineNames();
}
