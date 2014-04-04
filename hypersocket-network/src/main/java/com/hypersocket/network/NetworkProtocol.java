/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hypersocket.repository.AbstractEntity;

@Entity
@Table(name="network_protocols")
public class NetworkProtocol extends AbstractEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name="id", nullable=false)
	Long id;
	
	@Column(name="name", nullable=false, unique=true)
	String name;
	
	@Column(name="transport", nullable=false)
	NetworkTransport transport;
	
	@Column(name="start_port", nullable=false)
	Integer startPort;
	
	@Column(name="end_port", nullable=true)
	Integer endPort;

	public NetworkProtocol() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public NetworkTransport getTransport() {
		return transport;
	}

	public void setTransport(NetworkTransport transport) {
		this.transport = transport;
	}

	@Override
	public Long getId() {
		return id;
	}
	
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
