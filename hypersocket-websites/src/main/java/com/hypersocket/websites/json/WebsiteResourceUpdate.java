package com.hypersocket.websites.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WebsiteResourceUpdate {

	Long id;
	String name;
	String launchUrl;
	String additionalUrls;
	Long[] roles;
	String logo;
	
	public WebsiteResourceUpdate() {
		
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

	public Long[] getRoles() {
		return roles;
	}

	public void setRoles(Long[] roles) {
		this.roles = roles;
	}

	public String getLaunchUrl() {
		return launchUrl;
	}
	
	public void setLaunchUrl(String launchUrl) {
		this.launchUrl = launchUrl;
	}

	public String getAdditionalUrls() {
		return additionalUrls;
	}

	public void setAdditionalUrls(String additionalUrls) {
		this.additionalUrls = additionalUrls;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
}
