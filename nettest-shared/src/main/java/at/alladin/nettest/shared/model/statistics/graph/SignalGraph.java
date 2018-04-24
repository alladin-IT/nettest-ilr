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
import at.alladin.nettest.shared.model.Signal;

public class SignalGraph {
	private static int LOWER_BOUND = -1500;
	private static int MAX_TIME = 60000;
	
	private ArrayList<SignalGraphItem> signalList = new ArrayList<>();
	
	/**
	 * Gets all signal measurements from a test
	 * @param testUID the test uid
	 * @param testTime the begin of the test
	 * @throws SQLException
	 */
	public SignalGraph(final String testUuid, final long testTime, final java.sql.Connection conn) throws SQLException {		
		try (PreparedStatement psSignal = 
				conn.prepareStatement("SELECT t.json->'signals' from ha_measurement t WHERE uuid = ?::uuid");) {
			
            psSignal.setString(1, testUuid);
            ResultSet rsSignal = psSignal.executeQuery();
            
            if (rsSignal.next()) {
                final Gson gson = GsonBasicHelper.getDateTimeGsonBuilder().create();
                Type listType = new TypeToken<ArrayList<Signal>>(){}.getType();
                
                final String json = rsSignal.getString(1);
                final List<Signal> signals = gson.fromJson(json, listType);
                
                boolean first = true;
                SignalGraphItem item = null;
                for (int i = 0; i < signals.size(); i++) {
                	final Signal signal = signals.get(i);
                	long timeElapsed = (signal.getRelativeTimeNs() / (long)1e6);
                	//there could be measurements taken before a test started
                	//in this case, only return the last one
                	if (first && timeElapsed >= 0 && item != null) {
                		this.signalList.add(item);
                		first = false;
                	}
                	
                	//ignore measurements after a threshold of one minute
                	if (timeElapsed > MAX_TIME) {
                		break;
                	}
                	
                	
                	int signalStrength = signal.getSignalStrength() == null ? 0 : signal.getSignalStrength(); //rsSignal.getInt("signal_strength");
                	int lteRsrp = signal.getLteRsrp() == null ? 0 : signal.getLteRsrp(); //rsSignal.getInt("lte_rsrp");
                	int lteRsrq = signal.getLteRsrq() == null ? 0 : signal.getLteRsrq(); //rsSignal.getInt("lte_rsrq");
                	if (signalStrength == 0) {
                		signalStrength = signal.getWifiRssi(); //rsSignal.getInt("wifi_rssi");
                	}
                	
                	if (signalStrength > LOWER_BOUND) {
                		//item = new SignalGraphItem(Math.max(timeElapsed,0), rsSignal.getString("network_type"), signalStrength, lteRsrp, lteRsrq, rsSignal.getString("cat_technology"));
                		item = new SignalGraphItem(Math.max(timeElapsed,0), 
                				signal.getNetworkType(), signalStrength, lteRsrp, 
                				lteRsrq, signal.getCatTechnology());
                	}
                	
                	
                	//put 5-let in the array if it is not the first one
                	if (!first || (i+1) == signalList.size()) {
                		if (timeElapsed < 0) {
                			item.timeElapsed = 1000;
                		}
                		this.signalList.add(item);
                	}
                }
                
                rsSignal.close();
                psSignal.close();
            }
		}
		catch (final Exception e) {
			throw e;
		}
	}
	
	public ArrayList<SignalGraphItem>getSignalList() {
		return this.signalList;
	}
	
	public class SignalGraphItem {
		private long timeElapsed;
		private String networkType;
		private int signalStrength;
		private int lteRsrp;
		private int lteRsrq;
		private String catTechnology;
		
		public SignalGraphItem(long timeElapsed, String networkType, int signalStrength, int lteRsrp, int lteRsrq, String catTechnology) {
			this.timeElapsed = timeElapsed;
			this.networkType = networkType;
			this.signalStrength = signalStrength;
			this.lteRsrp = lteRsrp;
			this.lteRsrq = lteRsrq;
			this.catTechnology = catTechnology;
		}
		
		/**
		 * @return The time elapsed since the begin of the test
		 */
		public long getTimeElapsed() {
			return this.timeElapsed;
		}
		
		/**
		 * @return The type of the network, e.g. 
		 */
		public String getNetworkType() {
			return this.networkType;
		}

   		/**
		 * @return The signal strength RSSI in dBm
		 */
		public int getSignalStrength() {
			return this.signalStrength;
		}

   		/**
		 * @return The signal strength RSRP in dBm
		 */
		public int getLteRsrp() {
			return this.lteRsrp;
		}
		
   		/**
		 * @return The signal quality RSRQ in dB
		 */
		public int getLteRsrq() {
			return this.lteRsrq;
		}
		
		public String getCatTechnology() {
			return this.catTechnology;
		}

	}
}