package com.hypersocket.network.events;

import com.hypersocket.network.NetworkProtocol;
import com.hypersocket.network.NetworkResource;
import com.hypersocket.session.Session;

public class NetworkResourceSessionOpened extends NetworkResourceEvent {

	private static final long serialVersionUID = -7729337279204702387L;

	public static final String EVENT_RESOURCE_KEY = "networkResource.sessionOpened";
	
	public static final String ATTR_RESOURCE_NAME = "attr.resourceName";
	public static final String ATTR_PROTOCOL_NAME = "attr.protocolName";
	public static final String ATTR_DESTINATION_HOST = "attr.destinationHost";
	public static final String ATTR_DESTINATION_PORT = "attr.destinationPort";
	
	public NetworkResourceSessionOpened(Object source,
			boolean success, NetworkResource resource, Session session, Integer actualPort, NetworkProtocol protocol) {
		super(source, EVENT_RESOURCE_KEY, success, session);
		addAttribute(ATTR_RESOURCE_NAME, resource.getName());
		addAttribute(ATTR_PROTOCOL_NAME, protocol.getName());
		addAttribute(ATTR_DESTINATION_HOST, resource.resolveHostname());
		addAttribute(ATTR_DESTINATION_PORT, String.valueOf(actualPort));
	}

	public NetworkResourceSessionOpened(Object source,
			Throwable e, NetworkResource resource, Session session, Integer actualPort) {
		super(source, EVENT_RESOURCE_KEY, e, session);
		addAttribute(ATTR_RESOURCE_NAME, resource.getName());
		addAttribute(ATTR_DESTINATION_HOST, resource.resolveHostname());
		addAttribute(ATTR_DESTINATION_PORT, String.valueOf(actualPort));
	}

	public NetworkResourceSessionOpened(Object source,
			Throwable e, Session session) {
		super(source, "networkResource.sessionOpened", e, session);
	}
}
