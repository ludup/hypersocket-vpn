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
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.hypersocket.resource.AssignableResource;

@Entity
@Table(name="network_resources", uniqueConstraints = {@UniqueConstraint(columnNames={"name"})})
public class NetworkResource extends AssignableResource {

	@Column(name="hostname")
	String hostname;
	
	@Column(name="destination", nullable=true)
	String destinationHostname;
	
	@ManyToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name="network_resource_protocols", 
		joinColumns={@JoinColumn(name="resource_id")}, 
		inverseJoinColumns={@JoinColumn(name="protocol_id")})
	Set<NetworkProtocol> protocols = new HashSet<NetworkProtocol>();

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
	
}
