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

package at.alladin.nettest.shared.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Generated;

import org.joda.time.DateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.UuidAwareEntity;
import at.alladin.nettest.shared.annotation.ExcludeFromOpenTest;
import at.alladin.nettest.shared.model.response.MeasurementRequestResponse.TargetMeasurementServer;

/**
 * 
 * @author lb@specure.com
 *
 */
@Generated("org.jsonschema2pojo")
public class Measurement extends UuidAwareEntity {

	private static final long serialVersionUID = 6487947366013204859L;

	/**
     * Is the measurement implausible
     * 
     * (Required)
     * 
     */
    @SerializedName("implausible")
    @Expose
    private boolean implausible = false;
    
    /**
     * Has the measurement been deleted
     * 
     * (Required)
     * 
     */
    @SerializedName("deleted")
    @Expose
    private boolean deleted = false;
    
    /**
     * Comment
     * 
     * (Required)
     * 
     */
    @SerializedName("comment")
    @Expose
    private String comment;
    
    /**
     * Deprecated
     * 
     * (Required)
     */
    @SerializedName("open_uuid")
    @Expose
    private String openUuid;
    
    /**
     * UUID of measurement in opendata
     * 
     * (Required)
     * 
     */
    @SerializedName("open_test_uuid")
    @Expose
    private String openTestUuid;
    
    /**
     * can be additionally provided by a web client (applet, js test, or submitted after test)
     * 
     * (Required)
     * 
     */
    @SerializedName("zip_code")
    @Expose
    private Integer zipCode;
    
    /**
     * unknown origin. OpenTestResource says: (internal) zip-code, integer derived from geo_location, Austria only. trigger?
     * 
     * (Required)
     * 
     */
    @SerializedName("zip_code_geo")
    @Expose
    private Integer zipCodeGeo;
    
    /**
     * Token issued by control server for request
     * 
     * (Required)
     * 
     */
    @SerializedName("token")
    @Expose
    private String token;
    
    /**
     * trigger only. last update timestamp
     * (known as time in the OpenTestResource)
     * 
     * (Required)
     * 
     */
    @SerializedName("timestamp")
    @Expose
    private DateTime timestamp;
    
    /**
     * saved during test result submit: ServerResource.getIP()
     * 
     * (Required)
     * 
     */
    @ExcludeFromOpenTest
    @SerializedName("source_ip")
    @Expose
    private String sourceIp;
    
    /**
     * Anonymized IP
     * 
     * (Required)
     * 
     */
    @SerializedName(value="source_ip_anonymized", alternate="ip_anonym")
    @Expose
    private String sourceIpAnonymized;
    
    /**
     * Measurement tag settable by the client
     * 
     * (Required)
     * 
     */
    @SerializedName("tag")
    @Expose
    private String tag;
    
    /**
     * deprecated
     * 
     * (Required)
     * 
     */
    @SerializedName("hidden_code")
    @Expose
    private String hiddenCode;
    
    /**
     * deprecated
     * 
     * (Required)
     * 
     */
    @SerializedName("data")
    @Expose
    private Object data;
    
    
    /**
     * Should measurement be published as open data
     * 
     * (Required)
     * 
     */
    @SerializedName("publish_public_data")
    @Expose
    private Boolean publishPublicData;
    
    /**
     * Austrian 'Gemeindekennzahl'
     * 
     * (Required)
     * 
     */
    @SerializedName("gkz")
    @Expose
    private Integer gkz;
    
    /**
     * Name of originating system
     * 
     * (Required)
     * 
     */
    @SerializedName("opendata_source")
    @Expose
    private String opendataSource;
        
    @SerializedName("client_info")
    @Expose
    private MeasurementClientInfo clientInfo;
    
    @SerializedName("device_info")
    @Expose
    private MeasurementDeviceInfo deviceInfo;
    
    /**
     * remove network_ prefix for all fields
     * 
     */
    @SerializedName("mobile_network_info")
    @Expose
    private MeasurementMobileNetworkInfo mobileNetworkInfo;
    
    @SerializedName("network_info")
    @Expose
    private MeasurementNetworkInfo networkInfo;
    
    @SerializedName("speedtest")
    @Expose
    private MeasurementSpeedtest speedtest;
    
    /**
     * 
     */
    @SerializedName("qos")
    @Expose
    private MeasurementQos qos;
    
    /**
     * replaces geo_location table; previous version: geometry object. geo_lat, geo_long, geo_provider, geo_accuracy has been removed.
     * (Required)
     * 
     */
    @SerializedName("locations")
    @Expose
    private List<GeoLocation> locations = new ArrayList<>();
    
    /**
     * replaces geometry from previous version, same object as in 'location'. real_geo_lat and real_geo_long are obsolete
     * (Required)
     * 
     */
    @SerializedName("real_locations")
    @Expose
    private GeoLocation realLocation;
    
    /**
     * replaces signal table; previous version: integer
     * 
     */
    @SerializedName("signals")
    @Expose
    private List<Signal> signals = new ArrayList<>();
    
    /**
     * replaces cell_location table; the geo_location value is the same object as in 'location'
     * (Required)
     * 
     */
    @SerializedName("cell_locations")
    @Expose
    private List<CellLocation> cellLocations = new ArrayList<>();

    @SerializedName("wifi_info")
    @Expose
    private MeasurementWifiInfo wifiInfo;
    
    /**
     * Nominal speed of wifi-link in mbit/s
     */
	@SerializedName("wifi_link_speed")
	@Expose
	private Integer wifiLinkSpeed;
	
	/**
	 * Signal_strength RSSI, mainly GSM/UMTS and Wifi, Android only, in dBm
	 */
	@SerializedName("signal_strength")
	@Expose
	private Integer signalStrength;

	/**
	 * signal_strength RSRP, Android only, in dBm
	 */
	@SerializedName("lte_rsrp")
	@Expose
	private Integer lteRsrp;
	
	/**
	 * signal quality RSRQ, Android only, in dB
	 */
	@SerializedName("lte_rsrq")
	@Expose
	private Integer lteRsrq;
	
    @SerializedName("extended_test_stat")
    @Expose
    private ExtendedTestStat extendedTestStat;
    
    @SerializedName("anonymization")
    @Expose
    private MeasurementAnonymization anonymization;
	
    /**
     * 
     */
    public Measurement() {
    	super();
    	this.uuid = UUID.randomUUID().toString(); // generate random uuid for this measurement, TODO: should this be moved?
    }
    
    public MeasurementAnonymization getAnonymization() {
		return anonymization;
	}

	public void setAnonymization(MeasurementAnonymization anonymization) {
		this.anonymization = anonymization;
	}

	
    
    /**
     * 
     * (Required)
     * 
     * @return
     *     The implausible
     */
    public Boolean getImplausible() {
        return implausible;
    }

    /**
     * 
     * (Required)
     * 
     * @param implausible
     *     The implausible
     */
    public void setImplausible(Boolean implausible) {
        this.implausible = implausible;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * 
     * (Required)
     * 
     * @param deleted
     *     The deleted
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * 
     * (Required)
     * 
     * @param comment
     *     The comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The openUuid
     */
    public String getOpenUuid() {
        return openUuid;
    }

    /**
     * 
     * (Required)
     * 
     * @param openUuid
     *     The open_uuid
     */
    public void setOpenUuid(String openUuid) {
        this.openUuid = openUuid;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The openTestUuid
     */
    public String getOpenTestUuid() {
        return openTestUuid;
    }

    /**
     * 
     * (Required)
     * 
     * @param openTestUuid
     *     The open_test_uuid
     */
    public void setOpenTestUuid(String openTestUuid) {
        this.openTestUuid = openTestUuid;
    }

    /**
     * can be additionally provided by a web client (applet, js test, or submitted after test)
     * (Required)
     * 
     * @return
     *     The zipCode
     */
    public Integer getZipCode() {
        return zipCode;
    }

    /**
     * can be additionally provided by a web client (applet, js test, or submitted after test)
     * (Required)
     * 
     * @param zipCode
     *     The zip_code
     */
    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * unknown origin. OpenTestResource says: (internal) zip-code, integer derived from geo_location, Austria only. trigger?
     * (Required)
     * 
     * @return
     *     The zipCodeGeo
     */
    public Integer getZipCodeGeo() {
        return zipCodeGeo;
    }

    /**
     * unknown origin. OpenTestResource says: (internal) zip-code, integer derived from geo_location, Austria only. trigger?
     * (Required)
     * 
     * @param zipCodeGeo
     *     The zip_code_geo
     */
    public void setZipCodeGeo(Integer zipCodeGeo) {
        this.zipCodeGeo = zipCodeGeo;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The token
     */
    public String getToken() {
        return token;
    }

    /**
     * 
     * (Required)
     * 
     * @param token
     *     The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * trigger only. last update timestamp
     * (Required)
     * 
     * @return
     *     The timestamp
     */
    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
     * trigger only. last update timestamp
     * (Required)
     * 
     * @param timestamp
     *     The timestamp
     */
    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * saved during test result submit: ServerResource.getIP()
     * (Required)
     * 
     * @return
     *     The sourceIp
     */
    public String getSourceIp() {
        return sourceIp;
    }

    /**
     * saved during test result submit: ServerResource.getIP()
     * (Required)
     * 
     * @param sourceIp
     *     The source_ip
     */
    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The sourceIpAnonymized
     */
    public String getSourceIpAnonymized() {
        return sourceIpAnonymized;
    }

    /**
     * 
     * (Required)
     * 
     * @param sourceIpAnonymized
     *     The source_ip_anonymized
     */
    public void setSourceIpAnonymized(String sourceIpAnonymized) {
        this.sourceIpAnonymized = sourceIpAnonymized;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * 
     * (Required)
     * 
     * @param tag
     *     The tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The hiddenCode
     */
    public String getHiddenCode() {
        return hiddenCode;
    }

    /**
     * 
     * (Required)
     * 
     * @param hiddenCode
     *     The hidden_code
     */
    public void setHiddenCode(String hiddenCode) {
        this.hiddenCode = hiddenCode;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The data
     */
    public Object getData() {
        return data;
    }

    /**
     * 
     * (Required)
     * 
     * @param data
     *     The data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The publishPublicData
     */
    public Boolean getPublishPublicData() {
        return publishPublicData;
    }

    /**
     * 
     * (Required)
     * 
     * @param publishPublicData
     *     The publish_public_data
     */
    public void setPublishPublicData(Boolean publishPublicData) {
        this.publishPublicData = publishPublicData;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The gkz
     */
    public Integer getGkz() {
        return gkz;
    }

    /**
     * 
     * (Required)
     * 
     * @param gkz
     *     The gkz
     */
    public void setGkz(Integer gkz) {
        this.gkz = gkz;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The opendataSource
     */
    public String getOpendataSource() {
        return opendataSource;
    }

    /**
     * 
     * (Required)
     * 
     * @param opendataSource
     *     The opendata_source
     */
    public void setOpendataSource(String opendataSource) {
        this.opendataSource = opendataSource;
    }

    /**
     * 
     * @return
     *     The measurementClientInfo
     */
    public MeasurementClientInfo getClientInfo() {
        return clientInfo;
    }

    /**
     * 
     * @param measurementClientInfo
     *     The measurement_client_info
     */
    public void setClientInfo(MeasurementClientInfo measurementClientInfo) {
        this.clientInfo = measurementClientInfo;
    }

    /**
     * 
     * @return
     *     The measurementDeviceInfo
     */
    public MeasurementDeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    /**
     * 
     * @param measurementDeviceInfo
     *     The measurement_device_info
     */
    public void setDeviceInfo(MeasurementDeviceInfo measurementDeviceInfo) {
        this.deviceInfo = measurementDeviceInfo;
    }

    /**
     * remove network_ prefix for all fields
     * 
     * @return
     *     The measurementMobileNetworkInfo
     */
    public MeasurementMobileNetworkInfo getMobileNetworkInfo() {
        return mobileNetworkInfo;
    }

    /**
     * remove network_ prefix for all fields
     * 
     * @param measurementMobileNetworkInfo
     *     The measurement_mobile_network_info
     */
    public void setMobileNetworkInfo(MeasurementMobileNetworkInfo measurementMobileNetworkInfo) {
        this.mobileNetworkInfo = measurementMobileNetworkInfo;
    }

    /**
     * 
     * @return
     *     The measurementNetworkInfo
     */
    public MeasurementNetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    /**
     * 
     * @param measurementNetworkInfo
     *     The measurement_network_info
     */
    public void setNetworkInfo(MeasurementNetworkInfo measurementNetworkInfo) {
        this.networkInfo = measurementNetworkInfo;
    }

    /**
     * 
     * @return
     *     The measurementSpeedtest
     */
    public MeasurementSpeedtest getSpeedtest() {
        return speedtest;
    }

    /**
     * 
     * @param measurementSpeedtest
     *     The measurement_speedtest
     */
    public void setSpeedtest(MeasurementSpeedtest measurementSpeedtest) {
        this.speedtest = measurementSpeedtest;
    }
    
    /**
     * 
     * @return
     */
    public MeasurementQos getQos() {
		return qos;
	}
    
    /**
     * 
     * @param qos
     */
    public void setQos(MeasurementQos qos) {
		this.qos = qos;
	}

    /**
     * replaces geo_location table; previous version: geometry object. geo_lat, geo_long, geo_provider, geo_accuracy has been removed.
     * (Required)
     * 
     * @return
     *     The location
     */
    public List<GeoLocation> getLocations() {
        return locations;
    }

    /**
     * replaces geo_location table; previous version: geometry object. geo_lat, geo_long, geo_provider, geo_accuracy has been removed.
     * (Required)
     * 
     * @param location
     *     The location
     */
    public void setLocations(List<GeoLocation> locations) {
        this.locations = locations;
    }

    /**
     * replaces geometry from previous version, same object as in 'location'. real_geo_lat and real_geo_long are obsolete
     * (Required)
     * 
     * @return
     *     The realLocation
     */
    public GeoLocation getRealLocation() {
        return realLocation;
    }

    /**
     * replaces geometry from previous version, same object as in 'location'. real_geo_lat and real_geo_long are obsolete
     * (Required)
     * 
     * @param realLocation
     *     The real_location
     */
    public void setRealLocation(GeoLocation realLocation) {
        this.realLocation = realLocation;
    }

    /**
     * replaces signal table; previous version: integer
     * 
     * @return
     *     The measurementSignal
     */
    public List<Signal> getSignals() {
        return signals;
    }

    /**
     * replaces signal table; previous version: integer
     * 
     * @param measurementSignal
     *     The measurement_signal
     */
    public void setSignals(List<Signal> signals) {
        this.signals = signals;
    }

    /**
     * replaces cell_location table; the geo_location value is the same object as in 'location'
     * (Required)
     * 
     * @return
     *     The cellLocations
     */
    public List<CellLocation> getCellLocations() {
        return cellLocations;
    }

    /**
     * replaces cell_location table; the geo_location value is the same object as in 'location'
     * (Required)
     * 
     * @param cellLocations
     *     The cell_locations
     */
    public void setCellLocations(List<CellLocation> cellLocations) {
        this.cellLocations = cellLocations;
    }

    public MeasurementWifiInfo getWifiInfo() {
		return wifiInfo;
	}

	public void setWifiInfo(MeasurementWifiInfo wifiInfo) {
		this.wifiInfo = wifiInfo;
	}

	public Integer getWifiLinkSpeed() {
		return wifiLinkSpeed;
	}

	public void setWifiLinkSpeed(Integer wifiLinkSpeed) {
		this.wifiLinkSpeed = wifiLinkSpeed;
	}

	public Integer getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(Integer signalStrength) {
		this.signalStrength = signalStrength;
	}

	public Integer getLteRsrp() {
		return lteRsrp;
	}

	public void setLteRsrp(Integer lteRsrp) {
		this.lteRsrp = lteRsrp;
	}

	public Integer getLteRsrq() {
		return lteRsrq;
	}

	public void setLteRsrq(Integer lteRsrq) {
		this.lteRsrq = lteRsrq;
	}

	public ExtendedTestStat getExtendedTestStat() {
		return extendedTestStat;
	}

	public void setExtendedTestStat(ExtendedTestStat extendedTestStat) {
		this.extendedTestStat = extendedTestStat;
	}
	
	/////
	
	/**
	 * Creates a Measurement object and initializes all contained objects/lists.
	 * 
	 * @return
	 */
	public static Measurement create() {
		final Measurement m = new Measurement();
		
		m.setSpeedtest(new MeasurementSpeedtest());
		m.getSpeedtest().setTargetMeasurementServer(new TargetMeasurementServer());
		m.setClientInfo(new MeasurementClientInfo()); 
		m.setDeviceInfo(new MeasurementDeviceInfo());
		m.setNetworkInfo(new MeasurementNetworkInfo());
		m.setQos(new MeasurementQos());
		
		return m;
	}

	@Override
	public String toString() {
		return "Measurement [implausible=" + implausible + ", deleted=" + deleted + ", comment=" + comment
				+ ", openUuid=" + openUuid + ", openTestUuid=" + openTestUuid + ", zipCode=" + zipCode + ", zipCodeGeo="
				+ zipCodeGeo + ", token=" + token + ", timestamp=" + timestamp + ", sourceIp=" + sourceIp
				+ ", sourceIpAnonymized=" + sourceIpAnonymized + ", tag=" + tag + ", hiddenCode=" + hiddenCode
				+ ", data=" + data + ", publishPublicData=" + publishPublicData + ", gkz=" + gkz + ", opendataSource="
				+ opendataSource + ", clientInfo=" + clientInfo + ", deviceInfo=" + deviceInfo + ", mobileNetworkInfo="
				+ mobileNetworkInfo + ", networkInfo=" + networkInfo + ", speedtest=" + speedtest + ", qos=" + qos
				+ ", locations=" + locations + ", realLocation=" + realLocation + ", signals=" + signals
				+ ", cellLocations=" + cellLocations + ", wifiInfo=" + wifiInfo + ", wifiLinkSpeed=" + wifiLinkSpeed
				+ ", signalStrength=" + signalStrength + ", lteRsrp=" + lteRsrp + ", lteRsrq=" + lteRsrq
				+ ", extendedTestStat=" + extendedTestStat + ", anonymization=" + anonymization + "]";
	}
}