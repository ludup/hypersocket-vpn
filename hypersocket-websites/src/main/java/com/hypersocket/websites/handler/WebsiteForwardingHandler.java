package com.hypersocket.websites.handler;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hypersocket.events.EventService;
import com.hypersocket.network.handlers.AbstractForwardingHandler;
import com.hypersocket.network.handlers.ForwardingService;
import com.hypersocket.server.HypersocketServer;
import com.hypersocket.session.Session;
import com.hypersocket.websites.WebsiteResource;
import com.hypersocket.websites.WebsiteResourceService;
import com.hypersocket.websites.events.WebsiteResourceSessionClosed;
import com.hypersocket.websites.events.WebsiteResourceSessionOpened;

@Component
public class WebsiteForwardingHandler extends
		AbstractForwardingHandler<WebsiteResource> {

	@Autowired
	WebsiteResourceService websiteService;

	@Autowired
	EventService eventService;

	@Autowired
	HypersocketServer server;

	public WebsiteForwardingHandler() {
		super("website");
	}

	@PostConstruct
	private void postConstruct() {
		server.registerWebsocketpHandler(this);
	}

	@Override
	protected ForwardingService<WebsiteResource> getService() {
		return websiteService;
	}

	@Override
	protected void fireResourceOpenSuccessEvent(Session session,
			WebsiteResource resource, String hostname, Integer port) {
		eventService.publishEvent(new WebsiteResourceSessionOpened(this, true,
				resource, session));
	}

	@Override
	protected void fireResourceSessionOpenFailedEvent(Throwable cause,
			Session session, WebsiteResource resource, String hostname,
			Integer port) {
		eventService.publishEvent(new WebsiteResourceSessionOpened(this, cause,
				resource, session, hostname));
	}

	@Override
	protected void fireResourceSessionClosedEvent(WebsiteResource resource,
			Session session, String hostname, Integer port, long totalBytesIn,
			long totalBytesOut) {
		eventService.publishEvent(new WebsiteResourceSessionClosed(this,
				resource, session, totalBytesIn, totalBytesOut));
	}

}
