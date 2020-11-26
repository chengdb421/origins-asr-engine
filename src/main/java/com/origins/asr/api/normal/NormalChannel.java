/**
 * @(#) NormalChannel.java ASR引擎
 */
package com.origins.asr.api.normal;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.origins.asr.api.AsrTaskCreationRequestMessage;
import com.origins.asr.api.GenericChannel;
import com.origins.asr.api.LocalRepository;
import com.origins.asr.api.MediaStreamBuilder;
import com.origins.asr.api.OldSystem;
import com.origins.asr.engine.AsrEngineFactory;

/**
 * 常规通道
 * 
 * @author 智慧工厂@M
 *
 */
@Service("normal")
@RabbitListener(queues = "new_asr_task_queue")
public class NormalChannel extends GenericChannel {
	@Autowired
	private AsrEngineFactory asrEngineFactory;

	@Autowired
	private LocalRepository repository;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private MediaStreamBuilder mediaStreamBuilder;

	@Autowired
	private OldSystem oldSystem;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.api.Channel#createAsrTask(com.cnwansun.asr.api.
	 * AsrTaskCreationRequestMessage)
	 */
	@Override
	public void createAsrTask(AsrTaskCreationRequestMessage message) {
		if (message.getEngineName() == null) {
			message.setEngineName("wansun");
		}
		createAsrTask(mediaStreamBuilder, asrEngineFactory, repository, amqpTemplate, message);
	}

	@RabbitHandler
	public void handleMessage(byte[] message) {
		createAsrTask(JSON.parseObject(new String(message), AsrTaskCreationRequestMessage.class));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.api.GenericChannel#getOldSystem()
	 */
	@Override
	public OldSystem getOldSystem() {
		return oldSystem;
	}
}
