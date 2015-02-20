package com.weisong.test.comm.impl.local;

import com.weisong.test.comm.CAddressable;
import com.weisong.test.comm.CHub;
import com.weisong.test.comm.CMessageHandler;
import com.weisong.test.comm.exception.CException;
import com.weisong.test.comm.impl.CBaseAgent;
import com.weisong.test.comm.impl.local.CLocalProxy.LocalEndpointRemote;
import com.weisong.test.comm.pdu.CPdu;
import com.weisong.test.comm.util.AddrUtil;

public class CLocalAgent extends CBaseAgent {

	private class LocalProxyRemote implements ProxyRemote {
		private CLocalProxy proxy;
		private LocalProxyRemote(CLocalProxy proxy) {
			this.proxy = proxy;
		}
		@Override
		public void register(CAddressable endpoint) throws CException {
			LocalEndpointRemote epRemote = new LocalEndpointRemote(CLocalAgent.this, endpoint.getAddress());
			proxy.register(epRemote);
		}
		@Override
		public void publish(CPdu pdu) throws CException {
			if(AddrUtil.isDriver(pdu.destAddr)) {
				proxy.register(new LocalEndpointRemote(CLocalAgent.this, pdu.srcAddr));
			}
			proxy.publish(pdu);
		}
	}
	
	public CLocalAgent(CLocalProxy proxy, CHub hub, CMessageHandler handler)
			throws CException {
		super(hub, handler);
		setProxyRemote(new LocalProxyRemote(proxy));
	}

}
