package com.hypersocket.launcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hypersocket.tables.BootstrapTableResult;
import com.hypersocket.utils.HttpUtils;

@Component
public class OnlineTemplateResolver implements ApplicationLauncherTemplateResolver {
	final static Logger log = LoggerFactory.getLogger(OnlineTemplateResolver.class);

	public final static String RESOLVER_ID = "online";
	
	@Autowired
	private ApplicationLauncherResourceService applicationLauncherResourceService;
	
	@Autowired
	private HttpUtils httpUtils;
	
	@PostConstruct
	private void setup() {
		applicationLauncherResourceService.registerTemplateResolver(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BootstrapTableResult<ApplicationLauncherTemplate> resolveTemplates(String search, int iDisplayStart, int iDisplayLength) throws IOException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("search", search);
		params.put("offset", String.valueOf(iDisplayStart));
		params.put("limit", String.valueOf(iDisplayLength));
		params.put("order", "asc");

		String url = System.getProperty("hypersocket.templateServerUrl",
				"https://updates2.hypersocket.com/hypersocket/api/templates")
				+ "/"
				+ (Boolean
						.getBoolean("hypersocketLauncher.enablePrivate") ? "developer"
						: "table") + "/2";
		
		if(log.isDebugEnabled()) {
			log.debug(String.format("Loading templates from %s", url));
		}
		
		String json = httpUtils
				.doHttpPost(
						url, params, true);

		ObjectMapper mapper = new ObjectMapper();

		return mapper.readValue(json, BootstrapTableResult.class);
		
	}

	@Override
	public String getId() {
		return RESOLVER_ID;
	}

	
}
