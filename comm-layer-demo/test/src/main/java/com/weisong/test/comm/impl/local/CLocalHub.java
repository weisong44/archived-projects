package com.weisong.test.comm.impl.local;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.weisong.test.comm.CComponentType;
import com.weisong.test.comm.CHub;
import com.weisong.test.comm.impl.CPduProcessor;
import com.weisong.test.comm.pdu.CPdu;

public class CLocalHub extends CPduProcessor implements CHub {

	private ConcurrentHashMap<String, Map<String, CHub.Listener>> map = new ConcurrentHashMap<>();

	public CLocalHub() {
		super(CComponentType.hub);
		
	}

	public CLocalHub(String id) {
		super(CComponentType.hub);
		this.id = id;
	}

	@Override
	public String register(String topic, CHub.Listener listener) {
		Map<String, CHub.Listener> listenerMap = map.get(topic);
		if(listenerMap == null) {
			listenerMap = new ConcurrentHashMap<String, CHub.Listener>();
			map.put(topic, listenerMap);
		}
		String regId = UUID.randomUUID().toString();
		listenerMap.put(regId, listener);
		return regId;
	}

	@Override
	public void unregister(String topic, String regId) {
		Map<String, CHub.Listener> listenerMap = map.get(topic);
		if(listenerMap != null) {
			listenerMap.remove(regId);
			if(listenerMap.isEmpty()) {
				map.remove(topic);
			}
		}
	}

	@Override
	protected void handlePdu(CPdu pdu) {
		Map<String, CHub.Listener> listenerMap = map.get(pdu.destAddr);
		if(listenerMap != null) {
			for(CHub.Listener l : listenerMap.values()) {
				l.onHubPdu(pdu);
			}
		}
		else {
			logger.warn(String.format("%s no listener found for %s", id, pdu.destAddr));
			//throw new RuntimeException("No listener found for " + message.address);
		}
	}

	@Override
	public void publish(String topic, CPdu pdu) {
		Map<String, CHub.Listener> listenerMap = map.get(topic);
		if(listenerMap != null) {
			for(CHub.Listener l : listenerMap.values()) {
				l.onHubPdu(pdu);
			}
		}
		else {
			logger.warn(String.format("%s no listener found for %s", id, pdu.destAddr));
			//throw new RuntimeException("No listener found for " + message.address);
		}
	}
}
