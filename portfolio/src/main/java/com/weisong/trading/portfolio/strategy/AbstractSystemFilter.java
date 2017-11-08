package com.weisong.trading.portfolio.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.weisong.trading.portfolio.model.DailyStats;
import com.weisong.trading.portfolio.model.SystemSelection;
import com.weisong.trading.portfolio.model.SystemStats;
import com.weisong.trading.portfolio.model.YearAndMonth;
import com.weisong.trading.portfolio.util.DateUtil;
import com.weisong.trading.portfolio.util.Util;

import lombok.Getter;

abstract public class AbstractSystemFilter implements SystemFilter {

	final static protected String CTX_KEY_EQUITY_CURVE_RAW = "equity.curve.raw";
	
	@Getter protected Config config;
	
	abstract protected void filter(SystemSelection selection, 
		YearAndMonth ym, Map<String, Object> ctx) 
		throws Exception;
	
	public AbstractSystemFilter(Config config) {
		this.config = config;
	}

	@Override
	public void filterAll(SystemSelection selection) throws Exception {
		
		SystemStats system = selection.getSystem();
		
		ArrayList<DailyStats> dailyStats = system.getDailyStats();
		DailyStats.sortByDate(dailyStats);

		Date first = dailyStats.get(0).getDate();
		Date last = dailyStats.get(dailyStats.size() - 1).getDate();
		
		YearAndMonth firstYm = new YearAndMonth(
			DateUtil.getYear(first), DateUtil.getMonth(first));
		
		YearAndMonth lastYm = new YearAndMonth(
			DateUtil.getYear(last), DateUtil.getMonth(last));
		
		Util.logInfo(String.format("%s filtering %s %s - %s",
			config.getName(), system.getSystemId(), firstYm, lastYm));
		
		Map<String, Object> ctx = new HashMap<>();
		for(YearAndMonth ym = firstYm; !ym.after(lastYm); ym = YearAndMonth.inc(ym)) {
			filter(selection, ym, ctx);
		}
	}
	
	protected Date getSelectionDay(YearAndMonth ym) throws Exception {
		switch(config.getRebalanceDateType()) {
		case END_OF_MONTH_NO_TRADING_DAY:
			return DateUtil.getEomAndWeekend(ym);
		case END_OF_MONTH:
			return DateUtil.getEndOfMonth(ym);
		default:
			throw new RuntimeException("Unknown selection date type: " + config.getRebalanceDateType());
		}
	}
}
