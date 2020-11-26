/**
 * @(#) EmergencyEvaluateChannel.java ASR引擎
 */
package com.origins.evaluate.api.emergency;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.origins.evaluate.EvaluateModelManager;
import com.origins.evaluate.EvaluateModelMetaData;
import com.origins.evaluate.EvaluateRequest;
import com.origins.evaluate.EvaluateResult;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@RequestMapping("/evaluate")
@Controller
@Slf4j
public class EmergencyEvaluateChannel {
	@Autowired
	private EvaluateModelManager manager;

	/**
	 * 实时测评
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/eval")
	@ApiOperation(value = "实时测评", notes = "EVAL")
	@ApiImplicitParams(@ApiImplicitParam(name = "实时测评", paramType = "eval", dataTypeClass = EvaluateRequest.class))
	@ResponseBody
	public List<EvaluateResult> eval(@RequestBody EvaluateRequest request) {
		log.info("开始测评");
		return manager.getModels().stream().map(model -> model.evaluate(request))	.collect(Collectors.toList());
	}

	/**
	 * 实时获取系统内可用的评测模型
	 * 
	 * @return
	 */
	@GetMapping("/models")
	@ApiOperation(value = "获取系统内可用的测评模型", notes = "getAllModels")
	@ApiImplicitParams(@ApiImplicitParam(name = "获取系统内可用的测评模型", paramType = "getAllModels"))
	@ResponseBody
	public List<EvaluateModelMetaData> getAllModels() {
		return manager.getModels().stream().map(model -> model.getEvaluateModelMetaData()).collect(Collectors.toList());
	}
}
