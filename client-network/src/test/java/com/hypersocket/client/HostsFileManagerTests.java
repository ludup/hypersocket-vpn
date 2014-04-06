/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
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

import junit.framework.Assert;

import org.junit.Test;

import com.hypersocket.client.hosts.HostsFileManager;

public class HostsFileManagerTests {

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
			
			
			Assert.assertEquals(true, manager.getAliasPool().contains("127.0.0.255"));
			Assert.assertEquals(254, manager.getAliasPool().size());
			
			int expectedSize = 254;
			for(int i=0;i<=255;i++) {
				for(int x=0;x<=255;x++) {
					Assert.assertEquals(expectedSize, manager.getAliasPool().size());
					for(int y=(i==0 && x==0?2:1);y<=255;y++) {
						System.out.println("Checking IP exists: 127." + i + "." + x + "." + y);
						Assert.assertEquals(true, manager.getAliasPool().contains("127." + i + "." + x + "." + y));
					}
					manager.generatePool();
					expectedSize += 255;
				}
			}

			manager.cleanup();

		} finally {
			tempHostsFile.delete();
		}
	}

	public void addAliases(HostsFileManager manager) throws IOException {

		String alias1 = manager.getAlias("example2.local");
		Assert.assertEquals("Expected 127.0.0.2", "127.0.0.2", alias1);

		String alias2 = manager.getAlias("example3.local");
		Assert.assertEquals("Expected 127.0.0.3", "127.0.0.3", alias2);

		// 127.0.0.4 is skipped because its predefined in the hosts file
		String alias3 = manager.getAlias("example5.local");
		Assert.assertEquals("Expected 127.0.0.5", "127.0.0.5", alias3);

	}

	public void getAliases(HostsFileManager manager) throws IOException {

		String alias1 = manager.getAlias("example2.local");
		Assert.assertEquals("Expected 127.0.0.2", "127.0.0.2", alias1);

		String alias2 = manager.getAlias("example3.local");
		Assert.assertEquals("Expected 127.0.0.3", "127.0.0.3", alias2);

		// 127.0.0.4 is skipped because its predefined in the hosts file
		String alias3 = manager.getAlias("example5.local");
		Assert.assertEquals("Expected 127.0.0.5", "127.0.0.5", alias3);
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
