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
import at.alladin.nettest.shared.model.Measurement;
import at.alladin.nettest.shared.model.MeasurementOpenTest;


public class GsonHelperTest {

	private final String testJson = "{\n" + 
			"  \"upload_classification\": 3,\n" + 
			"  \"test_duration\": 0,\n" + 
			"  \"testdl_if_bytes_upload\": 314851,\n" + 
			"  \"sim_country\": \"at\",\n" + 
			"  \"time_ul_ms\": 15570.25379,\n" + 
			"  \"time_dl_ms\": 7002.900437,\n" + 
			"  \"public_ip_as_name\": \"H3G-AUSTRIA-AS, AT\",\n" + 
			"  \"country_geoip\": null,\n" + 
			"  \"ping_classification\": 2,\n" + 
			"  \"roaming_type\": null,\n" + 
			"  \"implausible\": \"false\",\n" + 
			"  \"model\": \"LGE Nexus 5X\",\n" + 
			"  \"connection\": \"nat_local_to_public_ipv4\",\n" + 
			"  \"signal_strength\": -80,\n" + 
			"  \"lat\": null,\n" + 
			"  \"speed_curve\": {\n" + 
			"    \"download\": [\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 47,\n" + 
			"        \"bytes_total\": 4096\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 54,\n" + 
			"        \"bytes_total\": 8192\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 58,\n" + 
			"        \"bytes_total\": 12288\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 164,\n" + 
			"        \"bytes_total\": 147456\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 186,\n" + 
			"        \"bytes_total\": 241664\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 5122,\n" + 
			"        \"bytes_total\": 23085056\n" + 
			"      }\n" + 
			"    ],\n" + 
			"    \"upload\": [\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 53,\n" + 
			"        \"bytes_total\": 4096\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 55,\n" + 
			"        \"bytes_total\": 8192\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 57,\n" + 
			"        \"bytes_total\": 12288\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 157,\n" + 
			"        \"bytes_total\": 110592\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 158,\n" + 
			"        \"bytes_total\": 200704\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 160,\n" + 
			"        \"bytes_total\": 299008\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 267,\n" + 
			"        \"bytes_total\": 401408\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 270,\n" + 
			"        \"bytes_total\": 503808\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 277,\n" + 
			"        \"bytes_total\": 626688\n" + 
			"      },\n" + 
			"      {\n" + 
			"        \"time_elapsed\": 5431,\n" + 
			"        \"bytes_total\": 15589376\n" + 
			"      }\n" + 
			"    ],\n" + 
			"    \"location\": [],\n" + 
			"    \"signal\": []\n" + 
			"  },\n" + 
			"  \"test_if_bytes_upload\": 32401516,\n" + 
			"  \"duration_download_ms\": 5006.829613,\n" + 
			"  \"network_name\": \"3 AT\",\n" + 
			"  \"bytes_upload\": 15568331,\n" + 
			"  \"signal_classification\": 3,\n" + 
			"  \"ip_anonym\": \"178.115.129\",\n" + 
			"  \"upload_kbit\": 24532,\n" + 
			"  \"ping_ms\": 40.331429,\n" + 
			"  \"wifi_link_speed\": null,\n" + 
			"  \"num_threads\": 3,\n" + 
			"  \"cat_technology\": \"4G\",\n" + 
			"  \"server_name\": \"TESTSERVER ()\",\n" + 
			"  \"model_native\": \"Nexus 5X\",\n" + 
			"  \"network_mcc_mnc\": \"232-05\",\n" + 
			"  \"country_location\": null,\n" + 
			"  \"download_classification\": 3,\n" + 
			"  \"long\": null,\n" + 
			"  \"platform\": \"Android\",\n" + 
			"  \"testul_if_bytes_download\": 383454,\n" + 
			"  \"loc_src\": null,\n" + 
			"  \"loc_accuracy\": null,\n" + 
			"  \"bytes_download\": 23083246,\n" + 
			"  \"open_test_uuid\": \"Od2e3579e-6d63-4df0-bacf-47d30e42b907\",\n" + 
			"  \"country_asn\": \"AT\",\n" + 
			"  \"num_threads_ul\": null,\n" + 
			"  \"client_version\": \"0.0.2-beta\",\n" + 
			"  \"provider_name\": \"3 AT\",\n" + 
			"  \"network_country\": null,\n" + 
			"  \"product\": \"bullhead\",\n" + 
			"  \"duration_upload_ms\": 5076.962707,\n" + 
			"  \"testul_if_bytes_upload\": 17484906,\n" + 
			"  \"num_threads_requested\": 3,\n" + 
			"  \"sim_mcc_mnc\": \"232-05\",\n" + 
			"  \"test_if_bytes_download\": 32401516,\n" + 
			"  \"ping_variance\": 57.32110643992842,\n" + 
			"  \"lte_rsrq\": -12,\n" + 
			"  \"lte_rsrp\": -90,\n" + 
			"  \"time\": \"2017-04-20 16:10:32\",\n" + 
			"  \"download_kbit\": 36883,\n" + 
			"  \"network_type\": \"MOBILE\",\n" + 
			"  \"asn\": 25255,\n" + 
			"  \"testdl_if_bytes_download\": 26464689,\n" + 
			"  \"source_ip\": \"178.115.129.122\"\n" + 
			"}";
	
	@Test
	public void jsonToMeasurement_ContainsEntriesTest() {
		Gson gson = GsonHelper.createDatabaseGsonBuilder().create();
		Measurement measurement = gson.fromJson(this.testJson, Measurement.class);
		assertEquals("Od2e3579e-6d63-4df0-bacf-47d30e42b907", measurement.getOpenTestUuid());
		assertEquals(-90, (int) measurement.getLteRsrp());
		assertEquals("178.115.129.122", measurement.getSourceIp());
		assertNull(measurement.getTag());
	}
		
	@Test
	public void jsonToOpenTestMeasurement_ContainsOnlyAllowedEntries(){
		Gson gson = GsonHelper.createOpenTestGsonBuilder().create();
		MeasurementOpenTest measurement = gson.fromJson(this.testJson, MeasurementOpenTest.class);
		//the source ip is marked to not be saved with the OpenTestGsonBuilder
		assertNull(measurement.getSourceIp());
		assertEquals(36883, (long) measurement.getDownloadKbit());
	}
	
	@Test
	public void jsonToMeasurementFromStatisticsServer_UsesAlternateNames(){
		Gson gson = GsonHelper.createDatabaseGsonBuilder().create();
		Measurement measurement = gson.fromJson(this.testJson, Measurement.class);
		//ip_anonym is an alternate
		assertEquals("178.115.129", measurement.getSourceIpAnonymized());
	}
}
