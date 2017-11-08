package com.weisong.trading.portfolio.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.weisong.trading.portfolio.BaseUnitTest;
import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.model.MonthlySelection;
import com.weisong.trading.portfolio.model.SystemSelection;
import com.weisong.trading.portfolio.model.YearAndMonth;

public class TestNetProfitToMaxDdSystemFilter extends BaseUnitTest {
	
	static public final String[] expectedSelectionDate = new String[] {
					  "01/30/2011", "02/27/2011", "04/02/2011",
		"04/30/2011", "05/29/2011", "07/02/2011", "07/31/2011",
		"08/28/2011", "10/01/2011", "10/30/2011", "11/27/2011",
		"12/31/2011", "01/29/2012", "02/26/2012", "03/31/2012",
		"04/29/2012", "06/02/2012", "06/30/2012", "07/29/2012",
		"09/01/2012", "09/30/2012", "10/28/2012", "12/01/2012",
		"12/30/2012", "02/02/2013"
	};
	
	static public final int[] expectedMonthlySelection = new int[] {
		   1, 1, 1, 
		0, 1, 1, 1, 
		1, 1, 0, 0, 
		1, 1, 1, 1, 
		1, 1, 1, 1, 
		1, 1, 1, 1, 
		1, 1 
	};
	
	@Before
	public void prepare() throws Exception {
		super.prepare();
	}
	
	@Test
	public void testFilter() throws Exception {
		SystemSelection selection = new SystemSelection(s991);
		SystemFilterChain chain = new SystemFilterChain(
		  	createNetProfitToMaxDdFilter()
		);
		chain.filter(selection);

		Assert.assertFalse(selection.getAlltMonthlySelection().isEmpty());

		int i = 0;
		YearAndMonth ym1 = new YearAndMonth(2008, 01);
		YearAndMonth ym2 = new YearAndMonth(2012, 12);
		for(YearAndMonth ym = ym1; !ym.after(ym2); ym = YearAndMonth.inc(ym)) {
			MonthlySelection s = selection.getMonthlySelection(ym);
			if(s != null && i < expectedSelectionDate.length) {
				System.out.println("Verifying " + ym + " ...");
				Assert.assertEquals(
					expectedSelectionDate[i], 
					Constants.df1.format(s.getSelectionDate()));
				Assert.assertEquals(
					expectedMonthlySelection[i],
					s.getContracts());
				++i;
			}
		}
	}
}
