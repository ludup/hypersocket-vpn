package com.hypersocket.network.events;

import com.hypersocket.network.NetworkResource;
import com.hypersocket.network.NetworkResourceServiceImpl;
import com.hypersocket.resource.AssignableResourceEvent;
import com.hypersocket.session.Session;

public class NetworkResourceEvent extends AssignableResourceEvent {

	private static final long serialVersionUID = 8248391425589891659L;

	
	public static final String ATTR_PROTOCOLS = "attr.protocols";
	public static final String ATTR_HOSTNAME = "attr.destinationHost";
	public static final String ATTR_DESTINATION_HOST = "attr.destinationHost";
	
	
	public NetworkResourceEvent(Object source, String resourceKey,
			boolean success, NetworkResource resource, Session session) {
		super(source, resourceKey, success, session, resource);
		addAttribute(ATTR_HOSTNAME, resource.getHostname());
		addAttribute(ATTR_DESTINATION_HOST, resource.getDestinationHostname());
		addAttribute(ATTR_PROTOCOLS, resource.getProtocolsDesc());
	}

	public NetworkResourceEvent(Object source, String resourceKey, NetworkResource resource, Throwable e,
			Session session) {
		super(source, resourceKey, resource, e, session);
		addAttribute(ATTR_HOSTNAME, resource.getHostname());
		addAttribute(ATTR_DESTINATION_HOST, resource.getDestinationHostname());
		addAttribute(ATTR_PROTOCOLS, resource.getProtocolsDesc());
	}

	@Override
	public String getResourceBundle() {
		return NetworkResourceServiceImpl.RESOURCE_BUNDLE;
	}

}
