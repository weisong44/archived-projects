package com.weisong.test.comm;

import com.weisong.test.comm.exception.CException;
import com.weisong.test.comm.pdu.CPdu;

public interface CProxy extends CAddressable {
	
	public interface EndpointRemote {
		String getAddress();
		void publish(CPdu pdu) throws CException;
	}
	
	void register(EndpointRemote endpointRemote) throws CException;
	void publish(CPdu pdu) throws CException;

}
