package com.hypersocket.websites.events;

import org.apache.commons.lang3.ArrayUtils;

import com.hypersocket.resource.ResourceSessionEvent;
import com.hypersocket.session.Session;
import com.hypersocket.websites.WebsiteResource;
import com.hypersocket.websites.WebsiteResourceServiceImpl;

public abstract class WebsiteResourceSessionEvent extends ResourceSessionEvent {

	private static final long serialVersionUID = -3240036750533492092L;

	public static final String EVENT_RESOURCE_KEY = "websiteSession.event";
	
	public static final String ATTR_URL = "attr.url";
	
	public WebsiteResourceSessionEvent(Object source, String resourceKey,
			boolean success, WebsiteResource resource, Session session) {
		super(source, resourceKey, success, session, resource);
		addAttribute(ATTR_URL, resource.getLaunchUrl());
	}

	public WebsiteResourceSessionEvent(Object source, String resourceKey, WebsiteResource resource, Throwable e,
			Session session,String hostname) {
		super(source, resourceKey, resource.getName(), e, session);
		addAttribute(ATTR_URL, hostname);
	}

	@Override
	public String getResourceBundle() {
		return WebsiteResourceServiceImpl.RESOURCE_BUNDLE;
	}

	public String[] getResourceKeys() {
		return ArrayUtils.add(super.getResourceKeys(), EVENT_RESOURCE_KEY);
	}
}
