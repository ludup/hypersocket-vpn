/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network;

import java.util.Set;

import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.Role;
import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractAssignableResourceService;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.server.forward.ForwardingService;

public interface NetworkResourceService extends
		ForwardingService<NetworkResource>,
		AbstractAssignableResourceService<NetworkResource> {

	NetworkResource updateResource(NetworkResource resourceById, String name,
			String hostname, String destinationHostname,
			Set<NetworkProtocol> protocols,
			Set<ApplicationLauncherResource> launchers, Set<Role> roles, String logo)
			throws ResourceException, AccessDeniedException;

	NetworkResource createResource(String name, String hostname,
			String destinationHostname, Set<NetworkProtocol> protocols,
			Set<ApplicationLauncherResource> launchers, Set<Role> roles,
			Realm realm, String logo) throws
			AccessDeniedException, ResourceException;
	
	NetworkResource getResourceById(Long id) throws ResourceNotFoundException, AccessDeniedException;

}
