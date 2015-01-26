package com.hypersocket.protocols.events;

import org.apache.commons.lang3.ArrayUtils;

import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.realm.events.ResourceEvent;
import com.hypersocket.session.Session;

public class NetworkProtocolEvent extends ResourceEvent {

	public static final String EVENT_RESOURCE_KEY = "protocol.event";
	
	public static final String ATTR_PORTS = "attr.ports";
	public static final String ATTR_TRANSPORT = "attr.transport";
	
	private static final long serialVersionUID = 2078403064568526971L;

	public NetworkProtocolEvent(Object source, String resourceKey,
			Session session, NetworkProtocol resource) {
		super(source, resourceKey, true, session, resource);
		addAttributes(resource);
	}

	public NetworkProtocolEvent(Object source, String resourceKey,
			NetworkProtocol resource, Throwable e, Session session) {
		super(source, resourceKey, e, session, resource);
		addAttributes(resource);
	}
	
	private void addAttributes(NetworkProtocol resource) {
		addAttribute(ATTR_PORTS, resource.getPortRange());
		addAttribute(ATTR_TRANSPORT, resource.getTransport().toString());
	}
	
	public String[] getResourceKeys() {
		return ArrayUtils.add(super.getResourceKeys(), EVENT_RESOURCE_KEY);
	}
}
