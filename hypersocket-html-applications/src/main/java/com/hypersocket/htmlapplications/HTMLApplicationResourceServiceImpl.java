package com.hypersocket.htmlapplications;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hypersocket.applications.ApplicationTemplateResolver;
import com.hypersocket.events.EventService;
import com.hypersocket.htmlapplications.events.HTMLApplicationResourceCreatedEvent;
import com.hypersocket.htmlapplications.events.HTMLApplicationResourceDeletedEvent;
import com.hypersocket.htmlapplications.events.HTMLApplicationResourceEvent;
import com.hypersocket.htmlapplications.events.HTMLApplicationResourceUpdatedEvent;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.menus.AbstractTableAction;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.permissions.Role;
import com.hypersocket.properties.EntityResourcePropertyStore;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractAssignableResourceRepository;
import com.hypersocket.resource.AbstractAssignableResourceServiceImpl;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.tables.BootstrapTableResult;

@Service
public class HTMLApplicationResourceServiceImpl extends AbstractAssignableResourceServiceImpl<HTMLApplicationResource>
		implements HTMLApplicationResourceService {

	public static final String RESOURCE_BUNDLE = "HTMLApplicationResourceService";

	public static final String HTML_APPLICATION_ACTIONS = "htmlApplicationActions";

	@Autowired
	HTMLApplicationResourceRepository repository;

	@Autowired
	I18NService i18nService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	MenuService menuService;

	@Autowired
	EventService eventService;

	@Autowired
	HTMLApplicationResourceHelper helper;

	public HTMLApplicationResourceServiceImpl() {
		super("hTMLApplication");
	}

	@PostConstruct
	private void postConstruct() {

		i18nService.registerBundle(RESOURCE_BUNDLE);

		PermissionCategory cat = permissionService.registerPermissionCategory(RESOURCE_BUNDLE,
				"category.hTMLApplication");

		for (HTMLApplicationResourcePermission p : HTMLApplicationResourcePermission.values()) {
			permissionService.registerPermission(p, cat);
		}

		repository.loadPropertyTemplates("hTMLApplicationResourceTemplate.xml");

		menuService.registerMenu(
				new MenuRegistration(RESOURCE_BUNDLE, "hTMLApplications", "fa-html5", "hTMLApplications", 100,
						HTMLApplicationResourcePermission.READ, HTMLApplicationResourcePermission.CREATE,
						HTMLApplicationResourcePermission.UPDATE, HTMLApplicationResourcePermission.DELETE),
				MenuService.MENU_RESOURCES);

		menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE, "myHTMLApplications", "fa-html5",
				"myHTMLApplications", 100, null, null, null, null), MenuService.MENU_MY_RESOURCES);

		menuService.registerTableAction(HTML_APPLICATION_ACTIONS, new AbstractTableAction("launch", "fa-rocket",
				null, HTMLApplicationResourcePermission.READ, 900, null, "canLaunch"));

		eventService.registerEvent(HTMLApplicationResourceEvent.class, RESOURCE_BUNDLE);
		eventService.registerEvent(HTMLApplicationResourceCreatedEvent.class, RESOURCE_BUNDLE);
		eventService.registerEvent(HTMLApplicationResourceUpdatedEvent.class, RESOURCE_BUNDLE);
		eventService.registerEvent(HTMLApplicationResourceDeletedEvent.class, RESOURCE_BUNDLE);

		EntityResourcePropertyStore.registerResourceService(HTMLApplicationResource.class, repository);

	}

	@Override
	protected AbstractAssignableResourceRepository<HTMLApplicationResource> getRepository() {
		return repository;
	}

	@Override
	protected String getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

	@Override
	public Class<?> getPermissionType() {
		return HTMLApplicationResourcePermission.class;
	}

	@Override
	protected void fireResourceCreationEvent(HTMLApplicationResource resource) {
		eventService.publishEvent(new HTMLApplicationResourceCreatedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceCreationEvent(HTMLApplicationResource resource, Throwable t) {
		eventService.publishEvent(new HTMLApplicationResourceCreatedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceUpdateEvent(HTMLApplicationResource resource) {
		eventService.publishEvent(new HTMLApplicationResourceUpdatedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceUpdateEvent(HTMLApplicationResource resource, Throwable t) {
		eventService.publishEvent(new HTMLApplicationResourceUpdatedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceDeletionEvent(HTMLApplicationResource resource) {
		eventService.publishEvent(new HTMLApplicationResourceDeletedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceDeletionEvent(HTMLApplicationResource resource, Throwable t) {
		eventService.publishEvent(new HTMLApplicationResourceDeletedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	public HTMLApplicationResource updateResource(HTMLApplicationResource resource, String name, Set<Role> roles,
			Map<String, String> properties) throws ResourceException, AccessDeniedException {

		resource.setName(name);

		/**
		 * Set any additional fields on your resource here before calling
		 * updateResource.
		 * 
		 * Remember to fill in the fire*Event methods to ensure events are fired
		 * for all operations.
		 */
		updateResource(resource, roles, properties);

		return resource;
	}

	@Override
	public HTMLApplicationResource createResource(String name, Set<Role> roles, Realm realm,
			Map<String, String> properties) throws ResourceException, AccessDeniedException {

		HTMLApplicationResource resource = new HTMLApplicationResource();
		resource.setName(name);
		resource.setRealm(realm);
		resource.setRoles(roles);

		/**
		 * Set any additional fields on your resource here before calling
		 * createResource.
		 * 
		 * Remember to fill in the fire*Event methods to ensure events are fired
		 * for all operations.
		 */
		createResource(resource, properties);

		return resource;
	}

	@Override
	public Collection<PropertyCategory> getPropertyTemplate(HTMLApplicationResource resource)
			throws AccessDeniedException {

		assertPermission(HTMLApplicationResourcePermission.READ);
		return repository.getPropertyCategories(resource);
	}

	@Override
	public Collection<PropertyCategory> getPropertyTemplate() throws AccessDeniedException {
		assertPermission(HTMLApplicationResourcePermission.READ);
		return repository.getPropertyCategories(null);
	}

	@Override
	protected Class<HTMLApplicationResource> getResourceClass() {
		return HTMLApplicationResource.class;
	}

	@Override
	public void registerTemplateResolver(ApplicationTemplateResolver<HTMLApplicationTemplate> resolver) {
		helper.registerTemplateResolver(resolver);
	}

	@Override
	public BootstrapTableResult<?> searchTemplates(String resolver, String search, int iDisplayStart,
			int iDisplayLength) throws IOException, AccessDeniedException {
		return helper.searchTemplates(resolver, search, iDisplayStart, iDisplayLength);
	}

	@Override
	public void downloadTemplateImage(String uuid, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		helper.downloadTemplateImage(uuid, request, response);
	}

	@Override
	public HTMLApplicationResource createFromTemplate(String script) throws AccessDeniedException, ResourceException {
		return helper.createFromTemplate(script);
	}

}
