package com.hypersocket.client.hosts;

import java.io.IOException;

public interface SocketRedirector {

	
	void startRedirecting(String sourceAddr, int sourcePort, String destAddr, int destPort) throws IOException;
	
	void stopRedirecting(String sourceAddr, int sourcePort, String destAddr, int destPort) throws IOException;
	
	void enableLogging(boolean enabled) throws IOException;
}
