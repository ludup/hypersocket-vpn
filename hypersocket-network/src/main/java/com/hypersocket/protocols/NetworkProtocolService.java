package com.hypersocket.protocols;

import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceService;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.server.forward.ForwardingTransport;

public interface NetworkProtocolService extends
		AbstractResourceService<NetworkProtocol> {
	
	String getFingerprint();

	NetworkProtocol updateResource(NetworkProtocol resourceById, String name,
			Integer startPort, Integer endPort, ForwardingTransport transport)
			throws ResourceChangeException, AccessDeniedException;

	NetworkProtocol createResource(String name, Realm realm, Integer startPort,
			Integer endPort, ForwardingTransport transport)
			throws ResourceCreationException, AccessDeniedException;


}
