/**
 * @(#) TimeUtils.java ASR引擎
 */
package com.origins.asr.engine.utils;

import java.util.Date;

import org.joda.time.LocalTime;

/**
 * @author 智慧工厂@M
 *
 */
public class TimeUtils {
	public static Date localTimeToDate(LocalTime time) {
		return time.toDateTimeToday().toDate();
	}
}
