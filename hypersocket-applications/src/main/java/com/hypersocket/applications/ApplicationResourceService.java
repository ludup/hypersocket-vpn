package com.hypersocket.applications;

import java.util.Set;

import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.Role;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractAssignableResourceService;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;


public interface ApplicationResourceService extends
		AbstractAssignableResourceService<ApplicationResource> {

	/**
	 * TODO rename this class to match your entity. Modify updateResource, createResource methods
	 * to take parameters for each additional field you have defined in your entity. 
	 */
	
	ApplicationResource updateResource(ApplicationResource resourceById, String name,
			Set<Role> roles) throws ResourceChangeException, AccessDeniedException;

	ApplicationResource createResource(String name, Set<Role> roles, Realm realm)
			throws ResourceCreationException, AccessDeniedException;


}
