package com.hypersocket.htmlapplications;

import org.springframework.stereotype.Repository;

import com.hypersocket.resource.AbstractAssignableResourceRepositoryImpl;

@Repository
public class HTMLApplicationResourceRepositoryImpl extends
		AbstractAssignableResourceRepositoryImpl<HTMLApplicationResource> implements
		HTMLApplicationResourceRepository {

	@Override
	protected Class<HTMLApplicationResource> getResourceClass() {
		return HTMLApplicationResource.class;
	}

}
