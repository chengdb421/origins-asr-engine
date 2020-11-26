/**
 * @(#) Testing.java ASR引擎
 */
package com.origins.qc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.origins.asr.boot.Bootstrap;
import com.origins.asr.engine.AsrEngine;
import com.origins.asr.engine.AsrEngineFactory;
import com.origins.asr.engine.AsrTask;
import com.origins.asr.engine.AsrTaskCreationAndGetResultMessage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@Slf4j
public class Testing {
	@Autowired
	private AsrEngineFactory asrEngineFactory;

	@Test
	@SneakyThrows
	public void test() {
		AsrEngine engine = asrEngineFactory.getAsrEngineByName("wansun");
		Assert.assertNotNull(engine);
		AsrTask incoming = new AsrTask("7c75c89d-f19a-11ea-9031-7085c288e069", "8475334a-f19b-11ea-9031-7085c7883349",
				"http://192.165.4.13:25080/d/pbxrecord/pbxrecord/000EA9050ED7/20190701/39a10a0f8d32_AAP-20200630-002-000494_108684__N_268_13589792493_20200724-103747_16591.mp3",
				"wansun");
		incoming.setTemporaryMediaUrl(incoming.getRawMediaUrl());
		engine.acceptNewAsrTask(incoming, e -> {
			if (e instanceof AsrTaskCreationAndGetResultMessage) {
				AsrTaskCreationAndGetResultMessage message = (AsrTaskCreationAndGetResultMessage) e;
				log.info("结果为:" + message.getResultText());
				synchronized (Testing.this) {
					Testing.this.notify();
				}
			}
		});

		synchronized (Testing.this) {
			Testing.this.wait();
		}

	}
}
