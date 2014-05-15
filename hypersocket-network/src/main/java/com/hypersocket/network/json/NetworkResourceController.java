/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network.json;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hypersocket.auth.json.AuthenticationRequired;
import com.hypersocket.auth.json.ResourceController;
import com.hypersocket.auth.json.UnauthorizedException;
import com.hypersocket.i18n.I18N;
import com.hypersocket.json.ResourceList;
import com.hypersocket.json.ResourceStatus;
import com.hypersocket.network.NetworkProtocol;
import com.hypersocket.network.NetworkProtocolColumns;
import com.hypersocket.network.NetworkResource;
import com.hypersocket.network.NetworkResourceColumns;
import com.hypersocket.network.NetworkResourceService;
import com.hypersocket.network.NetworkTransport;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.PermissionService;
import com.hypersocket.permissions.Role;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.ResourceChangeException;
import com.hypersocket.resource.ResourceCreationException;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.session.json.SessionTimeoutException;
import com.hypersocket.tables.Column;
import com.hypersocket.tables.ColumnSort;
import com.hypersocket.tables.DataTablesResult;
import com.hypersocket.tables.json.DataTablesPageProcessor;

@Controller
public class NetworkResourceController extends ResourceController {

	@Autowired
	NetworkResourceService networkService;

	@Autowired
	PermissionService permissionService;

	@AuthenticationRequired
	@RequestMapping(value = "myNetworkResources", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<NetworkResource> getResourcesByCurrentPrincipal(
			HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);
		try {
			return new ResourceList<NetworkResource>(
					networkService.getResources(sessionUtils
							.getPrincipal(request)));
		} finally {
			clearAuthenticatedContext(networkService);
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "networkResources", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<NetworkResource> getResources(
			HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);
		try {
			return new ResourceList<NetworkResource>(
					networkService.getResources(sessionUtils
							.getCurrentRealm(request)));
		} finally {
			clearAuthenticatedContext(networkService);
		}
	}
	
	@AuthenticationRequired
	@RequestMapping(value = "table/networkResources", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public DataTablesResult tableNetworkResources(final HttpServletRequest request,
			HttpServletResponse response) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);

		try {
			return processDataTablesRequest(request,
					new DataTablesPageProcessor() {

						@Override
						public Column getColumn(int col) {
							return NetworkResourceColumns.values()[col];
						}

						@Override
						public List<?> getPage(String searchPattern, int start, int length,
								ColumnSort[] sorting) throws UnauthorizedException, AccessDeniedException {
							return networkService.searchResources(sessionUtils.getCurrentRealm(request), searchPattern, start, length, sorting);
						}
						
						@Override
						public Long getTotalCount(String searchPattern) throws UnauthorizedException, AccessDeniedException {
							return networkService.getResourceCount(sessionUtils.getCurrentRealm(request), searchPattern);
						}
					});
		} finally {
			clearAuthenticatedContext();
		}
	}
	
	@AuthenticationRequired
	@RequestMapping(value = "table/networkProtocols", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public DataTablesResult tableNetworkProtocols(final HttpServletRequest request,
			HttpServletResponse response) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);

		try {
			return processDataTablesRequest(request,
					new DataTablesPageProcessor() {

						@Override
						public Column getColumn(int col) {
							return NetworkProtocolColumns.values()[col];
						}

						@Override
						public List<?> getPage(String searchPattern, int start, int length,
								ColumnSort[] sorting) throws UnauthorizedException, AccessDeniedException {
							return networkService.searchProtocols(searchPattern, start, length, sorting);
						}
						
						@Override
						public Long getTotalCount(String searchPattern) throws UnauthorizedException, AccessDeniedException {
							return networkService.getProtocolCount(searchPattern);
						}
					});
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "template/networkResource", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<PropertyCategory> getResourceTemplate(HttpServletRequest request)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {
		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));

		try {
			return new ResourceList<PropertyCategory>();
		} finally {
			clearAuthenticatedContext();
		}	
	}
	
	@AuthenticationRequired
	@RequestMapping(value = "networkResource/{id}", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public NetworkResource getResource(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("id") Long id)
			throws AccessDeniedException, UnauthorizedException,
			ResourceNotFoundException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);
		try {
			return networkService.getResourceById(id);
		} finally {
			clearAuthenticatedContext(networkService);
		}

	}

	@AuthenticationRequired
	@RequestMapping(value = "networkResource", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<NetworkResource> createOrUpdateNetworkResource(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody NetworkResourceUpdate resource)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);
		try {

			NetworkResource newResource;

			Realm realm = sessionUtils.getCurrentRealm(request);

			Set<NetworkProtocol> protocols = new HashSet<NetworkProtocol>();
			for (Long id : resource.getProtocols()) {
				protocols.add(networkService.getProtocolById(id));
			}

			Set<Role> roles = new HashSet<Role>();
			for (Long id : resource.getRoles()) {
				roles.add(permissionService.getRoleById(id, realm));
			}

			if (resource.getId() != null) {
				newResource = networkService.updateResource(
						networkService.getResourceById(resource.getId()),
						resource.getName(), resource.getHostname(), resource.getDestinationHostname(), protocols,
						roles);
			} else {
				newResource = networkService.createResource(resource.getName(),
						resource.getHostname(), resource.getDestinationHostname(), protocols, roles, realm);
			}
			return new ResourceStatus<NetworkResource>(newResource,
					I18N.getResource(sessionUtils.getLocale(request),
							NetworkResourceService.RESOURCE_BUNDLE, resource
									.getId() != null ? "resource.updated.info"
									: "resource.created.info", resource
									.getName()));

		} catch (ResourceChangeException e) {
			return new ResourceStatus<NetworkResource>(false, I18N.getResource(
					sessionUtils.getLocale(request), e.getBundle(),
					e.getResourceKey(), e.getArgs()));
		} catch (ResourceCreationException e) {
			return new ResourceStatus<NetworkResource>(false, I18N.getResource(
					sessionUtils.getLocale(request), e.getBundle(),
					e.getResourceKey(), e.getArgs()));
		} catch (ResourceNotFoundException e) {
			return new ResourceStatus<NetworkResource>(false, I18N.getResource(
					sessionUtils.getLocale(request), e.getBundle(),
					e.getResourceKey(), e.getArgs()));
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "networkResource/{id}", method = RequestMethod.DELETE, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<NetworkResource> deleteResource(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") Long id) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);
		try {

			NetworkResource resource = networkService.getResourceById(id);

			if (resource == null) {
				return new ResourceStatus<NetworkResource>(false,
						I18N.getResource(sessionUtils.getLocale(request),
								NetworkResourceService.RESOURCE_BUNDLE,
								"error.invalidResourceId", id));
			}

			String preDeletedName = resource.getName();
			networkService.deleteResource(resource);

			return new ResourceStatus<NetworkResource>(true, I18N.getResource(
					sessionUtils.getLocale(request),
					NetworkResourceService.RESOURCE_BUNDLE,
					"protocol.deleted.info", preDeletedName));

		} catch (ResourceException e) {
			return new ResourceStatus<NetworkResource>(false, e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "networkProtocols", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<NetworkProtocol> getProtocols(
			HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);
		try {
			return new ResourceList<NetworkProtocol>(
					networkService.getProtocols());
		} finally {
			clearAuthenticatedContext(networkService);
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "networkProtocol/{id}", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public NetworkProtocol getProtocol(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("id") Long id)
			throws AccessDeniedException, UnauthorizedException,
			ResourceNotFoundException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);
		try {
			return networkService.getProtocolById(id);
		} finally {
			clearAuthenticatedContext(networkService);
		}

	}

	@AuthenticationRequired
	@RequestMapping(value = "networkProtocol", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<NetworkProtocol> createOrUpdateNetworProtocol(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody NetworkProtocolUpdate protocol)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);
		try {

			NetworkProtocol newProtocol;

			if (protocol.getId() != null) {
				newProtocol = networkService.updateProtocol(
						networkService.getProtocolById(protocol.getId()),
						protocol.getName(),
						NetworkTransport.valueOf(protocol.getTransport()),
						protocol.getStartPort(), protocol.getEndPort());
			} else {
				newProtocol = networkService.createProtocol(protocol.getName(),
						NetworkTransport.valueOf(protocol.getTransport()),
						protocol.getStartPort(), protocol.getEndPort());
			}
			return new ResourceStatus<NetworkProtocol>(newProtocol,
					I18N.getResource(sessionUtils.getLocale(request),
							NetworkResourceService.RESOURCE_BUNDLE, protocol
									.getId() != null ? "protocol.updated.info"
									: "protocol.created.info", protocol
									.getName()));

		} catch (ResourceChangeException e) {
			return new ResourceStatus<NetworkProtocol>(false, I18N.getResource(
					sessionUtils.getLocale(request), e.getBundle(),
					e.getResourceKey(), e.getArgs()));
		} catch (ResourceCreationException e) {
			return new ResourceStatus<NetworkProtocol>(false, I18N.getResource(
					sessionUtils.getLocale(request), e.getBundle(),
					e.getResourceKey(), e.getArgs()));
		} catch (ResourceNotFoundException e) {
			return new ResourceStatus<NetworkProtocol>(false, I18N.getResource(
					sessionUtils.getLocale(request), e.getBundle(),
					e.getResourceKey(), e.getArgs()));
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "networkProtocol/{id}", method = RequestMethod.DELETE, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<NetworkProtocol> deleteProtocol(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") Long id) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), networkService);
		try {

			NetworkProtocol protocol = networkService.getProtocolById(id);

			if (protocol == null) {
				return new ResourceStatus<NetworkProtocol>(false,
						I18N.getResource(sessionUtils.getLocale(request),
								NetworkResourceService.RESOURCE_BUNDLE,
								"error.invalidProtocolId", id));
			}

			String preDeletedName = protocol.getName();
			networkService.deleteProtocol(protocol);

			return new ResourceStatus<NetworkProtocol>(true, I18N.getResource(
					sessionUtils.getLocale(request),
					NetworkResourceService.RESOURCE_BUNDLE,
					"protocol.deleted.info", preDeletedName));

		} catch (ResourceChangeException e) {
			return new ResourceStatus<NetworkProtocol>(false, e.getMessage());
		} catch (ResourceNotFoundException e) {
			return new ResourceStatus<NetworkProtocol>(false, e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}
}
