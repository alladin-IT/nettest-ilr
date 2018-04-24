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

package at.alladin.nettest.shared.model.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.joda.time.DateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.model.CellLocation;
import at.alladin.nettest.shared.model.ExtendedTestStat;
import at.alladin.nettest.shared.model.GeoLocation;
import at.alladin.nettest.shared.model.MeasurementSpeedRawItem;
import at.alladin.nettest.shared.model.MeasurementSpeedtestPortMap;
import at.alladin.nettest.shared.model.Ping;
import at.alladin.nettest.shared.model.Signal;

/**
 * 
 * @author lb@specure.com
 *
 */
public class SpeedtestResultSubmitRequest extends BasicRequestImpl {

	/**
	 * TODO: Same as <language>?
	 */
    @SerializedName("client_language")
    @Expose
    private String clientLanguage;

    /**
     * TODO: Same as <software_version>?
     */
    @SerializedName("client_software_version")
    @Expose
    private String clientSoftwareVersion;
    
    
    /**
     * Uuid of client
     * 
     * (Required)
     * 
     */
    @SerializedName("client_uuid")
    @Expose
    private String clientUuid;

    /**
     * TODO: Same as <software_version_name>? RMBT_VERSION_NUMBER
     */
    @SerializedName("client_version")
    @Expose
    private String clientVersion;

    /**
     * TODO:
     */
    @SerializedName("extended_test_stat")
    @Expose
    private ExtendedTestStat extendedTestStat;

    /**
     * Locations during measurement
     */
    @SerializedName("geo_locations")
    @Expose
    private List<GeoLocation> geoLocations = new ArrayList<GeoLocation>();

    /**
     * TODO:
     */
    @SerializedName("network_type")
    @Expose
    private Integer networkType;

    /**
     * Ping measurements
     */
    @SerializedName("pings")
    @Expose
    private List<Ping> pings = new ArrayList<Ping>();

    /**
     * Signal measurements
     */
    @SerializedName("signals")
    @Expose
    private List<Signal> signals = new ArrayList<Signal>();
    
    /**
     * Speed measurements
     */
    @SerializedName("speed_detail")
    @Expose
    private List<SpeedRawItem> speedDetail = new ArrayList<SpeedRawItem>();
    
    /**
     * TODO:
     */
    @SerializedName("bytes_download")
    @Expose
    private Long bytesDownload;
    
    /**
     * TODO:
     */
    @SerializedName("bytes_upload")
    @Expose
    private Long bytesUpload;
    
    /**
     * TODO:
     */
    @SerializedName("encryption")
    @Expose
    private String encryption;
    
    /**
     * Local ip (from socket) 
     */
    @SerializedName("ip_local")
    @Expose
    private String ipLocal;
    
    /**
     * Remote ip of measurement server (from socket)
     */
    @SerializedName("ip_server")
    @Expose
    private String ipServer;
    
    /**
     * Remote port of measurement server (from socket)
     */
    @SerializedName("port_remote")
    @Expose
    private Long portRemote;
    
    /**
     * TODO:
     */
    @SerializedName("duration_upload_ns")
    @Expose
    private Long durationUploadNs;
    
    /**
     * TODO:
     */
    @SerializedName("duration_download_ns")
    @Expose
    private Long durationDownloadNs;

    /**
     * Number of threads used (download)
     */
    @SerializedName("num_threads")
    @Expose
    private Long numThreads;

    /**
     * Number of threads used (upload)
     */
    @SerializedName("num_threads_ul")
    @Expose
    private Long numThreadsUl;
    
    /**
     * Shortest ping (client)
     */
    @SerializedName("ping_shortest")
    @Expose
    private Long pingShortest;

    /**
     * Calculated download speed (in kbs)
     */
    @SerializedName("speed_download")
    @Expose
    private Long speedDownload;

    /**
     * Calculated upload speed (in kbs)
     */
    @SerializedName("speed_upload")
    @Expose
    private Long speedUpload;
    
    /**
     * Server test token
     */
    @SerializedName("token")
    @Expose
    private String token;

    /**
     * TODO:
     */
    @SerializedName("total_bytes_download")
    @Expose
    private Long totalBytesDownload;

    /**
     * TODO:
     */
    @SerializedName("total_bytes_upload")
    @Expose
    private Long totalBytesUpload;

    /**
     * TODO:
     */
    @SerializedName("interface_total_bytes_download")
    @Expose
    private Long interfaceTotalBytesDownload;

    /**
     * TODO:
     */
    @SerializedName("interface_total_bytes_upload")
    @Expose
    private Long interfaceTotalBytesUpload;

    /**
     * TODO:
     */
    @SerializedName("interface_dltest_bytes_download")
    @Expose
    private Long interfaceDltestBytesDownload;

    /**
     * TODO:
     */
    @SerializedName("interface_dltest_bytes_upload")
    @Expose
    private Long interfaceDltestBytesUpload;

    /**
     * TODO:
     */
    @SerializedName("interface_ultest_bytes_download")
    @Expose
    private Long interfaceUltestBytesDownload;

    /**
     * TODO:
     */
    @SerializedName("interface_ultest_bytes_upload")
    @Expose
    private Long interfaceUltestBytesUpload;

    /**
     * TODO: Test send time (ISO formatted)
     */
    @SerializedName("time")
    @Expose
    private DateTime time;

    /**
     * TODO: Relative start time download in ns?
     */
    @SerializedName("relative_time_dl_ns")
    @Expose
    private Long relativeTimeDlNs;

    /**
     * TODO: Relative start time upload in ns?
     */
    @SerializedName("relative_time_ul_ns")
    @Expose
    private Long relativeTimeUlNs;
    
    /**
     * TODO: Test uuid?
     * 
     * (Required)
     * 
     */
    @SerializedName("uuid")
    @Expose
    private String uuid;

    /**
     * TODO:
     */
    @SerializedName("telephony_info")
    @Expose
    private TelephonyInfo telephonyInfo;

    /**
     * TODO:
     */
    @SerializedName("wifi_info")
    @Expose
    private WifiInfo wifiInfo;

    /**
     * TODO:
     */
    @SerializedName("cell_locations")
    @Expose
    private List<CellLocation> cellLocations;

    /**
     * TODO: Should publish data? (default: true)
     */
    @SerializedName("publish_public_data")
    @Expose
    private boolean publishPublicData = true;

    /**
     * TODO:
     */
    @SerializedName("tag")
    @Expose
    private String tag;
	
    /**
     * 
     */
    @SerializedName("source_ports")
    @Expose
    private MeasurementSpeedtestPortMap sourcePortMap;
    
	public String getClientLanguage() {
		return clientLanguage;
	}

	public void setClientLanguage(String clientLanguage) {
		this.clientLanguage = clientLanguage;
	}

	public String getClientSoftwareVersion() {
		return clientSoftwareVersion;
	}

	public void setClientSoftwareVersion(String clientSoftwareVersion) {
		this.clientSoftwareVersion = clientSoftwareVersion;
	}

	public String getClientUuid() {
		return clientUuid;
	}

	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public ExtendedTestStat getExtendedTestStat() {
		return extendedTestStat;
	}

	public void setExtendedTestStat(ExtendedTestStat extendedTestStat) {
		this.extendedTestStat = extendedTestStat;
	}

	public List<GeoLocation> getGeoLocations() {
		return geoLocations;
	}

	public void setGeoLocations(List<GeoLocation> geoLocations) {
		this.geoLocations = geoLocations;
	}

	public Integer getNetworkType() {
		return networkType;
	}

	public void setNetworkType(Integer networkType) {
		this.networkType = networkType;
	}

	public List<Ping> getPings() {
		return pings;
	}

	public void setPings(List<Ping> pings) {
		this.pings = pings;
	}

	public List<Signal> getSignals() {
		return signals;
	}

	public void setSignals(List<Signal> signals) {
		this.signals = signals;
	}

	public List<SpeedRawItem> getSpeedDetail() {
		return speedDetail;
	}

	public void setSpeedDetail(List<SpeedRawItem> speedDetail) {
		this.speedDetail = speedDetail;
	}

	public Long getBytesDownload() {
		return bytesDownload;
	}

	public void setBytesDownload(Long bytesDownload) {
		this.bytesDownload = bytesDownload;
	}

	public Long getBytesUpload() {
		return bytesUpload;
	}

	public void setBytesUpload(Long bytesUpload) {
		this.bytesUpload = bytesUpload;
	}

	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	public String getIpLocal() {
		return ipLocal;
	}

	public void setIpLocal(String ipLocal) {
		this.ipLocal = ipLocal;
	}

	public String getIpServer() {
		return ipServer;
	}

	public void setIpServer(String ipServer) {
		this.ipServer = ipServer;
	}

	public Long getDurationUploadNs() {
		return durationUploadNs;
	}

	public void setDurationUploadNs(Long durationUploadNs) {
		this.durationUploadNs = durationUploadNs;
	}

	public Long getDurationDownloadNs() {
		return durationDownloadNs;
	}

	public void setDurationDownloadNs(Long durationDownloadNs) {
		this.durationDownloadNs = durationDownloadNs;
	}

	public Long getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(Long numThreads) {
		this.numThreads = numThreads;
	}

	public Long getNumThreadsUl() {
		return numThreadsUl;
	}

	public void setNumThreadsUl(Long numThreadsUl) {
		this.numThreadsUl = numThreadsUl;
	}

	public Long getPingShortest() {
		return pingShortest;
	}

	public void setPingShortest(Long pingShortest) {
		this.pingShortest = pingShortest;
	}

	public Long getPortRemote() {
		return portRemote;
	}

	public void setPortRemote(Long portRemote) {
		this.portRemote = portRemote;
	}

	public Long getSpeedDownload() {
		return speedDownload;
	}

	public void setSpeedDownload(Long speedDownload) {
		this.speedDownload = speedDownload;
	}

	public Long getSpeedUpload() {
		return speedUpload;
	}

	public void setSpeedUpload(Long speedUpload) {
		this.speedUpload = speedUpload;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getTotalBytesDownload() {
		return totalBytesDownload;
	}

	public void setTotalBytesDownload(Long totalBytesDownload) {
		this.totalBytesDownload = totalBytesDownload;
	}

	public Long getTotalBytesUpload() {
		return totalBytesUpload;
	}

	public void setTotalBytesUpload(Long totalBytesUpload) {
		this.totalBytesUpload = totalBytesUpload;
	}

	public Long getInterfaceTotalBytesDownload() {
		return interfaceTotalBytesDownload;
	}

	public void setInterfaceTotalBytesDownload(Long interfaceTotalBytesDownload) {
		this.interfaceTotalBytesDownload = interfaceTotalBytesDownload;
	}

	public Long getInterfaceTotalBytesUpload() {
		return interfaceTotalBytesUpload;
	}

	public void setInterfaceTotalBytesUpload(Long interfaceTotalBytesUpload) {
		this.interfaceTotalBytesUpload = interfaceTotalBytesUpload;
	}

	public Long getInterfaceDltestBytesDownload() {
		return interfaceDltestBytesDownload;
	}

	public void setInterfaceDltestBytesDownload(Long interfaceDltestBytesDownload) {
		this.interfaceDltestBytesDownload = interfaceDltestBytesDownload;
	}

	public Long getInterfaceDltestBytesUpload() {
		return interfaceDltestBytesUpload;
	}

	public void setInterfaceDltestBytesUpload(Long interfaceDltestBytesUpload) {
		this.interfaceDltestBytesUpload = interfaceDltestBytesUpload;
	}

	public Long getInterfaceUltestBytesDownload() {
		return interfaceUltestBytesDownload;
	}

	public void setInterfaceUltestBytesDownload(Long interfaceUltestBytesDownload) {
		this.interfaceUltestBytesDownload = interfaceUltestBytesDownload;
	}

	public Long getInterfaceUltestBytesUpload() {
		return interfaceUltestBytesUpload;
	}

	public void setInterfaceUltestBytesUpload(Long interfaceUltestBytesUpload) {
		this.interfaceUltestBytesUpload = interfaceUltestBytesUpload;
	}

	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}

	public Long getRelativeTimeDlNs() {
		return relativeTimeDlNs;
	}

	public void setRelativeTimeDlNs(Long relativeTimeDlNs) {
		this.relativeTimeDlNs = relativeTimeDlNs;
	}

	public Long getRelativeTimeUlNs() {
		return relativeTimeUlNs;
	}

	public void setRelativeTimeUlNs(Long relativeTimeUlNs) {
		this.relativeTimeUlNs = relativeTimeUlNs;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public TelephonyInfo getTelephonyInfo() {
		return telephonyInfo;
	}

	public void setTelephonyInfo(TelephonyInfo telephonyInfo) {
		this.telephonyInfo = telephonyInfo;
	}

	public WifiInfo getWifiInfo() {
		return wifiInfo;
	}

	public void setWifiInfo(WifiInfo wifiInfo) {
		this.wifiInfo = wifiInfo;
	}

	public List<CellLocation> getCellLocations() {
		return cellLocations;
	}

	public void setCellLocations(List<CellLocation> cellLocations) {
		this.cellLocations = cellLocations;
	}

	public boolean isPublishPublicData() {
		return publishPublicData;
	}

	public void setPublishPublicData(boolean publishPublicData) {
		this.publishPublicData = publishPublicData;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public MeasurementSpeedtestPortMap getSourcePortMap() {
		return sourcePortMap;
	}

	public void setSourcePortMap(MeasurementSpeedtestPortMap sourcePortMap) {
		this.sourcePortMap = sourcePortMap;
	}

	/**
	 * 
	 * @author lb@specure.com
	 *
	 */
	public static class SpeedRawItem extends MeasurementSpeedRawItem {

	    /**
	     * Data transfer direction
	     */
	    @SerializedName("direction")
		@Expose
		private SpeedRawItemDirection direction;
		
	    
		public SpeedRawItemDirection getDirection() {
			return direction;
		}

		public void setDirection(SpeedRawItemDirection direction) {
			this.direction = direction;
		}

		/**
		 * 
		 * @author lb@specure.com
		 *
		 */
	    public static enum SpeedRawItemDirection {

	    	/**
	    	 * Data transfer direction download
	    	 */
	        @SerializedName("download")
	        DOWNLOAD("download"),
	        
	        /**
	         * Data transfer direction upload
	         */
	        @SerializedName("upload")
	        UPLOAD("upload");
	        
	        /**
	         * 
	         */
	        private final String value;
	        
	        /**
	         * 
	         */
	        private final static Map<String, SpeedRawItemDirection> CONSTANTS = new HashMap<String, SpeedRawItemDirection>();

	        /**
	         * 
	         */
	        static {
	            for (SpeedRawItemDirection c : values()) {
	                CONSTANTS.put(c.value, c);
	            }
	        }

	        /**
	         * 
	         * @param value
	         */
	        private SpeedRawItemDirection(String value) {
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
	        public static SpeedRawItemDirection fromValue(String value) {
	        	SpeedRawItemDirection constant = CONSTANTS.get(value);
	            if (constant == null) {
	                throw new IllegalArgumentException(value);
	            } else {
	                return constant;
	            }
	        }
	    }
	}
	
	/**
	 * 
	 * @author lb@specure.com
	 *
	 */
	@Generated("org.jsonschema2pojo")
	public static class TelephonyInfo {

	    @SerializedName("data_state")
	    @Expose
	    private Integer dataState;
	    
	    @SerializedName("network_country")
	    @Expose
	    private String networkCountry;
	    
	    @SerializedName("network_is_roaming")
	    @Expose
	    private boolean networkIsRoaming;
	    
	    @SerializedName("network_operator")
	    @Expose
	    private String networkOperator;
	    
	    @SerializedName("network_operator_name")
	    @Expose
	    private String networkOperatorName;
	    
	    @SerializedName("network_sim_country")
	    @Expose
	    private String networkSimCountry;
	    
	    @SerializedName("network_sim_operator")
	    @Expose
	    private String networkSimOperator;
	    
	    @SerializedName("network_sim_operator_name")
	    @Expose
	    private String networkSimOperatorName;
	    
	    @SerializedName("phone_type")
	    @Expose
	    private Long phoneType;

	    /**
	     * 
	     * @return
	     *     The dataState
	     */
	    public Integer getDataState() {
	        return dataState;
	    }

	    /**
	     * 
	     * @param dataState
	     *     The data_state
	     */
	    public void setDataState(Integer dataState) {
	        this.dataState = dataState;
	    }

	    /**
	     * 
	     * @return
	     *     The networkCountry
	     */
	    public String getNetworkCountry() {
	        return networkCountry;
	    }

	    /**
	     * 
	     * @param networkCountry
	     *     The network_country
	     */
	    public void setNetworkCountry(String networkCountry) {
	        this.networkCountry = networkCountry;
	    }

	    /**
	     * 
	     * @return
	     *     The networkIsRoaming
	     */
	    public boolean getNetworkIsRoaming() {
	        return networkIsRoaming;
	    }

	    /**
	     * 
	     * @param networkIsRoaming
	     *     The network_is_roaming
	     */
	    public void setNetworkIsRoaming(boolean networkIsRoaming) {
	        this.networkIsRoaming = networkIsRoaming;
	    }

	    /**
	     * 
	     * @return
	     *     The networkOperator
	     */
	    public String getNetworkOperator() {
	        return networkOperator;
	    }

	    /**
	     * 
	     * @param networkOperator
	     *     The network_operator
	     */
	    public void setNetworkOperator(String networkOperator) {
	        this.networkOperator = networkOperator;
	    }

	    /**
	     * 
	     * @return
	     *     The networkOperatorName
	     */
	    public String getNetworkOperatorName() {
	        return networkOperatorName;
	    }

	    /**
	     * 
	     * @param networkOperatorName
	     *     The network_operator_name
	     */
	    public void setNetworkOperatorName(String networkOperatorName) {
	        this.networkOperatorName = networkOperatorName;
	    }

	    /**
	     * 
	     * @return
	     *     The networkSimCountry
	     */
	    public String getNetworkSimCountry() {
	        return networkSimCountry;
	    }

	    /**
	     * 
	     * @param networkSimCountry
	     *     The network_sim_country
	     */
	    public void setNetworkSimCountry(String networkSimCountry) {
	        this.networkSimCountry = networkSimCountry;
	    }

	    /**
	     * 
	     * @return
	     *     The networkSimOperator
	     */
	    public String getNetworkSimOperator() {
	        return networkSimOperator;
	    }

	    /**
	     * 
	     * @param networkSimOperator
	     *     The network_sim_operator
	     */
	    public void setNetworkSimOperator(String networkSimOperator) {
	        this.networkSimOperator = networkSimOperator;
	    }

	    /**
	     * 
	     * @return
	     *     The networkSimOperatorName
	     */
	    public String getNetworkSimOperatorName() {
	        return networkSimOperatorName;
	    }

	    /**
	     * 
	     * @param networkSimOperatorName
	     *     The network_sim_operator_name
	     */
	    public void setNetworkSimOperatorName(String networkSimOperatorName) {
	        this.networkSimOperatorName = networkSimOperatorName;
	    }

	    /**
	     * 
	     * @return
	     *     The phoneType
	     */
	    public Long getPhoneType() {
	        return phoneType;
	    }

	    /**
	     * 
	     * @param phoneType
	     *     The phone_type
	     */
	    public void setPhoneType(Long phoneType) {
	        this.phoneType = phoneType;
	    }

	}

	/**
	 * 
	 * @author lb@specure.com
	 *
	 */
	@Generated("org.jsonschema2pojo")
	public static class WifiInfo {

		/**
		 * BSSID
		 */
	    @SerializedName("bssid")
	    @Expose
	    private String bssid;
	    
	    /**
	     * TODO: Network ID
	     */
	    @SerializedName("network_id")
	    @Expose
	    private String networkId;
	    
	    /**
	     * SSID
	     */
	    @SerializedName("ssid")
	    @Expose
	    private String ssid;
	    
	    /**
	     * TODO:
	     */
	    @SerializedName("supplicant_state")
	    @Expose
	    private String supplicantState;
	    
	    /**
	     * TODO:
	     */
	    @SerializedName("supplicant_state_detail")
	    @Expose
	    private String supplicantStateDetail;

	    /**
	     * 
	     * @return
	     *     The bssid
	     */
	    public String getBssid() {
	        return bssid;
	    }

	    /**
	     * 
	     * @param bssid
	     *     The bssid
	     */
	    public void setBssid(String bssid) {
	        this.bssid = bssid;
	    }

	    /**
	     * 
	     * @return
	     *     The networkId
	     */
	    public String getNetworkId() {
	        return networkId;
	    }

	    /**
	     * 
	     * @param networkId
	     *     The network_id
	     */
	    public void setNetworkId(String networkId) {
	        this.networkId = networkId;
	    }

	    /**
	     * 
	     * @return
	     *     The ssid
	     */
	    public String getSsid() {
	        return ssid;
	    }

	    /**
	     * 
	     * @param ssid
	     *     The ssid
	     */
	    public void setSsid(String ssid) {
	        this.ssid = ssid;
	    }

	    /**
	     * 
	     * @return
	     *     The supplicantState
	     */
	    public String getSupplicantState() {
	        return supplicantState;
	    }

	    /**
	     * 
	     * @param supplicantState
	     *     The supplicant_state
	     */
	    public void setSupplicantState(String supplicantState) {
	        this.supplicantState = supplicantState;
	    }

	    /**
	     * 
	     * @return
	     *     The supplicantStateDetail
	     */
	    public String getSupplicantStateDetail() {
	        return supplicantStateDetail;
	    }

	    /**
	     * 
	     * @param supplicantStateDetail
	     *     The supplicant_state_detail
	     */
	    public void setSupplicantStateDetail(String supplicantStateDetail) {
	        this.supplicantStateDetail = supplicantStateDetail;
	    }

	}

	@Override
	public String toString() {
		return "SpeedtestResultSubmitRequest [clientLanguage=" + clientLanguage + ", clientSoftwareVersion="
				+ clientSoftwareVersion + ", clientUuid=" + clientUuid + ", clientVersion=" + clientVersion
				+ ", extendedTestStat=" + extendedTestStat + ", geoLocations=" + geoLocations + ", networkType="
				+ networkType + ", pings=" + pings + ", signals=" + signals + ", speedDetail=" + speedDetail
				+ ", bytesDownload=" + bytesDownload + ", bytesUpload=" + bytesUpload + ", encryption=" + encryption
				+ ", ipLocal=" + ipLocal + ", ipServer=" + ipServer + ", portRemote=" + portRemote
				+ ", durationUploadNs=" + durationUploadNs + ", durationDownloadNs=" + durationDownloadNs
				+ ", numThreads=" + numThreads + ", numThreadsUl=" + numThreadsUl + ", pingShortest=" + pingShortest
				+ ", speedDownload=" + speedDownload + ", speedUpload=" + speedUpload + ", token=" + token
				+ ", totalBytesDownload=" + totalBytesDownload + ", totalBytesUpload=" + totalBytesUpload
				+ ", interfaceTotalBytesDownload=" + interfaceTotalBytesDownload + ", interfaceTotalBytesUpload="
				+ interfaceTotalBytesUpload + ", interfaceDltestBytesDownload=" + interfaceDltestBytesDownload
				+ ", interfaceDltestBytesUpload=" + interfaceDltestBytesUpload + ", interfaceUltestBytesDownload="
				+ interfaceUltestBytesDownload + ", interfaceUltestBytesUpload=" + interfaceUltestBytesUpload
				+ ", time=" + time + ", relativeTimeDlNs=" + relativeTimeDlNs + ", relativeTimeUlNs=" + relativeTimeUlNs
				+ ", uuid=" + uuid + ", telephonyInfo=" + telephonyInfo + ", wifiInfo=" + wifiInfo + ", cellLocations="
				+ cellLocations + ", publishPublicData=" + publishPublicData + ", tag=" + tag + ", sourcePortMap="
				+ sourcePortMap + "]";
	}
}
