package com.weisong.trading.portfolio.simulation;

import org.junit.Assert;
import org.junit.Test;

import com.weisong.trading.portfolio.BaseUnitTest;
import com.weisong.trading.portfolio.generator.Generator;
import com.weisong.trading.portfolio.model.Portfolio.OptimizationProfile;
import com.weisong.trading.portfolio.model.Portfolio.OptimizationResult;
import com.weisong.trading.portfolio.model.PortfolioEquity;
import com.weisong.trading.portfolio.model.SystemEquity;
import com.weisong.trading.portfolio.model.SystemSelection;

public class TestGenerator extends BaseUnitTest {

//	@Test
	public void testGenerateSystemEquityCurve() throws Exception {
		Generator generator = new Generator();
		SystemSelection selection = new SystemSelection(s991); 
		filterChain.filter(selection);
		SystemEquity equity = generator.generateSystemEquityCurve(selection);
		Assert.assertNotNull(equity);
		
		System.out.println(equity.toCsv());
	}


	@Test
	public void testGeneratePortfolioEquityCurve() throws Exception {
		OptimizationProfile profile = new OptimizationProfile(rebalanceDates, filterChain);
		OptimizationResult result = portfolio.optimize(profile);
		
		Generator generator = new Generator();
		PortfolioEquity equity = generator.generatePortfolioEquityCurve(result);
		Assert.assertNotNull(equity);
		
		System.out.println(equity.toCsv());
	}
}
