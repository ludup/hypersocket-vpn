package com.hypersocket.applications;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.resource.RealmResource;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.tables.BootstrapTableResult;

public interface ApplicationResourceService<R extends RealmResource, T extends ApplicationTemplate<R>> {
	
	void registerTemplateResolver(ApplicationTemplateResolver<T> resolver);

	BootstrapTableResult<?> searchTemplates(String resolver, String search, int iDisplayStart,
			int iDisplayLength) throws IOException, AccessDeniedException;

	void downloadTemplateImage(String uuid, HttpServletRequest request,
			HttpServletResponse response) throws IOException;

	R createFromTemplate(String script)
			throws AccessDeniedException, ResourceException;

}
