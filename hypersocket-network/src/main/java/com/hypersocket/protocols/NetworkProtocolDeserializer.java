package com.hypersocket.protocols;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.hypersocket.server.forward.ForwardingTransport;

public class NetworkProtocolDeserializer extends
		JsonDeserializer<NetworkProtocol> {

	@Override
	public NetworkProtocol deserialize(JsonParser jp,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		NetworkProtocol protocol = new NetworkProtocol();
		protocol.setName(node.get("name").asText());
		protocol.setHidden(((BooleanNode) node.get("hidden")).booleanValue());
		protocol.setResourceCategory(node.get("resourceCategory").asText());
		protocol.setSystem(((BooleanNode) node.get("system")).booleanValue());
		protocol.setTransport(ForwardingTransport.valueOf(node.get("transport")
				.asText()));
		protocol.setStartPort(((IntNode) node.get("startPort")).intValue());
		if (node.get("endPort") != NullNode.instance) {
			protocol.setEndPort(((IntNode) node.get("endPort")).intValue());
		}
		return protocol;
	}

}
