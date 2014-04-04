package com.hypersocket.network.events;

import com.hypersocket.network.NetworkResourceService;
import com.hypersocket.session.Session;
import com.hypersocket.session.events.SessionEvent;

public class NetworkResourceEvent extends SessionEvent {

	private static final long serialVersionUID = 8248391425589891659L;

	public NetworkResourceEvent(Object source, String resourceKey,
			boolean success, Session session) {
		super(source, resourceKey, success, session);
	}

	public NetworkResourceEvent(Object source, String resourceKey, Throwable e,
			Session session) {
		super(source, resourceKey, e, session);
	}

	@Override
	public String getResourceBundle() {
		return NetworkResourceService.RESOURCE_BUNDLE;
	}

}
