package com.hypersocket.client.hosts;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.hypersocket.utils.CommandExecutor;

public class WindowsSocketRedirector extends AbstractSocketRedirector implements
		SocketRedirector {

	File installCommand;
	File redirectCommand;
	File driverFile;
	
	public WindowsSocketRedirector() {
		
		String windowsDir = System.getenv("SystemRoot");
		String arch = System.getProperty("os.arch");
		File cwd = new File(System.getProperty("user.dir"));
		if(StringUtils.isNotBlank(System.getProperty("hypersocket.bootstrap.distDir"))) {
			cwd = new File(System.getProperty("hypersocket.bootstrap.distDir"), "x-client-network");
		}
		File currentDriverFile = new File(cwd, "bin"
				+ File.separator
				+ "windows"
				+ File.separator
				+ arch
				+ File.separator
				+ "ip_redirect_driver.sys");
		
		installCommand = new File(cwd, "bin"
				+ File.separator
				+ "windows"
				+ File.separator
				+ arch
				+ File.separator
				+ "installer.exe");
		
		redirectCommand = new File(cwd, "bin"
				+ File.separator
				+ "windows"
				+ File.separator
				+ arch
				+ File.separator
				+ "redirect.exe");
		
		driverFile = new File(windowsDir, "System32"
				+ File.separator
				+ "drivers"
				+ File.separator
				+ "ip_redirect_driver.sys");
		
		if(!driverFile.exists() || currentDriverFile.lastModified()!=driverFile.lastModified()) {
			try {
				FileUtils.copyFile(currentDriverFile, driverFile);
				driverFile.setLastModified(currentDriverFile.lastModified());
		
				CommandExecutor cmd = new CommandExecutor(installCommand.getAbsolutePath());
				
				cmd.addArg("ip_redirect_driver");
				cmd.addArg(driverFile.getAbsolutePath());
				
				int exit = cmd.execute();
				
				if(exit!=0) {
					if(exit!=1073) {
						throw new IllegalStateException("Could not install redirect driver exitCode=" + exit);
					} else {
						log.warn("Ignoring 1073 exit code as this indicates driver is already installed");
					}
				}
			} catch (IOException e) {
				throw new IllegalStateException("Could not update redirect driver", e);
			}
		}
		
		CommandExecutor startCommand = new CommandExecutor(installCommand.getAbsolutePath());
		startCommand.addArg("ip_redirect_driver");
		startCommand.addArg("start");
		
		int exit;
		try {
			exit = startCommand.execute();
			if(exit!=0) {
				if(exit!=1056) {
					throw new IllegalStateException("Could not install redirect driver exitCode=" + exit);
				} else {
					log.warn("Ignoring exit code 1056 as this indicates the redirect service is already running");
				}
			}	
		} catch (IOException e) {
			throw new IllegalStateException("Could not execute driver start command", e);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				
				CommandExecutor stopCommand = new CommandExecutor(installCommand.getAbsolutePath());
				stopCommand.addArg("ip_redirect_driver");
				stopCommand.addArg("stop");
				
				int exit;
				try {
					exit = stopCommand.execute();
					if(exit!=0 && exit!=1063) {
						log.error("Could not stop redirect driver exitCode=" + exit);
					}	
				} catch (IOException e) {
					log.error("Could not execute driver stop command", e);
				}
			}
		});
		
	}
	
	@Override
	protected String[] getLoggingArguments(boolean enabled) {
		return new String[] { };
	}

	@Override
	protected String getCommand() {
		return redirectCommand.getAbsolutePath();
	}

	@Override
	protected String[] getStartArguments(String sourceAddr, int sourcePort,
			String destAddr, int destPort) {
		return new String[] { "add", sourceAddr, String.valueOf(sourcePort), destAddr, String.valueOf(destPort) };
	}

	@Override
	protected String[] getStopArguments(String sourceAddr, int sourcePort, String destAddr, int destPort) {
		return new String[] { "remove", sourceAddr, String.valueOf(sourcePort)};
	}

	public static void main(String[] args) {
		new WindowsSocketRedirector();
	}
}
