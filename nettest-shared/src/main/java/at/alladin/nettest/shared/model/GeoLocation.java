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
public class GeoLocation {

    /**
     * TODO: Location measurment time?
     * 
     * (Required)
     * 
     */
    @SerializedName("time")
    @Expose
    private DateTime time;
    

    /**
     * Location accuracy
     */
    @SerializedName("accuracy")
    @Expose
    private Double accuracy;

    /**
     * Location altitude
     */
    @SerializedName("altitude")
    @Expose
    private Double altitude;

    /**
     * Movement heading
     */
    @SerializedName("heading")
    @Expose
    private Double heading;

    /**
     * Movement speed
     */
    @SerializedName("speed")
    @Expose
    private Double speed;

    /**
     * Location provider
     */
    @SerializedName("provider")
    @Expose
    private String provider;
    
    /**
     * Latitude
     * 
     * (Required)
     * 
     */
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    
    /**
     * Longitude
     * 
     * (Required)
     * 
     */
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    
    /**
     * TODO: Position measurement time relative to test begin?
     * 
     * Renamed from time_ns
     * 
     * (Required)
     * 
     */
    @SerializedName("relative_time_ns")
    @Expose
    private Long relativeTimeNs;

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
     * @return
     *     The accuracy
     */
    public Double getAccuracy() {
        return accuracy;
    }

    /**
     * 
     * @param accuracy
     *     The accuracy
     */
    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * 
     * @return
     *     The altitude
     */
    public Double getAltitude() {
        return altitude;
    }

    /**
     * 
     * @param altitude
     *     The altitude
     */
    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    /**
     * 
     * @return
     *     The bearing
     */
    public Double getHeading() {
        return heading;
    }

    /**
     * 
     * @param bearing
     *     The bearing
     */
    public void setHeading(Double heading) {
        this.heading = heading;
    }

    /**
     * 
     * @return
     *     The speed
     */
    public Double getSpeed() {
        return speed;
    }

    /**
     * 
     * @param speed
     *     The speed
     */
    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    /**
     * 
     * @return
     *     The provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * 
     * @param provider
     *     The provider
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * 
     * (Required)
     * 
     * @param latitude
     *     The latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * 
     * (Required)
     * 
     * @param longitude
     *     The longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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
		result = prime * result + ((accuracy == null) ? 0 : accuracy.hashCode());
		result = prime * result + ((altitude == null) ? 0 : altitude.hashCode());
		result = prime * result + ((heading == null) ? 0 : heading.hashCode());
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + ((provider == null) ? 0 : provider.hashCode());
		result = prime * result + ((relativeTimeNs == null) ? 0 : relativeTimeNs.hashCode());
		result = prime * result + ((speed == null) ? 0 : speed.hashCode());
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
		GeoLocation other = (GeoLocation) obj;
		if (accuracy == null) {
			if (other.accuracy != null)
				return false;
		} else if (!accuracy.equals(other.accuracy))
			return false;
		if (altitude == null) {
			if (other.altitude != null)
				return false;
		} else if (!altitude.equals(other.altitude))
			return false;
		if (heading == null) {
			if (other.heading != null)
				return false;
		} else if (!heading.equals(other.heading))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		if (relativeTimeNs == null) {
			if (other.relativeTimeNs != null)
				return false;
		} else if (!relativeTimeNs.equals(other.relativeTimeNs))
			return false;
		if (speed == null) {
			if (other.speed != null)
				return false;
		} else if (!speed.equals(other.speed))
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
		return "GeoLocation [time=" + time + ", accuracy=" + accuracy + ", altitude=" + altitude + ", bearing="
				+ heading + ", speed=" + speed + ", provider=" + provider + ", latitude=" + latitude + ", longitude="
				+ longitude + ", relativeTimeNs=" + relativeTimeNs + "]";
	}
}