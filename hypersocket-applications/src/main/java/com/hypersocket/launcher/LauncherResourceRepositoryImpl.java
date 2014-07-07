package com.hypersocket.launcher;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hypersocket.resource.AbstractResourceRepositoryImpl;

@Repository
@Transactional
public class LauncherResourceRepositoryImpl extends
		AbstractResourceRepositoryImpl<LauncherResource> implements
		LauncherResourceRepository {

	/**
	 * TODO rename this class to match your entity / interface
	 */
	@Override
	protected Class<LauncherResource> getResourceClass() {
		return LauncherResource.class;
	}

}
