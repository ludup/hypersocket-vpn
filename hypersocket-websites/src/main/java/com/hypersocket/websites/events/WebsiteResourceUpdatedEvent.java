package com.hypersocket.websites.events;

import com.hypersocket.session.Session;
import com.hypersocket.websites.WebsiteResource;

public class WebsiteResourceUpdatedEvent extends
		WebsiteResourceEvent {

	private static final long serialVersionUID = 3151997741366122070L;

	public static final String EVENT_RESOURCE_KEY = "website.deleted";

	public WebsiteResourceUpdatedEvent(Object source,
			Session session, WebsiteResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public WebsiteResourceUpdatedEvent(Object source,
			WebsiteResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
