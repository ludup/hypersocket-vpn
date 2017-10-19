package com.hypersocket.launcher;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hypersocket.applications.ApplicationOS;
import com.hypersocket.applications.ApplicationResource;
import com.hypersocket.properties.ResourceUtils;
import com.hypersocket.resource.RealmResource;

@Entity
@Table(name="launchers")
@JsonDeserialize(using = ApplicationLauncherResourceDeserializer.class)
public class ApplicationLauncherResource extends RealmResource implements ApplicationResource {

	private static final long serialVersionUID = 1278949481466400246L;

	@Column(name="startup_script")
	@Lob
	String startupScript = "";
	
	@Column(name="shutdown_script")
	@Lob
	String shutdownScript = "";
	
	@Column(name="install_script")
	@Lob
	String installScript = "";
	
	@Column(name="files")
	@Lob
	String files;
	
	@Column(name="variables")
	@Lob
	String variables;	@Column(name="os")
	ApplicationOS os;

	@Column(name="exe", length=1024)
	String exe;
	
	@Column(name="args", length=1024)
	String args;
	
	@Column(name="logo", length=256)
	String logo;

	@Override
	public String getExe() {
		return exe;
	}

	@Override
	public String getLogo() {
		return logo == null ? "logo://100_autotype_autotype_auto.png" : logo;
	}

	@Override
	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Override
	public void setExe(String exe) {
		this.exe = exe;
	}

	@Override
	public String getArgs() {
		return args;
	}

	@Override
	public void setArgs(String args) {
		this.args = args;
	}

	@Override
	public ApplicationOS getOs() {
		return os;
	}

	@Override
	public void setOs(ApplicationOS os) {
		this.os = os;
	}
	
	@Override
	public String getOsFamily() {
		return os==null? "" : os.getFamily();
	}
	
	@Override
	public String getOsVersion() {
		return os==null? "" : os.getVersion();
	}
	
	public Map<String, String> getVariablesMap() {
		return ResourceUtils.explodeMap(variables);
	}
	
	public String getVariables() {
		return variables;
	}

	public void setVariablesMap(Map<String,String> variables) {
		this.variables = ResourceUtils.implodeMap(variables);
	}

	public void setVariables(String variables) {
		this.variables = variables;
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
