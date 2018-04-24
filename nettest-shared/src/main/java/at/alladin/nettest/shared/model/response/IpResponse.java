/*******************************************************************************
 * Copyright 2016-2017 alladin-IT GmbH
 * Copyright 2016 SPECURE GmbH
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

package at.alladin.nettest.shared.model.response;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class IpResponse {

	/**
	 * 
	 */
	@Expose
	private String ip;
	
	/**
	 * 
	 */
	@Expose
	private IpVersion version;
	
	/**
	 * 
	 */
	public IpResponse() {
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String getIp() {
		return ip;
	}
	
	/**
	 * 
	 * @param ip
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	/**
	 * 
	 * @return
	 */
	public IpVersion getVersion() {
		return version;
	}
	
	/**
	 * 
	 * @param version
	 */
	public void setVersion(IpVersion version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IpResponse [ip=" + ip + ", version=" + version + "]";
	}
	
	/**
	 * 
	 * @author Specure GmbH (bp@specure.com)
	 *
	 */
	public static enum IpVersion {
		
		/**
		 * 
		 */
		@SerializedName("ipv4")
		IP_VERSION_4("ipv4"),
		
		/**
		 * 
		 */
		@SerializedName("ipv6")
		IP_VERSION_6("ipv6");
		
		/**
		 * 
		 */
		private final String value;

        /**
         * 
         * @param value
         */
        private IpVersion(String value) {
            this.value = value;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return this.value;
        }

        /**
         * 
         * @param value
         * @return
         */
        public static IpVersion fromInetAddress(InetAddress inetAddress) {
        	if (inetAddress instanceof Inet4Address) {
    			return IP_VERSION_4;
    		} else if (inetAddress instanceof Inet6Address) {
    			return IP_VERSION_6;
    		}
        	
        	throw new IllegalArgumentException("Unknown InetAddress type");
        }
	}
}
