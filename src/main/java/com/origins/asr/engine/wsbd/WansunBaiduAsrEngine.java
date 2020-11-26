/**
 * @(#) WansunBaiduAsrEngine.java ASR引擎
 */
package com.origins.asr.engine.wsbd;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.baidu.acu.pie.model.RecognitionResult;
import com.origins.asr.engine.AsrEngine;
import com.origins.asr.engine.AsrEngineVendorInfo;
import com.origins.asr.engine.AsrResultHandler;
import com.origins.asr.engine.AsrTask;
import com.origins.asr.engine.AsrTaskCreationAndGetResultMessage;
import com.origins.asr.engine.AsrTaskCreationErrorMessage;
import com.origins.asr.engine.AsrTaskCreationHandler;
import com.origins.asr.engine.AsrTaskCreationOkMessage;

/**
 * @author 智慧工厂@M
 *
 */
@Service("wansun")
public class WansunBaiduAsrEngine implements AsrEngine {
	@Autowired
	@Qualifier("wansun-asr-disptcher-threadpool")
	private ExecutorService executorService;

	@Autowired
	private LocalAsrProc localAsrProc;

	private AsrEngineVendorInfo gendorInfo = AsrEngineVendorInfo.builder().vendorName("wansun").vendorVersion("1.0.0")
			.metaInfo("万乘-百度私有化ASR引擎").build();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.engine.AsrEngine#getAsrEngineVendorInfo()
	 */
	@Override
	public AsrEngineVendorInfo getAsrEngineVendorInfo() {
		return gendorInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cnwansun.asr.engine.AsrEngine#acceptNewAsrTask(com.cnwansun.asr.engine.
	 * AsrTask, com.cnwansun.asr.engine.AsrTaskCreationHandler)
	 */
	@Override
	public void acceptNewAsrTask(AsrTask incoming, AsrTaskCreationHandler handler) {
		String uuid = UUID.randomUUID().toString();
		handler.callback(new AsrTaskCreationOkMessage(incoming, uuid));
//		executorService.execute(() -> {
		try {
			List<RecognitionResult> results = localAsrProc
					.recognize(new ByteArrayInputStream(incoming.getMediaStream()));
			if (results.isEmpty()) {
				handler.callback(new AsrTaskCreationErrorMessage(incoming, "识别的内容为空"));
				return;
			}
			StringBuffer sb = new StringBuffer();
			results.forEach(x -> sb.append(x.getResult()));
			LocalTime start = results.get(0).getStartTime();
			LocalTime end = results.get(results.size() - 1).getEndTime();
			handler.callback(new AsrTaskCreationAndGetResultMessage(uuid, incoming, sb.toString(), start, end));
		} catch (Exception e) {
			e.printStackTrace();
			handler.callback(new AsrTaskCreationErrorMessage(incoming, e + "" + e.getMessage()));
		}
//		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.engine.AsrEngine#retriveAsrResult(java.util.List,
	 * com.cnwansun.asr.engine.AsrResultHandler)
	 */
	@Override
	public void retriveAsrResult(List<String> requestIds, AsrResultHandler handler) {
		throw new RuntimeException("服务不支持");
	}

}
