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
public class SpeedtestResultResponse {

	@SerializedName("measurement")
	@Expose
	private List<ClassifiedResultItem> classifiedMeasurementDataList;
	
	@SerializedName("net")
	@Expose
	private List<ResultItem> networkDetailList;
	
	@SerializedName("network_type")
	@Expose
	private int networkType;

	@SerializedName("open_test_uuid")
	@Expose
	private String openTestUuid;
		
	@SerializedName("open_uuid")
	@Expose
	private String openUuid;

	@Expose
	private long time;

	@SerializedName("time_string")
	@Expose
	private String timeString;

	@Expose
	private String timezone;
	
	@Expose
	private String location;
	
	@SerializedName("geo_long")
	@Expose
	private Double longitude;
	
	@SerializedName("geo_lat")
	@Expose
	private Double latitude;
	
	@SerializedName("share_text")
	@Expose
	private String shareText;
	
	@SerializedName("share_subject")
	@Expose
	private String shareSubject;
	
	@SerializedName("qos_result_available")
	@Expose
	private boolean qosResultAvailable;
	
	public SpeedtestResultResponse() {
		
	}
	
	public int getNetworkType() {
		return networkType;
	}

	public void setNetworkType(int networkType) {
		this.networkType = networkType;
	}

	public String getOpenTestUuid() {
		return openTestUuid;
	}

	public void setOpenTestUuid(String openTestUuid) {
		this.openTestUuid = openTestUuid;
	}

	public String getOpenUuid() {
		return openUuid;
	}

	public void setOpenUuid(String openUuid) {
		this.openUuid = openUuid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	public List<ClassifiedResultItem> getClassifiedMeasurementDataList() {
		return classifiedMeasurementDataList;
	}

	public void setClassifiedMeasurementDataList(List<ClassifiedResultItem> classifiedMeasurementDataList) {
		this.classifiedMeasurementDataList = classifiedMeasurementDataList;
	}

	public List<ResultItem> getNetworkDetailList() {
		return networkDetailList;
	}

	public void setNetworkDetailList(List<ResultItem> networkDetailList) {
		this.networkDetailList = networkDetailList;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getShareText() {
		return shareText;
	}

	public void setShareText(String shareText) {
		this.shareText = shareText;
	}

	public String getShareSubject() {
		return shareSubject;
	}

	public void setShareSubject(String shareSubject) {
		this.shareSubject = shareSubject;
	}

	public boolean isQosResultAvailable() {
		return qosResultAvailable;
	}
	
	public void setQosResultAvailable(boolean qosResultAvailable) {
		this.qosResultAvailable = qosResultAvailable;
	}
	
	/**
	 * 
	 * @author lb@specure.com
	 *
	 */
	public static class ResultItem {
		@Expose		
		String key;
		
		@Expose
		String value;
		
		@Expose
		String title;

		public ResultItem() { }
		
		public ResultItem(final String title, final String value) {
			this.title = title;
			this.value = value;
		}
		
		public ResultItem(final String key, final String title, final String value) {
			this.title = title;
			this.value = value;
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public String toString() {
			return "ResultItem [key=" + key + ", value=" + value + ", title=" + title + "]";
		}
	}
	
	/**
	 * 
	 * @author lb@specure.com
	 *
	 */
	public static class ClassifiedResultItem extends ResultItem {
		@Expose
		int classification;

		@SerializedName("classification_color")
		@Expose
		String classificationColor;
		
		public ClassifiedResultItem() {
			super();
		}
		
		public ClassifiedResultItem(final String title, final String value, final int classification) {
			super(title, value);
			this.classification = classification;
		}
		
		public ClassifiedResultItem(final String title, final String value, final int classification, final String classificationColor) {
			super(title, value);
			this.classification = classification;
			this.classificationColor = classificationColor;
		}
		
		public ClassifiedResultItem(final String key, final String title, final String value, final int classification) {
			super(key, title, value);
			this.classification = classification;
		}
		
		public ClassifiedResultItem(final String key, final String title, final String value, final int classification, final String classificationColor) {
			super(key, title, value);
			this.classification = classification;
			this.classificationColor = classificationColor;
		}
		
		public int getClassification() {
			return classification;
		}

		public void setClassification(int classification) {
			this.classification = classification;
		}

		public String getClassificationColor() {
			return classificationColor;
		}

		public void setClassificationColor(String classificationColor) {
			this.classificationColor = classificationColor;
		}

		@Override
		public String toString() {
			return "ClassifiedResultItem [classification=" + classification + ", classificationColor="
					+ classificationColor + "]";
		}
	}
	
}
