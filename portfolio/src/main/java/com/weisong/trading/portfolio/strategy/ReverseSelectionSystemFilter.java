package com.weisong.trading.portfolio.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weisong.trading.portfolio.generator.Generator;
import com.weisong.trading.portfolio.model.MonthlySelection;
import com.weisong.trading.portfolio.model.SystemEquity;
import com.weisong.trading.portfolio.model.SystemSelection;
import com.weisong.trading.portfolio.model.YearAndMonth;
import com.weisong.trading.portfolio.util.Util;

public class ReverseSelectionSystemFilter extends AbstractSystemFilter {

	static public class Config extends SystemFilter.Config {

		final static public String KEY_LOOKBACK = "lookback";
		final static public String KEY_POS_THRESHOLD = "positive.threshold";
		final static public String KEY_NEG_THRESHOLD = "negative.threshold";
		
		public Config() {
		}
		
		public Config(Map<String, String> props) {
			super(props);
		}
				
		@Override
		public String getName() {
			return "reverse-selection";
		}

		public Config setLookback(int lookback) {
			put(KEY_LOOKBACK, lookback);
			return this;
		}
		
		public int getLookback() {
			return getInt(KEY_LOOKBACK);
		}

		public Config setPositiveThreshold(float threshold) {
			put(KEY_POS_THRESHOLD, threshold);
			return this;
		}
		
		public float getPositiveThreshold() {
			return getFloat(KEY_POS_THRESHOLD);
		}

		public Config setNegativeThreshold(float threshold) {
			put(KEY_NEG_THRESHOLD, threshold);
			return this;
		}
		
		public float getNegativeThreshold() {
			return getFloat(KEY_NEG_THRESHOLD);
		}

	}
	
	static public class Builder extends SystemFilter.Builder<ReverseSelectionSystemFilter> {
		@Override
		public ReverseSelectionSystemFilter create(Map<String, String> props) {
			return new ReverseSelectionSystemFilter(new Config(props));
		}

		@Override
		public ReverseSelectionSystemFilter createDefault() {
			return new ReverseSelectionSystemFilter(new Config()
				.setLookback(6)
				.setPositiveThreshold(0.4F)
				.setNegativeThreshold(0.6F)
			);
		}
	}
	
	public ReverseSelectionSystemFilter(Config config) {
		super(config);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void filter(SystemSelection selection, 
			YearAndMonth ym, Map<String, Object> ctx) 
			throws Exception {	
		
		if(selection.getAlltMonthlySelection().isEmpty()) {
			throw new RuntimeException(String.format(
				"%s can not be the first filter", getClass().getSimpleName()));
		}
		
		int lookback = ((Config) config).getLookback();
		float posThreshold = ((Config) config).getPositiveThreshold();
		float negThreshold = ((Config) config).getNegativeThreshold();
		
		String refKey = config.getName() + ".selection";
		SystemSelection ref = (SystemSelection) ctx.get(refKey);
		if(ref == null) {
			ref = new SystemSelection(selection);
			ctx.put(refKey, ref);
		}
		
		SystemEquity systemEquity = (SystemEquity) ctx.get(CTX_KEY_EQUITY_CURVE_RAW);
		if(systemEquity == null) {
			SystemSelection s1 = new SystemSelection(selection.getSystem());
			new AllPassSystemFilter.Builder().createDefault().filterAll(s1);
			systemEquity = new Generator().generateSystemEquityCurve(s1);
			ctx.put(CTX_KEY_EQUITY_CURVE_RAW, systemEquity);
		}
		
		String mgKey = config.getName() + ".monthly.gain";
		Map<String, Float> mgMap = (Map<String, Float>) ctx.get(mgKey);
		if(mgMap == null) {
			mgMap = new HashMap<>();
			ctx.put(mgKey, mgMap);
			// Populate the monthly gain map
			List<String> mList = new ArrayList<>(ref.getAlltMonthlySelection().keySet());
			Collections.sort(mList);
			YearAndMonth ym0 = ref.getMonthlySelection(mList.get(0)).getYearAndMonth();
			YearAndMonth ym1 = YearAndMonth.shift(ym0, 1);
			while(ref.getMonthlySelection(ym1) != null) {
				Date begDate = getSelectionDay(ym0);
				Date endDate = getSelectionDay(ym1);
				float gain = systemEquity.getGain(begDate, endDate);
				mgMap.put(ym1.toString(), gain);
				//System.out.println(String.format("%s,%.2f", ym1, gain));
				ym0 = YearAndMonth.inc(ym0);
				ym1 = YearAndMonth.inc(ym1);
			}
		}
		
		YearAndMonth ym0 = YearAndMonth.shift(ym, -1);
		YearAndMonth ym1 = ym;
		int correctSelectionCount = 0;
		for(int i = 0; i < lookback; i++) {
			Float monthlyGain = mgMap.get(ym1.toString());
			if(monthlyGain == null) {
				return;
			}
			MonthlySelection sel = ref.getMonthlySelection(ym0);
			if(sel == null) {
				return;
			}
			if(monthlyGain >= 0 && sel.getContracts() == 1) {
				++correctSelectionCount;
			} else if(monthlyGain < 0 && sel.getContracts() == 0) {
				++correctSelectionCount;
			} 
			ym0 = YearAndMonth.dec(ym0);
			ym1 = YearAndMonth.dec(ym1);
		}

		float posRatio = ((float) correctSelectionCount) / lookback;
		float negRatio = 1F - posRatio;
		
		MonthlySelection ms = selection.getMonthlySelection(ym);
		if(ms == null) {
			return;
		}
		
		String action = null;
		int oldValue = ms.getContracts();
		int newValue = 0;
		if(posRatio > posThreshold) {
			action = "do nothing";
			newValue = ms.getContracts();
		} else if(negRatio > negThreshold) {
			action = "reverse";
			if(ms.getContracts() == 0) {
				ms.setContracts(1);
				newValue = 1;
			} else {
				ms.setContracts(0);
				newValue = 0;
			}
		} else {
			action = "disable";
			newValue = 0;
			ms.setContracts(0);
		}

		Util.logDebug(String.format(
			" - %s %s %.2f (%.2f, %.2f) %d -> %d %s",
			selection.getSystem().getSystemId(), ym, 
			posRatio, posThreshold, negThreshold, 
			oldValue, newValue, action));
	}
}
