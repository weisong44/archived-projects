package com.weisong.trading.portfolio.model;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class EquityCurve {

	@Getter @Setter protected List<DailyEquity> dailyEquities;
	
	public void addDailyEquity(DailyEquity e) {
		dailyEquities.add(e);
	}
	
	public float getMaxDd() {
		return getMaxDd(null, null);
	}

	public float getMaxDd(Date beg, Date end) {
		Float initDd = null;
		Float maxDd = null;
		for(DailyEquity e : dailyEquities) {
			if(beg != null && e.getDate().before(beg)) {
				continue;
			}
			if(end != null && e.getDate().after(end)) {
				break;
			}
			if(initDd == null) {
				initDd = e.getDrawdown();
			} else if(e.getDrawdown() >= 0) {
				initDd = 0F;
			}
			
			float realDd = e.getDrawdown() <= initDd ? e.getDrawdown() - initDd : 0;
			maxDd = maxDd == null ? realDd : Math.min(maxDd, realDd);
//			System.out.println(String.format(
//					"%s,%.0f,%.0f", 
//					Constants.df1.format(e.getDate()), realDd, maxDd));
		}
		return maxDd;
	}

	public float getGain() {
		return getGain(null, null);
	}
	
	public float getGain(Date beg, Date end) {
		
		DailyEquity begEquity = null;
		DailyEquity endEquity = null;
		
		if(beg == null) {
			begEquity = dailyEquities.get(0);
		}
		
		if(end == null) {
			endEquity = dailyEquities.get(dailyEquities.size() - 1);
		}
		
		if(begEquity == null || endEquity == null) {
			for(DailyEquity e : dailyEquities) {
				if(begEquity == null && e.getDate().after(beg)) {
					begEquity = e;;
				}
				if(endEquity == null && e.getDate().after(end)) {
					endEquity = e;
				}
			}
		}
		
		if(endEquity == null) {
			endEquity = dailyEquities.get(dailyEquities.size() - 1);
		}
		
		return endEquity.getEquity() - begEquity.getEquity();
	}
	
	public String toCsv() {
		return toString().replace("\t", ",");
	}

}
