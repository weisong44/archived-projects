package com.weisong.trading.portfolio;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.weisong.trading.portfolio.util.LogLevel;

public class Constants {
	
	final static public int DAYS_IN_A_MONTH = 21;
	
	final static public SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
	final static public SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	final static public SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd");
	
	static public int logLevel = LogLevel.WARNING;
	
	static public class COLUMN_SET {
		public String DAILY_STATS_DATE;
		public String DAILY_STATS_EQUITY;
		public COLUMN_SET(String DAILY_STATS_DATE, String DAILY_STATS_EQUITY) {
			this.DAILY_STATS_DATE = DAILY_STATS_DATE;
			this.DAILY_STATS_EQUITY = DAILY_STATS_EQUITY;
		}
	}
	
	static public COLUMN_SET discoverColumnSet(Map<String, Integer> columns) {
		COLUMN_SET result = null;
		if((result = tryColumnSet(columns, "date", "equity")) != null) {
			return result;
		} else if((result = tryColumnSet(columns, "date", "daily-mark-to-market")) != null) {
			return result;
		} else {
			throw new RuntimeException("Failed to discover column set for input data");
		}
	}

	static private COLUMN_SET tryColumnSet(Map<String, Integer> columns, 
			String DAILY_STATS_DATE, String DAILY_STATS_EQUITY) {
		if(columns.containsKey(DAILY_STATS_DATE) && columns.containsKey(DAILY_STATS_EQUITY)) {
			return new COLUMN_SET(DAILY_STATS_DATE, DAILY_STATS_EQUITY);
		}
		return null;
	}
}
