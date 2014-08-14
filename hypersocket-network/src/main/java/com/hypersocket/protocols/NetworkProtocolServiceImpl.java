package com.hypersocket.protocols;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hypersocket.events.EventService;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.menus.MenuService;
import com.hypersocket.network.NetworkTransport;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.protocols.events.NetworkProtocolCreatedEvent;
import com.hypersocket.protocols.events.NetworkProtocolDeletedEvent;
import com.hypersocket.protocols.events.NetworkProtocolUpdatedEvent;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AbstractResourceRepository;
import com.hypersocket.resource.AbstractResourceServiceImpl;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;

@Service
public class NetworkProtocolServiceImpl extends
		AbstractResourceServiceImpl<NetworkProtocol> implements
		NetworkProtocolService {

	public static final String RESOURCE_BUNDLE = "NetworkProtocolService";

	@Autowired
	NetworkProtocolRepository repository;

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
				RESOURCE_BUNDLE, "category.websites");

		for (NetworkProtocolPermission p : NetworkProtocolPermission.values()) {
			permissionService.registerPermission(p.getResourceKey(), p.isSystem(), cat);
		}

		/**
		 * TODO add your menu item and other initialization.
		 */
		// menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE,
		// "resources", "fa-globe", "resources", 100,
		// TemplateResourcePermission.READ, TemplateResourcePermission.CREATE,
		// TemplateResourcePermission.UPDATE,
		// TemplateResourcePermission.DELETE),
		// MenuService.MENU_RESOURCES);

		/**
		 * Register the events. All events have to be registerd so the system
		 * knows about them.
		 */
		eventService.registerEvent(NetworkProtocolCreatedEvent.class,
				RESOURCE_BUNDLE);
		eventService.registerEvent(NetworkProtocolUpdatedEvent.class,
				RESOURCE_BUNDLE);
		eventService.registerEvent(NetworkProtocolDeletedEvent.class,
				RESOURCE_BUNDLE);

	}

	@Override
	protected AbstractResourceRepository<NetworkProtocol> getRepository() {
		return repository;
	}

	@Override
	protected String getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

	@Override
	public Class<NetworkProtocolPermission> getPermissionType() {
		return NetworkProtocolPermission.class;
	}

	@Override
	protected void fireResourceCreationEvent(NetworkProtocol resource) {
		eventService.publishEvent(new NetworkProtocolCreatedEvent(this,
				getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceCreationEvent(NetworkProtocol resource,
			Throwable t) {
		eventService.publishEvent(new NetworkProtocolCreatedEvent(this,
				resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceUpdateEvent(NetworkProtocol resource) {
		eventService.publishEvent(new NetworkProtocolUpdatedEvent(this,
				getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceUpdateEvent(NetworkProtocol resource, Throwable t) {
		eventService.publishEvent(new NetworkProtocolUpdatedEvent(this,
				resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceDeletionEvent(NetworkProtocol resource) {
		eventService.publishEvent(new NetworkProtocolDeletedEvent(this,
				getCurrentSession(), resource));
	}

	@Override
	protected void fireResourceDeletionEvent(NetworkProtocol resource,
			Throwable t) {
		eventService.publishEvent(new NetworkProtocolDeletedEvent(this,
				resource, t, getCurrentSession()));
	}

	@Override
	public NetworkProtocol updateResource(NetworkProtocol resource,
			String name, Integer startPort, Integer endPort,
			NetworkTransport transport) throws ResourceChangeException,
			AccessDeniedException {

		resource.setName(name);
		resource.setStartPort(startPort);
		resource.setEndPort(endPort);
		resource.setTransport(transport);
		
		updateResource(resource);

		return resource;
	}

	@Override
	public NetworkProtocol createResource(String name, Realm realm,
			Integer startPort, Integer endPort, NetworkTransport transport)
			throws ResourceCreationException, AccessDeniedException {

		NetworkProtocol resource = new NetworkProtocol();
		resource.setName(name);
		resource.setStartPort(startPort);
		resource.setEndPort(endPort);
		resource.setTransport(transport);

		createResource(resource);

		return resource;
	}
}
