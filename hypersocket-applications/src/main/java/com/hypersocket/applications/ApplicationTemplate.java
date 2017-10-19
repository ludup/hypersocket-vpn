package com.hypersocket.applications;

import java.util.Collection;

import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.resource.RealmResource;
import com.hypersocket.template.TemplateResource;

public abstract class ApplicationTemplate<R extends RealmResource> extends TemplateResource {

	private static final long serialVersionUID = -7919161182536032301L;
	private R resource;
	private Collection<PropertyCategory> categories;

	public R getResource() {
		return resource;
	}

	public void setResource(R resource) {
		this.resource = resource;
	}

	public Collection<PropertyCategory> getCategories() {
		return categories;
	}

	public void setCategories(Collection<PropertyCategory> categories) {
		this.categories = categories;
	}

}
