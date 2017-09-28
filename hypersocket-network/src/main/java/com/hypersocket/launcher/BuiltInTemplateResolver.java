package com.hypersocket.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hypersocket.properties.PropertyCategory;
import com.hypersocket.tables.BootstrapTableResult;

@Component
public class BuiltInTemplateResolver implements ApplicationLauncherTemplateResolver {
	final static Logger LOG = LoggerFactory.getLogger(BuiltInTemplateResolver.class);

	public final static String RESOLVER_ID = "builtIn";

	@Autowired
	private ApplicationLauncherResourceService applicationLauncherResourceService;

	@PostConstruct
	private void setup() {
		applicationLauncherResourceService.registerTemplateResolver(this);
	}

	@Override
	public String getId() {
		return RESOLVER_ID;
	}

	@Override
	public BootstrapTableResult<ApplicationLauncherTemplate> resolveTemplates(String search, int iDisplayStart,
			int iDisplayLength) throws IOException {
		List<ApplicationLauncherTemplate> rows = new ArrayList<>();
		for (Enumeration<URL> en = getClass().getClassLoader().getResources("application.json"); en
				.hasMoreElements();) {
			ObjectMapper om = new ObjectMapper();
			om.enable(Feature.ALLOW_COMMENTS);
			om.enable(Feature.ALLOW_MISSING_VALUES);
			SimpleModule testModule = new SimpleModule("MyModule", new Version(1, 0, 0, null, null, null))
					.addDeserializer(PropertyCategory.class, new ApplicationTemplatePropertyCategoryDeserializer());
			om.registerModule(testModule);
			URL url = en.nextElement();
			try (InputStream in = url.openStream()) {
				String json = IOUtils.toString(in);

				try {
					if (json.startsWith("[")) {
						/* Is array of application templates */
						List<ApplicationLauncherTemplate> readValue = om.readValue(json,
								new TypeReference<List<ApplicationLauncherTemplate>>() {
								});
						rows.addAll(readValue);
					} else {
						/* Is single application template object */
						rows.add(om.readValue(json, ApplicationLauncherTemplate.class));
					}
				} catch (JsonParseException jpe) {
					LOG.error(String.format("Failed to load template %s.", url), jpe);
				}
			}
		}

		/* Make sure all the templates have a fake (negative) resource ID */
		for (ApplicationLauncherTemplate templ : rows)
			if (templ.getId() == null)
				templ.setId(Math.abs(templ.hashCode()) * -1l);

		long total = rows.size();
		if (!rows.isEmpty())
			rows = rows.subList(Math.max(0, iDisplayStart), Math.min(iDisplayStart + iDisplayLength, rows.size()));
		BootstrapTableResult<ApplicationLauncherTemplate> r = new BootstrapTableResult<>();
		r.setRows(rows);
		r.setTotal(total);
		return r;
	}

}
