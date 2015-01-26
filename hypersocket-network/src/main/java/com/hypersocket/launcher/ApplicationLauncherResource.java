package com.hypersocket.launcher;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.hypersocket.resource.RealmResource;

@Entity
@Table(name="launchers")
public class ApplicationLauncherResource extends RealmResource {

	@Column(name="exe", length=1024)
	String exe;
	
	@Column(name="args", length=1024)
	String args;

	@Column(name="os")
	ApplicationLauncherOS os;
	
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

	public ApplicationLauncherOS getOs() {
		return os;
	}

	public void setOs(ApplicationLauncherOS os) {
		this.os = os;
	}
	
	public String getOsFamily() {
		return os.getFamily();
	}
	
	public String getOsVersion() {
		return os.getVersion();
	}
	
	
	
}
