package com.hypersocket.websites.events;

import com.hypersocket.resource.ResourceEvent;
import com.hypersocket.session.Session;
import com.hypersocket.websites.WebsiteResource;
import com.hypersocket.websites.WebsiteResourceServiceImpl;

public class WebsiteResourceSessionEvent extends ResourceEvent {

	private static final long serialVersionUID = -3240036750533492092L;

	public static final String ATTR_URL = "attr.url";
	
	public WebsiteResourceSessionEvent(Object source, String resourceKey,
			boolean success, WebsiteResource resource, Session session) {
		super(source, resourceKey, success, session, resource);
		addAttribute(ATTR_URL, resource.getLaunchUrl());
	}

	public WebsiteResourceSessionEvent(Object source, String resourceKey, WebsiteResource resource, Throwable e,
			Session session) {
		super(source, resourceKey, resource, e, session);
		addAttribute(ATTR_URL, resource.getLaunchUrl());
	}

	@Override
	public String getResourceBundle() {
		return WebsiteResourceServiceImpl.RESOURCE_BUNDLE;
	}

}
