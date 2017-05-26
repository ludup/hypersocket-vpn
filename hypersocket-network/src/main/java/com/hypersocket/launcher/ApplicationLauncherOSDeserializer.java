package com.hypersocket.launcher;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ApplicationLauncherOSDeserializer extends JsonDeserializer<ApplicationLauncherOS> {

	@Override
	public ApplicationLauncherOS deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		String name = node.get("name").asText();
		return ApplicationLauncherOS.valueOf(name);
	}

		  
}