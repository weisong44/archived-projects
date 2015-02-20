package com.weisong.test.comm;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;

import com.weisong.test.comm.impl.CBaseEndpoint;
import com.weisong.test.comm.impl.CBaseProxy;
import com.weisong.test.comm.impl.CDefaultDriver;
import com.weisong.test.comm.impl.CPduProcessor;
import com.weisong.test.comm.message.builtin.CCommonMsgs;

abstract public class CBaseDriverTestOne {

	protected CHub hub;
	protected CBaseProxy[] proxies;
	protected CDefaultDriver[] drivers;
	protected CBaseEndpoint[] endpoints;
	protected String[] epAddrs;
	
	@Test
	public void testSend() throws Exception {
		for(final CDriver driver : drivers) {
			for(int i = 0; i < epAddrs.length; i++) {
				final CCommonMsgs.Ping.Request request = new CCommonMsgs.Ping.Request(driver.getAddress(), epAddrs[i]);
				CCommonMsgs.Ping.Response response = driver.send(request);
				Assert.assertNotNull(response);
			}
		}
	}
	
	@After
	public void after() {
		if(endpoints != null) {
			for(CBaseEndpoint e : endpoints) {
				e.shutdown();
			}
		}
		if(proxies != null) {
			for(CBaseProxy p : proxies) {
				p.shutdown();
			}
		}
		if(drivers != null) {
			for(CDefaultDriver d : drivers) {
				d.shutdown();
			}
		}
		if(hub != null && hub instanceof CPduProcessor) {
			((CPduProcessor)hub).shutdown();
		}
	}
	
}
