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


/**
 * all network_ prefixes have been removed in this object 
 * @author lb@specure.com
 *
 */
@Generated("org.jsonschema2pojo")
public class MeasurementMobileNetworkInfo {

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("country")
    @Expose
    private String country;
    
    /**
     * result json key: telephony_network_operator
     * Equal to the network_mcc_mnc
     * (Required)
     * 
     */
    @SerializedName("operator")
    @Expose
    private String operator;
    
    /**
     * result json key telephony_network_operator_name
     * (Required)
     * 
     */
    @SerializedName("operator_name")
    @Expose
    private String operatorName;
    
    /**
     * result json key: telephony_network_sim_country
     * (internal) Android-only, country derived by client from SIM (country of home network)
     * (Required)
     * 
     */
    @SerializedName("sim_country")
    @Expose
    private String simCountry;
    
    /**
     * result json key: telephony_network_sim_operator
     * Equal to the sim_mcc_mnc
     * (Required)
     * 
     */
    @SerializedName("sim_operator")
    @Expose
    private String simOperator;
    
    /**
     * result json key: telephony_network_sim_operator_name
     * (Required)
     * 
     */
    @SerializedName("sim_operator_name")
    @Expose
    private String simOperatorName;
    
    /**
     * trigger only. too complex to explain in words. the sql inside the trigger (L93-102) will tell you more.
     * 
     */
    @SerializedName("mobile_provider")
    @Expose
    private Provider mobileProvider;
    
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("network_is_roaming")
    @Expose
    private Boolean networkIsRoaming;
    
    /**
     * trigger only. 0=no roaming, 1=national, 2=international.
     * 
     */
    @SerializedName("roaming_type")
    @Expose
    private Integer roamingType;
    

    /**
     * 
     * (Required)
     * 
     * @return
     *     The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * 
     * (Required)
     * 
     * @param country
     *     The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * result json key: telephony_network_operator
     * (Required)
     * 
     * @return
     *     The operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * result json key: telephony_network_operator
     * (Required)
     * 
     * @param operator
     *     The operator
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * result json key telephony_network_operator_name
     * (Required)
     * 
     * @return
     *     The operatorName
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * esult json key telephony_network_operator_name
     * (Required)
     * 
     * @param operatorName
     *     The operator_name
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * result json key: telephony_network_sim_country
     * (Required)
     * 
     * @return
     *     The simCountry
     */
    public String getSimCountry() {
        return simCountry;
    }

    /**
     * result json key: telephony_network_sim_country
     * (Required)
     * 
     * @param simCountry
     *     The sim_country
     */
    public void setSimCountry(String simCountry) {
        this.simCountry = simCountry;
    }

    /**
     * result json key: telephony_network_sim_operator
     * (Required)
     * 
     * @return
     *     The simOperator
     */
    public String getSimOperator() {
        return simOperator;
    }

    /**
     * result json key: telephony_network_sim_operator
     * (Required)
     * 
     * @param simOperator
     *     The sim_operator
     */
    public void setSimOperator(String simOperator) {
        this.simOperator = simOperator;
    }

    /**
     * result json key: telephony_network_sim_operator_name
     * (Required)
     * 
     * @return
     *     The simOperatorName
     */
    public String getSimOperatorName() {
        return simOperatorName;
    }

    /**
     * result json key: telephony_network_sim_operator_name
     * (Required)
     * 
     * @param simOperatorName
     *     The sim_operator_name
     */
    public void setSimOperatorName(String simOperatorName) {
        this.simOperatorName = simOperatorName;
    }

    /**
     * trigger only. too complex to explain in words. the sql inside the trigger (L93-102) will tell you more.
     * 
     * @return
     *     The mobileProvider
     */
    public Provider getMobileProvider() {
        return mobileProvider;
    }

    /**
     * trigger only. too complex to explain in words. the sql inside the trigger (L93-102) will tell you more.
     * 
     * @param mobileProvider
     *     The mobile_provider
     */
    public void setMobileProvider(Provider mobileProvider) {
        this.mobileProvider = mobileProvider;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The networkIsRoaming
     */
    public Boolean getNetworkIsRoaming() {
        return networkIsRoaming;
    }

    /**
     * 
     * (Required)
     * 
     * @param networkIsRoaming
     *     The network_is_roaming
     */
    public void setNetworkIsRoaming(Boolean networkIsRoaming) {
        this.networkIsRoaming = networkIsRoaming;
    }

    /**
     * trigger only. 0=no roaming, 1=national, 2=international.
     * 
     * @return
     *     The roamingType
     */
    public Integer getRoamingType() {
        return roamingType;
    }

    /**
     * trigger only. 0=no roaming, 1=national, 2=international.
     * 
     * @param roamingType
     *     The roaming_type
     */
    public void setRoamingType(Integer roamingType) {
        this.roamingType = roamingType;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((mobileProvider == null) ? 0 : mobileProvider.hashCode());
		result = prime * result + ((networkIsRoaming == null) ? 0 : networkIsRoaming.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((operatorName == null) ? 0 : operatorName.hashCode());
		result = prime * result + ((roamingType == null) ? 0 : roamingType.hashCode());
		result = prime * result + ((simCountry == null) ? 0 : simCountry.hashCode());
		result = prime * result + ((simOperator == null) ? 0 : simOperator.hashCode());
		result = prime * result + ((simOperatorName == null) ? 0 : simOperatorName.hashCode());
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
		MeasurementMobileNetworkInfo other = (MeasurementMobileNetworkInfo) obj;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (mobileProvider == null) {
			if (other.mobileProvider != null)
				return false;
		} else if (!mobileProvider.equals(other.mobileProvider))
			return false;
		if (networkIsRoaming == null) {
			if (other.networkIsRoaming != null)
				return false;
		} else if (!networkIsRoaming.equals(other.networkIsRoaming))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (operatorName == null) {
			if (other.operatorName != null)
				return false;
		} else if (!operatorName.equals(other.operatorName))
			return false;
		if (roamingType == null) {
			if (other.roamingType != null)
				return false;
		} else if (!roamingType.equals(other.roamingType))
			return false;
		if (simCountry == null) {
			if (other.simCountry != null)
				return false;
		} else if (!simCountry.equals(other.simCountry))
			return false;
		if (simOperator == null) {
			if (other.simOperator != null)
				return false;
		} else if (!simOperator.equals(other.simOperator))
			return false;
		if (simOperatorName == null) {
			if (other.simOperatorName != null)
				return false;
		} else if (!simOperatorName.equals(other.simOperatorName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MeasurementMobileNetworkInfo [country=" + country + ", operator=" + operator + ", operatorName="
				+ operatorName + ", simCountry=" + simCountry + ", simOperator=" + simOperator + ", simOperatorName="
				+ simOperatorName + ", mobileProvider=" + mobileProvider + ", networkIsRoaming=" + networkIsRoaming
				+ ", roamingType=" + roamingType + "]";
	}
}