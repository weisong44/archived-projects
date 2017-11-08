package com.weisong.trading.portfolio.strategy;

import java.util.Map;

public class NetProfitSystemFilter1 extends NetProfitSystemFilter {

	static public class Config extends NetProfitSystemFilter.Config {
		
		public Config() {
		}
		
		public Config(Map<String, String> props) {
			super(props);
		}
				
		@Override
		public String getName() {
			return "net-profit-1";
		}
	}
	
	static public class Builder extends SystemFilter.Builder<NetProfitSystemFilter1> {
		@Override
		public NetProfitSystemFilter1 create(Map<String, String> props) {
			return new NetProfitSystemFilter1(new Config(props));
		}

		@Override
		public NetProfitSystemFilter1 createDefault() {
			Config config = new Config();
			config.setPeriod(4);
			return new NetProfitSystemFilter1(config);
		}
	}
	
	public NetProfitSystemFilter1(Config config) {
		super(config);
	}
}
