package com.weisong.trading.portfolio.strategy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.weisong.trading.portfolio.model.RebalanceDateType;
import com.weisong.trading.portfolio.model.SystemSelection;

public interface SystemFilter extends Processor {
	
	static abstract public class Config extends Processor.Config {
		
		static public String KEY_REBALANCE_DATE_TYPE = "selection.date.type";
		static public String KEY_ORDER = "order";
		
		public Config() {
			setRebalanceDateType(RebalanceDateType.END_OF_MONTH_NO_TRADING_DAY);
		}
		
		public Config(Map<String, String> props) {
			super(props);
		}
		
		@Override
		String getDefaultKeyPrefix() {
			return "system.filters";
		}
		
		@Override
		String getKeyPrefix() {
			return "system.filter";
		}
		
		public void setRebalanceDateType(RebalanceDateType type) {
			putDefault(KEY_REBALANCE_DATE_TYPE, type.toString());
		}
		
		public RebalanceDateType getRebalanceDateType() {
			return RebalanceDateType.valueOf(get(KEY_REBALANCE_DATE_TYPE));
		}

		public void setOrder(List<String> order) {
			String orderStr = order.toString()
				.replaceAll(" ", "")
				.replaceAll("[", "")
				.replaceAll("]", "");
			putDefault(KEY_ORDER, orderStr);
		}
		
		public List<String> getOrder() {
			String orderStr = get(KEY_ORDER);
			if(orderStr == null) {
				return null;
			}
			orderStr = orderStr
				.replaceAll(" ", "")
				.replaceAll("\t", "");
			return Arrays.asList(orderStr.split(","));
		}
		
	}

	static abstract public class Builder<P extends SystemFilter> implements Processor.Builder<P> {
	}
	
	void filterAll(SystemSelection selection) throws Exception;
}
