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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.CouchDbEntity;

/**
 * 
 * @author lb@specure.com
 *
 */
public class NetworkType extends CouchDbEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("network_type_id")
	@Expose
	private int networkTypeId;
	
	@Expose
	private String name;

	@SerializedName("group_name")
	@Expose
	private String groupName;
	
	@Expose
	private String[] aggregate;
	
	@Expose
	private NetworkTypeName type;
	
	@SerializedName("technology_order")
	@Expose
	private int technologyOrder;
	
	/**
	 * 
	 * @author lb@specure.com
	 *
	 */
	public static enum NetworkTypeName {
		MOBILE,
		CLI,
		WLAN,
		LAN
	}

	public int getNetworkTypeId() {
		return networkTypeId;
	}

	public void setNetworkTypeId(int networkTypeId) {
		this.networkTypeId = networkTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String[] getAggregate() {
		return aggregate;
	}

	public void setAggregate(String[] aggregate) {
		this.aggregate = aggregate;
	}

	public NetworkTypeName getType() {
		return type;
	}

	public void setType(NetworkTypeName type) {
		this.type = type;
	}

	public int getTechnologyOrder() {
		return technologyOrder;
	}

	public void setTechnologyOrder(int technologyOrder) {
		this.technologyOrder = technologyOrder;
	}
}
