package com.hypersocket.migration.mixin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonDeserializer.None;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hypersocket.launcher.ApplicationLauncherOS;
import com.hypersocket.launcher.ApplicationLauncherOSDeserializer;
import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.migration.mixin.MigrationMixIn;
import com.hypersocket.realm.Realm;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = None.class)
public class ApplicationLauncherResourceMigrationMixIn extends ApplicationLauncherResource implements MigrationMixIn {

	private static final long serialVersionUID = -2841920008464155494L;

	private ApplicationLauncherResourceMigrationMixIn() {}

	@Override
    @JsonIgnore
    public Realm getRealm() { return null;}
	
	@JsonDeserialize(using = ApplicationLauncherOSDeserializer.class)
	public ApplicationLauncherOS getOs() {return null;}
}
