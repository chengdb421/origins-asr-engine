/**
 * @(#) DefaultLocalRepository.java ASR引擎
 */
package com.origins.asr.api.core;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.origins.asr.api.LocalRepository;
import com.origins.asr.api.mapper.AsrModelsMapper;
import com.origins.asr.api.models.AsrTaskModel;
import com.origins.asr.engine.AsrTask;

/**
 * @author 智慧工厂@M
 *
 */
@Service
public class DefaultLocalRepository implements LocalRepository {
	@Autowired
	private AsrModelsMapper mapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.api.LocalRepository#findAsrTaskByUuid(java.lang.String)
	 */
	public AsrTask findAsrTaskByUuid(String uuid) {
		return findAsrTaskByUuid(uuid, model -> {
			if (model == null) {
				return null;
			}
			return new AsrTask(model.getProjectUUID(), model.getUuid(), null, model.getRawMediaUrl());
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cnwansun.asr.api.LocalRepository#createAsrTask(com.cnwansun.asr.engine.
	 * AsrTask)
	 */
	@Override
	public int createAsrTask(AsrTask task) {
		return mapper.insertAsNewAsrTaskModel(AsrTaskModel.builder().projectUUID(task.getProjectUUID())
				.uuid(task.getUuid()).rawMediaUrl(task.getRawMediaUrl()).temporaryMediaUrl(task.getTemporaryMediaUrl())
				.modelName(task.getModelName()).build());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.api.LocalRepository#findAsrTaskByUuid(java.lang.String,
	 * java.util.function.Function)
	 */
	@Override
	public <T> T findAsrTaskByUuid(String uuid, Function<AsrTaskModel, T> converter) {
		return converter.apply(mapper.findAsrTaskModelByUuid(uuid));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.asr.api.LocalRepository#getAsrModelsMapper()
	 */
	@Override
	public AsrModelsMapper getAsrModelsMapper() {
		return mapper;
	}

}
