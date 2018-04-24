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

public class AsnMapping {
	
	private long asn;
	private String reverseDnsSuffix;
	
	public AsnMapping() {
	}
	
	public AsnMapping(long asn, String reverseDnsSuffix) {
		super();
		this.asn = asn;
		this.reverseDnsSuffix = reverseDnsSuffix;
	}
	public long getAsn() {
		return asn;
	}
	public void setAsn(long asn) {
		this.asn = asn;
	}

	public String getReverseDnsSuffix() {
		return reverseDnsSuffix;
	}

	public void setReverseDnsSuffix(String reverseDnsSuffix) {
		this.reverseDnsSuffix = reverseDnsSuffix;
	}
	
}
