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

package at.alladin.nettest.shared.server.helper;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import at.alladin.nettest.shared.helper.GsonBasicHelper;

public class GroupSpeedtestDetailResultsHelperTest {
	
	private final Gson gson = GsonBasicHelper.getDateTimeGsonBuilder().create();
	private final String testJson = "{\n" + 
			"  \"client_info\": {\n" + 
			"    \"ip_local_anonymized\": \"37.244.204\",\n" + 
			"    \"time\": \"2017-05-03T15:00:15.000+02:00\",\n" + 
			"    \"version\": \"1.0.1\"\n" + 
			"  },\n" + 
			"  \"upload_classification\": 3,\n" + 
			"  \"cell_locations\": [],\n" + 
			"  \"signal_classification\": null,\n" + 
			"  \"uuid\": \"69b43da2-76f3-4531-b113-6e56a71be096\",\n" + 
			"  \"download_classification\": 3,\n" + 
			"  \"network_info\": {\n" + 
			"    \"client_public_ip_anonymized\": \"37.244.204\",\n" + 
			"    \"nat_type\": \"nat_local_to_public_ipv4\",\n" + 
			"    \"public_ip_asn\": 12810\n" + 
			"  },\n" + 
			"  \"doctype\": \"Measurement\",\n" + 
			"  \"open_test_uuid\": \"69b43da2-76f3-4531-b113-6e56a71be096\",\n" + 
			"  \"deleted\": false,\n" + 
			"  \"device_info\": {\n" + 
			"    \"model\": \"Galaxy S5 Mini\",\n" + 
			"    \"platform\": \"Android\"\n" + 
			"  },\n" + 
			"  \"signals\": [],\n" + 
			"  \"qos\": {\n" + 
			"    \"results\": []\n" + 
			"  },\n" + 
			"  \"ping_classification\": 2,\n" + 
			"  \"implausible\": false,\n" + 
			"  \"locations\": [\n" + 
			"    {}\n" + 
			"  ],\n" + 
			"  \"signal_strength\": -57,\n" + 
			"  \"source_ip_anonymized\": \"37.244.204\",\n" + 
			"  \"speedtest\": {\n" + 
			"    \"duration_upload_ns\": 0,\n" + 
			"    \"total_test_duration_ns\": 0,\n" + 
			"    \"duration_download_ns\": 0,\n" + 
			"    \"relative_time_dl_ns\": 0,\n" + 
			"    \"ping_shortest_log\": 0,\n" + 
			"    \"speed_upload\": 2243,\n" + 
			"    \"nominal_duration\": 5,\n" + 
			"    \"ping_shortest\": 0,\n" + 
			"    \"bytes_download\": 0,\n" + 
			"    \"interface_dltest_bytes_download\": 0,\n" + 
			"    \"interface_ultest_bytes_upload\": 0,\n" + 
			"    \"target_measurement_server\": {\n" + 
			"      \"name\": \"HAKOM Server (Zagreb)\"\n" + 
			"    },\n" + 
			"    \"total_bytes_download\": 0,\n" + 
			"    \"total_bytes_upload\": 0,\n" + 
			"    \"pings\": [],\n" + 
			"    \"speed_download_log\": 0.6031991071791358,\n" + 
			"    \"interface_ultest_bytes_download\": 0,\n" + 
			"    \"test_slot\": 0,\n" + 
			"    \"interface_total_bytes_download\": 0,\n" + 
			"    \"interface_total_bytes_upload\": 0,\n" + 
			"    \"bytes_upload\": 0,\n" + 
			"    \"relative_time_ul_ns\": 0,\n" + 
			"    \"ping_median\": 47865408,\n" + 
			"    \"num_threads_requested\": 0,\n" + 
			"    \"interface_dltest_bytes_upload\": 0,\n" + 
			"    \"speed_download\": 2587,\n" + 
			"    \"ping_variance\": 0,\n" + 
			"    \"ping_median_log\": 0,\n" + 
			"    \"num_threads\": 3,\n" + 
			"    \"time\": \"2017-05-03T15:00:15.000+02:00\",\n" + 
			"    \"speed_upload_log\": 0.587707318395742,\n" + 
			"    \"status\": \"FINISHED\"\n" + 
			"  },\n" + 
			"  \"speed_curve\": {\n" + 
			"    \"download\": [],\n" + 
			"    \"upload\": [],\n" + 
			"    \"location\": [],\n" + 
			"    \"signal\": []\n" + 
			"  },\n" + 
			"  \"timestamp\": \"2017-05-03T15:00:15.000+02:00\"\n" + 
			"}";
	private final String testCorrectGroupStructure = "[\n" + 
			"    {\n" + 
			"      \"key\": \"client_info_group\",\n" + 
			"      \"icon\": \"A\",\n" + 
			"      \"values\": [\n" + 
			"        {\n" + 
			"          \"key\": \"device_info.model\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"client_info.time\",\n" + 
			"          \"time_format\": \"GMT+01:00\"\n" + 
			"        }\n" + 
			"      ]\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"key\": \"signal_group\",\n" + 
			"      \"icon\": \"B\",\n" + 
			"      \"values\": [\n" + 
			"        {\n" + 
			"          \"key\": \"signal_strength\",\n" + 
			"          \"unit\": \"dBm\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"implausible\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"open_test_uuid\"\n" + 
			"        }\n" + 
			"      ]\n" + 
			"    }\n" + 
			"  ]";
	private final String testCorrectGroupStructureAllClientInfo = "[\n" + 
			"    {\n" + 
			"      \"key\": \"client_info_group\",\n" + 
			"      \"icon\": \"A\",\n" + 
			"      \"values\": [\n" + 
			"        {\n" + 
			"          \"key\": \"device_info.model\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"client_info.ip_local_anonymized\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"client_info.version\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"client_info.time\"\n" + 
			"        }\n" + 
			"      ]\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"key\": \"signal_group\",\n" + 
			"      \"icon\": \"B\",\n" + 
			"      \"values\": [\n" + 
			"        {\n" + 
			"          \"key\": \"signal_strength\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"implausible\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"open_test_uuid\"\n" + 
			"        }\n" + 
			"      ]\n" + 
			"    }\n" + 
			"  ]";
	private final String testGroupStructureWithInvalidFields ="[\n" + 
			"    {\n" + 
			"      \"key\": \"non_existant_group\",\n" + 
			"      \"icon\": \"A\",\n" + 
			"      \"values\": [\n" + 
			"        {\n" + 
			"          \"key\": \"device_info.invalid_key\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"invalid_key.time\"\n" + 
			"        }\n" + 
			"      ]\n" + 
			"    },\n" + 
			"    {\n" + 
			"      \"key\": \"signal_group\",\n" + 
			"      \"icon\": \"B\",\n" + 
			"      \"values\": [\n" + 
			"        {\n" + 
			"          \"key\": \"non_existant_key\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"implausible\"\n" + 
			"        },\n" + 
			"        {\n" + 
			"          \"key\": \"open_test_uuid\"\n" + 
			"        }\n" + 
			"      ]\n" + 
			"    }\n" + 
			"  ]\n";
	
	@Test
	public void jsonGroupedSuccessfully_NoDuplicateValuesAndCarriedOverOtherValues(){
		JsonObject resObj = GroupSpeedtestDetailResultsHelper.groupJsonResult(testJson, gson.fromJson(testCorrectGroupStructure, JsonArray.class));//new JsonArray(testCorrectGroupStructure));
		assertFalse(resObj.has("implausible"));
		assertEquals("B", resObj.getAsJsonArray("groups").get(1).getAsJsonObject().get("icon").getAsString());
		assertFalse(resObj.getAsJsonArray("groups").get(1).getAsJsonObject().getAsJsonArray("values").get(1).getAsJsonObject().get("value").getAsBoolean());
		assertEquals("dBm", resObj.getAsJsonArray("groups").get(1).getAsJsonObject().getAsJsonArray("values").get(0).getAsJsonObject().get("unit").getAsString());
		assertEquals(3, resObj.get("upload_classification").getAsInt());
		assertEquals("1.0.1", resObj.getAsJsonObject("client_info").get("version").getAsString());
	}
	
	@Test
	public void jsonGroupingAllEntriesOfJsonObject_DeletesThatJsonObject(){
		JsonObject resObj = GroupSpeedtestDetailResultsHelper.groupJsonResult(testJson, gson.fromJson(testCorrectGroupStructureAllClientInfo, JsonArray.class));
		assertFalse(resObj.has("client_info"));
	}
	
	@Test
	public void jsonGroupedWithUnfoundStructureValues_WorksGracefully(){
		JsonObject resObj = GroupSpeedtestDetailResultsHelper.groupJsonResult(testJson, gson.fromJson(testGroupStructureWithInvalidFields, JsonArray.class));
		assertEquals(2, resObj.getAsJsonArray("groups").get(0).getAsJsonObject().getAsJsonArray("values").size());
	}
	
	@Test
	public void groupWithoutValidEntries_NotPartOfResult(){
		JsonObject resObj = GroupSpeedtestDetailResultsHelper.groupJsonResult(testJson, gson.fromJson(testGroupStructureWithInvalidFields, JsonArray.class));
		assertEquals(1, resObj.getAsJsonArray("groups").size());
	}
}
