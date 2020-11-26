/**
 * @(#) AsrClientFactory.java ASR引擎
 */
package com.origins.asr.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.baidu.acu.pie.client.AsrClient;
import com.baidu.acu.pie.model.AsrConfig;
import com.baidu.acu.pie.model.AsrProduct;

/**
 * @author 智慧工厂@M
 *
 */
@Component
public class AsrClientFactory {
	@Value("${wansun.asr.server.ip}")
	private String wansunAsrServerIp;

	@Value("${wansun.asr.server.port}")
	private int wansunAsrServerPort;

	@Value("${wansun.asr.server.username}")
	private String wansunAsrServerUsername;

	@Value("${wansun.asr.server.password}")
	private String wansunAsrServerPassword;

	/**
	 * 创建ASR CLIENT
	 * 
	 * @return
	 */
	public AsrClient createAsrClient() {
		AsrConfig asrConfig = AsrConfig.builder().appName("wansun-asr").serverIp(wansunAsrServerIp)
				.serverPort(wansunAsrServerPort).product(AsrProduct.CUSTOMER_SERVICE_FINANCE)
				.userName(wansunAsrServerUsername).password(wansunAsrServerPassword).build();
		AsrClient asrClient = com.baidu.acu.pie.client.AsrClientFactory.buildClient(asrConfig);
		return asrClient;
	}
}
