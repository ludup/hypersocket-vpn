package com.hypersocket.launcher;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hypersocket.resource.AbstractResourceRepositoryImpl;

@Repository
@Transactional
public class ApplicationLauncherResourceRepositoryImpl extends
		AbstractResourceRepositoryImpl<ApplicationLauncherResource> implements
		ApplicationLauncherResourceRepository {

	@Override
	protected Class<ApplicationLauncherResource> getResourceClass() {
		return ApplicationLauncherResource.class;
	}

}
