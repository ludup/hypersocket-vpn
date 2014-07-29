/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network.json;

public class NetworkResourceUpdate {

	Long id;
	String name;
	String hostname;
	String destinationHostname;
	Long[] protocols;
	Long[] launchers;
	Long[] roles;
	
	public NetworkResourceUpdate() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long[] getProtocols() {
		return protocols;
	}

	public void setProtocols(Long[] protocols) {
		this.protocols = protocols;
	}

	public Long[] getLaunchers() {
		return launchers;
	}

	public void setLaunchers(Long[] launchers) {
		this.launchers = launchers;
	}

	public Long[] getRoles() {
		return roles;
	}

	public void setRoles(Long[] roles) {
		this.roles = roles;
	}

	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public void setDestinationHostname(String destinationHostname) {
		this.destinationHostname = destinationHostname;
	}
	
	public String getDestinationHostname() {
		return destinationHostname;
	}

}
