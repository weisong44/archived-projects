package com.weisong.test.comm.util;

public class AddrUtil {
	
	static public String getAnyDriver() {
		return "ccp:driver://any";
	}

	static public String getAllProxies() {
		return "ccp:proxy://all";
	}

	static public String getAllAgents() {
		return "ccp:agent://all";
	}

	static public boolean isDriver(String addr) {
		return addr.contains(":driver:");
	}

	static public boolean isProxy(String addr) {
		return addr.contains(":proxy:");
	}

	static public boolean isEndpoint(String addr) {
		return addr.contains(":endpoint:");
	}

	static public boolean isAny(String addr) {
		return addr.endsWith("://any");
	}

	static public boolean isAll(String addr) {
		return addr.endsWith("://all");
	}
}
