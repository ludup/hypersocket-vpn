package com.hypersocket.launcher;

import com.hypersocket.applications.ApplicationTemplateDeserializer;

public class ApplicationLauncherTemplateDeserializer
		extends ApplicationTemplateDeserializer<ApplicationLauncherTemplate, ApplicationLauncherResource> {

	public ApplicationLauncherTemplateDeserializer() {
		super(ApplicationLauncherResource.class);
	}

	@Override
	protected ApplicationLauncherTemplate createResource() {
		return new ApplicationLauncherTemplate();
	}

}
