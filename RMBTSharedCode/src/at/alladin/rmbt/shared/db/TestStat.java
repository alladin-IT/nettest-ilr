/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

package at.alladin.rmbt.shared.db;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class TestStat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long testUid;
	private JSONObject cpuUsage;
	private JSONObject memUsage;
	
	/**
	 * 
	 */
	public TestStat() {
		
	}

	public Long getTestUid() {
		return testUid;
	}

	public void setTestUid(Long testUid) {
		this.testUid = testUid;
	}

	public JSONObject getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(JSONObject cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public JSONObject getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(JSONObject memUsage) {
		this.memUsage = memUsage;
	}
	
	public JSONObject toJsonObject() throws JSONException {
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("cpu_usage", cpuUsage);
		jsonObject.put("mem_usage", memUsage);
		return jsonObject;
	}

	/**
	 * checks for submitted extended test stats 
	 * @param entity
	 * @param testUid
	 * @return
	 */
	public static TestStat checkForSubmittedTestStats(final JSONObject entity, final long testUid) {
		final JSONObject testStatObject = entity.optJSONObject("extended_test_stat");
		if (testStatObject != null) {
			final JSONObject cpuUsage = testStatObject.optJSONObject("cpu_usage");
			final JSONObject memUsage = testStatObject.optJSONObject("mem_usage");
			
			if (cpuUsage != null || memUsage != null) {
				final TestStat ts = new TestStat();
				
				ts.setTestUid(testUid);
				ts.setCpuUsage(cpuUsage);
				ts.setMemUsage(memUsage);
				
				return ts;
			}
		}
		
		return null;
	}
}
