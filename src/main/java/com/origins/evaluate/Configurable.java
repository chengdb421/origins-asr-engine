/**
 * @(#) Configurable.java ASR引擎
 */
package com.origins.evaluate;

import java.util.function.Function;

/**
 * 可配置对象
 * 
 * @author 智慧工厂@M
 *
 */
public abstract class Configurable {

	public abstract String getParameter(String parameterName);

	public String getParameter(String parameterName, String failback) {
		String string = getParameter(parameterName);
		return string == null ? failback : string;
	}

	<T> T getParameter(String parameterName, Function<String, T> transformer) {
		String string = getParameter(parameterName);
		return string == null ? null : transformer.apply(string);
	}

	<T> T getParameter(String parameterName, Function<String, T> transformer, T failback) {
		String string = getParameter(parameterName);
		return string == null ? failback : transformer.apply(string);
	}

	public int getParameterAsInt(String parameterName) {
		return getParameter(parameterName, Integer::parseInt);
	}

	public int getParameterAsInt(String parameterName, int failback) {
		return getParameter(parameterName, Integer::parseInt, failback);
	}

	public long getParameterAsLong(String parameterName) {
		return getParameter(parameterName, Long::parseLong);
	}

	public long getParameterAsLong(String parameterName, long failback) {
		return getParameter(parameterName, Long::parseLong, failback);
	}

	public double getParameterAsDouble(String parameterName) {
		return getParameter(parameterName, Double::parseDouble);
	}

	public double getParameterAsDouble(String parameterName, double failback) {
		return getParameter(parameterName, Double::parseDouble, failback);
	}

	public float getParameterAsFloat(String parameterName) {
		return getParameter(parameterName, Float::parseFloat);
	}

	public float getParameterAsFloat(String parameterName, float failback) {
		return getParameter(parameterName, Float::parseFloat, failback);
	}

	public boolean getParameterAsBoolean(String parameterName) {
		return getParameter(parameterName, Boolean::parseBoolean);
	}

	public boolean getParameterAsBoolean(String parameterName, boolean failback) {
		return getParameter(parameterName, Boolean::parseBoolean, failback);
	}
}
