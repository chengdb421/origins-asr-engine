/**
 * @(#) EvaluateCallback.java ASR引擎
 */
package com.origins.evaluate;

/**
 * @author 智慧工厂@M
 *
 */
@FunctionalInterface
public interface EvaluateCallback {
	void callback(EvaluateResult result);
}
