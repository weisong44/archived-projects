package com.weisong.trading.portfolio;

import org.junit.Before;

import com.weisong.trading.portfolio.model.Portfolio;
import com.weisong.trading.portfolio.model.SystemStats;
import com.weisong.trading.portfolio.strategy.AllPassSystemFilter;
import com.weisong.trading.portfolio.strategy.DefaultSystemChooser;
import com.weisong.trading.portfolio.strategy.NetProfitSystemFilter1;
import com.weisong.trading.portfolio.strategy.NetProfitSystemFilter2;
import com.weisong.trading.portfolio.strategy.NetProfitToMaxDdSystemFilter;
import com.weisong.trading.portfolio.strategy.RebalanceDates;
import com.weisong.trading.portfolio.strategy.RebalanceDates.Type;
import com.weisong.trading.portfolio.strategy.ReverseSelectionSystemFilter;
import com.weisong.trading.portfolio.strategy.SystemChooser;
import com.weisong.trading.portfolio.strategy.SystemFilter;
import com.weisong.trading.portfolio.strategy.SystemFilterChain;
import com.weisong.trading.portfolio.util.LogLevel;

import lombok.Getter;

public class BaseUnitTest {
	
	protected String baseDir = "data/ut";
	
	protected SystemStats s991;
	protected SystemStats s992;
	
	@Getter
	protected SystemChooser systemChooser;
	
	@Getter
	protected RebalanceDates rebalanceDates;
	
	@Getter
	protected SystemFilterChain filterChain;
	
	protected Portfolio portfolio;
	
	@Before
	public void prepare() throws Exception {

		Constants.logLevel = LogLevel.INFO;
		
		rebalanceDates = new RebalanceDates(Type.MONTHLY_LAST_WEEKEND);
		
		filterChain = new SystemFilterChain(createNetProfitSystemFilter1());
		
		systemChooser = new DefaultSystemChooser.Builder().createDefault();
		
		s991 = SystemStats.fromFile(getTSFileName("S991"));
		s992 = SystemStats.fromFile(getMCFileName("S992"));
		
		portfolio = Portfolio.fromDirOrFile("test-portfolio", baseDir, null, null, false, systemChooser);
	}
	
	protected SystemFilter createAllPassSystemFilter() {
		return new AllPassSystemFilter.Builder().createDefault();
	}

	protected SystemFilter createNetProfitSystemFilter1() {
		return new NetProfitSystemFilter1.Builder().createDefault();
	}

	protected SystemFilter createNetProfitSystemFilter2() {
		return new NetProfitSystemFilter2.Builder().createDefault();
	}

	protected SystemFilter createReverseSelectionFilter() {
		return new ReverseSelectionSystemFilter.Builder().createDefault();
	}

	protected SystemFilter createNetProfitToMaxDdFilter() {
		return new NetProfitToMaxDdSystemFilter.Builder().createDefault();
	}

	protected String getFullPath(String fname) {
		return String.format("%s/%s", baseDir, fname);
	}
	
	protected String getTSFileName(String systemId) {
		return String.format("%s/%s-TS.csv", baseDir, systemId);
	}
	
	protected String getMCFileName(String systemId) {
		return String.format("%s/%s-MC.csv", baseDir, systemId);
	}
}
