package com.hypersocket.applications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.properties.ResourceUtils;
import com.hypersocket.resource.RealmResource;
import com.hypersocket.template.TemplateType;

public abstract class ApplicationTemplateDeserializer<T extends ApplicationTemplate<R>, R extends RealmResource> extends JsonDeserializer<T> {
	
	private Class<R> resourceClass;

	protected abstract T createResource();
	
	protected ApplicationTemplateDeserializer(Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	@Override
	public T deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ObjectCodec codec = jp.getCodec();
		JsonNode node = codec.readTree(jp);
		T resource = createResource();
		resource.setName(node.get("name").asText());
		resource.setScript(node.has("script") ? node.get("script").asText() : "");
		resource.setTemplateLogo(node.has("templateLogo") ? node.get("templateLogo").asText() : "");
		resource.setTemplateType(
				node.has("templateType") ? node.get("templateType").asText() : TemplateType.APPLICATION.name());
		if (node.has("resource")) {
			resource.setResource(codec.treeToValue(node.get("resource"), resourceClass));
		}
		if (node.has("categories")) {
			List<PropertyCategory> l = new ArrayList<>();
			JsonNode categoryNodes = node.get("categories");
			for (JsonNode cat : categoryNodes) {
				l.add(codec.treeToValue(cat, PropertyCategory.class));
			}
			resource.setCategories(l);
		}
		if (node.has("variables")) {
			List<String> varMap = new ArrayList<>();
			for (Iterator<Map.Entry<String, JsonNode>> en = node.get("variables").fields(); en.hasNext();) {
				Map.Entry<String, JsonNode> men = en.next();
				varMap.add(men.getKey() + "=" + men.getValue().asText());
			}
			resource.setVariables(ResourceUtils.implodeValues(varMap));
		}
		return resource;
	}

}
