package com.hypersocket.websites;

import com.hypersocket.tables.Column;

public enum WebsiteResourceColumns implements Column {

	NAME;
	
	public String getColumnName() {
		switch(this.ordinal()) {
		default:
			return "name";
		}
	}
}