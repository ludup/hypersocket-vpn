package com.hypersocket.launcher;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hypersocket.applications.ApplicationTemplate;

@JsonDeserialize(using = ApplicationLauncherTemplateDeserializer.class)
public class ApplicationLauncherTemplate extends ApplicationTemplate<ApplicationLauncherResource> {

	private static final long serialVersionUID = -7919161182536032301L;

}
