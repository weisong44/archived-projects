package com.weisong.trading.portfolio.util;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.junit.Test;

import com.weisong.trading.portfolio.BaseUnitTest;

public class TestPearsonCorrelation extends BaseUnitTest {
	int length;
	double a[] = new double[] { 2,  1 };
	double b[] = new double[] { 1, -2 };
	
	@Test
	public void testCorrelation() {
		/*
		length = 100;
		a = new double[length];
		b = new double[length];
		Random rand = new Random();
		for(int i = 0; i < length; i++) {
			a[i] = rand.nextFloat() * length;
			b[i] = rand.nextFloat() * length;
		}
		 */
		
		PearsonsCorrelation pc = new PearsonsCorrelation();
		double c = pc.correlation(a, b);
		System.out.println("c = " + c);
	}
}
