package com.weisong.trading.portfolio.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.weisong.trading.portfolio.BaseUnitTest;
import com.weisong.trading.portfolio.model.SystemSelection;

public class TestSystemFilterChain extends BaseUnitTest {
	
	@Before
	public void prepare() throws Exception {
		super.prepare();
	}
	
	@Test
	public void testFilter() throws Exception {
		SystemSelection selection = new SystemSelection(s991); 
		filterChain.filter(selection);
		Assert.assertFalse(selection.getAlltMonthlySelection().isEmpty());
	}
}
