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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb@specure.com
 *
 */
public class ExtendedTestStat {

	@SerializedName("cpu_usage")
	@Expose
	TestStat cpuUsage;

	@SerializedName("mem_usage")
	@Expose
	TestStat memUsage;
	
	public TestStat getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(TestStat cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public TestStat getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(TestStat memUsage) {
		this.memUsage = memUsage;
	}

	/**
	 * 
	 * @author lb@specure.com
	 *
	 */
	public static class TestStat {

		@SerializedName("values")
		@Expose
		List<TestStatValue> values = new ArrayList<>();

		@SerializedName("flags")
		@Expose
		List<Map<String, Object>> flags = new ArrayList<>();
			
		public List<TestStatValue> getValues() {
			return values;
		}

		public void setValues(List<TestStatValue> values) {
			this.values = values;
		}

		public List<Map<String, Object>> getFlags() {
			return flags;
		}

		public void setFlags(List<Map<String, Object>> flags) {
			this.flags = flags;
		}

		/**
		 * 
		 * @author lb@specure.com
		 *
		 */
		public static class TestStatValue {
			
			@SerializedName("time_ns")
			@Expose
			Long timeNs;
			
			@SerializedName("value")
			@Expose
			Float value;

			public Long getTimeNs() {
				return timeNs;
			}

			public void setTimeNs(Long timeNs) {
				this.timeNs = timeNs;
			}

			public Float getValue() {
				return value;
			}

			public void setValue(Float value) {
				this.value = value;
			}
		}
	}
}
