package com.hypersocket.network.events;

import org.apache.commons.lang3.ArrayUtils;

import com.hypersocket.network.NetworkResource;
import com.hypersocket.network.NetworkResourceServiceImpl;
import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.resource.ResourceSessionEvent;
import com.hypersocket.session.Session;

public class NetworkResourceSessionEvent extends ResourceSessionEvent {

	private static final long serialVersionUID = 8248391425589891659L;

	public static final String EVENT_RESOURCE_KEY = "network.event";
	
	public static final String ATTR_PROTOCOL_NAME = "attr.protocolName";
	public static final String ATTR_DESTINATION_HOST = "attr.destinationHost";
	public static final String ATTR_DESTINATION_PORT = "attr.destinationPort";
	
	public NetworkResourceSessionEvent(Object source, String resourceKey,
			boolean success, NetworkResource resource, NetworkProtocol protocol, int actualPort, Session session) {
		super(source, resourceKey, success, session, resource);
		addAttribute(ATTR_PROTOCOL_NAME, protocol.getName());
		addAttribute(ATTR_DESTINATION_HOST, resource.resolveHostname());
		addAttribute(ATTR_DESTINATION_PORT, String.valueOf(actualPort));
	}

	public NetworkResourceSessionEvent(Object source, String resourceKey, NetworkResource resource, int actualPort, Throwable e,
			Session session) {
		super(source, resourceKey, resource.getName(), e, session);
		addAttribute(ATTR_DESTINATION_HOST, resource.resolveHostname());
		addAttribute(ATTR_DESTINATION_PORT, String.valueOf(actualPort));
	}

	@Override
	public String getResourceBundle() {
		return NetworkResourceServiceImpl.RESOURCE_BUNDLE;
	}

	public String[] getResourceKeys() {
		return ArrayUtils.add(super.getResourceKeys(), EVENT_RESOURCE_KEY);
	}
	
	public boolean isUsage() {
		return false;
	}
}
