/**
 * @(#) EvaluateModelMetaData.java ASR引擎
 */
package com.origins.evaluate;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 智慧工厂@M
 *
 */
@RequiredArgsConstructor
@Getter
@Builder
public class EvaluateModelMetaData {
	private final String name;
	private final String version;
	private final String description;
}
