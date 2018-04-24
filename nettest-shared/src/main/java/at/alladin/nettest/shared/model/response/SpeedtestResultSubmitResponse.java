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

package at.alladin.nettest.shared.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb@specure.com
 *
 */
public class SpeedtestResultSubmitResponse {

	@SerializedName("open_test_uuid")
	@Expose
	private String openTestUuid;
	
	@SerializedName("test_uuid")
	@Expose	
	private String testUuid;

	public String getOpenTestUuid() {
		return openTestUuid;
	}

	public void setOpenTestUuid(String openTestUuid) {
		this.openTestUuid = openTestUuid;
	}

	public String getTestUuid() {
		return testUuid;
	}

	public void setTestUuid(String testUuid) {
		this.testUuid = testUuid;
	}

	@Override
	public String toString() {
		return "SpeedtestResultSubmitResponse [openTestUuid=" + openTestUuid + ", testUuid=" + testUuid + "]";
	}
}
