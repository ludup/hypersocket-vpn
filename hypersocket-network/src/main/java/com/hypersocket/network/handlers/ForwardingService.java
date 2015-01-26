package com.hypersocket.network.handlers;

import com.hypersocket.auth.AuthenticatedService;
import com.hypersocket.network.NetworkTransport;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.session.Session;

public interface ForwardingService<T extends ForwardingResource> extends
		AuthenticatedService {

	T getResourceById(Long resourceId) throws ResourceNotFoundException;

	void verifyResourceSession(T resource, String hostname, int port,
			NetworkTransport transport, Session session)
			throws AccessDeniedException;
}
