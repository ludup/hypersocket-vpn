package com.hypersocket.protocols;

import com.hypersocket.tables.Column;

public enum NetworkProtocolColumns implements Column {

	NAME;
	
	public String getColumnName() {
		switch(this.ordinal()) {
		default:
			return "name";
		}
	}
}