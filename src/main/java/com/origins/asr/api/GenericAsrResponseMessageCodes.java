/**
 * @(#) GenericAsrResponseMessageCodes.java ASR引擎
 */
package com.origins.asr.api;

/**
 * @author 智慧工厂@M
 *
 */
public interface GenericAsrResponseMessageCodes {
	/**
	 * 转译就绪
	 */
	static final int OK = 200;
	
	/**
	 * 内部错误（含ASR引擎接口的错误）
	 */
	static final int INTERNAL_ERROR = 500;
	
	/**
	 * 任务已经存在
	 */
	static final int TASK_ALREADY_EXIST = 501;
	
	/**
	 * 任务正在运行中
	 */
	static final int TASK_HAS_BEEN_RUNNING = 601;
}
