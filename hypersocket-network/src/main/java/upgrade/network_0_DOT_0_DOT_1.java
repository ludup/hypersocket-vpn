/*******************************************************************************
 * Copyright (c) 2013 Hypersocket Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package upgrade;

import org.springframework.beans.factory.annotation.Autowired;

import com.hypersocket.network.NetworkProtocol;
import com.hypersocket.network.NetworkResourceRepository;
import com.hypersocket.network.NetworkTransport;

public class network_0_DOT_0_DOT_1 implements Runnable {

	@Autowired
	NetworkResourceRepository networkRepository;
	
	@Override
	public void run() {

//		createProtocol("FTP (Data)", NetworkTransport.BOTH, 20, null);
//		createProtocol("FTP (Control)", NetworkTransport.TCP, 21, null);
//		createProtocol("FTPS (Data)", NetworkTransport.BOTH, 989, null);
//		createProtocol("FTPS (Control)", NetworkTransport.BOTH, 990, null);
		createProtocol("Telnet", NetworkTransport.TCP, 23, null);
		createProtocol("SSH", NetworkTransport.TCP, 22, null);
		createProtocol("VNC", NetworkTransport.TCP, 5900, 5910);
		createProtocol("VNC:0", NetworkTransport.TCP, 5900, null);
		createProtocol("VNC:1", NetworkTransport.TCP, 5901, null);
		createProtocol("VNC:2", NetworkTransport.TCP, 5902, null);
		createProtocol("VNC:3", NetworkTransport.TCP, 5903, null);
		createProtocol("VNC:4", NetworkTransport.TCP, 5904, null);
		createProtocol("VNC:5", NetworkTransport.TCP, 5905, null);
		createProtocol("RDP", NetworkTransport.TCP, 3389, null);
		createProtocol("HTTP", NetworkTransport.TCP, 80, null);
		createProtocol("HTTPS", NetworkTransport.TCP, 443, null);
//		createProtocol("POP3", NetworkTransport.TCP, 110, null);
//		createProtocol("SSL-POP", NetworkTransport.TCP, 995, null);
//		createProtocol("SMTP", NetworkTransport.TCP, 25, null);
//		createProtocol("SSMTP", NetworkTransport.TCP, 465, null);
//		createProtocol("IMAP", NetworkTransport.TCP, 143, null);
//		createProtocol("IMAP4-SSL", NetworkTransport.TCP, 585, null);
//		createProtocol("IMAPS", NetworkTransport.TCP, 993, null);
//		createProtocol("LDAP", NetworkTransport.TCP, 389, null);
//		createProtocol("LDAPS", NetworkTransport.TCP, 636, null);
//		createProtocol("IPP", NetworkTransport.BOTH, 631, null);
	}

	void createProtocol(String name, NetworkTransport transport, Integer start,
			Integer end) {
		NetworkProtocol protocol = new NetworkProtocol();
		protocol.setName(name);
		protocol.setTransport(transport);
		protocol.setStartPort(start);
		protocol.setEndPort(end);

		networkRepository.saveProtocol(protocol);

	}

}
