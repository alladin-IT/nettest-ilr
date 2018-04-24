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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.UuidAwareEntity;

/**
 * 
 * @author lb@specure.com
 *
 */
@Generated("org.jsonschema2pojo")
public class MeasurementServer extends UuidAwareEntity {

	private static final long serialVersionUID = -2505324068173954284L;
	
	/**
     * 
     */
    @Expose
    private String name;
    
    /**
     * 
     */
    @SerializedName("web_address")
    @Expose
    private String webAddress;
    
    /**
     * 
     */
    @Expose
    private Integer port;
    
    /**
     * 
     */
    @SerializedName("port_ssl")
    @Expose
    private Integer portSsl;
    
    /**
     * 
     */
    @Expose
    private String city;
    
    /**
     * 
     */
    @Expose
    private String country;
    
    /**
     * 
     */
    @SerializedName("geo_location")
    @Expose
    private GeoLocation geoLocation;
    
    /**
     * 
     */
    @SerializedName("web_address_ipv4")
    @Expose
    private String webAddressIpv4;
    
    /**
     * 
     */
    @SerializedName("web_address_ipv6")
    @Expose
    private String webAddressIpv6;
    
    /**
     * 
     */
    @SerializedName("server_type")
    @Expose
    private Type serverType;
    
    /**
     * 
     */
    @Expose
    private Integer priority;
    
    /**
     * 
     */
    @Expose
    private Integer weight;

    /**
     * 
     * (Required)
     * 
     */
    @Expose
    private Boolean active;
    
    /**
     * 
     */
    @SerializedName("secret_key")
    @Expose
    private String secretKey;
    
    /**
     * 
     */
    @Expose
    private Boolean selectable;
    
    /**
     * 
     */
    @Expose
    private List<String> countries = new ArrayList<>();
    
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
     *     The webAddress
     */
    public String getWebAddress() {
        return webAddress;
    }

    /**
     * 
     * @param webAddress
     *     The web_address
     */
    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
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
     *     The portSsl
     */
    public Integer getPortSsl() {
        return portSsl;
    }

    /**
     * 
     * @param portSsl
     *     The port_ssl
     */
    public void setPortSsl(Integer portSsl) {
        this.portSsl = portSsl;
    }

    /**
     * 
     * @return
     *     The city
     */
    public String getCity() {
        return city;
    }

    /**
     * 
     * @param city
     *     The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 
     * @return
     *     The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * 
     * @param country
     *     The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 
     * @return
     *     The geoLocation
     */
    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    /**
     * 
     * @param geoLocation
     *     The geo_location
     */
    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    /**
     * 
     * @return
     *     The webAddressIpv4
     */
    public String getWebAddressIpv4() {
        return webAddressIpv4;
    }

    /**
     * 
     * @param webAddressIpv4
     *     The web_address_ipv4
     */
    public void setWebAddressIpv4(String webAddressIpv4) {
        this.webAddressIpv4 = webAddressIpv4;
    }

    /**
     * 
     * @return
     *     The webAddressIpv6
     */
    public String getWebAddressIpv6() {
        return webAddressIpv6;
    }

    /**
     * 
     * @param webAddressIpv6
     *     The web_address_ipv6
     */
    public void setWebAddressIpv6(String webAddressIpv6) {
        this.webAddressIpv6 = webAddressIpv6;
    }

    /**
     * 
     * @return
     *     The serverType
     */
    public Type getServerType() {
        return serverType;
    }

    /**
     * 
     * @param serverType
     *     The server_type
     */
    public void setServerType(Type serverType) {
        this.serverType = serverType;
    }

    /**
     * 
     * @return
     *     The priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * 
     * @param priority
     *     The priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * 
     * @return
     *     The weight
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * 
     * @param weight
     *     The weight
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    /**
     * 
     * @return
     *     The active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * 
     * @param active
     *     The active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * 
     * @return
     *     The key
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 
     * @param key
     *     The key
     */
    public void setSecretKey(String key) {
        this.secretKey = key;
    }

    /**
     * 
     * @return
     *     The selectable
     */
    public Boolean getSelectable() {
        return selectable;
    }

    /**
     * 
     * @param selectable
     *     The selectable
     */
    public void setSelectable(Boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * 
     * @return
     *     The countries
     */
    public List<String> getCountries() {
        return countries;
    }

    /**
     * 
     * @param countries
     *     The countries
     */
    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((countries == null) ? 0 : countries.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((geoLocation == null) ? 0 : geoLocation.hashCode());
		result = prime * result + ((secretKey == null) ? 0 : secretKey.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((portSsl == null) ? 0 : portSsl.hashCode());
		result = prime * result + ((priority == null) ? 0 : priority.hashCode());
		result = prime * result + ((selectable == null) ? 0 : selectable.hashCode());
		result = prime * result + ((serverType == null) ? 0 : serverType.hashCode());
		result = prime * result + ((webAddress == null) ? 0 : webAddress.hashCode());
		result = prime * result + ((webAddressIpv4 == null) ? 0 : webAddressIpv4.hashCode());
		result = prime * result + ((webAddressIpv6 == null) ? 0 : webAddressIpv6.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeasurementServer other = (MeasurementServer) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (countries == null) {
			if (other.countries != null)
				return false;
		} else if (!countries.equals(other.countries))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (geoLocation == null) {
			if (other.geoLocation != null)
				return false;
		} else if (!geoLocation.equals(other.geoLocation))
			return false;
		if (secretKey == null) {
			if (other.secretKey != null)
				return false;
		} else if (!secretKey.equals(other.secretKey))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (portSsl == null) {
			if (other.portSsl != null)
				return false;
		} else if (!portSsl.equals(other.portSsl))
			return false;
		if (priority == null) {
			if (other.priority != null)
				return false;
		} else if (!priority.equals(other.priority))
			return false;
		if (selectable == null) {
			if (other.selectable != null)
				return false;
		} else if (!selectable.equals(other.selectable))
			return false;
		if (serverType == null) {
			if (other.serverType != null)
				return false;
		} else if (!serverType.equals(other.serverType))
			return false;
		if (webAddress == null) {
			if (other.webAddress != null)
				return false;
		} else if (!webAddress.equals(other.webAddress))
			return false;
		if (webAddressIpv4 == null) {
			if (other.webAddressIpv4 != null)
				return false;
		} else if (!webAddressIpv4.equals(other.webAddressIpv4))
			return false;
		if (webAddressIpv6 == null) {
			if (other.webAddressIpv6 != null)
				return false;
		} else if (!webAddressIpv6.equals(other.webAddressIpv6))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TestServer [name=" + name + ", webAddress=" + webAddress + ", port=" + port + ", portSsl=" + portSsl
				+ ", city=" + city + ", country=" + country + ", geoLocation=" + geoLocation + ", webAddressIpv4="
				+ webAddressIpv4 + ", webAddressIpv6=" + webAddressIpv6 + ", serverType=" + serverType + ", priority="
				+ priority + ", weight=" + weight + ", active=" + active + ", key=" + secretKey + ", selectable=" + selectable
				+ ", countries=" + countries + "]";
	}


	/**
	 * 
	 * @author alladin-IT GmbH (fj@alladin.at)
	 *
	 */
	public static enum Type {
		
		@SerializedName("RMBT")
		SPEED_TCP("RMBT"),
		
		@SerializedName("RMBTws")
		SPEED_WEBSOCKET("RMBTws"),
		
		@SerializedName("RMBTqos")
		QOS("RMBTqos");
		
		/**
         * 
         */
        private final String value;
        
        /**
         * 
         */
        private final static Map<String, Type> CONSTANTS = new HashMap<>();

        /**
         * 
         */
        static {
            for (Type c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        /**
         * 
         * @param value
         */
        private Type(String value) {
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
        public static Type fromValue(String value) {
            final Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
	}
}