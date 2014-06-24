/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hypersocket.events.EventService;
import com.hypersocket.i18n.I18N;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;
import com.hypersocket.network.events.NetworkResourceSessionClosed;
import com.hypersocket.network.events.NetworkResourceSessionOpened;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.permissions.Role;
import com.hypersocket.realm.Realm;
import com.hypersocket.realm.RealmService;
import com.hypersocket.resource.AbstractAssignableResourceRepository;
import com.hypersocket.resource.AbstractAssignableResourceServiceImpl;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.session.Session;
import com.hypersocket.tables.ColumnSort;
import com.hypersocket.ui.UserInterfaceContentHandler;

@Service
public class NetworkResourceServiceImpl extends
		AbstractAssignableResourceServiceImpl<NetworkResource> implements
		NetworkResourceService {

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
	
	@Autowired
	UserInterfaceContentHandler jQueryUIContentHandler;

	public NetworkResourceServiceImpl() {

	}

	@PostConstruct
	public void postConstruct() {

		if (log.isDebugEnabled()) {
			log.debug("Constructing NetworkResourceService");
		}

		i18nService.registerBundle(RESOURCE_BUNDLE);

		PermissionCategory cat = permissionService.registerPermissionCategory(
				RESOURCE_BUNDLE, "category.networkResources");

		for (NetworkResourcePermission p : NetworkResourcePermission.values()) {
			permissionService.registerPermission(p.getResourceKey(), cat);
		}

		menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE, "networkResources", "fa-sitemap",
				"networkResources", 100, NetworkResourcePermission.READ,
				NetworkResourcePermission.CREATE,
				NetworkResourcePermission.UPDATE,
				NetworkResourcePermission.DELETE),
				MenuService.MENU_RESOURCES);

		menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE, "endpoints", "fa-sitemap",
				"endpoints", 100, NetworkResourcePermission.READ,
				NetworkResourcePermission.CREATE,
				NetworkResourcePermission.UPDATE,
				NetworkResourcePermission.DELETE),
				"networkResources");

		menuService.registerMenu(new MenuRegistration(RESOURCE_BUNDLE, "protocols", "fa-exchange",
				"protocols", 200, NetworkResourcePermission.READ,
				NetworkResourcePermission.CREATE,
				NetworkResourcePermission.UPDATE,
				NetworkResourcePermission.DELETE),
				"networkResources");
		
		
		eventService.registerEvent(NetworkResourceSessionOpened.class, RESOURCE_BUNDLE);
		eventService.registerEvent(NetworkResourceSessionClosed.class, RESOURCE_BUNDLE);
		
		if (log.isDebugEnabled()) {
			log.debug("NetworkResourceService constructed");
		}
	}

	@Override
	public NetworkProtocol verifyResourceSession(NetworkResource resource,
			Integer port, NetworkTransport transport, Session session)
			throws AccessDeniedException {

		if (log.isDebugEnabled()) {
			log.debug("Requested NetworkResource session for resource "
					+ resource.getName() + " on session " + session.getId());
		}

		NetworkProtocol protocol = verifyPort(resource, port, transport);

		if (log.isDebugEnabled()) {
			log.debug("Verified NetworkResource, creating resource session for resource "
					+ resource.getName() + " on session " + session.getId());
		}
		return protocol;

	}

	public NetworkProtocol verifyPort(NetworkResource resource, Integer port,
			NetworkTransport transport) throws AccessDeniedException {

		if (log.isDebugEnabled()) {
			log.debug("Verifying port " + port + " for resource "
					+ resource.getName());
		}

		for (NetworkProtocol protocol : resource.getProtocols()) {

			if (log.isDebugEnabled()) {
				log.debug("Checking against " + protocol.getName()
						+ " startPort=" + protocol.getStartPort() + " endPort="
						+ protocol.getEndPort());
			}
			if (protocol.getTransport() == transport
					|| protocol.getTransport() == NetworkTransport.BOTH) {
				if (protocol.getEndPort() != null) {
					if (protocol.getStartPort().intValue() >= port.intValue()
							&& port.intValue() <= protocol.getEndPort()
									.intValue()) {
						if (log.isDebugEnabled()) {
							log.debug("Matched port " + port
									+ " with protocol " + protocol.getName());
						}
						return protocol;
					}
				} else {
					if (protocol.getStartPort().equals(port)) {
						if (log.isDebugEnabled()) {
							log.debug("Matched port " + port
									+ " with protocol " + protocol.getName());
						}
						return protocol;
					}
				}
			}
		}

		throw new AccessDeniedException(I18N.getResource(
					getCurrentLocale(), RESOURCE_BUNDLE,
					"error.portNoAuthorized", port, resource.getName()));
		
	}

	@Override
	public List<NetworkProtocol> getProtocols() {
		return resourceRepository.getProtocols();
	}
	
	@Override
	public long getProtocolCount(String searchPattern) throws AccessDeniedException {
		
		assertPermission(NetworkResourcePermission.READ);
		
		return resourceRepository.getProtocolCount(searchPattern);
	}
	
	@Override
	public List<NetworkProtocol> searchProtocols(String searchPattern, int start, int length, ColumnSort[] sorting) throws AccessDeniedException {
		
		assertPermission(NetworkResourcePermission.READ);
		
		return resourceRepository.searchProtocols(searchPattern, start, length, sorting);
	}

	@Override
	public NetworkProtocol getProtocolById(Long id)
			throws ResourceNotFoundException {
		NetworkProtocol protocol = resourceRepository.getProtocolById(id);
		if (protocol == null) {
			throw new ResourceNotFoundException(RESOURCE_BUNDLE,
					"error.invalidProtocolId", id);
		}
		return protocol;
	}

	@Override
	public NetworkProtocol updateProtocol(NetworkProtocol protocol,
			String name, NetworkTransport transport, Integer startPort,
			Integer endPort) throws ResourceChangeException,
			AccessDeniedException {

		assertPermission(NetworkResourcePermission.UPDATE);

		NetworkProtocol existing = resourceRepository.getProtocolByName(name);
		if (existing != null && !existing.equals(protocol)) {
			throw new ResourceChangeException(RESOURCE_BUNDLE,
					"error.nameExists", name, transport.toString());
		}

		protocol.setName(name);
		protocol.setTransport(transport);
		protocol.setStartPort(startPort);
		protocol.setEndPort(endPort);

		resourceRepository.saveProtocol(protocol);

		return protocol;

	}

	@Override
	public NetworkProtocol createProtocol(String name,
			NetworkTransport transport, Integer startPort, Integer endPort)
			throws ResourceCreationException, AccessDeniedException {

		assertPermission(NetworkResourcePermission.CREATE);

		NetworkProtocol existing = resourceRepository.getProtocolByName(name);
		if (existing != null) {
			throw new ResourceCreationException(RESOURCE_BUNDLE,
					"error.nameExists", name, transport.toString());
		}

		if (endPort != null) {
			if (endPort < startPort) {
				throw new ResourceCreationException(RESOURCE_BUNDLE,
						"error.portEndError");
			}
		}

		if (startPort < 1 || startPort > 65535) {
			throw new ResourceCreationException(RESOURCE_BUNDLE,
					"error.portNumberError");
		}

		if (endPort != null && (endPort < 1 || endPort > 65535)) {
			throw new ResourceCreationException(RESOURCE_BUNDLE,
					"error.portNumberError");
		}

		NetworkProtocol protocol = new NetworkProtocol();

		protocol.setName(name);
		protocol.setTransport(transport);
		protocol.setStartPort(startPort);
		protocol.setEndPort(endPort);

		resourceRepository.saveProtocol(protocol);

		return protocol;

	}

	@Override
	public void deleteProtocol(NetworkProtocol protocol)
			throws AccessDeniedException, ResourceChangeException {

		assertPermission(NetworkResourcePermission.DELETE);

		List<NetworkResource> resources = resourceRepository
				.getResourcesByProtocol(protocol);
		if (resources.size() > 0) {
			throw new ResourceChangeException(RESOURCE_BUNDLE,
					"error.protocolInUse", protocol.getName(), resources.size());
		}

		resourceRepository.deleteProtocol(protocol);

	}

	@Override
	public NetworkResource updateResource(NetworkResource resource,
			String name, String hostname, String destinationHostname,
			Set<NetworkProtocol> protocols, Set<Role> roles)
			throws ResourceChangeException, AccessDeniedException {

		assertPermission(NetworkResourcePermission.UPDATE);

		resource.setName(name);
		resource.setHostname(hostname);
		resource.setDestinationHostname(destinationHostname);
		resource.setProtocols(protocols);
		resource.setRoles(roles);

		updateResource(resource);

		return resource;

	}

	@Override
	public NetworkResource createResource(String name, String hostname,
			String destinationHostname, Set<NetworkProtocol> protocols,
			Set<Role> roles, Realm realm) throws ResourceCreationException,
			AccessDeniedException {

		NetworkResource resource = new NetworkResource();
		
		resource.setName(name);
		resource.setHostname(hostname);
		resource.setDestinationHostname(destinationHostname);
		resource.setRealm(realm);
		resource.setProtocols(protocols);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fireResourceCreationEvent(NetworkResource resource,
			Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fireResourceUpdateEvent(NetworkResource resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fireResourceUpdateEvent(NetworkResource resource, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fireResourceDeletionEvent(NetworkResource resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fireResourceDeletionEvent(NetworkResource resource,
			Throwable t) {
		// TODO Auto-generated method stub
		
	}

}
