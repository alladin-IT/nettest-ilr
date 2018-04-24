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

import java.util.List;

import javax.validation.constraints.Size;

import com.google.gson.annotations.Expose;
import at.alladin.nettest.shared.CouchDbEntity;
import at.alladin.nettest.shared.annotation.NotEmpty;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class Provider extends CouchDbEntity {

	private static final long serialVersionUID = 7894526372742491879L;

	/**
	 * 
	 */
	@NotEmpty
	@Expose
	private String name;

	/**
	 * 
	 */
	@NotEmpty
	@Expose
	private String shortname;
	
	/**
	 * 
	 */
	@Expose
	@Size(min=5, max=7)
	private String mccMnc;
	
	/**
	 * 
	 */
    @Expose
    private boolean mapFilterEnabled;

    @Expose
    private List<AsnMapping> asnMappings;
    
    @Expose
    private List<MccMncMapping> mccMncMappings;
    

    /**
     * 
     * @return
     */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getShortname() {
		return shortname;
	}

	/**
	 * 
	 * @param shortname
	 */
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	/**
	 * 
	 * @return
	 */
	public String getMccMnc() {
		return mccMnc;
	}

	/**
	 * 
	 * @param mccMnc
	 */
	public void setMccMnc(String mccMnc) {
		this.mccMnc = mccMnc;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isMapFilterEnabled() {
		return mapFilterEnabled;
	}

	/**
	 * 
	 * @param mapFilterEnabled
	 */
	public void setMapFilterEnabled(boolean mapFilterEnabled) {
		this.mapFilterEnabled = mapFilterEnabled;
	}

	/**
	 * @return
	 */
	public List<AsnMapping> getAsnMappings() {
		return asnMappings;
	}

	/**
	 * @param asnMapping
	 */
	public void setAsnMappings(List<AsnMapping> asnMappings) {
		this.asnMappings = asnMappings;
	}

	/**
	 * @return
	 */
	public List<MccMncMapping> getMccMncMappings() {
		return mccMncMappings;
	}

	/**
	 * @param mccMncMappings
	 */
	public void setMccMncMappings(List<MccMncMapping> mccMncMappings) {
		this.mccMncMappings = mccMncMappings;
	}

}
