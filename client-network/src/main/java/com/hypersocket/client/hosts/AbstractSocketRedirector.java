package com.hypersocket.client.hosts;

import java.io.IOException;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hypersocket.client.util.BashSilentSudoCommand;
import com.hypersocket.utils.CommandExecutor;

public abstract class AbstractSocketRedirector implements SocketRedirector {

	static Logger log = LoggerFactory.getLogger(AbstractSocketRedirector.class);
	
	static SocketRedirector systemRedirector;
	
	public AbstractSocketRedirector() {
	}

	@Override
	public void startRedirecting(String sourceAddr, int sourcePort,
			String destAddr, int destPort) throws IOException {
		
		CommandExecutor cmd;

		if(Boolean.getBoolean("hypersocket.development")) {
			cmd = new BashSilentSudoCommand(
					System.getProperty("sudo.password").toCharArray(), 
					getCommand(), getStartArguments(sourceAddr, sourcePort, destAddr, destPort));
		} else {
			cmd = new CommandExecutor(getCommand());
			cmd.addArgs(getStartArguments(sourceAddr, sourcePort, destAddr, destPort));
		}

		if(log.isInfoEnabled()) {
			log.info("Creating socket redirection from " + sourceAddr + ":" + sourcePort + 
					" to " + destAddr + ":" + destPort);
		}
		int ret;

		ret = cmd.execute();
		
		if(ret!=0) {
			throw new IOException("Socket redirector failed to start socket redirection. Command returned " + ret);
		}

	}

	@Override
	public void stopRedirecting(String sourceAddr, int sourcePort,String destAddr, int destPort)
			throws IOException {
		
		CommandExecutor cmd;

		if(Boolean.getBoolean("hypersocket.development")) {
			cmd = new BashSilentSudoCommand(
					System.getProperty("sudo.password").toCharArray(), 
					getCommand(),
					getStopArguments(sourceAddr, sourcePort, destAddr, destPort));
		} else {
			cmd = new CommandExecutor(getCommand());
			cmd.addArgs(getStopArguments(sourceAddr, sourcePort, destAddr, destPort));
		}
		
		if(log.isInfoEnabled()) {
			log.info("Removing socket redirection from " + sourceAddr + ":" + sourcePort + 
					" to " + destAddr + ":" + destPort);
		}
		
		int ret;

		ret = cmd.execute();
		
		if(ret!=0) {
			throw new IOException("Socket redirector failed to stop socket redirection. Command returned " + ret);
		}

	}
	
	public void enableLogging(boolean enabled) throws IOException {
		
		CommandExecutor cmd;

		cmd = new CommandExecutor(getCommand());
		cmd.addArgs(getLoggingArguments(enabled));

		int ret;

		ret = cmd.execute();
		
		if(ret!=0) {
			throw new IOException("Socket redirector failed to stop socket redirection. Command returned " + ret);
		}
	}
	
	protected abstract String[] getLoggingArguments(boolean enabled);

	protected abstract String getCommand();
	
	protected abstract String[] getStartArguments(String sourceAddr, int sourcePort, String destAddr, int destPort);

	protected abstract String[] getStopArguments(String sourceAddr, int sourcePort, String destAddr, int destPort);

	public static SocketRedirector getSystemRedirector() throws IOException {
		
		if(systemRedirector!=null) {
			return systemRedirector;
		}
		
		String osName = System.getProperty("os.name");
		if (SystemUtils.IS_OS_MAC_OSX) {
			systemRedirector = new OSXSocketRedirector();
		} else if (SystemUtils.IS_OS_LINUX) {
			systemRedirector = new LinuxSocketRedirector();
		} else if (SystemUtils.IS_OS_WINDOWS) {
			systemRedirector = new WindowsSocketRedirector();
		} else {
			throw new IOException("Unsupported operating system " + osName);
		}

		if (log.isInfoEnabled()) {
			log.info("Starting redirector for "
					+ System.getProperty("os.name"));
		}
		
		return systemRedirector;
	}
}
