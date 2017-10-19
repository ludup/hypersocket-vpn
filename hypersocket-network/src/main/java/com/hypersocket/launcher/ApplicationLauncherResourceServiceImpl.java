package com.hypersocket.launcher;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hypersocket.applications.ApplicationTemplateResolver;
import com.hypersocket.events.EventService;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.launcher.events.ApplicationLauncherCreatedEvent;
import com.hypersocket.launcher.events.ApplicationLauncherDeletedEvent;
import com.hypersocket.launcher.events.ApplicationLauncherEvent;
import com.hypersocket.launcher.events.ApplicationLauncherUpdatedEvent;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;
import com.hypersocket.network.NetworkResourceServiceImpl;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.properties.EntityResourcePropertyStore;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceRepository;
import com.hypersocket.resource.AbstractResourceServiceImpl;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.tables.BootstrapTableResult;
import com.hypersocket.ui.IndexPageFilter;

@Service
public class ApplicationLauncherResourceServiceImpl extends AbstractResourceServiceImpl<ApplicationLauncherResource>
		implements ApplicationLauncherResourceService {

	static Logger log = LoggerFactory.getLogger(ApplicationLauncherResourceServiceImpl.class);

	public static final String RESOURCE_BUNDLE = "LauncherService";

	public static final String APPLICATION_LAUNCHER_ACTIONS = "applicationLauncherActions";

	@Autowired
	private ApplicationLauncherResourceRepository repository;

	@Autowired
	private I18NService i18nService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private MenuService menuService;

	@Autowired
	private EventService eventService;

	@Autowired
	private IndexPageFilter indexPage;

	@Autowired
	private ApplicationLauncherResourceHelper helper;

	public ApplicationLauncherResourceServiceImpl() {
		super("applicationLauncher");
	}

	@PostConstruct
	private void postConstruct() {

		i18nService.registerBundle(RESOURCE_BUNDLE);

		PermissionCategory cat = permissionService.registerPermissionCategory(RESOURCE_BUNDLE, "category.lauchers");

		repository.loadPropertyTemplates("applicationLauncherTemplate.xml");

		for (ApplicationLauncherResourcePermission p : ApplicationLauncherResourcePermission.values()) {
			permissionService.registerPermission(p, cat);
		}

		menuService.registerMenu(
				new MenuRegistration(RESOURCE_BUNDLE, "launchers", "fa-desktop", "launchers", 9999,
						ApplicationLauncherResourcePermission.READ, ApplicationLauncherResourcePermission.CREATE,
						ApplicationLauncherResourcePermission.UPDATE, ApplicationLauncherResourcePermission.DELETE),
				NetworkResourceServiceImpl.MENU_NETWORK);

		eventService.registerEvent(ApplicationLauncherEvent.class, RESOURCE_BUNDLE, this);
		eventService.registerEvent(ApplicationLauncherCreatedEvent.class, RESOURCE_BUNDLE, this);
		eventService.registerEvent(ApplicationLauncherUpdatedEvent.class, RESOURCE_BUNDLE, this);
		eventService.registerEvent(ApplicationLauncherDeletedEvent.class, RESOURCE_BUNDLE, this);

		indexPage.addScript("${uiPath}/js/launchers.js");

		EntityResourcePropertyStore.registerResourceService(ApplicationLauncherResource.class, repository);
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
	protected void fireResourceCreationEvent(ApplicationLauncherResource resource) {
		eventService.publishEvent(new ApplicationLauncherCreatedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceCreationEvent(ApplicationLauncherResource resource, Throwable t) {
		eventService.publishEvent(new ApplicationLauncherCreatedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceUpdateEvent(ApplicationLauncherResource resource) {
		eventService.publishEvent(new ApplicationLauncherUpdatedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceUpdateEvent(ApplicationLauncherResource resource, Throwable t) {
		eventService.publishEvent(new ApplicationLauncherUpdatedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceDeletionEvent(ApplicationLauncherResource resource) {
		eventService.publishEvent(new ApplicationLauncherDeletedEvent(this, getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceDeletionEvent(ApplicationLauncherResource resource, Throwable t) {
		eventService.publishEvent(new ApplicationLauncherDeletedEvent(this, resource, t, getCurrentSession()));
	}

	@Override
	public ApplicationLauncherResource updateResource(ApplicationLauncherResource resource, String name,
			Map<String, String> properties) throws ResourceException, AccessDeniedException {

		resource.setName(name);

		updateResource(resource, properties);

		return resource;
	}

	@Override
	public ApplicationLauncherResource createResource(String name, Realm realm, Map<String, String> properties)
			throws ResourceException, AccessDeniedException {

		ApplicationLauncherResource resource = new ApplicationLauncherResource();
		resource.setName(name);
		resource.setRealm(realm);

		createResource(resource, properties);

		return resource;
	}

	@Override
	public BootstrapTableResult<?> searchTemplates(String resolver, String search, int iDisplayStart,
			int iDisplayLength) throws IOException, AccessDeniedException {
		assertPermission(ApplicationLauncherResourcePermission.CREATE);
		return helper.searchTemplates(resolver, search, iDisplayStart, iDisplayLength);
	}

	@Override
	public ApplicationLauncherResource createFromTemplate(final String script)
			throws ResourceException, AccessDeniedException {
		assertPermission(ApplicationLauncherResourcePermission.CREATE);
		return helper.createFromTemplate(script);

	}

	@Override
	public void registerTemplateResolver(ApplicationTemplateResolver<ApplicationLauncherTemplate> resolver) {
		helper.registerTemplateResolver(resolver);
	}

	@Override
	public void downloadTemplateImage(String uuid, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		helper.downloadTemplateImage(uuid, request, response);
	}
}
