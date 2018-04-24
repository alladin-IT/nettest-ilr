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

import java.util.Date;

public class MccMncMapping {

	private MccMnc mccMncSim;
	private MccMnc mccMncNetwork;
	private Date validFrom;
	private Date validTo;
	
	public MccMnc getMccMncSim() {
		return mccMncSim;
	}
	public void setMccMncSim(MccMnc mccMncSim) {
		this.mccMncSim = mccMncSim;
	}
	public MccMnc getMccMncNetwork() {
		return mccMncNetwork;
	}
	public void setMccMncNetwork(MccMnc mccMncNetwork) {
		this.mccMncNetwork = mccMncNetwork;
	}
	public Date getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
	public Date getValidTo() {
		return validTo;
	}
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
	@Override
	public String toString() {
		return "MccMncMapping [mccMncSim=" + mccMncSim + ", mccMncNetwork=" + mccMncNetwork + ", validFrom=" + validFrom
				+ ", validTo=" + validTo + "]";
	}
}
