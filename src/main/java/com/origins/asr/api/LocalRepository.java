/**
 * @(#) LocalRepository.java ASR引擎
 */
package com.origins.asr.api;

import java.util.function.Function;

import com.origins.asr.api.mapper.AsrModelsMapper;
import com.origins.asr.api.models.AsrTaskModel;
import com.origins.asr.engine.AsrTask;

/**
 * @author 智慧工厂@M
 *
 */
public interface LocalRepository {
	<T> T findAsrTaskByUuid(String uuid, Function<AsrTaskModel, T> converter);

	int createAsrTask(AsrTask task);

	AsrModelsMapper getAsrModelsMapper();
}
