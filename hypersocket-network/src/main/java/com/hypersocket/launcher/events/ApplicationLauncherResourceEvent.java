package com.hypersocket.launcher.events;

import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.resource.ResourceEvent;
import com.hypersocket.session.Session;

public class ApplicationLauncherResourceEvent extends ResourceEvent {

	private static final long serialVersionUID = -6288441923228736221L;

	public ApplicationLauncherResourceEvent(Object source, String resourceKey,
			Session session, ApplicationLauncherResource resource) {
		super(source, resourceKey, true, session, resource);
	}

	public ApplicationLauncherResourceEvent(Object source, String resourceKey,
			ApplicationLauncherResource resource, Throwable e, Session session) {
		super(source, resourceKey, resource, e, session);
		

	}

}
