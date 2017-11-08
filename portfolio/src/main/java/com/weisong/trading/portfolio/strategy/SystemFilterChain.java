package com.weisong.trading.portfolio.strategy;

import java.util.LinkedList;
import java.util.List;

import com.weisong.trading.portfolio.model.SystemSelection;

public class SystemFilterChain {
	
	private List<SystemFilter> filters = new LinkedList<>();
	
	public SystemFilterChain(SystemFilter ... filters) {
		for(SystemFilter f : filters) {
			addFilter(f);
		}
	}
	
	public void addFilter(SystemFilter filter) {
		filters.add(filter);
	}

	public void filter(SystemSelection selection) throws Exception {
		for(SystemFilter f : filters) {
			f.filterAll(selection);
		}
	}
	
}
