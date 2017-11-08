package com.weisong.trading.portfolio.model;

import java.util.List;

import lombok.Getter;

import com.weisong.trading.portfolio.Constants;

@Getter
public class SystemEquity extends EquityCurve {

	private SystemStats system;
	
	public SystemEquity(SystemStats system, List<DailyEquity> dailyEquities) {
		super(dailyEquities);
		this.system = system;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Date\tEquity\tChange\tDrawdown").append("\n");
		for(DailyEquity e : dailyEquities) {
			sb.append(String.format("%s\t%.2f\t%.2f\t%.2f\n", 
				Constants.df1.format(e.getDate()), 
				e.getEquity(), e.getEquityChange(), e.getDrawdown()));
		}
		return sb.toString();
	}

}
