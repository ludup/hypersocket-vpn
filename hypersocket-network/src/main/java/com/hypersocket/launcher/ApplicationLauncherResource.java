package com.hypersocket.launcher;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hypersocket.resource.RealmResource;

@Entity
@Table(name="launchers")
@JsonDeserialize(using = ApplicationLauncherResourceDeserializer.class)
public class ApplicationLauncherResource extends RealmResource {

	private static final long serialVersionUID = 1278949481466400246L;

	@Column(name="exe", length=1024)
	String exe;
	
	@Column(name="args", length=1024)
	String args;

	@Column(name="os")
	ApplicationLauncherOS os;
	
	@Column(name="startup_script")
	@Lob
	String startupScript = "";
	
	@Column(name="shutdown_script")
	@Lob
	String shutdownScript = "";
	
	@Column(name="install_script")
	@Lob
	String installScript = "";
	
	@Column(name="logo", length=256)
	String logo;
	
	@Column(name="files")
	@Lob
	String files;
	
	public String getExe() {
		return exe;
	}

	public String getLogo() {
		return logo == null ? "logo://100_autotype_autotype_auto.png" : logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
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
		return os==null? "" : os.getFamily();
	}
	
	public String getOsVersion() {
		return os==null? "" : os.getVersion();
	}

	public String getStartupScript() {
		return startupScript == null ? "" : startupScript;
	}

	public void setStartupScript(String startupScript) {
		this.startupScript = startupScript;
	}

	public String getInstallScript() {
		return installScript == null ? "" : installScript;
	}

	public void setInstallScript(String installScript) {
		this.installScript = installScript;
	}
	
	public String getShutdownScript() {
		return shutdownScript == null ? "" : shutdownScript;
	}

	public void setShutdownScript(String shutdownScript) {
		this.shutdownScript = shutdownScript;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}
}
