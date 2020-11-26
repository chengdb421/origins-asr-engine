/**
 * @(#) SimpleKeywordEvaluateModelConfiguration.java ASR引擎
 */
package com.origins.evaluate.keyword;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.origins.asr.api.IOUtils;

import lombok.Cleanup;
import lombok.SneakyThrows;

/**
 * @author 智慧工厂@M
 *
 */
public class SimpleKeywordEvaluateModelConfiguration {
	/**
	 * 配置文件路径
	 */
	private final String configurationPath;

	private final String namespaceURI = "https://www.cnwansun.com/tech/audio/evaluate/simple";

	private final Hashtable<String, String> parameters = new Hashtable<>();

	public SimpleKeywordEvaluateModelConfiguration(String configurationPath) {
		this.configurationPath = configurationPath;
		this.initialize();
	}

	/**
	 * 初始化配置
	 */
	@SneakyThrows
	void initialize() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		@Cleanup
		InputStream in = new ByteArrayInputStream(IOUtils.readFile(configurationPath));
		Document document = factory.newDocumentBuilder().parse(in);
		NodeList modelNodes = document.getElementsByTagNameNS(namespaceURI, "model");
		if (modelNodes.getLength() < 1) {
			throw new RuntimeException("配置错误:" + configurationPath);
		}

		Element modelElement = (Element) modelNodes.item(0);
		NodeList parameterNodes = modelElement.getElementsByTagNameNS(namespaceURI, "parameter");
		for (int i = 0; i < parameterNodes.getLength(); i++) {
			Element parameterElement = (Element) parameterNodes.item(i);
			String pname = parameterElement.getAttribute("name");
			String pvalue = parameterElement.getAttribute("value");
			if (pname == null || pvalue == null) {
				continue;
			}
			parameters.put(pname, pvalue);
		}
	}

	public String getParameter(String parameterName) {
		if (parameterName == null) {
			return null;
		}
		return parameters.get(parameterName);
	}
}
