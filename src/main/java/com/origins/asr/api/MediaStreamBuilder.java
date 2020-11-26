/**
 * @(#) MediaStreamBuilder.java ASR引擎
 */
package com.origins.asr.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.origins.asr.engine.AsrTask;
import com.origins.asr.engine.baidu.URLBuilder;

import lombok.Cleanup;
import lombok.SneakyThrows;

/**
 * @author 智慧工厂@M
 *
 */
@Component
public class MediaStreamBuilder {
	@Autowired
	private MediaDownloader mediaDownloader;

	@Autowired
	private File temporaryFileFolder;

	@Autowired
	private URLBuilder urlBuilder;

	/**
	 * 重构录音地址
	 * 
	 * @param rawMediaUrl
	 * @return
	 */
	public String rebuildAsrTaskExternalFileURL(String rawMediaUrl) {
		try {
			byte[] payload = mediaDownloader.download(rawMediaUrl);
			File tempInputFile = IOUtils.writeLocalFile("audio", payload);
			/**
			 * 转换录音采样率
			 */
			File tempOutputFile = new File(temporaryFileFolder, UUID.randomUUID().toString() + ".pcm");
			Proc.executeSubProcess("ffmpeg", "-y", "-i", tempInputFile.getAbsolutePath(), "-acodec", "pcm_s16le", "-f",
					"s16le", "-ac", " 1", "-ar", "8000", tempOutputFile.getAbsolutePath());
			Stream.of(tempInputFile).forEach(x -> x.delete());
			return urlBuilder.buildURL(tempOutputFile.getName());
		} catch (Exception e) {
			throw new RuntimeException("下载录音或转换录音错误，请检查录音地址是否有效或录音格式是否有效" + rawMediaUrl + ",Error:" + getErrorTrace(e));
		}
	}

	@SneakyThrows
	String getErrorTrace(Throwable e) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(out, true);
		e.printStackTrace(writer);
		return new String(out.toByteArray());
	}

	/**
	 * 下载录音到本地并提供文件访问
	 * 
	 * @param task
	 */
	@SneakyThrows
	public void rebuildAsrTaskExternalFileURL(AsrTask task) {
		try {
			byte[] payload = mediaDownloader.download(task.getRawMediaUrl());
			File tempInputFile = IOUtils.writeLocalFile("audio", payload);
			/**
			 * 转换录音采样率
			 */
			File tempOutputFile = new File(temporaryFileFolder, UUID.randomUUID().toString() + ".pcm");
			Proc.executeSubProcess("ffmpeg", "-y", "-i", tempInputFile.getAbsolutePath(), "-acodec", "pcm_s16le", "-f",
					"s16le", "-ac", " 1", "-ar", "8000", tempOutputFile.getAbsolutePath());
			Stream.of(tempInputFile).forEach(x -> x.delete());
			task.setTemporaryMediaUrl(task.getRawMediaUrl());
			@Cleanup
			InputStream in = new FileInputStream(tempOutputFile);
			byte[] stream = new byte[in.available()];
			in.read(stream);
			task.setMediaStream(stream);
			task.setTemporaryMediaUrl(urlBuilder.buildURL(tempOutputFile.getName()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"下载录音或转换录音错误，请检查录音地址是否有效或录音格式是否有效,URL:" + task.getRawMediaUrl() + ",Error:" + getErrorTrace(e));
		}
	}
}
