package com.origins.asr.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Component;

import lombok.SneakyThrows;

@Component
public class MediaDownloader {
	private final int EOF = -1;

	@SneakyThrows
	public byte[] download(String url) {
		HttpURLConnection http = null;
		try {
			http = (HttpURLConnection) new URL(url).openConnection();
			http.setConnectTimeout(3000);
			http.connect();
			InputStream in = http.getInputStream();
			byte[] buf = new byte[256];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int len = EOF;
			do {
				len = in.read(buf);
				if (len != EOF) {
					out.write(buf, 0, len);
				}
			} while (len != EOF);
			in.close();
			return out.toByteArray();
		} finally {
			if (http != null) {
				http.disconnect();
			}
		}
	}
}
