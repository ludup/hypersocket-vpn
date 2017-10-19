package com.hypersocket.htmlapplications.json;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hypersocket.auth.json.AuthenticationRequired;
import com.hypersocket.auth.json.ResourceController;
import com.hypersocket.auth.json.UnauthorizedException;
import com.hypersocket.htmlapplications.HTMLApplicationResource;
import com.hypersocket.htmlapplications.HTMLApplicationResourceColumns;
import com.hypersocket.htmlapplications.HTMLApplicationResourceService;
import com.hypersocket.htmlapplications.HTMLApplicationResourceServiceImpl;
import com.hypersocket.i18n.I18N;
import com.hypersocket.json.PropertyItem;
import com.hypersocket.json.ResourceList;
import com.hypersocket.json.ResourceStatus;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.Role;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.AssignableResourceUpdate;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.session.json.SessionTimeoutException;
import com.hypersocket.tables.BootstrapTableResult;
import com.hypersocket.tables.Column;
import com.hypersocket.tables.ColumnSort;
import com.hypersocket.tables.json.BootstrapTablePageProcessor;

@Controller
public class HTMLApplicationResourceController extends ResourceController {

	@Autowired
	HTMLApplicationResourceService resourceService;

	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/list", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<HTMLApplicationResource> getResources(HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));
		try {
			return new ResourceList<HTMLApplicationResource>(
					resourceService.getResources(sessionUtils.getCurrentRealm(request)));
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/myHTMLApplications", method = RequestMethod.GET, produces = {
			"application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<HTMLApplicationResource> getResourcesByCurrentPrincipal(HttpServletRequest request,
			HttpServletResponse response) throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));
		try {
			return new ResourceList<HTMLApplicationResource>(
					resourceService.getResources(sessionUtils.getPrincipal(request)));
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/table", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public BootstrapTableResult<?> tableResources(final HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));

		try {
			return processDataTablesRequest(request, new BootstrapTablePageProcessor() {

				@Override
				public Column getColumn(String col) {
					return HTMLApplicationResourceColumns.valueOf(col.toUpperCase());
				}

				@Override
				public List<?> getPage(String searchColumn, String searchPattern, int start, int length,
						ColumnSort[] sorting) throws UnauthorizedException, AccessDeniedException {
					return resourceService.searchResources(sessionUtils.getCurrentRealm(request), searchColumn,
							searchPattern, start, length, sorting);
				}

				@Override
				public Long getTotalCount(String searchColumn, String searchPattern)
						throws UnauthorizedException, AccessDeniedException {
					return resourceService.getResourceCount(sessionUtils.getCurrentRealm(request), searchColumn,
							searchPattern);
				}
			});
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/template", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<PropertyCategory> getResourceTemplate(HttpServletRequest request)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {
		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));

		try {
			return new ResourceList<PropertyCategory>(resourceService.getPropertyTemplate());
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/properties/{id}", method = RequestMethod.GET, produces = {
			"application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<PropertyCategory> getActionTemplate(HttpServletRequest request, @PathVariable Long id)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException, ResourceNotFoundException {
		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));
		try {
			HTMLApplicationResource resource = resourceService.getResourceById(id);
			return new ResourceList<PropertyCategory>(resourceService.getPropertyTemplate(resource));
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/hTMLApplication/{id}", method = RequestMethod.GET, produces = {
			"application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public HTMLApplicationResource getResource(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") Long id)
			throws AccessDeniedException, UnauthorizedException, ResourceNotFoundException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));
		try {
			return resourceService.getResourceById(id);
		} finally {
			clearAuthenticatedContext();
		}

	}

	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/hTMLApplication", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<HTMLApplicationResource> createOrUpdateResource(HttpServletRequest request,
			HttpServletResponse response, @RequestBody AssignableResourceUpdate resource)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));
		try {

			HTMLApplicationResource newResource;

			Realm realm = sessionUtils.getCurrentRealm(request);

			Set<Role> roles = new HashSet<Role>();
			for (Long id : resource.getRoles()) {
				roles.add(permissionRepository.getRoleById(id));
			}

			Map<String, String> properties = new HashMap<String, String>();
			for (PropertyItem i : resource.getProperties()) {
				properties.put(i.getId(), i.getValue());
			}

			if (resource.getId() != null) {
				newResource = resourceService.updateResource(resourceService.getResourceById(resource.getId()),
						resource.getName(), roles, properties);
			} else {
				newResource = resourceService.createResource(resource.getName(), roles, realm, properties);
			}
			return new ResourceStatus<HTMLApplicationResource>(newResource, I18N.getResource(
					sessionUtils.getLocale(request), HTMLApplicationResourceServiceImpl.RESOURCE_BUNDLE,
					resource.getId() != null ? "resource.updated.info" : "resource.created.info", resource.getName()));

		} catch (ResourceException e) {
			return new ResourceStatus<HTMLApplicationResource>(false, e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}

	@SuppressWarnings("unchecked")
	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/hTMLApplication/{id}", method = RequestMethod.DELETE, produces = {
			"application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<HTMLApplicationResource> deleteResource(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("id") Long id)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));
		try {

			HTMLApplicationResource resource = resourceService.getResourceById(id);

			if (resource == null) {
				return new ResourceStatus<HTMLApplicationResource>(false,
						I18N.getResource(sessionUtils.getLocale(request),
								HTMLApplicationResourceServiceImpl.RESOURCE_BUNDLE, "error.invalidResourceId", id));
			}

			String preDeletedName = resource.getName();
			resourceService.deleteResource(resource);

			return new ResourceStatus<HTMLApplicationResource>(true, I18N.getResource(sessionUtils.getLocale(request),
					HTMLApplicationResourceServiceImpl.RESOURCE_BUNDLE, "resource.deleted.info", preDeletedName));

		} catch (ResourceException e) {
			return new ResourceStatus<HTMLApplicationResource>(false, e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/personal", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public BootstrapTableResult<?> personalResources(final HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));

		try {
			return processDataTablesRequest(request, new BootstrapTablePageProcessor() {

				@Override
				public Column getColumn(String col) {
					return HTMLApplicationResourceColumns.valueOf(col.toUpperCase());
				}

				@Override
				public Collection<?> getPage(String searchColumn, String searchPattern, int start, int length,
						ColumnSort[] sorting) throws UnauthorizedException, AccessDeniedException {
					return resourceService.searchPersonalResources(sessionUtils.getPrincipal(request), searchColumn,
							searchPattern, start, length, sorting);
				}

				@Override
				public Long getTotalCount(String searchColumn, String searchPattern)
						throws UnauthorizedException, AccessDeniedException {
					return resourceService.getPersonalResourceCount(sessionUtils.getPrincipal(request), searchColumn,
							searchPattern);
				}
			});
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/search", method = { RequestMethod.GET, RequestMethod.POST }, produces = {
			"application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public BootstrapTableResult<?> search(final HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException, NumberFormatException,
			IOException {

		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));

		try {
			String iDisplayStart = request.getParameter("iDisplayStart");
			String iDisplayLength = request.getParameter("iDisplayLength");
			return resourceService.searchTemplates("builtInHTML", request.getParameter("sSearch"),
					iDisplayStart == null ? 0 : Integer.parseInt(iDisplayStart),
					iDisplayLength == null ? 10 : Integer.parseInt(iDisplayLength));
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "hTMLApplications/script", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<HTMLApplicationResource> createFromScript(HttpServletRequest request,
			HttpServletResponse response, @RequestParam String script)
			throws AccessDeniedException, UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request), sessionUtils.getLocale(request));
		try {

			HTMLApplicationResource newResource = resourceService
					.importResources("[" + script + "]", getCurrentRealm(), false).iterator().next();

			return new ResourceStatus<HTMLApplicationResource>(newResource,
					I18N.getResource(sessionUtils.getLocale(request),
							HTMLApplicationResourceServiceImpl.RESOURCE_BUNDLE, "resource.created.info",
							newResource.getName()));

		} catch (ResourceException e) {
			return new ResourceStatus<HTMLApplicationResource>(false, e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}
}
