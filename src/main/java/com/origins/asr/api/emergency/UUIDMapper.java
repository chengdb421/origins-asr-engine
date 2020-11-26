package com.origins.asr.api.emergency;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UUIDMapper {
	@JSONField(name = "project_uuid")
	private final String projectUUID;

	@JSONField(name = "task_id")
	private final String taskId;
}
