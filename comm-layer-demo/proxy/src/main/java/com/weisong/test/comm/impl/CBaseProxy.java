package com.weisong.test.comm.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.weisong.test.comm.CComponentType;
import com.weisong.test.comm.CHub;
import com.weisong.test.comm.CProxy;
import com.weisong.test.comm.exception.CException;
import com.weisong.test.comm.message.CMessage;
import com.weisong.test.comm.message.builtin.CCommonMsgs;
import com.weisong.test.comm.message.builtin.CDriverMsgs;
import com.weisong.test.comm.pdu.CPdu;
import com.weisong.test.comm.util.AddrUtil;
import com.weisong.test.comm.util.EvictableList;
import com.weisong.test.comm.util.EvictableMap;

abstract public class CBaseProxy extends CHubPduProcessor implements CProxy {

	static class EndpointRemoteContext {
		private EndpointRemoteContext(EndpointRemote epRemote, String regId) {
			this.epRemote = epRemote;
			this.regId = regId;
		}
		private EndpointRemote epRemote;
		private String regId;
	}

	/* key: epAddr, value: ep remote stub */
	protected EvictableMap<String, EndpointRemoteContext> epMap = new EvictableMap<>();
	/* key: class name, value: list of driver addresses*/
	protected Map<String, EvictableList<String>> driverMap = new ConcurrentHashMap<>();
	
	public CBaseProxy(final CHub hub) {
		super(CComponentType.proxy, hub);
		
		epMap.setListener(new EvictableMap.Listener<String, EndpointRemoteContext>() {
			@Override
			public void entryEvicted(String addr, EndpointRemoteContext ctx) {
				hub.unregister(addr, ctx.regId);
			}
		});
		
		register(AddrUtil.getAnyDriver());

		hub.register(AddrUtil.getAllProxies(), new CHub.Listener() {
			@Override
			public void onHubPdu(CPdu pdu) {
				CMessage message = pdu.toMessage();
				if(message instanceof CCommonMsgs.Reset) {
					driverMap.clear();
					epMap.clear();
				}
				else if(message instanceof CDriverMsgs.Profile) {
					CDriverMsgs.Profile dfn = (CDriverMsgs.Profile) message;
					processDriverProfileNotification(dfn);
				}
			}
			private void processDriverProfileNotification(CDriverMsgs.Profile dfn) {
				for(String clazz : dfn.getSupportedNotifications()) {
					EvictableList<String> list = driverMap.get(clazz);
					if(list == null) {
						list = new EvictableList<String>();
						driverMap.put(clazz, list);
					}
					if(list.add(dfn.srcAddr)) {
						logger.info(String.format("%s adding %s -> %s [total=%d]", 
								id, clazz, dfn.srcAddr, list.size()));
					}
				}
			}
		});
	}

	@Override
	public void register(EndpointRemote remote) {
		String epAddr = remote.getAddress();
		if(epMap.containsKey(epAddr) == false) {
			String regId = hub.register(epAddr, new CHub.Listener() {
				@Override
				public void onHubPdu(CPdu pdu) {
					publish(pdu);
				}
			});
			epMap.put(epAddr, new EndpointRemoteContext(remote, regId));
			logger.info(String.format("%s proxying for %s", id, epAddr));
		}
		else {
			if(logger.isDebugEnabled()) {
				logger.debug(String.format("%s refreshing %s", id, epAddr));
			}
			epMap.refresh(epAddr);
		}
	}

	@Override
	protected void handlePdu(CPdu pdu) {
		if(AddrUtil.isEndpoint(pdu.destAddr)) {
			if(logger.isDebugEnabled()) {
				logger.debug(String.format("%s sends downstream [%s]", id, pdu));
			}
			EndpointRemoteContext ctx = epMap.get(pdu.destAddr); 
			if(ctx != null) {
				try {
					ctx.epRemote.publish(pdu);
				} catch (CException e) {
					e.printStackTrace();
				}
			}
			else {
				logger.warn("Endpoint not found for " + pdu.destAddr);
			}
		}
		else if(AddrUtil.isDriver(pdu.destAddr)) {
			if(logger.isDebugEnabled()) {
				logger.debug(String.format("%s sends upstream [%s]", id, pdu));
			}

			// Rewrite message destination
			if(AddrUtil.isAny(pdu.destAddr)) {
				String newAddr = null;
				EvictableList<String> list = driverMap.get(pdu.type);
				if(list != null && (newAddr = list.getOne()) != null) {
					pdu.destAddr = newAddr;
				}
				else {
					logger.warn(String.format(
							"Received message to %s, but found no driver", 
							pdu.destAddr));
					return;
				}
			}
			else if(AddrUtil.isAll(pdu.destAddr)) {
				logger.warn("Address not supported: " + pdu.destAddr);
			}
			
			try {
				hub.publish(pdu);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		else {
			throw new RuntimeException("Not supported");
		}
	}
}
