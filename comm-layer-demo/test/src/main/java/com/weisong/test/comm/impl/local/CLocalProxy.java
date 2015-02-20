package com.weisong.test.comm.impl.local;

import com.weisong.test.comm.CHub;
import com.weisong.test.comm.exception.CException;
import com.weisong.test.comm.impl.CBaseProxy;
import com.weisong.test.comm.impl.CPduProcessor;
import com.weisong.test.comm.pdu.CPdu;

public class CLocalProxy extends CBaseProxy {

	static public class LocalEndpointRemote implements EndpointRemote {
		protected CPduProcessor agent;
		protected String address;
		protected LocalEndpointRemote(CPduProcessor agent, String address) {
			this.agent = agent;
			this.address = address;
		}
		@Override
		public String getAddress() {
			return address;
		}
		@Override
		public void publish(CPdu pdu) throws CException {
			agent.publish(pdu);
		}
	}
	
	public CLocalProxy(CHub hub) {
		super(hub);
	}
}
