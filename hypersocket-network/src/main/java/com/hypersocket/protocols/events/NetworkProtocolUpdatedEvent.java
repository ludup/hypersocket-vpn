package com.hypersocket.protocols.events;

import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.session.Session;

public class NetworkProtocolUpdatedEvent extends
		NetworkProtocolEvent {

	private static final long serialVersionUID = 5470084618146238293L;

	public static final String EVENT_RESOURCE_KEY = "networkProtocol.updated";

	public NetworkProtocolUpdatedEvent(Object source,
			Session session, NetworkProtocol resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public NetworkProtocolUpdatedEvent(Object source,
			NetworkProtocol resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
