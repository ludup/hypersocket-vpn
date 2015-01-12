package com.hypersocket.network.events;

import org.apache.commons.lang3.ArrayUtils;

import com.hypersocket.network.NetworkResource;
import com.hypersocket.session.Session;

public class NetworkResourceUpdatedEvent extends NetworkResourceEvent {

	private static final long serialVersionUID = -6393313391992539374L;
	
	public static final String EVENT_RESOURCE_KEY ="networkResource.updated";
	
	public NetworkResourceUpdatedEvent(Object source, NetworkResource resource, Session session) {
		super(source, EVENT_RESOURCE_KEY, true, resource, session);
	}

	public NetworkResourceUpdatedEvent(Object source,
			NetworkResource resource, Throwable e,
			Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

	public String[] getResourceKeys() {
		return ArrayUtils.add(super.getResourceKeys(), EVENT_RESOURCE_KEY);
	}
}
