package com.hypersocket.client.hosts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Requires the script 'src/main/scripts/hs-socket-redirector' to be installed
 * on the PATH
 *
 */
public class LinuxSocketRedirector extends AbstractSocketRedirector implements
		SocketRedirector {
	
	final static Logger LOG = LoggerFactory.getLogger(LinuxSocketRedirector.class);
	
	private File tempScript;

	@Override
	protected String[] getLoggingArguments(boolean enabled) {
		return new String[] { };
	}

	@Override
	protected String getCommand() {
		if (tempScript == null) {
			try {
				tempScript = File.createTempFile("lsr", ".sh");
				tempScript.deleteOnExit();
				tempScript.setExecutable(true, true);
				InputStream in = getClass().getResourceAsStream("/hs-socket-redirector");
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
				throw new RuntimeException("Failed to create temporary script file.", ioe);
			}
		}
		return tempScript.getAbsolutePath();
	}

	@Override
	protected String[] getStartArguments(String sourceAddr, int sourcePort,
			String destAddr, int destPort) {
		return new String[] { "--add", sourceAddr, String.valueOf(sourcePort), destAddr, String.valueOf(destPort) };
	}

	@Override
	protected String[] getStopArguments(String sourceAddr, int sourcePort, String destAddr, int destPort) {
		return new String[] { sourceAddr, String.valueOf(sourcePort), destAddr, String.valueOf(destPort) };
	}

}
