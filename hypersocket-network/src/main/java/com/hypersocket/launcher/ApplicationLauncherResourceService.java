package com.hypersocket.launcher;

import java.util.Collection;

import org.springframework.web.multipart.MultipartFile;

import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceService;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.resource.ResourceExportException;
import com.hypersocket.resource.ResourceNotFoundException;

public interface ApplicationLauncherResourceService extends
		AbstractResourceService<ApplicationLauncherResource> {

	ApplicationLauncherResource updateResource(
			ApplicationLauncherResource resourceById, String name, String exe,
			String args, ApplicationLauncherOS os, String startupScript,
			String shutdownScript) throws ResourceChangeException,
			AccessDeniedException;

	ApplicationLauncherResource createResource(String name, Realm realm,
			String exe, String args, ApplicationLauncherOS os,
			String startupScript, String shutdownScript)
			throws ResourceCreationException, AccessDeniedException;

	String exportResoure(long id) throws ResourceNotFoundException,
			ResourceExportException;

	String exportAllResoures() throws ResourceExportException;

	Collection<ApplicationLauncherResource> uploadLaunchers(
			MultipartFile jsonFile) throws ResourceException,
			AccessDeniedException;

}
