package com.weisong.trading.portfolio.strategy;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public interface Processor {
	
	Config getConfig();
	
	static abstract public class Config {
		
		abstract public String getName();
		abstract String getDefaultKeyPrefix();
		abstract String getKeyPrefix();
		
		private Map<String, String> properties;
		
		public Config() {
			this.properties = new HashMap<>();
		}
		
		public Config(Map<String, String> props) {
			this.properties = props;
		}
		
		public void putDefault(String key, String value) {
			properties.put(getDefaultKey(key), value);
		}
		
		public void put(String key, String value) {
			properties.put(getKey(key), value);
		}
		
		public void put(String key, Integer value) {
			put(key, value.toString());
		}
		
		public void put(String key, Float value) {
			put(key, value.toString());
		}
		
		public String get(String key) {
			String value = properties.get(getDefaultKey(key));
			if(value != null) {
				return value;
			}
			return properties.get(getKey(key));
		}
		
		public Integer getInt(String key) {
			String value = get(key);
			return value != null ? Integer.valueOf(value) : null;
		}
		
		public Float getFloat(String key) {
			String value = get(key);
			return value != null ? Float.valueOf(value) : null;
		}
		
		protected String getDefaultKey(String key) {
			return String.format("%s.%s", getDefaultKeyPrefix(), key);
		}
		
		protected String getKey(String key) {
			return String.format("%s.%s.%s", getKeyPrefix(), getName(), key);
		}
	}
	
	static public interface Builder<P extends Processor> {
		P create(Map<String, String> props);
		P createDefault();
	}

	static public Map<String, String> loadConfig(String fname) throws Exception {
		File file = new File(fname);
		if(!file.exists()) {
			throw new RuntimeException("File not found: " + fname);
		}
		if(!file.isFile()) {
			throw new RuntimeException(fname + " is not a file");
		}

		Properties props = new Properties();
		props.load(new FileInputStream(fname));
		Map<String, String> map = new HashMap<>();
		props.forEach((k, v) -> map.put(k.toString(),  v.toString()));
		return map;
	}
}
