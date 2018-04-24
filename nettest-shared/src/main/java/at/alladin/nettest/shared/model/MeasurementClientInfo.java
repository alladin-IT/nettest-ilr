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

import javax.annotation.Generated;

import org.joda.time.DateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.annotation.ExcludeFromOpenTest;


/**
 * 
 * @author lb@specure.com
 *
 * client_id has been replaced by this object, also no client_id is stored anymore. instead the client's uuid is stored in this document. all fields' keys inside this object had a client_ prefix that has been removed
 */
@Generated("org.jsonschema2pojo")
public class MeasurementClientInfo {

    /**
     * formerly: id. client_id has been replaced by this object, also no client_id is stored anymore. instead the client's uuid is stored in this document
     * (Required)
     * 
     */
	@ExcludeFromOpenTest
    @SerializedName("uuid")
    @Expose
    private String uuid;
    
    /**
     * on Android: contains value of Config.RMBT_VERSION_NUMBER
     * (Required)
     * 
     */
    @SerializedName("software_version")
    @Expose
    private String softwareVersion;
    
    /**
     * 
     */
    @SerializedName("software_version_code")
    @Expose
    private Integer softwareVersionCode;
    
    /**
     * mobile: RMBT / browser: RMBTws
     * 
     * (Required)
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    
    /**
     * Language of client
     * 
     * (Required)
     * 
     */
    @SerializedName("language")
    @Expose
    private String language;
    
    /**
     * removed? Helperfunctions, L238: // obsoleted by removal of old client_local_ip column
     * 
     */
    @SerializedName("local_ip")
    @Expose
    private String localIp;
    
    /**
     * client's local ip; v0 json key in transmitted results: test_ip_local
     * (Required)
     * 
     */
    @ExcludeFromOpenTest
    @SerializedName("ip_local")
    @Expose
    private String ipLocal;
    
    /**
     * anonymized local ip
     */
    @SerializedName("ip_local_anonymized")
    @Expose
    private String ipLocalAnonymized;
    
    /**
     * Helperfunctions.IpType(client_ip_local)
     * (Required)
     * 
     */
    @SerializedName("ip_local_type")
    @Expose
    private String ipLocalType;
    
    /**
     * Test time
     * 
     * (Required)
     * 
     */
    @SerializedName("time")
    @Expose
    private DateTime time;
    
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("test_counter")
    @Expose
    private Integer testCounter;
    
    /**
     * Status of previous test of same client
     * 
     * (Required)
     * 
     */
    @SerializedName("previous_test_status")
    @Expose
    private String previousTestStatus;
    
    /**
     * Revision of client code
     * 
     * (Required)
     * 
     */
    @SerializedName("software_revision")
    @Expose
    private String softwareRevision;

    /**
     * protocol version
     */
    @SerializedName("version")
    @Expose
    private String version;

    /**
     * Type of client
     */
    @SerializedName("type")
    @Expose
    private Client.ClientType type;
    
    /**
     * timezone string, like: 'Europe/Vienna' or 'America/Chicago'
     * 
     * (Required)
     * 
     */
    @SerializedName("timezone")
    @Expose
    private String timezone;
    
    /**
     * feature request IRL (additional client provider contract information)
     */
    @SerializedName("contract_info")
    @Expose
    private ClientContractInformation clientContractInformation;

    @Expose
    private String position;
    
    /**
     * formerly: id. client_id has been replaced by this object, also no client_id is stored anymore. instead the client's uuid is stored in this document
     * (Required)
     * 
     * @return
     *     The uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * formerly: id. client_id has been replaced by this object, also no client_id is stored anymore. instead the client's uuid is stored in this document
     * (Required)
     * 
     * @param uuid
     *     The uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * on Android: contains value of Config.RMBT_VERSION_NUMBER
     * (Required)
     * 
     * @return
     *     The version
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * on Android: contains value of Config.RMBT_VERSION_NUMBER
     * (Required)
     * 
     * @param version
     *     The version
     */
    public void setSoftwareVersion(String version) {
        this.softwareVersion = version;
    }

    /**
     * 
     * @return
     */
    public Integer getSoftwareVersionCode() {
		return softwareVersionCode;
	}
    
    /**
     * 
     * @param softwareVersionCode
     */
    public void setSoftwareVersionCode(Integer softwareVersionCode) {
		this.softwareVersionCode = softwareVersionCode;
	}
    
    /**
     * 
     * (Required)
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * (Required)
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 
     * (Required)
     * 
     * @param language
     *     The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * removed? Helperfunctions, L238: // obsoleted by removal of old client_local_ip column
     * 
     * @return
     *     The localIp
     */
    public String getLocalIp() {
        return localIp;
    }

    /**
     * removed? Helperfunctions, L238: // obsoleted by removal of old client_local_ip column
     * 
     * @param localIp
     *     The local_ip
     */
    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    /**
     * client's local ip; v0 json key in trasmitted results: test_ip_local
     * (Required)
     * 
     * @return
     *     The ipLocal
     */
    public String getIpLocal() {
        return ipLocal;
    }

    /**
     * client's local ip; v0 json key in trasmitted results: test_ip_local
     * (Required)
     * 
     * @param ipLocal
     *     The ip_local
     */
    public void setIpLocal(String ipLocal) {
        this.ipLocal = ipLocal;
    }

    /**
     * 
     * @return
     *     The ipLocalAnonymized
     */
    public String getIpLocalAnonymized() {
        return ipLocalAnonymized;
    }

    /**
     * 
     * @param ipLocalAnonymized
     *     The ip_local_anonymized
     */
    public void setIpLocalAnonymized(String ipLocalAnonymized) {
        this.ipLocalAnonymized = ipLocalAnonymized;
    }

    /**
     * Helperfunctions.IpType(client_ip_local)
     * (Required)
     * 
     * @return
     *     The ipLocalType
     */
    public String getIpLocalType() {
        return ipLocalType;
    }

    /**
     * Helperfunctions.IpType(client_ip_local)
     * (Required)
     * 
     * @param ipLocalType
     *     The ip_local_type
     */
    public void setIpLocalType(String ipLocalType) {
        this.ipLocalType = ipLocalType;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The time
     */
    public DateTime getTime() {
        return time;
    }

    /**
     * 
     * (Required)
     * 
     * @param time
     *     The time
     */
    public void setTime(DateTime time) {
        this.time = time;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The testCounter
     */
    public Integer getTestCounter() {
        return testCounter;
    }

    /**
     * 
     * (Required)
     * 
     * @param testCounter
     *     The test_counter
     */
    public void setTestCounter(Integer testCounter) {
        this.testCounter = testCounter;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The previousTestStatus
     */
    public String getPreviousTestStatus() {
        return previousTestStatus;
    }

    /**
     * 
     * (Required)
     * 
     * @param previousTestStatus
     *     The previous_test_status
     */
    public void setPreviousTestStatus(String previousTestStatus) {
        this.previousTestStatus = previousTestStatus;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The softwareRevision
     */
    public String getSoftwareRevision() {
        return softwareRevision;
    }

    /**
     * 
     * (Required)
     * 
     * @param softwareRevision
     *     The software_revision
     */
    public void setSoftwareRevision(String softwareRevision) {
        this.softwareRevision = softwareRevision;
    }

	public Client.ClientType getType() {
		return type;
	}

	public void setType(Client.ClientType type) {
		this.type = type;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
    /**
     * timezone string, like: 'Europe/Vienna' or 'America/Chicago'
     * (Required)
     * 
     * @return
     *     The timezone
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * timezone string, like: 'Europe/Vienna' or 'America/Chicago'
     * (Required)
     * 
     * @param timezone
     *     The timezone
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

	public ClientContractInformation getClientContractInformation() {
		return clientContractInformation;
	}

	public void setClientContractInformation(ClientContractInformation clientContractInformation) {
		this.clientContractInformation = clientContractInformation;
	}
	
	public String getPosition() {
		return position;
	}
	
	public void setPosition(String position) {
		this.position = position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ipLocal == null) ? 0 : ipLocal.hashCode());
		result = prime * result + ((ipLocalAnonymized == null) ? 0 : ipLocalAnonymized.hashCode());
		result = prime * result + ((ipLocalType == null) ? 0 : ipLocalType.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((localIp == null) ? 0 : localIp.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((previousTestStatus == null) ? 0 : previousTestStatus.hashCode());
		result = prime * result + ((softwareRevision == null) ? 0 : softwareRevision.hashCode());
		result = prime * result + ((softwareVersion == null) ? 0 : softwareVersion.hashCode());
		result = prime * result + ((testCounter == null) ? 0 : testCounter.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((timezone == null) ? 0 : timezone.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		MeasurementClientInfo other = (MeasurementClientInfo) obj;
		if (ipLocal == null) {
			if (other.ipLocal != null)
				return false;
		} else if (!ipLocal.equals(other.ipLocal))
			return false;
		if (ipLocalAnonymized == null) {
			if (other.ipLocalAnonymized != null)
				return false;
		} else if (!ipLocalAnonymized.equals(other.ipLocalAnonymized))
			return false;
		if (ipLocalType == null) {
			if (other.ipLocalType != null)
				return false;
		} else if (!ipLocalType.equals(other.ipLocalType))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (localIp == null) {
			if (other.localIp != null)
				return false;
		} else if (!localIp.equals(other.localIp))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (previousTestStatus == null) {
			if (other.previousTestStatus != null)
				return false;
		} else if (!previousTestStatus.equals(other.previousTestStatus))
			return false;
		if (softwareRevision == null) {
			if (other.softwareRevision != null)
				return false;
		} else if (!softwareRevision.equals(other.softwareRevision))
			return false;
		if (softwareVersion == null) {
			if (other.softwareVersion != null)
				return false;
		} else if (!softwareVersion.equals(other.softwareVersion))
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
		if (timezone == null) {
			if (other.timezone != null)
				return false;
		} else if (!timezone.equals(other.timezone))
			return false;
		if (type != other.type)
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
		return "MeasurementClientInfo [uuid=" + uuid + ", softwareVersion=" + softwareVersion + ", softwareVersionCode="
				+ softwareVersionCode + ", name=" + name + ", language=" + language + ", localIp=" + localIp
				+ ", ipLocal=" + ipLocal + ", ipLocalAnonymized=" + ipLocalAnonymized + ", ipLocalType=" + ipLocalType
				+ ", time=" + time + ", testCounter=" + testCounter + ", previousTestStatus=" + previousTestStatus
				+ ", softwareRevision=" + softwareRevision + ", version=" + version + ", type=" + type + ", timezone="
				+ timezone + ", clientContractInformation=" + clientContractInformation + "]";
	}
}