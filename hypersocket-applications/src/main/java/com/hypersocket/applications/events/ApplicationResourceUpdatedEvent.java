package com.hypersocket.applications.events;

import com.hypersocket.applications.ApplicationResource;
import com.hypersocket.session.Session;

public class ApplicationResourceUpdatedEvent extends ApplicationResourceEvent {

	private static final long serialVersionUID = -5736345757147793015L;

	public static final String EVENT_RESOURCE_KEY = "<resource>.deleted";

	public ApplicationResourceUpdatedEvent(Object source, Session session,
			ApplicationResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public ApplicationResourceUpdatedEvent(Object source,
			ApplicationResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
