package com.weisong.trading.portfolio.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.model.DailyEquity;
import com.weisong.trading.portfolio.model.DailyStats;
import com.weisong.trading.portfolio.model.MonthlySelection;
import com.weisong.trading.portfolio.model.Portfolio;
import com.weisong.trading.portfolio.model.Portfolio.OptimizationResult;
import com.weisong.trading.portfolio.model.PortfolioEquity;
import com.weisong.trading.portfolio.model.SystemEquity;
import com.weisong.trading.portfolio.model.SystemSelection;
import com.weisong.trading.portfolio.model.SystemStats;
import com.weisong.trading.portfolio.util.Util;

public class Generator {

	public SystemEquity generateSystemEquityCurve(SystemSelection systemSelection) 
			throws Exception {
		return generateSystemEquityCurve(systemSelection, null);
	}
	
	public SystemEquity generateSystemEquityCurve(SystemSelection systemSelection, Date endDate) 
			throws Exception {
		
		SystemStats system = systemSelection.getSystem();
		
		Util.logInfo(String.format("Generating equity curve for system %s", system.getSystemId()));

		Map<String, MonthlySelection> map = systemSelection.getAlltMonthlySelection();
		List<MonthlySelection> selections = new ArrayList<>(map.values());
		Collections.sort(selections, new Comparator<MonthlySelection>() {
			@Override
			public int compare(MonthlySelection o1, MonthlySelection o2) {
				if(o1.getSelectionDate().before(o2.getSelectionDate())) {
					return -1;
				} else if(o1.getSelectionDate().after(o2.getSelectionDate())) {
					return 1;
				}
				return 0;
			}
		});
		
		List<DailyEquity> list = new ArrayList<>();
		
		float equity = 0;
		float drawdown = 0;
		float equityHigh = Integer.MIN_VALUE;
		Iterator<MonthlySelection> i = selections.iterator();
		MonthlySelection s1 = i.next();
		MonthlySelection s2 = i.next();
		
		for(DailyStats stats : system.getDailyStats()) {
			
			if(endDate != null && endDate.before(stats.getDate())) {
				break;
			}
			
			Date date = stats.getDate();
			if(date.after(s1.getSelectionDate()) == false) {
				continue;
			} else if(i.hasNext() && date.after(s2.getSelectionDate())) {
				s1 = s2;
				s2 = i.next();
			}

			DailyEquity dailyEquity = null;
			if(s1.getContracts() > 0) {
				equity += stats.getDailyEquityChange() * s1.getContracts();
				equityHigh = Math.max(equity, equityHigh);
				drawdown = equity - equityHigh;
				dailyEquity = new DailyEquity(date, equity,
						stats.getDailyEquityChange(), drawdown); 
			} else {
				dailyEquity = new DailyEquity(date, equity, 0, drawdown); 
			}
			
			list.add(dailyEquity);

			Util.logDebug(String.format(" - %s %s %s %6.0f %5.0f %5.0f",
				system.getSystemId(), s1.getYearAndMonth(), Constants.df1.format(date), 
				dailyEquity.getEquityChange(), dailyEquity.getEquity(), dailyEquity.getDrawdown()));
			
		}
		
		return new SystemEquity(system, list);
	}

	public PortfolioEquity generatePortfolioEquityCurve(OptimizationResult result) 
			throws Exception {
		
		Portfolio portfolio = result.getPortfolio();
		
		Util.logInfo(String.format("Generating equity curve for portfolio %s", portfolio.getName()));
		
		Set<Date> allDates = new HashSet<>();
		List<SystemEquity> systemEquities = new ArrayList<>();
		for(SystemSelection selection : result.getSelections()) {
			SystemEquity se = generateSystemEquityCurve(selection);
			se.getDailyEquities().forEach(de -> allDates.add(de.getDate()));
			systemEquities.add(se);
		}

		List<DailyEquity> allDailyEquities = new ArrayList<>();
		systemEquities.forEach(se -> {
			List<DailyEquity> equities = normalize(se.getDailyEquities(), allDates);
			se.setDailyEquities(equities);
			allDailyEquities.addAll(equities);
		});
		
		DailyEquity.sortByDate(allDailyEquities);
		
		List<DailyEquity> equities = new ArrayList<>();
		DailyEquity cur = null;
		for(DailyEquity e : allDailyEquities) {
			if(cur == null || e.getDate().after(cur.getDate())) {
				if(cur != null) {
					Util.logDebug(String.format(" - %s %s %5.0f %6.0f %6.0f",
						portfolio.getName(), Constants.df1.format(cur.getDate()),
						cur.getEquityChange(), cur.getEquity(), cur.getDrawdown()));
				}
				cur = new DailyEquity(e);
				equities.add(cur);
			} else if(cur.getDate().equals(e.getDate())) {
				cur.setEquity(cur.getEquity() + e.getEquity());
				cur.setEquityChange(cur.getEquityChange() + e.getEquityChange());
				cur.setDrawdown(cur.getDrawdown() + e.getDrawdown());
			} else {
				throw new RuntimeException("Shouldn't reach here!");
			}
		}
		
		float maxEquity = 0;
		for(DailyEquity e : equities){
			maxEquity = Math.max(maxEquity, e.getEquity());
			e.setDrawdown(e.getEquity() - maxEquity);
		}
		
		return new PortfolioEquity(portfolio, equities, systemEquities);

	}
	
	private List<DailyEquity> normalize(List<DailyEquity> equities, Collection<Date> allDates) {

		List<Date> allDatesList = new ArrayList<>(allDates);
		Collections.sort(allDatesList);
		
		Map<Date, DailyEquity> map = new HashMap<>();
		equities.forEach(de -> map.put(de.getDate(), de));
		
		LinkedList<DailyEquity> result = new LinkedList<>();
		allDatesList.forEach(date -> {
			if(map.containsKey(date)) {
				result.add(map.remove(date));
			} else if(result.isEmpty()) {
				result.add(new DailyEquity(date, 0, 0, 0));
			} else {
				DailyEquity last = result.getLast();
				result.add(new DailyEquity(date, last.getEquity(), 0, last.getDrawdown()));
			}
		});

		if(map.isEmpty() == false) {
			throw new RuntimeException("Something is wrong, the map must be empty!");
		}
		
		return result;
	}
	
}
