/**
 * @(#) EnmergencyChannel.java ASR引擎
 */
package com.origins.asr.api.emergency;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.origins.asr.api.AsrTaskCreationRequestMessage;
import com.origins.asr.api.GenericAsrResponseMessage;
import com.origins.asr.api.GenericChannel;
import com.origins.asr.api.LocalRepository;
import com.origins.asr.api.MediaStreamBuilder;
import com.origins.asr.api.OldSystem;
import com.origins.asr.api.original.DataSyncHelper;
import com.origins.asr.engine.AsrEngineFactory;
import com.origins.asr.engine.AsrEngineVendorInfo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;

/**
 * @author 智慧工厂@M
 *
 */
@Controller
@RequestMapping("/asr")
public class EnmergencyChannel extends GenericChannel {
	@Autowired
	private AsrEngineFactory asrEngineFactory;

	@Autowired
	private LocalRepository repository;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private MediaStreamBuilder mediaStreamBuilder;

	@Autowired
	private DataSyncHelper dataSyncHelper;

	@Autowired
	private OldSystem oldSystem;

	@Autowired
	@Qualifier("major-disptcher-threadpool")
	private ExecutorService exec;

	@PostMapping("/availableEngines")
	@ApiOperation(value = "获取系统内有效的ASR引擎信息", notes = "ASR")
	@ApiImplicitParams(@ApiImplicitParam(name = "获取系统内有效的ASR引擎信息", paramType = "retriveAvailableEngineProviders", dataTypeClass = List.class))
	@ResponseBody
	public List<AsrEngineVendorInfo> retriveAvailableEngineProviders() {
		return this.getAvailableAsrEngineNames();
	}

	@PostMapping("/createTask")
	@ApiOperation(value = "创建转译任务", notes = "ASR")
	@ApiImplicitParams(@ApiImplicitParam(name = "创建转译任务", paramType = "handleCreateAsrTask", dataTypeClass = GenericAsrResponseMessage.class))
	@ResponseBody
	public GenericAsrResponseMessage handleCreateAsrTask(@RequestBody AsrTaskCreationRequestMessage message) {
		try {
			exec.execute(() -> createAsrTask(message));
			return GenericAsrResponseMessage.builder().code(GenericAsrResponseMessage.OK).message("ok")
					.uuid(message.getUuid()).build();
		} catch (Exception e) {
			return GenericAsrResponseMessage.builder().code(GenericAsrResponseMessage.INTERNAL_ERROR)
					.message(e.getMessage() == null ? e.toString() : e.getMessage()).uuid(message.getUuid()).build();
		}
	}

	@PostMapping("/performance/test")
	@ResponseBody
	@ApiOperation(value = "压力测试", notes = "ASR")
	@ApiImplicitParams(@ApiImplicitParam(name = "压力测试", paramType = "performanceTest", dataTypeClass = GenericAsrResponseMessage.class))
	public GenericAsrResponseMessage performanceTest(@RequestBody PerformanceTestRequest request) {
		for (int i = 0; i < request.getCount(); i++) {
			AsrTaskCreationRequestMessage message = new AsrTaskCreationRequestMessage();
			message.setEngineName(request.getEngineName());
			message.setMediaUrl(request.getMediaURL());
			message.setUuid(UUID.randomUUID().toString());
			message.setProjectUUID(request.getProjectUUID());
			this.fireRabbitMessage(amqpTemplate, "new_asr_task_queue", message);
		}
		return GenericAsrResponseMessage.builder().code(200).message("ok").build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.api.Channel#createAsrTask(com.cnwansun.asr.api.
	 * AsrTaskCreationRequestMessage)
	 */
	@Override
	public void createAsrTask(AsrTaskCreationRequestMessage message) {
		if (message.getEngineName() == null) {
			/**
			 * 默认采用小黑熊本地化语音识别引擎
			 */
			message.setEngineName("wansun");
		}
		this.createAsrTask(mediaStreamBuilder, asrEngineFactory, repository, amqpTemplate, message);
	}

	@GetMapping("/sync/{beginAt}")
	@ResponseBody
	@SneakyThrows
	public GenericAsrResponseMessage sync(@PathVariable("beginAt") @DateTimeFormat Date beginAt) {
		try {
			dataSyncHelper.process(beginAt);
			return GenericAsrResponseMessage.builder().code(200).message("ok").build();
		} catch (Exception e) {
			e.printStackTrace();
			return GenericAsrResponseMessage.builder().code(500)
					.message(e.getMessage() == null ? e.toString() : e.getMessage()).build();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.api.GenericChannel#getOldSystem()
	 */
	@Override
	public OldSystem getOldSystem() {
		return this.oldSystem;
	}
}
