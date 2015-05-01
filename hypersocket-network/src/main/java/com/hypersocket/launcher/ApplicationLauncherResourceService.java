package com.hypersocket.launcher;

import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceService;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;

public interface ApplicationLauncherResourceService extends
		AbstractResourceService<ApplicationLauncherResource> {

	

	ApplicationLauncherResource updateResource(ApplicationLauncherResource resourceById, String name,
			String exe, String args, ApplicationLauncherOS os, String startupScript, String shutdownScript)
			throws ResourceChangeException, AccessDeniedException;

	ApplicationLauncherResource createResource(String name, Realm realm,
			String exe, String args, ApplicationLauncherOS os,
			String startupScript, String shutdownScript)
			throws ResourceCreationException, AccessDeniedException;

}
