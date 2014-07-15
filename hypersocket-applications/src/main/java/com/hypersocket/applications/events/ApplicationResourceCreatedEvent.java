package com.hypersocket.applications.events;

import com.hypersocket.applications.ApplicationResource;
import com.hypersocket.session.Session;

public class ApplicationResourceCreatedEvent extends
		ApplicationResourceEvent {

	private static final long serialVersionUID = -1745595723722901262L;

	public static final String EVENT_RESOURCE_KEY = "application.created";
	
	public ApplicationResourceCreatedEvent(Object source,
			Session session,
			ApplicationResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public ApplicationResourceCreatedEvent(Object source,
			ApplicationResource resource, Throwable e,
			Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
