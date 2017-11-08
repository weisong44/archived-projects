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

public class NetProfitToMaxDdSystemFilter extends AbstractSystemFilter {

	static public class Config extends SystemFilter.Config {
		
		final static public String KEY_LOOKBACK = "lookback";
		final static public String KEY_RATIO = "ratio";
		
		public Config() {
		}
		
		public Config(Map<String, String> props) {
			super(props);
		}
				
		@Override
		public String getName() {
			return "profit-to-maxdd";
		}
		
		public Config setLookback(int lookback) {
			put(KEY_LOOKBACK, lookback);
			return this;
		}
		
		public Config setRatio(float ratio) {
			put(KEY_RATIO, ratio);
			return this;
		}
		
		public int getLookback() {
			return getInt(KEY_LOOKBACK);
		}
		
		public float getRatio() {
			return getFloat(KEY_RATIO);
		}
	}
	
	static public class Builder extends SystemFilter.Builder<NetProfitToMaxDdSystemFilter> {
		@Override
		public NetProfitToMaxDdSystemFilter create(Map<String, String> props) {
			return new NetProfitToMaxDdSystemFilter(new Config(props));
		}

		@Override
		public NetProfitToMaxDdSystemFilter createDefault() {
			return new NetProfitToMaxDdSystemFilter(new Config()
				.setLookback(36)
				.setRatio(2.5F)
			);
		}
	}
	
	public NetProfitToMaxDdSystemFilter(Config config) {
		super(config);
	}

	@Override
	protected void filter(SystemSelection selection, 
			YearAndMonth ym, Map<String, Object> ctx) 
			throws Exception {
		
		int lookback = ((Config) config).getLookback();
		float ratio = ((Config) config).getRatio();
		
		ArrayList<DailyStats> dailyStats = selection.getSystem().getDailyStats();

		MonthlySelection ms = selection.getMonthlySelection(ym);
		
		SystemEquity systemEquity = (SystemEquity) ctx.get(CTX_KEY_EQUITY_CURVE_RAW);
		if(systemEquity == null) {
			SystemSelection s1 = new SystemSelection(selection.getSystem());
			new AllPassSystemFilter.Builder().createDefault().filterAll(s1);
			systemEquity = new Generator().generateSystemEquityCurve(s1);
			ctx.put(CTX_KEY_EQUITY_CURVE_RAW, systemEquity);
		}
		
		Date begDate = getSelectionDay(YearAndMonth.shift(ym, -lookback));
		Date endDate = getSelectionDay(ym);
		if(dailyStats.get(0).getDate().after(begDate)) {
			if(ms != null) {
				ms.setContracts(0);
				Util.logDebug(String.format(" - %s %s %d (within lookback)",
					selection.getSystem().getSystemId(), ym, ms.getContracts()));
			}
			return;
		}
		
		if(ms != null && ms.getContracts() <= 0) {
			Util.logDebug(String.format(" - %s %s %d (already disabled)",
				selection.getSystem().getSystemId(), ym, ms.getContracts()));
			return; // Already disabled
		}
		
		float gain = systemEquity.getGain(begDate, endDate) * 12 / lookback;
		float maxDd = Math.abs(systemEquity.getMaxDd(begDate, endDate));
		int contracts = gain >= maxDd * ratio ? 1 : 0;

		if(ms == null) {
			ms = new MonthlySelection(ym, endDate, contracts);
			selection.addMonthlySelection(ym, ms);
		} else {
			ms.setContracts(contracts);
		}

		if(contracts <= 0) {
			Util.logDebug(String.format(" - %s %s Disable, reason: abs(gain/maxDd) < %.2f", 
				selection.getSystem().getSystemId(), ms.getYearAndMonth(), ratio));
		}
		
		Util.logDebug(String.format(" - %s %s [%s-%s] %6.0f %6.0f   %d",
			selection.getSystem().getSystemId(), ym, 
			Constants.df1.format(begDate), Constants.df1.format(endDate),
			gain, -maxDd, ms.getContracts()));
		
	}
}
