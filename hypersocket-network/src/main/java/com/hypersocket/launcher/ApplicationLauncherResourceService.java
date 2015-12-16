package com.hypersocket.launcher;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceService;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.tables.BootstrapTableResult;


public interface ApplicationLauncherResourceService extends
		AbstractResourceService<ApplicationLauncherResource> {

	ApplicationLauncherResource updateResource(
			ApplicationLauncherResource resourceById, String name, String exe,
			String args, ApplicationLauncherOS os, String startupScript,
			String shutdownScript, String logo) throws ResourceChangeException,
			AccessDeniedException;

	ApplicationLauncherResource createResource(String name, Realm realm,
			String exe, String args, ApplicationLauncherOS os,
			String startupScript, String shutdownScript, String logo)
			throws ResourceCreationException, AccessDeniedException;

	BootstrapTableResult searchTemplates(String search, int iDisplayStart,
			int iDisplayLength) throws IOException, AccessDeniedException;

	void downloadTemplateImage(String uuid, HttpServletRequest request,
			HttpServletResponse response) throws IOException;

	ApplicationLauncherResource createFromTemplate(String script)
			throws AccessDeniedException, ResourceException;

}
