package com.weisong.trading.portfolio.strategy;

public interface SystemChooser {
	boolean chooseSystemId(String systemId);
	boolean chooseFileName(String fname);
}
