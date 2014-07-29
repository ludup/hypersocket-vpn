package com.hypersocket.protocols.events;

import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.session.Session;

public class NetworkProtocolDeletedEvent extends
		NetworkProtocolEvent {

	private static final long serialVersionUID = 6278519037098771491L;
	
	public static final String EVENT_RESOURCE_KEY = "networkProtocol.deleted";

	public NetworkProtocolDeletedEvent(Object source,
			Session session, NetworkProtocol resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public NetworkProtocolDeletedEvent(Object source,
			NetworkProtocol resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
