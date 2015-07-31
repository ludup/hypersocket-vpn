package com.hypersocket.launcher.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
import com.hypersocket.launcher.ApplicationLauncherOS;
import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.launcher.ApplicationLauncherResourceColumns;
import com.hypersocket.launcher.ApplicationLauncherResourceService;
import com.hypersocket.launcher.ApplicationLauncherResourceServiceImpl;
import com.hypersocket.permissions.AccessDeniedException;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.ResourceException;
import com.hypersocket.resource.ResourceExportException;
import com.hypersocket.resource.ResourceNotFoundException;
import com.hypersocket.session.json.SessionTimeoutException;
import com.hypersocket.tables.Column;
import com.hypersocket.tables.ColumnSort;
import com.hypersocket.tables.BootstrapTableResult;
import com.hypersocket.tables.json.BootstrapTablePageProcessor;
import com.hypersocket.utils.HypersocketUtils;

@Controller
public class ApplicationLauncherResourceController extends ResourceController {

	@Autowired
	ApplicationLauncherResourceService resourceService;

	@AuthenticationRequired
	@RequestMapping(value = "launchers/table", method = RequestMethod.GET, produces = { "application/json" })
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
							return ApplicationLauncherResourceColumns.values()[col];
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
	@RequestMapping(value = "launchers/template", method = RequestMethod.GET, produces = { "application/json" })
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
	@RequestMapping(value = "launchers/launcher/{id}", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ApplicationLauncherResource getResource(HttpServletRequest request,
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
	@RequestMapping(value = "launchers/search", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public BootstrapTableResult search(final HttpServletRequest request,
			HttpServletResponse response) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException,
			NumberFormatException, IOException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));

		try {
			String iDisplayStart = request.getParameter("iDisplayStart");
			String iDisplayLength = request.getParameter("iDisplayLength");
			return resourceService.searchTemplates(
					request.getParameter("sSearch"),
					iDisplayStart == null ? 0 :Integer.parseInt(iDisplayStart),
					iDisplayLength == null ? 10 :Integer.parseInt(iDisplayLength));
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "launchers/script", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<ApplicationLauncherResource> createFromScript(
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam String script) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {

			ApplicationLauncherResource newResource = resourceService
					.importResources(script, getCurrentRealm(), false).iterator().next();

			return new ResourceStatus<ApplicationLauncherResource>(
					newResource,
					I18N.getResource(
							sessionUtils.getLocale(request),
							ApplicationLauncherResourceServiceImpl.RESOURCE_BUNDLE,
							"resource.created.info", newResource.getName()));

		} catch (ResourceException e) {
			return new ResourceStatus<ApplicationLauncherResource>(false,
					e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "launchers/image/{uuid}", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseStatus(value = HttpStatus.OK)
	public void downloadTemplateImage(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String uuid)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException, IOException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {

			resourceService.downloadTemplateImage(uuid, request, response);

		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "launchers/launcher", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<ApplicationLauncherResource> createOrUpdateNetworkResource(
			HttpServletRequest request, HttpServletResponse response,
			@RequestBody ApplicationLauncherResourceUpdate resource)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {

			ApplicationLauncherResource newResource;

			Realm realm = sessionUtils.getCurrentRealm(request);

			ApplicationLauncherOS os = ApplicationLauncherOS.values()[resource
					.getOs()];

			if (resource.getId() != null) {
				newResource = resourceService.updateResource(
						resourceService.getResourceById(resource.getId()),
						resource.getName(), resource.getExe(),
						resource.getArgs(), os, resource.getStartupScript(),
						resource.getShutdownScript());
			} else {
				newResource = resourceService.createResource(
						resource.getName(), realm, resource.getExe(),
						resource.getArgs(), os, resource.getStartupScript(),
						resource.getShutdownScript());
			}
			return new ResourceStatus<ApplicationLauncherResource>(
					newResource,
					I18N.getResource(
							sessionUtils.getLocale(request),
							ApplicationLauncherResourceServiceImpl.RESOURCE_BUNDLE,
							resource.getId() != null ? "resource.updated.info"
									: "resource.created.info", resource
									.getName()));

		} catch (ResourceException e) {
			return new ResourceStatus<ApplicationLauncherResource>(false,
					e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}

	@SuppressWarnings("unchecked")
	@AuthenticationRequired
	@RequestMapping(value = "launchers/launcher/{id}", method = RequestMethod.DELETE, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<ApplicationLauncherResource> deleteResource(
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") Long id) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {

			ApplicationLauncherResource resource = resourceService
					.getResourceById(id);

			if (resource == null) {
				return new ResourceStatus<ApplicationLauncherResource>(
						false,
						I18N.getResource(
								sessionUtils.getLocale(request),
								ApplicationLauncherResourceServiceImpl.RESOURCE_BUNDLE,
								"error.invalidResourceId", id));
			}

			String preDeletedName = resource.getName();
			resourceService.deleteResource(resource);

			return new ResourceStatus<ApplicationLauncherResource>(
					true,
					I18N.getResource(
							sessionUtils.getLocale(request),
							ApplicationLauncherResourceServiceImpl.RESOURCE_BUNDLE,
							"resource.deleted.info", preDeletedName));

		} catch (ResourceException e) {
			return new ResourceStatus<ApplicationLauncherResource>(false,
					e.getMessage());
		} finally {
			clearAuthenticatedContext();
		}
	}

	@AuthenticationRequired
	@RequestMapping(value = "launchers/list", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<ApplicationLauncherResource> getProtocols(
			HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException,
			SessionTimeoutException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {
			return new ResourceList<ApplicationLauncherResource>(
					resourceService.allResources());
		} finally {
			clearAuthenticatedContext();
		}
	}

	@RequestMapping(value = "launchers/os", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceList<ApplicationLauncherOS> getResourcesByCurrentPrincipal(
			HttpServletRequest request, HttpServletResponse response)
			throws AccessDeniedException, UnauthorizedException {
		return new ResourceList<ApplicationLauncherOS>(
				Arrays.asList(ApplicationLauncherOS.values()));
	}

	@AuthenticationRequired
	@RequestMapping(value = "launchers/export/{id}", method = RequestMethod.GET, produces = { "text/plain" })
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String exportLauncher(HttpServletRequest request,
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
					+ resourceService.getResourceCategory() + "-"
					+ resourceService.getResourceById(id).getName() + ".json\"");
			return resourceService.exportResoure(id);
		} finally {
			clearAuthenticatedContext();
		}

	}

	@AuthenticationRequired
	@RequestMapping(value = "launchers/export", method = RequestMethod.GET, produces = { "text/plain" })
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public String exportLauncher(HttpServletRequest request,
			HttpServletResponse response) throws AccessDeniedException,
			UnauthorizedException, SessionTimeoutException,
			ResourceNotFoundException, ResourceExportException {

		setupAuthenticatedContext(sessionUtils.getSession(request),
				sessionUtils.getLocale(request));
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		try {
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ resourceService.getResourceCategory() + ".json\"");
			return resourceService.exportAllResoures();
		} finally {
			clearAuthenticatedContext();
		}

	}
	
	@AuthenticationRequired
	@RequestMapping(value = "launchers/import", method = { RequestMethod.POST }, produces = { "application/json" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResourceStatus<ApplicationLauncherResource> importLaunchers(
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
			Collection<ApplicationLauncherResource> collects = resourceService
					.importResources(json, getCurrentRealm(), dropExisting);
			return new ResourceStatus<ApplicationLauncherResource>(
					true,
					I18N.getResource(
							sessionUtils.getLocale(request),
							I18NServiceImpl.USER_INTERFACE_BUNDLE,
							"resource.import.success", collects.size()));
		} catch (ResourceException e) {
			return new ResourceStatus<ApplicationLauncherResource>(false,
					e.getMessage());
		} catch (Exception e) {
			return new ResourceStatus<ApplicationLauncherResource>(
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
