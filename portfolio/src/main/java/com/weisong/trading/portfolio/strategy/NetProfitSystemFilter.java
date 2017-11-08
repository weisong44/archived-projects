package com.weisong.trading.portfolio.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.generator.Generator;
import com.weisong.trading.portfolio.model.DailyStats;
import com.weisong.trading.portfolio.model.MonthlySelection;
import com.weisong.trading.portfolio.model.SystemEquity;
import com.weisong.trading.portfolio.model.SystemSelection;
import com.weisong.trading.portfolio.model.YearAndMonth;
import com.weisong.trading.portfolio.util.Util;

abstract public class NetProfitSystemFilter extends AbstractSystemFilter {

	static public int DAYS = 21;
	
	abstract static public class Config extends SystemFilter.Config {
		
		static public String KEY_PERIOD = "period";
		
		public Config() {
		}
		
		public Config(Map<String, String> props) {
			super(props);
		}
				
		public Config setPeriod(int months) {
			put(KEY_PERIOD, months);
			return this;
		}
		
		public int getPeriod() {
			return getInt(KEY_PERIOD);
		}
		
	}
	
	public NetProfitSystemFilter(Config config) {
		super(config);
	}

	@Override
	protected void filter(SystemSelection selection, 
			YearAndMonth ym, Map<String, Object> ctx) 
			throws Exception {
		
		int period = ((Config) config).getPeriod();

		SystemEquity systemEquity = (SystemEquity) ctx.get(CTX_KEY_EQUITY_CURVE_RAW);
		if(systemEquity == null) {
			SystemSelection s1 = new SystemSelection(selection.getSystem());
			new AllPassSystemFilter.Builder().createDefault().filterAll(s1);
			systemEquity = new Generator().generateSystemEquityCurve(s1);
			ctx.put(CTX_KEY_EQUITY_CURVE_RAW, systemEquity);
		}
		
		Date endDate = getSelectionDay(ym);		
		Date begDate = getSelectionDay(YearAndMonth.shift(ym, -period));
		
		MonthlySelection ms = selection.getMonthlySelection(ym);
		if(ms == null) {
			ms = new MonthlySelection(ym, endDate, 1);
		} else if(ms.getContracts() <= 0) {
			return;
		}

		ArrayList<DailyStats> dailyStats = selection.getSystem().getDailyStats();
		if(dailyStats.get(0).getDate().after(begDate)) {
			if(ms != null) {
				ms.setContracts(0);
				Util.logDebug(String.format(" - %s %s %d (within lookback)",
					selection.getSystem().getSystemId(), ym, ms.getContracts()));
			}
			return;
		}
		
		float gain = systemEquity.getGain(begDate, endDate) / period;
		if(ms.getContracts() > 0 && gain < 0) {
			Util.logDebug(String.format(" - %s %s Disable, reason: %d month profit < 0",
				selection.getSystem().getSystemId(), ms.getYearAndMonth(), period));
			ms.setContracts(0);
		}
		
		selection.addMonthlySelection(ym, ms);
		
		Util.logDebug(String.format(" - %s %s [%s-%s] %6.0f   %d",
				selection.getSystem().getSystemId(), ym,
				Constants.df1.format(begDate), Constants.df1.format(endDate),
				gain, ms.getContracts()));
		
	}
}
