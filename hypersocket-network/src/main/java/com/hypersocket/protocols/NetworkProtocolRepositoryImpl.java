package com.hypersocket.protocols;

import org.springframework.stereotype.Repository;

import com.hypersocket.resource.AbstractResourceRepositoryImpl;

@Repository
public class NetworkProtocolRepositoryImpl extends
		AbstractResourceRepositoryImpl<NetworkProtocol> implements
		NetworkProtocolRepository {

	@Override
	protected Class<NetworkProtocol> getResourceClass() {
		return NetworkProtocol.class;
	}

}
