/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network.handlers;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hypersocket.auth.AuthenticationService;
import com.hypersocket.auth.json.UnauthorizedException;
import com.hypersocket.events.EventService;
import com.hypersocket.network.NetworkProtocol;
import com.hypersocket.network.NetworkResource;
import com.hypersocket.network.NetworkResourceService;
import com.hypersocket.network.NetworkTransport;
import com.hypersocket.network.events.NetworkResourceSessionClosed;
import com.hypersocket.network.events.NetworkResourceSessionOpened;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.server.HypersocketServer;
import com.hypersocket.server.handlers.HttpResponseProcessor;
import com.hypersocket.server.websocket.TCPForwardingClientCallback;
import com.hypersocket.server.websocket.WebsocketClient;
import com.hypersocket.server.websocket.WebsocketClientCallback;
import com.hypersocket.session.ResourceSession;
import com.hypersocket.session.Session;
import com.hypersocket.session.SessionService;
import com.hypersocket.session.json.SessionUtils;

@Component
public class TCPForwardingHandler extends
		AbstractForwardingHandler<NetworkResource> {

	static Logger log = LoggerFactory.getLogger(TCPForwardingHandler.class);

	@Autowired
	NetworkResourceService networkService;

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
						NetworkTransport.TCP)));
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
						NetworkTransport.TCP), session, totalBytesIn,
				totalBytesOut, port));

	}
}
