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

	@Column(name="additional_urls", length=8000)
	String additionalUrls;
	
	public void setLaunchUrl(String launchUrl) {
		this.launchUrl = launchUrl;
	}
	
	public String getLaunchUrl() {
		return launchUrl;
	}

	public String getAdditionalUrls() {
		return additionalUrls;
	}

	public void setAdditionalUrls(String additionalUrls) {
		this.additionalUrls = additionalUrls;
	}
	
	
}
