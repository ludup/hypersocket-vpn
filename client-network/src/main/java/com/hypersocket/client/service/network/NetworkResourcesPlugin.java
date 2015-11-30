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

	static Logger log = LoggerFactory.getLogger(NetworkResourcesPlugin.class);

	Map<String, NetworkResource> localForwards = new HashMap<String, NetworkResource>();
	// Map<String, String> resourceForwards = new HashMap<String, String>();
	Map<Resource, WebsiteResourceTemplate> resourceToWebsiteResourceTemplate = new HashMap<Resource, WebsiteResourceTemplate>();
	Map<Resource, NetworkResourceTemplate> resourceToNetworkResourceTemplate = new HashMap<Resource, NetworkResourceTemplate>();
	Map<Resource, List<NetworkResource>> resourceToNetworkResources = new HashMap<Resource, List<NetworkResource>>();

	HostsFileManager mgr;
	SocketRedirector redirector;

	public NetworkResourcesPlugin() {
		super("websites");
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
			String json = serviceClient.getTransport().get(
					"websites/myWebsites");

			int errors = processWebsiteResources(realmResources, json);

			if (errors > 0) {
				// Warn
				serviceClient.showWarning(errors
						+ " websites could not be opened.");
			}

		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error("Could not start website resources", e);
			}
		}
	}

	protected int processWebsiteResources(final List<Resource> realmResources,
			String json) throws IOException {

		return processResourceList(json, new ResourceMapper() {

			@Override
			public boolean processResource(JSONObject resource) {

				boolean success = false;

				String name = (String) resource.get("name");
				String launchUrl = (String) resource.get("launchUrl");
				String additionalUrls = (String) resource.get("additionalUrls");
				Long id = (Long) resource.get("id");
				Number modified = (Number) resource.get("modifiedDate");

				try {
					// Create a Template (the local state object, NOT sent over
					// RMI)
					WebsiteResourceTemplate template = new WebsiteResourceTemplate(
							id, name, launchUrl, additionalUrls
									.split("\\]\\|\\["));

					// Create a Resource (the object that will be sent over RMI)
					ResourceImpl res = new ResourceImpl("network-" + id, name);
					res.setType(Type.BROWSER);
					res.setLaunchable(true);
					if (modified != null) {
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(modified.longValue());
						res.setModified(c);
					}
					res.setResourceLauncher(new BrowserLauncher(template
							.getLaunchUrl()));

					// Map the Resource to the Template (we will need it later
					// to start or stop the tunnels)
					resourceToWebsiteResourceTemplate.put(res, template);

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
			String json = serviceClient.getTransport().get(
					"networkResources/personal");

			int errors = processNetworkResources(realmResources, json);

			if (errors > 0) {
				// Warn
				serviceClient.showWarning(errors
						+ " ports could not be opened.");
			}

		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error("Could not start network resources", e);
			}
		}
	}

	protected int processNetworkResources(final List<Resource> realmResources,
			String json) throws IOException {

		final Map<String, String> variables = serviceClient.getUserVariables();

		return processResourceList(json, new ResourceMapper() {

			@Override
			public boolean processResource(JSONObject field) {

				boolean success = false;

				String hostname = serviceClient.processReplacements(
						(String) field.get("hostname"), variables);
				String destinationHostname = (String) field
						.get("destinationHostname");

				if (StringUtils.isBlank(destinationHostname)) {
					destinationHostname = hostname;
				} else {
					destinationHostname = serviceClient.processReplacements(
							destinationHostname, variables);
				}

				String name = (String) field.get("name");
				Long id = (Long) field.get("id");

				JSONArray launchers = (JSONArray) field.get("launchers");

				@SuppressWarnings("unchecked")
				Iterator<JSONObject> it3 = (Iterator<JSONObject>) launchers
						.iterator();

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
				Calendar modifiedDate = Calendar.getInstance();
				Number modifiedDateTime = (Number) field.get("modifiedDate");
				if (modifiedDateTime != null)
					modifiedDate.setTimeInMillis(modifiedDateTime.longValue());

				List<ApplicationLauncherTemplate> launcherTemplates = new ArrayList<ApplicationLauncherTemplate>();
				while (it3.hasNext()) {
					JSONObject launcher = it3.next();

					String family = (String) launcher.get("osFamily");
					String version = (String) launcher.get("osVersion");
					Long lid = (Long) launcher.get("id");

					if (System.getProperty("os.name").toLowerCase()
							.startsWith(family.toLowerCase())) {
						Version launcherVersion = new Version(version);
						if (ourVersion == null
								|| ourVersion.compareTo(launcherVersion) >= 0) {
							String n = (String) launcher.get("name");
							String exe = (String) launcher.get("exe");
							String logo = (String) launcher.get("logo");
							String args = (String) launcher.get("args");
							String startupScript = (String) launcher
									.get("startupScript");
							String shutdownScript = (String) launcher
									.get("shutdownScript");

							modifiedDateTime = (Number) field
									.get("modifiedDate");
							if (modifiedDateTime != null
									&& modifiedDateTime.longValue() > modifiedDate
											.getTimeInMillis()) {
								modifiedDate.setTimeInMillis(modifiedDateTime
										.longValue());
							}

							launcherTemplates
									.add(new ApplicationLauncherTemplate(lid,
											n, exe, startupScript,
											shutdownScript, logo, variables,
											args.split("\\]\\|\\[")));
						}
					}
				}
				JSONArray protocols = (JSONArray) field.get("protocols");

				@SuppressWarnings("unchecked")
				Iterator<JSONObject> it2 = (Iterator<JSONObject>) protocols
						.iterator();
				List<ProtocolTemplate> protocolTemplates = new ArrayList<>();

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
					modifiedDateTime = (Number) field.get("modifiedDate");
					if (modifiedDateTime != null
							&& modifiedDateTime.longValue() > modifiedDate
									.getTimeInMillis()) {
						modifiedDate.setTimeInMillis(modifiedDateTime
								.longValue());
					}
					protocolTemplates.add(new ProtocolTemplate(pid,
							protocolName, transport, startPort, endPort));
				}

				//
				// Create a Resource for each protocol and application. Each
				// resource under
				// the same application will use an identical launcher
				//
				// TODO This is a bit odd, and is only done like to be able to
				// use the PROTOCOL
				// icon for the launcher. When the server is capable of defining
				// icons for
				// each resource we will use those and this code will be a bit
				// more sensible
				//

				for (ProtocolTemplate protocol : protocolTemplates) {
					// Create a Template (the local state object, NOT sent over
					// RMI)
					NetworkResourceTemplate template = new NetworkResourceTemplate(
							id, name, hostname, destinationHostname, protocol
									.getName(), protocol.getTransport(),
							protocol.getStartPort(), protocol.getEndPort());

					for (ApplicationLauncherTemplate launcherTemplate : launcherTemplates) {

						// Create a Resource (the object that will be sent over
						// RMI)
						ResourceImpl res = new ResourceImpl(id + "-"
								+ protocol.getId() + "-"
								+ launcherTemplate.getId(), name + " - "
								+ launcherTemplate.getName());
						res.setType(Type.NETWORK);
						res.setLaunchable(true);
						res.setModified(modifiedDate);
						res.setIcon(launcherTemplate.getLogo());
						// res.setIcon("res://types/proto-" +
						// protocol.getName().toLowerCase() + ".png");
						res.setResourceLauncher(new ApplicationLauncher(
								serviceClient.getPrincipalName(), template
										.getHostname(), launcherTemplate));

						// Map the Resource to the Template (we will need it
						// later
						// to start or stop the tunnels)
						resourceToNetworkResourceTemplate.put(res, template);

						// Add to the list of resources found
						realmResources.add(res);
						success = true;
					}
				}

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
			log.error(
					"Failed to remove resource realm "
							+ serviceClient.getHost(), e);
		}

		vpnService.stopAllForwarding(serviceClient);

		resourceToNetworkResources.clear();
		resourceToWebsiteResourceTemplate.clear();
		resourceToNetworkResourceTemplate.clear();

	}

	@Override
	public String getName() {
		return "Network Resources";
	}

	@Override
	protected boolean onCreatedResource(Resource resource) {
		WebsiteResourceTemplate wrt = resourceToWebsiteResourceTemplate
				.get(resource);
		if (wrt != null) {
			return startWebsiteResource(resource, wrt);
		}
		NetworkResourceTemplate nrt = resourceToNetworkResourceTemplate
				.get(resource);
		if (nrt != null) {
			return startNetworkResource(resource, nrt);
		}
		return true;
	}

	@Override
	protected boolean onUpdatedResource(Resource resource) {
		WebsiteResourceTemplate wrt = resourceToWebsiteResourceTemplate
				.get(resource);
		NetworkResourceTemplate nrt = resourceToNetworkResourceTemplate
				.get(resource);
		stopAllNetworkResourcesForResource(resource);
		if (wrt != null) {
			return startWebsiteResource(resource, wrt);
		} else if (wrt != null) {
			return startNetworkResource(resource, nrt);
		}
		return true;
	}

	@Override
	protected boolean onDeletedResource(Resource resource) {
		stopAllNetworkResourcesForResource(resource);
		return true;
	}

	private boolean startNetworkResource(Resource resource,
			NetworkResourceTemplate wrt) {
		boolean success = false;
		List<NetworkResource> netResources = new ArrayList<NetworkResource>();
		if (wrt.getTransport().equals("TCP")) {

			for (long port = wrt.getStartPort(); port <= wrt.getEndPort(); port++) {

				try {
					NetworkResource netResource = new NetworkResource(
							wrt.getParentResourceId(), wrt.getHostname(),
							wrt.getDestinationHostname(), (int) port, "tunnel");

					if (log.isInfoEnabled()) {
						log.info(String.format(
								"Starting forward for resource %s:%s",
								wrt.getDestinationHostname(), port));
					}

					boolean started = vpnService.startLocalForwarding(
							netResource, serviceClient);
					if (started) {
						netResources.add(netResource);
						ResourceProtocolImpl proto = new ResourceProtocolImpl(
								wrt.getProtocol());
						resource.getProtocols().add(proto);
						success = true;
						wrt.addLiveResource(netResource);
					} else {
						break;
					}

				} catch (Exception e) {
					if (log.isErrorEnabled()) {
						log.error("Failed to start local forwarding", e);
					}
				}
			}
		}
		if (success) {
			if (resourceToNetworkResources.containsKey(resource)) {
				throw new IllegalStateException("Resource key conflict "
						+ resource);
			}
			resourceToNetworkResources.put(resource, netResources);
		} else {
			// Stop any that did manage to start
			for (NetworkResource nr : netResources) {
				vpnService.stopLocalForwarding(nr, serviceClient);
			}
		}

		return success;
	}

	private boolean startWebsiteResource(Resource resource,
			WebsiteResourceTemplate wrt) {
		boolean success = false;
		List<NetworkResource> netResources = new ArrayList<NetworkResource>();
		final NetworkResource netResource = vpnService.createURLForwarding(serviceClient,
				wrt.getLaunchUrl(), wrt.getId());
		if (netResource != null) {
			success = true;
			netResources.add(netResource);
			for (String url : wrt.getAdditionalUrls()) {
				final NetworkResource additionalNetResource = vpnService.createURLForwarding(serviceClient,
						url, wrt.getId());
				if (additionalNetResource == null) {
					success = false;
					break;
				} else {
					netResources.add(additionalNetResource);
				}
			}
		}

		if (success) {
			if (resourceToNetworkResources.containsKey(resource)) {
				throw new IllegalStateException("Resource key conflict "
						+ resource);
			}
			resourceToNetworkResources.put(resource, netResources);
		} else {
			// Stop any that did manage to start
			for (NetworkResource nr : netResources) {
				vpnService.stopLocalForwarding(nr, serviceClient);
			}
		}
		return success;
	}

	private void stopAllNetworkResourcesForResource(Resource resource) {
		List<NetworkResource> netResources = resourceToNetworkResources
				.get(resource);
		if (netResources == null) {
			log.warn(String
					.format("Could not find any started network resource for the resource %s",
							resource.getName()));
			for (Map.Entry<Resource, List<NetworkResource>> en : resourceToNetworkResources
					.entrySet()) {
			}
		} else {
			for (NetworkResource r : netResources) {
				vpnService.stopLocalForwarding(r, serviceClient);
			}
			resourceToNetworkResources.remove(resource);
		}
	}

}
