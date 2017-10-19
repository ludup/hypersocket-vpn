package com.hypersocket.applications;

import java.io.IOException;

import com.hypersocket.tables.BootstrapTableResult;

public interface ApplicationTemplateResolver<T extends ApplicationTemplate<?>> {

	String getId();
	
	BootstrapTableResult<T> resolveTemplates(String search, int iDisplayStart, int iDisplayLength) throws IOException;
}
