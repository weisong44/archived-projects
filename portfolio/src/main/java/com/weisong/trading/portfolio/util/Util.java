package com.weisong.trading.portfolio.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weisong.trading.portfolio.Constants;

public class Util {

	static public void logDebug(String s) {
		if(LogLevel.DEBUG <= Constants.logLevel) {
			logInternal("DEBUG", s);
		}
	}
	
	static public void logInfo(String s) {
		if(LogLevel.INFO <= Constants.logLevel) {
			logInternal("INFO ", s);
		}
	}
	
	static public void logWarn(String s) {
		if(LogLevel.WARNING <= Constants.logLevel) {
			logInternal("WARN ", s);
		}
	}
	
	static public void logError(String s) {
		if(LogLevel.ERROR <= Constants.logLevel) {
			logInternal("ERROR", s);
		}
	}

	static private void logInternal(String logLevel, String s) {
		StackTraceElement e = Thread.currentThread().getStackTrace()[3];
		String className = e.getClassName();
		className = className.substring(className.lastIndexOf(".") + 1);
		String source = String.format("%s.%s():%d", 
			className, e.getMethodName(), e.getLineNumber());
		System.out.println((String.format("%s %s %s %s", 
			Constants.df2.format(new Date()), logLevel, source, s)));
	}
	
	static public List<Map<String, String>> readCsv(String fname) throws Exception {
		Util.logInfo("Reading " + fname);
		
		List<Map<String, String>> list = new ArrayList<>();
		FileInputStream fstream = new FileInputStream(fname);
		try(BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
			String line;
			List<String> fields = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(line.length() <= 0) {
					continue;
				}
				if(fields.isEmpty()) {
					String[] tokens = line.split(",");
					for(int i = 0; i < tokens.length; i++) {
						fields.add(tokens[i]);
					}
				} else {
					Map<String, String> mLine = new HashMap<>();
					String[] tokens = line.split(",");
					for(int i = 0; i < tokens.length; i++) {
						mLine.put(fields.get(i), tokens[i]);
					}
					list.add(mLine);
				}
			}
		}
		return list;
	}
}
