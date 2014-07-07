package com.hypersocket.launcher.events;

import com.hypersocket.launcher.LauncherResource;
import com.hypersocket.session.Session;

public class LauncherResourceCreatedEvent extends
		LauncherResourceEvent {

	private static final long serialVersionUID = -5171604642969111588L;

	public static final String EVENT_RESOURCE_KEY = "launcher.created";
	
	public LauncherResourceCreatedEvent(Object source,
			Session session,
			LauncherResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public LauncherResourceCreatedEvent(Object source,
			LauncherResource resource, Throwable e,
			Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
