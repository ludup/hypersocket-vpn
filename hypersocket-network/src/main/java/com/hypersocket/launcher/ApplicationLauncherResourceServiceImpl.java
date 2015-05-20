package com.hypersocket.launcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hypersocket.events.EventService;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.launcher.events.ApplicationLauncherCreatedEvent;
import com.hypersocket.launcher.events.ApplicationLauncherDeletedEvent;
import com.hypersocket.launcher.events.ApplicationLauncherEvent;
import com.hypersocket.launcher.events.ApplicationLauncherUpdatedEvent;
import com.hypersocket.menus.AbstractTableAction;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;
import com.hypersocket.network.NetworkResourceServiceImpl;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceRepository;
import com.hypersocket.resource.AbstractResourceServiceImpl;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.resource.ResourceExportException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.utils.HypersocketUtils;

@Service
public class ApplicationLauncherResourceServiceImpl extends
		AbstractResourceServiceImpl<ApplicationLauncherResource> implements
		ApplicationLauncherResourceService {

	static Logger log = LoggerFactory
			.getLogger(ApplicationLauncherResourceServiceImpl.class);

	public static final String APPLICATION_LAUNCHER_ACTIONS = "applicationLauncherActions";

	public static final String RESOURCE_BUNDLE = "LauncherService";

	@Autowired
	ApplicationLauncherResourceRepository repository;

	@Autowired
	I18NService i18nService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	MenuService menuService;

	@Autowired
	EventService eventService;

	public ApplicationLauncherResourceServiceImpl() {
		super("applicationLauncher");
	}

	@PostConstruct
	private void postConstruct() {

		i18nService.registerBundle(RESOURCE_BUNDLE);

		PermissionCategory cat = permissionService.registerPermissionCategory(
				RESOURCE_BUNDLE, "category.lauchers");

		repository.loadPropertyTemplates("applicationLauncherTemplate.xml");

		for (ApplicationLauncherResourcePermission p : ApplicationLauncherResourcePermission
				.values()) {
			permissionService.registerPermission(p, cat);
		}

		menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE,
				"launchers", "fa-desktop", "launchers", 9999,
				ApplicationLauncherResourcePermission.READ,
				ApplicationLauncherResourcePermission.CREATE,
				ApplicationLauncherResourcePermission.UPDATE,
				ApplicationLauncherResourcePermission.DELETE),
				NetworkResourceServiceImpl.MENU_NETWORK);

		menuService.registerExtendableTable(APPLICATION_LAUNCHER_ACTIONS);

		menuService.registerTableAction(APPLICATION_LAUNCHER_ACTIONS,
				new AbstractTableAction("exportApplicationResource",
						"fa-download", "exportApplicationResource",
						ApplicationLauncherResourcePermission.UPDATE, 0, null,
						null));

		eventService.registerEvent(ApplicationLauncherEvent.class,
				RESOURCE_BUNDLE, this);
		eventService.registerEvent(ApplicationLauncherCreatedEvent.class,
				RESOURCE_BUNDLE, this);
		eventService.registerEvent(ApplicationLauncherUpdatedEvent.class,
				RESOURCE_BUNDLE, this);
		eventService.registerEvent(ApplicationLauncherDeletedEvent.class,
				RESOURCE_BUNDLE, this);

	}

	@Override
	protected AbstractResourceRepository<ApplicationLauncherResource> getRepository() {
		return repository;
	}

	@Override
	protected String getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

	@Override
	public Class<ApplicationLauncherResourcePermission> getPermissionType() {
		return ApplicationLauncherResourcePermission.class;
	}

	protected Class<ApplicationLauncherResource> getResourceClass() {
		return ApplicationLauncherResource.class;
	}

	@Override
	protected void fireResourceCreationEvent(
			ApplicationLauncherResource resource) {
		eventService.publishEvent(new ApplicationLauncherCreatedEvent(this,
				getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceCreationEvent(
			ApplicationLauncherResource resource, Throwable t) {
		eventService.publishEvent(new ApplicationLauncherCreatedEvent(this,
				resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceUpdateEvent(ApplicationLauncherResource resource) {
		eventService.publishEvent(new ApplicationLauncherUpdatedEvent(this,
				getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceUpdateEvent(
			ApplicationLauncherResource resource, Throwable t) {
		eventService.publishEvent(new ApplicationLauncherUpdatedEvent(this,
				resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceDeletionEvent(
			ApplicationLauncherResource resource) {
		eventService.publishEvent(new ApplicationLauncherDeletedEvent(this,
				getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceDeletionEvent(
			ApplicationLauncherResource resource, Throwable t) {
		eventService.publishEvent(new ApplicationLauncherDeletedEvent(this,
				resource, t, getCurrentSession()));
	}

	@Override
	public ApplicationLauncherResource updateResource(
			ApplicationLauncherResource resource, String name, String exe,
			String args, ApplicationLauncherOS os, String startupScript,
			String shutdownScript) throws ResourceChangeException,
			AccessDeniedException {

		resource.setName(name);
		resource.setExe(exe);
		resource.setArgs(args);
		resource.setOs(os);
		resource.setStartupScript(startupScript);
		resource.setShutdownScript(shutdownScript);

		/**
		 * Set any additional fields on your resource here before calling
		 * updateResource.
		 * 
		 * Remember to fill in the fire*Event methods to ensure events are fired
		 * for all operations.
		 */
		updateResource(resource, new HashMap<String, String>());

		return resource;
	}

	@Override
	public ApplicationLauncherResource createResource(String name, Realm realm,
			String exe, String args, ApplicationLauncherOS os,
			String startupScript, String shutdownScript)
			throws ResourceCreationException, AccessDeniedException {

		ApplicationLauncherResource resource = new ApplicationLauncherResource();
		resource.setName(name);
		resource.setExe(exe);
		resource.setArgs(args);
		resource.setOs(os);
		resource.setStartupScript(startupScript);
		resource.setShutdownScript(shutdownScript);
		resource.setRealm(realm);

		/**
		 * Set any additional fields on your resource here before calling
		 * createResource.
		 * 
		 * Remember to fill in the fire*Event methods to ensure events are fired
		 * for all operations.
		 */
		createResource(resource, new HashMap<String, String>());

		return resource;
	}

	@Override
	public String exportResoure(long id) throws ResourceNotFoundException,
			ResourceExportException {
		final ApplicationLauncherResource resource = getResourceById(id);
		List<ApplicationLauncherResource> list = new ArrayList<ApplicationLauncherResource>();
		list.add(resource);
		return exportResources(list);
	}

	@Override
	public String exportAllResoures() throws ResourceExportException {
		List<ApplicationLauncherResource> list = getResources();
		return exportResources(list);
	}

	@Override
	public Collection<ApplicationLauncherResource> uploadLaunchers(
			MultipartFile jsonFile) throws ResourceException,
			AccessDeniedException {
		try {
			String json = IOUtils.toString(jsonFile.getInputStream());
			if (!HypersocketUtils.isValidJSON(json)) {
				throw new ResourceException(RESOURCE_BUNDLE,
						"error.incorrectJSON");
			}
			return importResources(json, getCurrentRealm());
		} catch (IOException e) {
			log.error("Error in upload Applicatin Launchers", e);
			return null;
		}
	}

}
