package com.hypersocket.migration.mixin.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonDeserializer.None;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.migration.helper.MigrationDeserializer;
import com.hypersocket.migration.helper.MigrationSerializerForResource;
import com.hypersocket.migration.mixin.MigrationMixIn;
import com.hypersocket.network.NetworkResource;
import com.hypersocket.permissions.Role;
import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.realm.Realm;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = None.class)
public class NetworkResourceMigrationMixIn extends NetworkResource implements MigrationMixIn {

	private static final long serialVersionUID = -3556876885159275624L;
	
	private NetworkResourceMigrationMixIn() {}

	@Override
    @JsonIgnore
    public Realm getRealm() { return null;}

    @Override
    @JsonSerialize(contentUsing = MigrationSerializerForResource.class)
    @JsonDeserialize(contentUsing = MigrationDeserializer.class)
    public Set<NetworkProtocol> getProtocols() {return null;}
    
    @Override
    @JsonSerialize(contentUsing = MigrationSerializerForResource.class)
    @JsonDeserialize(contentUsing = MigrationDeserializer.class)
    public Set<ApplicationLauncherResource> getLaunchers() { return null;}
    
    @Override
    @JsonSerialize(contentUsing = MigrationSerializerForResource.class)
    @JsonDeserialize(contentUsing = MigrationDeserializer.class)
    public Set<Role> getRoles() {return null;}
}
