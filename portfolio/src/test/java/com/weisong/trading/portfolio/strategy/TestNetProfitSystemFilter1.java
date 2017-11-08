package com.weisong.trading.portfolio.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.weisong.trading.portfolio.BaseUnitTest;
import com.weisong.trading.portfolio.Constants;
import com.weisong.trading.portfolio.model.MonthlySelection;
import com.weisong.trading.portfolio.model.SystemSelection;
import com.weisong.trading.portfolio.model.YearAndMonth;

public class TestNetProfitSystemFilter1 extends BaseUnitTest {
	
	static public final String[] expectedSelectionDate = new String[] {
		              "01/31/2009", "02/28/2009", "03/29/2009",
		"05/02/2009", "05/31/2009", "06/28/2009", "08/01/2009",
		"08/30/2009", "09/27/2009", "10/31/2009", "11/29/2009",
		"01/02/2010", "01/31/2010", "02/28/2010", "03/28/2010",
		"05/01/2010", "05/30/2010", "06/27/2010", "07/31/2010",
		"08/29/2010", "10/02/2010", "10/31/2010", "11/28/2010",
		"01/01/2011", "01/30/2011"
	};
	
	static public final int[] expectedMonthlySelection = new int[] {
		   1, 0, 0, 
		0, 0, 1, 1, 
		1, 1, 1, 1, 
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
		createNetProfitSystemFilter1().filterAll(selection);
		Assert.assertFalse(selection.getAlltMonthlySelection().isEmpty());

		int i = 0;
		YearAndMonth ym1 = new YearAndMonth(2008, 01);
		YearAndMonth ym2 = new YearAndMonth(2012, 12);
		for(YearAndMonth ym = ym1; !ym.after(ym2); ym = YearAndMonth.inc(ym)) {
			MonthlySelection s = selection.getMonthlySelection(ym);
			if(s == null || s.getSelectionDate().before(Constants.df1.parse((expectedSelectionDate[0])))) {
				continue;
			}
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
