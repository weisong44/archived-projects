package com.weisong.trading.portfolio.chart;

import java.util.LinkedList;
import java.util.List;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.weisong.trading.portfolio.model.DailyEquity;
import com.weisong.trading.portfolio.model.MonthlySelection;
import com.weisong.trading.portfolio.model.Portfolio.OptimizationResult;
import com.weisong.trading.portfolio.model.PortfolioEquity;
import com.weisong.trading.portfolio.model.SystemSelection;

public class ChartUtil {
	
	static public XYDataset createDataset(PortfolioEquity equity) {
		TimeSeriesCollection c = new TimeSeriesCollection();
		c.addSeries(createEquitySeries("Total", equity.getDailyEquities()));
		c.addSeries(createDrawdownSeries("Total", equity.getDailyEquities()));
		equity.getSystemEquities().forEach(e -> {
//			c.addSeries(createEquitySeries(e.getSystem().getSystemId(), e.getDailyEquities()));
//			c.addSeries(createDrawdownSeries(e.getSystem().getSystemId(), e.getDailyEquities()));
		});
		return c;
	}
    
	static public XYDataset createDataset(OptimizationResult result) {
		TimeSeriesCollection c = new TimeSeriesCollection();
		for(SystemSelection ss : result.getSelections()) {
			c.addSeries(createResultSeries(ss));
		}
		return c;
	}
    
	static public TimeSeries createEquitySeries(String name, List<DailyEquity> equities) {
		final TimeSeries series = new TimeSeries(name);
		for(DailyEquity e : equities) {
			series.add(new Day(e.getDate()), e.getEquity());
		}
		return series;
	}
    
	static public TimeSeries createDrawdownSeries(String name, List<DailyEquity> equities) {
		final TimeSeries series = new TimeSeries(name + "-DD");
		for(DailyEquity e : equities) {
			series.add(new Day(e.getDate()), e.getDrawdown());
		}
		return series;
	}
    
	static int resultCount;
	static public TimeSeries createResultSeries(SystemSelection selection) {
		List<MonthlySelection> list = new LinkedList<>(
				selection.getAlltMonthlySelection().values());
		MonthlySelection.sortBySelectionDate(list);
		
		++resultCount;
		final TimeSeries series = new TimeSeries(selection.getSystem().getSystemId());
		for(MonthlySelection ms : list) {
			series.add(new Day(ms.getSelectionDate()), resultCount * ms.getContracts());
		}
		return series;
	}

}
