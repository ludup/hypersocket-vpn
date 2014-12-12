package com.hypersocket.protocols.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
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
