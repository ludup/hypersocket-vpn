package com.hypersocket.launcher.events;

import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.session.Session;

public class ApplicationLauncherResourceUpdatedEvent extends
		ApplicationLauncherResourceEvent {

	private static final long serialVersionUID = -268651453479469122L;

	public static final String EVENT_RESOURCE_KEY = "launcher.deleted";

	public ApplicationLauncherResourceUpdatedEvent(Object source,
			Session session, ApplicationLauncherResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public ApplicationLauncherResourceUpdatedEvent(Object source,
			ApplicationLauncherResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
