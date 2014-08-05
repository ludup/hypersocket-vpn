package com.hypersocket.applications.json;

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

import com.hypersocket.applications.ApplicationResource;
import com.hypersocket.applications.ApplicationResourceColumns;
import com.hypersocket.applications.ApplicationResourceService;
import com.hypersocket.applications.ApplicationResourceServiceImpl;
import com.hypersocket.auth.json.AuthenticationRequired;
import com.hypersocket.auth.json.ResourceController;
import com.hypersocket.auth.json.UnauthorizedException;
import com.hypersocket.i18n.I18N;
import com.hypersocket.json.ResourceList;
import com.hypersocket.json.ResourceStatus;
import com.hypersocket.permissions.AccessDeniedException;
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
public class ApplicationResourceController extends ResourceController {

	/**
	 * TODO rename this class to match your entity.
	 * 
	 * rename RequestMapping annotions for your desired resource URLs. e.g
	 * replace applications with "applications"
	 */
	@Autowired
	ApplicationResourceService resourceService;

	@AuthenticationRequired
	@RequestMapping(value = "applications/myApplications", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<ApplicationResource> getResourcesByCurrentPrincipal(
			HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {
			return new ResourceList<ApplicationResource>(
					resourceService.getResources(sessionUtils
							.getPrincipal(request)));
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "applications/table", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public DataTablesResult tableNetworkResources(
			final HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));

		try {
			return processDataTablesRequest(request,
					new DataTablesPageProcessor() {

						@Override
						public Column getColumn(int col) {
							return ApplicationResourceColumns.values()[col];
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
	@RequestMapping(value = "applications/template", method = RequestMethod.GET, produces = { "application/json" })
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
	@RequestMapping(value = "applications/application/{id}", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ApplicationResource getResource(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("id") Long id)
			throws AccessDeniedException, UnauthorizedException,
			ResourceNotFoundException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {
			return resourceService.getResourceById(id);
		} finally {
			clearAuthenticatedContext();
		}

	}

	@AuthenticationRequired
	@RequestMapping(value = "applications/application", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<ApplicationResource> createOrUpdateNetworkResource(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody ApplicationResourceUpdate resource)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {

			ApplicationResource newResource;

			Realm realm = sessionUtils.getCurrentRealm(request);

			Set<Role> roles = new HashSet<Role>();
			for (Long id : resource.getRoles()) {
				roles.add(permissionService.getRoleById(id, realm));
			}

			if (resource.getId() != null) {
				newResource = resourceService.updateResource(
						resourceService.getResourceById(resource.getId()),
						resource.getName(), roles);
			} else {
				newResource = resourceService.createResource(
						resource.getName(), roles, realm);
			}
			return new ResourceStatus<ApplicationResource>(newResource,
					I18N.getResource(sessionUtils.getLocale(request),
							ApplicationResourceServiceImpl.RESOURCE_BUNDLE,
							resource.getId() != null ? "resource.updated.info"
									: "resource.created.info", resource
									.getName()));

		} catch (ResourceChangeException e) {
			return new ResourceStatus<ApplicationResource>(false,
					I18N.getResource(sessionUtils.getLocale(request),
							e.getBundle(), e.getResourceKey(), e.getArgs()));
		} catch (ResourceCreationException e) {
			return new ResourceStatus<ApplicationResource>(false,
					I18N.getResource(sessionUtils.getLocale(request),
							e.getBundle(), e.getResourceKey(), e.getArgs()));
		} catch (ResourceNotFoundException e) {
			return new ResourceStatus<ApplicationResource>(false,
					I18N.getResource(sessionUtils.getLocale(request),
							e.getBundle(), e.getResourceKey(), e.getArgs()));
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "applications/application/{id}", method = RequestMethod.DELETE, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<ApplicationResource> deleteResource(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") Long id) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {

			ApplicationResource resource = resourceService.getResourceById(id);

			if (resource == null) {
				return new ResourceStatus<ApplicationResource>(false,
						I18N.getResource(sessionUtils.getLocale(request),
								ApplicationResourceServiceImpl.RESOURCE_BUNDLE,
								"error.invalidResourceId", id));
			}

			String preDeletedName = resource.getName();
			resourceService.deleteResource(resource);

			return new ResourceStatus<ApplicationResource>(true,
					I18N.getResource(sessionUtils.getLocale(request),
							ApplicationResourceServiceImpl.RESOURCE_BUNDLE,
							"resource.deleted.info", preDeletedName));

		} catch (ResourceException e) {
			return new ResourceStatus<ApplicationResource>(false,
					e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}
}
