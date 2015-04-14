/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package upgrade;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.hypersocket.network.NetworkResourceRepository;
import com.hypersocket.network.NetworkTransport;
import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.protocols.NetworkProtocolRepository;
import com.hypersocket.realm.RealmService;

public class network_0_DOT_2_DOT_6 implements Runnable {

	@Autowired
	NetworkResourceRepository networkRepository;
	
	@Autowired
	NetworkProtocolRepository protocolRepository;
	
	@Autowired
	RealmService realmService; 
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {

		for(NetworkProtocol protocol : protocolRepository.getResources(null)) {
			protocol.setRealm(realmService.getSystemRealm());
			protocolRepository.saveResource(protocol, new HashMap<String,String>());
		}
	}

	@SuppressWarnings("unchecked")
	void createProtocol(String name, NetworkTransport transport, Integer start,
			Integer end) {
		NetworkProtocol protocol = new NetworkProtocol();
		protocol.setName(name);
		protocol.setTransport(transport);
		protocol.setStartPort(start);
		protocol.setEndPort(end);

		protocolRepository.saveResource(protocol, new HashMap<String,String>());

	}

}
