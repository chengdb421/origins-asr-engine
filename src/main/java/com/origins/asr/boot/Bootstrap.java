/**
 * @(#) Bootstrap.java ASR引擎
 */
package com.origins.asr.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 智慧工厂@M
 *
 */
@SpringBootApplication
@EnableSwagger2
@EnableScheduling
@ComponentScan({"com.origins.asr", "com.origins.evaluate"})
@MapperScan({ "com.origins.asr.api.mapper", "com.origins.evaluate.api.mapper" })
public class Bootstrap {
	public static void main(String[] args) {
		SpringApplication.run(Bootstrap.class, args);
	}
}
