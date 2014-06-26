package com.hypersocket.network.events;

import com.hypersocket.network.NetworkResource;
import com.hypersocket.session.Session;

public class NetworkResourceCreatedEvent extends NetworkResourceEvent {

	private static final long serialVersionUID = -4880399039322821067L;

	public static final String EVENT_RESOURCE_KEY ="networkResource.created";
	
	public NetworkResourceCreatedEvent(Object source, NetworkResource resource, Session session) {
		super(source, EVENT_RESOURCE_KEY, true, resource, session);
	}

	public NetworkResourceCreatedEvent(Object source,
			NetworkResource resource, Throwable e,
			Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
