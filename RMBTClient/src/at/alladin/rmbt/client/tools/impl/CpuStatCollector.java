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

package at.alladin.rmbt.client.tools.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.alladin.rmbt.util.tools.Collector;
import at.alladin.rmbt.util.tools.CpuStat;

public class CpuStatCollector implements Collector<Float, JSONObject> {

	public final static String JSON_KEY = "cpu_usage";

	List<CollectorData<Float>> collectorDataList = new ArrayList<Collector.CollectorData<Float>>();
	final CpuStat cpuStat;
	final long pauseNs;

	/**
	 * 
	 * @param cpuStatImpl
	 * @param pauseNs
	 */
	public CpuStatCollector(CpuStat cpuStatImpl, long pauseNs) {
		this.cpuStat = cpuStatImpl;
		this.pauseNs = pauseNs;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.client.tools.Collector#update(float, java.util.concurrent.TimeUnit)
	 */
	public synchronized CollectorData<Float> update(float delta, TimeUnit timeUnit) {
		float[] cores = cpuStat.update(false);

		if (cores  != null) {
			float cpu = 0f;
			
			for (float c : cores) {
				cpu += c;
			}

			CollectorData<Float> data = new CollectorData<Float>(cpu / cores.length);
			
			if (delta != 0f) {
				collectorDataList.add(data);
			}
			
			return data;
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.client.tools.Collector#getNanoPause()
	 */
	public long getNanoPause() {
		return pauseNs;
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.client.tools.Collector#getJsonKey()
	 */
	public String getJsonKey() {
		return JSON_KEY;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.client.tools.Collector#getJsonResult()
	 */
	public JSONObject getJsonResult(boolean clean) throws JSONException {
		return getJsonResult(clean, 0, TimeUnit.NANOSECONDS);
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.rmbt.util.tools.Collector#getJsonResult(boolean, long, java.util.concurrent.TimeUnit)
	 */
	public JSONObject getJsonResult(boolean clean, long relTimeStamp, TimeUnit timeUnit) throws JSONException {
		final long relativeTimeStampNs = TimeUnit.NANOSECONDS.convert(relTimeStamp, timeUnit);
		final JSONArray jsonArray = new JSONArray();
		for (CollectorData<Float> data : collectorDataList) {
			final JSONObject dataJson = new JSONObject();
			dataJson.put("value", ((data.getValue() * 100f)));
			dataJson.put("time_ns", data.getTimeStampNs() - relativeTimeStampNs);
			jsonArray.put(dataJson);
		}
		
		final JSONObject jsonObject = new JSONObject();
		if (!cpuStat.getLastCpuUsage().isDetectedIdleOrIoWaitDrop()) {
			jsonObject.put("values", jsonArray);
		}
		else {
			jsonObject.put("values", new JSONArray());
			final JSONArray flagArray = new JSONArray();
			JSONObject flag = new JSONObject();
			flag.put("info", "implausible idle/iowait");
			flagArray.put(flag);
			jsonObject.put("flags", flagArray);
		}
		
		if (clean) {
			collectorDataList.clear();
		}
		
		return jsonObject;
	}
}
