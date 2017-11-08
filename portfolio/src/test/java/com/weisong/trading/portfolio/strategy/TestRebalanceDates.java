package com.weisong.trading.portfolio.strategy;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import com.weisong.trading.portfolio.Constants;

public class TestRebalanceDates {
	
	@Test
	public void testEndOfMonth() throws ParseException {
		RebalanceDates rd = new RebalanceDates(RebalanceDates.Type.END_OF_MONTH);
		Assert.assertTrue(rd.isRebalanceDay(Constants.df1.parse("09/30/2017")));
		Assert.assertFalse(rd.isRebalanceDay(Constants.df1.parse("01/01/2017")));
	}

	@Test
	public void testFirstWeekendOfMonth() throws ParseException {
		RebalanceDates rd = new RebalanceDates(RebalanceDates.Type.MONTHLY_FIRST_WEEKEND);
		Assert.assertTrue(rd.isRebalanceDay(Constants.df1.parse("05/06/2017")));
		Assert.assertFalse(rd.isRebalanceDay(Constants.df1.parse("05/07/2017")));
	}

	@Test
	public void testLastWeekendOfMonth() throws ParseException {
		RebalanceDates rd = new RebalanceDates(RebalanceDates.Type.MONTHLY_LAST_WEEKEND);
		Assert.assertTrue(rd.isRebalanceDay(Constants.df1.parse("12/31/2016")));
		Assert.assertFalse(rd.isRebalanceDay(Constants.df1.parse("12/30/2016")));
	}
}
