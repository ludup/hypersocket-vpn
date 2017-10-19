package com.hypersocket.htmlapplications;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hypersocket.applications.BuiltInTemplateResolver;

@Component
public class HTMLApplicationBuiltInTemplateResolver extends BuiltInTemplateResolver<HTMLApplicationTemplate> {
	final static Logger LOG = LoggerFactory.getLogger(HTMLApplicationBuiltInTemplateResolver.class);

	public final static String RESOLVER_ID = "builtInHTML";

	@Autowired
	private HTMLApplicationResourceService htmlApplicationResourceService;
	
	public HTMLApplicationBuiltInTemplateResolver() {
		super(RESOLVER_ID, "html-application.json", HTMLApplicationTemplate.class);
	}

	@PostConstruct
	private void setup() {
		htmlApplicationResourceService.registerTemplateResolver(this);
	}

	@Override
	protected JsonDeserializer<HTMLApplicationTemplate> createDeserializer() {
		return new HTMLApplicationTemplateDeserializer();
	}

}
