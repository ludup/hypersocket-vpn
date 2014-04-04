/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.network.json;

public class NetworkProtocolUpdate {

	Long id;
	String name;
	String transport;
	Integer startPort;
	Integer endPort;
	
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
	public String getTransport() {
		return transport;
	}
	public void setTransport(String transport) {
		this.transport = transport;
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
