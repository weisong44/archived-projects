package com.weisong.test.comm.impl;

import lombok.Getter;

import com.weisong.test.comm.CAddressable;
import com.weisong.test.comm.CComponentType;
import com.weisong.test.comm.util.IdGen;

public class CDefaultAddressable implements CAddressable {

	static public String hostname = "localhost";
	
	static {
		/*
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			hostname = "localhost";
		}
		 */
	}
	
	@Getter protected String id;
	@Getter protected String address;
	@Getter protected CComponentType type;
	
	public CDefaultAddressable(CComponentType type) {
		this.type = type;
		id = IdGen.next(type.toString());
		address = String.format("ccp:%s://%s/%s", type, hostname, id);
	}
}
