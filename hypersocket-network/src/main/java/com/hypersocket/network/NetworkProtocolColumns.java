package com.hypersocket.network;

import com.hypersocket.tables.Column;

public enum NetworkProtocolColumns implements Column {

	NAME,
	TRANSPORT,
	PORT;
	
	public String getColumnName() {
		switch(this.ordinal()) {
		case 1:
			return "transport";
		case 2:
			return "startPort";
		default:
			return "name";
		}
	}
}