package com.hypersocket.protocols;

import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceService;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.server.forward.ForwardingTransport;

public interface NetworkProtocolService extends
		AbstractResourceService<NetworkProtocol> {
	
	String getFingerprint();

	NetworkProtocol updateResource(NetworkProtocol resourceById, String name,
			Integer startPort, Integer endPort, ForwardingTransport transport)
			throws ResourceException, AccessDeniedException;

	NetworkProtocol createResource(String name, Realm realm, Integer startPort,
			Integer endPort, ForwardingTransport transport)
			throws ResourceException, AccessDeniedException;


}
