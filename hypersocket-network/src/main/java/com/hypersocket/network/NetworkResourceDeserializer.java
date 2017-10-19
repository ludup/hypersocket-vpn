package com.hypersocket.network;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.hypersocket.applications.ApplicationOS;
import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.server.forward.ForwardingTransport;

public class NetworkResourceDeserializer extends
		JsonDeserializer<NetworkResource> {

	@Override
	public NetworkResource deserialize(JsonParser jp,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		NetworkResource resource = new NetworkResource();
		resource.setName(node.get("name").asText());
		resource.setHidden(node.get("hidden").asBoolean());
		resource.setResourceCategory(node.get("resourceCategory").asText());
		resource.setSystem(node.get("system").asBoolean());

		resource.setHostname(node.get("hostname").asText());
		resource.setDestinationHostname(node.get("destinationHostname")
				.asText());

		Set<NetworkProtocol> networkProtocolList = new HashSet<NetworkProtocol>();
		Iterator<JsonNode> protocolIterator = node.get("protocols").iterator();
		while (protocolIterator.hasNext()) {
			JsonNode protocol = protocolIterator.next();
			NetworkProtocol networkProtocol = new NetworkProtocol();

			networkProtocol.setName(protocol.get("name").asText());
			networkProtocol.setHidden(protocol.get("hidden").asBoolean());
			networkProtocol.setResourceCategory(protocol
					.get("resourceCategory").asText());
			networkProtocol.setSystem(protocol.get("system").asBoolean());

			networkProtocol.setTransport(ForwardingTransport.valueOf(protocol.get(
					"transport").asText()));
			networkProtocol.setStartPort(protocol.get("startPort").asInt());
			networkProtocol.setEndPort(protocol.get("endPort").asInt());

			networkProtocolList.add(networkProtocol);
		}
		resource.setProtocols(networkProtocolList);

		Set<ApplicationLauncherResource> launcherlList = new HashSet<ApplicationLauncherResource>();
		Iterator<JsonNode> launcherIterator = node.get("launchers").iterator();
		while (launcherIterator.hasNext()) {
			JsonNode launcher = launcherIterator.next();
			ApplicationLauncherResource applicationLauncher = new ApplicationLauncherResource();
			
			applicationLauncher.setName(launcher.get("name").asText());
			applicationLauncher.setHidden(launcher.get("hidden").asBoolean());
			applicationLauncher.setResourceCategory(launcher.get(
					"resourceCategory").asText());
			applicationLauncher.setSystem(launcher.get("system").asBoolean());

			applicationLauncher.setExe(launcher.get("exe").asText());
			applicationLauncher.setArgs(launcher.get("args").asText());
			JsonNode osnode = launcher.get("os");
			ApplicationOS os = ApplicationOS.values()[((IntNode) osnode
					.get("id")).intValue()];
			applicationLauncher.setOs(os);
			applicationLauncher.setStartupScript(launcher.get("startupScript")
					.asText());
			applicationLauncher.setShutdownScript(launcher
					.get("shutdownScript").asText());

			launcherlList.add(applicationLauncher);
		}
		resource.setLaunchers(launcherlList);

		return resource;
	}
}
