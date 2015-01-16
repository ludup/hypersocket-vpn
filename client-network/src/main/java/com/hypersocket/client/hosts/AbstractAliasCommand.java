/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.client.hosts;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hypersocket.client.util.BashSilentSudoCommand;
import com.hypersocket.utils.CommandExecutor;

public abstract class AbstractAliasCommand implements AliasCommand {

	static Logger log = LoggerFactory.getLogger(OSXAliasCommand.class);

	public AbstractAliasCommand() {

	}
	@Override
	public boolean createAlias(String alias) throws IOException {
		CommandExecutor cmd;

		if(Boolean.getBoolean("hypersocket.development")) {
			cmd = new BashSilentSudoCommand(System.getProperty("sudo.password").toCharArray(),
					getCommand(), getArguments(alias, true));
		} else {
			cmd = new CommandExecutor(getCommand());
			cmd.addArgs(getArguments(alias, true));
		}
		
		if(log.isInfoEnabled()) {
			log.info("Creating alias " + alias);
		}

		int ret;

		ret = cmd.execute();

		return ret == 0;

	}

	@Override
	public boolean deleteAlias(String alias) throws IOException {
		CommandExecutor cmd;

		if(Boolean.getBoolean("hypersocket.development")) {
			cmd = new BashSilentSudoCommand(System.getProperty("sudo.password").toCharArray(),
					getCommand(), getArguments(alias, false));
		} else {
			cmd = new CommandExecutor(getCommand());
			cmd.addArgs(getArguments(alias, false));
		}
		

		if(log.isInfoEnabled()) {
			log.info("Deleting alias " + alias);
		}
		
		int ret;

		ret = cmd.execute();

		return ret == 0;
	}
	
	protected String getCommand() {
		return "ifconfig";
	}
	
	protected abstract String[] getArguments(String alias, boolean create);

}
