package com.weisong.trading.portfolio.model;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.weisong.trading.portfolio.BaseUnitTest;
import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.generator.Generator;
import com.weisong.trading.portfolio.strategy.AllPassSystemFilter;

public class TestEquityCurve extends BaseUnitTest {
	@Test
	public void testMaxDd() throws Exception {
		SystemSelection s = new SystemSelection(s991);
		new AllPassSystemFilter.Builder().createDefault().filterAll(s);
		SystemEquity equity = new Generator().generateSystemEquityCurve(s);
		testMaxDdInternal(equity, "07/20/2015", "01/01/2016", -4120F);
		testMaxDdInternal(equity, "03/27/2015", "05/01/2015", -2760F);
	}
	
	private void testMaxDdInternal(SystemEquity equity, String beg, 
			String end, float expected) throws Exception {
		Date begDate = Constants.df1.parse(beg);
		Date endDate = Constants.df1.parse(end);
		float maxDd = equity.getMaxDd(begDate, endDate);
		Assert.assertEquals(expected, maxDd, 0.00001F);
	}
}
