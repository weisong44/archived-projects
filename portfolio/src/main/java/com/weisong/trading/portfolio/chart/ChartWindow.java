package com.weisong.trading.portfolio.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import com.weisong.trading.portfolio.model.Portfolio.OptimizationResult;
import com.weisong.trading.portfolio.model.PortfolioEquity;

@SuppressWarnings("serial")
public class ChartWindow extends ApplicationFrame {
	
	public ChartWindow(String name, PortfolioEquity equity) {
		super(name);
		show(ChartUtil.createDataset(equity));
	}
	
	public ChartWindow(String name, OptimizationResult result) {
		super(name);
		show(ChartUtil.createDataset(result));
	}
	
	public void show(XYDataset dataset) {
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(
				getTitle(), "Date", "Equity", dataset, true, true, false);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
		chartPanel.setMouseZoomable(true, false);
		setContentPane(chartPanel);
		pack();
		RefineryUtilities.positionFrameRandomly(this);
		setVisible(true);
	}
}
