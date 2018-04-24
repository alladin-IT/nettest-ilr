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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.dao.Dao;
import at.alladin.nettest.shared.helper.GsonBasicHelper;
import at.alladin.nettest.shared.model.Client.ClientType;
import at.alladin.nettest.shared.model.GeoLocation;
import at.alladin.nettest.shared.model.HistoryItem;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nettest.shared.model.request.MeasurementRequest;
import at.alladin.nettest.shared.model.request.QosMeasurementRequest;
import at.alladin.nettest.shared.model.request.QosMeasurementResultSubmitRequest;
import at.alladin.nettest.shared.model.request.SettingsRequest;
import at.alladin.nettest.shared.model.request.SpeedtestResultSubmitRequest;
import at.alladin.nettest.shared.model.response.MeasurementRequestResponse;
import at.alladin.nettest.shared.model.response.QosMeasurementRequestResponse;
import at.alladin.nettest.shared.model.response.SettingsResponse;
import at.alladin.nettest.shared.model.response.SpeedtestResultSubmitResponse;

import at.alladin.rmbt.client.Ping;
import at.alladin.rmbt.client.RMBTTestParameter;
import at.alladin.rmbt.client.SpeedItem;
import at.alladin.rmbt.client.TotalTestResult;
import at.alladin.rmbt.client.db.model.DbHistoryItem;
import at.alladin.rmbt.client.db.model.DbMeasurementItem;
import at.alladin.rmbt.client.db.util.DbUtil;
import at.alladin.rmbt.client.ndt.UiServicesAdapter;
import at.alladin.rmbt.client.v2.task.TaskDesc;
import at.alladin.rmbt.client.v2.task.service.TestMeasurement;
import at.alladin.rmbt.client.v2.task.service.TestMeasurement.TrafficDirection;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ControlServerConnection
{
    
    // url to make request
    private URI hostUri;
    
    private final JSONParser jParser;
    
    private String testId = "";
    
    private long testTime = 0;
    
    private String provider;
    
    private String clientUUID = "";
    
    private URI resultURI;
    
    public TaskDesc udpTaskDesc;
    public TaskDesc dnsTaskDesc;
    public TaskDesc ntpTaskDesc;
    public TaskDesc httpTaskDesc;
    public TaskDesc tcpTaskDesc;
    
    public List<TaskDesc> v2TaskDesc;
    private long startTimeNs = 0;
    
    public ControlServerService controlServerService;
    private MeasurementRequestResponse measurementParam;
    
    public ControlServerConnection(final boolean encryption, final String host, final String pathPrefix, final int port)
    {
        // Creating JSON Parser instance
        jParser = new JSONParser();

        //need to initilize client with http 1.1 (2.0 buggy)
        final List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpClientInterceptor())
                .protocols(protocols)
                .build();

        Retrofit r = new Retrofit.Builder()
        		.baseUrl(getUri(encryption, host, pathPrefix, port, Config.RMBT_CONTROL_MAIN_URL).toString())
        	    .addConverterFactory(GsonConverterFactory.create(Converters.registerDateTime(new GsonBuilder()).create()))
                .client(httpClient)
        		.build();

        System.out.println("BASE URL: " + r.baseUrl().toString() +  " class: " + r.getClass());
        controlServerService = r.create(ControlServerService.class);
    }
    
    private static URI getUri(final boolean encryption, final String host, final String pathPrefix, final int port,
            final String path)
    {
        try
        {
            final String protocol = encryption ? "https" : "http";
            final int defaultPort = encryption ? 443 : 80;
            final String totalPath = (pathPrefix != null ? pathPrefix : "") + Config.RMBT_CONTROL_PATH + path;
            
            if (defaultPort == port)
                return new URL(protocol, host, totalPath).toURI();
            else
                return new URL(protocol, host, port, totalPath).toURI();
        }
        catch (final MalformedURLException e)
        {
            return null;
        }
        catch (final URISyntaxException e)
        {
            return null;
        }
    }

    public void requestQoSTestParametersNew(final String uuid, final boolean encryption, final MeasurementRequest measurementRequest) {
    	try {
    	    final Gson gson = GsonBasicHelper.getDateTimeGsonBuilder().create();
    		final QosMeasurementRequest request = gson.fromJson(gson.toJson(measurementRequest), QosMeasurementRequest.class);
    		request.setClientUuid(uuid);
            request.setMeasurementUuid(measurementParam.getTestUuid());

            //System.out.println(controlServerService.requestQoSObjectives(request).execute().toString());

            QosMeasurementRequestResponse response = controlServerService.requestQoSObjectives(request).execute().body();
            final Map<String, List<Map<String, Object>>> qosMap = new HashMap<>();
            for (Entry<QosMeasurementType, List<Map<String, Object>>> e : response.getObjectives().entrySet()) {
                qosMap.put(e.getKey().getValue(), e.getValue());
            }
    		parseQoSTests(qosMap, encryption);
    	}
    	catch (final Exception e) {
    		e.printStackTrace();
    	}
    }

    public static SettingsResponse requestSettings(final SettingsRequest request, final ControlServerService controlServerService) {
        try {
            System.out.println("SETTINGS REQUEST: " + new Gson().toJson(request));
            final Response<SettingsResponse> response = controlServerService.requestSettings(request).execute();
            System.out.println("RETROFIT RESPONSE to: " + response.raw().request().url().toString() + " - code: " + response.code() + "; Message: " + response.message());
            return response.body();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public SettingsResponse requestSettings(final SettingsRequest request) {
        return requestSettings(request, this.controlServerService);
    }

    public void parseQoSTests(Map<String, List<Map<String, Object>>> testParamsMap, final boolean encryption) throws JSONException {
    	int testPort = 5233;
    	
    	v2TaskDesc = new ArrayList<TaskDesc>();
    	
    	for (Entry<String, List<Map<String, Object>>> e : testParamsMap.entrySet()) {
    		List<Map<String, Object>> paramList = (List<Map<String, Object>>) e.getValue();
    		for (Map<String, Object> params : paramList) {
    			TaskDesc taskDesc = new TaskDesc(measurementParam.getTargetMeasurementServer().getAddress(), testPort, encryption, 
    					measurementParam.getTestToken(), 0, 1, 0, System.nanoTime(), params, e.getKey());
				v2TaskDesc.add(taskDesc);
    		}
    	}
    }
    
    public MeasurementRequest requestNewTestConnection(final String host, final String pathPrefix, final int port,
            final boolean encryption, final ArrayList<String> geoInfo, final String uuid, final ClientType clientType,
            final String clientName, final String clientVersion, final MeasurementRequest request) {
        request.setUuid(uuid);
        request.setClientName(clientName);
        request.setVersion(Config.RMBT_VERSION_NUMBER);
        request.setClientType(clientType);
        request.setSoftwareVersion(clientVersion);
        request.setSoftwareRevision(RevisionHelper.getVerboseRevision());
        request.setLanguage(Locale.getDefault().getLanguage());
        request.setTimezone(TimeZone.getDefault().getID());
        request.setTime(System.currentTimeMillis());

        startTimeNs = System.nanoTime();

        if (geoInfo != null)
        {
            GeoLocation geoLocation = new GeoLocation();
            geoLocation.setTime(new DateTime(Long.parseLong(geoInfo.get(0))));
            geoLocation.setLatitude(Double.valueOf(geoInfo.get(1)));
            geoLocation.setLongitude(Double.valueOf(geoInfo.get(2)));
            geoLocation.setAccuracy(Double.valueOf(geoInfo.get(3)));
            geoLocation.setAltitude(Double.valueOf(geoInfo.get(4)));
            geoLocation.setHeading(Double.valueOf(geoInfo.get(5)));
            geoLocation.setSpeed(Double.valueOf(geoInfo.get(6)));
            geoLocation.setProvider(geoInfo.get(7));
            request.setGeoLocation(geoLocation);
        }

        try {
            System.out.println("RETROFIT SPEED REQUEST: " + controlServerService.requestSpeedTest(request).request().url().toString());
            final Response<MeasurementRequestResponse> response = controlServerService.requestSpeedTest(request).execute();
            //System.out.println("RETROFIT RESPONSE to: " + response.raw().request().url().toString() + " - code: " + response.code() + "; Message: " + response.message());
            measurementParam = response.body();
        }
        catch(final Exception e) {
            e.printStackTrace();
        }

        System.out.println(measurementParam);

        //final JSONObject response = jParser.sendJSONToUrl(hostUri, regData);

        if (measurementParam != null) {
            testTime = System.currentTimeMillis() + 1000 * measurementParam.getTestWait();
        }

        try {
            resultURI = new URI("http://10.9.8.160:8080/RMBTControlServer/result");
        }
        catch (Exception e) {

        }

        return request;
    }


    /**
     * request history list
     * @param clientUuid client uuid
     * @param historyItemDao DAO for history DB (see {@link DbHistoryItem})
     * @param measurementDao DAO for measurement DB (see {@link DbMeasurementItem})
     * @return
     */
    public List<HistoryItem> requestHistory(final String clientUuid, final Dao<DbHistoryItem, String> historyItemDao, final Dao<DbMeasurementItem, String> measurementDao) {
       final List<HistoryItem> remoteHistoryItemList = requestHistory(clientUuid, DbUtil.getLastHistoryTime(historyItemDao));
        if (remoteHistoryItemList != null) {
            DbUtil.syncHistoryList(historyItemDao, measurementDao, remoteHistoryItemList);
        }

        try {
            final List<DbHistoryItem> dbHistoryList = historyItemDao.queryBuilder().orderBy("timeStamp", false).query();
            if (dbHistoryList != null) {
                final Gson gson = new Gson();
                final List<HistoryItem> historyList = new ArrayList<>();
                for (final DbHistoryItem i : dbHistoryList) {
                    historyList.add(gson.fromJson(i.getHistoryItem(), HistoryItem.class));
                }

                return historyList;
            }
        }
        catch (final SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * request history list
     * @param clientUuid client uuid
     * @param lastTimeStamp the timestamp (or 0) that marks the last cache entry
     * @return
     */
    public List<HistoryItem> requestHistory(final String clientUuid, final long lastTimeStamp) {
        try {
            return controlServerService.requestHistory(clientUuid, lastTimeStamp).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * request disassociation feature: remove test from client in DB
     * @param clientUuid
     * @param measurementUuid
     * @return true if request was successful
     */
    public boolean requestDisassociation(final String clientUuid, final String measurementUuid) {
        try {
            final Response<JsonObject> response = controlServerService.requestDisassociation(clientUuid, measurementUuid).execute();
            return response != null && response.code() == 200;
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public SpeedtestResultSubmitResponse sendTestResult(TotalTestResult speedResult, SpeedtestResultSubmitRequest result) {
        try {
            result.setClientUuid(clientUUID);
            result.setUuid(measurementParam.getTestUuid());
            result.setClientName(Config.RMBT_CLIENT_NAME);
            result.setClientVersion(Config.RMBT_VERSION_NUMBER);
            result.setClientLanguage(Locale.getDefault().getLanguage());
            result.setTime(new DateTime(System.currentTimeMillis()));
            result.setToken(measurementParam.getTestToken());
            result.setPortRemote((long)speedResult.port_remote);
            result.setBytesDownload(speedResult.bytes_download);
            result.setBytesUpload(speedResult.bytes_upload);
            result.setTotalBytesDownload(speedResult.totalDownBytes);
            result.setTotalBytesUpload(speedResult.totalUpBytes);
            result.setEncryption(speedResult.encryption);
            result.setIpLocal(speedResult.ip_local.getHostAddress());
            result.setIpServer(speedResult.ip_server.getHostAddress());
            result.setDurationDownloadNs(speedResult.nsec_download);
            result.setDurationUploadNs(speedResult.nsec_upload);
            result.setNumThreads((long)speedResult.num_threads);
            result.setSpeedDownload((long) Math.floor(speedResult.speed_download + 0.5d));
            result.setSpeedUpload((long) Math.floor(speedResult.speed_upload + 0.5d));
            result.setPingShortest(speedResult.ping_shortest);

            result.setInterfaceTotalBytesDownload(speedResult.getTotalTrafficMeasurement(TrafficDirection.RX));
            result.setInterfaceTotalBytesUpload(speedResult.getTotalTrafficMeasurement(TrafficDirection.TX));
            result.setInterfaceDltestBytesDownload(speedResult.getTrafficByTestPart(TestStatus.DOWN, TrafficDirection.RX));
            result.setInterfaceDltestBytesUpload(speedResult.getTrafficByTestPart(TestStatus.DOWN, TrafficDirection.TX));
            result.setInterfaceUltestBytesDownload(speedResult.getTrafficByTestPart(TestStatus.UP, TrafficDirection.RX));
            result.setInterfaceUltestBytesUpload(speedResult.getTrafficByTestPart(TestStatus.UP, TrafficDirection.TX));

            //source port map
            result.setSourcePortMap(speedResult.getSourcePortMap());

            // relative timestamps:
            TestMeasurement dlMeasurement = speedResult.getTestMeasurementByTestPart(TestStatus.DOWN);
            if (dlMeasurement != null) {
                result.setRelativeTimeDlNs(dlMeasurement.getTimeStampStart() - startTimeNs);
            }
            TestMeasurement ulMeasurement = speedResult.getTestMeasurementByTestPart(TestStatus.UP);
            if (ulMeasurement != null) {
                result.setRelativeTimeUlNs(ulMeasurement.getTimeStampStart() - startTimeNs);
            }


            if (speedResult.pings != null && !speedResult.pings.isEmpty()) {
                final List<at.alladin.nettest.shared.model.Ping> pingList = new ArrayList<>();
                result.setPings(pingList);
                for (final Ping ping : speedResult.pings) {
                	// TODO: Should rel_time_ns = time_ns - startTimeNs???
                    pingList.add(ping.toNewPingModel());
                }
            }

            if (speedResult.speedItems != null) {
                final List<SpeedtestResultSubmitRequest.SpeedRawItem> speedItemList = new ArrayList<>();
                result.setSpeedDetail(speedItemList);
                for (SpeedItem item : speedResult.speedItems) {
                    speedItemList.add(item.toNewSpeedModel());
                }
            }

            return controlServerService.sendSpeedTestResults(result.getUuid(), result).execute().body();
        }
        catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // TODO: remove (Applet)
    @Deprecated
    public String sendTestResult(TotalTestResult result, JSONObject additionalValues)
    {
        String errorMsg = null;
        if (resultURI != null)
        {
            
            final JSONObject testData = new JSONObject();
            
            try
            {
                testData.put("client_uuid", clientUUID);
                testData.put("client_name", Config.RMBT_CLIENT_NAME);
                testData.put("client_version", Config.RMBT_CLIENT_NAME);
                testData.put("client_language", Locale.getDefault().getLanguage());
                
                testData.put("time", System.currentTimeMillis());
                
                testData.put("test_token", measurementParam.getTestToken());
                
                testData.put("test_port_remote", result.port_remote);
                testData.put("test_bytes_download", result.bytes_download);
                testData.put("test_bytes_upload", result.bytes_upload);
                testData.put("test_total_bytes_download", result.totalDownBytes);
                testData.put("test_total_bytes_upload", result.totalUpBytes);
                testData.put("test_encryption", result.encryption);
                testData.put("test_ip_local", result.ip_local.getHostAddress());
                testData.put("test_ip_server", result.ip_server.getHostAddress());
                testData.put("test_nsec_download", result.nsec_download);
                testData.put("test_nsec_upload", result.nsec_upload);
                testData.put("test_num_threads", result.num_threads);
                testData.put("test_speed_download", (long) Math.floor(result.speed_download + 0.5d));
                testData.put("test_speed_upload", (long) Math.floor(result.speed_upload + 0.5d));
                testData.put("test_ping_shortest", result.ping_shortest);
               
                //dz todo - add interface values
                
                // total bytes on interface
                testData.put("test_if_bytes_download", result.getTotalTrafficMeasurement(TrafficDirection.RX));
                testData.put("test_if_bytes_upload", result.getTotalTrafficMeasurement(TrafficDirection.TX)); 
                // bytes during download test
                testData.put("testdl_if_bytes_download", result.getTrafficByTestPart(TestStatus.DOWN, TrafficDirection.RX));
                testData.put("testdl_if_bytes_upload", result.getTrafficByTestPart(TestStatus.DOWN, TrafficDirection.TX));
                // bytes during upload test
                testData.put("testul_if_bytes_download", result.getTrafficByTestPart(TestStatus.UP, TrafficDirection.RX));
                testData.put("testul_if_bytes_upload", result.getTrafficByTestPart(TestStatus.UP, TrafficDirection.TX));
                
                //relative timestamps:
                TestMeasurement dlMeasurement = result.getTestMeasurementByTestPart(TestStatus.DOWN);
                if (dlMeasurement != null) {
                    testData.put("time_dl_ns", dlMeasurement.getTimeStampStart() - startTimeNs);
                }
                TestMeasurement ulMeasurement = result.getTestMeasurementByTestPart(TestStatus.UP);
                if (ulMeasurement != null) {
                    testData.put("time_ul_ns", ulMeasurement.getTimeStampStart() - startTimeNs);	
                }                	
                
                
                final JSONArray pingData = new JSONArray();
                
                if (result.pings != null && !result.pings.isEmpty())
                {
                    for (final Ping ping : result.pings)
                    {
                        final JSONObject pingItem = new JSONObject();
                        pingItem.put("value", ping.client);
                        pingItem.put("value_server", ping.server);
                        pingItem.put("time_ns", ping.timeNs - startTimeNs);
                        pingData.put(pingItem);
                    }
                }
                
                testData.put("pings", pingData);
                
                JSONArray speedDetail = new JSONArray();
                
                if (result.speedItems != null)
                {
                    for (SpeedItem item : result.speedItems) {
                        speedDetail.put(item.toJSON());
                    }
                }
                
                testData.put("speed_detail", speedDetail);
                
                addToJSONObject(testData, additionalValues);
                
                // System.out.println(testData.toString(4));
            }
            catch (final JSONException e1)
            {
                errorMsg = "Error gernerating request";
                e1.printStackTrace();
            }
            
            // getting JSON string from URL
            final JSONObject response = jParser.sendJSONToUrl(resultURI, testData);
            
            if (response != null)
                try
                {
                    final JSONArray errorList = response.getJSONArray("error");
                    
                    // System.out.println(response.toString(4));
                    
                    if (errorList.length() == 0)
                    {
                        
                        // System.out.println("All is fine");
                        
                    }
                    else
                    {
                        for (int i = 0; i < errorList.length(); i++)
                        {
                            if (i > 0)
                                errorMsg += "\n";
                            errorMsg += errorList.getString(i);
                        }
                    }
                    
                    // }
                }
                catch (final JSONException e)
                {
                    errorMsg = "Error parsing server response";
                    e.printStackTrace();
                }
        }
        else
            errorMsg = "No URL to send the Data to.";
        
        return errorMsg;
    }
    
    public String sendQoSResult(final TotalTestResult result, final JSONArray qosTestResult) {
        System.out.println("sending qos results ...");
        final JSONObject testData = new JSONObject();
        
        try
        {
            testData.put("client_uuid", clientUUID);
            testData.put("client_name", Config.RMBT_CLIENT_NAME);
            testData.put("client_version", Config.RMBT_VERSION_NUMBER);
            testData.put("client_language", Locale.getDefault().getLanguage());
            
            testData.put("time", System.currentTimeMillis());
            
            testData.put("test_token", measurementParam.getTestToken());
            
           	testData.put("qos_result", qosTestResult);
           	
           	System.out.println(testData.toString(3));
        }
        catch (final JSONException e1)
        {
            e1.printStackTrace();
        }
        
        QosMeasurementResultSubmitRequest request = new QosMeasurementResultSubmitRequest();
        request.setClientName(Config.RMBT_CLIENT_NAME);
        
        try {
        	JsonParser jsonParser = new JsonParser();
        	final List<JsonObject> jsonList = new ArrayList<>();

            for (int i = 0; i < qosTestResult.length(); i++) {
                JsonObject jsonObject = (JsonObject)jsonParser.parse(qosTestResult.getJSONObject(i).toString());
                jsonList.add(jsonObject);
            }
            
            //System.out.println(qosTestResult.toString(4));
            //System.out.println(testData.toString(4));
            
        	request.setQosResultList(jsonList);
        	final JsonObject response = controlServerService.sendQoSResults(measurementParam.getTestUuid(), request).execute().body();
        	System.out.println(response.toString());
        	return response.toString();
        }
        catch (final IOException | JSONException e) {
        	e.printStackTrace();
        }
        
        return null;
    }
    
    public void sendNDTResult(final String host, final String pathPrefix, final int port, final boolean encryption,
            final String clientUUID, final UiServicesAdapter data, final String testUuid)
    {
        hostUri = getUri(encryption, host, pathPrefix, port, Config.RMBT_CONTROL_MAIN_URL);
        this.clientUUID = clientUUID;
        sendNDTResult(data, testUuid);
    }
    
    public void sendNDTResult(final UiServicesAdapter data, final String testUuid)
    {
        final JSONObject testData = new JSONObject();
        
        try
        {
            testData.put("client_uuid", clientUUID);
            testData.put("client_language", Locale.getDefault().getLanguage());
            if (testUuid != null)
                testData.put("test_uuid", testUuid);
            else
                testData.put("test_uuid", measurementParam.getTestUuid());
            testData.put("s2cspd", data.s2cspd);
            testData.put("c2sspd", data.c2sspd);
            testData.put("avgrtt", data.avgrtt);
            testData.put("main", data.sbMain.toString());
            testData.put("stat", data.sbStat.toString());
            testData.put("diag", data.sbDiag.toString());
            testData.put("time_ns", data.getStartTimeNs() - startTimeNs);
            testData.put("time_end_ns", data.getStopTimeNs() - startTimeNs);
            
            jParser.sendJSONToUrl(hostUri.resolve(Config.RMBT_CONTROL_NDT_RESULT_URL), testData);
            
            System.out.println(testData);
        }
        catch (final JSONException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void addToJSONObject(final JSONObject data, final JSONObject additionalValues) throws JSONException
    {
        if (additionalValues != null && additionalValues.length() > 0)
        {
            final JSONArray attr = additionalValues.names();
            for (int i = 0; i < attr.length(); i++)
                data.put(attr.getString(i), additionalValues.get(attr.getString(i)));
        }
    }
    
    public String getRemoteIp()
    {
        //return remoteIp;
        return measurementParam.getClientRemoteIp();
    }
    
    public String getClientUUID()
    {
        return clientUUID;
    }
    
    public String getServerName()
    {
        return measurementParam.getTargetMeasurementServer().getName();
    }
    
    public String getProvider()
    {
        return provider;
    }
    
    public long getTestTime()
    {
        return testTime;
    }
    
    public long getStartTimeNs() {
    	return startTimeNs;
    }
    
    public String getTestId()
    {
        return testId;
    }
    
    public String getTestUuid()
    {
        return measurementParam.getTestUuid();
    }
    
    public RMBTTestParameter getTestParameter(RMBTTestParameter overrideParams)
    {
        String host = measurementParam.getTargetMeasurementServer().getAddress();
        int port = measurementParam.getTargetMeasurementServer().getPort();
        boolean encryption = measurementParam.getTargetMeasurementServer().getIsEncrypted();
        int duration = measurementParam.getDuration();
        int numThreads = measurementParam.getNumThreads();
        int numPings = measurementParam.getNumPings();
        
        if (overrideParams != null)
        {
            if (overrideParams.getHost() != null && overrideParams.getPort() > 0)
            {
                host = overrideParams.getHost();
                encryption = overrideParams.isEncryption();
                port = overrideParams.getPort();
            }
            if (overrideParams.getDuration() > 0) {
                duration = overrideParams.getDuration();
            }
            if (overrideParams.getNumThreads() > 0) {
                numThreads = overrideParams.getNumThreads();
            }
            if (overrideParams.getNumPings() > 0) {
            	numPings = overrideParams.getNumPings();
            }
        }
        return new RMBTTestParameter(host, port, encryption, measurementParam.getTestToken(), duration, numThreads, numPings, testTime);
    }
    
}
