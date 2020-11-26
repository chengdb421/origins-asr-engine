/**
 * @(#) StringUtils.java ASR引擎
 */
package com.origins.utils;

/**
 * @author 智慧工厂@M
 *
 */
public class StringUtils {
	public static final boolean isEmpty(String input) {
		return input == null || input.isEmpty();
	}

	public static final boolean isNotEmpty(String input) {
		return !isEmpty(input);
	}
}
