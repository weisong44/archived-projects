package com.weisong.trading.portfolio.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@ToString
public class SystemStats {
	
	@Getter private String systemId;

	private boolean dailyStatsModified;
	private ArrayList<DailyStats> dailyStats = new ArrayList<>();
	private Map<Date, DailyStats> dailyStatsMap = new HashMap<>();
	
	public SystemStats(String systemId, ArrayList<DailyStats> dailyStats) {
		this.systemId = systemId;
		
		this.dailyStatsModified = false;
		this.dailyStats = dailyStats;
		for(DailyStats s : dailyStats) {
			dailyStatsMap.put(s.getDate(), s);
		}
	}
	
	public boolean hasDailyStats(Date date) {
		return dailyStatsMap.containsKey(date);
	}
	
	public ArrayList<DailyStats> getDailyStats() {
		if(dailyStatsModified) {
			dailyStats.clear();
			for(DailyStats s : dailyStatsMap.values()) {
				dailyStats.add(s);
			}
			DailyStats.sortByDate(dailyStats);
			dailyStatsModified = false;
		}
		return dailyStats;
	}
	
	public SystemEquity getEquity() {
		List<DailyEquity> list = new LinkedList<>();
		float equity = 0;
		float maxEquity = 0;
		float drawdown = 0;
		for(DailyStats s : getDailyStats()) {
			equity += s.getDailyEquityChange();
			maxEquity = Math.max(maxEquity, equity);
			drawdown = equity - maxEquity;
			list.add(new DailyEquity(s.getDate(), equity, s.getDailyEquityChange(), drawdown));
		}
		return new SystemEquity(this, list);
	}
	
	public Date getFirstDate() {
		return getDailyStats().get(0).getDate();
	}
	
	public Date getLastDate() {
		return getDailyStats().get(getDailyStats().size() - 1).getDate();
	}
	
	public int getCount() {
		return getDailyStats().size();
	}
	
	public float getWorstDayGain() {
		float min = Float.MAX_VALUE;
		for(DailyStats s : getDailyStats()) {
			min = Math.min(min, s.getDailyEquityChange());
		}
		return min;
	}
	
	static public String getSystemId(String fname) {
		return new File(fname).getName()
			.replace("-TS.csv", "")
			.replace("-MC.csv", "");
	}
	
	static public SystemStats fromFile(String fname) throws Exception {
		return fromFile(fname, null, null);
	}

	static public SystemStats fromFile(String fname, Date fromDate, Date toDate) throws Exception {
		ArrayList<DailyStats> stats = DailyStats.fromFile(fname, fromDate, toDate);
		return new SystemStats(getSystemId(fname), stats);
	}

	static public Date getFirstDate(List<SystemStats> list) {
		Date first = null;
		for(SystemStats s : list) {
			Date d = s.getDailyStats().get(0).getDate();
			if(first == null || first.after(d)) {
				first = d;
			}
		}
		return first;
	}
}
