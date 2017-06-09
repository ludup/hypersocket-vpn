package com.hypersocket.migration.mixin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonDeserializer.None;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hypersocket.migration.mixin.MigrationMixIn;
import com.hypersocket.protocols.NetworkProtocol;
import com.hypersocket.realm.Realm;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = None.class)
public class NetworkProtocolMigrationMixIn extends NetworkProtocol implements MigrationMixIn {
	
	private static final long serialVersionUID = 1912643434432905713L;

	private NetworkProtocolMigrationMixIn() {}

	@Override
    @JsonIgnore
    public Realm getRealm() { return null;}
	
	
}
