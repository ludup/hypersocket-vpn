package com.hypersocket.htmlapplications.events;

import org.apache.commons.lang3.ArrayUtils;

import com.hypersocket.htmlapplications.HTMLApplicationResource;
import com.hypersocket.session.Session;

@SuppressWarnings("serial")
public class HTMLApplicationResourceDeletedEvent extends
		HTMLApplicationResourceEvent {

	/**
	 * TODO rename to suit your resource and replace hTMLApplication with lower case
	 * name of your resource.
	 * 
	 * You typically add attributes to the base HTMLApplicationResourceEvent
	 * class so these can be reused across all resource events.
	 */
	public static final String EVENT_RESOURCE_KEY = "hTMLApplication.deleted";

	public HTMLApplicationResourceDeletedEvent(Object source,
			Session session, HTMLApplicationResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public HTMLApplicationResourceDeletedEvent(Object source,
			HTMLApplicationResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

	public String[] getResourceKeys() {
		return ArrayUtils.add(super.getResourceKeys(), EVENT_RESOURCE_KEY);
	}
}
