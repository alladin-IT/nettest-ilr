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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.model.Client.ClientType;

/**
 * 
 * @author lb@specure.com
 *
 */
public class BasicRequestImpl implements BasicRequest {
	
	/**
	 * TODO: Android specific?
	 */
    @SerializedName("api_level")
    @Expose
    private String apiLevel;
    
    /**
     * TODO: RMBT/RMBTws (RMBT_CLIENT_NAME)
     */
    @SerializedName("client_name")
    @Expose
    private String clientName;
    
    /**
     * Type of device (browser: unused)
     */
    @SerializedName("device")
    @Expose    
    private String device;

    /**
     * Client language used
     */
    @SerializedName("language")
    @Expose
    private String language;

    /**
     * Device model (browser: <browser> (without version))
     */
    @SerializedName("model")
    @Expose
    private String model;
    
    /**
     * OS version (browser: not used?)
     */
    @SerializedName("os_version")
    @Expose
    private String osVersion;
    
    /**
     * Platform used for test (browser: RMBTws)
     */
    @SerializedName("platform")
    @Expose
    private String platform;
    
    /**
     * TODO: this (END/ABORTED)
     * 
     * renamed from previousTestStatus
     * 
     */
    @SerializedName("previous_test_status")
    @Expose
    private String previousTestStatus;
    
    /**
     * TODO:
     */
    @SerializedName("product")
    @Expose
    private String product;
    
    /**
     * <branch>-<commit>
     * 
     * renamed from softwareRevision
     * 
     */
    @SerializedName("software_revision")
    @Expose
    private String softwareRevision;
    
    /**
     * Version of client (x.y.z)
     * 
     * renamed from softwareVersion
     * 
     */
    @SerializedName("software_version")
    @Expose
    private String softwareVersion;
    
    /**
     * Release counter
     * 
     * renamed from softwareVersionCode
     * 
     */
    @SerializedName("software_version_code")
    @Expose
    private Integer softwareVersionCode;
    
    /**
     * TODO: Same as software_version? Protocol version (0.1/0.3)?
     * 
     * renamed from softwareVersionName
     * 
     */
    @SerializedName("software_version_name")
    @Expose
    private String softwareVersionName;

    /**
     * Timezone of client
     */
    @SerializedName("timezone")
    @Expose
    private String timezone;
    
    /**
     * Type of client ('MOBILE', 'DESKTOP')
     */
    @SerializedName("client_type")
    @Expose
    private ClientType clientType;

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getApiLevel()
	 */
	@Override
	public String getApiLevel() {
		return apiLevel;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setApiLevel(java.lang.String)
	 */
	@Override
	public void setApiLevel(String apiLevel) {
		this.apiLevel = apiLevel;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getClient()
	 */
	@Override
	public String getClientName() {
		return clientName;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setClient(java.lang.String)
	 */
	@Override
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getDevice()
	 */
	@Override
	public String getDevice() {
		return device;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setDevice(java.lang.String)
	 */
	@Override
	public void setDevice(String device) {
		this.device = device;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getLanguage()
	 */
	@Override
	public String getLanguage() {
		return language;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setLanguage(java.lang.String)
	 */
	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getModel()
	 */
	@Override
	public String getModel() {
		return model;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setModel(java.lang.String)
	 */
	@Override
	public void setModel(String model) {
		this.model = model;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getOsVersion()
	 */
	@Override
	public String getOsVersion() {
		return osVersion;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setOsVersion(java.lang.String)
	 */
	@Override
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getPlatform()
	 */
	@Override
	public String getPlatform() {
		return platform;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setPlatform(java.lang.String)
	 */
	@Override
	public void setPlatform(String platform) {
		this.platform = platform;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getPreviousTestStatus()
	 */
	@Override
	public String getPreviousTestStatus() {
		return previousTestStatus;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setPreviousTestStatus(java.lang.String)
	 */
	@Override
	public void setPreviousTestStatus(String previousTestStatus) {
		this.previousTestStatus = previousTestStatus;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getProduct()
	 */
	@Override
	public String getProduct() {
		return product;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setProduct(java.lang.String)
	 */
	@Override
	public void setProduct(String product) {
		this.product = product;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getSoftwareRevision()
	 */
	@Override
	public String getSoftwareRevision() {
		return softwareRevision;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setSoftwareRevision(java.lang.String)
	 */
	@Override
	public void setSoftwareRevision(String softwareRevision) {
		this.softwareRevision = softwareRevision;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getSoftwareVersion()
	 */
	@Override
	public String getSoftwareVersion() {
		return softwareVersion;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setSoftwareVersion(java.lang.String)
	 */
	@Override
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getSoftwareVersionCode()
	 */
	@Override
	public Integer getSoftwareVersionCode() {
		return softwareVersionCode;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setSoftwareVersionCode(java.lang.Integer)
	 */
	@Override
	public void setSoftwareVersionCode(Integer softwareVersionCode) {
		this.softwareVersionCode = softwareVersionCode;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getSoftwareVersionName()
	 */
	@Override
	public String getSoftwareVersionName() {
		return softwareVersionName;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setSoftwareVersionName(java.lang.String)
	 */
	@Override
	public void setSoftwareVersionName(String softwareVersionName) {
		this.softwareVersionName = softwareVersionName;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getTimezone()
	 */
	@Override
	public String getTimezone() {
		return timezone;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setTimezone(java.lang.String)
	 */
	@Override
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#getClientType()
	 */
	@Override
	public ClientType getClientType() {
		return clientType;
	}

	/* (non-Javadoc)
	 * @see at.alladin.nettest.shared.model.request.SimpleRequest#setClientType(at.alladin.nettest.shared.model.Client.ClientType)
	 */
	@Override
	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}   
}
