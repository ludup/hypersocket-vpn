/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.client.hosts;


public class OSXAliasCommand extends AbstractAliasCommand {

	@Override
	protected String[] getArguments(String alias, boolean create) {
		if(create) {
			return new String[] { "lo0", "alias", alias};
		} else {
			return new String[] { "lo0", "-alias", alias};
		}
	}

	

}
