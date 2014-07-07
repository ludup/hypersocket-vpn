package com.hypersocket.launcher.events;

import com.hypersocket.launcher.LauncherResource;
import com.hypersocket.session.Session;

public class LauncherResourceDeletedEvent extends
		LauncherResourceEvent {

	private static final long serialVersionUID = -6247451317077897460L;

	public static final String EVENT_RESOURCE_KEY = "launcher.deleted";

	public LauncherResourceDeletedEvent(Object source,
			Session session, LauncherResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public LauncherResourceDeletedEvent(Object source,
			LauncherResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
