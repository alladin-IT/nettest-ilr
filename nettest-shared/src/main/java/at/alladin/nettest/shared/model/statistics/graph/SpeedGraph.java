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

package at.alladin.nettest.shared.model.statistics.graph;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import at.alladin.nettest.shared.helper.GsonBasicHelper;
import at.alladin.nettest.shared.model.MeasurementSpeedRaw;
import at.alladin.nettest.shared.model.MeasurementSpeedRawItem;

public class SpeedGraph {
	private ArrayList<SpeedGraphItem> upload = new ArrayList<>();
	private ArrayList<SpeedGraphItem> download = new ArrayList<>();
	
	
	private final class SpeedItemComparator implements Comparator<MeasurementSpeedRawItem> {

		@Override
		public int compare(MeasurementSpeedRawItem o1, MeasurementSpeedRawItem o2) {
			return Long.compare(o1.getTime(), o2.getTime());
		}
		
	}
	
	/**
	 * Load download and upload speed details
	 * @param testUID the test uid
	 * @param threads the max number of threads used in the test
	 * @throws SQLException
	 */
	public SpeedGraph(String testUuid, int threads, java.sql.Connection conn) throws SQLException {
		try (PreparedStatement psSpeed = 
				conn.prepareStatement("SELECT t.json->'speedtest'->>'speed_raw' from ha_measurement t WHERE uuid = ?::uuid");) {
			
			psSpeed.setString(1, testUuid);
            ResultSet rsSpeed = psSpeed.executeQuery();
            
            if (rsSpeed.next()) {
                final Gson gson = GsonBasicHelper.getDateTimeGsonBuilder().create();
               
                final String json = rsSpeed.getString(1);
                final MeasurementSpeedRaw speedRaw = gson.fromJson(json, MeasurementSpeedRaw.class);

                System.out.println(testUuid);
               
                speedRaw.getDownload().sort(new SpeedItemComparator());
                speedRaw.getUpload().sort(new SpeedItemComparator());
                                
                cumulate(speedRaw.getDownload(), this.download, threads);
                cumulate(speedRaw.getUpload(), this.upload, threads);
				
				rsSpeed.close();
            }
		}
	}
    
	private void cumulate(final List<MeasurementSpeedRawItem> rawList, final List<SpeedGraphItem> cumulatedList, final int threads) {
    	long bytes[] = new long[threads];            	
		long bytesCumulated = 0;
		double lastMs = -1;
		SpeedGraphItem lastObj = null; 	// the last object => if there are more
										// than one entries for one timestamp
        for (MeasurementSpeedRawItem rawItem : rawList) {
			int thread = rawItem.getThread();
			double ms = rawItem.getTime();

			// bytesCum = bytesCum - old + new
			bytesCumulated = bytesCumulated - bytes[thread];
			bytes[thread] = rawItem.getBytes();
			bytesCumulated = bytesCumulated + bytes[thread];

			// if it is a new timestamp => make new array
			if (lastMs != ms) {
				SpeedGraphItem obj = new SpeedGraphItem((long)ms, bytesCumulated); 
				cumulatedList.add(obj);
				lastObj = obj;
				lastMs = ms;
			} else {
				// if it is the same time => update the previous timestamp
				lastObj.setBytesTotal(bytesCumulated);
			}
        }
	}
	
	
	public ArrayList<SpeedGraphItem> getUpload() {
		return this.upload;
	}
	
	public ArrayList<SpeedGraphItem> getDownload() {
		return this.download;
	}

	public class SpeedGraphItem {
		private long timeElapsed;
		private long bytesTotal;
		public SpeedGraphItem(long timeElapsed, long bytesTotal) {
			this.timeElapsed = timeElapsed;
			this.bytesTotal = bytesTotal;
		}
		
		/**
		 * @return The time elapsed since the begin of the test
		 */
		public long getTimeElapsed() {
			return this.timeElapsed;
		}
		
		/**
		 * @return The total bytes transmitted in all threads since the begin of the test 
		 */
		public long getBytesTotal() {
			return this.bytesTotal;
		}
		
		public void setBytesTotal(long bytesTotal) {
			this.bytesTotal = bytesTotal;
		}
	}
}