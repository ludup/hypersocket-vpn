package com.hypersocket.network.events;

import com.hypersocket.network.NetworkProtocol;
import com.hypersocket.network.NetworkResource;
import com.hypersocket.session.Session;

public class NetworkResourceSessionClosed extends NetworkResourceSessionEvent {

	private static final long serialVersionUID = -584310921887757202L;

	public static final String EVENT_RESOURCE_KEY = "networkResource.sessionClosed";
	
	public static final String ATTR_BYTES_IN = "attr.totalBytesIn";
	public static final String ATTR_BYTES_OUT = "attr.totalBytesOut";
	
	public NetworkResourceSessionClosed(Object source,
			NetworkResource resource, NetworkProtocol protocol, Session session, long totalBytesIn,
			long totalBytesOut, int actualPort) {
		super(source, EVENT_RESOURCE_KEY, true, resource, protocol, actualPort, session);
		addAttribute(ATTR_BYTES_IN, String.valueOf(totalBytesIn));
		addAttribute(ATTR_BYTES_OUT, String.valueOf(totalBytesIn));
	}

}
