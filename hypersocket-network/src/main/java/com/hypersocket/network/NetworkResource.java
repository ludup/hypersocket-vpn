/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hypersocket.launcher.ApplicationLauncherResource;
import com.hypersocket.network.handlers.ForwardingResource;
import com.hypersocket.protocols.NetworkProtocol;

@Entity
@Table(name="network_resources")
public class NetworkResource extends ForwardingResource {

	@Column(name="hostname")
	String hostname;
	
	@Column(name="destination", nullable=true)
	String destinationHostname;
	
	@ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name="network_resource_protocols", 
		joinColumns={@JoinColumn(name="resource_id")}, 
		inverseJoinColumns={@JoinColumn(name="protocol_id")})
	Set<NetworkProtocol> protocols = new HashSet<NetworkProtocol>();

	@ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name="network_resource_launchers", 
		joinColumns={@JoinColumn(name="resource_id")}, 
		inverseJoinColumns={@JoinColumn(name="launcher_id")})
	Set<ApplicationLauncherResource> launchers = new HashSet<ApplicationLauncherResource>();
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setDestinationHostname(String destinationHostname) {
		this.destinationHostname = destinationHostname;
	}
	
	@JsonIgnore
	public String resolveHostname() {
		return StringUtils.isEmpty(destinationHostname) ? hostname : destinationHostname;
	}
	
	public String getDestinationHostname() {
		return destinationHostname;
	}
	
	public Set<NetworkProtocol> getProtocols() {
		return protocols;
	}

	public void setProtocols(Set<NetworkProtocol> protocols) {
		this.protocols = protocols;
	}
	
	public Set<ApplicationLauncherResource> getLaunchers() {
		return launchers;
	}

	public void setLaunchers(Set<ApplicationLauncherResource> launchers) {
		this.launchers = launchers;
	}

	public String getProtocolsDesc() {
		StringBuffer buf = new StringBuffer();
		for(NetworkProtocol protocol : protocols) {
			if(buf.length() > 0) {
				buf.append(",");
			}
			buf.append(protocol.getName());
		}
		return buf.toString();
	}

	public NetworkProtocol getNetworkProtocol(Integer port, NetworkTransport transport) {
		for (NetworkProtocol protocol : getProtocols()) {

			if (protocol.getTransport() == transport
					|| protocol.getTransport() == NetworkTransport.BOTH) {
				if (protocol.getEndPort() != null) {
					if (protocol.getStartPort().intValue() >= port.intValue()
							&& port.intValue() <= protocol.getEndPort()
									.intValue()) {
						return protocol;
					}
				} else {
					if (protocol.getStartPort().equals(port)) {
						return protocol;
					}
				}
			}
		}
		return null;
	}
	
}
