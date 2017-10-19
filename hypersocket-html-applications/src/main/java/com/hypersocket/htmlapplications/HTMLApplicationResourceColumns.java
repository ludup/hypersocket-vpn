package com.hypersocket.htmlapplications;

import com.hypersocket.tables.Column;

public enum HTMLApplicationResourceColumns implements Column {

	NAME;

	public String getColumnName() {
		switch(this.ordinal()) {
		default:
			return "name";
		}
	}
}