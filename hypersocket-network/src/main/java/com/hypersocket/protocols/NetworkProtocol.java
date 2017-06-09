package com.hypersocket.protocols;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hypersocket.migration.annotation.AllowNameOnlyLookUp;
import com.hypersocket.resource.RealmResource;
import com.hypersocket.server.forward.ForwardingTransport;

@Entity
@Table(name = "network_protocols")
@JsonDeserialize(using = NetworkProtocolDeserializer.class)
@AllowNameOnlyLookUp
public class NetworkProtocol extends RealmResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6615968421593775450L;

	@Column(name = "transport", nullable = false)
	ForwardingTransport transport;

	@Column(name = "start_port", nullable = false)
	Integer startPort;

	@Column(name = "end_port", nullable = true)
	Integer endPort;

	public ForwardingTransport getTransport() {
		return transport;
	}

	public void setTransport(ForwardingTransport transport) {
		this.transport = transport;
	}

	public String getPortRange() {
		if (startPort != endPort && endPort != null) {
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
