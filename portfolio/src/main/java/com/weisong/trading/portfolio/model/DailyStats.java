package com.weisong.trading.portfolio.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.util.Util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class DailyStats {

	private Date date;
	private float dailyEquityChange;
	
	static public void sortByDate(List<DailyStats> c) {
		Collections.sort(c, new Comparator<DailyStats>() {
			@Override
			public int compare(DailyStats o1, DailyStats o2) {
				if(o1.date.before(o2.date)) {
					return -1;
				} else if(o1.date.after(o2.date)) {
					return 1;
				}
				return 0;
			}
		});
	}

	static public ArrayList<DailyStats> fromFile(String fname) throws Exception {
		return fromFile(fname, null, null);
	}
	
	static public ArrayList<DailyStats> fromFile(String fname, Date fromDate, Date toDate) throws Exception {

		String strFromDate = fromDate != null ? Constants.df1.format(fromDate) : "N/A";
		String strToDate = toDate != null ? Constants.df1.format(toDate) : "N/A";
		Util.logInfo(String.format("Reading %s from %s to %s", fname, strFromDate, strToDate));
		
		ArrayList<DailyStats> list = new ArrayList<>();
		FileInputStream fstream = new FileInputStream(fname);
		try(BufferedReader br = new BufferedReader(new InputStreamReader(fstream))) {
			Map<String, Integer> fields = new HashMap<>();
			Constants.COLUMN_SET columnSet = null;
			String line;
			String[] tokens; 
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(line.length() <= 0) {
					continue;
				}
				
				if(Character.isDigit(line.charAt(0)) == false) {
					tokens = line.split(",");
					for(int i = 0; i < tokens.length; i++) {
						fields.put(tokens[i].toLowerCase(), i);
					}
					columnSet = Constants.discoverColumnSet(fields);
					continue;
				}
				
				tokens = line.split(",");
				Date date = Constants.df1.parse(tokens[fields.get(columnSet.DAILY_STATS_DATE)]);
				if((fromDate != null && date.before(fromDate)) || (toDate != null && date.after(toDate))) {
					Util.logDebug(" - skipping " + Constants.df1.format(date));
					continue;
				}
				
				float dailyEquityChange = Float.valueOf(tokens[fields.get(columnSet.DAILY_STATS_EQUITY)]);
				
				DailyStats stats = new DailyStats(date, dailyEquityChange);
				Util.logDebug(" - " + stats);
				list.add(stats);
			}
		}
		
		sortByDate(list);
		
		return list;
	}

	/**
	 * Get the index of the given date. If it doesn't exist, return
	 * the index with closest date before the input date.
	 */
	static public int getIndex(ArrayList<DailyStats> list, Date date) {
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getDate().after(date)) {
				return i - 1;
			}
		}
		return -1;
	}
	
	/**
	 * Calculate gain for element with index, where beg < index <= end
	 */
	static public float getGain(ArrayList<DailyStats> list, int beg, int end) {
		float gain = 0F;
		for(int i = end; i > beg; i--) {
			gain += list.get(i).dailyEquityChange;
		}
		return gain;
	}
}
