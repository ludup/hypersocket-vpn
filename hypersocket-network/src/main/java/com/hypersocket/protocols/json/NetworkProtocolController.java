package com.hypersocket.protocols.json;

import java.util.List;

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
import com.hypersocket.network.NetworkResource;
import com.hypersocket.network.NetworkTransport;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.protocols.NetworkProtocolColumns;
import com.hypersocket.protocols.NetworkProtocolService;
import com.hypersocket.protocols.NetworkProtocolServiceImpl;
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
public class NetworkProtocolController extends ResourceController {

	@Autowired
	NetworkProtocolService resourceService;

	@AuthenticationRequired
	@RequestMapping(value = "networkProtocols/table", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public DataTablesResult tableNetworkResources(
			final HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), resourceService);

		try {
			return processDataTablesRequest(request,
					new DataTablesPageProcessor() {

						@Override
						public Column getColumn(int col) {
							return NetworkProtocolColumns.values()[col];
						}

						@Override
						public List<?> getPage(String searchPattern, int start,
								int length, ColumnSort[] sorting)
								throws UnauthorizedException,
								AccessDeniedException {
							return resourceService.searchResources(
									sessionUtils.getCurrentRealm(request),
									searchPattern, start, length, sorting);
						}

						@Override
						public Long getTotalCount(String searchPattern)
								throws UnauthorizedException,
								AccessDeniedException {
							return resourceService.getResourceCount(
									sessionUtils.getCurrentRealm(request),
									searchPattern);
						}
					});
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "networkProtocols/template", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<PropertyCategory> getResourceTemplate(
			HttpServletRequest request) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {
		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));

		try {
			return new ResourceList<PropertyCategory>();
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "networkProtocols/networkProtocol/{id}", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public NetworkProtocol getResource(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("id") Long id)
			throws AccessDeniedException, UnauthorizedException,
			ResourceNotFoundException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), resourceService);
		try {
			return resourceService.getResourceById(id);
		} finally {
			clearAuthenticatedContext(resourceService);
		}

	}

	@AuthenticationRequired
	@RequestMapping(value = "networkProtocols/networkProtocol", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<NetworkProtocol> createOrUpdateNetworkResource(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody NetworkProtocolUpdate resource)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), resourceService);
		try {

			NetworkProtocol newResource;

			Realm realm = sessionUtils.getCurrentRealm(request);

			if (resource.getId() != null) {
				newResource = resourceService.updateResource(
						resourceService.getResourceById(resource.getId()),
						resource.getName(), resource.getStartPort(),
						resource.getEndPort(),
						NetworkTransport.valueOf(resource.getTransport()));
			} else {
				newResource = resourceService.createResource(
						resource.getName(), realm, resource.getStartPort(),
						resource.getEndPort(),
						NetworkTransport.valueOf(resource.getTransport()));
			}
			return new ResourceStatus<NetworkProtocol>(newResource,
					I18N.getResource(sessionUtils.getLocale(request),
							NetworkProtocolServiceImpl.RESOURCE_BUNDLE,
							resource.getId() != null ? "resource.updated.info"
									: "resource.created.info", resource
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
	@RequestMapping(value = "networkProtocols/networkProtocol/{id}", method = RequestMethod.DELETE, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<NetworkProtocol> deleteResource(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") Long id) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), resourceService);
		try {

			NetworkProtocol resource = resourceService.getResourceById(id);

			if (resource == null) {
				return new ResourceStatus<NetworkProtocol>(false,
						I18N.getResource(sessionUtils.getLocale(request),
								NetworkProtocolServiceImpl.RESOURCE_BUNDLE,
								"error.invalidResourceId", id));
			}

			String preDeletedName = resource.getName();
			resourceService.deleteResource(resource);

			return new ResourceStatus<NetworkProtocol>(true, I18N.getResource(
					sessionUtils.getLocale(request),
					NetworkProtocolServiceImpl.RESOURCE_BUNDLE,
					"resource.deleted.info", preDeletedName));

		} catch (ResourceException e) {
			return new ResourceStatus<NetworkProtocol>(false, e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "networkProtocols/list", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<NetworkProtocol> getProtocols(
			HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request), resourceService);
		try {
			return new ResourceList<NetworkProtocol>(
					resourceService.getResources());
		} finally {
			clearAuthenticatedContext(resourceService);
		}
	}
}
