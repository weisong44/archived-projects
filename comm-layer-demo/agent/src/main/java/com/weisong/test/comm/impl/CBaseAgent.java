package com.weisong.test.comm.impl;

import com.weisong.test.comm.CAddressable;
import com.weisong.test.comm.CAgent;
import com.weisong.test.comm.CComponentType;
import com.weisong.test.comm.CHub;
import com.weisong.test.comm.CMessageHandler;
import com.weisong.test.comm.exception.CException;
import com.weisong.test.comm.message.CMessage;
import com.weisong.test.comm.message.CResponse;
import com.weisong.test.comm.pdu.CPdu;
import com.weisong.test.comm.util.AddrUtil;
import com.weisong.test.comm.util.EvictableList;
import com.weisong.test.comm.util.HostAndPortResolver;
import com.weisong.test.comm.util.HostAndPortResolver.HostAndPort;

abstract public class CBaseAgent extends CHubPduProcessor implements CAgent {

	public interface ProxyRemote {
		void register(CAddressable addressable) throws CException;
		void publish(CPdu pdu) throws CException;
	}

	protected ProxyRemote proxyRemote;
	protected EvictableList<String> epList = new EvictableList<>();

	public CBaseAgent(CHub hub, CMessageHandler handler) 
			throws CException {
		super(CComponentType.agent, hub, handler);
		register(AddrUtil.getAnyDriver());
		register(AddrUtil.getAllAgents());
	}

	public void setProxyRemote(ProxyRemote proxyRemote) throws CException {
		this.proxyRemote = proxyRemote;
		this.proxyRemote.register(this);
	}

	@Override
	protected void handlePdu(CPdu pdu) {
		if(AddrUtil.isEndpoint(pdu.destAddr)) {
			if(logger.isDebugEnabled()) {
				logger.debug(String.format("%s sends downstream [%s]", id, pdu));
			}
			try {
				hub.publish(pdu);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		else if(AddrUtil.isDriver(pdu.destAddr)) {
			if(logger.isDebugEnabled()) {
				logger.debug(String.format("%s sends upstream [%s]", id, pdu));
			}
			
			if(AddrUtil.isEndpoint(pdu.srcAddr)) {
				boolean isNew = epList.add(pdu.srcAddr);
				if(isNew) {
					logger.info(String.format("%s manages [%s]", getId(), pdu.srcAddr));
				}
			}
			
			try {
				proxyRemote.publish(pdu);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		else if(address.equals(pdu.destAddr)) {
			if(pdu.isRequest()) {
				CResponse response = messageHandler.onRequest(pdu.toRequest());
				sendMessage(response);
			}
		}
		else {
			throw new RuntimeException("Not supported");
		}
	}

	protected void sendMessage(CMessage message) {
		try {
			CPdu pdu = new CPdu(message);
			if(AddrUtil.isDriver(message.destAddr)) {
				proxyRemote.publish(pdu);
			}
			else if(AddrUtil.isEndpoint(message.destAddr)) {
				hub.publish(pdu);
			}
			else {
				logger.warn(String.format("Unable to deliver to ", message.destAddr));
			}
		} catch (CException e) {
			e.printStackTrace();
		}
	}

	static public void printUsageAndExit(Class<?> clazz) {
		System.out.println("Usage:");
		System.out.println(String.format("    java %s [host:port[,host:port]...]",
				clazz.getSimpleName()));
		System.out.println();
		System.exit(0);
	}

	static public String[] getUrls(String[] args) throws Exception {
		String hostAndPortString = args.length == 0 ?
			"localhost:8080" : args[0];

		HostAndPort[] hapArray = HostAndPortResolver.resolveMultiple(
				hostAndPortString, "localhost", 6379);
		String[] urls = new String[hapArray.length];
		System.out.println("Using proxies at");
		for(int i = 0; i < hapArray.length; i++) {
			urls[i] = String.format("ws://%s/proxy", hapArray[i].toString());
			System.out.println("    " + urls[i]);
		}
		return urls;
	}
}
