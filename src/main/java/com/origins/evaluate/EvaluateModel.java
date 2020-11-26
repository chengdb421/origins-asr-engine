/**
 * @(#) EvaluateModel.java ASR引擎
 */
package com.origins.evaluate;

/**
 * 测评模型
 * 
 * @author 智慧工厂@M
 *
 */
public abstract class EvaluateModel extends Configurable {
	/**
	 * 获取模型描述
	 * 
	 * @return
	 */
	public abstract EvaluateModelMetaData getEvaluateModelMetaData();

	/**
	 * 执行测评
	 * 
	 * @param evaluateRequest 测评请求
	 * @return
	 */
	public abstract EvaluateResult evaluate(EvaluateRequest evaluateRequest);

	/**
	 * 异步测评
	 * 
	 * @param evaluateRequest
	 * @param callback
	 */
	public abstract void evaluateAsync(EvaluateRequest evaluateRequest, EvaluateCallback callback);
}
