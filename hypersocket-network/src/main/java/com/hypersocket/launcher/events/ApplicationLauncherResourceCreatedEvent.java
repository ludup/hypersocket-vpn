package com.hypersocket.launcher.events;

import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.session.Session;

public class ApplicationLauncherResourceCreatedEvent extends
		ApplicationLauncherResourceEvent {

	private static final long serialVersionUID = -5171604642969111588L;

	public static final String EVENT_RESOURCE_KEY = "launcher.created";
	
	public ApplicationLauncherResourceCreatedEvent(Object source,
			Session session,
			ApplicationLauncherResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public ApplicationLauncherResourceCreatedEvent(Object source,
			ApplicationLauncherResource resource, Throwable e,
			Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
