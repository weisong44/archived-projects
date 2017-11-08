package com.weisong.trading.portfolio.model;

import lombok.Data;

@Data
public class YearAndMonth {

	private int year;
	private int month;

	public YearAndMonth(int year, int month) {
		this.year = year;
		this.month = month;
		validate();
	}
	
	public YearAndMonth(YearAndMonth ym) {
		this.year = ym.year;
		this.month = ym.month;
	}
	
	public boolean before(YearAndMonth other) {
		return year < other.year
			|| (year == other.year && month < other.month); 
	}
	
	public boolean after(YearAndMonth other) {
		return year > other.year
			|| (year == other.year && month > other.month); 
	}
	
	@Override
	public String toString() {
		return String.format("%d/%02d", year, month);
	}
	
	private void validate() {
		if(year < 1900 || year > 2020) {
			throw new RuntimeException("Invalid year: " + year);
		}
		if(month < 1 || month > 12) {
			throw new RuntimeException("Invalid month: " + month);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof YearAndMonth) {
			return year == ((YearAndMonth)o).getYear()
				&& month == ((YearAndMonth)o).getMonth();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new Integer(year * 100 + month).hashCode();
	}
	
	static public YearAndMonth inc(YearAndMonth ym) {
		return shift(ym, 1);
	}
	
	static public YearAndMonth dec(YearAndMonth ym) {
		return shift(ym, -1);
	}
	
	static public YearAndMonth shift(YearAndMonth ym, int months) {
		int y = ym.year;
		int m = ym.month;
		if(months > 0) {
			for(int i = 0; i < months; i++) {
				++m;
				if(m > 12) {
					++y;
					m = 1;
				}
			}
		} else if(months < 0) {
			for(int i = 0; i > months; i--) {
				--m;
				if(m < 1) {
					--y;
					m = 12;
				}
			}
		}
		
		return new YearAndMonth(y, m);
	}
}
