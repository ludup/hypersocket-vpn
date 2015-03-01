package com.hypersocket.client.service.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.SystemUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hypersocket.Version;
import com.hypersocket.client.HypersocketClient;
import com.hypersocket.client.NetworkResource;
import com.hypersocket.client.hosts.AbstractSocketRedirector;
import com.hypersocket.client.hosts.HostsFileManager;
import com.hypersocket.client.hosts.SocketRedirector;
import com.hypersocket.client.i18n.I18N;
import com.hypersocket.client.rmi.ApplicationLauncher;
import com.hypersocket.client.rmi.ApplicationLauncherTemplate;
import com.hypersocket.client.rmi.BrowserLauncher;
import com.hypersocket.client.rmi.GUICallback;
import com.hypersocket.client.rmi.ResourceImpl;
import com.hypersocket.client.rmi.ResourceProtocolImpl;
import com.hypersocket.client.rmi.ResourceRealm;
import com.hypersocket.client.rmi.ResourceService;
import com.hypersocket.client.service.AbstractServicePlugin;
import com.hypersocket.client.service.ResourceMapper;

public class NetworkResourcesPlugin extends AbstractServicePlugin {

	static Logger log = LoggerFactory.getLogger(NetworkResourcesPlugin.class);

	List<NetworkResourceTemplate> networkResources = new ArrayList<NetworkResourceTemplate>();
	List<WebsiteResourceTemplate> websiteResources = new ArrayList<WebsiteResourceTemplate>();

	Map<String, NetworkResource> localForwards = new HashMap<String, NetworkResource>();
	Map<String, String> resourceForwards = new HashMap<String, String>();
	
	HypersocketClient<?> serviceClient;
	HostsFileManager mgr;
	SocketRedirector redirector;
	ResourceService resourceService;

	public NetworkResourcesPlugin() {
	}

	@Override
	public boolean start(HypersocketClient<?> serviceClient,
			ResourceService resourceService, GUICallback gui) {

		this.serviceClient = serviceClient;
		this.resourceService = resourceService;

		if (log.isInfoEnabled()) {
			log.info("Starting Network Resources");
		}

		startNetworkResources();

		if (log.isInfoEnabled()) {
			log.info("Starting Websites");
		}
		
		startWebsites();

		return true;
	}

	protected void startWebsites() {
		try {
			String json = serviceClient.getTransport().get(
					"websites/myWebsites");

			int errors = processWebsiteResources(json);

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

	protected int processWebsiteResources(String json) throws IOException {

		return processResourceList(json, new ResourceMapper() {

			@Override
			public boolean processResource(JSONObject resource) {

				boolean success = false;

				try {
					String name = (String) resource.get("name");
					String launchUrl = (String) resource.get("launchUrl");
					String additionalUrls = (String) resource
							.get("additionalUrls");
					Long id = (Long) resource.get("id");

					WebsiteResourceTemplate template = new WebsiteResourceTemplate(
							id, name, launchUrl, additionalUrls
									.split("\\]\\|\\["));
					websiteResources.add(template);

					if (createURLForwarding(launchUrl, template)) {

						success = true;
						for (String url : template.getAdditionalUrls()) {
							if (!createURLForwarding(url, template)) {
								success = false;
								break;
							}
						}

						ResourceRealm resourceRealm = resourceService
								.getResourceRealm(serviceClient.getHost());

						ResourceImpl res = new ResourceImpl(name + " - " + I18N.getResource("text.defaultBrowser"));
						res.setLaunchable(true);
						res.setResourceLauncher(new BrowserLauncher(
								template.getLaunchUrl()));
						resourceRealm.addResource(res);
					}

				} catch (MalformedURLException ex) {
					log.error("Failed to parse launch url", ex);
				} catch (RemoteException ex) {
					log.error(
							"Received remote exception whilst processing resources",
							ex);
				}

				return success;
			}

		}, "websites");
	}

	private boolean createURLForwarding(String forwardedUrl,
			WebsiteResourceTemplate template) {

		boolean success = false;
		try {
			URL url = new URL(forwardedUrl);

			String hostname = url.getHost();

			int port = url.getPort();
			if (port == -1) {
				port = url.getDefaultPort();
			}

			NetworkResource resource = new NetworkResource(template.getId(),
					hostname, url.getHost(), (int) port, "website");
			boolean started = startLocalForwarding(resource);

			if (log.isInfoEnabled()) {
				log.info("Local forwarding to "
						+ hostname
						+ ":"
						+ (started ? resource.getLocalPort() : resource
								.getPort())
						+ (started ? " succeeded" : " failed"));
			}

			if (started) {
				template.addLiveResource(resource);
				success = true;
			}

		} catch (MalformedURLException e) {
			log.error("Failed to parse url " + forwardedUrl, e);
		} catch (IOException e) {
			log.error("Failed to start forwarding for " + forwardedUrl, e);
		}

		return success;
	}

	protected void startNetworkResources() {
		try {
			String json = serviceClient.getTransport()
					.get("networkResources/personal");

			int errors = processNetworkResources(json);

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

	

	protected int processNetworkResources(String json) throws IOException {

		return processResourceList(json, new ResourceMapper() {

			@Override
			public boolean processResource(JSONObject field) {

				boolean success = false;

				try {
					String hostname = (String) field.get("hostname");
					String destinationHostname = (String) field
							.get("destinationHostname");
					String name = (String) field.get("name");
					Long id = (Long) field.get("id");

					JSONArray launchers = (JSONArray) field.get("launchers");
					
					@SuppressWarnings("unchecked")
					Iterator<JSONObject> it3 = (Iterator<JSONObject>) launchers
							.iterator();
					
					// For now, ignore version if on Linux, os.version is not that useful for us
					Version ourVersion = null;
					if(!SystemUtils.IS_OS_LINUX) {
						ourVersion = new Version(System.getProperty("os.version"));
					}
					
					List<ApplicationLauncherTemplate> launcherTemplates = new ArrayList<ApplicationLauncherTemplate>();
					while (it3.hasNext()) {
						JSONObject launcher = it3.next();
						
						String family = (String) launcher.get("osFamily");
						String version = (String) launcher.get("osVersion");
						
						if(System.getProperty("os.name").startsWith(family)) {
							Version launcherVersion = new Version(version);
							if(ourVersion == null || ourVersion.compareTo(launcherVersion) >= 0) {
								String n = (String) launcher.get("name");
								String exe = (String) launcher.get("exe");
								String args = (String) launcher.get("args");
								
								launcherTemplates.add(new ApplicationLauncherTemplate(n, exe, args));
							}
						}
					}
					JSONArray protocols = (JSONArray) field.get("protocols");

					@SuppressWarnings("unchecked")
					Iterator<JSONObject> it2 = (Iterator<JSONObject>) protocols
							.iterator();

					if (log.isInfoEnabled()) {
						log.info("Creating forwardings for " + name);
					}

					ResourceRealm resourceRealm = resourceService
							.getResourceRealm(serviceClient.getHost());

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

						NetworkResourceTemplate template = new NetworkResourceTemplate(
								name, hostname, destinationHostname, protocolName, transport,
								startPort, endPort);
						networkResources.add(template);

						ResourceImpl res = new ResourceImpl(name);
						
						if (transport.equals("TCP")) {

							for (long port = startPort; port <= endPort; port++) {

								try {
									NetworkResource resource = new NetworkResource(
											id, hostname, destinationHostname,
											(int) port, "tunnel");
									
									if(resourceForwards.containsKey(resource.getId())) {
										if (log.isInfoEnabled()) {
											log.info("Skipping forward for resource id " + resource.getId()
													+ " because there is already an active forward");
										}
										continue;
									} else {
										
										if (log.isInfoEnabled()) {
											log.info("Starting forward for resource id " + resource.getId());
										}
										
										boolean started = startLocalForwarding(resource);
	
										if (log.isInfoEnabled()) {
											log.info("Local forwarding to "
													+ hostname
													+ ":"
													+ (started ? resource
															.getLocalPort()
															: resource.getPort())
													+ (started ? " succeeded"
															: " failed"));
										}
	
										if (started) {
	
											ResourceProtocolImpl proto = new ResourceProtocolImpl(
													pid, protocolName);
											res.addProtocol(proto);
											template.addLiveResource(resource);
											
											success = true;
										} else {
											break;
										}
									}

								} catch (Exception e) {
									if (log.isErrorEnabled()) {
										log.error(
												"Failed to start local forwarding",
												e);
									}
								}
							}
						}

	
						if(launcherTemplates.size() > 0) {
							for(ApplicationLauncherTemplate t : launcherTemplates) {
								ResourceImpl app = new ResourceImpl(name + " - " + t.getName());
								app.setLaunchable(true);
								app.setResourceLauncher(new ApplicationLauncher(serviceClient.getPrincipalName(),
										template.getHostname(), t));
								resourceRealm.addResource(app);
							}
						}
					}

				} catch (RemoteException ex) {
					log.error(
							"Received remote exception whilst processing resources",
							ex);
				}

				return success;
			}

		}, "network resources");
	}

	@Override
	public void stop() {

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

		stopAllForwarding();

	}

	@Override
	public String getName() {
		return "Network Resources";
	}

	public boolean startLocalForwarding(NetworkResource resource)
			throws IOException {

		if (mgr == null) {
			mgr = HostsFileManager.getSystemHostsFile();
		}

		if (redirector == null) {
			redirector = AbstractSocketRedirector.getSystemRedirector();
		}

		String alias = mgr.getAlias(resource.getHostname());

		int actualPort;
		if ((actualPort = serviceClient.getTransport().startLocalForwarding(
				"127.0.0.1", 0, resource)) > 0) {
			try {
				redirector.startRedirecting(alias, resource.getPort(),
						"127.0.0.1", actualPort);
				resource.setLocalPort(actualPort);
				resource.setLocalInterface("127.0.0.1");

				localForwards.put("127.0.0.1" + ":" + actualPort, resource);
				resourceForwards.put(resource.getId() + "/" + resource.getPort(), "127.0.0.1" + ":" + actualPort);
				return true;
			} catch (Exception e) {
				log.error("Failed to redirect local forwarding", e);
				return false;
			}
		} else {
			return false;
		}

	}

	public void stopAllForwarding() {

		List<NetworkResource> tmp = new ArrayList<NetworkResource>(localForwards.values());
		for (NetworkResource resource : tmp) {
			stopLocalForwarding(resource);
		}
		
	}

	private void stopLocalForwarding(NetworkResource resource) {
		String key = resource.getLocalInterface() + ":"
				+ resource.getLocalPort();
		if (localForwards.containsKey(key)) {
			serviceClient.getTransport().stopLocalForwarding(
					resource.getLocalInterface(), resource.getLocalPort());
			try {
				redirector.stopRedirecting(resource.getLocalInterface(),
						resource.getPort(), resource.getLocalInterface(),
						resource.getLocalPort());
			} catch (Exception e) {
				log.error("Failed to stop local forwarding redirect", e);
			} finally {
				localForwards.remove(key);
				resourceForwards.remove(resource.getId());
			}
		}
	}
}
