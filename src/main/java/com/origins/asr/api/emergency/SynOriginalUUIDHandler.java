/**
 * @(#) SynOriginalUUIDHandler.java ASR引擎
 */
package com.origins.asr.api.emergency;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.origins.asr.api.mapper.AsrModelsMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@RabbitListener(queues = "asr_task_uuid_sync_queue")
@Component
@Slf4j
public class SynOriginalUUIDHandler {
	@Autowired
	private AsrModelsMapper mapper;

	@RabbitHandler
	public void handleMessage(byte[] payload) {
		SyncUUIDMessage message = JSON.parseObject(new String(payload), SyncUUIDMessage.class);
		log.info("更新项目ID " + message);
		mapper.updateProjectUUID(message);
	}
}
