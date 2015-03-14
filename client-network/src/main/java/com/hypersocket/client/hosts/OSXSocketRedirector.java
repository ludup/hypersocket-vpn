package com.hypersocket.client.hosts;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.hypersocket.Version;
import com.hypersocket.client.util.BashSilentSudoCommand;
import com.hypersocket.utils.CommandExecutor;


public class OSXSocketRedirector extends AbstractSocketRedirector {

	File redirectCmd;
	File redirectNke;
	
	public OSXSocketRedirector() throws IOException {
		
		File cwd = new File(System.getProperty("user.dir"));
		if(StringUtils.isNotBlank(System.getProperty("hypersocket.bootstrap.distDir"))) {
			cwd = new File(System.getProperty("hypersocket.bootstrap.distDir"), "x-client-network");
		}
		
		Version v = new Version(System.getProperty("os.version"));
		Version yosemite = new Version("10.10");
		
		String kextName = "RedirectNKE.kext";
		String redirectName = "RedirectCMD";
		
		if(v.compareTo(yosemite) < 0) {
			kextName = "unsigned/RedirectNKE.kext";
			redirectName = "unsigned/RedirectCMD";
		}
		
		if(Boolean.getBoolean("hypersocket.development")) {

			redirectNke = new File("../x-client-network/bin/macosx/" + kextName);
			File tmpNke = File.createTempFile("nke", "tmp2");
			
			tmpNke = new File(tmpNke.getParentFile(), "RedirectNKE.kext");
			
			CommandExecutor del = new BashSilentSudoCommand(
					System.getProperty("sudo.password").toCharArray(), 
					"rm",
					"-rf",
					tmpNke.getAbsolutePath());
			
			del.execute();
			
			FileUtils.copyDirectory(redirectNke, tmpNke);
			tmpNke.deleteOnExit();
			
			CommandExecutor chmod = new BashSilentSudoCommand(
					System.getProperty("sudo.password").toCharArray(), 
					"chown",
					"-R",
					"root:wheel",
					tmpNke.getAbsolutePath());
			
			if(chmod.execute()!=0) {
				throw new IOException("Failed to chown temporary nke to root:wheel");
			}
			
			redirectNke = tmpNke;
			redirectCmd = new File("../x-client-network/bin/macosx/" + redirectName);		
		} else {
			
			redirectNke = new File(cwd, "bin/macosx/" + kextName);
			redirectCmd = new File(cwd, "bin/macosx/" + redirectName);	
			File systemNke = new File("/Library/Extensions/RedirectNKE.kext");
			
			if(!systemNke.exists() || systemNke.lastModified()!=redirectNke.lastModified()) {
				
				if(systemNke.exists()) {
					CommandExecutor del = new CommandExecutor(
							"rm",
							"-rf",
							systemNke.getAbsolutePath());
					
					del.execute();
				}
				
				FileUtils.copyDirectory(redirectNke, systemNke);
				
				systemNke.setLastModified(redirectNke.lastModified());
				
				CommandExecutor chmod = new CommandExecutor(
						"chown",
						"-R",
						"root:wheel",
						systemNke.getAbsolutePath());
				
				if(chmod.execute()!=0) {
					throw new IOException("Failed to chown system nke to root:wheel");
				}
				
				chmod = new CommandExecutor(
						"chmod",
						"-R",
						"755",
						systemNke.getAbsolutePath());
				
				if(chmod.execute()!=0) {
					throw new IOException("Failed to chmod system nke to 755");
				}
				
				
			}
			
			redirectNke = systemNke;
				
		}
		
		CommandExecutor cmd;

		cmd = new CommandExecutor(
				"chmod",
				"700",
				redirectCmd.getAbsolutePath());
		
		cmd.execute();
		
		if(Boolean.getBoolean("hypersocket.development")) {
			cmd = new BashSilentSudoCommand(System.getProperty("sudo.password").toCharArray(),
					"kextload", 
					redirectNke.getAbsolutePath());
		} else {
			cmd = new CommandExecutor("kextload",
				redirectNke.getAbsolutePath());
		}
		
		int ret;

		ret = cmd.execute();

		if(ret!=0) {
			throw new IOException("kextload returned " + ret);
		}
	}

	@Override
	protected String getCommand() {
		return redirectCmd.getAbsolutePath();
	}

	@Override
	protected String[] getStartArguments(String sourceAddr, int sourcePort,
			String destAddr, int destPort) {
		return new String[] { "add", sourceAddr, String.valueOf(sourcePort), destAddr, String.valueOf(destPort) };
	}

	@Override
	protected String[] getStopArguments(String sourceAddr, int sourcePort, String destAddr, int destPort) {
		return new String[] { "remove", sourceAddr, String.valueOf(sourcePort) , destAddr, String.valueOf(destPort)};
	}

	@Override
	protected String[] getLoggingArguments(boolean enabled) {
		return new String[] { "log", enabled ? "on" : "off" };
	}

}
