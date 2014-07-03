package com.hypersocket.applications;

import com.hypersocket.resource.AbstractAssignableResourceRepositoryImpl;

public class ApplicationResourceRepositoryImpl extends
		AbstractAssignableResourceRepositoryImpl<ApplicationResource> implements
		ApplicationResourceRepository {

	@Override
	protected Class<ApplicationResource> getResourceClass() {
		return ApplicationResource.class;
	}

}
