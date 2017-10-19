package com.hypersocket.applications;

public interface ApplicationResource {

	String getExe();

	String getLogo();

	void setLogo(String logo);

	void setExe(String exe);

	String getArgs();

	void setArgs(String args);

	ApplicationOS getOs();

	void setOs(ApplicationOS os);

	String getOsFamily();

	String getOsVersion();

}