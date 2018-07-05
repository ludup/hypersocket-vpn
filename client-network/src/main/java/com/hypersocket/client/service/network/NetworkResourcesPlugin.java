package com.hypersocket.client.service.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hypersocket.Version;
import com.hypersocket.client.NetworkResource;
import com.hypersocket.client.hosts.HostsFileManager;
import com.hypersocket.client.hosts.SocketRedirector;
import com.hypersocket.client.rmi.ApplicationLauncher;
import com.hypersocket.client.rmi.ApplicationLauncherTemplate;
import com.hypersocket.client.rmi.BrowserLauncher;
import com.hypersocket.client.rmi.FileMetaData;
import com.hypersocket.client.rmi.FileMetaDataResourceStatus;
import com.hypersocket.client.rmi.Resource;
import com.hypersocket.client.rmi.Resource.Type;
import com.hypersocket.client.rmi.ResourceImpl;
import com.hypersocket.client.rmi.ResourceProtocolImpl;
import com.hypersocket.client.rmi.ScriptLauncher;
import com.hypersocket.client.service.AbstractServicePlugin;
import com.hypersocket.client.service.ResourceMapper;

public class NetworkResourcesPlugin extends AbstractServicePlugin {

	public class NetworkResourceDetail {
		List<NetworkResource> networkResources = new ArrayList<>();
		NetworkResourceTemplate networkResourceTemplate;
		WebsiteResourceTemplate websiteResourceTemplate;
	}

	static Logger log = LoggerFactory.getLogger(NetworkResourcesPlugin.class);

	Map<String, NetworkResource> localForwards = new HashMap<>();
	Map<Resource, NetworkResourceDetail> resourceDetails = new HashMap<>();
	Map<Resource, NetworkResourceDetail> startedResourceDetails = new HashMap<>();
	Map<Resource, List<Resource>> childResources = new HashMap<>();

	HostsFileManager mgr;
	SocketRedirector redirector;

	String[] ALLOWED_SYSTEM_PROPERTIES = { "user.name", "user.home", "user.dir" };
	
	public NetworkResourcesPlugin() {
		super("websites", "networkResources");
	}

	@Override
	protected void reloadResources(List<Resource> realmResources) {
		if (log.isInfoEnabled()) {
			log.info("Loading Network Resources");
		}

		startNetworkResources(realmResources);

		if (log.isInfoEnabled()) {
			log.info("Loading Websites");
		}

		startWebsites(realmResources);
 
		if (log.isInfoEnabled()) {
			log.info("Loaded Resoruces");
		}
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
					res.setConnectionId(serviceClient.getAttachment().getId());

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
		final Set<Long> alreadyInstalled = new HashSet<>();

		return processResourceList(json, new ResourceMapper() {

			@Override
			public boolean processResource(JSONObject field) {

				boolean success = false;

				try {
					String name = (String) field.get("name");
					Long id = (Long) field.get("id");
					String hostname = serviceClient.processReplacements((String) field.get("hostname"), variables);
					String destinationHostname = (String) field.get("destinationHostname");
					
					//String endpointLogo = field.containsKey("logo") ? (String) field.get("logo") : null;
	
					if (StringUtils.isBlank(destinationHostname)) {
						destinationHostname = serviceClient.processReplacements(hostname, variables);
					} else {
						destinationHostname = serviceClient.processReplacements(destinationHostname, variables);
					}
					
					/* The hostname may resolve as empty if a replacement variables is being used and
					 * the user has not set this.
					 */
					if(destinationHostname.isEmpty())
						throw new Exception(String.format("No hostname for resource destinationHostname=%s hostname=%s", 
								field.get("destinationHostname"), 
								field.get("hostname")));
	
					if(log.isInfoEnabled()) {
						log.info(String.format("Processing endpoint %s (%d) to %s using hostname %s", name, id, destinationHostname, hostname));
					}
					List<ProtocolTemplate> protocolTemplates = new ArrayList<>();
	
					JSONArray launchers = (JSONArray) field.get("launchers");
	
					@SuppressWarnings("unchecked")
					Iterator<JSONObject> it3 = launchers.iterator();
	
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
	
					List<ApplicationLauncherTemplate> launcherTemplates = new ArrayList<>();
					while (it3.hasNext()) {
						JSONObject launcher = it3.next();
	
						String family = (String) launcher.get("osFamily");
						String version = (String) launcher.get("osVersion");
						Long lid = (Long) launcher.get("id");
	
						if (System.getProperty("os.name").toLowerCase().startsWith(family.toLowerCase())) {
							Version launcherVersion = new Version(version);
							if (ourVersion == null || ourVersion.compareTo(launcherVersion) >= 0) {
								
								if(log.isDebugEnabled()) {
									log.debug(String.format("Endpoint OS %s %s matches %s %s", 
											System.getProperty("os.name"), System.getProperty("os.version"), family, version));
								}
								String n = (String) launcher.get("name");
								String exe = (String) launcher.get("exe");
								String logo = (String) launcher.get("logo");
								String args = (String) launcher.get("args");
								String startupScript = (String) launcher.get("startupScript");
								String shutdownScript = (String) launcher.get("shutdownScript");
								String installScript = (String) launcher.get("installScript");
								String files = (String) launcher.get("files");
								
								Calendar launcherModifiedDate = Calendar.getInstance();
								Number launcherModifiedDateTime = (Number) launcher.get("modifiedDate");
								launcherModifiedDate.setTimeInMillis(Math.max(modifiedDateTime.longValue(), launcherModifiedDateTime.longValue()));
								
								File applicationDirectory = new File(System.getProperty("user.dir"), n);
								if(!applicationDirectory.exists()) {
									applicationDirectory.mkdirs();
								}
								
								ApplicationLauncherTemplate launcherTemplate = new ApplicationLauncherTemplate(
										lid, 
										n, 
										exe, 
										startupScript,
										shutdownScript, 
										applicationDirectory.getAbsolutePath(), 
										logo, 
										variables, 
										launcherModifiedDate, 
										args==null ? new String[0] : args.split("\\]\\|\\["));
								
								launcherTemplates.add(launcherTemplate);
								
								/*
								 * We only want to do this ONCE per actual application, not launcher
								 * template. If not, as soon as you have more than one launcher for 
								 * a template, the same files will be downloaded over and over again,
								 * and because the modification dates differ for each template (they
								 * are actually the latest date of all related resources), this behavior
								 * will continue on every login.
								 */
								if(!alreadyInstalled.contains(lid)) {
									try {
										downloadAndInstall(launcherTemplate, files!=null ? files.split("\\]\\|\\[") : new String[] {}, installScript);
									}
									finally {
										alreadyInstalled.add(lid);
									}
								}
							} else {
								if(log.isDebugEnabled()) {
									log.debug(String.format("Endpoint OS %s %s DOES NOT match %s %s", 
											System.getProperty("os.name"), System.getProperty("os.version"), family, version));
								}
							}
						} else {
							if(log.isDebugEnabled()) {
								log.debug(String.format("Endpoint OS %s %s DOES NOT match %s %s", 
										System.getProperty("os.name"), System.getProperty("os.version"), family, version));
							}
						}
					}
					JSONArray protocols = (JSONArray) field.get("protocols");
	
					@SuppressWarnings("unchecked")
					Iterator<JSONObject> it2 = protocols.iterator();
	
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
						res.setConnectionId(serviceClient.getAttachment().getId());
						
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
						res.setConnectionId(serviceClient.getAttachment().getId());
	
						// TODO - this is disabled for now, until the grouping
						// component is completed.
						// res.setGroup(launcherTemplate.getName());
						// res.setGroupIcon(launcherTemplate.getLogo());
						res.setGroup(res.getName());
	
						res.setResourceLauncher(new ApplicationLauncher(
								serviceClient.getPrincipalName(),
								hostname, 
								launcherTemplate));
	
						// Add to the list of resources found
						realmResources.add(res);
						
						/* This specifies that when THIS updates, we actually want ALL of the protocol resource as well */ 
						childResources.put(res, protocolResources);
	
						// The resource detail is shared across the launchable
						// resources
	//					resourceDetails.put(res, detail);
					}
					success = true;

				} catch(Throwable t) {
					log.error("Error loading resource", t);
				}
				return success;
			}

		}, "network resources");
	}

	protected boolean downloadAndInstall(ApplicationLauncherTemplate launcherTemplate, String[] files, String installScript) {

		
		try {
			
			boolean newInstall = true;
			File installDir = new File(launcherTemplate.getApplicationDirectory());
			File installFile = new File(launcherTemplate.getApplicationDirectory(), ".installed");
			if(installFile.exists()) {
				newInstall = false;
				if(log.isInfoEnabled()) {
					log.info(String.format("%s is already installed. Last modified timestamp is %d and app current timestamp is %d",
							launcherTemplate.getName(),
							installFile.lastModified(), 
							launcherTemplate.getModifiedDate().getTimeInMillis()));
				}
				if(installFile.lastModified()==launcherTemplate.getModifiedDate().getTimeInMillis()) {
					if(log.isInfoEnabled()) {
						log.info(String.format("%s is up-to-date", launcherTemplate.getName()));
					}
					return true;
				}
			}
			
			
			if(log.isInfoEnabled()) {
				log.info(String.format("%s is being %s", launcherTemplate.getName(), newInstall ? "installed" : "updated"));
			}
			Map<String,FileMetaData> downloadFiles = processDownloadRequirements(files, installDir);
			
			long length = 0;
			long totalSoFar = 0;
			for(FileMetaData file : downloadFiles.values()) {
				length += file.getFileSize();
			}
			
			guiRegistry.onUpdateInit(1);
			guiRegistry.getGUI().onUpdateStart(launcherTemplate.getName(), length, null);
		
			for(FileMetaData file : downloadFiles.values()) {
				totalSoFar = downloadFile(file, installDir, launcherTemplate.getName(), totalSoFar, length);
				guiRegistry.getGUI().onUpdateProgress(launcherTemplate.getName(), file.getFileSize(), totalSoFar, length);
			}
		
			int exitCode = 0;
			if(StringUtils.isNotBlank(installScript)) {
				
				Map<String,String> properties = new HashMap<>();
				
				properties.put("username", serviceClient.getPrincipalName());
				properties.put("timestamp", String.valueOf(System.currentTimeMillis()));
				properties.put("java.home", System.getProperty("java.home"));
				properties.put("client.appdir", installDir.getAbsolutePath());
				for(String prop : ALLOWED_SYSTEM_PROPERTIES) {
					properties.put(prop.replace("user.", "client.user"), System.getProperty(prop));
				}
				
				properties.putAll(launcherTemplate.getVariables());
				
				ScriptLauncher script = new ScriptLauncher(installScript, installDir, properties);
				script.addArg(newInstall ? "-i" : "-u");
				exitCode = script.launch();
				
				if(exitCode!=0) {
					guiRegistry.onUpdateDone(false, "Installs script returned non-zero exit code! Installation may have failed.");
				} else {
					guiRegistry.onUpdateComplete(launcherTemplate.getName(), length);
					guiRegistry.onUpdateDone(false, null);
				}
			} else {
				guiRegistry.onUpdateComplete(launcherTemplate.getName(), length);
				guiRegistry.onUpdateDone(false, null);
			}
			
			if(!installFile.exists()) {
				installFile.createNewFile();
			}
			installFile.setLastModified(launcherTemplate.getModifiedDate().getTimeInMillis());
			
			return true;
		
		} catch(IOException e)  { 
			log.error("Failed to install app " + launcherTemplate.getName(), e);
			guiRegistry.onUpdateFailure(launcherTemplate.getName(), e);
		} 
		
		return false;
	}
	
	private Map<String,FileMetaData> processDownloadRequirements(String files[], File applicationDirectory) throws IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,FileMetaData> data = new HashMap<>();
		
		for(String file : files) {
			if(StringUtils.isBlank(file)) {
				continue;
			}
			String fileJson = serviceClient.getTransport().get("files/file/" + file);
			FileMetaDataResourceStatus meta = objectMapper.readValue(fileJson, FileMetaDataResourceStatus.class);
			if(!meta.isSuccess()) {
				throw new IOException("Could not find file " + file);
			}
			File metaFile = new File(applicationDirectory, meta.getResource().getFileName());
			if(metaFile.exists()) {
				if(checkMD5Sum(new FileInputStream(metaFile), 
						meta.getResource().getMd5Sum(), 
						meta.getResource().getFileName())) {
					continue;
				}
			}
			
			data.put(file, meta.getResource());
		}
		
		return data;
	}
	
	private long downloadFile(FileMetaData meta, File applicationDirectory, String app, long totalSoFar, long totalLength) throws IOException {

		
		File metaFile = new File(applicationDirectory, meta.getFileName());

		InputStream in = serviceClient.getTransport().getContent("files/download/" + meta.getName(), 60000);
		OutputStream out = new FileOutputStream(metaFile);
		
		byte[] buf = new byte[32768];
		try {
			while(true) {
				int r = in.read(buf);
				if(r==-1) {
					break;
				}
				out.write(buf, 0, r);
				guiRegistry.onUpdateProgress(app, r, totalSoFar += r, totalLength);
			}
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		
		if(!checkMD5Sum(new FileInputStream(metaFile), meta.getMd5Sum(), meta.getFileName())) {
			throw new IOException(meta.getFileName() + " is corrupt!");
		}
		
		return totalSoFar;
	}

	private boolean checkMD5Sum(InputStream in, String expectedMd5Sum, String filename) {
		if(log.isDebugEnabled()) {
			log.debug("Checking " + filename + " digest");
		}
		try {
			String localFileHash = Hex.encodeHexString(DigestUtils.md5(in));
			if(localFileHash.equalsIgnoreCase(expectedMd5Sum)) {
				if(log.isDebugEnabled()) {
					log.debug(filename + " exists and has not changed since last use");
				}
				return true;
			}
			if(log.isDebugEnabled()) {
				log.debug(filename + " has changed since last use. Updating.");
			}
			return false;
		} catch(IOException ex) { 
			if(log.isErrorEnabled()) {
				log.error("Failed to read file during digest calculation", ex);
			}
			return false;
		} finally {
			IOUtils.closeQuietly(in);
		}
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
				detail.websiteResourceTemplate.getLaunchUrl(), 
				detail.websiteResourceTemplate.getId());
		if (netResource != null) {
			success = true;
			detail.networkResources.add(netResource);
			for (String url : detail.websiteResourceTemplate.getAdditionalUrls()) {
				final NetworkResource additionalNetResource = vpnService.createURLForwarding(
						serviceClient, 
						url,
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
