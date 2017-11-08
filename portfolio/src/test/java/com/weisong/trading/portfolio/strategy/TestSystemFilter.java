package com.weisong.trading.portfolio.strategy;

import org.junit.Assert;
import org.junit.Test;

public class TestSystemFilter {
	
	static public class Config extends SystemFilter.Config {
		String strKey = "str.key";
		String intKey = "int.key";
		String floatKey = "float.key";

		@Override
		public String getName() {
			return "test";
		}
		public Config setIntParam(int value) {
			put(intKey, value);
			return this;
		}
		public Config setStrParam(String value) {
			put(strKey, value);
			return this;
		}
		public Config setFloatParam(float value) {
			put(floatKey, value);
			return this;
		}
		public int getIntParam() {
			return getInt(intKey);
		}
		public String getStrParam() {
			return get(strKey);
		}
		public float getFloatParam() {
			return getFloat(floatKey);
		}
	}
	
	@Test
	public void testConfig() {
		Config config = new Config()
			.setIntParam(1)
			.setFloatParam(2f)
			.setStrParam("string");
		Assert.assertEquals(1, config.getIntParam());
		Assert.assertEquals(2f, config.getFloatParam(), 0.0001f);
		Assert.assertEquals("string", config.getStrParam());
	}
}
