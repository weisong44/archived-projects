package com.weisong.trading.portfolio.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class SystemSelection {
	
	@Getter private SystemStats system;
	private Map<String, MonthlySelection> selectionMap = new HashMap<>();
	
	public SystemSelection(SystemStats system) {
		this.system = system;
	}
	
	public SystemSelection(SystemSelection selection) {
		this.system = selection.system;
		selection.selectionMap.forEach(
			(ym, ms) -> selectionMap.put(ym, new MonthlySelection(ms)));
	}

	public void mergeFrom(SystemSelection secondSelection) throws Exception {
		Map<String, MonthlySelection> target = new HashMap<>();
		for(String k : selectionMap.keySet()) {
			if(secondSelection.selectionMap.containsKey(k)) {
				MonthlySelection first = selectionMap.get(k);
				MonthlySelection second = secondSelection.selectionMap.get(k);
				if(first.getSelectionDate().equals(second.getSelectionDate()) == false) {
					throw new Exception("Monthly selection dates are not identical");
				}
				int contracts = first.contracts > 0 ?
						second.contracts > 0 ? 1 : 0
					  :	0;
				MonthlySelection third = new MonthlySelection(first.getYearAndMonth(), 
					first.selectionDate, contracts);
				target.put(k, third);
			}
		}
		selectionMap = target;
	}
	
	public MonthlySelection getMonthlySelection(YearAndMonth ym) {
		return selectionMap.get(MonthlySelection.getId(ym));
	}
	
	public MonthlySelection getMonthlySelection(String id) {
		return selectionMap.get(id);
	}
	
	public Map<String, MonthlySelection> getAlltMonthlySelection() {
		return selectionMap;
	}
	
	public void addMonthlySelection(YearAndMonth ym, MonthlySelection selection) {
		selectionMap.put(MonthlySelection.getId(ym), selection);
	}
	
}
