package com.weisong.test.comm;

import com.weisong.test.comm.exception.CException;
import com.weisong.test.comm.pdu.CPdu;

public interface CHub {

	public interface Listener {
		void onHubPdu(CPdu pdu);
	}

	void publish(CPdu pdu) throws CException;
	void publish(String topic, CPdu pdu) throws CException;
	
	/**
	 * @param topic the topic, this is typically an address
	 * @param listener
	 * @return unique registration Id
	 */
	String register(String topic, Listener listener);

	void unregister(String topic, String regId);
}
