/**
 * @(#) AsrTaskModel.java ASR引擎
 */
package com.origins.asr.api.models;

import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * 任务模型
 * 
 * @author 智慧工厂@M
 */
@Data
@Builder
@ToString
public class AsrTaskModel {
	private int id;
	private String projectUUID;
	private String uuid;
	private String rawMediaUrl;
	private String temporaryMediaUrl;
	private AsrTaskStates state;
	private String requestId;
	private Date createdAt;
	private Date runningAt;
	private Date completedAt;
	private String resultText;
	private String modelName;
	private String errorMessage;
	private int sequence;

	public AsrTaskModel(Integer id, String projectUUID, String uuid, String rawMediaUrl, String temporaryMediaUrl,
			String state, String requestId, Date createdAt, Date runningAt, Date completedAt, String resultText,
			String modelName, String errorMessage, Integer sequence) {
		this(id, projectUUID, uuid, rawMediaUrl, temporaryMediaUrl,
				state == null ? AsrTaskStates.UKNOWN : AsrTaskStates.valueOf(state), requestId, createdAt, runningAt,
				completedAt, resultText, modelName, errorMessage, sequence);
	}

	public AsrTaskModel(Integer id, String projectUUID, String uuid, String rawMediaUrl, String temporaryMediaUrl,
			AsrTaskStates state, String requestId, Date createdAt, Date runningAt, Date completedAt, String resultText,
			String modelName, String errorMessage, Integer sequence) {
		this.id = id;
		this.uuid = uuid;
		this.projectUUID = projectUUID;
		this.rawMediaUrl = rawMediaUrl;
		this.temporaryMediaUrl = temporaryMediaUrl;
		this.state = state;
		this.requestId = requestId;
		this.createdAt = createdAt;
		this.runningAt = runningAt;
		this.completedAt = completedAt;
		this.resultText = resultText;
		this.modelName = modelName;
		this.errorMessage = errorMessage;
		this.sequence = sequence;
	}

}
