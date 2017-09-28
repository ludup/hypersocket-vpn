package com.hypersocket.launcher;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ApplicationLauncherOSSerializer.class)
public enum ApplicationLauncherOS {

	WINDOWS_VISTA(0, "6.0", "Windows Vista", "Windows"),
	WINDOWS_7(1, "6.1", "Windows 7", "Windows"),
	WINDOWS_8(2, "6.2", "Windows 8", "Windows"),
	WINDOWS_8_1(3, "6.3", "Windows 8.1", "Windows"),
	WINDOWS_10(10, "10.0", "Windows 10", "Windows"),
	MACOSX_SNOW_LEOPARD(4, "10.6", "Mac OS X Snow Leopard", "Mac OS X"),
	MACOSX_LION(5, "10.7", "Mac OS X Lion", "Mac OS X"),
	MACOSX_MOUNTAIN_LION(6, "10.8", "Mac OS X Mountain Lion", "Mac OS X"),
	MACOSX_MAVERICKS(7, "10.9", "Mac OS X Mavericks", "Mac OS X"),
	MACOSX_YOSEMITE(8, "10.10", "Mac OS X Yosemite", "Mac OS X"),
	MACOSX_ELCAPITAN(11, "10.11", "Mac OS X El Capitan", "Mac OS X"),
	MACOSX_SIERA(12, "10.11", "Mac OS X Siera", "Mac OS X"),
	LINUX(9, "", "Linux", "Linux"),
	CROSS_PLATFORM(13, "", "Any", "Cross platform");
	
	private final int val;
	private final String version;
	private final String name;
	private final String family;
	
	private ApplicationLauncherOS(final int val, final String version, final String name, final String family) {
		this.val = val;
		this.version = version;
		this.name = name;
		this.family = family;
	}
	
	public int getId() {
		return val;
	}

	public String getName() {
		return name();
	}
	
	public String getDisplayName() {
		return name;
	}
	
	public String getVersion(){ 
		return version;
	}
	
	public String getFamily() {
		return family;
	}

}
