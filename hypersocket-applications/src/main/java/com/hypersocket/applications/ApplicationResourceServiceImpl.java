package com.hypersocket.applications;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.hypersocket.i18n.I18NService;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.permissions.Role;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractAssignableResourceRepository;
import com.hypersocket.resource.AbstractAssignableResourceServiceImpl;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;

public class ApplicationResourceServiceImpl extends
		AbstractAssignableResourceServiceImpl<ApplicationResource> implements
		ApplicationResourceService {


	public static final String RESOURCE_BUNDLE = "ApplicationService";

	@Autowired
	ApplicationResourceRepository repository;

	@Autowired
	I18NService i18nService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	MenuService menuService;

	@PostConstruct
	private void postConstruct() {

		i18nService.registerBundle(RESOURCE_BUNDLE);

		PermissionCategory cat = permissionService.registerPermissionCategory(
				RESOURCE_BUNDLE, "category.websites");

		for (ApplicationResourcePermission p : ApplicationResourcePermission.values()) {
			permissionService.registerPermission(p.getResourceKey(), cat);
		}

		/**
		 * TODO add your menu item and other initialization.
		 */
		menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE,
				"applications", "fa-desktop", "applications", 200,
				ApplicationResourcePermission.READ, ApplicationResourcePermission.CREATE,
				ApplicationResourcePermission.UPDATE, ApplicationResourcePermission.DELETE),
				MenuService.MENU_RESOURCES);

	}

	@Override
	protected AbstractAssignableResourceRepository<ApplicationResource> getRepository() {
		return repository;
	}

	@Override
	protected String getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

	@Override
	public Class<?> getPermissionType() {
		return ApplicationResourcePermission.class;
	}

	@Override
	protected void fireResourceCreationEvent(ApplicationResource resource) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireResourceCreationEvent(ApplicationResource resource,
			Throwable t) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireResourceUpdateEvent(ApplicationResource resource) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireResourceUpdateEvent(ApplicationResource resource, Throwable t) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireResourceDeletionEvent(ApplicationResource resource) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireResourceDeletionEvent(ApplicationResource resource,
			Throwable t) {
		// TODO Auto-generated method stub

	}

	@Override
	public ApplicationResource updateResource(ApplicationResource resource,
			String name, Set<Role> roles) throws ResourceChangeException, AccessDeniedException {
		
		resource.setName(name);
		resource.getRoles().clear();
		resource.getRoles().addAll(roles);
		
		/**
		 * Set any additional fields on your resource here before calling updateResource. 
		 * 
		 * Remember to fill in the fire*Event methods to ensure events are fired for all operations.
		 */
		updateResource(resource);
		
		return resource;
	}

	@Override
	public ApplicationResource createResource(String name, Set<Role> roles,
			Realm realm) throws ResourceCreationException, AccessDeniedException {
		
		ApplicationResource resource = new ApplicationResource();
		resource.setName(name);
		resource.setRoles(roles);
		
		/**
		 * Set any additional fields on your resource here before calling createResource. 
		 * 
		 * Remember to fill in the fire*Event methods to ensure events are fired for all operations.
		 */
		createResource(resource);
		
		return resource;
	}

}
