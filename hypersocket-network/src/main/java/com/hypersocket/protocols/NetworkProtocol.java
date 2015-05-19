package com.hypersocket.protocols;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hypersocket.network.NetworkTransport;
import com.hypersocket.resource.RealmResource;

@Entity
@Table(name="network_protocols")
@JsonIgnoreProperties({"createDate", "modifiedDate","portRange"})
public class NetworkProtocol extends RealmResource {

	@Column(name="transport", nullable=false)
	NetworkTransport transport;
	
	@Column(name="start_port", nullable=false)
	Integer startPort;
	
	@Column(name="end_port", nullable=true)
	Integer endPort;
	
	public NetworkTransport getTransport() {
		return transport;
	}

	public void setTransport(NetworkTransport transport) {
		this.transport = transport;
	}

	@XmlTransient
	public String getPortRange() {
		if(startPort != endPort && endPort != null) {
			return String.valueOf(startPort) + "-" + String.valueOf(endPort);
		} else {
			return String.valueOf(startPort);
		}
	}
	
	public Integer getStartPort() {
		return startPort;
	}

	public void setStartPort(Integer startPort) {
		this.startPort = startPort;
	}

	public Integer getEndPort() {
		return endPort;
	}

	public void setEndPort(Integer endPort) {
		this.endPort = endPort;
	}
}
