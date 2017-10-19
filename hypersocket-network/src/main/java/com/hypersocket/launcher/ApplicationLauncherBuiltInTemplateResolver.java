package com.hypersocket.launcher;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hypersocket.applications.BuiltInTemplateResolver;

@Component
public class ApplicationLauncherBuiltInTemplateResolver extends BuiltInTemplateResolver<ApplicationLauncherTemplate> {
	final static Logger LOG = LoggerFactory.getLogger(ApplicationLauncherBuiltInTemplateResolver.class);

	public final static String RESOLVER_ID = "builtIn";

	@Autowired
	private ApplicationLauncherResourceService applicationLauncherResourceService;

	public ApplicationLauncherBuiltInTemplateResolver() {
		super(RESOLVER_ID, "application.json", ApplicationLauncherTemplate.class);
	}

	@PostConstruct
	private void setup() {
		applicationLauncherResourceService.registerTemplateResolver(this);
	}

	@Override
	protected JsonDeserializer<ApplicationLauncherTemplate> createDeserializer() {
		return new ApplicationLauncherTemplateDeserializer();
	}

}
