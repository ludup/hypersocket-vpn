/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hypersocket.events.EventService;
import com.hypersocket.i18n.I18N;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;
import com.hypersocket.network.events.NetworkResourceCreatedEvent;
import com.hypersocket.network.events.NetworkResourceDeletedEvent;
import com.hypersocket.network.events.NetworkResourceSessionClosed;
import com.hypersocket.network.events.NetworkResourceSessionOpened;
import com.hypersocket.network.events.NetworkResourceUpdatedEvent;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.permissions.Role;
import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.realm.Realm;
import com.hypersocket.realm.RealmService;
import com.hypersocket.resource.AbstractAssignableResourceRepository;
import com.hypersocket.resource.AbstractAssignableResourceServiceImpl;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.session.Session;
import com.hypersocket.ui.UserInterfaceContentHandler;

@Service
public class NetworkResourceServiceImpl extends
		AbstractAssignableResourceServiceImpl<NetworkResource> implements
		NetworkResourceService {

	public static final String MENU_NETWORK = "networkResources";

	static Logger log = LoggerFactory
			.getLogger(NetworkResourceServiceImpl.class);

	@Autowired
	NetworkResourceRepository resourceRepository;

	@Autowired
	RealmService realmService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	I18NService i18nService;

	@Autowired
	MenuService menuService;

	@Autowired
	EventService eventService;

	public NetworkResourceServiceImpl() {

	}

	@PostConstruct
	public void postConstruct() {

		if (log.isDebugEnabled()) {
			log.debug("Constructing NetworkResourceService");
		}

		i18nService.registerBundle(NetworkResourceService.RESOURCE_BUNDLE);

		PermissionCategory cat = permissionService.registerPermissionCategory(
				NetworkResourceService.RESOURCE_BUNDLE,
				"category.networkResources");

		for (NetworkResourcePermission p : NetworkResourcePermission.values()) {
			permissionService.registerPermission(p.getResourceKey(), cat);
		}

		menuService.registerMenu(new MenuRegistration(
				NetworkResourceService.RESOURCE_BUNDLE, MENU_NETWORK,
				"fa-sitemap", "networkResources", 100,
				NetworkResourcePermission.READ,
				NetworkResourcePermission.CREATE,
				NetworkResourcePermission.UPDATE,
				NetworkResourcePermission.DELETE), MenuService.MENU_RESOURCES);

		menuService.registerMenu(new MenuRegistration(
				NetworkResourceService.RESOURCE_BUNDLE, "endpoints",
				"fa-sitemap", "endpoints", 100, NetworkResourcePermission.READ,
				NetworkResourcePermission.CREATE,
				NetworkResourcePermission.UPDATE,
				NetworkResourcePermission.DELETE), MENU_NETWORK);

		menuService.registerMenu(new MenuRegistration(
				NetworkResourceService.RESOURCE_BUNDLE, "protocols",
				"fa-exchange", "protocols", 200,
				NetworkResourcePermission.READ,
				NetworkResourcePermission.CREATE,
				NetworkResourcePermission.UPDATE,
				NetworkResourcePermission.DELETE), MENU_NETWORK);

		eventService.registerEvent(NetworkResourceCreatedEvent.class,
				NetworkResourceService.RESOURCE_BUNDLE);
		eventService.registerEvent(NetworkResourceUpdatedEvent.class,
				NetworkResourceService.RESOURCE_BUNDLE);
		eventService.registerEvent(NetworkResourceDeletedEvent.class,
				NetworkResourceService.RESOURCE_BUNDLE);

		eventService.registerEvent(NetworkResourceSessionOpened.class,
				NetworkResourceService.RESOURCE_BUNDLE);
		eventService.registerEvent(NetworkResourceSessionClosed.class,
				NetworkResourceService.RESOURCE_BUNDLE);

		if (log.isDebugEnabled()) {
			log.debug("NetworkResourceService constructed");
		}
	}

	@Override
	public void verifyResourceSession(NetworkResource resource,
			String hostname, int port, NetworkTransport transport,
			Session session) throws AccessDeniedException {

		if (log.isDebugEnabled()) {
			log.debug("Requested NetworkResource session for resource "
					+ resource.getName() + " on session " + session.getId());
		}

		verifyPort(resource, port, transport);

		if (log.isDebugEnabled()) {
			log.debug("Verified NetworkResource, creating resource session for resource "
					+ resource.getName() + " on session " + session.getId());
		}
	}

	public NetworkProtocol verifyPort(NetworkResource resource, Integer port,
			NetworkTransport transport) throws AccessDeniedException {

		if (log.isDebugEnabled()) {
			log.debug("Verifying port " + port + " for resource "
					+ resource.getName());
		}

		NetworkProtocol protocol = resource.getNetworkProtocol(port, transport);

		if (protocol == null) {
			throw new AccessDeniedException(I18N.getResource(
					getCurrentLocale(), RESOURCE_BUNDLE,
					"error.portNoAuthorized", port, resource.getName()));
		}

		return protocol;

	}

	@Override
	public NetworkResource updateResource(NetworkResource resource,
			String name, String hostname, String destinationHostname,
			Set<NetworkProtocol> protocols,
			Set<ApplicationLauncherResource> launchers, Set<Role> roles)
			throws ResourceChangeException, AccessDeniedException {

		assertPermission(NetworkResourcePermission.UPDATE);

		resource.setName(name);
		resource.setHostname(hostname);
		resource.setDestinationHostname(destinationHostname);
		resource.setProtocols(protocols);
		resource.setLaunchers(launchers);
		resource.setRoles(roles);

		updateResource(resource);

		return resource;

	}

	@Override
	public NetworkResource createResource(String name, String hostname,
			String destinationHostname, Set<NetworkProtocol> protocols,
			Set<ApplicationLauncherResource> launchers, Set<Role> roles,
			Realm realm) throws ResourceCreationException,
			AccessDeniedException {

		NetworkResource resource = new NetworkResource();

		resource.setName(name);
		resource.setHostname(hostname);
		resource.setDestinationHostname(destinationHostname);
		resource.setRealm(realm);
		resource.setProtocols(protocols);
		resource.setLaunchers(launchers);
		resource.setRoles(roles);

		createResource(resource);

		return resource;

	}

	@Override
	protected AbstractAssignableResourceRepository<NetworkResource> getRepository() {
		return resourceRepository;
	}

	@Override
	protected String getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

	@Override
	public Class<?> getPermissionType() {
		return NetworkResourcePermission.class;
	}

	@Override
	protected void fireResourceCreationEvent(NetworkResource resource) {
		eventService.publishEvent(new NetworkResourceCreatedEvent(this,
				resource, getCurrentSession()));
	}

	@Override
	protected void fireResourceCreationEvent(NetworkResource resource,
			Throwable t) {
		eventService.publishEvent(new NetworkResourceCreatedEvent(this,
				resource, t, getCurrentSession()));

	}

	@Override
	protected void fireResourceUpdateEvent(NetworkResource resource) {
		eventService.publishEvent(new NetworkResourceUpdatedEvent(this,
				resource, getCurrentSession()));

	}

	@Override
	protected void fireResourceUpdateEvent(NetworkResource resource, Throwable t) {
		eventService.publishEvent(new NetworkResourceUpdatedEvent(this,
				resource, t, getCurrentSession()));
	}

	@Override
	protected void fireResourceDeletionEvent(NetworkResource resource) {
		eventService.publishEvent(new NetworkResourceDeletedEvent(this,
				resource, getCurrentSession()));
	}

	@Override
	protected void fireResourceDeletionEvent(NetworkResource resource,
			Throwable t) {
		eventService.publishEvent(new NetworkResourceDeletedEvent(this,
				resource, t, getCurrentSession()));
	}

}
