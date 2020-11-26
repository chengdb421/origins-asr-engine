/**
 * @(#) BaiduAsrGetTaskResultMessage.java ASR引擎
 */
package com.origins.asr.engine.baidu;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Data
public class BaiduAsrGetTaskResultMessage {
	private final String requestId;
}
