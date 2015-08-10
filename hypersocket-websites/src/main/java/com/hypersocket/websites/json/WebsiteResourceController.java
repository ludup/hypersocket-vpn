package com.hypersocket.websites.json;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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
import org.springframework.web.multipart.MultipartFile;

import com.hypersocket.auth.json.AuthenticationRequired;
import com.hypersocket.auth.json.ResourceController;
import com.hypersocket.auth.json.UnauthorizedException;
import com.hypersocket.i18n.I18N;
import com.hypersocket.i18n.I18NServiceImpl;
import com.hypersocket.json.ResourceList;
import com.hypersocket.json.ResourceStatus;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.permissions.Role;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.ResourceColumns;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.resource.ResourceExportException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.session.json.SessionTimeoutException;
import com.hypersocket.tables.Column;
import com.hypersocket.tables.ColumnSort;
import com.hypersocket.tables.BootstrapTableResult;
import com.hypersocket.tables.json.BootstrapTablePageProcessor;
import com.hypersocket.utils.HypersocketUtils;
import com.hypersocket.websites.WebsiteResource;
import com.hypersocket.websites.WebsiteResourceColumns;
import com.hypersocket.websites.WebsiteResourceService;
import com.hypersocket.websites.WebsiteResourceServiceImpl;

@Controller
public class WebsiteResourceController extends ResourceController {

	@Autowired
	WebsiteResourceService websiteService;

	@AuthenticationRequired
	@RequestMapping(value = "websites/myWebsites", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<WebsiteResource> getResourcesByCurrentPrincipal(
			HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {
			return new ResourceList<WebsiteResource>(
					websiteService.getResources(sessionUtils
							.getPrincipal(request)));
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "websites/personal", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public BootstrapTableResult personalWebsites(final HttpServletRequest request,
			HttpServletResponse response) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));

		try {
			return processDataTablesRequest(request,
					new BootstrapTablePageProcessor() {

						@Override
						public Column getColumn(int col) {
							return ResourceColumns.values()[col];
						}

						@Override
						public Collection<?> getPage(String searchPattern,
								int start, int length, ColumnSort[] sorting)
								throws UnauthorizedException,
								AccessDeniedException {
							return websiteService.searchPersonalResources(
									sessionUtils.getPrincipal(request),
									searchPattern, start, length, sorting);
						}

						@Override
						public Long getTotalCount(String searchPattern)
								throws UnauthorizedException,
								AccessDeniedException {
							return websiteService.getPersonalResourceCount(
									sessionUtils.getPrincipal(request),
									searchPattern);
						}
					});
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "websites/table", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public BootstrapTableResult tableNetworkResources(
			final HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));

		try {
			return processDataTablesRequest(request,
					new BootstrapTablePageProcessor() {

						@Override
						public Column getColumn(int col) {
							return WebsiteResourceColumns.values()[col];
						}

						@Override
						public List<?> getPage(String searchPattern, int start,
								int length, ColumnSort[] sorting)
								throws UnauthorizedException,
								AccessDeniedException {
							return websiteService.searchResources(
									sessionUtils.getCurrentRealm(request),
									searchPattern, start, length, sorting);
						}

						@Override
						public Long getTotalCount(String searchPattern)
								throws UnauthorizedException,
								AccessDeniedException {
							return websiteService.getResourceCount(
									sessionUtils.getCurrentRealm(request),
									searchPattern);
						}
					});
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "websites/template", method = RequestMethod.GET, produces = { "application/json" })
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
	@RequestMapping(value = "websites/website/{id}", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public WebsiteResource getResource(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("id") Long id)
			throws AccessDeniedException, UnauthorizedException,
			ResourceNotFoundException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {
			return websiteService.getResourceById(id);
		} finally {
			clearAuthenticatedContext();
		}

	}

	@AuthenticationRequired
	@RequestMapping(value = "websites/website", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<WebsiteResource> createOrUpdate(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody WebsiteResourceUpdate resource)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {

			WebsiteResource newResource;

			Set<Role> roles = new HashSet<Role>();
			for (Long id : resource.getRoles()) {
				roles.add(permissionRepository.getRoleById(id));
			}

			if (resource.getId() != null) {
				newResource = websiteService.updateResource(
						websiteService.getResourceById(resource.getId()),
						resource.getName(), resource.getLaunchUrl(),
						resource.getAdditionalUrls(), roles);
			} else {
				newResource = websiteService.createResource(resource.getName(),
						resource.getLaunchUrl(), resource.getAdditionalUrls(),
						roles);
			}
			return new ResourceStatus<WebsiteResource>(newResource,
					I18N.getResource(sessionUtils.getLocale(request),
							WebsiteResourceServiceImpl.RESOURCE_BUNDLE,
							resource.getId() != null ? "resource.updated.info"
									: "resource.created.info", resource
									.getName()));

		} catch (ResourceException e) {
			return new ResourceStatus<WebsiteResource>(false, e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}

	@SuppressWarnings("unchecked")
	@AuthenticationRequired
	@RequestMapping(value = "websites/website/{id}", method = RequestMethod.DELETE, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<WebsiteResource> deleteResource(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") Long id) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {

			WebsiteResource resource = websiteService.getResourceById(id);

			if (resource == null) {
				return new ResourceStatus<WebsiteResource>(false,
						I18N.getResource(sessionUtils.getLocale(request),
								WebsiteResourceServiceImpl.RESOURCE_BUNDLE,
								"error.invalidResourceId", id));
			}

			String preDeletedName = resource.getName();
			websiteService.deleteResource(resource);

			return new ResourceStatus<WebsiteResource>(true, I18N.getResource(
					sessionUtils.getLocale(request),
					WebsiteResourceServiceImpl.RESOURCE_BUNDLE,
					"resource.deleted.info", preDeletedName));

		} catch (ResourceException e) {
			return new ResourceStatus<WebsiteResource>(false, e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}
	
	@AuthenticationRequired
	@RequestMapping(value = "webistes/export/{id}", method = RequestMethod.GET, produces = { "text/plain" })
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String export(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("id") long id)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException, ResourceNotFoundException,
			ResourceExportException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		try {
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ websiteService.getResourceCategory() + "-"
					+ websiteService.getResourceById(id).getName() + ".json\"");
			return websiteService.exportResoure(id);
		} finally {
			clearAuthenticatedContext();
		}
	}
	
	@AuthenticationRequired
	@RequestMapping(value = "websites/export", method = RequestMethod.GET, produces = { "text/plain" })
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String exportAll(HttpServletRequest request,
			HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException, ResourceNotFoundException,
			ResourceExportException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		try {
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ websiteService.getResourceCategory() + "-all" + ".json\"");
			return websiteService.exportResources(websiteService.allResources());
		} finally {
			clearAuthenticatedContext();
		}
	}
	
	@AuthenticationRequired
	@RequestMapping(value = "websites/import", method = { RequestMethod.POST }, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<WebsiteResource> importLaunchers(
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "file") MultipartFile jsonFile,
			@RequestParam(required=false) boolean dropExisting)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {
		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}
		try {
			String json = IOUtils.toString(jsonFile.getInputStream());
			if (!HypersocketUtils.isValidJSON(json)) {
				throw new ResourceException(I18NServiceImpl.USER_INTERFACE_BUNDLE,
						"error.incorrectJSON");
			}
			Collection<WebsiteResource> collects = websiteService
					.importResources(json, getCurrentRealm(), dropExisting);
			return new ResourceStatus<WebsiteResource>(
					true,
					I18N.getResource(
							sessionUtils.getLocale(request),
							I18NServiceImpl.USER_INTERFACE_BUNDLE,
							"resource.import.success", collects.size()));
		} catch (ResourceException e) {
			return new ResourceStatus<WebsiteResource>(false, e.getMessage());
		} catch (Exception e) {
			return new ResourceStatus<WebsiteResource>(
					false,
					I18N.getResource(
							sessionUtils.getLocale(request),
							I18NServiceImpl.USER_INTERFACE_BUNDLE,
							"resource.import.failure",
							e.getMessage()));
		} finally {
			clearAuthenticatedContext();
		}
	}
}
