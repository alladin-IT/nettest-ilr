/*******************************************************************************
 * Copyright 2016-2017 alladin-IT GmbH
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
import at.alladin.nettest.shared.model.response.SpeedtestDetailResultResponse.SpeedtestDetailItem;

public class SpeedtestDetailGroupResultResponse {

	/**
	 * Contains a list of groups together with their icons, translated title and all entries of that group
	 */
	@SerializedName("testresultDetailGroups")
	@Expose
	private List<SpeedtestDetailResponseGroup> speedtestDetailGroups;
	
	public List<SpeedtestDetailResponseGroup> getSpeedtestDetailGroups() {
		return speedtestDetailGroups;
	}

	public void setSpeedtestDetailGroups(List<SpeedtestDetailResponseGroup> speedtestDetailGroups) {
		this.speedtestDetailGroups = speedtestDetailGroups;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SpeedtestDetailGroupResultResponse [speedtestDetailGroups=" + speedtestDetailGroups + "]";
	}

	public static class SpeedtestDetailResponseGroup{

		/**
		 * Contains all the entries of the given group
		 */
		@Expose
		private List<SpeedtestDetailItem> entries;
		
		/**
		 * The already translated title of the given group
		 */
		@Expose
		private String title;
		
		/**
		 * The icon to be used for the given group
		 * TODO: is this the path to the icons? A single char for the custom font??
		 */
		@Expose
		private String icon;
		
		public List<SpeedtestDetailItem> getEntries() {
			return entries;
		}

		public void setEntries(List<SpeedtestDetailItem> entries) {
			this.entries = entries;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "SpeedtestDetailGroup [groupEntries=" + entries + ", groupTitle=" + title + ", icon="
					+ icon + "]";
		}
		
	}
}
