package com.weisong.test.comm.impl;

import com.weisong.test.comm.CComponentType;
import com.weisong.test.comm.CMessageHandler;

public class CHazelcastEndpoint extends CBaseEndpoint {

	public CHazelcastEndpoint(CMessageHandler handler)
			throws Exception {
		super(new CHazelcastHub(CComponentType.endpoint), handler);
	}

}
