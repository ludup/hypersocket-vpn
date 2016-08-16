package com.hypersocket.launcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hypersocket.attributes.user.UserAttributeService;
import com.hypersocket.events.EventService;
import com.hypersocket.http.HttpUtilsImpl;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.launcher.events.ApplicationLauncherCreatedEvent;
import com.hypersocket.launcher.events.ApplicationLauncherDeletedEvent;
import com.hypersocket.launcher.events.ApplicationLauncherEvent;
import com.hypersocket.launcher.events.ApplicationLauncherUpdatedEvent;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;
import com.hypersocket.netty.HttpRequestDispatcherHandler;
import com.hypersocket.network.NetworkResourceServiceImpl;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.realm.Realm;
import com.hypersocket.realm.RealmService;
import com.hypersocket.resource.AbstractResourceRepository;
import com.hypersocket.resource.AbstractResourceServiceImpl;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.tables.BootstrapTableResult;
import com.hypersocket.transactions.TransactionService;

@Service
public class ApplicationLauncherResourceServiceImpl extends
		AbstractResourceServiceImpl<ApplicationLauncherResource> implements
		ApplicationLauncherResourceService {

	static Logger log = LoggerFactory.getLogger(ApplicationLauncherResourceServiceImpl.class);
	
	public static final String RESOURCE_BUNDLE = "LauncherService";

	public static final String APPLICATION_LAUNCHER_ACTIONS = "applicationLauncherActions";

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

	@Autowired
	RealmService realmService;

	@Autowired
	UserAttributeService attributeService;

	@Autowired
	TransactionService transactionService;
	
	@Autowired
	HttpUtilsImpl httpUtils;
	
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

		eventService.registerEvent(ApplicationLauncherEvent.class,
				RESOURCE_BUNDLE, this);
		eventService.registerEvent(ApplicationLauncherCreatedEvent.class,
				RESOURCE_BUNDLE, this);
		eventService.registerEvent(ApplicationLauncherUpdatedEvent.class,
				RESOURCE_BUNDLE, this);
		eventService.registerEvent(ApplicationLauncherDeletedEvent.class,
				RESOURCE_BUNDLE, this);

		repository.getEntityStore().registerResourceService(
				ApplicationLauncherResource.class, repository);
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
			ApplicationLauncherResource resource, 
			String name, Map<String,String> properties) throws ResourceChangeException,
			AccessDeniedException {

		resource.setName(name);
		
		updateResource(resource, properties);

		return resource;
	}

	@Override
	public ApplicationLauncherResource createResource(String name, Realm realm,
			Map<String,String> properties)
			throws ResourceCreationException, AccessDeniedException {

		ApplicationLauncherResource resource = new ApplicationLauncherResource();
		resource.setName(name);
		resource.setRealm(realm);

		createResource(resource, properties);

		return resource;
	}

	@Override
	public BootstrapTableResult searchTemplates(String search, int iDisplayStart,
			int iDisplayLength) throws IOException, AccessDeniedException {

		assertPermission(ApplicationLauncherResourcePermission.CREATE);

		Map<String, String> params = new HashMap<String, String>();
		params.put("search", search);
		params.put("offset", String.valueOf(iDisplayStart));
		params.put("limit", String.valueOf(iDisplayLength));
		params.put("order", "asc");

		String json = httpUtils
				.doHttpPost(
						System.getProperty("hypersocket.templateServerUrl",
								"https://templates1x.hypersocket.com/hypersocket/api/templates")
								+ "/"
								+ (Boolean
										.getBoolean("hypersocketLauncher.enablePrivate") ? "developer"
										: "table") + "/2", params, true);

		ObjectMapper mapper = new ObjectMapper();

		return mapper.readValue(json, BootstrapTableResult.class);
	}

	@Override
	public void downloadTemplateImage(String uuid, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		request.setAttribute(
				HttpRequestDispatcherHandler.CONTENT_INPUTSTREAM,
				httpUtils.doHttpGet(
						System.getProperty(
								"hypersocket.templateServerImageUrl",
								"https://templates1x.hypersocket.com/hypersocket/api/templates/image/")
								+ uuid, true));

	}

	@Override
	public ApplicationLauncherResource createFromTemplate(final String script)
			throws ResourceException, AccessDeniedException {

		assertPermission(ApplicationLauncherResourcePermission.CREATE);

		ApplicationLauncherResource result = transactionService
				.doInTransaction(new TransactionCallback<ApplicationLauncherResource>() {

					@Override
					public ApplicationLauncherResource doInTransaction(
							TransactionStatus status) {

						ScriptEngineManager manager = new ScriptEngineManager();
						ScriptEngine engine = manager
								.getEngineByName("beanshell");

						Bindings bindings = engine.createBindings();
						bindings.put("realmService", realmService);
						bindings.put("templateService",
								ApplicationLauncherResourceServiceImpl.this);
						bindings.put("attributeService", attributeService);
						bindings.put("log", log);

						try {
							Object result = engine.eval(script, bindings);
							if (result instanceof ApplicationLauncherResource) {
								return (ApplicationLauncherResource) result;
							} else {
								throw new IllegalStateException(
										"Transaction failed",
										new ResourceCreationException(
												RESOURCE_BUNDLE,
												"error.templateFailed",
												"Script returned invalid object"));
							}
						} catch (ScriptException e) {
							log.error(
									"Failed to create application launcher from template",
									e);
							if (e.getCause() instanceof ResourceCreationException) {
								throw new IllegalStateException(
										"Transaction failed", e.getCause());
							}
							throw new IllegalStateException(
									"Transaction failed",
									new ResourceCreationException(
											RESOURCE_BUNDLE,
											"error.templateFailed", e
													.getMessage()));
						}
					}

				});

		return result;

	}
}
