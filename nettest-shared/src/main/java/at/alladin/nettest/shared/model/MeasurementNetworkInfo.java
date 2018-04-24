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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.annotation.ExcludeFromOpenTest;
import at.alladin.nettest.shared.model.NetworkType.NetworkTypeName;

/**
 * 
 * @author lb@specure.com
 *
 */
@Generated("org.jsonschema2pojo")
public class MeasurementNetworkInfo {

    /**
     * Helperfunctions.getNatType()
     * 
     */
    @SerializedName("nat_type")
    @Expose
    private String natType;
    
    /**
     * ASN for public ip
     */
    @SerializedName(value="public_ip_asn", alternate="asn")
    @Expose
    private Long publicIpAsn;
    
    /**
     * Reverse dns for public ip
     */
    @SerializedName("public_ip_rdns")
    @ExcludeFromOpenTest
    @Expose
    private String publicIpRdns;
    
    /**
     * Name of AS number
     */
    @SerializedName("public_ip_as_name")
    @Expose
    private String publicIpAsName;
    
    /**
     * Public ip of client
     * 
     * (Required)
     * 
     */
    @SerializedName("client_public_ip")
    @ExcludeFromOpenTest
    @Expose
    private String clientPublicIp;
    
    /**
     * Anonymized public ip
     * 
     * (Required)
     * 
     */
    @SerializedName("client_public_ip_anonymized")
    @Expose
    private String clientPublicIpAnonymized;
    
    /**
     * network_group_name = cat_technology in OpenData export
     * 
     * (Required)
     * 
     */
    @SerializedName(value="network_group_name", alternate="cat_technology")
    @Expose
    private String networkGroupName;
    
    /**
     * network_group_type = network_type in OpenData export
     * 
     * (Required)
     * 
     */
    @SerializedName("network_group_type")
    @Expose
    private NetworkTypeName networkGroupType;
    
    /**
     * network type (NO foreign key for table: [network_type]). Interesting fact: network_group_type = network_type in OpenData export, network_group_name = cat_technology in OpenData export
     * 
     * (Required)
     * 
     */
    @SerializedName("network_type")
    @Expose
    private Integer networkType;
    
    /**
     * country_code derived from AS, eg. 'EU'
     */
    @SerializedName("country_asn")
    @Expose
    private String countryAsn;
    
    /**
     * country-code derived from geo_location, eg. 'DE'
     */
    @SerializedName("country_location")
    @Expose
    private String countryLocation;
    
    /**
     * country-code derived from public IP-address, eg. 'AT'
     */
    @SerializedName("country_geoip")
    @Expose
    private String countryGeoip;
    
    @Expose
    private Provider provider;
    
    /**
     * TelephonyManager.getDataState(), 0=DATA_DISCONNECTED, 1=DATA_CONNECTING, 2=DATA_CONNECTED, 3=DATA_SUSPENDED
     * 
     */
    @SerializedName("data_state")
    @Expose
    private Integer dataState;

    /**
     * Helperfunctions.getNatType()
     * 
     * @return
     *     The natType
     */
    public String getNatType() {
        return natType;
    }

    /**
     * Helperfunctions.getNatType()
     * 
     * @param natType
     *     The nat_type
     */
    public void setNatType(String natType) {
        this.natType = natType;
    }

    /**
     * 
     * @return
     *     The publicIpAsn
     */
    public Long getPublicIpAsn() {
        return publicIpAsn;
    }

    /**
     * 
     * @param publicIpAsn
     *     The public_ip_asn
     */
    public void setPublicIpAsn(Long publicIpAsn) {
        this.publicIpAsn = publicIpAsn;
    }

    /**
     * 
     * @return
     *     The publicIpRdns
     */
    public String getPublicIpRdns() {
        return publicIpRdns;
    }

    /**
     * 
     * @param publicIpRdns
     *     The public_ip_rdns
     */
    public void setPublicIpRdns(String publicIpRdns) {
        this.publicIpRdns = publicIpRdns;
    }

    /**
     * 
     * @return
     *     The publicIpAsName
     */
    public String getPublicIpAsName() {
        return publicIpAsName;
    }

    /**
     * 
     * @param publicIpAsName
     *     The public_ip_as_name
     */
    public void setPublicIpAsName(String publicIpAsName) {
        this.publicIpAsName = publicIpAsName;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The clientPublicIp
     */
    public String getClientPublicIp() {
        return clientPublicIp;
    }

    /**
     * 
     * (Required)
     * 
     * @param clientPublicIp
     *     The client_public_ip
     */
    public void setClientPublicIp(String clientPublicIp) {
        this.clientPublicIp = clientPublicIp;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The clientPublicIpAnonymized
     */
    public String getClientPublicIpAnonymized() {
        return clientPublicIpAnonymized;
    }

    /**
     * 
     * (Required)
     * 
     * @param clientPublicIpAnonymized
     *     The client_public_ip_anonymized
     */
    public void setClientPublicIpAnonymized(String clientPublicIpAnonymized) {
        this.clientPublicIpAnonymized = clientPublicIpAnonymized;
    }

    /**
     * network_group_name = cat_technology in OpenData export
     * (Required)
     * 
     * @return
     *     The networkGroupName
     */
    public String getNetworkGroupName() {
        return networkGroupName;
    }

    /**
     * network_group_name = cat_technology in OpenData export
     * (Required)
     * 
     * @param networkGroupName
     *     The network_group_name
     */
    public void setNetworkGroupName(String networkGroupName) {
        this.networkGroupName = networkGroupName;
    }

    /**
     * network_group_type = network_type in OpenData export
     * (Required)
     * 
     * @return
     *     The networkGroupType
     */
    public NetworkTypeName getNetworkGroupType() {
        return networkGroupType;
    }

    /**
     * network_group_type = network_type in OpenData export
     * (Required)
     * 
     * @param networkGroupType
     *     The network_group_type
     */
    public void setNetworkGroupType(NetworkTypeName networkGroupType) {
        this.networkGroupType = networkGroupType;
    }

    /**
     * network type (NO foreign key for table: [network_type]). Interesting fact: network_group_type = network_type in OpenData export, network_group_name = cat_technology in OpenData export
     * (Required)
     * 
     * @return
     *     The networkType
     */
    public Integer getNetworkType() {
        return networkType;
    }

    /**
     * network type (NO foreign key for table: [network_type]). Interesting fact: network_group_type = network_type in OpenData export, network_group_name = cat_technology in OpenData export
     * (Required)
     * 
     * @param networkType
     *     The network_type
     */
    public void setNetworkType(Integer networkType) {
        this.networkType = networkType;
    }

    /**
     * 
     * @return
     *     The countryAsn
     */
    public String getCountryAsn() {
        return countryAsn;
    }

    /**
     * 
     * @param countryAsn
     *     The country_asn
     */
    public void setCountryAsn(String countryAsn) {
        this.countryAsn = countryAsn;
    }

    /**
     * 
     * @return
     *     The countryLocation
     */
    public String getCountryLocation() {
        return countryLocation;
    }

    /**
     * 
     * @param countryLocation
     *     The country_location
     */
    public void setCountryLocation(String countryLocation) {
        this.countryLocation = countryLocation;
    }

    /**
     * 
     * @return
     *     The countryGeoip
     */
    public String getCountryGeoip() {
        return countryGeoip;
    }

    /**
     * 
     * @param countryGeoip
     *     The country_geoip
     */
    public void setCountryGeoip(String countryGeoip) {
        this.countryGeoip = countryGeoip;
    }

    public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	/**
     * TelephonyManager.getDataState(), 0=DATA_DISCONNECTED, 1=DATA_CONNECTING, 2=DATA_CONNECTED, 3=DATA_SUSPENDED
     * 
     * @return
     *     The dataState
     */
    public Integer getDataState() {
        return dataState;
    }

    /**
     * TelephonyManager.getDataState(), 0=DATA_DISCONNECTED, 1=DATA_CONNECTING, 2=DATA_CONNECTED, 3=DATA_SUSPENDED
     * 
     * @param dataState
     *     The data_state
     */
    public void setDataState(Integer dataState) {
        this.dataState = dataState;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientPublicIp == null) ? 0 : clientPublicIp.hashCode());
		result = prime * result + ((clientPublicIpAnonymized == null) ? 0 : clientPublicIpAnonymized.hashCode());
		result = prime * result + ((countryAsn == null) ? 0 : countryAsn.hashCode());
		result = prime * result + ((countryGeoip == null) ? 0 : countryGeoip.hashCode());
		result = prime * result + ((countryLocation == null) ? 0 : countryLocation.hashCode());
		result = prime * result + ((dataState == null) ? 0 : dataState.hashCode());
		result = prime * result + ((natType == null) ? 0 : natType.hashCode());
		result = prime * result + ((networkGroupName == null) ? 0 : networkGroupName.hashCode());
		result = prime * result + ((networkGroupType == null) ? 0 : networkGroupType.hashCode());
		result = prime * result + ((networkType == null) ? 0 : networkType.hashCode());
		result = prime * result + ((publicIpAsName == null) ? 0 : publicIpAsName.hashCode());
		result = prime * result + ((publicIpAsn == null) ? 0 : publicIpAsn.hashCode());
		result = prime * result + ((publicIpRdns == null) ? 0 : publicIpRdns.hashCode());
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
		MeasurementNetworkInfo other = (MeasurementNetworkInfo) obj;
		if (clientPublicIp == null) {
			if (other.clientPublicIp != null)
				return false;
		} else if (!clientPublicIp.equals(other.clientPublicIp))
			return false;
		if (clientPublicIpAnonymized == null) {
			if (other.clientPublicIpAnonymized != null)
				return false;
		} else if (!clientPublicIpAnonymized.equals(other.clientPublicIpAnonymized))
			return false;
		if (countryAsn == null) {
			if (other.countryAsn != null)
				return false;
		} else if (!countryAsn.equals(other.countryAsn))
			return false;
		if (countryGeoip == null) {
			if (other.countryGeoip != null)
				return false;
		} else if (!countryGeoip.equals(other.countryGeoip))
			return false;
		if (countryLocation == null) {
			if (other.countryLocation != null)
				return false;
		} else if (!countryLocation.equals(other.countryLocation))
			return false;
		if (dataState == null) {
			if (other.dataState != null)
				return false;
		} else if (!dataState.equals(other.dataState))
			return false;
		if (natType == null) {
			if (other.natType != null)
				return false;
		} else if (!natType.equals(other.natType))
			return false;
		if (networkGroupName == null) {
			if (other.networkGroupName != null)
				return false;
		} else if (!networkGroupName.equals(other.networkGroupName))
			return false;
		if (networkGroupType == null) {
			if (other.networkGroupType != null)
				return false;
		} else if (!networkGroupType.equals(other.networkGroupType))
			return false;
		if (networkType == null) {
			if (other.networkType != null)
				return false;
		} else if (!networkType.equals(other.networkType))
			return false;
		if (publicIpAsName == null) {
			if (other.publicIpAsName != null)
				return false;
		} else if (!publicIpAsName.equals(other.publicIpAsName))
			return false;
		if (publicIpAsn == null) {
			if (other.publicIpAsn != null)
				return false;
		} else if (!publicIpAsn.equals(other.publicIpAsn))
			return false;
		if (publicIpRdns == null) {
			if (other.publicIpRdns != null)
				return false;
		} else if (!publicIpRdns.equals(other.publicIpRdns))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MeasurementNetworkInfo [natType=" + natType + ", publicIpAsn=" + publicIpAsn + ", publicIpRdns="
				+ publicIpRdns + ", publicIpAsName=" + publicIpAsName + ", clientPublicIp=" + clientPublicIp
				+ ", clientPublicIpAnonymized=" + clientPublicIpAnonymized + ", networkGroupName=" + networkGroupName
				+ ", networkGroupType=" + networkGroupType + ", networkType=" + networkType + ", countryAsn="
				+ countryAsn + ", countryLocation=" + countryLocation + ", countryGeoip=" + countryGeoip
				+ ", dataState=" + dataState + "]";
	}
}