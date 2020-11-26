package com.origins.asr.api.models;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * ASR 结果同步消息实体
 * 
 * @author 智慧工厂@M
 *
 */
@Data
public class AsrResultSyncMessage {
	@JSONField(name = "uuid")
	private String uuid;

	@JSONField(name = "result_text")
	private String resultText;

	@JSONField(name = "request_id")
	private String requestId;

	@JSONField(name = "state")
	private String state;

	@JSONField(name = "error_message")
	private String errorMessage;

	@JSONField(name = "start_asr_at")
	private Date startAsrAt;

	@JSONField(name = "end_asr_at")
	private Date endAsrAt;
}
