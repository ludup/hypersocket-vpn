package com.hypersocket.htmlapplications;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.hypersocket.applications.ApplicationOS;
import com.hypersocket.applications.ApplicationResource;
import com.hypersocket.properties.ResourceUtils;
import com.hypersocket.resource.AssignableResource;

@Entity
@Table(name = "hTMLApplication_resource")
public class HTMLApplicationResource extends AssignableResource implements ApplicationResource {

	private static final long serialVersionUID = 1278949481466400246L;

	@Column(name = "os")
	ApplicationOS os;

	@Column(name = "exe", length = 1024)
	String exe;

	@Column(name = "args", length = 1024)
	String args;

	@Column(name = "window_target")
	@Enumerated(EnumType.STRING)
	BrowserWindowTarget windowTarget = BrowserWindowTarget.WINDOW;

	@Column(name = "logo", length = 256)
	String logo;

	@Column(name = "variables")
	@Lob
	String variables;

	public Map<String, String> getVariablesMap() {
		return ResourceUtils.explodeMap(variables);
	}

	public String getVariables() {
		return variables;
	}

	public void setVariablesMap(Map<String, String> variables) {
		this.variables = ResourceUtils.implodeMap(variables);
	}

	public void setVariables(String variables) {
		this.variables = variables;
	}

	public BrowserWindowTarget getWindowTarget() {
		return windowTarget;
	}

	public void setWindowTarget(BrowserWindowTarget windowTarget) {
		this.windowTarget = windowTarget;
	}

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
		return os == null ? "" : os.getFamily();
	}

	@Override
	public String getOsVersion() {
		return os == null ? "" : os.getVersion();
	}

}
