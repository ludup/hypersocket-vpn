/*******************************************************************************
 * Copyright (c) 2013 LogonBox Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.hypersocket.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.junit.Assert;

import org.apache.log4j.BasicConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hypersocket.client.hosts.HostsFileManager;

public class HostsFileManagerTests {

	@BeforeClass
	public static void startup() {
		BasicConfigurator.configure();
	}
	
	@Test
	public void test() throws FileNotFoundException, IOException {

		File tempHostsFile = File.createTempFile("hosts", ".tmp");
		URL resource = HostsFileManagerTests.class.getResource("/hosts");

		copyStream(resource.openStream(), new FileOutputStream(
				tempHostsFile));

		try {
			HostsFileManager manager = new HostsFileManager(tempHostsFile, new DummyAliasCommand());
			
			addAliases(manager);

			getAliases(manager);
			
			manager.cleanup();

		} finally {
			tempHostsFile.delete();
		}
	}
	
	@Test
	public void checkPool() throws FileNotFoundException, IOException {
		
		File tempHostsFile = File.createTempFile("hosts", ".tmp");

		try {
			HostsFileManager manager = new HostsFileManager(tempHostsFile, new DummyAliasCommand());
			
			
			Assert.assertEquals(true, manager.getAliasPool().contains("192.168.10.254"));
			Assert.assertEquals(254, manager.getAliasPool().size());
			
			manager.cleanup();

		} finally {
			tempHostsFile.delete();
		}
	}

	public void addAliases(HostsFileManager manager) throws IOException {

		String alias1 = manager.getAlias("example1.local");
		Assert.assertEquals("Expected 192.168.10.1", "192.168.10.1", alias1);

		String alias2 = manager.getAlias("example2.local");
		Assert.assertEquals("Expected 192.168.10.2", "192.168.10.2", alias2);

		String alias3 = manager.getAlias("example3.local");
		Assert.assertEquals("Expected 192.168.10.3", "192.168.10.3", alias3);
		
		// 192.168.10.4 is skipped because its predefined in the hosts file
		String alias5 = manager.getAlias("example5.local");
		Assert.assertEquals("Expected 192.168.10.5", "192.168.10.5", alias5);

	}

	public void getAliases(HostsFileManager manager) throws IOException {

		String alias1 = manager.getAlias("example2.local");
		Assert.assertEquals("Expected 192.168.10.2", "192.168.10.2", alias1);

		String alias2 = manager.getAlias("example3.local");
		Assert.assertEquals("Expected 192.168.10.3", "192.168.10.3", alias2);

		// 192.168.10.4 is skipped because its predefined in the hosts file
		String alias3 = manager.getAlias("example5.local");
		Assert.assertEquals("Expected 192.168.10.5", "192.168.10.5", alias3);
	}
	
	
	private void copyStream(InputStream in, OutputStream out) throws IOException {
		
		try {
			
			int r;
			byte[] tmp = new byte[4096];
			while((r = in.read(tmp)) > -1) {
				out.write(tmp, 0, r);
			}
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
			try {
				out.close();
			} catch (Exception e) {
			}
			
		}
	}

}
