package com.hypersocket.htmlapplications;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.hypersocket.applications.ApplicationResourceService;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.Role;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractAssignableResourceService;
import com.hypersocket.resource.ResourceException;


public interface HTMLApplicationResourceService extends
		AbstractAssignableResourceService<HTMLApplicationResource>, ApplicationResourceService<HTMLApplicationResource, HTMLApplicationTemplate> {

	HTMLApplicationResource updateResource(HTMLApplicationResource resourceById, String name,
			Set<Role> roles, Map<String,String> properties) throws ResourceException, AccessDeniedException;

	HTMLApplicationResource createResource(String name, Set<Role> roles, Realm realm, Map<String,String> properties)
			throws ResourceException, AccessDeniedException;

	Collection<PropertyCategory> getPropertyTemplate(
			HTMLApplicationResource resource) throws AccessDeniedException;

	Collection<PropertyCategory> getPropertyTemplate()
			throws AccessDeniedException;


}
