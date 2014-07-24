package com.hypersocket.websites.events;

import com.hypersocket.session.Session;
import com.hypersocket.websites.WebsiteResource;

public class WebsiteResourceCreatedEvent extends
		WebsiteResourceEvent {

	private static final long serialVersionUID = 5728049902239520347L;

	public static final String EVENT_RESOURCE_KEY = "website.created";
	
	public WebsiteResourceCreatedEvent(Object source,
			Session session,
			WebsiteResource resource) {
		super(source, EVENT_RESOURCE_KEY, session, resource);
	}

	public WebsiteResourceCreatedEvent(Object source,
			WebsiteResource resource, Throwable e,
			Session session) {
		super(source, EVENT_RESOURCE_KEY, resource, e, session);
	}

}
