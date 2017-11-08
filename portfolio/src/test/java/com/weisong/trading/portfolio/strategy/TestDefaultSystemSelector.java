package com.weisong.trading.portfolio.strategy;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.weisong.trading.portfolio.BaseUnitTest;


public class TestDefaultSystemSelector extends BaseUnitTest {
	@Test
	public void testDefaultSystemSelector() {
		
		SystemChooser c = getSystemChooser();
		
		Assert.assertTrue(c.chooseSystemId("S991"));
		Assert.assertTrue(c.chooseSystemId("s991"));
		Assert.assertTrue(c.chooseSystemId("S992"));
		Assert.assertTrue(c.chooseSystemId("s992"));

		Assert.assertFalse(c.chooseSystemId(null));
		Assert.assertFalse(c.chooseSystemId("abc"));
		
		Assert.assertTrue(c.chooseFileName("c:" + File.separator + "Temp" + File.separator + "S991 a test system"));
		Assert.assertTrue(c.chooseFileName("c:" + File.separator + "Temp" + File.separator + "S992 a test system"));
		Assert.assertFalse(c.chooseFileName("c:" + File.separator + "Temp" + File.separator + "S993 a test system"));
		Assert.assertFalse(c.chooseFileName("c:" + File.separator + "Temp" + File.separator + "S91 a test system"));
	}
}
