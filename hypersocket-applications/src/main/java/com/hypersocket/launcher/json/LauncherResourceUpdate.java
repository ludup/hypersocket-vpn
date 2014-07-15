package com.hypersocket.launcher.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class LauncherResourceUpdate {

	Long id;
	String name;
	int os;
	String exe;
	String args;
	
	public LauncherResourceUpdate() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExe() {
		return exe;
	}

	public void setExe(String exe) {
		this.exe = exe;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	public int getOs() {
		return os;
	}

	public void setOs(int os) {
		this.os = os;
	}
	
	
}
