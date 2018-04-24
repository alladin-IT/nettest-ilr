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

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb@specure.com
 *
 */
public class SpeedtestDetailResultResponse {
	
	@SerializedName("testresultdetail")
	@Expose
	public List<SpeedtestDetailItem> testResultDetailList;
	
	public List<SpeedtestDetailItem> getTestResultDetailList() {
		return testResultDetailList;
	}

	public void setTestResultDetailList(List<SpeedtestDetailItem> testResultDetailList) {
		this.testResultDetailList = testResultDetailList;
	}
	
	@Override
	public String toString() {
		return "SpeedtestDetailResultResponse [testResultDetailList=" + testResultDetailList + "]";
	}
	
	/**
	 * 
	 * @author lb@specure.com
	 *
	 */
	public static class SpeedtestDetailItem {
		
		@Expose
		String value;
		
		@Expose
		String key;
		
		@Expose
		String title;
		
		@Expose
		String unit;
		
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		@Override
		public String toString() {
			return "SpeedtestDetailItem [value=" + value + ", key=" + key + ", title=" + title + ", unit=" + unit + "]";
		}
	}
	
	public static class SpeedtestTimeDetailItem extends SpeedtestDetailItem {
		
		@Expose
		Long time;
		
		@Expose
		String timezone;

		public Long getTime() {
			return time;
		}

		public void setTime(Long time) {
			this.time = time;
		}

		public String getTimezone() {
			return timezone;
		}

		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}
	}
}
