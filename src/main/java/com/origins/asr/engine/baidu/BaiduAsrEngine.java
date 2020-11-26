/**
 * @(#) BaiduAsrEngine.java ASR引擎
 */
package com.origins.asr.engine.baidu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.origins.asr.engine.AsrEngine;
import com.origins.asr.engine.AsrEngineVendorInfo;
import com.origins.asr.engine.AsrResultErrorMessage;
import com.origins.asr.engine.AsrResultHandler;
import com.origins.asr.engine.AsrResultOkMessage;
import com.origins.asr.engine.AsrResultRunningMessage;
import com.origins.asr.engine.AsrTask;
import com.origins.asr.engine.AsrTaskCreationErrorMessage;
import com.origins.asr.engine.AsrTaskCreationHandler;
import com.origins.asr.engine.AsrTaskCreationOkMessage;

/**
 * @author 智慧工厂@M
 *
 */
@Service("baidu")
public class BaiduAsrEngine implements AsrEngine {
	/**
	 * 百度智能呼叫中心API
	 */
	@Autowired
	private BaiduAsrAPI api;
	/**
	 * 供应商信息
	 */
	private final AsrEngineVendorInfo vendorInfo = AsrEngineVendorInfo.builder().vendorName("baidu")
			.vendorVersion("1.0").metaInfo("百度呼叫中心语音转写").build();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.engine.AsrEngine#getAsrEngineVendorInfo()
	 */
	@Override
	public AsrEngineVendorInfo getAsrEngineVendorInfo() {
		return vendorInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cnwansun.asr.engine.AsrEngine#acceptNewAsrTask(com.cnwansun.asr.engine.
	 * AsrTask, com.cnwansun.asr.engine.AsrTaskHandler)
	 */
	@Override
	public void acceptNewAsrTask(AsrTask incoming, AsrTaskCreationHandler handler) {
		BaiduAsrCreateTaskRequest request = new BaiduAsrCreateTaskRequest();
		request.setFormat(BaiduAsrCreateTaskRequest.PCM);
		request.setPid(BaiduAsrCreateTaskRequest.MANDARIN_CHINSES);
		request.setRate(BaiduAsrCreateTaskRequest._8K);
		request.setSpeechUrl(incoming.getTemporaryMediaUrl());
		try {
			String requestId = api.createAsrTask(request);
			handler.callback(new AsrTaskCreationOkMessage(incoming, requestId));
		} catch (Exception e) {
			handler.callback(new AsrTaskCreationErrorMessage(incoming, e.getMessage()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.engine.AsrEngine#retriveAsrResult(java.util.List,
	 * com.cnwansun.asr.engine.AsrResultHandler)
	 */
	@Override
	public void retriveAsrResult(List<String> requestIds, AsrResultHandler handler) {
		BaiduAsrGetTaskResultRequest request = new BaiduAsrGetTaskResultRequest();
		request.setTaskIds(requestIds);
		api.retrieveAsrResult(request, e -> {
			if (e instanceof BaiduAsrGetTaskResultErrorMessage) {
				BaiduAsrGetTaskResultErrorMessage err = (BaiduAsrGetTaskResultErrorMessage) e;
				handler.handleMessage(new AsrResultErrorMessage(err.getRequestId(), err.getErrorMessage()));
			} else if (e instanceof BaiduAsrGetTaskResultRunningMessage) {
				BaiduAsrGetTaskResultRunningMessage running = (BaiduAsrGetTaskResultRunningMessage) e;
				handler.handleMessage(new AsrResultRunningMessage(running.getRequestId()));
			} else if (e instanceof BaiduAsrGetTaskResultOkMessage) {
				BaiduAsrGetTaskResultOkMessage ok = (BaiduAsrGetTaskResultOkMessage) e;
				handler.handleMessage(new AsrResultOkMessage(ok.getRequestId(), ok.getResultText()));
			}
		});
	}
}
