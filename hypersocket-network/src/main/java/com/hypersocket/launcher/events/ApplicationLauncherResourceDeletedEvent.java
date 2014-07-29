package com.hypersocket.launcher.events;

import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.session.Session;

public class ApplicationLauncherResourceDeletedEvent extends
		ApplicationLauncherResourceEvent {

	private static final long serialVersionUID = -6247451317077897460L;

	public static final String EVENT_RESOURCE_KEY = "launcher.deleted";

	public ApplicationLauncherResourceDeletedEvent(Object source,
			Session session, ApplicationLauncherResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public ApplicationLauncherResourceDeletedEvent(Object source,
			ApplicationLauncherResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
