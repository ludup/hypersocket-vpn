package com.hypersocket.applications;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.tables.BootstrapTableResult;

public abstract class BuiltInTemplateResolver<T extends ApplicationTemplate<?>> implements ApplicationTemplateResolver<T> {
	final static Logger LOG = LoggerFactory.getLogger(BuiltInTemplateResolver.class);

	private String id;
	private String resourceName;
	private Class<T> resourceClass;
	
	protected BuiltInTemplateResolver(String id, String resourceName, Class<T> resourceClass) {
		this.id = id;
		this.resourceName = resourceName;
		this.resourceClass = resourceClass;
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public BootstrapTableResult<T> resolveTemplates(String search, int iDisplayStart,
			int iDisplayLength) throws IOException {
		List<T> rows = new ArrayList<>();
		for (Enumeration<URL> en = getClass().getClassLoader().getResources(resourceName); en
				.hasMoreElements();) {
			ObjectMapper om = new ObjectMapper();
			om.enable(Feature.ALLOW_COMMENTS);
			om.enable(Feature.ALLOW_MISSING_VALUES);
			SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null, null, null));
			testModule.addDeserializer(PropertyCategory.class, new ApplicationTemplatePropertyCategoryDeserializer());
			testModule.addDeserializer(ApplicationTemplate.class, createDeserializer());
			om.registerModule(testModule);
			URL url = en.nextElement();
			try (InputStream in = url.openStream()) {
				String json = IOUtils.toString(in);

				try {
					if (json.startsWith("[")) {
						/* Is array of application templates */
						List<T> readValue = om.readValue(json,
								new TypeReference<List<T>>() {
								});
						rows.addAll(readValue);
					} else {
						/* Is single application template object */
						rows.add(om.readValue(json, resourceClass));
					}
				} catch (JsonParseException jpe) {
					LOG.error(String.format("Failed to load template %s.", url), jpe);
				}
			}
		}

		/* Make sure all the templates have a fake (negative) resource ID */
		for (T templ : rows)
			if (templ.getId() == null)
				templ.setId(Math.abs(templ.hashCode()) * -1l);

		long total = rows.size();
		if (!rows.isEmpty())
			rows = rows.subList(Math.max(0, iDisplayStart), Math.min(iDisplayStart + iDisplayLength, rows.size()));
		BootstrapTableResult<T> r = new BootstrapTableResult<>();
		r.setRows(rows);
		r.setTotal(total);
		return r;
	}

	protected abstract JsonDeserializer<T> createDeserializer();

}
