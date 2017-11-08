package com.weisong.trading.portfolio.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.weisong.trading.portfolio.Constants;

import lombok.Getter;

@Getter
public class PortfolioEquity extends EquityCurve {

	private Portfolio portfolio;
	private List<SystemEquity> systemEquities;
	
	public PortfolioEquity(Portfolio portfolio, List<DailyEquity> portfolioDailyEquities,
			List<SystemEquity> systemEquities) {
		super(portfolioDailyEquities);
		this.portfolio = portfolio;
		this.systemEquities = systemEquities;
	}
	
	@Override
	public String toString() {
		
		Set<String> idSet = new HashSet<>();
		Map<Date, Map<String, DailyEquity>> dMap = new HashMap<>();

		String pId = "__Portfolio__";
		processDailyEquities(pId, dailyEquities, dMap, idSet);
		
		for(SystemEquity se : systemEquities) {
			processDailyEquities(se.getSystem().getSystemId(), 
				se.getDailyEquities(), dMap, idSet);
		}
		
		idSet.remove(pId);
		List<String> idList = new LinkedList<>(idSet);
		Collections.sort(idList);

		List<Date> dateList = new LinkedList<>(dMap.keySet());
		Collections.sort(dateList);
		
		StringBuilder sb = new StringBuilder("Date");

		// Create the header
		String template = "\t{id}-Equity\t{id}-DD"; 
		sb.append(template.replace("{id}-", ""));
		for(String id : idList) {
			sb.append(template.replace("{id}", id));
		}
		sb.append("\n");

		for(Date date : dateList) {
			sb.append(Constants.df1.format(date));
			Map<String, DailyEquity> deMap = dMap.get(date);
			DailyEquity de = deMap.get(pId);
			sb.append("\t").append(getDailyEquityAsString(de));
			for(String id : idList) {
				de = deMap.get(id);
				sb.append("\t").append(getDailyEquityAsString(de));
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private String getDailyEquityAsString(DailyEquity de) {
		if(de != null) {
			return String.format("%.2f\t%.2f", 
				de.getEquity(), de.getDrawdown());
		} else {
			return "\t\t";
		}
	}
	
	private void processDailyEquities(String id, List<DailyEquity> dailyEquities, 
			Map<Date, Map<String, DailyEquity>> dMap, Set<String> idSet) {
		dailyEquities.forEach(de -> {
			Map<String, DailyEquity> eMap = dMap.get(de.getDate());
			if(eMap == null) {
				eMap = new HashMap<>();
				dMap.put(de.getDate(), eMap);
			}
			eMap.put(id, de);
			idSet.add(id);
		});
	}

}
