/**
 * @(#) BaiduAsrCreateTaskRequest.java ASR引擎
 */
package com.origins.asr.engine.baidu;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * 百度创建ASR转写任务请求实体
 * 
 * @author 智慧工厂@M
 *
 */
@Data
public class BaiduAsrCreateTaskRequest implements BaiduAsrPids, BaiduMediaFormat, BaiduMediaSampleRates {
	@JSONField(name = "speech_url")
	private String speechUrl;
	private String format = PCM;
	private long pid = MANDARIN_CHINSES;
	private long rate = _8K;
}
