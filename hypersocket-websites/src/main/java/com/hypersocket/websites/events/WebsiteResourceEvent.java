package com.hypersocket.websites.events;

import org.apache.commons.lang3.ArrayUtils;

import com.hypersocket.resource.AssignableResourceEvent;
import com.hypersocket.session.Session;
import com.hypersocket.websites.WebsiteResource;

public class WebsiteResourceEvent extends AssignableResourceEvent {

	public static final String ATTR_LAUNCH_URL = "attr.launchUrl";
	public static final String ATTR_ADDITIONAL_URLS = "attr.additionalUrls";
	
	public static final String EVENT_RESOURCE_KEY = "website.event";
	
	private static final long serialVersionUID = 9037257765175335624L;

	public WebsiteResourceEvent(Object source, String resourceKey,
			Session session, WebsiteResource resource) {
		super(source, resourceKey, true, session, resource);
		addAttributes(resource);
	}

	public WebsiteResourceEvent(Object source, String resourceKey,
			WebsiteResource resource, Throwable e, Session session) {
		super(source, resourceKey, resource, e, session);
		addAttributes(resource);
	}
	
	private void addAttributes(WebsiteResource resource) {
		addAttribute(ATTR_LAUNCH_URL, resource.getLaunchUrl());
		addAttribute(ATTR_ADDITIONAL_URLS, resource.getAdditionalUrls().replace("]|[", "\r\n"));
	}

	public String[] getResourceKeys() {
		return ArrayUtils.add(super.getResourceKeys(), EVENT_RESOURCE_KEY);
	}
}
