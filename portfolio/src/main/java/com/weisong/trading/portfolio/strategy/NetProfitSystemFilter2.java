package com.weisong.trading.portfolio.strategy;

import java.util.Map;

public class NetProfitSystemFilter2 extends NetProfitSystemFilter {

	static public class Config extends NetProfitSystemFilter.Config {
		
		public Config() {
		}
		
		public Config(Map<String, String> props) {
			super(props);
		}
				
		@Override
		public String getName() {
			return "net-profit-2";
		}
	}
	
	static public class Builder extends SystemFilter.Builder<NetProfitSystemFilter2> {
		@Override
		public NetProfitSystemFilter2 create(Map<String, String> props) {
			return new NetProfitSystemFilter2(new Config(props));
		}

		@Override
		public NetProfitSystemFilter2 createDefault() {
			Config config = new Config();
			config.setPeriod(12);
			return new NetProfitSystemFilter2(config);
		}
	}
	
	public NetProfitSystemFilter2(Config config) {
		super(config);
	}
}
