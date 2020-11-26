package com.origins.asr.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.stream.Stream;

public class Proc {
	/**
	 * Execute sub process
	 * 
	 * @param args
	 */
	public static void executeSubProcess(String... args) {
		Process process = null;
		try {
			Stream.of(args).forEach(x -> {
				System.out.print(x);
				System.out.print(" ");
			});
			System.out.println();
			process = new ProcessBuilder(args).start();
			// dump(process.getInputStream(), System.out);
			dump(process.getErrorStream(), System.err);

			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroyForcibly();
			}
		}
	}

	private static void dump(InputStream in, PrintStream out) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;

		while ((line = reader.readLine()) != null) {
			out.println(line);
		}
	}
}
