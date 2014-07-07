package com.hypersocket.launcher;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hypersocket.applications.events.ApplicationResourceCreatedEvent;
import com.hypersocket.applications.events.ApplicationResourceDeletedEvent;
import com.hypersocket.applications.events.ApplicationResourceUpdatedEvent;
import com.hypersocket.events.EventService;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.launcher.events.LauncherResourceCreatedEvent;
import com.hypersocket.launcher.events.LauncherResourceDeletedEvent;
import com.hypersocket.launcher.events.LauncherResourceUpdatedEvent;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceRepository;
import com.hypersocket.resource.AbstractResourceServiceImpl;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;

@Service
public class LauncherResourceServiceImpl extends
		AbstractResourceServiceImpl<LauncherResource> implements
		LauncherResourceService {

	public static final String RESOURCE_BUNDLE = "LauncherService";

	@Autowired
	LauncherResourceRepository repository;

	@Autowired
	I18NService i18nService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	MenuService menuService;

	@Autowired
	EventService eventService;
	
	@PostConstruct
	private void postConstruct() {

		i18nService.registerBundle(RESOURCE_BUNDLE);

		PermissionCategory cat = permissionService.registerPermissionCategory(
				RESOURCE_BUNDLE, "category.lauchers");

		for (LauncherResourcePermission p : LauncherResourcePermission.values()) {
			permissionService.registerPermission(p.getResourceKey(), cat);
		}

		menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE,
				"launchers", "fa-rocket", "launchers", 100,
				LauncherResourcePermission.READ, LauncherResourcePermission.CREATE,
				LauncherResourcePermission.UPDATE, LauncherResourcePermission.DELETE),
				"applications");

		eventService.registerEvent(ApplicationResourceCreatedEvent.class, RESOURCE_BUNDLE);
		eventService.registerEvent(ApplicationResourceUpdatedEvent.class, RESOURCE_BUNDLE);
		eventService.registerEvent(ApplicationResourceDeletedEvent.class, RESOURCE_BUNDLE);
		
	}

	@Override
	protected AbstractResourceRepository<LauncherResource> getRepository() {
		return repository;
	}

	@Override
	protected String getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

	@Override
	public Class<LauncherResourcePermission> getPermissionType() {
		return LauncherResourcePermission.class;
	}

	@Override
	protected void fireResourceCreationEvent(LauncherResource resource) {
		eventService.publishEvent(new LauncherResourceCreatedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceCreationEvent(LauncherResource resource,
			Throwable t) {
		eventService.publishEvent(new LauncherResourceCreatedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceUpdateEvent(LauncherResource resource) {
		eventService.publishEvent(new LauncherResourceUpdatedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceUpdateEvent(LauncherResource resource, Throwable t) {
		eventService.publishEvent(new LauncherResourceUpdatedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceDeletionEvent(LauncherResource resource) {
		eventService.publishEvent(new LauncherResourceDeletedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceDeletionEvent(LauncherResource resource,
			Throwable t) {
		eventService.publishEvent(new LauncherResourceDeletedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	public LauncherResource updateResource(LauncherResource resource,
			String name, String exe, String args, LauncherOS os) throws ResourceChangeException, AccessDeniedException {
		
		resource.setName(name);
		resource.setExe(exe);
		resource.setArgs(args);
		resource.setOs(os);
		
		/**
		 * Set any additional fields on your resource here before calling updateResource. 
		 * 
		 * Remember to fill in the fire*Event methods to ensure events are fired for all operations.
		 */
		updateResource(resource);
		
		return resource;
	}

	@Override
	public LauncherResource createResource(String name,
			Realm realm, String exe, String args, LauncherOS os) throws ResourceCreationException, AccessDeniedException {
		
		LauncherResource resource = new LauncherResource();
		resource.setName(name);
		resource.setExe(exe);
		resource.setArgs(args);
		resource.setOs(os);
		
		/**
		 * Set any additional fields on your resource here before calling createResource. 
		 * 
		 * Remember to fill in the fire*Event methods to ensure events are fired for all operations.
		 */
		createResource(resource);
		
		return resource;
	}

}
