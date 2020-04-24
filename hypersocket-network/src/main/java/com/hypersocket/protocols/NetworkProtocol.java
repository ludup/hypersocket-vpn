package com.hypersocket.protocols;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hypersocket.realm.Realm;
import com.hypersocket.resource.RealmResource;
import com.hypersocket.server.forward.ForwardingTransport;

@Entity
@Table(name = "network_protocols")
@JsonDeserialize(using = NetworkProtocolDeserializer.class)
public class NetworkProtocol extends RealmResource {

	private static final long serialVersionUID = 6615968421593775450L;

	@Column(name = "transport", nullable = false)
	ForwardingTransport transport;

	@Column(name = "start_port", nullable = false)
	Integer startPort;

	@Column(name = "end_port", nullable = true)
	Integer endPort;
	
	@ManyToOne
	@JoinColumn(name = "realm_id", foreignKey = @ForeignKey(name = "network_protocols_cascade_1"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	protected Realm realm;

	@Override
	protected Realm doGetRealm() {
		return realm;
	}

	@Override
	public void setRealm(Realm realm) {
		this.realm = realm;
	}

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
