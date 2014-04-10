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

		if(log.isInfoEnabled()) {
			log.info("Starting Network Resources");
		}
		
		this.serviceClient = serviceClient;
		try {
			String json = serviceClient.getTransport().get("myNetworkResources");

			int errors = processNetworkResources(json, serviceClient);

			if (errors > 0) {
				// Warn
				serviceClient.showWarning(errors + " ports could not be opened.");
			}

			return true;
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error("Could not start network resources", e);
			}
			return false;
		}

	}
	
	protected int processNetworkResources(String json, HypersocketClient<?> serviceClient) throws IOException {

		try {
			JSONParser parser = new JSONParser();

			JSONObject result = (JSONObject) parser.parse(json);

			if (log.isDebugEnabled()) {
				log.debug(result.toJSONString());
			}

			JSONArray fields = (JSONArray) result.get("resources");

			int totalPorts = 0;
			int totalErrors = 0;

			@SuppressWarnings("unchecked")
			Iterator<JSONObject> it = (Iterator<JSONObject>) fields.iterator();
			while (it.hasNext()) {
				JSONObject field = it.next();

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
							totalPorts++;
							try {
								NetworkResource resource = new NetworkResource(
										id, hostname, (int) port);
								boolean success = startLocalForwarding(resource);

								if (log.isInfoEnabled()) {
									log.info("Local forwarding to "
											+ hostname
											+ ":"
											+ (success ? resource.getLocalPort() : resource.getPort())
											+ (success ? " succeeded"
													: " failed"));
								}

								if (!success) {
									totalErrors++;
								} else {
									template.addLiveResource(resource);
								}

							} catch (Exception e) {
								totalErrors++;
								if (log.isErrorEnabled()) {
									log.error(
											"Failed to start local forwarding",
											e);
								}
							}
						}
					}
				}
			}

			if (totalErrors == totalPorts) {
				// We could not start any resources
				throw new IOException("No network resources could be started!");
			}

			return totalErrors;

		} catch (ParseException e) {
			throw new IOException("Failed to parse network resources json", e);
		}
	}

	@Override
	public void stop() {
		
		if(log.isInfoEnabled()) {
			log.info("Stopping Network Resources plugin");
		}
		
		stopAllForwarding();
		
		try {
			if (mgr != null) {
				mgr.cleanup();
			}
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error("Error cleaning up hosts file manager", e);
			}
		}
		
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
		serviceClient.getTransport().stopAllForwarding();
	}
	
	public void stopLocalForwarding(NetworkResource resource) {
		String key = resource.getLocalInterface() + ":" + resource.getLocalPort();
		if (localForwards.containsKey(key)) {
			serviceClient.getTransport().stopLocalForwarding(resource.getLocalInterface(), resource.getLocalPort());
			try {
				redirector.stopRedirecting(resource.getLocalInterface(), resource.getPort(), resource.getLocalInterface(), resource.getLocalPort());
			} catch(Exception e) {
				log.error("Failed to stop local forwarding redirect", e);
			} finally {
				localForwards.remove(key);
			}
		}
	}
}
