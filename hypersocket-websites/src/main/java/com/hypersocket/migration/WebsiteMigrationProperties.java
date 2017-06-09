package com.hypersocket.migration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.hypersocket.migration.properties.MigrationProperties;
import com.hypersocket.migration.repository.MigrationExportCriteriaBuilder;
import com.hypersocket.migration.repository.MigrationLookupCriteriaBuilder;
import com.hypersocket.repository.AbstractEntity;
import com.hypersocket.websites.WebsiteResource;

public class WebsiteMigrationProperties implements MigrationProperties {

	@Override
	public Short sortOrder() {
		return 9000;
	}

	@Override
	public List<Class<? extends AbstractEntity<Long>>> getOrderList() {
		return Arrays.<Class<? extends AbstractEntity<Long>>>asList(
				WebsiteResource.class
		);
	}

	@Override
	public Map<Class<?>, MigrationExportCriteriaBuilder> getExportCriteriaMap() {
		return Collections.emptyMap();
	}

	@Override
	public Map<Class<?>, MigrationLookupCriteriaBuilder> getLookupCriteriaMap() {
		return Collections.emptyMap();
	}

}
