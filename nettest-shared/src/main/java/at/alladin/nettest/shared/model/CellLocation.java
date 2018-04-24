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

/**
 * 
 * @author lb@specure.com
 *
 */
@Generated("org.jsonschema2pojo")
public class CellLocation {

    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("area_code")
    @Expose
    private Integer areaCode;
    
    /**
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
    @SerializedName("primary_scrambling_code")
    @Expose
    private Integer primaryScramblingCode;
        
    /**
     * renamed from time_ns
     * (Required)
     * 
     */
    @SerializedName("relative_time_ns")
    @Expose
    private Long relativeTimeNs;


    public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	/**
     * 
     * (Required)
     * 
     * @return
     *     The areaCode
     */
    public Integer getAreaCode() {
        return areaCode;
    }

    /**
     * 
     * (Required)
     * 
     * @param areaCode
     *     The area_code
     */
    public void setAreaCode(Integer areaCode) {
        this.areaCode = areaCode;
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
     *     The primaryScramblingCode
     */
    public Integer getPrimaryScramblingCode() {
        return primaryScramblingCode;
    }

    /**
     * 
     * (Required)
     * 
     * @param primaryScramblingCode
     *     The primary_scrambling_code
     */
    public void setPrimaryScramblingCode(Integer primaryScramblingCode) {
        this.primaryScramblingCode = primaryScramblingCode;
    }

    /**
     * renamed from time_ns
     * (Required)
     * 
     * @return
     *     The relativeTimeNs
     */
    public Long getRelativeTimeNs() {
        return relativeTimeNs;
    }

    /**
     * renamed from time_ns
     * (Required)
     * 
     * @param relativeTimeNs
     *     The relative_time_ns
     */
    public void setRelativeTimeNs(Long relativeTimeNs) {
        this.relativeTimeNs = relativeTimeNs;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((areaCode == null) ? 0 : areaCode.hashCode());
		result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
		result = prime * result + ((primaryScramblingCode == null) ? 0 : primaryScramblingCode.hashCode());
		result = prime * result + ((relativeTimeNs == null) ? 0 : relativeTimeNs.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		CellLocation other = (CellLocation) obj;
		if (areaCode == null) {
			if (other.areaCode != null)
				return false;
		} else if (!areaCode.equals(other.areaCode))
			return false;
		if (locationId == null) {
			if (other.locationId != null)
				return false;
		} else if (!locationId.equals(other.locationId))
			return false;
		if (primaryScramblingCode == null) {
			if (other.primaryScramblingCode != null)
				return false;
		} else if (!primaryScramblingCode.equals(other.primaryScramblingCode))
			return false;
		if (relativeTimeNs == null) {
			if (other.relativeTimeNs != null)
				return false;
		} else if (!relativeTimeNs.equals(other.relativeTimeNs))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CellLocation [locationId=" + locationId + ", areaCode=" + areaCode + ", time=" + time
				+ ", primaryScramblingCode=" + primaryScramblingCode + ", relativeTimeNs=" + relativeTimeNs + "]";
	}
}