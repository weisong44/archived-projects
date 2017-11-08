package com.weisong.trading.portfolio.model;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.weisong.trading.portfolio.BaseUnitTest;
import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.util.Util;

public class TestSystemStats extends BaseUnitTest {
	@Test
	public void testReadFromFile() throws Exception {
		SystemStats stats = SystemStats.fromFile(getTSFileName("S991"));
		Assert.assertEquals("S991", stats.getSystemId());
		Assert.assertEquals(2795, stats.getDailyStats().size());
	}

	@Test
	public void testGetEquity() throws Exception {
		List<DailyEquity> eList = SystemStats
			.fromFile(getTSFileName("S991"))
			.getEquity()
			.getDailyEquities();
		List<Map<String, String>> cList = Util.readCsv(
			getTSFileName("S991").replaceAll(".csv", ".verify.equity.csv"));
		Assert.assertEquals(cList.size(), eList.size());
		for(int i = 0; i < eList.size(); i++) {
			Map<String, String> map = cList.get(i);
			DailyEquity de = eList.get(i);
			Assert.assertEquals(Constants.df1.parse(map.get("Date")), de.getDate());
			Assert.assertEquals(Float.valueOf(map.get("Equity")), new Float(de.getEquity()));
			Assert.assertEquals(Float.valueOf(map.get("DD")), new Float(de.getDrawdown()));
		}
	}
}
