package com.origins.asr.engine.baidu;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class BaiduAsrCreateTaskResponse {
	@JSONField(name = "log_id")
	private String logId;

	@JSONField(name = "task_id")
	private String taskId;

	@JSONField(name = "task_status")
	private String taskStatus;
}
