package com.hypersocket.websites.events;

import com.hypersocket.session.Session;
import com.hypersocket.websites.WebsiteResource;

public class WebsiteResourceDeletedEvent extends
		WebsiteResourceEvent {

	private static final long serialVersionUID = -8250366873232451152L;

	public static final String EVENT_RESOURCE_KEY = "website.deleted";

	public WebsiteResourceDeletedEvent(Object source,
			Session session, WebsiteResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public WebsiteResourceDeletedEvent(Object source,
			WebsiteResource resource, Throwable e, Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
