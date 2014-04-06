package com.hypersocket.client;

import com.hypersocket.client.hosts.AbstractAliasCommand;

public class DummyAliasCommand extends AbstractAliasCommand {

	public DummyAliasCommand() {
	}

	@Override
	protected String[] getArguments(String alias, boolean create) {
		return new String[] { "dummy" };
	}
	
	protected String getCommand() {
		return "echo";
	}

}
