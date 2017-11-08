package com.weisong.trading.portfolio.util;

import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.model.YearAndMonth;

public class TestDateUtil {
	
	private Date d0;
	private Date d1;
	private Date d2;
	private Date d3;
	private Date d4;
	private Date d5;
	private Date d6;
	
	@Before
	public void prepare() throws ParseException {
		d0 = Constants.df1.parse("02/29/2012");
		d1 = Constants.df1.parse("03/01/2012");
		d2 = Constants.df1.parse("03/02/2012");

		d3 = Constants.df1.parse("02/26/2012");
		d4 = Constants.df1.parse("03/31/2012");
		d5 = Constants.df1.parse("04/29/2012");
		d6 = Constants.df1.parse("06/02/2012");
	}
	
	@Test
	public void testGetYear() {
		Assert.assertEquals(2012, DateUtil.getYear(d1));
	}
	
	@Test
	public void testGetDay() {
		Assert.assertEquals(d0, DateUtil.getDay(d1, -1));
		Assert.assertEquals(d1, DateUtil.getDay(d0, +1));
		Assert.assertEquals(d1, DateUtil.getDay(d2, -1));
	}
	
	@Test
	public void testEndOfMonth() throws Exception {
		YearAndMonth ym = new YearAndMonth(2012, 2);
		Assert.assertEquals(d0, DateUtil.getEndOfMonth(ym));
	}
	
	@Test
	public void testGetEomNoTradingDay() throws Exception {
		YearAndMonth ym = new YearAndMonth(2012, 2);
		Assert.assertEquals(d3, DateUtil.getEomAndWeekend(ym));
		ym = YearAndMonth.inc(ym);
		Assert.assertEquals(d4, DateUtil.getEomAndWeekend(ym));
		ym = YearAndMonth.inc(ym);
		Assert.assertEquals(d5, DateUtil.getEomAndWeekend(ym));
		ym = YearAndMonth.inc(ym);
		Assert.assertEquals(d6, DateUtil.getEomAndWeekend(ym));
	}
	
	@Test
	public void testIsEndOfMonth() throws Exception {
		Assert.assertTrue(DateUtil.isEndOfMonth(d0));
		Assert.assertTrue(DateUtil.isEndOfMonth(d4));

		Assert.assertFalse(DateUtil.isEndOfMonth(d1));
		Assert.assertFalse(DateUtil.isEndOfMonth(d2));
	}
	
}
