package com.hypersocket.protocols;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hypersocket.events.EventService;
import com.hypersocket.i18n.I18NService;
import com.hypersocket.menus.MenuRegistration;
import com.hypersocket.menus.MenuService;
import com.hypersocket.network.NetworkResourceServiceImpl;
import com.hypersocket.network.NetworkTransport;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionCategory;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.protocols.events.NetworkProtocolCreatedEvent;
import com.hypersocket.protocols.events.NetworkProtocolDeletedEvent;
import com.hypersocket.protocols.events.NetworkProtocolEvent;
import com.hypersocket.protocols.events.NetworkProtocolUpdatedEvent;
import com.hypersocket.realm.Realm;
import com.hypersocket.realm.RealmAdapter;
import com.hypersocket.realm.RealmService;
import com.hypersocket.resource.AbstractResourceRepository;
import com.hypersocket.resource.AbstractResourceServiceImpl;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;

@Service
public class NetworkProtocolServiceImpl extends
		AbstractResourceServiceImpl<NetworkProtocol> implements
		NetworkProtocolService {

	static Logger log = LoggerFactory
			.getLogger(NetworkProtocolServiceImpl.class);

	public static final String NETWORK_PROTOCOLS_ACTIONS = "networkProtocolsActions";

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

	@Autowired
	RealmService realmService;

	public NetworkProtocolServiceImpl() {
		super("networkProtocol");
	}

	@PostConstruct
	private void postConstruct() {

		i18nService.registerBundle(RESOURCE_BUNDLE);

		PermissionCategory cat = permissionService.registerPermissionCategory(
				RESOURCE_BUNDLE, "category.websites");

		repository.loadPropertyTemplates("networkProtocolTemplate.xml");

		for (NetworkProtocolPermission p : NetworkProtocolPermission.values()) {
			permissionService.registerPermission(p, cat);
		}

		menuService.registerMenu(new MenuRegistration(
				NetworkResourceServiceImpl.RESOURCE_BUNDLE, "protocols",
				"fa-exchange", "protocols", 200,
				NetworkProtocolPermission.READ,
				NetworkProtocolPermission.CREATE,
				NetworkProtocolPermission.UPDATE,
				NetworkProtocolPermission.DELETE),
				NetworkResourceServiceImpl.MENU_NETWORK);

		menuService.registerExtendableTable(NETWORK_PROTOCOLS_ACTIONS);

		eventService.registerEvent(NetworkProtocolEvent.class, RESOURCE_BUNDLE,
				this);
		eventService.registerEvent(NetworkProtocolCreatedEvent.class,
				RESOURCE_BUNDLE, this);
		eventService.registerEvent(NetworkProtocolUpdatedEvent.class,
				RESOURCE_BUNDLE, this);
		eventService.registerEvent(NetworkProtocolDeletedEvent.class,
				RESOURCE_BUNDLE, this);

		realmService.registerRealmListener(new RealmAdapter() {

			@Override
			public void onCreateRealm(Realm realm) {
				createDefaultProtocols(realm);
			}

			@Override
			public boolean hasCreatedDefaultResources(Realm realm) {
				return repository.getResourceCount(realm, "") > 0;
			}

		});
	}

	private void createDefaultProtocols(Realm realm) {

		createProtocol(realm, "FTP (Data)", NetworkTransport.BOTH, 20, null);
		createProtocol(realm, "FTP (Control)", NetworkTransport.TCP, 21, null);
		createProtocol(realm, "FTPS (Data)", NetworkTransport.BOTH, 989, null);
		createProtocol(realm, "FTPS (Control)", NetworkTransport.BOTH, 990,
				null);
		createProtocol(realm, "Telnet", NetworkTransport.TCP, 23, null);
		createProtocol(realm, "SSH", NetworkTransport.TCP, 22, null);
		createProtocol(realm, "VNC", NetworkTransport.TCP, 5900, 5910);
		createProtocol(realm, "VNC:0", NetworkTransport.TCP, 5900, null);
		createProtocol(realm, "VNC:1", NetworkTransport.TCP, 5901, null);
		createProtocol(realm, "VNC:2", NetworkTransport.TCP, 5902, null);
		createProtocol(realm, "VNC:3", NetworkTransport.TCP, 5903, null);
		createProtocol(realm, "VNC:4", NetworkTransport.TCP, 5904, null);
		createProtocol(realm, "VNC:5", NetworkTransport.TCP, 5905, null);
		createProtocol(realm, "RDP", NetworkTransport.TCP, 3389, null);
		createProtocol(realm, "HTTP", NetworkTransport.TCP, 80, null);
		createProtocol(realm, "HTTPS", NetworkTransport.TCP, 443, null);
		createProtocol(realm, "POP3", NetworkTransport.TCP, 110, null);
		createProtocol(realm, "SSL-POP", NetworkTransport.TCP, 995, null);
		createProtocol(realm, "SMTP", NetworkTransport.TCP, 25, null);
		createProtocol(realm, "SSMTP", NetworkTransport.TCP, 465, null);
		createProtocol(realm, "IMAP", NetworkTransport.TCP, 143, null);
		createProtocol(realm, "IMAP4-SSL", NetworkTransport.TCP, 585, null);
		createProtocol(realm, "IMAPS", NetworkTransport.TCP, 993, null);
		createProtocol(realm, "LDAP", NetworkTransport.TCP, 389, null);
		createProtocol(realm, "LDAPS", NetworkTransport.TCP, 636, null);
		createProtocol(realm, "IPP", NetworkTransport.BOTH, 631, null);
	}

	@SuppressWarnings("unchecked")
	void createProtocol(Realm realm, String name, NetworkTransport transport,
			Integer start, Integer end) {
		NetworkProtocol protocol = new NetworkProtocol();
		protocol.setRealm(realm);
		protocol.setName(name);
		protocol.setTransport(transport);
		protocol.setStartPort(start);
		protocol.setEndPort(end);

		repository.saveResource(protocol, new HashMap<String, String>());

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

	protected Class<NetworkProtocol> getResourceClass() {
		return NetworkProtocol.class;
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

		updateResource(resource, new HashMap<String, String>());

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
		resource.setRealm(realm);

		createResource(resource, new HashMap<String, String>());

		return resource;
	}
}
