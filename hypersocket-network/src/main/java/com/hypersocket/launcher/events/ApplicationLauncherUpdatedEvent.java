package com.hypersocket.launcher.events;

import org.apache.commons.lang3.ArrayUtils;

import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.session.Session;

public class ApplicationLauncherUpdatedEvent extends
		ApplicationLauncherEvent {

	private static final long serialVersionUID = -268651453479469122L;

	public static final String EVENT_RESOURCE_KEY = "launcher.updated";

	public ApplicationLauncherUpdatedEvent(Object source,
			Session session, ApplicationLauncherResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public ApplicationLauncherUpdatedEvent(Object source,
			ApplicationLauncherResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

	public String[] getResourceKeys() {
		return ArrayUtils.add(super.getResourceKeys(), EVENT_RESOURCE_KEY);
	}
}
