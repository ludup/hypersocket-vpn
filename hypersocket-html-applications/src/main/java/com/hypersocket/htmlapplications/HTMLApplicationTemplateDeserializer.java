package com.hypersocket.htmlapplications;

import com.hypersocket.applications.ApplicationTemplateDeserializer;

public class HTMLApplicationTemplateDeserializer
		extends ApplicationTemplateDeserializer<HTMLApplicationTemplate, HTMLApplicationResource> {

	public HTMLApplicationTemplateDeserializer() {
		super(HTMLApplicationResource.class);
	}

	@Override
	protected HTMLApplicationTemplate createResource() {
		return new HTMLApplicationTemplate();
	}

}
