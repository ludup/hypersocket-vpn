package com.hypersocket.launcher;

import org.springframework.stereotype.Component;

import com.hypersocket.applications.ApplicationResourceHelper;

@Component
public class ApplicationLauncherResourceHelper extends ApplicationResourceHelper<ApplicationLauncherResource, ApplicationLauncherTemplate> {

	public ApplicationLauncherResourceHelper() {
		super(ApplicationLauncherResourceServiceImpl.RESOURCE_BUNDLE);
	}
}
