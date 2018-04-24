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

package at.alladin.nettest.shared.model.statistics.graph;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import at.alladin.nettest.shared.helper.GsonBasicHelper;
import at.alladin.nettest.shared.model.GeoLocation;

import at.alladin.nettest.shared.model.statistics.LocationUtil;

public class LocationGraph {
	private double totalDistance;
	private ArrayList<LocationGraphItem> locations = new ArrayList<>();
	
	/**
	 * Gets all distinctive locations of a client during a test
	 * @param testUuid the uuid of the test
	 * @param testTime the begin of the test
	 * @throws SQLException
	 */
	public LocationGraph(final String testUuid, long testTime, java.sql.Connection conn) throws SQLException {    		
		try (PreparedStatement psLocation = 
				conn.prepareStatement("SELECT t.json->'locations' from ha_measurement t WHERE uuid = ?::uuid");) {
			
            psLocation.setString(1, testUuid);
            ResultSet rsLocation = psLocation.executeQuery();
            
            if (rsLocation.next()) {
                final Gson gson = GsonBasicHelper.getDateTimeGsonBuilder().create();
                Type listType = new TypeToken<ArrayList<GeoLocation>>(){}.getType();
                
                final String json = rsLocation.getString(1);
                final List<GeoLocation> locationList = gson.fromJson(json, listType);
                
                boolean first = true;
                boolean usedCurrentItem = false;
                LocationGraphItem item = null;
                
                double lastLat=0;
                double lastLong=0;
                double lastAcc=0;
                this.totalDistance=0;
                for (int i = 0; i < locationList.size(); i++) {
                	final GeoLocation location = locationList.get(i);
                	long timeElapsed = location.getRelativeTimeNs() / (long)1e6;
                	//there could be measurements taken before a test started
                	//in this case, only return the last one
                	if (first && timeElapsed >= 0 && item != null) {
                		this.locations.add(item);
                		lastLat = item.getLatitude();
        				lastLong = item.getLongitude();
        				lastAcc = item.getAccuracy();
                		first = false;
                	}
                	
                	
                	item = new LocationGraphItem(Math.max(timeElapsed,0), location.getLongitude(), location.getLatitude(), location.getAccuracy());
                	usedCurrentItem = false;
                	
                	//put triplet in the array if it is not the first one
                	if (!first) {
                		//only put the point in the resulting array, if there is a significant
                		//distance from the last point
                		//therefore (difference in m) > (tolerance last point + tolerance new point)
                		double diff = LocationUtil.distFrom(lastLat, lastLong, item.getLatitude(), item.getLongitude());
                		//System.out.println("dist: " + diff);
                		double maxDiff = item.getAccuracy() + lastAcc;
                		//System.out.println("Distance: " + diff + "; tolTotal " + maxDiff + "; tol1 " + lastAcc + "; tol2 " + json.getDouble("loc_accuracy"));
                		if (diff > maxDiff) {
                			this.locations.add(item);
                    		lastLat = item.getLatitude();
            				lastLong = item.getLongitude();
            				lastAcc = item.getAccuracy();
            				this.totalDistance += diff;
                		}
                		else {
                			//if not, replace the old point, if the new is more accurate
                			if (item.getAccuracy() < lastAcc) {
                				this.locations.remove(this.locations.size()-1);
                				this.locations.add(item);
                        		lastLat = item.getLatitude();
                				lastLong = item.getLongitude();
                				lastAcc = item.getAccuracy();
                			}
                		}
                		
                		usedCurrentItem = true;
                	}
                }
                
                //always use the last item to connect the path with the end point
                if (!usedCurrentItem && this.locations.size() > 0) {
                	//replace the last set point with it
                	//since it is in the same inaccurracy area
                	
                	this.locations.remove(this.locations.size()-1);
    				this.locations.add(item);
            		lastLat = item.getLatitude();
    				lastLong = item.getLongitude();
    				lastAcc = item.getAccuracy();
                	
                }
                
                rsLocation.close();
                psLocation.close();
            }
		}

	}
	
	public double getTotalDistance() {
		return this.totalDistance;
	}
	
	public ArrayList<LocationGraphItem> getLocations() {
		return this.locations;
	}
	
	public class LocationGraphItem {
		private double longitude;
		private double latitude;
		private double accuracy;
		private long timeElapsed;
		
		public LocationGraphItem(long timeElapsed, double longitude, double latitude, double accuracy) {
			this.longitude = longitude;
			this.latitude = latitude;
			this.timeElapsed = timeElapsed;
			this.accuracy = accuracy;
		}
		
		/**
		 * @return The time elapsed since the begin of the test
		 */
		public long getTimeElapsed() {
			return this.timeElapsed;
		}
		
		public double getLongitude() {
			return this.longitude;
		}
		
		public double getLatitude() {
			return this.latitude;
		}
		
		/**
		 * @return The accuracy of the measurement in meters
		 */
		public double getAccuracy() {
			return this.accuracy;
		}
	}
}