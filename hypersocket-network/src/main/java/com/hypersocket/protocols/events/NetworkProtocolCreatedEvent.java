package com.hypersocket.protocols.events;

import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.session.Session;

public class NetworkProtocolCreatedEvent extends
		NetworkProtocolEvent {

	private static final long serialVersionUID = -5084025576793931478L;

	public static final String EVENT_RESOURCE_KEY = "networkProtocol.created";
	
	public NetworkProtocolCreatedEvent(Object source,
			Session session,
			NetworkProtocol resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public NetworkProtocolCreatedEvent(Object source,
			NetworkProtocol resource, Throwable e,
			Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
