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

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.model.ClientContractInformation;
import at.alladin.nettest.shared.model.GeoLocation;
import at.alladin.nettest.shared.model.MeasurementServer;

@Generated("org.jsonschema2pojo")
public class MeasurementRequest extends BasicRequestImpl {

	/**
	 * 
	 */
	@SerializedName("measurement_server")
	@Expose
	private MeasurementServer measurementServer; // TODO: why? this isn't sent to the client

	/**
	 * 
	 */
	@SerializedName("ndt")
    @Expose
    private Boolean ndt = false;
	
	/**
	 * 
	 */
	@SerializedName("anonymous")
    @Expose
	private boolean anonymous = false;
    
    /**
     * renamed from testCounter
     * 
     */
    @SerializedName("test_counter")
    @Expose
    private Integer testCounter;
    
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("version")
    @Expose
    private String version; // TODO: this is already included in BasicRequest?
    @SerializedName("geo_location")
    @Expose
    private GeoLocation geoLocation;
    
    @SerializedName("time")
    @Expose
    private Long time;
    
    @SerializedName("contract_info")
    @Expose
    private ClientContractInformation clientContractInformation;

    @SerializedName("position")
    @Expose
    private String advancedPosition;
    
	public MeasurementServer getMeasurementServer() {
		return measurementServer;
	}
	
	public void setMeasurementServer(MeasurementServer measurementServer) {
		this.measurementServer = measurementServer;
	}
	
	public Boolean getNdt() {
		return ndt;
	}
	
	public void setNdt(Boolean ndt) {
		this.ndt = ndt;
	}
	
	public Boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(Boolean anonymous) {
		this.anonymous = anonymous;
	}

	public Integer getTestCounter() {
		return testCounter;
	}
	
	public void setTestCounter(Integer testCounter) {
		this.testCounter = testCounter;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public GeoLocation getGeoLocation() {
		return geoLocation;
	}
	
	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
	}
	
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public ClientContractInformation getClientContractInformation() {
		return clientContractInformation;
	}

	public void setClientContractInformation(ClientContractInformation clientContractInformation) {
		this.clientContractInformation = clientContractInformation;
	}
	
	public String getAdvancedPosition() {
		return advancedPosition;
	}
	
	public void setAdvancedPosition(String advancedPosition) {
		this.advancedPosition = advancedPosition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((geoLocation == null) ? 0 : geoLocation.hashCode());
		result = prime * result + ((measurementServer == null) ? 0 : measurementServer.hashCode());
		result = prime * result + ((ndt == null) ? 0 : ndt.hashCode());
		result = prime * result + ((testCounter == null) ? 0 : testCounter.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeasurementRequest other = (MeasurementRequest) obj;
		if (geoLocation == null) {
			if (other.geoLocation != null)
				return false;
		} else if (!geoLocation.equals(other.geoLocation))
			return false;
		if (measurementServer == null) {
			if (other.measurementServer != null)
				return false;
		} else if (!measurementServer.equals(other.measurementServer))
			return false;
		if (ndt == null) {
			if (other.ndt != null)
				return false;
		} else if (!ndt.equals(other.ndt))
			return false;
		if (testCounter == null) {
			if (other.testCounter != null)
				return false;
		} else if (!testCounter.equals(other.testCounter))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "MeasurementRequest [measurementServer=" + measurementServer + ", ndt=" + ndt + ", anonymous="
				+ anonymous + ", testCounter=" + testCounter + ", uuid=" + uuid + ", version=" + version
				+ ", geoLocation=" + geoLocation + ", time=" + time + ", clientContractInformation="
				+ clientContractInformation + "]";
	}
}