package com.hypersocket.client.service.network;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProtocolTemplate implements Serializable {

	private int startPort;
	private String transport;
	private String name;
	private Long id;
	private int endPort;

	public ProtocolTemplate(Long id, String name, String transport,
			int startPort, int endPort) {
		this.id = id;
		this.name = name;
		this.transport = transport;
		this.startPort = startPort;
		this.endPort = endPort;
	}

	public int getStartPort() {
		return startPort;
	}

	public String getTransport() {
		return transport;
	}

	public String getName() {
		return name;
	}

	public Long getId() {
		return id;
	}

	public int getEndPort() {
		return endPort;
	}

}
