package com.origins.asr.engine.baidu;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class BaiduAsrGetTaskResultRequest {
	@JSONField(name = "task_ids")
	private List<String> taskIds;
}
