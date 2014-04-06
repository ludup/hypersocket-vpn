package com.hypersocket.client.hosts;

public class LinuxSocketRedirector extends AbstractSocketRedirector implements
		SocketRedirector {

	@Override
	protected String[] getLoggingArguments(boolean enabled) {
		return new String[] { };
	}

	@Override
	protected String getCommand() {
		return "echo";
	}

	@Override
	protected String[] getStartArguments(String sourceAddr, int sourcePort,
			String destAddr, int destPort) {
		return new String[] { };
	}

	@Override
	protected String[] getStopArguments(String sourceAddr, int sourcePort, String destAddr, int destPort) {
		return new String[] { };
	}

}
