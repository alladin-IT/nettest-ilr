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

import at.alladin.nettest.shared.model.Client.ClientType;

public interface BasicRequest {

	String getApiLevel();

	void setApiLevel(String apiLevel);

	String getClientName();

	void setClientName(String client);

	String getDevice();

	void setDevice(String device);

	String getLanguage();

	void setLanguage(String language);

	String getModel();

	void setModel(String model);

	String getOsVersion();

	void setOsVersion(String osVersion);

	String getPlatform();

	void setPlatform(String platform);

	String getPreviousTestStatus();

	void setPreviousTestStatus(String previousTestStatus);

	String getProduct();

	void setProduct(String product);

	String getSoftwareRevision();

	void setSoftwareRevision(String softwareRevision);

	String getSoftwareVersion();

	void setSoftwareVersion(String softwareVersion);

	Integer getSoftwareVersionCode();

	void setSoftwareVersionCode(Integer softwareVersionCode);

	String getSoftwareVersionName();

	void setSoftwareVersionName(String softwareVersionName);

	String getTimezone();

	void setTimezone(String timezone);

	ClientType getClientType();

	void setClientType(ClientType clientType);

}