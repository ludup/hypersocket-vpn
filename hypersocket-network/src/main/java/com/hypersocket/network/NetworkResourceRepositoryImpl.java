/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.hypersocket.repository.DeletedCriteria;
import com.hypersocket.resource.AbstractAssignableResourceRepositoryImpl;

@Repository
public class NetworkResourceRepositoryImpl extends
		AbstractAssignableResourceRepositoryImpl<NetworkResource> implements
		NetworkResourceRepository {

	@Override
	public void saveProtocol(NetworkProtocol protocol) {
		save(protocol);
	}

	@Override
	public List<NetworkProtocol> getProtocols() {
		return allEntities(NetworkProtocol.class, new DeletedCriteria(false));
	}
	
	@Override
	public NetworkProtocol getProtocolById(Long id) {
		return get("id", id, NetworkProtocol.class, new DeletedCriteria(false));
	}

	@Override
	public void deleteProtocol(NetworkProtocol protocol) {
		
		NetworkProtocol tmp;
		int idx = 0;
		do {
			tmp = getProtocolByName(protocol.getName() + " [#" + (++idx) + " deleted]", true);
		} while(tmp!=null);
		
		protocol.setDeleted(true);
		protocol.setName(protocol.getName() + " [#" + idx + " deleted]");
		
		saveProtocol(protocol);
	}

	@Override
	public NetworkProtocol getProtocolByName(String name) {
		return get("name", name, NetworkProtocol.class, new DeletedCriteria(false));
	}

	@Override
	public NetworkProtocol getProtocolByName(String name, boolean deleted) {
		return get("name", name, NetworkProtocol.class, new DeletedCriteria(deleted));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NetworkResource> getResourcesByProtocol(NetworkProtocol protocol) {
		
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(NetworkResource.class);
		crit.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		crit.add(Restrictions.eq("deleted", false));
		crit = crit.createCriteria("protocols");
		crit.add(Restrictions.eq("id", protocol.getId()));
		
		return (List<NetworkResource>)crit.list();
	}

	@Override
	protected Class<NetworkResource> getResourceClass() {
		return NetworkResource.class;
	}
}
