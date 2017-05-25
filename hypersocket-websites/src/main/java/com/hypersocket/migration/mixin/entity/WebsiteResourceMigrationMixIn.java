package com.hypersocket.migration.mixin.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hypersocket.migration.helper.MigrationDeserializer;
import com.hypersocket.migration.helper.MigrationSerializerForResource;
import com.hypersocket.migration.mixin.MigrationMixIn;
import com.hypersocket.permissions.Role;
import com.hypersocket.realm.Realm;
import com.hypersocket.websites.WebsiteResource;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WebsiteResourceMigrationMixIn extends WebsiteResource implements MigrationMixIn{

	private static final long serialVersionUID = 3959383050789975058L;

	private WebsiteResourceMigrationMixIn() {}
	
	@Override
    @JsonIgnore
    public Realm getRealm() { return null;}
	
	@Override
    @JsonSerialize(contentUsing = MigrationSerializerForResource.class)
    @JsonDeserialize(contentUsing = MigrationDeserializer.class)
    public Set<Role> getRoles() {return null;}
}
