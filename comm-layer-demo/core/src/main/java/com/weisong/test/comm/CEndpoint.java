package com.weisong.test.comm;

public interface CEndpoint extends CAddressable {
	void setMessageHandler(CMessageHandler handler);
}
