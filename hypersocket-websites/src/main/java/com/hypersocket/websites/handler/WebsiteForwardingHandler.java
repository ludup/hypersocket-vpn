package com.hypersocket.websites.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hypersocket.network.handlers.AbstractForwardingHandler;
import com.hypersocket.network.handlers.ForwardingService;
import com.hypersocket.session.Session;
import com.hypersocket.websites.WebsiteResource;
import com.hypersocket.websites.WebsiteResourceService;

@Component
public class WebsiteForwardingHandler extends AbstractForwardingHandler<WebsiteResource> {

	@Autowired
	WebsiteResourceService websiteService; 
	
	public WebsiteForwardingHandler() {
		super("website");
	}

	@Override
	protected ForwardingService<WebsiteResource> getService() {
		return websiteService;
	}

	@Override
	protected void fireResourceOpenSuccessEvent(Session session, WebsiteResource resource,
			String hostname, Integer port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fireResourceSessionOpenFailedEvent(Throwable cause,
			Session session, WebsiteResource resource, String hostname, Integer port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fireResourceSessionClosedEvent(WebsiteResource resource, Session session,
			String hostname, Integer port, long totalBytesIn, long totalBytesOut) {
		// TODO Auto-generated method stub
		
	}

}
