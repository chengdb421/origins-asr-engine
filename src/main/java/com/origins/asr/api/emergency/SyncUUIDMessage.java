/**
 * @(#) SyncUUIDMessage.java ASR引擎
 */
package com.origins.asr.api.emergency;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.ToString;

/**
 * @author 智慧工厂@M
 *
 */
@Data
@ToString
public class SyncUUIDMessage {
	@JSONField(name = "project_uuid")
	private String projectUUID;

	@JSONField(name = "task_id")
	private String taskId;
}
