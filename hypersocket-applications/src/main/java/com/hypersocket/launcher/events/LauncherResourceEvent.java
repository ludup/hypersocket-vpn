package com.hypersocket.launcher.events;

import com.hypersocket.launcher.LauncherResource;
import com.hypersocket.resource.ResourceEvent;
import com.hypersocket.session.Session;

public class LauncherResourceEvent extends ResourceEvent {

	private static final long serialVersionUID = -6288441923228736221L;

	public LauncherResourceEvent(Object source, String resourceKey,
			Session session, LauncherResource resource) {
		super(source, resourceKey, true, session, resource);
	}

	public LauncherResourceEvent(Object source, String resourceKey,
			LauncherResource resource, Throwable e, Session session) {
		super(source, resourceKey, resource, e, session);
		

	}

}
