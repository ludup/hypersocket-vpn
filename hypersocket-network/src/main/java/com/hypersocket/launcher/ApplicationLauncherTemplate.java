package com.hypersocket.launcher;

import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.template.TemplateResource;

@JsonDeserialize(using = ApplicationLauncherTemplateDeserializer.class)
public class ApplicationLauncherTemplate extends TemplateResource {

	private static final long serialVersionUID = -7919161182536032301L;
	private ApplicationLauncherResource resource;
	private Collection<PropertyCategory> categories;

	public ApplicationLauncherResource getResource() {
		return resource;
	}

	public void setResource(ApplicationLauncherResource resource) {
		this.resource = resource;
	}

	public Collection<PropertyCategory> getCategories() {
		return categories;
	}

	public void setCategories(Collection<PropertyCategory> categories) {
		this.categories = categories;
	}

}
