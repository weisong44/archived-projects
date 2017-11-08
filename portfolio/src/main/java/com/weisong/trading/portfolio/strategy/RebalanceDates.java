package com.weisong.trading.portfolio.strategy;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.util.DateUtil;
import com.weisong.trading.portfolio.util.Util;

import lombok.Getter;

public class RebalanceDates {
	
	public enum Type {
		END_OF_MONTH, 
		MONTHLY_FIRST_WEEKEND,
		MONTHLY_LAST_WEEKEND;
	}

	@Getter private Type type;
	@Getter private Date beg;
	@Getter private Date end;
	private Set<String> dates = new HashSet<>();
	
	public RebalanceDates(Type type) {
		this(type, new GregorianCalendar(2008, 1, 1).getTime());
	}
	
	public RebalanceDates(Type type, Date beg) {
		this(type, beg, new Date());
	}
	
	public RebalanceDates(Type type, Date beg, Date end) {
		this.type = type;
		this.beg = beg;
		this.end = end;
		Date d = beg;
		while(d.before(end)) {
			boolean isRebalanceDay = false;
			switch(type) {
			case END_OF_MONTH:
				isRebalanceDay = DateUtil.isEndOfMonth(d);
				break;
			case MONTHLY_FIRST_WEEKEND:
				isRebalanceDay = DateUtil.isFirstWeekendOfMonth(d);
				break;
			case MONTHLY_LAST_WEEKEND:
				isRebalanceDay = DateUtil.isLastWeekendOfMonth(d);
				break;
			default:
				throw new RuntimeException("Rebbalance type not supported: " + type);
			}
			
			if(isRebalanceDay) {
				Util.logDebug("Rebalancing day: " + Constants.df1.format(d));
				dates.add(Constants.df3.format(d));
			}

			d = new Date(d.getTime() + DateUtil.msInADay);
		}
	}
	
	public boolean isRebalanceDay(Date date) {
		return dates.contains(Constants.df3.format(date));
	}
	
	public Iterator<String> newIterator() {
		return new TreeSet<String>(dates).iterator();
	}
}
