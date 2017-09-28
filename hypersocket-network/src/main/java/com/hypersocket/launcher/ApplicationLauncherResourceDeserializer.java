package com.hypersocket.launcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ApplicationLauncherResourceDeserializer extends JsonDeserializer<ApplicationLauncherResource> {

	@Override
	public ApplicationLauncherResource deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		ApplicationLauncherResource resource = new ApplicationLauncherResource();
		resource.setName(node.get("name").asText());
		resource.setLogo(node.has("logo") ? node.get("logo").asText() : "");
		resource.setHidden(node.has("hidden") ? node.get("hidden").booleanValue() : false);
		resource.setResourceCategory(node.has("resourceCategory") ? node.get("resourceCategory").asText() : "Default");
		resource.setSystem(node.has("system") ? node.get("system").booleanValue() : false);
		resource.setExe(node.has("exe") ? node.get("exe").asText() : "");
		resource.setType(node.has("type") ? ApplicationLauncherType.valueOf(node.get("type").asText())
				: ApplicationLauncherType.CLIENT);
		resource.setArgs(node.has("args") ? node.get("args").asText() : "");
		if (node.has("variablesMap")) {
			JsonNode m = node.get("variablesMap");
			Map<String, String> map = new HashMap<>();
			for (Iterator<String> it = m.fieldNames(); it.hasNext();) {
				String k = it.next();
				map.put(k, m.get(k).asText());
			}
			resource.setVariablesMap(map);
		} else {
			resource.setVariables(node.has("variables") ? node.get("variables").asText() : "");
		}
		if (node.has("os")) {
			ApplicationLauncherOS os = null;
			for (ApplicationLauncherOS o : ApplicationLauncherOS.values()) {
				if (node.get("os").has("id") && o.getId() == node.get("os").get("id").intValue()) {
					os = o;
				}
			}
			resource.setOs(os);
		}
		resource.setStartupScript(node.has("startupScript") ? node.get("startupScript").asText() : "");
		resource.setShutdownScript(node.has("shutdownScript") ? node.get("shutdownScript").asText() : "");
		resource.setInstallScript(node.has("installScript") ? node.get("installScript").asText() : "");
		return resource;
	}

}
