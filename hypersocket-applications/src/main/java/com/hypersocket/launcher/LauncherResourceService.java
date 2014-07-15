package com.hypersocket.launcher;

import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceService;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;

public interface LauncherResourceService extends
		AbstractResourceService<LauncherResource> {

	/**
	 * TODO rename this class to match your entity. Modify updateResource,
	 * createResource methods to take parameters for each additional field you
	 * have defined in your entity.
	 */

	LauncherResource updateResource(LauncherResource resourceById, String name,
			String exe, String args, LauncherOS os)
			throws ResourceChangeException, AccessDeniedException;

	LauncherResource createResource(String name, Realm realm, String exe,
			String args, LauncherOS os) throws ResourceCreationException,
			AccessDeniedException;

}
