/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.rmbt.android.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import at.alladin.rmbt.util.tools.ConnectionInformation;
import at.alladin.rmbt.util.tools.ConnectionInformation.ProtocolType;
import at.alladin.rmbt.util.tools.NetStat;
import at.alladin.rmbt.util.tools.TcpConnectionInformation;
import at.alladin.rmbt.util.tools.UdpConnectionInformation;

/**
 * android (linux) implementation of the netstat tool
 * @author lb
 *
 */
public class NetStatAndroidImpl implements NetStat {
	private final static String PATH_TO_NET = "/proc/net/";
	
	public static enum NetFiles {
		TCP("tcp", 4),
		TCP6("tcp6", 16),
		UDP("udp", 4),
		UDP6("udp6", 16);
		
		protected final String file;
		protected final int addrSize;
		
		private NetFiles(String file, int addrSize) {
			this.file = file;
			this.addrSize = addrSize;
		}

		public String getFile() {
			return file;
		}

		public int getAddrSize() {
			return addrSize;
		}
	}
		
	protected static List<ConnectionInformation> readFromProc(NetFiles netFile) {
		BufferedReader br = null;
		List<ConnectionInformation> connList = new ArrayList<ConnectionInformation>();
		
		File f = new File(PATH_TO_NET + netFile.getFile());
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        	String currInputLine = null;
	        
        	while((currInputLine = br.readLine()) != null) {
        		ConnectionInformation conn = null;
        		
        		if (netFile == NetFiles.TCP6 || netFile == NetFiles.TCP) {
        			conn = TcpConnectionInformation.parseAndroid(currInputLine, netFile.getAddrSize());
        		}
        		else {
        			conn = UdpConnectionInformation.parseAndroid(currInputLine, netFile.getAddrSize());
        		}
        		
        		if (conn != null) {
        			connList.add(conn);
        		}
	        }
	        
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) { }
		}
		
		return connList;
	}
	

	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.client.tools.NetStat#runSystemCommand()
	 */
	public String runSystemCommand() {
        try {
            ProcessBuilder builder = new ProcessBuilder( "netstat", "-ntpu" );
            
        	Process p = builder.start();
			
        	BufferedReader inputFile = new BufferedReader(new InputStreamReader(p.getInputStream()));

        	StringBuilder sb = new StringBuilder();
        	
        	String currInputLine = null;
	        while((currInputLine = inputFile.readLine()) != null) {
	        	sb.append(currInputLine);
	            sb.append("\n");
	        }
	        
	        return sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

        return null;
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.client.tools.NetStat#getConnectionList(at.alladin.rmbt.client.tools.ConnectionInformation.ProtocolType)
	 */
	public List<ConnectionInformation> getConnectionList(ProtocolType protocolType) {
		switch (protocolType) {
		case TCP:
			return readFromProc(NetFiles.TCP);
		case TCP6:
			return readFromProc(NetFiles.TCP6);
		case UDP:
			return readFromProc(NetFiles.UDP);
		case UDP6:
			return readFromProc(NetFiles.UDP6);
		default:
			return new ArrayList<ConnectionInformation>();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.client.tools.NetStat#getConnectionList()
	 */
	public List<ConnectionInformation> getConnectionList() {
		List<ConnectionInformation> connList = new ArrayList<ConnectionInformation>();
		connList.addAll(readFromProc(NetFiles.TCP));
		connList.addAll(readFromProc(NetFiles.TCP6));
		connList.addAll(readFromProc(NetFiles.UDP));
		connList.addAll(readFromProc(NetFiles.UDP6));
		return connList;
	}
}
