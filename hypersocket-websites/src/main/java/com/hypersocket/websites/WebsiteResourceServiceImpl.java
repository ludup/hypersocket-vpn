package com.hypersocket.websites;

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

public class WebsiteResourceServiceImpl extends
		AbstractAssignableResourceServiceImpl<WebsiteResource> implements
		WebsiteResourceService {

	public static final String RESOURCE_BUNDLE = "WebsiteResourceService";

	@Autowired
	WebsiteResourceRepository websiteRepository;

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

		for (WebsitePermission p : WebsitePermission.values()) {
			permissionService.registerPermission(p.getResourceKey(), cat);
		}

		menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE,
				"websites", "fa-globe", "websites", 100,
				WebsitePermission.READ, WebsitePermission.CREATE,
				WebsitePermission.UPDATE, WebsitePermission.DELETE),
				MenuService.MENU_RESOURCES);

	}

	@Override
	protected AbstractAssignableResourceRepository<WebsiteResource> getRepository() {
		return websiteRepository;
	}

	@Override
	protected String getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

	@Override
	public Class<?> getPermissionType() {
		return WebsitePermission.class;
	}

	@Override
	protected void fireResourceCreationEvent(WebsiteResource resource) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireResourceCreationEvent(WebsiteResource resource,
			Throwable t) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireResourceUpdateEvent(WebsiteResource resource) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireResourceUpdateEvent(WebsiteResource resource, Throwable t) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireResourceDeletionEvent(WebsiteResource resource) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireResourceDeletionEvent(WebsiteResource resource,
			Throwable t) {
		// TODO Auto-generated method stub

	}

	@Override
	public WebsiteResource updateResource(WebsiteResource website, String name,
			String launchUrl, String additionalUrls, Set<Role> roles)
			throws ResourceChangeException, AccessDeniedException {

		website.setName(name);
		website.setLaunchUrl(launchUrl);
		website.setAdditionalUrls(additionalUrls);
		website.getRoles().clear();
		website.getRoles().addAll(roles);

		updateResource(website);
		return website;
	}

	@Override
	public WebsiteResource createResource(String name, String launchUrl,
			String additionalUrls, Set<Role> roles, Realm realm)
			throws ResourceCreationException, AccessDeniedException {

		WebsiteResource website = new WebsiteResource();
		website.setName(name);
		website.setLaunchUrl(launchUrl);
		website.setAdditionalUrls(additionalUrls);
		website.getRoles().clear();
		website.getRoles().addAll(roles);

		createResource(website);

		return website;
	}

}
