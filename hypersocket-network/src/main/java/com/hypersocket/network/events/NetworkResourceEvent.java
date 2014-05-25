package com.hypersocket.network.events;

import com.hypersocket.network.NetworkProtocol;
import com.hypersocket.network.NetworkResource;
import com.hypersocket.network.NetworkResourceService;
import com.hypersocket.session.Session;
import com.hypersocket.session.events.SessionEvent;

public class NetworkResourceEvent extends SessionEvent {

	private static final long serialVersionUID = 8248391425589891659L;

	public static final String ATTR_RESOURCE_NAME = "attr.resourceName";
	public static final String ATTR_PROTOCOL_NAME = "attr.protocolName";
	public static final String ATTR_DESTINATION_HOST = "attr.destinationHost";
	public static final String ATTR_DESTINATION_PORT = "attr.destinationPort";
	
	public NetworkResourceEvent(Object source, String resourceKey,
			boolean success, NetworkResource resource, NetworkProtocol protocol, int actualPort, Session session) {
		super(source, resourceKey, success, session);
		addAttribute(ATTR_RESOURCE_NAME, resource.getName());
		addAttribute(ATTR_PROTOCOL_NAME, protocol.getName());
		addAttribute(ATTR_DESTINATION_HOST, resource.resolveHostname());
		addAttribute(ATTR_DESTINATION_PORT, String.valueOf(actualPort));
	}

	public NetworkResourceEvent(Object source, String resourceKey, NetworkResource resource, int actualPort, Throwable e,
			Session session) {
		super(source, resourceKey, e, session);
		addAttribute(ATTR_RESOURCE_NAME, resource.getName());
		addAttribute(ATTR_DESTINATION_HOST, resource.resolveHostname());
		addAttribute(ATTR_DESTINATION_PORT, String.valueOf(actualPort));
	}

	@Override
	public String getResourceBundle() {
		return NetworkResourceService.RESOURCE_BUNDLE;
	}

}
