package com.hypersocket.launcher.events;

import org.apache.commons.lang3.ArrayUtils;

import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.realm.events.ResourceEvent;
import com.hypersocket.session.Session;

public class ApplicationLauncherEvent extends ResourceEvent {

	public static final String EVENT_RESOURCE_KEY = "launcher.event";
	
	private static final long serialVersionUID = -6288441923228736221L;

	public ApplicationLauncherEvent(Object source, String resourceKey,
			Session session, ApplicationLauncherResource resource) {
		super(source, resourceKey, true, session, resource);
	}

	public ApplicationLauncherEvent(Object source, String resourceKey,
			ApplicationLauncherResource resource, Throwable e, Session session) {
		super(source, resourceKey, e, session, resource);
	}
	
	public String[] getResourceKeys() {
		return ArrayUtils.add(super.getResourceKeys(), EVENT_RESOURCE_KEY);
	}

}
