package com.hypersocket.applications;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ApplicationOSDeserializer extends JsonDeserializer<ApplicationOS> {

	@Override
	public ApplicationOS deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
		JsonNode nameNode = node.get("name");
		return nameNode == null ? ApplicationOS.fromId(node.get("id").asInt()) : ApplicationOS.valueOf(nameNode.asText());
	}

		  
}