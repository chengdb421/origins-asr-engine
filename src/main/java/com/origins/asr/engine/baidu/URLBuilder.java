package com.origins.asr.engine.baidu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;

/**
 * URL Builder
 * 
 * @author 智慧工厂@M
 *
 */
@Component
public class URLBuilder {
	@Value("${asr.external.http.prefix}")
	private String asrExternalPrefix;

	@SneakyThrows
	public String buildURL(String suffix) {
		return asrExternalPrefix + suffix;
	}
}
