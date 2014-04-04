/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network;

import java.util.List;
import java.util.Set;

import com.hypersocket.auth.AuthenticatedService;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.Role;
import com.hypersocket.realm.Principal;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.session.Session;


public interface NetworkResourceService extends AuthenticatedService {

	static final String RESOURCE_BUNDLE = "NetworkResourceService";
	
	List<NetworkResource> getResources(Principal principal) throws AccessDeniedException;

	List<NetworkProtocol> getProtocols();

	NetworkProtocol getProtocolById(Long id) throws ResourceNotFoundException;

	NetworkProtocol updateProtocol(NetworkProtocol protocolById, String name,
			NetworkTransport valueOf, Integer startPort, Integer endPort) throws ResourceChangeException, AccessDeniedException;

	NetworkProtocol createProtocol(String name, NetworkTransport valueOf,
			Integer startPort, Integer endPort) throws ResourceCreationException, AccessDeniedException;

	void deleteProtocol(NetworkProtocol protocol) throws ResourceChangeException, AccessDeniedException;

	NetworkResource getResourceById(Long id) throws ResourceNotFoundException;

	NetworkResource updateResource(NetworkResource resourceById, String name,
			String hostname, String destinationHostname, Set<NetworkProtocol> protocols, Set<Role> roles) throws ResourceChangeException, AccessDeniedException;

	NetworkResource createResource(String name, String hostname, String destinationHostname,
			Set<NetworkProtocol> protocols, Set<Role> roles, Realm realm) throws ResourceCreationException, AccessDeniedException;

	void deleteResource(NetworkResource resource) throws ResourceChangeException, AccessDeniedException;

	List<NetworkResource> getResources(Realm realm) throws AccessDeniedException;

	NetworkProtocol verifyResourceSession(NetworkResource resource, Integer port,
			NetworkTransport transport, Session session)
			throws AccessDeniedException;
}
