package com.weisong.trading.portfolio.util;

import java.util.Calendar;
import java.util.Date;

import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.model.YearAndMonth;

public class DateUtil {
	
	final static public long msInADay = 24 * 3600 * 1000; 
	
	static public Date getEomAndWeekend(YearAndMonth ym) throws Exception {
		Date date = getEndOfMonth(ym);
		if(isWeekend(date)) {
			return date;
		}
		Date d1 = date;
		Date d2 = date;
		while(true) {
			d1 = getDay(d1, -1);
			if(isWeekend(d1)) {
				return d1;
			}
			d2 = getDay(d2, 1);
			if(isWeekend(d2)) {
				return d2;
			}
		}
	}

	static public Date getEndOfMonth(YearAndMonth ym) throws Exception {
		String dateStr = String.format("%02d/01/%d", ym.getMonth(), ym.getYear());
		Date first = Constants.df1.parse(dateStr);
		Calendar c = Calendar.getInstance();
		c.setTime(first);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return c.getTime();
	}

	static public Date getBeginOfMonth(YearAndMonth ym) throws Exception {
		String dateStr = String.format("%02d/01/%d", ym.getYear(), ym.getMonth());
		Date first = Constants.df1.parse(dateStr);
		Calendar c = Calendar.getInstance();
		c.setTime(first);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	static public int getYear(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	
	static public int getMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH) + 1;
	}
	
	static public Date getDay(Date date, int offset) {
		return new Date(date.getTime() + offset * msInADay);
	}

	static public Date getMonth(Date date, int offset) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, offset);
		return cal.getTime();
	}

	static public boolean isEndOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return date.equals(c.getTime());
	}

	static public boolean isWeekend(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		switch(c.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SATURDAY:
		case Calendar.SUNDAY:
			return true;
		default:
			return false;
		}
	}
	
	static public boolean isFirstWeekendOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		switch(c.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SATURDAY:
			return c.get(Calendar.DAY_OF_MONTH) < 7;
		case Calendar.SUNDAY:
			return c.get(Calendar.DAY_OF_MONTH) == 1;
		default:
			return false;
		}
	}
	
	static public boolean isLastWeekendOfMonth(Date date) {
		
		if("01/24/2016".equals(Constants.df1.format(date))) {
			Util.logDebug("");
		}
		

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date);
		c2.set(Calendar.DAY_OF_MONTH, 1);
		c2.add(Calendar.MONTH, 1);
		c2.add(Calendar.DAY_OF_MONTH, -1);
		int lastDayOfMonth = c2.get(Calendar.DAY_OF_MONTH);

		switch(c.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SATURDAY:
			return c.get(Calendar.DAY_OF_MONTH) == lastDayOfMonth;
		case Calendar.SUNDAY:
			return c.get(Calendar.DAY_OF_MONTH) + 6 > lastDayOfMonth;
		default:
			return false;
		}
	}
}
