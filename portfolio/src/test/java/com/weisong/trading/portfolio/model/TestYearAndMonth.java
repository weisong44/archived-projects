package com.weisong.trading.portfolio.model;

import org.junit.Assert;
import org.junit.Test;

public class TestYearAndMonth {

	@Test(expected=RuntimeException.class)
	public void testBadYear() {
		new YearAndMonth(1000, 11);
	}

	@Test(expected=RuntimeException.class)
	public void testBadMonth() {
		new YearAndMonth(2000, 13);
	}

	@Test
	public void testIncAndDec() {
		YearAndMonth ym = new YearAndMonth(2000, 12);
		ym = YearAndMonth.inc(ym);
		Assert.assertEquals(2001, ym.getYear());
		Assert.assertEquals(1, ym.getMonth());
		ym = YearAndMonth.inc(ym);
		Assert.assertEquals(2001, ym.getYear());
		Assert.assertEquals(2, ym.getMonth());
		
		ym = new YearAndMonth(2000, 1);
		ym = YearAndMonth.dec(ym);
		Assert.assertEquals(1999, ym.getYear());
		Assert.assertEquals(12, ym.getMonth());
		ym = YearAndMonth.dec(ym);
		Assert.assertEquals(1999, ym.getYear());
		Assert.assertEquals(11, ym.getMonth());
	}

	@Test
	public void testBeforeAndAfter() {
		YearAndMonth ym1 = new YearAndMonth(1999, 12);
		YearAndMonth ym2 = new YearAndMonth(2000, 11);
		Assert.assertTrue(ym1.before(ym2));
		
		ym1 = new YearAndMonth(1999, 10);
		ym2 = new YearAndMonth(1999, 11);
		Assert.assertTrue(ym1.before(ym2));		
		
		ym1 = new YearAndMonth(2000, 10);
		ym2 = new YearAndMonth(1999, 11);
		Assert.assertTrue(ym1.after(ym2));		
		
		ym1 = new YearAndMonth(1999, 12);
		ym2 = new YearAndMonth(1999, 11);
		Assert.assertTrue(ym1.after(ym2));		
	}
	
	public void testShiftByMonth() {
		YearAndMonth ym  = new YearAndMonth(1999, 12);
		Assert.assertEquals(new YearAndMonth(2000, 11), YearAndMonth.shift(ym, +11));
		Assert.assertEquals(new YearAndMonth(2001, 12), YearAndMonth.shift(ym, +24));
		Assert.assertEquals(new YearAndMonth(1997, 12), YearAndMonth.shift(ym, -24));
		Assert.assertEquals(new YearAndMonth(1998,  9), YearAndMonth.shift(ym, -15));
	}
}
