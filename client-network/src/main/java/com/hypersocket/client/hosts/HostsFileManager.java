/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.client.hosts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jboss.netty.handler.ipfilter.CIDR;
import org.jboss.netty.handler.ipfilter.CIDR4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hypersocket.client.util.BashSilentSudoCommand;
import com.hypersocket.utils.CommandExecutor;

public class HostsFileManager {

	static Logger log = LoggerFactory.getLogger(HostsFileManager.class);

	File hostsFile;
	List<String> content = new ArrayList<String>();
	LinkedList<String> aliasPool = new LinkedList<String>();
	Map<String, String> hostsToLoopbackAlias = new HashMap<String, String>();
	
	int _8bits = 192;
	int _16bits = 168;
	int _24bits = 10;
	
	static HostsFileManager systemManager;
	
	static final String BEGIN = "#----HYPERSOCKET BEGIN----";

	public HostsFileManager(File hostsFile, AliasCommand aliasCommand)
			throws IOException {
		this.hostsFile = hostsFile;
		
		selectNextRange();
		generatePool();
		loadFile(true);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					cleanup();
				} catch (IOException e) {
				}
			}
		});
	}

	private void selectNextRange() throws IOException {
		while(!checkRange(_8bits, _16bits, _24bits) && _24bits < 255) {
			_24bits++;
		}
		
		if(_24bits==255 && !checkRange(_8bits, _16bits, _24bits)) {
			_8bits = 10;
			_16bits = 240;
			_24bits = 10;
			
			while(!checkRange(_8bits, _16bits, _24bits) && _24bits < 255) {
				_24bits++;
			}
			
			if(_24bits==255 && !checkRange(_8bits, _16bits, _24bits)) {
				throw new IOException("Unable to allocate IP range");
			}
		}
		
		if(log.isInfoEnabled()) {
			log.info("Selected IP range " + _8bits + "." + _16bits + "." + _24bits);
		}
	}
	
	private boolean checkRange(int _8bits, int _16bits, int _24bits) throws SocketException, UnknownHostException {
		
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		while(e.hasMoreElements()) {
			NetworkInterface net = e.nextElement();
			for(InterfaceAddress i : net.getInterfaceAddresses()) {
				String range = _8bits + "." + _16bits + "." + _24bits;
				if(log.isInfoEnabled()) {
					log.info("Checking interface " + i.toString());
				}
				if(i.getNetworkPrefixLength() > 0 && i.getNetworkPrefixLength() <= 31) {
					
					CIDR c = CIDR4.newCIDR(range + ".0" + "/" + i.getNetworkPrefixLength());
					
					if(c.contains(i.getAddress())) {
						if(log.isInfoEnabled()) {
							log.warn(i.getAddress() + " appears to be in our chosen range " + range + ".0" + "/" + i.getNetworkPrefixLength());
						}
						return false;
					}
				}
			}
		}
		return true;
	}
	public List<String> getAliasPool() {
		return aliasPool;
	}
	
	public static HostsFileManager getSystemHostsFile() throws IOException {

		synchronized(HostsFileManager.class) {
			
			if(systemManager!=null) {
				return systemManager;
			}
			File hostsFile = null;
	
			String osName = System.getProperty("os.name");
			AliasCommand aliasCommand = null;
	
			if (osName.startsWith("Mac")) {
				hostsFile = new File("/private/etc/hosts");
				aliasCommand = new OSXAliasCommand();
			} else if (osName.startsWith("Linux")) {
				hostsFile = new File("/etc/hosts");
				aliasCommand = new LinuxAliasCommand();
			} else if (osName.startsWith("Windows")) {
				hostsFile = new File(System.getenv("SystemRoot"), "System32"
						+ File.separator + "drivers" + File.separator + "etc"
						+ File.separator + "hosts");
			} else {
				throw new IOException("Unsupported operating system " + osName);
			}
	
			if (log.isInfoEnabled()) {
				log.info("Starting hosts file manager for "
						+ System.getProperty("os.name"));
			}
	
			systemManager = new HostsFileManager(hostsFile, aliasCommand);
			return systemManager;
		}
	}

	public void generatePool() throws IOException {

		
		selectNextRange();
		
		for (int i=1; i <= 254; i++) {
			System.out.println("Generating " + _8bits + "." + _16bits + "." + _24bits + "." + i);
			aliasPool.addLast(_8bits + "." + _16bits + "." + _24bits + "." + i);
		}
		_24bits++;
	}

	public void cleanup() throws IOException {
		flushFile(false);
	}

	private void loadFile(boolean processHypersocketEntries) throws IOException {
		// Load and remove any aliases that might be left in the file
		FileInputStream in = new FileInputStream(hostsFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		content.clear();
		
		try {
			String str;
			while ((str = reader.readLine()) != null && !str.equals(BEGIN)) {
				content.add(str);
			}

			if(processHypersocketEntries) {
				processContentForAliases();
			}

		} finally {
			try {
				reader.close();
			} catch(IOException ex){ }
			
			try {
				in.close();
			} catch(IOException ex){ }
		}
	}

	private void processContentForAliases() {

		for (String str : content) {
			if (!str.trim().startsWith("#")) {
				StringTokenizer t = new StringTokenizer(str, " ");
				if (t.hasMoreTokens()) {
					String address = t.nextToken();
					if (aliasPool.contains(address)) {
						// The localhost alias is taken already
						aliasPool.remove(address);
					}
				}
			}
		}
	}

	private synchronized void flushFile(boolean outputAliases) throws IOException {

		if (!outputAliases) {
			for (String alias : hostsToLoopbackAlias.values()) {
				aliasPool.addLast(alias);
			}
			hostsToLoopbackAlias.clear();
		}

		if (Boolean.getBoolean("hypersocket.development")) {
			File tmp = File.createTempFile("hypersocket", ".tmp");
			writeFile(tmp, outputAliases);

			CommandExecutor cmd = new BashSilentSudoCommand(System.getProperty(
					"sudo.password").toCharArray(), "mv", "-f",
					tmp.getAbsolutePath(), hostsFile.getAbsolutePath());

			if (cmd.execute() != 0) {
				throw new IOException("Could not flush localhost alias to "
						+ hostsFile.getAbsolutePath());
			}
		} else {
			writeFile(hostsFile, outputAliases);
		}
	}

	private void writeFile(File file, boolean outputAliases) throws IOException {
		
		// Load the latest content first in case user added entries
		loadFile(false);
		
		FileOutputStream out = new FileOutputStream(file);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		try {
			for (String host : content) {
				writer.write(host);
				writer.write(System.getProperty("line.separator"));
			}

			if (outputAliases) {
				writer.write(System.getProperty("line.separator"));
				writer.write(BEGIN);
				writer.write(System.getProperty("line.separator"));
				writer.write("# WARNING: Any hosts added beyond this line will be removed when the Hypersocket client disconnects.");
				writer.write(System.getProperty("line.separator"));

				for (Map.Entry<String, String> host : hostsToLoopbackAlias
						.entrySet()) {
					writer.write(host.getValue() + " " + host.getKey());
					writer.write(System.getProperty("line.separator"));
				}
			}
			writer.flush();
		} finally {
			writer.close();
		}
	}

	public synchronized String getAlias(String hostname) throws IOException {
		if (hostsToLoopbackAlias.containsKey(hostname)) {
			return hostsToLoopbackAlias.get(hostname);
		}

		return addAlias(hostname);
	}

	public synchronized boolean hasAlias(String hostname) {
		return hostsToLoopbackAlias.containsKey(hostname);
	}

	private synchronized String addAlias(String hostname) throws IOException {
		if (!aliasPool.iterator().hasNext()) {
			generatePool();
		}
		hostsToLoopbackAlias.put(hostname, aliasPool.removeFirst());
		String alias = getAlias(hostname);

		flushFile(true);
		return alias;
	}

	public void removeHostname(String hostname) throws IOException {
		
		hostsToLoopbackAlias.remove(hostname);
		
		flushFile(true);
		
	}
}
