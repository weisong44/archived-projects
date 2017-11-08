package com.weisong.trading.portfolio.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.weisong.trading.portfolio.model.DailyStats;
import com.weisong.trading.portfolio.model.MonthlySelection;
import com.weisong.trading.portfolio.model.SystemSelection;
import com.weisong.trading.portfolio.model.YearAndMonth;
import com.weisong.trading.portfolio.util.Util;

public class SFIncubationSystemFilter extends AbstractSystemFilter {

	static public int DAYS = 21;
	
	static public class Config extends SystemFilter.Config {
		
		public Config() {
		}
		
		public Config(Map<String, String> props) {
			super(props);
		}
				
		@Override
		public String getName() {
			return "sf-incubation";
		}
	}
	
	static public class Builder extends SystemFilter.Builder<SFIncubationSystemFilter> {
		@Override
		public SFIncubationSystemFilter create(Map<String, String> props) {
			return new SFIncubationSystemFilter(new Config(props));
		}

		@Override
		public SFIncubationSystemFilter createDefault() {
			return new SFIncubationSystemFilter(new Config());
		}
	}
	
	public SFIncubationSystemFilter(Config config) {
		super(config);
	}

	@Override
	protected void filter(SystemSelection selection, 
			YearAndMonth ym, Map<String, Object> ctx) 
			throws Exception {
		
		if(selection.getMonthlySelection(ym) != null) {
			throw new RuntimeException(String.format(
				"%s should be the first filter", getClass().getSimpleName()));
		}
		
		ArrayList<DailyStats> dailyStats = selection.getSystem().getDailyStats();
		
		float avgMonthlyGain = 0;
		for(DailyStats s : dailyStats) {
			avgMonthlyGain += 21 * s.getDailyEquityChange() / dailyStats.size();
		}
		
		Date date = getSelectionDay(ym);
		int index = getIndex(dailyStats, date);
		int iIndex = index - DAYS * 6;
		float iGain = DailyStats.getGain(dailyStats, iIndex, index) / 6;
		
		MonthlySelection result = new MonthlySelection(ym, date, 0);
		if(iGain > avgMonthlyGain) {
			result.setContracts(1);
		}
		
		selection.addMonthlySelection(ym, result);
		
		Util.logDebug(String.format(" - %s %s %6.0f %6.0f   %d",
				selection.getSystem().getSystemId(),
				ym, iGain, avgMonthlyGain, result.getContracts()));
	}

	private int getIndex(ArrayList<DailyStats> list, Date date) {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getDate().after(date)) {
				return i;
			}
		}
		return -1;
	}
}
