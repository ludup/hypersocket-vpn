package com.hypersocket.websites;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.hypersocket.resource.AssignableResource;

@Entity
@Table(name="websites")
public class WebsiteResource extends AssignableResource {

	@Column(name="launch_url", length=1024)
	String launchUrl;

	public void setLaunchUrl(String launchUrl) {
		this.launchUrl = launchUrl;
	}
	
	public String getLaunchUrl() {
		return launchUrl;
	}
	
}
