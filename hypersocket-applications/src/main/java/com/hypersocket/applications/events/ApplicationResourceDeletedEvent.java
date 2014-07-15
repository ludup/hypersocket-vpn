package com.hypersocket.applications.events;

import com.hypersocket.applications.ApplicationResource;
import com.hypersocket.session.Session;

public class ApplicationResourceDeletedEvent extends
		ApplicationResourceEvent {

	private static final long serialVersionUID = 8245669898740843827L;

	public static final String EVENT_RESOURCE_KEY = "application.deleted";

	public ApplicationResourceDeletedEvent(Object source,
			Session session, ApplicationResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public ApplicationResourceDeletedEvent(Object source,
			ApplicationResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
