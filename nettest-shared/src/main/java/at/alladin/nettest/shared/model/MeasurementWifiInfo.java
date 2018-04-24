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


/**
 * group all wifi info into one object. all wifi_ prefixes have been removed
 * @author lb@specure.com
 *
 */
@Generated("org.jsonschema2pojo")
public class MeasurementWifiInfo {

    /**
     * on Android: WifiInfo.getSSID()
     * (Required)
     * TODO: need to exclude ssid?
     */
	@ExcludeFromOpenTest
    @SerializedName("ssid")
    @Expose
    private String ssid;
    
    /**
     * on Android: WifiInfo.getBSSID()
     * (Required)
     * TODO: need to exclude bssid?
     */
	@ExcludeFromOpenTest
    @SerializedName("bssid")
    @Expose
    private String bssid;
    
    /**
     * on Android: WifiInfo.getNetworkId()
     * (Required)
     * 
     */
    @SerializedName("network_id")
    @Expose
    private String networkId;

    /**
     * on Android: WifiInfo.getSSID()
     * (Required)
     * 
     * @return
     *     The ssid
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * on Android: WifiInfo.getSSID()
     * (Required)
     * 
     * @param ssid
     *     The ssid
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * on Android: WifiInfo.getBSSID()
     * (Required)
     * 
     * @return
     *     The bssid
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * on Android: WifiInfo.getBSSID()
     * (Required)
     * 
     * @param bssid
     *     The bssid
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * on Android: WifiInfo.getNetworkId()
     * (Required)
     * 
     * @return
     *     The networkId
     */
    public String getNetworkId() {
        return networkId;
    }

    /**
     * on Android: WifiInfo.getNetworkId()
     * (Required)
     * 
     * @param networkId
     *     The network_id
     */
    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bssid == null) ? 0 : bssid.hashCode());
		result = prime * result + ((networkId == null) ? 0 : networkId.hashCode());
		result = prime * result + ((ssid == null) ? 0 : ssid.hashCode());
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
		MeasurementWifiInfo other = (MeasurementWifiInfo) obj;
		if (bssid == null) {
			if (other.bssid != null)
				return false;
		} else if (!bssid.equals(other.bssid))
			return false;
		if (networkId == null) {
			if (other.networkId != null)
				return false;
		} else if (!networkId.equals(other.networkId))
			return false;
		if (ssid == null) {
			if (other.ssid != null)
				return false;
		} else if (!ssid.equals(other.ssid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MeasurementWifiInfo [ssid=" + ssid + ", bssid=" + bssid + ", networkId=" + networkId + "]";
	}
}