package com.hypersocket.htmlapplications;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hypersocket.applications.ApplicationTemplate;

@JsonDeserialize(using = HTMLApplicationTemplateDeserializer.class)
public class HTMLApplicationTemplate extends ApplicationTemplate<HTMLApplicationResource> {

	private static final long serialVersionUID = -7919161182536032301L;

}
