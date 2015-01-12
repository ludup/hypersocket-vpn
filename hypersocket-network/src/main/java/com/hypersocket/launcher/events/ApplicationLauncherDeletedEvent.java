package com.hypersocket.launcher.events;

import org.apache.commons.lang3.ArrayUtils;

import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.session.Session;

public class ApplicationLauncherDeletedEvent extends
		ApplicationLauncherEvent {

	private static final long serialVersionUID = -6247451317077897460L;

	public static final String EVENT_RESOURCE_KEY = "launcher.deleted";

	public ApplicationLauncherDeletedEvent(Object source,
			Session session, ApplicationLauncherResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public ApplicationLauncherDeletedEvent(Object source,
			ApplicationLauncherResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

	public String[] getResourceKeys() {
		return ArrayUtils.add(super.getResourceKeys(), EVENT_RESOURCE_KEY);
	}
}
