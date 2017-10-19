package com.hypersocket.applications;

import javax.persistence.MappedSuperclass;

import com.hypersocket.resource.RealmResource;

@MappedSuperclass
public abstract class ApplicationTemplateResource extends RealmResource implements ApplicationResource {

	private static final long serialVersionUID = -7625240263934504347L;


}
