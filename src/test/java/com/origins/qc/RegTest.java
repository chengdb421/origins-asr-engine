/**
 * @(#) RegTest.java ASR引擎
 */
package com.origins.qc;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

/**
 * @author 智慧工厂@M
 *
 */
public class RegTest {

	@Data
	@ToString
	static class A {
		String name;
		Date when;
	}

	/**
	 * @param args
	 */
	@SneakyThrows
	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("(妈的)|(你妈)");
		Matcher matcher = pattern.matcher("你他妈的什么时候还钱");
		while (matcher.find()) {
			System.out.println("###" + matcher.start() + " :" + matcher.end() + " ::" + matcher.group());
		}

		String json = "{\"name\":\"mclaren\",\"when\":1602235044904}";
		A a = JSON.parseObject(json, A.class);
		System.out.println(a);
	}

}
