/**
 * @(#) BaiduAsrAPI.java ASR引擎
 */
package com.origins.asr.engine.baidu;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 智慧工厂@M
 *
 */
@Component
@Slf4j
public class BaiduAsrAPI {
	@Value("${baidu.api.key}")
	private String appKey;

	@Value("${baidu.secret.key}")
	private String secretKey;

	public String getAccessToken() {
		return getAccessToken(appKey, secretKey);
	}

	@SneakyThrows
	public String getAccessToken(String appKey, String secretKey) {
		Response response = Jsoup
				.connect("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + appKey
						+ "&client_secret=" + secretKey)
				.ignoreContentType(true).method(Method.POST).execute();
		if (response.statusCode() != 200) {
			throw new RuntimeException("认证失败");
		}

		return JSON.parseObject(response.body()).getString("access_token");
	}

	@SneakyThrows
	public String createAsrTask(BaiduAsrCreateTaskRequest baiduAsrRequest) {
		String json = JSON.toJSONString(baiduAsrRequest);
		log.info("发送创建任务的请求为:{}", json);
		String accessToken = getAccessToken();
		log.info("Access token is " + accessToken);
		Response response = Jsoup.connect("https://aip.baidubce.com/rpc/2.0/aasr/v1/create?access_token=" + accessToken)
				.method(Method.POST).requestBody(json).ignoreContentType(true)
				.header("Content-Type", "application/json;charset=UTF-8").execute();
		if (response.statusCode() != 200) {
			throw new RuntimeException("HTTP faild:" + response.statusCode());
		}

		JSONObject ret = JSON.parseObject(response.body());
		if (ret.getString("error_msg") != null) {
			throw new RuntimeException("调用接口失败:" + ret.getString("error_msg"));
		}
		return JSON.parseObject(response.body(), BaiduAsrCreateTaskResponse.class).getTaskId();
	}

	@SneakyThrows
	public void retrieveAsrResult(BaiduAsrGetTaskResultRequest request, BaiduAsrGetTaskResultHandler handler) {
		String json = JSON.toJSONString(request);
		log.info("请求为:" + json);
		Response response = Jsoup
				.connect("https://aip.baidubce.com/rpc/2.0/aasr/v1/query?access_token=" + getAccessToken())
				.method(Method.POST).requestBody(json).ignoreContentType(true)
				.header("Content-Type", "application/json;charset=UTF-8").execute();
		if (response.statusCode() != 200) {
			throw new RuntimeException("HTTP faild:" + response.statusCode());
		}

		JSONObject ret = JSON.parseObject(response.body());
		log.info("收到响应为:" + response.body());
		JSONArray box = ret.getJSONArray("tasks_info");
		box.stream().map(x -> (JSONObject) x).forEach(x -> {
			String taskId = x.getString("task_id");
			String status = x.getString("task_status");
			JSONObject taskResult = x.getJSONObject("task_result");
			if (status.equalsIgnoreCase("Running")) {
				handler.handleMessage(new BaiduAsrGetTaskResultRunningMessage(taskId));
			} else if (status.equalsIgnoreCase("Failure")) {
				handler.handleMessage(new BaiduAsrGetTaskResultErrorMessage(taskId, taskResult.getString("err_msg")));
			} else if (status.equalsIgnoreCase("Success")) {
				StringBuffer buf = new StringBuffer();
				taskResult.getJSONArray("result").stream().map(rt -> rt.toString()).forEach(buf::append);
				handler.handleMessage(new BaiduAsrGetTaskResultOkMessage(taskId, buf.toString()));
				buf.delete(0, buf.length());
			}
		});
	}
}
