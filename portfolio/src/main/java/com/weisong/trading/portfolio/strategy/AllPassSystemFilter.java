package com.weisong.trading.portfolio.strategy;

import java.util.Date;
import java.util.Map;

import com.weisong.trading.portfolio.model.MonthlySelection;
import com.weisong.trading.portfolio.model.SystemSelection;
import com.weisong.trading.portfolio.model.YearAndMonth;

public class AllPassSystemFilter extends AbstractSystemFilter {

	static public int DAYS = 21;
	
	public AllPassSystemFilter(Config config) {
		super(config);
	}

	@Override
	protected void filter(SystemSelection selection, 
			YearAndMonth ym, Map<String, Object> ctx) 
			throws Exception {
		Date date = getSelectionDay(ym);
		MonthlySelection ms = new MonthlySelection(ym, date, 1);
		selection.addMonthlySelection(ym, ms);
	}

	static public class Config extends SystemFilter.Config {

		public Config() {
		}
		
		public Config(Map<String, String> props) {
			super(props);
		}
		
		@Override
		public String getName() {
			return "all-pass";
		}
	}
	
	static public class Builder extends SystemFilter.Builder<AllPassSystemFilter> {
		@Override
		public AllPassSystemFilter create(Map<String, String> props) {
			return new AllPassSystemFilter(new Config(props));
		}

		@Override
		public AllPassSystemFilter createDefault() {
			return new AllPassSystemFilter(new Config());
		}
	}
}
