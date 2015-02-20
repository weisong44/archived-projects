package com.weisong.test.comm.impl;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weisong.test.comm.CComponentType;
import com.weisong.test.comm.CEndpoint;
import com.weisong.test.comm.CHub;
import com.weisong.test.comm.CMessageHandler;
import com.weisong.test.comm.exception.CException;
import com.weisong.test.comm.message.CRequest;
import com.weisong.test.comm.message.CResponse;
import com.weisong.test.comm.message.builtin.CCommonMsgs;
import com.weisong.test.comm.message.builtin.CCommonMsgs.Status.StatusValue;
import com.weisong.test.comm.pdu.CPdu;
import com.weisong.test.comm.util.AddrUtil;

abstract public class CBaseEndpoint extends CHubPduProcessor implements CEndpoint {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Getter @Setter 
	protected int reportingInterval = 1000; // ms
	
	protected ReportingWorker reportingWorker;
	
	public CBaseEndpoint(CHub hub, CMessageHandler handler)
			throws CException {
		super(CComponentType.endpoint, hub, handler);
		
		reportingWorker = new ReportingWorker();
		reportingWorker.start();
	}
	
	@Override
	protected void handlePdu(CPdu pdu) {
		if(pdu.isRequest()) {
			CRequest request = pdu.toRequest();
			CResponse response = messageHandler.onRequest(request);
			if(logger.isDebugEnabled()) {
				logger.debug(String.format("%s responds with [%s]", getId(), response));
			}
			try {
				hub.publish(AddrUtil.getAllAgents(), new CPdu(response));
			} catch (CException e) {
				e.printStackTrace();
			}
		}
		else if(pdu.isNotification()) {
			messageHandler.onNotification(pdu.toNotification());
		}
		else {
			logger.error("Message type not supported: " + pdu.type);;
		}
	}

	@Override
	public void shutdown() {
		super.shutdown();
		reportingWorker.shutdown = true;
		try {
			reportingWorker.interrupt();
			reportingWorker.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class ReportingWorker extends Thread {
		private boolean shutdown;
		public void run() {
			setName(CBaseEndpoint.this.getId() + ".ReportingWorker");
			while(shutdown == false) {
				try {
					Thread.sleep(reportingInterval);
					CCommonMsgs.Status okStatus = 
						new CCommonMsgs.Status(address, AddrUtil.getAnyDriver(), StatusValue.Ok);
					hub.publish(new CPdu(okStatus));
				} catch (Throwable t) {
					// Do nothing
				}
			}
		}
	}
}
