package com.weisong.test.comm.impl.local;

import com.weisong.test.comm.CHub;
import com.weisong.test.comm.CMessageHandler;
import com.weisong.test.comm.exception.CException;
import com.weisong.test.comm.impl.CBaseEndpoint;

public class CLocalEndpoint extends CBaseEndpoint {
	
	public CLocalEndpoint(CHub hub, CMessageHandler handler)
			throws CException {
		super(hub, handler);
	}
}
