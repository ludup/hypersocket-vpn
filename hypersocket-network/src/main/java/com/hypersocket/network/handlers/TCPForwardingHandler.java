/*******************************************************************************
 * Copyright (c) 2013 LogonBox Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network.handlers;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hypersocket.events.EventService;
import com.hypersocket.network.NetworkResource;
import com.hypersocket.network.NetworkResourceService;
import com.hypersocket.network.events.NetworkResourceSessionClosed;
import com.hypersocket.network.events.NetworkResourceSessionOpened;
import com.hypersocket.server.HypersocketServer;
import com.hypersocket.server.forward.AbstractForwardingHandler;
import com.hypersocket.server.forward.ForwardingService;
import com.hypersocket.server.forward.ForwardingTransport;
import com.hypersocket.session.Session;

@Component
public class TCPForwardingHandler extends
		AbstractForwardingHandler<NetworkResource> {

	static Logger log = LoggerFactory.getLogger(TCPForwardingHandler.class);

	@Autowired
	NetworkResourceService networkService;

	@Autowired
	EventService eventService; 
	
	@Autowired
	HypersocketServer server;
	
	public TCPForwardingHandler() {
		super("tunnel");
	}

	@PostConstruct
	private void postConstruct() {
		server.registerWebsocketpHandler(this);
	}

	@Override
	protected ForwardingService<NetworkResource> getService() {
		return networkService;
	}

	@Override
	protected void fireResourceOpenSuccessEvent(Session session,
			NetworkResource resource, String hostname, Integer port) {
		eventService.publishEvent(new NetworkResourceSessionOpened(this, true,
				resource, session, port, resource.getNetworkProtocol(port,
						ForwardingTransport.TCP)));
	}

	@Override
	protected void fireResourceSessionOpenFailedEvent(Throwable cause,
			Session session, NetworkResource resource, String hostname,
			Integer port) {
		eventService.publishEvent(new NetworkResourceSessionOpened(this, cause,
				resource, session, port));

	}

	@Override
	protected void fireResourceSessionClosedEvent(NetworkResource resource,
			Session session, String hostname, Integer port, long totalBytesIn,
			long totalBytesOut) {
		eventService.publishEvent(new NetworkResourceSessionClosed(this,
				resource, resource.getNetworkProtocol(port,
						ForwardingTransport.TCP), session, totalBytesIn,
				totalBytesOut, port));

	}
}
