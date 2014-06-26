package com.hypersocket.network.events;

import com.hypersocket.network.NetworkResource;
import com.hypersocket.session.Session;

public class NetworkResourceDeletedEvent extends NetworkResourceEvent {

	private static final long serialVersionUID = -4015096377848542087L;

	public static final String EVENT_RESOURCE_KEY ="networkResource.deleted";
	
	public NetworkResourceDeletedEvent(Object source, NetworkResource resource, Session session) {
		super(source, EVENT_RESOURCE_KEY, true, resource, session);
	}

	public NetworkResourceDeletedEvent(Object source,
			NetworkResource resource, Throwable e,
			Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
