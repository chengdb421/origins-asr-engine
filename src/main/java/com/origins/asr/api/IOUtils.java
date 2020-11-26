package com.origins.asr.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtils {
	static Logger logger = LoggerFactory.getLogger(IOUtils.class);
	static {
		try {
			new URL("classpath:xxxx");
			logger.info("避免重复注册URL Handler");
		} catch (Exception e) {
			URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
				@Override
				public URLStreamHandler createURLStreamHandler(String protocol) {
					if ("classpath".equals(protocol)) {
						return new URLStreamHandler() {
							@Override
							protected URLConnection openConnection(URL u) throws IOException {
								return new URLConnection(u) {
									@Override
									public void connect() throws IOException {

									}

									@Override
									public InputStream getInputStream() throws IOException {
										return Thread.currentThread().getContextClassLoader()
												.getResourceAsStream(url.getFile());
									}
								};
							}
						};
					}
					return null;
				}
			});
		}

	}

	public static byte[] readFile(String url) {
		try {
			InputStream in = new URL(url).openConnection().getInputStream();
			byte[] buf = new byte[256];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int len = -1;
			do {
				len = in.read(buf);
				if (len != -1) {
					out.write(buf, 0, len);
				}
			} while (len != -1);
			in.close();
			return out.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static File writeLocalFile(String format, byte[] audioBinaries) {
		try {
			File tempFile = File.createTempFile(UUID.randomUUID().toString(), "." + format);
			FileOutputStream out = new FileOutputStream(tempFile);
			out.write(audioBinaries);
			out.close();
			return tempFile;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		new URL("classpath:4.wav").openConnection().getInputStream();
	}
}
