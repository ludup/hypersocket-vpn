package com.hypersocket.htmlapplications;

import org.springframework.stereotype.Component;

import com.hypersocket.applications.ApplicationResourceHelper;

@Component
public class HTMLApplicationResourceHelper
		extends ApplicationResourceHelper<HTMLApplicationResource, HTMLApplicationTemplate> {

	public HTMLApplicationResourceHelper() {
		super(HTMLApplicationResourceServiceImpl.RESOURCE_BUNDLE);
	}
}
