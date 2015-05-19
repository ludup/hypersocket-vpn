package com.hypersocket.protocols;

import java.util.Collection;

import org.springframework.web.multipart.MultipartFile;

import com.hypersocket.network.NetworkTransport;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceService;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.resource.ResourceExportException;
import com.hypersocket.resource.ResourceNotFoundException;

public interface NetworkProtocolService extends
		AbstractResourceService<NetworkProtocol> {

	NetworkProtocol updateResource(NetworkProtocol resourceById, String name,
			Integer startPort, Integer endPort, NetworkTransport transport)
			throws ResourceChangeException, AccessDeniedException;

	NetworkProtocol createResource(String name, Realm realm, Integer startPort,
			Integer endPort, NetworkTransport transport)
			throws ResourceCreationException, AccessDeniedException;

	String exportResoure(long id) throws ResourceNotFoundException,
			ResourceExportException;

	String exportAllResoures() throws ResourceExportException;

	Collection<NetworkProtocol> uploadProtocols(MultipartFile jsonFile)
			throws ResourceException, AccessDeniedException;

}
