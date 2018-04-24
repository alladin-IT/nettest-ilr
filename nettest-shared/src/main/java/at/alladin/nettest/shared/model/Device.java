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

import org.joda.time.DateTime;

import com.google.gson.annotations.Expose;
import at.alladin.nettest.shared.CouchDbEntity;
import at.alladin.nettest.shared.annotation.NotEmpty;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class Device extends CouchDbEntity {

	private static final long serialVersionUID = 7239750533665008499L;

	/**
	 * 
	 */
	@NotEmpty
	@Expose
	private String codename;
	
	/**
	 * 
	 */
	@NotEmpty
	@Expose
	private String fullname;
	
	/**
	 * 
	 */
	@NotEmpty
	@Expose
	private String source;
	
	/**
	 * 
	 */
    @Expose
    private DateTime timestamp;

    // TODO: maybe add more device specific values, like "lte-able",...?
    
    /**
     * 
     * @return
     */
	public String getCodename() {
		return codename;
	}

	/**
	 * 
	 * @param codename
	 */
	public void setCodename(String codename) {
		this.codename = codename;
	}

	/**
	 * 
	 * @return
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * 
	 * @param fullname
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	/**
	 * 
	 * @return
	 */
	public String getSource() {
		return source;
	}

	/**
	 * 
	 * @param source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * 
	 * @return
	 */
	public DateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}
}
