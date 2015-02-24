package com.hypersocket.websites;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hypersocket.browser.BrowserLaunchable;
import com.hypersocket.network.handlers.ForwardingResource;

@Entity
@Table(name="websites")
public class WebsiteResource extends ForwardingResource {

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

	@JsonIgnore
	public List<URL> getUrls() {
		List<URL> urls = new ArrayList<URL>();
		urls.add(createURL(launchUrl));
		if(StringUtils.isNotEmpty(additionalUrls)) {
			for(String url : additionalUrls.split("\\]\\|\\[")){ 
				urls.add(createURL(url));
			}
		}
		return urls;
	}
	
	private URL createURL(String url) {
		try {
			if(!url.startsWith("http")) {
				url = "http://" + url;
			}
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}
	@Override
	public String getDestinationHostname() {
		return createURL(launchUrl).getHost();
	}

	@Override
	public String getHostname() {
		return getDestinationHostname();
	}
	
	
}
