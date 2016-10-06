package com.hypersocket.websites;

import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.hypersocket.events.EventService;
import com.hypersocket.i18n.I18N;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.permissions.Role;
import com.hypersocket.realm.RealmService;
import com.hypersocket.resource.AbstractAssignableResourceRepository;
import com.hypersocket.resource.AbstractAssignableResourceServiceImpl;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.server.forward.ForwardingTransport;
import com.hypersocket.session.Session;
import com.hypersocket.ui.IndexPageFilter;
import com.hypersocket.websites.events.WebsiteResourceCreatedEvent;
import com.hypersocket.websites.events.WebsiteResourceDeletedEvent;
import com.hypersocket.websites.events.WebsiteResourceEvent;
import com.hypersocket.websites.events.WebsiteResourceUpdatedEvent;

public class WebsiteResourceServiceImpl extends AbstractAssignableResourceServiceImpl<WebsiteResource>
		implements WebsiteResourceService {

	public static final String RESOURCE_BUNDLE = "WebsiteResourceService";

	@Autowired
	WebsiteResourceRepository websiteRepository;

	@Autowired
	I18NService i18nService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	MenuService menuService;

	@Autowired
	EventService eventService;

	@Autowired
	RealmService realmService;
	
	@Autowired
	IndexPageFilter indexPageFilter;

	public WebsiteResourceServiceImpl() {
		super("website");
	}

	@PostConstruct
	private void postConstruct() {

		i18nService.registerBundle(RESOURCE_BUNDLE);

		PermissionCategory cat = permissionService.registerPermissionCategory(RESOURCE_BUNDLE, "category.websites");

		for (WebsitePermission p : WebsitePermission.values()) {
			permissionService.registerPermission(p, cat);
		}

		websiteRepository.loadPropertyTemplates("websiteResourceTemplate.xml");
		menuService.registerMenu(
				new MenuRegistration(RESOURCE_BUNDLE, "websites", "fa-globe", "websites", 100, WebsitePermission.READ,
						WebsitePermission.CREATE, WebsitePermission.UPDATE, WebsitePermission.DELETE),
				MenuService.MENU_RESOURCES);

		menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE, "myWebsites", "fa-globe", "myWebsites", 200) {
			public boolean canRead() {
				return websiteRepository
						.getAssignableResourceCount(realmService.getAssociatedPrincipals(getCurrentPrincipal())) > 0;
			}
		}, MenuService.MENU_MY_RESOURCES);

		eventService.registerEvent(WebsiteResourceEvent.class, RESOURCE_BUNDLE);
		eventService.registerEvent(WebsiteResourceCreatedEvent.class, RESOURCE_BUNDLE);
		eventService.registerEvent(WebsiteResourceUpdatedEvent.class, RESOURCE_BUNDLE);
		eventService.registerEvent(WebsiteResourceDeletedEvent.class, RESOURCE_BUNDLE);

		indexPageFilter.addStyleSheet("${uiPath}/css/websites.css");
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
		eventService.publishEvent(new WebsiteResourceCreatedEvent(this, getCurrentSession(), resource));

	}

	@Override
	protected void fireResourceCreationEvent(WebsiteResource resource, Throwable t) {
		eventService.publishEvent(new WebsiteResourceCreatedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceUpdateEvent(WebsiteResource resource) {
		eventService.publishEvent(new WebsiteResourceUpdatedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceUpdateEvent(WebsiteResource resource, Throwable t) {
		eventService.publishEvent(new WebsiteResourceUpdatedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceDeletionEvent(WebsiteResource resource) {
		eventService.publishEvent(new WebsiteResourceDeletedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceDeletionEvent(WebsiteResource resource, Throwable t) {
		eventService.publishEvent(new WebsiteResourceDeletedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	public WebsiteResource updateResource(WebsiteResource website, String name, String launchUrl, String additionalUrls,
			Set<Role> roles, String logo) throws AccessDeniedException, ResourceException {

		website.setName(name);
		website.setLaunchUrl(launchUrl);
		website.setAdditionalUrls(additionalUrls);
		website.setLogo(logo);

		updateResource(website, roles, new HashMap<String, String>());
		return website;
	}

	@Override
	public WebsiteResource createResource(String name, String launchUrl, String additionalUrls, Set<Role> roles, String logo)
			throws AccessDeniedException, ResourceException {

		WebsiteResource website = new WebsiteResource();
		website.setName(name);
		website.setLaunchUrl(launchUrl);
		website.setAdditionalUrls(additionalUrls);
		website.getRoles().addAll(roles);
		website.setLogo(logo);

		createResource(website, new HashMap<String, String>());

		return website;
	}

	@Override
	public void verifyResourceSession(WebsiteResource resource, String hostname, int port, ForwardingTransport transport,
			Session session) throws AccessDeniedException {

		for (URL url : resource.getUrls()) {
			if (hostname.equalsIgnoreCase(url.getHost())) {
				if (url.getPort() > -1) {
					if (url.getPort() == port) {
						return;
					}
				} else if (url.getDefaultPort() == port) {
					return;
				}
			}
		}

		throw new AccessDeniedException(I18N.getResource(getCurrentLocale(), 
				RESOURCE_BUNDLE, 
				"error.urlNotAuthorized",
				hostname, 
				port, 
				resource.getName()));

	}

	@Override
	protected Class<WebsiteResource> getResourceClass() {
		return WebsiteResource.class;
	}

}
