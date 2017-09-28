package com.hypersocket.launcher;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.properties.PropertyStore;
import com.hypersocket.properties.PropertyTemplate;

public class ApplicationTemplatePropertyCategoryDeserializer extends StdDeserializer<PropertyCategory> {

	final static Logger LOG = LoggerFactory.getLogger(ApplicationTemplatePropertyCategoryDeserializer.class);

	private static final long serialVersionUID = 1L;

	public ApplicationTemplatePropertyCategoryDeserializer() {
		super(PropertyCategory.class);
	}

	@Override
	public PropertyCategory deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		/*
		 * TODO there must be a better way than this just to deserialize an
		 * abstract collection of objects (Collection<AbstractPropertyTemplate>)
		 * but 3 hours is enough time wasted on this
		 */

		ObjectCodec codec = p.getCodec();
		JsonNode node = codec.readTree(p);
		PropertyCategory cat = new PropertyCategory();
		cat.setCategoryKey(node.get("categoryKey").asText());
		cat.setCategoryGroup(node.has("categoryGroup") ? node.get("categoryKey").asText() : null);
		cat.setCategoryNamespace(node.has("categoryNamespace") ? node.get("categoryNamespace").asText() : null);
		cat.setBundle(node.has("bundle") ? node.get("bundle").asText() : null);
		cat.setDisplayMode(node.has("displayMode") ? node.get("displayMode").asText() : null);
		cat.setWeight(node.has("weight") ? node.get("weight").asInt() : null);
		cat.setUserCreated(node.has("userCreated") ? node.get("userCreated").asBoolean() : false);
		cat.setSystemOnly(node.has("systemOnly") ? node.get("systemOnly").asBoolean() : false);
		cat.setNonSystem(node.has("nonSystem") ? node.get("nonSystem").asBoolean() : false);
		cat.setHidden(node.has("hidden") ? node.get("hidden").asBoolean() : false);
		cat.setFilter(node.has("filter") ? node.get("filter").asText() : null);
		cat.setName(node.has("name") ? node.get("name").asText() : null);
		cat.setVisibilityDependsOn(node.has("visibilityDependsOn") ? node.get("visibilityDependsOn").asText() : null);
		cat.setVisibilityDependsValue(
				node.has("visibilityDependsValue") ? node.get("visibilityDependsValue").asText() : null);
		if (node.has("templates")) {
			JsonNode templateNodes = node.get("templates");
			for (JsonNode template : templateNodes) {
				PropertyTemplate templateObject = codec.treeToValue(template, PropertyTemplate.class);
				if (templateObject == null)
					LOG.warn("Empty template found in " + cat.getCategoryKey());
				else {
					templateObject.setPropertyStore(new PropertyStore() {

						@Override
						public void setProperty(PropertyTemplate property, String value) {
							// Only ever used on client side
							throw new UnsupportedOperationException();
						}

						@Override
						public void registerTemplate(PropertyTemplate template, String module) {
							throw new UnsupportedOperationException();
						}

						@Override
						public boolean isDefaultStore() {
							return false;
						}

						@Override
						public String getPropertyValue(PropertyTemplate template) {
							return template.getDefaultValue();
						}

						@Override
						public PropertyTemplate getPropertyTemplate(String resourceKey) {
							throw new UnsupportedOperationException();
						}
					});
					cat.getTemplates().add(templateObject);
				}
			}
		}
		return cat;
	}

}
