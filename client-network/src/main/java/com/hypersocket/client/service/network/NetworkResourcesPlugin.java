package com.hypersocket.client.service.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hypersocket.Version;
import com.hypersocket.client.NetworkResource;
import com.hypersocket.client.hosts.HostsFileManager;
import com.hypersocket.client.hosts.SocketRedirector;
import com.hypersocket.client.rmi.ApplicationLauncher;
import com.hypersocket.client.rmi.ApplicationLauncherTemplate;
import com.hypersocket.client.rmi.BrowserLauncher;
import com.hypersocket.client.rmi.Resource;
import com.hypersocket.client.rmi.Resource.Type;
import com.hypersocket.client.rmi.ResourceImpl;
import com.hypersocket.client.rmi.ResourceProtocolImpl;
import com.hypersocket.client.service.AbstractServicePlugin;
import com.hypersocket.client.service.ResourceMapper;

public class NetworkResourcesPlugin extends AbstractServicePlugin {

	public class NetworkResourceDetail {
		List<NetworkResource> networkResources = new ArrayList<>();
		NetworkResourceTemplate networkResourceTemplate;
		WebsiteResourceTemplate websiteResourceTemplate;
	}

	static Logger log = LoggerFactory.getLogger(NetworkResourcesPlugin.class);

	Map<String, NetworkResource> localForwards = new HashMap<String, NetworkResource>();
	// Map<String, String> resourceForwards = new HashMap<String, String>();
	// List<ProtocolTemplate> protocolTemplates = new ArrayList<>();
	// Map<Resource, WebsiteResourceTemplate> resourceToWebsiteResourceTemplate
	// = new HashMap<Resource, WebsiteResourceTemplate>();
	// Map<Resource, NetworkResourceTemplate> resourceToNetworkResourceTemplate
	// = new HashMap<Resource, NetworkResourceTemplate>();
	// Map<Resource, List<NetworkResource>> resourceToNetworkResources = new
	// HashMap<Resource, List<NetworkResource>>();

	Map<Resource, NetworkResourceDetail> resourceDetails = new HashMap<Resource, NetworkResourceDetail>();
	Map<Resource, NetworkResourceDetail> startedResourceDetails = new HashMap<Resource, NetworkResourceDetail>();
	Map<Resource, List<Resource>> childResources = new HashMap<>();

	HostsFileManager mgr;
	SocketRedirector redirector;

	public NetworkResourcesPlugin() {
		super("websites", "networkResources");
	}

	protected void reloadResources(List<Resource> realmResources) {
		if (log.isInfoEnabled()) {
			log.info("Loading Network Resources");
		}

		startNetworkResources(realmResources);

		if (log.isInfoEnabled()) {
			log.info("Loading Websites");
		}

		startWebsites(realmResources);
 
	}

	@Override
	public boolean onStart() {

		return true;
	}

	protected void startWebsites(List<Resource> realmResources) {
		try {
			String json = serviceClient.getTransport().get("websites/myWebsites");

			int errors = processWebsiteResources(realmResources, json);

			if (errors > 0) {
				// Warn
				serviceClient.showWarning(errors + " websites could not be opened.");
			}

		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error("Could not start website resources", e);
			}
		}
	}

	protected int processWebsiteResources(final List<Resource> realmResources, String json) throws IOException {

		return processResourceList(json, new ResourceMapper() {

			@Override
			public boolean processResource(JSONObject resource) {

				boolean success = false;

				String name = (String) resource.get("name");
				String launchUrl = (String) resource.get("launchUrl");
				String additionalUrls = (String) resource.get("additionalUrls");
				Long id = (Long) resource.get("id");
				Number modified = (Number) resource.get("modifiedDate");
				String logo = (String) resource.get("logo");

				try {
					// Create a Template (the local state object, NOT sent over
					// RMI)
					WebsiteResourceTemplate template = new WebsiteResourceTemplate(id, name, launchUrl,
							additionalUrls.split("\\]\\|\\["), logo);

					// Create a Resource (the object that will be sent over RMI)
					ResourceImpl res = new ResourceImpl("network-" + id, name);
					res.setType(Type.BROWSER);
					res.setLaunchable(true);
					res.setGroup(res.getName());
					res.setIcon(template.getLogo());
					if (modified != null) {
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(modified.longValue());
						res.setModified(c);
					}
					res.setResourceLauncher(new BrowserLauncher(template.getLaunchUrl()));

					// Map the Resource to the Template (we will need it later
					// to start or stop the tunnels)
					NetworkResourceDetail detail = new NetworkResourceDetail();
					detail.websiteResourceTemplate = template;
					resourceDetails.put(res, detail);

					// Add to the list of resources found
					realmResources.add(res);
					success = true;
				} catch (MalformedURLException murle) {
					log.error("Failed to construct resource.", murle);
				}

				return success;
			}

		}, "websites");
	}

	protected void startNetworkResources(List<Resource> realmResources) {
		try {
			String json = serviceClient.getTransport().get("networkResources/personal");

			int errors = processNetworkResources(realmResources, json);

			if (errors > 0) {
				// Warn
				serviceClient.showWarning(errors + " ports could not be opened.");
			}

		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error("Could not start network resources", e);
			}
		}
	}

	protected int processNetworkResources(final List<Resource> realmResources, String json) throws IOException {

		final Map<String, String> variables = serviceClient.getUserVariables();

		return processResourceList(json, new ResourceMapper() {

			@Override
			public boolean processResource(JSONObject field) {

				boolean success = false;

				String hostname = serviceClient.processReplacements((String) field.get("hostname"), variables);
				String destinationHostname = (String) field.get("destinationHostname");
				
				//String endpointLogo = field.containsKey("logo") ? (String) field.get("logo") : null;

				if (StringUtils.isBlank(destinationHostname)) {
					destinationHostname = hostname;
				} else {
					destinationHostname = serviceClient.processReplacements(destinationHostname, variables);
				}

				String name = (String) field.get("name");
				Long id = (Long) field.get("id");

				log.info(String.format("Processing endpoint %s (%d) to %s from %s", name, id, destinationHostname, hostname));
				
				List<ProtocolTemplate> protocolTemplates = new ArrayList<>();

				JSONArray launchers = (JSONArray) field.get("launchers");

				@SuppressWarnings("unchecked")
				Iterator<JSONObject> it3 = (Iterator<JSONObject>) launchers.iterator();

				// For now, ignore version if on Linux, os.version is not
				// that useful for us
				Version ourVersion = null;
				if (!SystemUtils.IS_OS_LINUX) {
					ourVersion = new Version(System.getProperty("os.version"));
				}

				/*
				 * The last modified date of any actual Resource's we create
				 * needs to be the most recent last modified date of any of the
				 * resources components (protocol, launcher, or the server side
				 * resource itself).
				 */
				Number modifiedDateTime = (Number) field.get("modifiedDate");

				List<ApplicationLauncherTemplate> launcherTemplates = new ArrayList<ApplicationLauncherTemplate>();
				while (it3.hasNext()) {
					JSONObject launcher = it3.next();

					String family = (String) launcher.get("osFamily");
					String version = (String) launcher.get("osVersion");
					Long lid = (Long) launcher.get("id");

					if (System.getProperty("os.name").toLowerCase().startsWith(family.toLowerCase())) {
						Version launcherVersion = new Version(version);
						if (ourVersion == null || ourVersion.compareTo(launcherVersion) >= 0) {
							String n = (String) launcher.get("name");
							String exe = (String) launcher.get("exe");
							String logo = (String) launcher.get("logo");
							String args = (String) launcher.get("args");
							String startupScript = (String) launcher.get("startupScript");
							String shutdownScript = (String) launcher.get("shutdownScript");

							Calendar launcherModifiedDate = Calendar.getInstance();
							Number launcherModifiedDateTime = (Number) launcher.get("modifiedDate");
							launcherModifiedDate.setTimeInMillis(Math.max(modifiedDateTime.longValue(), launcherModifiedDateTime.longValue()));
							launcherTemplates.add(new ApplicationLauncherTemplate(lid, n, exe, startupScript,
									shutdownScript, logo, variables, launcherModifiedDate, args.split("\\]\\|\\[")));
						}
					}
				}
				JSONArray protocols = (JSONArray) field.get("protocols");

				@SuppressWarnings("unchecked")
				Iterator<JSONObject> it2 = (Iterator<JSONObject>) protocols.iterator();

				while (it2.hasNext()) {
					JSONObject protocol = it2.next();
					Long pid = (Long) protocol.get("id");
					String protocolName = (String) protocol.get("name");
					String transport = (String) protocol.get("transport");
					long tmp = (Long) protocol.get("startPort");
					int startPort = (int) tmp;
					int endPort = startPort;
					if (protocol.get("endPort") != null) {
						tmp = (Long) protocol.get("endPort");
						endPort = (int) tmp;
					}
					Calendar protocolModifiedDate = Calendar.getInstance();
					Number protocolModifiedDateTime = (Number) protocol.get("modifiedDate");
					protocolModifiedDate.setTimeInMillis(Math.max(modifiedDateTime.longValue(), protocolModifiedDateTime.longValue()));
					protocolTemplates.add(new ProtocolTemplate(pid, protocolName, transport, startPort, endPort, protocolModifiedDate));
				}

				List<Resource> protocolResources = new ArrayList<>();
				for (ProtocolTemplate protocol : protocolTemplates) {
					NetworkResourceDetail detail = new NetworkResourceDetail();
					/*
					 * Create a Template (the local state object, NOT sent over
					 * RMI)
					 */
					NetworkResourceTemplate template = new NetworkResourceTemplate(id, name, hostname,
							destinationHostname, protocol.getName(), protocol.getTransport(), protocol.getStartPort(),
							protocol.getEndPort());

					ResourceImpl res = new ResourceImpl(String.valueOf(id) + "-protocol-" + protocol.getId(), name);
					res.setType(Type.ENDPOINT);
					res.setLaunchable(false);
					res.setModified(protocol.getModifiedDate());
					realmResources.add(res);
					detail.networkResourceTemplate = template;
					resourceDetails.put(res, detail);
					protocolResources.add(res);
				}

				for (ApplicationLauncherTemplate launcherTemplate : launcherTemplates) {

					// Create a Resource (the object that will be sent over
					// RMI)
					ResourceImpl res = new ResourceImpl(id + "-template-" + +launcherTemplate.getId(), name);
					res.setType(Type.NETWORK);
					res.setLaunchable(true);
					res.setModified(launcherTemplate.getModifiedDate());
					res.setIcon(launcherTemplate.getLogo());

					// TODO - this is disabled for now, until the grouping
					// component is completed.
					// res.setGroup(launcherTemplate.getName());
					// res.setGroupIcon(launcherTemplate.getLogo());
					res.setGroup(res.getName());

					res.setResourceLauncher(new ApplicationLauncher(serviceClient.getPrincipalName(),
							destinationHostname, launcherTemplate));

					// Add to the list of resources found
					realmResources.add(res);
					
					/* This specifies that when THIS updates, we actually ALL of the protocol resource as well */ 
					childResources.put(res, protocolResources);

					// The resource detail is shared across the launchable
					// resources
//					resourceDetails.put(res, detail);
				}
				success = true;

				return success;
			}

		}, "network resources");
	}

	@Override
	public void onStop() {

		if (log.isInfoEnabled()) {
			log.info("Stopping Network Resources plugin");
		}

		try {
			resourceService.removeResourceRealm(serviceClient.getHost());
		} catch (RemoteException e) {
			log.error("Failed to remove resource realm " + serviceClient.getHost(), e);
		}

		vpnService.stopAllForwarding(serviceClient);
		
		startedResourceDetails.clear();
		resourceDetails.clear();
		childResources.clear();

	}

	@Override
	public String getName() {
		return "Network Resources";
	}

	@Override
	protected boolean onCreatedResource(Resource resource) {
		NetworkResourceDetail detail = resourceDetails.get(resource);
		if (detail != null && detail.websiteResourceTemplate != null) {
			return startWebsiteResource(resource, detail);
		}
		if (detail != null && detail.networkResourceTemplate != null) {
			return startNetworkResource(resource, detail);
		}
		return true;
	}

	@Override
	protected boolean onUpdatedResource(Resource resource) {
		List<Resource> children = childResources.get(resource);
		if(children != null) {
			for(Resource c : children) {
				onUpdatedResource(c);
			}
			return true;
		}
		
		NetworkResourceDetail detail = resourceDetails.get(resource);
		stopAllNetworkResourcesForResource(resource);
		if (detail != null && detail.websiteResourceTemplate != null) {
			return startWebsiteResource(resource, detail);
		} else if (detail != null && detail.networkResourceTemplate != null) {
			return startNetworkResource(resource, detail);
		} 
		return true;
	}

	@Override
	protected boolean onDeletedResource(Resource resource) {
		stopAllNetworkResourcesForResource(resource);
		return true;
	}

	private boolean startNetworkResource(Resource resource, NetworkResourceDetail detail) {
		boolean success = false;
		NetworkResourceTemplate templ = detail.networkResourceTemplate;
		if (templ.getTransport().equals("TCP")) {

			for (long port = templ.getStartPort(); port <= templ.getEndPort(); port++) {

				try {
					NetworkResource netResource = new NetworkResource(templ.getParentResourceId(), templ.getHostname(),
							templ.getDestinationHostname(), (int) port, "tunnel");

					if (log.isInfoEnabled()) {
						log.info(String.format("Starting forward for resource %s:%s", templ.getDestinationHostname(),
								port));
					}

					boolean started = vpnService.startLocalForwarding(netResource, serviceClient);
					if (started) {
						detail.networkResources.add(netResource);
						ResourceProtocolImpl proto = new ResourceProtocolImpl(templ.getProtocol());
						resource.getProtocols().add(proto);
						success = true;
						templ.addLiveResource(netResource);
					} else {
						break;
					}

				} catch (Exception e) {
					if (log.isErrorEnabled()) {
						log.error("Failed to start local forwarding", e);
					}
				}
			}
			if(success)
				startedResourceDetails.put(resource, detail);
		}
		if (!success) {
			// Stop any that did manage to start
			for (NetworkResource nr : detail.networkResources) {
				vpnService.stopLocalForwarding(nr, serviceClient);
			}
			detail.networkResources.clear();
			startedResourceDetails.remove(resource);
		}

		return success;
	}

	private boolean startWebsiteResource(Resource resource, NetworkResourceDetail detail) {
		boolean success = false;
		final NetworkResource netResource = vpnService.createURLForwarding(serviceClient,
				detail.websiteResourceTemplate.getLaunchUrl(), detail.websiteResourceTemplate.getId());
		if (netResource != null) {
			success = true;
			detail.networkResources.add(netResource);
			for (String url : detail.websiteResourceTemplate.getAdditionalUrls()) {
				final NetworkResource additionalNetResource = vpnService.createURLForwarding(serviceClient, url,
						detail.websiteResourceTemplate.getId());
				if (additionalNetResource == null) {
					success = false;
					break;
				} else {
					detail.networkResources.add(additionalNetResource);
				}
			}
		}

		if (!success) {
			// Stop any that did manage to start
			for (NetworkResource nr : detail.networkResources) {
				vpnService.stopLocalForwarding(nr, serviceClient);
			}
			detail.networkResources.clear();
		}
		else {
			startedResourceDetails.put(resource, detail);
		}
		return success;
	}

	private void stopAllNetworkResourcesForResource(Resource resource) {
		NetworkResourceDetail detail = startedResourceDetails.get(resource);
		if (detail == null || detail.networkResources.isEmpty()) {
			log.warn(String.format("Could not find any started network resource for the resource %s",
					resource.getName()));
		} else {
			for (NetworkResource r : detail.networkResources) {
				vpnService.stopLocalForwarding(r, serviceClient);
			}
			startedResourceDetails.remove(resource);
		}
	}

}
