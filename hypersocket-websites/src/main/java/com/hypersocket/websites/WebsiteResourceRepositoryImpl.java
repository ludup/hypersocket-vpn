package com.hypersocket.websites;

import com.hypersocket.resource.AbstractAssignableResourceRepositoryImpl;

public class WebsiteResourceRepositoryImpl extends
		AbstractAssignableResourceRepositoryImpl<WebsiteResource> implements
		WebsiteResourceRepository {

	@Override
	protected Class<WebsiteResource> getResourceClass() {
		return WebsiteResource.class;
	}

}
