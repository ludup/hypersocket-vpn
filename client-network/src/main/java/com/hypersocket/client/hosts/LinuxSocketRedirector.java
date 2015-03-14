package com.hypersocket.client.hosts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hypersocket.client.util.BashSilentSudoCommand;
import com.hypersocket.utils.CommandExecutor;

/**
 * Requires the script 'src/main/scripts/hs-socket-redirector' to be installed
 * on the PATH
 *
 */
public class LinuxSocketRedirector extends AbstractSocketRedirector implements
		SocketRedirector {

	private static File tempScript;

	static {
		// Install the script

		try {
			tempScript = File.createTempFile("lsr", ".sh");
			tempScript.deleteOnExit();
			tempScript.setExecutable(true, true);
			InputStream in = LinuxSocketRedirector.class
					.getResourceAsStream("/hs-socket-redirector");
			try {
				OutputStream out = new FileOutputStream(tempScript);
				try {
					IOUtils.copy(in, out);
				} finally {
					out.close();
				}
			} finally {
				in.close();
			}
		} catch (Exception ioe) {
			throw new RuntimeException(
					"Failed to create temporary script file.", ioe);
		}

		// On startup and shutdown, ensure all Hypersocket Client rules are
		// cleared out
		clearAllRules();
		Runtime.getRuntime().addShutdownHook(new Thread("ClearHSDNATRules") {
			@Override
			public void run() {
				clearAllRules();
			}
		});
	}

	private static void clearAllRules() {
		try {
			CommandExecutor cmd;
			if (Boolean.getBoolean("hypersocket.development")) {
				cmd = new BashSilentSudoCommand(System.getProperty(
						"sudo.password").toCharArray(),
						tempScript.getAbsolutePath(), "--clear");
			} else {
				cmd = new CommandExecutor(tempScript.getAbsolutePath(),
						"--clear");
			}
			int ret = cmd.execute();

			if (ret != 0) {
				throw new IOException("Command returned " + ret);
			}
		} catch (Exception e) {
			LOG.warn("Failed to clear existing Hypersocket client DNAT rules.",
					e);
		}
	}

	final static Logger LOG = LoggerFactory
			.getLogger(LinuxSocketRedirector.class);

	@Override
	protected String[] getLoggingArguments(boolean enabled) {
		return new String[] {};
	}

	@Override
	protected String getCommand() {
		return tempScript.getAbsolutePath();
	}

	@Override
	protected String[] getStartArguments(String sourceAddr, int sourcePort,
			String destAddr, int destPort) {
		return new String[] { "--add", sourceAddr, String.valueOf(sourcePort),
				destAddr, String.valueOf(destPort) };
	}

	@Override
	protected String[] getStopArguments(String sourceAddr, int sourcePort,
			String destAddr, int destPort) {
		return new String[] { sourceAddr, String.valueOf(sourcePort), destAddr,
				String.valueOf(destPort) };
	}

}
