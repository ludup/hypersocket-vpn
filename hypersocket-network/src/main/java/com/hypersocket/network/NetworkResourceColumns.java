package com.hypersocket.network;

import com.hypersocket.tables.Column;

public enum NetworkResourceColumns implements Column {

	NAME;
	
	public String getColumnName() {
		switch(this.ordinal()) {
		default:
			return "name";
		}
	}
}