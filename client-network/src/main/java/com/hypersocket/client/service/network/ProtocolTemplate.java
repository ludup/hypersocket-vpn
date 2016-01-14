package com.hypersocket.client.service.network;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class ProtocolTemplate implements Serializable {

	private int startPort;
	private String transport;
	private String name;
	private Long id;
	private int endPort;
	private Calendar modifiedDate;

	public ProtocolTemplate(Long id, String name, String transport,
			int startPort, int endPort, Calendar modifiedDate) {
		this.id = id;
		this.name = name;
		this.transport = transport;
		this.startPort = startPort;
		this.endPort = endPort;
		this.modifiedDate = modifiedDate;
	}

	public Calendar getModifiedDate() {
		return modifiedDate;
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
