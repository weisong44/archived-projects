package com.weisong.test.comm.impl;

import com.weisong.test.comm.CAddressable;
import com.weisong.test.comm.CComponentType;
import com.weisong.test.comm.CMessageHandler;
import com.weisong.test.comm.exception.CException;
import com.weisong.test.comm.pdu.CPdu;

public class CHazelcastWebSocketAgent extends CBaseAgent {
	
	private class WebSocketProxyRemote implements ProxyRemote {
		private CWebSocketClient wsClient;
		private WebSocketProxyRemote(String[] urls) throws Exception {
			wsClient = new CWebSocketClient(urls, CHazelcastWebSocketAgent.this);
		}
		@Override
		public void register(CAddressable addressable) throws CException {
		}
		@Override
		public void publish(CPdu pdu) throws CException {
			wsClient.publish(pdu);
		}
	}
	
	public CHazelcastWebSocketAgent(String[] urls, CMessageHandler handler) 
			throws Exception {
		super(new CHazelcastHub(CComponentType.agent), handler);
		setProxyRemote(new WebSocketProxyRemote(urls));
	}
	
	static public void main(String[] args) throws Exception {
		if(args.length == 1) {
			if("-h".equals(args[0]) || "-help".equals(args[0])) {
				printUsageAndExit(CHazelcastWebSocketAgent.class);
			}
		}

		if(args.length > 1) {
			printUsageAndExit(CHazelcastWebSocketAgent.class);
		}
		
		String[] urls = getUrls(args);
		CMessageHandler handler = new CDefaultRequestHandler();
		CHazelcastWebSocketAgent agent = new CHazelcastWebSocketAgent(urls, handler);
		synchronized (agent) {
			agent.wait();
		}
	}
}
