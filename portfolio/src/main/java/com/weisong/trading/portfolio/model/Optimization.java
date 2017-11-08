package com.weisong.trading.portfolio.model;

import java.util.HashMap;
import java.util.Map;

import com.weisong.trading.portfolio.strategy.SystemFilterChain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Optimization {

	public class Context {
		@Getter private Portfolio portfolio;
		@Getter private Map<String, Object> map = new HashMap<>();
		public Object get(String key) {
			return map.get(key);
		}
		public void put(String key, Object value) {
			map.put(key, value);
		}
		public void remove(String key) {
			map.remove(key);
		}
	}

	@AllArgsConstructor
	public class Profile {
		@Getter private SystemFilterChain systemFilterChain;
	}

	@Getter private Context ctx;
	@Getter private Profile profile;
	
	public Optimization(Portfolio portfolio) {
		this.ctx = new Context();
		ctx.portfolio = portfolio;
	}
}
