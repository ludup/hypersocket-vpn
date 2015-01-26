package com.hypersocket.protocols;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hypersocket.resource.AbstractResourceRepositoryImpl;

@Repository
@Transactional
public class NetworkProtocolRepositoryImpl extends
		AbstractResourceRepositoryImpl<NetworkProtocol> implements
		NetworkProtocolRepository {

	@Override
	protected Class<NetworkProtocol> getResourceClass() {
		return NetworkProtocol.class;
	}

}
