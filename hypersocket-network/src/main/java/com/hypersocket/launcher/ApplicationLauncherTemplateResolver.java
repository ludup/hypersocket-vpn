package com.hypersocket.launcher;

import java.io.IOException;

import com.hypersocket.tables.BootstrapTableResult;

public interface ApplicationLauncherTemplateResolver {

	String getId();
	
	BootstrapTableResult<ApplicationLauncherTemplate> resolveTemplates(String search, int iDisplayStart, int iDisplayLength) throws IOException;
}
