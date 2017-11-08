package com.weisong.trading.portfolio.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class MonthlySelection {

	@Getter YearAndMonth yearAndMonth;
	@Getter Date selectionDate;
	@Getter @Setter int contracts;

	public MonthlySelection(YearAndMonth ym, Date selectionDate, int contracts) {
		this.yearAndMonth = new YearAndMonth(ym);
		this.selectionDate = selectionDate;
		this.contracts = contracts;
	}
	
	public MonthlySelection(MonthlySelection ms) {
		this.yearAndMonth = new YearAndMonth(ms.yearAndMonth);
		this.selectionDate = ms.selectionDate;
		this.contracts = ms.contracts;
	}
	
	public String getId() {
		return getId(yearAndMonth);
	}
	
	static public String getId(YearAndMonth ym) {
		return String.format("%d-%02d", ym.getYear(), ym.getMonth());
	}
	
	static public void sortBySelectionDate(List<MonthlySelection> list) {
		Collections.sort(list, new Comparator<MonthlySelection>() {
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
	}
}
