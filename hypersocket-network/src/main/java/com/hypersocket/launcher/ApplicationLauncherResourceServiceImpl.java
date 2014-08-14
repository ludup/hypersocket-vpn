package com.hypersocket.launcher;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hypersocket.events.EventService;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.launcher.events.ApplicationLauncherResourceCreatedEvent;
import com.hypersocket.launcher.events.ApplicationLauncherResourceDeletedEvent;
import com.hypersocket.launcher.events.ApplicationLauncherResourceUpdatedEvent;
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

@Service
public class ApplicationLauncherResourceServiceImpl extends
		AbstractResourceServiceImpl<ApplicationLauncherResource> implements
		ApplicationLauncherResourceService {

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
	
	@PostConstruct
	private void postConstruct() {

		i18nService.registerBundle(RESOURCE_BUNDLE);

		PermissionCategory cat = permissionService.registerPermissionCategory(
				RESOURCE_BUNDLE, "category.lauchers");

		for (ApplicationLauncherResourcePermission p : ApplicationLauncherResourcePermission.values()) {
			permissionService.registerPermission(p.getResourceKey(), p.isSystem(), cat);
		}

		menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE,
				"launchers", "fa-desktop", "launchers", 9999,
				ApplicationLauncherResourcePermission.READ, ApplicationLauncherResourcePermission.CREATE,
				ApplicationLauncherResourcePermission.UPDATE, ApplicationLauncherResourcePermission.DELETE),
				NetworkResourceServiceImpl.MENU_NETWORK);

		eventService.registerEvent(ApplicationLauncherResourceCreatedEvent.class, RESOURCE_BUNDLE);
		eventService.registerEvent(ApplicationLauncherResourceUpdatedEvent.class, RESOURCE_BUNDLE);
		eventService.registerEvent(ApplicationLauncherResourceDeletedEvent.class, RESOURCE_BUNDLE);
		
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

	@Override
	protected void fireResourceCreationEvent(ApplicationLauncherResource resource) {
		eventService.publishEvent(new ApplicationLauncherResourceCreatedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceCreationEvent(ApplicationLauncherResource resource,
			Throwable t) {
		eventService.publishEvent(new ApplicationLauncherResourceCreatedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceUpdateEvent(ApplicationLauncherResource resource) {
		eventService.publishEvent(new ApplicationLauncherResourceUpdatedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceUpdateEvent(ApplicationLauncherResource resource, Throwable t) {
		eventService.publishEvent(new ApplicationLauncherResourceUpdatedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceDeletionEvent(ApplicationLauncherResource resource) {
		eventService.publishEvent(new ApplicationLauncherResourceDeletedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceDeletionEvent(ApplicationLauncherResource resource,
			Throwable t) {
		eventService.publishEvent(new ApplicationLauncherResourceDeletedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	public ApplicationLauncherResource updateResource(ApplicationLauncherResource resource,
			String name, String exe, String args, ApplicationLauncherOS os) throws ResourceChangeException, AccessDeniedException {
		
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
	public ApplicationLauncherResource createResource(String name,
			Realm realm, String exe, String args, ApplicationLauncherOS os) throws ResourceCreationException, AccessDeniedException {
		
		ApplicationLauncherResource resource = new ApplicationLauncherResource();
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
