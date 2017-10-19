package com.hypersocket.launcher;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hypersocket.applications.ApplicationTemplateResolver;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceService;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.tables.BootstrapTableResult;


public interface ApplicationLauncherResourceService extends
		AbstractResourceService<ApplicationLauncherResource> {
	
	void registerTemplateResolver(ApplicationTemplateResolver<ApplicationLauncherTemplate> resolver);

	ApplicationLauncherResource updateResource(
			ApplicationLauncherResource id, String name, Map<String,String> properties) throws ResourceException,
			AccessDeniedException;

	BootstrapTableResult<?> searchTemplates(String resolver, String search, int iDisplayStart,
			int iDisplayLength) throws IOException, AccessDeniedException;

	void downloadTemplateImage(String uuid, HttpServletRequest request,
			HttpServletResponse response) throws IOException;

	ApplicationLauncherResource createFromTemplate(String script)
			throws AccessDeniedException, ResourceException;

	ApplicationLauncherResource createResource(String name, Realm realm, Map<String, String> properties)
			throws ResourceException, AccessDeniedException;

}
