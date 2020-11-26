/**
 * @(#) AsrEngineFactory.java ASR引擎
 */
package com.origins.asr.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * @author 智慧工厂@M
 *
 */
@Component
@Getter
public class AsrEngineFactory {
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * 根据引擎名称获取引擎
	 * 
	 * @param engineName
	 * @return
	 */
	public AsrEngine getAsrEngineByName(String engineName) {
		AsrEngine engine = null;
		try {
			engine = applicationContext.getBean(engineName, AsrEngine.class);
		} catch (Exception e) {
		}

		if (engine == null) {
			throw new RuntimeException("Unknown asr engine: " + engineName);
		}
		return engine;
	}
}
