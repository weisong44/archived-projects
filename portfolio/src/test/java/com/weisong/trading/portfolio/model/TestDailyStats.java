package com.weisong.trading.portfolio.model;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.weisong.trading.portfolio.BaseUnitTest;
import com.weisong.trading.portfolio.Constants;

public class TestDailyStats extends BaseUnitTest {
	
	@Test
	public void testReadFromFile() throws Exception {
		List<DailyStats> list = DailyStats.fromFile(getTSFileName("S991"));
		Assert.assertEquals(2795, list.size());
	}

	@Test
	public void testReadFromFileWithDates() throws Exception {
		Date fromDate = Constants.df1.parse("01/01/2010"); 
		Date toDate = Constants.df1.parse("01/01/2015"); 
		List<DailyStats> list = DailyStats.fromFile(getTSFileName("S991"), fromDate, toDate);
		Assert.assertEquals(1557, list.size());
		list.forEach(stats -> {
			Assert.assertFalse(stats.getDate().before(fromDate));
			Assert.assertFalse(stats.getDate().after(toDate));
		});
	}

}
