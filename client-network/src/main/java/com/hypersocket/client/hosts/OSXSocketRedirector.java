package com.hypersocket.client.hosts;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.hypersocket.client.util.BashSilentSudoCommand;
import com.hypersocket.client.util.CommandExecutor;


public class OSXSocketRedirector extends AbstractSocketRedirector {

	File redirectCmd;
	File redirectNke;
	
	public OSXSocketRedirector() throws IOException {
		
		if(Boolean.getBoolean("hypersocket.development")) {

			redirectNke = new File("../client-install4j/bin/macosx/RedirectNKE.kext");
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
			redirectCmd = new File("../client-install4j/bin/macosx/RedirectCMD");		
		} else {
			
			redirectNke = new File("bin/macosx/RedirectNKE.kext");
			File tmpNke = File.createTempFile("nke", "tmp2");
			
			tmpNke = new File(tmpNke.getParentFile(), "RedirectNKE.kext");
			
			CommandExecutor del = new CommandExecutor(
					"rm",
					"-rf",
					tmpNke.getAbsolutePath());
			
			del.execute();
			
			FileUtils.copyDirectory(redirectNke, tmpNke);
			tmpNke.deleteOnExit();
			
			CommandExecutor chmod = new CommandExecutor(
					"chown",
					"-R",
					"root:wheel",
					tmpNke.getAbsolutePath());
			
			if(chmod.execute()!=0) {
				throw new IOException("Failed to chown temporary nke to root:wheel");
			}
			
			redirectNke = tmpNke;
			redirectCmd = new File("bin/macosx/RedirectCMD");		
		}
		
		
		CommandExecutor cmd;

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
