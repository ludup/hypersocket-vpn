package com.hypersocket.websites.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WebsiteResourceUpdate {

	Long id;
	String name;
	String launchUrl;
	String additionalUrls;
	Long[] roles;
	
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
	
	
	
}
