package com.hypersocket.launcher;

import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceService;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;

public interface ApplicationLauncherResourceService extends
		AbstractResourceService<ApplicationLauncherResource> {

	/**
	 * TODO rename this class to match your entity. Modify updateResource,
	 * createResource methods to take parameters for each additional field you
	 * have defined in your entity.
	 */

	ApplicationLauncherResource updateResource(ApplicationLauncherResource resourceById, String name,
			String exe, String args, ApplicationLauncherOS os)
			throws ResourceChangeException, AccessDeniedException;

	ApplicationLauncherResource createResource(String name, Realm realm, String exe,
			String args, ApplicationLauncherOS os) throws ResourceCreationException,
			AccessDeniedException;

}
