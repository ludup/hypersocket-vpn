package com.hypersocket.htmlapplications;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.hypersocket.properties.ResourceUtils;
import com.hypersocket.resource.AssignableResource;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name = "hTMLApplication_resource")
public abstract class HTMLApplicationResource extends AssignableResource {

	private static final long serialVersionUID = 1278949481466400246L;

	@Column(name = "window_target")
	@Enumerated(EnumType.STRING)
	BrowserWindowTarget windowTarget = BrowserWindowTarget.WINDOW;

	@Column(name = "logo", length = 256)
	String logo;

	public Map<String, String> getVariablesMap() {
		return ResourceUtils.explodeMap(getVariables());
	}
	
	public abstract String getExe();

	public abstract String getVariables();

	public BrowserWindowTarget getWindowTarget() {
		return windowTarget;
	}

	public void setWindowTarget(BrowserWindowTarget windowTarget) {
		this.windowTarget = windowTarget;
	}

	public String getLogo() {
		return logo == null ? "logo://100_autotype_autotype_auto.png" : logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}


}
