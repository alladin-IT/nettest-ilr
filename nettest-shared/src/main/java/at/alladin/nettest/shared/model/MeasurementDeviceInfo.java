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
 * 
 * @author lb@specure.com
 *
 */
@Generated("org.jsonschema2pojo")
public class MeasurementDeviceInfo {

    /**
     * currently used: 'CLI'/'Android'/Applet/iOS/[from client_name: RMBTws, RMBTjs](null)
     * (null) is used for RMBTjs 
     * (Required)
     * 
     */
    @SerializedName("platform")
    @Expose
    private String platform;
    
    /**
     * Version of operating system
     * 
     * (Required)
     * 
     */
    @SerializedName("os_version")
    @Expose
    private String osVersion;
    
    /**
     * Android specific
     * 
     * (Required)
     * 
     */
    @SerializedName("api_level")
    @Expose
    private String apiLevel;
    
    /**
     * Device used for test
     * 
     * (Required)
     * 
     */
    @SerializedName("device")
    @Expose
    private String device;
    
    /**
     * Detailed device designation
     */
    @SerializedName("model")
    @Expose
    private String model;
    
    /**
     * Full device name
     */
    @SerializedName(value="fullname", alternate="model_native")
    @Expose
    private String fullname;
    
    /**
     * product used for test:
     * 	Android APO 'product'
     * 	iOS: '' (not used)
     * 	Browser: same as for model (browser name)
     * (Required)
     * 
     */
    @SerializedName("product")
    @Expose
    private String product;
    
    /**
     * looks like it's obsolete (used in old OpenTestResource.java) - some kind of old value for 'internal' radio type.
     * 
     */
    @SerializedName("phone_type")
    @Expose
    private Long phoneType;

    /**
     * 
     * (Required)
     * 
     * @return
     *     The platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 
     * (Required)
     * 
     * @param platform
     *     The platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The osVersion
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * 
     * (Required)
     * 
     * @param osVersion
     *     The os_version
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The apiLevel
     */
    public String getApiLevel() {
        return apiLevel;
    }

    /**
     * 
     * (Required)
     * 
     * @param apiLevel
     *     The api_level
     */
    public void setApiLevel(String apiLevel) {
        this.apiLevel = apiLevel;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The device
     */
    public String getDevice() {
        return device;
    }

    /**
     * 
     * (Required)
     * 
     * @param device
     *     The device
     */
    public void setDevice(String device) {
        this.device = device;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The model
     */
    public String getModel() {
        return model;
    }

    /**
     * 
     * (Required)
     * 
     * @param model
     *     The model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The product
     */
    public String getProduct() {
        return product;
    }

    /**
     * 
     * (Required)
     * 
     * @param product
     *     The product
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * looks like it's obsolete (used in old OpenTestResource.java) - some kind of old value for 'internal' radio type.
     * 
     * @return
     *     The phoneType
     */
    public Long getPhoneType() {
        return phoneType;
    }

    /**
     * looks like it's obsolete (used in old OpenTestResource.java) - some kind of old value for 'internal' radio type.
     * 
     * @param phoneType
     *     The phone_type
     */
    public void setPhoneType(Long phoneType) {
        this.phoneType = phoneType;
    }
    
	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apiLevel == null) ? 0 : apiLevel.hashCode());
		result = prime * result + ((device == null) ? 0 : device.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((osVersion == null) ? 0 : osVersion.hashCode());
		result = prime * result + ((phoneType == null) ? 0 : phoneType.hashCode());
		result = prime * result + ((platform == null) ? 0 : platform.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
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
		MeasurementDeviceInfo other = (MeasurementDeviceInfo) obj;
		if (apiLevel == null) {
			if (other.apiLevel != null)
				return false;
		} else if (!apiLevel.equals(other.apiLevel))
			return false;
		if (device == null) {
			if (other.device != null)
				return false;
		} else if (!device.equals(other.device))
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		if (osVersion == null) {
			if (other.osVersion != null)
				return false;
		} else if (!osVersion.equals(other.osVersion))
			return false;
		if (phoneType == null) {
			if (other.phoneType != null)
				return false;
		} else if (!phoneType.equals(other.phoneType))
			return false;
		if (platform == null) {
			if (other.platform != null)
				return false;
		} else if (!platform.equals(other.platform))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MeasurementDeviceInfo [platform=" + platform + ", osVersion=" + osVersion + ", apiLevel=" + apiLevel
				+ ", device=" + device + ", model=" + model + ", product=" + product + ", phoneType=" + phoneType + "]";
	}
}