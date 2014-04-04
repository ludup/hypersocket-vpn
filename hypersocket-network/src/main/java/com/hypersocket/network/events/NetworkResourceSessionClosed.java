package com.hypersocket.network.events;

import com.hypersocket.network.NetworkResource;
import com.hypersocket.session.Session;

public class NetworkResourceSessionClosed extends NetworkResourceEvent {

	private static final long serialVersionUID = -584310921887757202L;

	public NetworkResourceSessionClosed(Object source,
			NetworkResource resource, Session session, long totalBytesIn,
			long totalBytesOut) {
		super(source, "networkResource.sessionClosed", true, session);
	}

	public NetworkResourceSessionClosed(Object source, Throwable e,
			NetworkResource resource, Session session) {
		super(source, "networkResource.sessionClosed", e, session);
	}

}
