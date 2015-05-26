package com.hypersocket.launcher;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;

public class ApplicationLauncherResourceDeserializer extends JsonDeserializer<ApplicationLauncherResource> {

	@Override
	public ApplicationLauncherResource deserialize(JsonParser jp,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		ApplicationLauncherResource resource=new ApplicationLauncherResource();
		resource.setName(node.get("name").asText());
		resource.setHidden(((BooleanNode) node.get("hidden")).booleanValue());
		resource.setResourceCategory(node.get("resourceCategory").asText());
		resource.setSystem(((BooleanNode) node.get("system")).booleanValue());
		resource.setExe(node.get("exe").asText());
		resource.setArgs(node.get("args").asText());
		JsonNode osnode=node.get("os");
		ApplicationLauncherOS os=ApplicationLauncherOS.values()[((IntNode)osnode.get("id")).intValue()];
		resource.setOs(os);
		resource.setStartupScript(node.get("startupScript").asText());
		resource.setShutdownScript(node.get("shutdownScript").asText());
		return resource;
	}

}
