package com.hypersocket.launcher.events;

import com.hypersocket.launcher.LauncherResource;
import com.hypersocket.session.Session;

public class LauncherResourceUpdatedEvent extends
		LauncherResourceEvent {

	private static final long serialVersionUID = -268651453479469122L;

	public static final String EVENT_RESOURCE_KEY = "launcher.deleted";

	public LauncherResourceUpdatedEvent(Object source,
			Session session, LauncherResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public LauncherResourceUpdatedEvent(Object source,
			LauncherResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
