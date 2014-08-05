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
import org.hibernate.FetchMode;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hypersocket.realm.Realm;
import com.hypersocket.repository.CriteriaConfiguration;
import com.hypersocket.resource.AbstractAssignableResourceRepositoryImpl;
import com.hypersocket.tables.ColumnSort;

@Repository
@Transactional
public class NetworkResourceRepositoryImpl extends
		AbstractAssignableResourceRepositoryImpl<NetworkResource> implements
		NetworkResourceRepository {

	@Override
	public List<NetworkResource> search(Realm realm, String searchPattern,
			int start, int length, ColumnSort[] sorting, CriteriaConfiguration... configs) {
		return super.search(realm, searchPattern, start, length, sorting, new CriteriaConfiguration() {
			@Override
			public void configure(Criteria criteria) {
				criteria.setFetchMode("protocols", FetchMode.SELECT);
			}
		});
	}

	

	@Override
	protected Class<NetworkResource> getResourceClass() {
		return NetworkResource.class;
	}
}
