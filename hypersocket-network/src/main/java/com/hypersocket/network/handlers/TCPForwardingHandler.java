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
import com.hypersocket.server.handlers.WebsocketHandler;
import com.hypersocket.server.websocket.TCPForwardingClientCallback;
import com.hypersocket.server.websocket.WebsocketClient;
import com.hypersocket.server.websocket.WebsocketClientCallback;
import com.hypersocket.session.ResourceSession;
import com.hypersocket.session.Session;
import com.hypersocket.session.SessionService;
import com.hypersocket.session.json.SessionUtils;

@Component
public class TCPForwardingHandler implements WebsocketHandler {

	static Logger log = LoggerFactory.getLogger(TCPForwardingHandler.class);

	@Autowired
	SessionService sessionService;

	@Autowired
	SessionUtils sessionUtils;

	@Autowired
	AuthenticationService authenticationService;

	@Autowired
	NetworkResourceService networkService;

	@Autowired
	HypersocketServer server;

	@Autowired
	EventService eventService;

	public TCPForwardingHandler() {

	}

	@PostConstruct
	public void postConstruct() {
		server.registerWebsocketpHandler(this);
	}

	@Override
	public boolean handlesRequest(HttpServletRequest request) {
		return request.getRequestURI().startsWith(server.resolvePath("tunnel"));
	}

	@Override
	public void acceptWebsocket(HttpServletRequest request,
			HttpServletResponse response, WebsocketClientCallback callback,
			HttpResponseProcessor processor) throws UnauthorizedException,
			AccessDeniedException {

		if (!sessionService.isLoggedOn(sessionUtils.getActiveSession(request),
				true)) {
			throw new UnauthorizedException();
		}

		Session session = sessionUtils.getActiveSession(request);

		networkService.setCurrentSession(session,
				sessionUtils.getLocale(request));

		try {
			Long resourceId = Long
					.parseLong(request.getParameter("resourceId"));

			Integer port = Integer.parseInt(request.getParameter("port"));

			NetworkResource resource = networkService.getResourceById(resourceId);
			NetworkProtocol protocol = networkService.verifyResourceSession(
					resource, port, NetworkTransport.TCP, session);

			server.connect(new TCPForwardingHandlerCallback(callback, session,
					resource, protocol, port));

		} catch(AccessDeniedException ex) { 
			// TODO Log event
			throw ex;
		} catch (ResourceNotFoundException e) {
			// TODO Log event
			throw new AccessDeniedException("Resource not found");
		} finally {
			networkService.clearPrincipalContext();
		}
	}

	class TCPForwardingHandlerCallback implements TCPForwardingClientCallback {

		WebsocketClientCallback callback;
		NetworkResource resource;
		NetworkProtocol protocol;
		Session session;
		Integer port;
		ResourceSession<NetworkResource> resourceSession;
		
		TCPForwardingHandlerCallback(WebsocketClientCallback callback,
				Session session, NetworkResource resource, NetworkProtocol protocol, Integer port) {
			this.callback = callback;
			this.resource = resource;
			this.protocol = protocol;
			this.session = session;
			this.port = port;
		}

		@Override
		public void websocketAccepted(final WebsocketClient client) {

			callback.websocketAccepted(client);

			if(!sessionService.hasResourceSession(session, resource)) {
				eventService.publishEvent(new NetworkResourceSessionOpened(
					TCPForwardingHandler.this, true, resource, session, port, protocol));
			}
		
			resourceSession = new ResourceSession<NetworkResource>() {
				@Override
				public void close() {
					client.close();
				}
				@Override
				public NetworkResource getResource() {
					return resource;
				}
			};
			
			sessionService.registerResourceSession(session, resourceSession);
		}

		@Override
		public void websocketRejected(Throwable cause) {

			callback.websocketRejected(cause);

			eventService.publishEvent(new NetworkResourceSessionOpened(
					TCPForwardingHandler.this, cause, resource, session, port));
		}

		@Override
		public void websocketClosed(WebsocketClient client) {

			callback.websocketClosed(client);
			
			sessionService.unregisterResourceSession(session, resourceSession);
			
			if(!sessionService.hasResourceSession(session, resource)) {
				eventService.publishEvent(new NetworkResourceSessionClosed(
						TCPForwardingHandler.this, resource, protocol, session, client
								.getTotalBytesIn(), client.getTotalBytesOut(), port));
			}
		}

		public int getPort() {
			return port;
		}

		public String getHostname() {
			String hostname = resource.getDestinationHostname();
			if (hostname == null || hostname.equals("")) {
				hostname = resource.getHostname();
			}
			return hostname;
		}

		@Override
		public String getResourceBundle() {
			return NetworkResourceService.RESOURCE_BUNDLE;
		}

		@Override
		public String getResourceKey() {
			return "networkResources";
		}
	}

	@Override
	public String getResourceKey() {
		return NetworkResourceService.RESOURCE_BUNDLE;
	}

	@Override
	public String getResourceBundle() {
		return "networkResources";
	}

}
