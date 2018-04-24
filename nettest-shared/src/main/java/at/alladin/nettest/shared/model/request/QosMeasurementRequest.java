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

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class QosMeasurementRequest extends BasicRequestImpl {

	/**
	 * 
	 */
	@Expose
	private String clientUuid;
	
	/**
	 * The measurement uuid, if there was a speedtest before. Null otherwise.
	 * 
	 */
	@Expose
	private String measurementUuid;
	
	/**
	 * 
	 */
	public QosMeasurementRequest() {
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String getClientUuid() {
		return clientUuid;
	}
	
	/**
	 * 
	 * @param clientUuid
	 */
	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMeasurementUuid() {
		return measurementUuid;
	}

	/**
	 * 
	 * @param measurementUuid
	 */
	public void setMeasurementUuid(String measurementUuid) {
		this.measurementUuid = measurementUuid;
	}
}
