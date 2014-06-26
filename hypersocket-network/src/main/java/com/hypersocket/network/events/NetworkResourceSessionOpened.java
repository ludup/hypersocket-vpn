package com.hypersocket.network.events;

import com.hypersocket.network.NetworkProtocol;
import com.hypersocket.network.NetworkResource;
import com.hypersocket.session.Session;

public class NetworkResourceSessionOpened extends NetworkResourceSessionEvent {

	private static final long serialVersionUID = -7729337279204702387L;

	public static final String EVENT_RESOURCE_KEY = "networkResource.sessionOpened";
	
	public NetworkResourceSessionOpened(Object source,
			boolean success, NetworkResource resource, Session session, Integer actualPort, NetworkProtocol protocol) {
		super(source, EVENT_RESOURCE_KEY, success, resource, protocol, actualPort, session);

	}

	public NetworkResourceSessionOpened(Object source,
			Throwable e, NetworkResource resource, Session session, Integer actualPort) {
		super(source, EVENT_RESOURCE_KEY, resource, actualPort, e, session);
	}

}
