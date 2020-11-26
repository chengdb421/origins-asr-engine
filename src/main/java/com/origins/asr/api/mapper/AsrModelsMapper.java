/**
 * @(#) AsrModelsMapper.java ASR引擎
 */
package com.origins.asr.api.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.origins.asr.api.emergency.SyncUUIDMessage;
import com.origins.asr.api.models.AsrTaskModel;

/**
 * @author 智慧工厂@M
 *
 */
public interface AsrModelsMapper {
	/**
	 * 通过UUID获取任务模型
	 * 
	 * @param uuid
	 * @return
	 */
	AsrTaskModel findAsrTaskModelByUuid(@Param("uuid") String uuid);

	/**
	 * 添加一个新的语音任务模型
	 * 
	 * @param model
	 * @return 新增入的主键
	 */
	int insertAsNewAsrTaskModel(AsrTaskModel model);

	/**
	 * 修改语音任务模型的状态
	 * 
	 * @param model
	 * @return
	 */
	int updateAsrTaskModelStatus(AsrTaskModel model);

	/**
	 * 更新录音地址
	 * 
	 * @param model
	 * @return
	 */
	int updateAsrTaskMediaUrl(AsrTaskModel model);

	/**
	 * 修改语音任务模型的运行时间
	 * 
	 * @param model
	 * @return
	 */
	int updateAsrTaskModelRunningTime(AsrTaskModel model);

	/**
	 * 更新任务ID
	 * 
	 * @param model
	 * @return
	 */
	int updateAsrTaskModelRequestId(AsrTaskModel model);

	/**
	 * 修改语音任务的完成结果
	 * 
	 * @param model
	 * @return
	 */
	int completeAsrTaskModel(AsrTaskModel model);

	/**
	 * 记录语音任务的错误信息
	 * 
	 * @param model
	 * @return
	 */
	int recordErrorWithAsrTaskModel(AsrTaskModel model);

	/**
	 * 获取处于尚未结束的任务
	 * 
	 * @return
	 */
	List<AsrTaskModel> findUnCompletedTasks();

	/**
	 * 获得所有的任务
	 * 
	 * @return
	 */
	List<AsrTaskModel> findAllTasks(@Param("begin_at") Date beginAt);

	/**
	 * 根据RequestID获取任务
	 * 
	 * @param requestId
	 * @return
	 */
	AsrTaskModel findAsrTaskModelByRequestId(@Param("requestId") String requestId);

	/**
	 * 升级SEQUENCE
	 * 
	 * @param list
	 * @return
	 */
	int incrementSequence(List<AsrTaskModel> list);

	int updateProjectUUID(SyncUUIDMessage message);
}
