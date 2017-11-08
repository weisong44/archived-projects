package com.weisong.trading.portfolio.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class DailyEquity {
	
	private Date date;
	private float equity;
	private float equityChange;
	private float drawdown;
	
	public DailyEquity(DailyEquity other) {
		this.date = other.date;
		this.equity = other.equity;
		this.equityChange = other.equityChange;
		this.drawdown = other.drawdown;
	}
	
	static public void sortByDate(List<DailyEquity> c) {
		Collections.sort(c, new Comparator<DailyEquity>() {
			@Override
			public int compare(DailyEquity o1, DailyEquity o2) {
				if(o1.date.before(o2.date)) {
					return -1;
				} else if(o1.date.after(o2.date)) {
					return 1;
				}
				return 0;
			}
		});
	}
	
}
