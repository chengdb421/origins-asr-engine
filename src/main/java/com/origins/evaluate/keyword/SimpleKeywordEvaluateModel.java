/**
 * @(#) SimpleKeywordEvaluateModel.java ASR引擎
 */
package com.origins.evaluate.keyword;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.origins.evaluate.EvaluateCallback;
import com.origins.evaluate.EvaluateIssueTypes;
import com.origins.evaluate.EvaluateModel;
import com.origins.evaluate.EvaluateModelMetaData;
import com.origins.evaluate.EvaluateRequest;
import com.origins.evaluate.EvaluateResult;
import com.origins.evaluate.EvaluateResultPassedMessage;
import com.origins.evaluate.EvaluateResultWarningMessage;
import com.origins.evaluate.EvaluateWarningEvidence;
import com.origins.evaluate.EvaluateWarningEvidenceChain;
import com.origins.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于简单关键字命中的测评模型
 * 
 * @author 智慧工厂@M
 *
 */
@Service("simple-keyword-evaluate-model")
@Slf4j
public class SimpleKeywordEvaluateModel extends EvaluateModel {
	/**
	 * 模型配置项目
	 */
	@Autowired
	private SimpleKeywordEvaluateModelConfiguration configuration;

	private EvaluateModelMetaData metaData = null;

	@Autowired
	@Qualifier("evaluate-disptcher-threadpool")
	private ExecutorService exec;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.evaluate.EvaluateModel#getEvaluateModelMetaData()
	 */
	@Override
	public EvaluateModelMetaData getEvaluateModelMetaData() {
		if (metaData == null) {
			metaData = EvaluateModelMetaData.builder().name(configuration.getParameter("name"))
					.version(configuration.getParameter("version"))
					.description(configuration.getParameter("description")).build();
		}
		return metaData;
	}

	EvaluateWarningEvidenceChain evaluate(EvaluateRequest evaluateRequest, EvaluateIssueTypes issueType) {
		String expr = configuration.getParameter(issueType.toString());
		EvaluateWarningEvidenceChain chain = new EvaluateWarningEvidenceChain(issueType);
		if (StringUtils.isEmpty(expr)) {
			return chain;
		}

		Pattern pattern = Pattern.compile(expr);
		Matcher matcher = pattern.matcher(evaluateRequest.getText());

		while (matcher.find()) {
			chain.getEvidenceChain().add(EvaluateWarningEvidence.builder().startAt(matcher.start()).endAt(matcher.end())
					.keyword(matcher.group()).timestamp(System.currentTimeMillis()).issueType(issueType).build());
		}
		return chain;
	}

	/*
	 * 实时测评 (non-Javadoc)
	 * 
	 * @see com.cnwansun.evaluate.EvaluateModel#evaluate(com.cnwansun.evaluate.
	 * EvaluateRequest)
	 */
	@Override
	public EvaluateResult evaluate(EvaluateRequest evaluateRequest) {
		List<EvaluateWarningEvidenceChain> chain = Stream
				.of(EvaluateIssueTypes.ISSUE_LAW, EvaluateIssueTypes.ISSUE_REGULAR)
				.map(issue -> evaluate(evaluateRequest, issue)).filter(x -> !x.getEvidenceChain().isEmpty())
				.collect(Collectors.toList());
		if (chain.size() == 0) {
			log.info("评测通过!!!!");
			return new EvaluateResultPassedMessage(getEvaluateModelMetaData(), evaluateRequest.getUuid(),
					evaluateRequest.getText(), System.currentTimeMillis());
		}

		EvaluateResultWarningMessage message = new EvaluateResultWarningMessage(getEvaluateModelMetaData(),
				evaluateRequest.getUuid(), evaluateRequest.getText(), System.currentTimeMillis());
		chain.forEach(message.getEvidenceChain()::add);
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.evaluate.EvaluateModel#evaluateAsync(com.cnwansun.evaluate.
	 * EvaluateRequest, com.cnwansun.evaluate.EvaluateCallback)
	 */
	@Override
	public void evaluateAsync(EvaluateRequest evaluateRequest, EvaluateCallback callback) {
		exec.execute(() -> callback.callback(this.evaluate(evaluateRequest)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cnwansun.evaluate.Configurable#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(String parameterName) {
		return configuration.getParameter(parameterName);
	}

}
