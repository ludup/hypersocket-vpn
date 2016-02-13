package com.hypersocket.launcher.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hypersocket.resource.ResourceUpdate;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationLauncherResourceUpdate extends ResourceUpdate{

	
	public ApplicationLauncherResourceUpdate() {
		
	}
}
