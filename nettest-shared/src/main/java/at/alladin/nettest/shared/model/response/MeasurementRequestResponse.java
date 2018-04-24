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

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.model.MeasurementDeviceInfo;

/**
 * 
 * @author lb@specure.com
 *
 */
@Generated("org.jsonschema2pojo")
public class MeasurementRequestResponse {

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("client_remote_ip")
    @Expose
    private String clientRemoteIp;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("duration")
    @Expose
    private Integer duration;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("num_threads")
    @Expose
    private Integer numThreads;
    @SerializedName("num_pings")
    @Expose
    private Integer numPings;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("target_measurement_server")
    @Expose
    private TargetMeasurementServer targetMeasurementServer;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("test_token")
    @Expose
    private String testToken;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("test_uuid")
    @Expose
    private String testUuid;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("test_wait")
    @Expose
    private Integer testWait;

    /**
     * To inform the client of guessed values based on the user agent
     * 
     * (Optional)
     */
    @Expose
    private MeasurementDeviceInfo device;
    
    /**
     * 
     * (Required)
     * 
     * @return
     *     The clientRemoteIp
     */
    public String getClientRemoteIp() {
        return clientRemoteIp;
    }

    /**
     * 
     * (Required)
     * 
     * @param clientRemoteIp
     *     The client_remote_ip
     */
    public void setClientRemoteIp(String clientRemoteIp) {
        this.clientRemoteIp = clientRemoteIp;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * 
     * (Required)
     * 
     * @param duration
     *     The duration
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The numThreads
     */
    public Integer getNumThreads() {
        return numThreads;
    }

    /**
     * 
     * (Required)
     * 
     * @param numThreads
     *     The num_threads
     */
    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    /**
     * 
     * @return
     *     The numPings
     */
    public Integer getNumPings() {
        return numPings;
    }

    /**
     * 
     * @param numPings
     *     The num_pings
     */
    public void setNumPings(Integer numPings) {
        this.numPings = numPings;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The targetMeasurementServer
     */
    public TargetMeasurementServer getTargetMeasurementServer() {
        return targetMeasurementServer;
    }

    /**
     * 
     * (Required)
     * 
     * @param targetMeasurementServer
     *     The target_measurement_server
     */
    public void setTargetMeasurementServer(TargetMeasurementServer targetMeasurementServer) {
        this.targetMeasurementServer = targetMeasurementServer;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The testToken
     */
    public String getTestToken() {
        return testToken;
    }

    /**
     * 
     * (Required)
     * 
     * @param testToken
     *     The test_token
     */
    public void setTestToken(String testToken) {
        this.testToken = testToken;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The testUuid
     */
    public String getTestUuid() {
        return testUuid;
    }

    /**
     * 
     * (Required)
     * 
     * @param testUuid
     *     The test_uuid
     */
    public void setTestUuid(String testUuid) {
        this.testUuid = testUuid;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The testWait
     */
    public Integer getTestWait() {
        return testWait;
    }

    /**
     * 
     * (Required)
     * 
     * @param testWait
     *     The test_wait
     */
    public void setTestWait(Integer testWait) {
        this.testWait = testWait;
    }
    
    public MeasurementDeviceInfo getDevice() {
		return device;
	}

	public void setDevice(MeasurementDeviceInfo device) {
		this.device = device;
	}

	@Override
	public String toString() {
		return "MeasurementRequestResponse [clientRemoteIp=" + clientRemoteIp + ", duration=" + duration
				+ ", numThreads=" + numThreads + ", numPings=" + numPings + ", targetMeasurementServer="
				+ targetMeasurementServer + ", testToken=" + testToken + ", testUuid=" + testUuid + ", testWait="
				+ testWait + ", device=" + device + "]";
	}


	/**
     * 
     * @author lb@specure.com
     *
     */
    @Generated("org.jsonschema2pojo")
    public static class TargetMeasurementServer {

        @SerializedName("address")
        @Expose
        private String address;
        
        @SerializedName("is_encrypted")
        @Expose
        private Boolean isEncrypted;

        /**
         * name of the test server used for download/upload (not applicable for JStest)
         */
        @SerializedName("name")
        @Expose
        private String name;
        
        @SerializedName("port")
        @Expose
        private Integer port;
        
        @SerializedName("uuid")
        @Expose
        private String uuid;
        
        @SerializedName("ip")
        @Expose        
        private String ip;

        /**
         * 
         * @return
         *     The address
         */
        public String getAddress() {
            return address;
        }

        /**
         * 
         * @param address
         *     The address
         */
        public void setAddress(String address) {
            this.address = address;
        }

        /**
         * 
         * @return
         *     The isEncrypted
         */
        public Boolean getIsEncrypted() {
            return isEncrypted;
        }

        /**
         * 
         * @param isEncrypted
         *     The is_encrypted
         */
        public void setIsEncrypted(Boolean isEncrypted) {
            this.isEncrypted = isEncrypted;
        }

        /**
         * 
         * @return
         *     The name
         */
        public String getName() {
            return name;
        }

        /**
         * 
         * @param name
         *     The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * 
         * @return
         *     The port
         */
        public Integer getPort() {
            return port;
        }

        /**
         * 
         * @param port
         *     The port
         */
        public void setPort(Integer port) {
            this.port = port;
        }

        /**
         * 
         * @return
         */
		public String getUuid() {
			return uuid;
		}

		/**
		 * 
		 * @param uuid
		 */
		public void setUuid(String uuid) {
			this.uuid = uuid;
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

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "TargetMeasurementServer [address=" + address + ", isEncrypted=" + isEncrypted + ", name=" + name
					+ ", port=" + port + ", uuid=" + uuid + ", ip=" + ip + "]";
		}
    }
}