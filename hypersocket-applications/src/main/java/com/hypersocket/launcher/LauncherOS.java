package com.hypersocket.launcher;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using = LauncherOSSerializer.class)
public enum LauncherOS {

	WINDOWS_VISTA(0, "6.0", "Windows Vista"),
	WINDOWS_7(1, "6.1", "Windows 7"),
	WINDOWS_8(2, "6.2", "Windows 8"),
	WINDOWS_8_1(3, "6.3", "Windows 8.1"),
	MACOSX_SNOW_LEOPARD(4, "10.6", "Mac OS X Snow Leopard"),
	MACOSX_LION(5, "10.7", "Mac OS X Lion"),
	MACOSX_MOUNTAIN_LION(6, "10.8", "Mac OS X Mountain Lion"),
	MACOSX_MAVERICKS(7, "10.9", "Mac OS X Mavericks"),
	MACOSX_YOSEMITE(8, "10.10", "Mac OS X Yosemite");
	
	private final int val;
	private final String version;
	private final String name;
	
	private LauncherOS(final int val, final String version, final String name) {
		this.val = val;
		this.version = version;
		this.name = name;
	}
	
	public int getId() {
		return val;
	}
	
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	public String getVersion(){ 
		return version;
	}
}
