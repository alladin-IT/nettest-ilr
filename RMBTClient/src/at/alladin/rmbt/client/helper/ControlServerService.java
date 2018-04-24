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

package at.alladin.rmbt.client.helper;

import com.google.gson.JsonObject;
import at.alladin.nettest.shared.model.HistoryItem;
import at.alladin.nettest.shared.model.request.MeasurementRequest;
import at.alladin.nettest.shared.model.request.QosMeasurementRequest;
import at.alladin.nettest.shared.model.request.QosMeasurementResultSubmitRequest;
import at.alladin.nettest.shared.model.request.SettingsRequest;
import at.alladin.nettest.shared.model.request.SpeedtestResultSubmitRequest;
import at.alladin.nettest.shared.model.response.IpResponse;
import at.alladin.nettest.shared.model.response.MeasurementRequestResponse;
import at.alladin.nettest.shared.model.response.QosMeasurementRequestResponse;
import at.alladin.nettest.shared.model.response.SettingsResponse;
import at.alladin.nettest.shared.model.response.SpeedtestDetailGroupResultResponse;
import at.alladin.nettest.shared.model.response.SpeedtestDetailResultResponse;
import at.alladin.nettest.shared.model.response.SpeedtestResultResponse;
import at.alladin.nettest.shared.model.response.SpeedtestResultSubmitResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 
 * @author lb@specure.com
 *
 */
public interface ControlServerService {
	
	@POST("api/v1/ip")
	Call<IpResponse> requestIp();

	@POST("api/v1/settings")
	Call<SettingsResponse> requestSettings(@Body SettingsRequest request);

	@GET("api/v1/clients/{clientUuid}/measurements")
	Call<List<HistoryItem>> requestHistory(@Path("clientUuid") String clientUuid, @Query("timestamp") long timestamp);
	
	@PUT("api/v1/measurements/qos/{uuid}")
	Call<JsonObject> sendQoSResults(@Path("uuid") String uuid, @Body QosMeasurementResultSubmitRequest qosResult);

	@POST("api/v1/measurements/qos")
	Call<QosMeasurementRequestResponse> requestQoSObjectives(@Body QosMeasurementRequest requestQoS);
	
	@GET("api/v1/measurements/qos/{uuid}")
	Call<JsonObject> getQoSResults(@Path("uuid") String uuid);
	
	@POST("api/v1/measurements/speed")
	Call<MeasurementRequestResponse> requestSpeedTest(@Body MeasurementRequest requestMeasurement);

	@PUT("api/v1/measurements/speed/{uuid}")
	Call<SpeedtestResultSubmitResponse> sendSpeedTestResults(@Path("uuid") String uuid, @Body SpeedtestResultSubmitRequest resultSubmitRequest);
	
	@GET("api/v1/measurements/speed/{uuid}")
	Call<SpeedtestResultResponse> requestSpeedTestResult(@Path("uuid") String uuid);

	@GET("api/v1/measurements/speed/{uuid}/details")
	Call<SpeedtestDetailResultResponse> requestSpeedTestDetailResult(@Path("uuid") String uuid);

	@GET("api/v1/measurements/speed/{uuid}/details?grouped=true")
	Call<SpeedtestDetailGroupResultResponse> requestSpeedTestDetailGroupResult(@Path("uuid") String uuid);

	@DELETE("api/v1/clients/{clientUuid}/measurements/{uuid}")
	Call<JsonObject> requestDisassociation(@Path("clientUuid") String clientUuid, @Path("uuid") String measurementUuid);
}
