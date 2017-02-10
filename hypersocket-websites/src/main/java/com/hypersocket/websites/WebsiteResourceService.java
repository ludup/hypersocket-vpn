package com.hypersocket.websites;

import java.util.Set;

import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.Role;
import com.hypersocket.resource.AbstractAssignableResourceService;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.server.forward.ForwardingService;

public interface WebsiteResourceService extends
		AbstractAssignableResourceService<WebsiteResource>, ForwardingService<WebsiteResource> {

	WebsiteResource updateResource(WebsiteResource resourceById, String name,
			String launchUrl, String additionalUrls, Set<Role> roles, String logo)
			throws ResourceException, AccessDeniedException;

	WebsiteResource createResource(String name, String launchUrl,
			String additionalUrls, Set<Role> roles, String logo)
			throws AccessDeniedException, ResourceException;
	
	WebsiteResource getResourceById(Long id) throws ResourceNotFoundException, AccessDeniedException;

}
