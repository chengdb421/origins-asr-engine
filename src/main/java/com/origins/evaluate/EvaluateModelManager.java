/**
 * @(#) EvaluateModelManager.java ASR引擎
 */
package com.origins.evaluate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 测评模型管理器
 * 
 * @author 智慧工厂@M
 *
 */
@Component
@Slf4j
public class EvaluateModelManager {
	private final List<EvaluateModel> models = new ArrayList<>();

	@Autowired
	private ApplicationContext applicationContext;

	public List<EvaluateModel> getModels() {
		if (models.isEmpty()) {
			log.info("开始加载模型库:" + applicationContext.getBeansOfType(EvaluateModel.class));
			applicationContext.getBeansOfType(EvaluateModel.class).values().forEach(models::add);
		}
		return models;
	}

	public EvaluateModel getEvaluateModel(String modelName) {
		EvaluateModel matched = getModels().stream()
				.filter(x -> x.getEvaluateModelMetaData().getName().equals(modelName)).findFirst().orElse(null);
		if (matched == null) {
			throw new RuntimeException("未知评测模型:" + modelName);
		}
		return matched;
	}

}
