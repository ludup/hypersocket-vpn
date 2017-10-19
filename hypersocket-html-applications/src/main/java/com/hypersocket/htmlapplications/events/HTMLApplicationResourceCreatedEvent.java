package com.hypersocket.htmlapplications.events;

import org.apache.commons.lang3.ArrayUtils;

import com.hypersocket.htmlapplications.HTMLApplicationResource;
import com.hypersocket.session.Session;

@SuppressWarnings("serial")
public class HTMLApplicationResourceCreatedEvent extends HTMLApplicationResourceEvent {

	public static final String EVENT_RESOURCE_KEY = "hTMLApplication.created";

	public HTMLApplicationResourceCreatedEvent(Object source, Session session, HTMLApplicationResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public HTMLApplicationResourceCreatedEvent(Object source, HTMLApplicationResource resource, Throwable e,
			Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

	public String[] getResourceKeys() {
		return ArrayUtils.add(super.getResourceKeys(), EVENT_RESOURCE_KEY);
	}
}
