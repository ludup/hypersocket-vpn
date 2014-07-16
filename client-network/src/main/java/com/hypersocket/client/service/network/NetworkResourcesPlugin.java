package com.hypersocket.client.service.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hypersocket.client.HypersocketClient;
import com.hypersocket.client.NetworkResource;
import com.hypersocket.client.hosts.AbstractSocketRedirector;
import com.hypersocket.client.hosts.HostsFileManager;
import com.hypersocket.client.hosts.SocketRedirector;
import com.hypersocket.client.service.ServicePlugin;

public class NetworkResourcesPlugin implements ServicePlugin {

	static Logger log = LoggerFactory.getLogger(NetworkResourcesPlugin.class);
	
	List<NetworkResourceTemplate> resources = new ArrayList<NetworkResourceTemplate>();
	Map<String, NetworkResource> localForwards = new HashMap<String, NetworkResource>();
	
	HypersocketClient<?> serviceClient;
	HostsFileManager mgr;
	SocketRedirector redirector;
	
	public NetworkResourcesPlugin() {
	}

	@Override
	public boolean start(HypersocketClient<?> serviceClient) {
		
		this.serviceClient = serviceClient;
		
		if(log.isInfoEnabled()) {
			log.info("Starting Network Resources");
		}
		
		startNetworkResources();
		

		
		return true;
	}
	
	protected void startWebsites() {
		try {
			String json = serviceClient.getTransport().get("websites/myWebsites");

			int errors = processWebsiteResources(json);

			if (errors > 0) {
				// Warn
				serviceClient.showWarning(errors + " websites could not be opened.");
			}

		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error("Could not start network resources", e);
			}
		}
	}
	
	protected int processWebsiteResources(String json) throws IOException {
		
		return processResourceList(json, new ResourceMapper() {

			@Override
			public boolean processResource(JSONObject resource) {
				
				return false;
			}
			
		});
	}
	
	protected void startNetworkResources() {
		try {
			String json = serviceClient.getTransport().get("myNetworkResources");

			int errors = processNetworkResources(json);

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
	
	protected int processResourceList(String json, ResourceMapper mapper) throws IOException {
		try {
			JSONParser parser = new JSONParser();

			JSONObject result = (JSONObject) parser.parse(json);

			if (log.isDebugEnabled()) {
				log.debug(result.toJSONString());
			}

			JSONArray fields = (JSONArray) result.get("resources");

			int totalResources = 0;
			int totalErrors = 0;

			@SuppressWarnings("unchecked")
			Iterator<JSONObject> it = (Iterator<JSONObject>) fields.iterator();
			while (it.hasNext()) {
				if(!mapper.processResource(it.next())) {
					totalErrors++;
				} 
				totalResources++;
			}

			if (totalErrors == totalResources) {
				// We could not start any resources
				throw new IOException("No resources could be started!");
			}

			return totalErrors;

		} catch (ParseException e) {
			throw new IOException("Failed to parse network resources json", e);
		}
	}
	
	protected int processNetworkResources(String json) throws IOException {

		return processResourceList(json, new ResourceMapper() {

			@Override
			public boolean processResource(JSONObject field) {
				
				boolean success = false;
				
				String hostname = (String) field.get("hostname");
				String name = (String) field.get("name");
				Long id = (Long) field.get("id");

				JSONArray protocols = (JSONArray) field.get("protocols");

				@SuppressWarnings("unchecked")
				Iterator<JSONObject> it2 = (Iterator<JSONObject>) protocols
						.iterator();

				if (log.isInfoEnabled()) {
					log.info("Creating forwardings for " + name);
				}

				while (it2.hasNext()) {
					JSONObject protocol = it2.next();

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
							name, hostname, protocolName, transport, startPort,
							endPort);
					resources.add(template);

					if (transport.equals("TCP")) {

						for (long port = startPort; port <= endPort; port++) {

							try {
								NetworkResource resource = new NetworkResource(
										id, hostname, (int) port);
								boolean started = startLocalForwarding(resource);

								if (log.isInfoEnabled()) {
									log.info("Local forwarding to "
											+ hostname
											+ ":"
											+ (success ? resource.getLocalPort() : resource.getPort())
											+ (success ? " succeeded"
													: " failed"));
								}

								if (started) {
									template.addLiveResource(resource);
									success = true;
								} else {
									break;
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
				}
				
				return success;
			}
			
		});
	}

	@Override
	public void stop() {
		
		if(log.isInfoEnabled()) {
			log.info("Stopping Network Resources plugin");
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
		
		if(redirector==null) {
			redirector = AbstractSocketRedirector.getSystemRedirector();
		}

		String alias = mgr.getAlias(resource.getHostname());

		int actualPort;
		if ((actualPort = serviceClient.getTransport().startLocalForwarding("127.0.0.1", 0, resource)) > 0) {
			try {
				redirector.startRedirecting(alias, resource.getPort(), "127.0.0.1", actualPort);
				resource.setLocalPort(actualPort);
				resource.setLocalInterface("127.0.0.1");
				
				localForwards.put("127.0.0.1" + ":" + actualPort, resource);
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
		
		for(NetworkResource resource : localForwards.values()) {
			stopLocalForwarding(resource, false);
		}
		
		localForwards.clear();
	}
	
	public void stopLocalForwarding(NetworkResource resource) {
		stopLocalForwarding(resource, true);
	}
	
	private void stopLocalForwarding(NetworkResource resource, boolean remove) {
		String key = resource.getLocalInterface() + ":" + resource.getLocalPort();
		if (localForwards.containsKey(key)) {
			serviceClient.getTransport().stopLocalForwarding(resource.getLocalInterface(), resource.getLocalPort());
			try {
				redirector.stopRedirecting(resource.getLocalInterface(), resource.getPort(), resource.getLocalInterface(), resource.getLocalPort());
			} catch(Exception e) {
				log.error("Failed to stop local forwarding redirect", e);
			} finally {
				if(remove) {
					localForwards.remove(key);
				}
			}
		}
	}
}
