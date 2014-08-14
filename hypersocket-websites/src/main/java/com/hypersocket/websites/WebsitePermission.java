/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.websites;

import com.hypersocket.permissions.PermissionType;

public enum WebsitePermission implements PermissionType {
	
	CREATE("website.create"),
	READ("website.read"),
	UPDATE("website.update"),
	DELETE("website.delete");
	
	private final String val;
	
	private WebsitePermission(final String val) {
		this.val = val;
	}
	
	public String toString() {
		return val;
	}

	@Override
	public String getResourceKey() {
		return val;
	}
	
	@Override
	public boolean isSystem() {
		return false;
	}
}
