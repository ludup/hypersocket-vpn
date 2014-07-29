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

import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.network.handlers.ForwardingService;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.Role;
import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.realm.Principal;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractAssignableResourceService;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceNotFoundException;

public interface NetworkResourceService extends
		ForwardingService<NetworkResource>,
		AbstractAssignableResourceService<NetworkResource> {

	static final String RESOURCE_BUNDLE = "NetworkResourceService";

	List<NetworkResource> getResources(Principal principal)
			throws AccessDeniedException;

	NetworkResource getResourceById(Long id) throws ResourceNotFoundException;

	void deleteResource(NetworkResource resource)
			throws ResourceChangeException, AccessDeniedException;

	List<NetworkResource> getResources(Realm realm)
			throws AccessDeniedException;

	NetworkResource updateResource(NetworkResource resourceById, String name,
			String hostname, String destinationHostname,
			Set<NetworkProtocol> protocols,
			Set<ApplicationLauncherResource> launchers, Set<Role> roles)
			throws ResourceChangeException, AccessDeniedException;

	NetworkResource createResource(String name, String hostname,
			String destinationHostname, Set<NetworkProtocol> protocols,
			Set<ApplicationLauncherResource> launchers, Set<Role> roles,
			Realm realm) throws ResourceCreationException,
			AccessDeniedException;

}
