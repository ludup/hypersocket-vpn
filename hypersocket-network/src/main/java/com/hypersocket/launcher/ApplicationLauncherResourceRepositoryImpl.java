package com.hypersocket.launcher;

import org.springframework.stereotype.Repository;

import com.hypersocket.resource.AbstractResourceRepositoryImpl;

@Repository
public class ApplicationLauncherResourceRepositoryImpl extends
		AbstractResourceRepositoryImpl<ApplicationLauncherResource> implements
		ApplicationLauncherResourceRepository {

	@Override
	protected Class<ApplicationLauncherResource> getResourceClass() {
		return ApplicationLauncherResource.class;
	}

}
