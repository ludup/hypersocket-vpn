/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network;

import java.util.List;

import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractAssignableResourceRepository;
import com.hypersocket.resource.ResourceChangeException;

public interface NetworkResourceRepository extends
		AbstractAssignableResourceRepository<NetworkResource> {

	void saveResource(NetworkResource resource);

	NetworkResource getResourceById(Long id);

	void deleteResource(NetworkResource resource) throws ResourceChangeException;

	List<NetworkResource> getResources(Realm realm);

	NetworkResource getResourceByName(String name);

	NetworkResource getResourceByName(String name, boolean deleted);
}
