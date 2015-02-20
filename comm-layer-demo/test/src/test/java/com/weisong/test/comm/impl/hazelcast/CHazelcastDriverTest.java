package com.weisong.test.comm.impl.hazelcast;

import com.weisong.test.comm.CBaseDriverTest;
import com.weisong.test.comm.CComponentType;
import com.weisong.test.comm.impl.CDefaultDriver;
import com.weisong.test.comm.impl.CHazelcastHub;

public class CHazelcastDriverTest extends CBaseDriverTest {

	@Override
	protected void createObjects() throws Exception {
		proxyHub = new CHazelcastHub(CComponentType.driver);
		drivers = new CDefaultDriver[] {
		    new CDefaultDriver(proxyHub)
		  , new CDefaultDriver(proxyHub)
		  , new CDefaultDriver(proxyHub)
		  , new CDefaultDriver(proxyHub)
		};
	}
}
