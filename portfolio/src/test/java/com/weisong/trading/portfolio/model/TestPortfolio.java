package com.weisong.trading.portfolio.model;

import org.junit.Assert;
import org.junit.Test;

import com.weisong.trading.portfolio.BaseUnitTest;
import com.weisong.trading.portfolio.model.Portfolio.OptimizationProfile;
import com.weisong.trading.portfolio.model.Portfolio.OptimizationResult;

public class TestPortfolio extends BaseUnitTest {
	@Test
	public void testFromDir() throws Exception {
		Assert.assertTrue(portfolio.getSystems().size() > 0);
	}

	@Test
	public void testOptimize() throws Exception {
		OptimizationProfile profile = new OptimizationProfile(rebalanceDates, filterChain);
		OptimizationResult result = portfolio.optimize(profile);
		Assert.assertTrue(result.getSelections().size() > 0);
	}
}
