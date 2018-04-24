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

package at.alladin.rmbt.android.util;

import android.content.Context;
import android.util.Log;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import at.alladin.nettest.shared.model.Client;
import at.alladin.nettest.shared.model.request.SettingsRequest;
import at.alladin.nettest.shared.model.response.IpResponse;
import at.alladin.nettest.shared.model.response.SettingsResponse;
import at.alladin.nettest.shared.model.response.SpeedtestDetailGroupResultResponse;
import at.alladin.nettest.shared.model.response.SpeedtestDetailResultResponse;
import at.alladin.nettest.shared.model.response.SpeedtestResultResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import at.alladin.rmbt.android.map.MapProperties;
import at.alladin.rmbt.client.helper.Config;
import at.alladin.rmbt.client.helper.ControlServerService;
import at.alladin.rmbt.client.helper.HttpClientInterceptor;
import at.alladin.rmbt.client.helper.JSONParser;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ControlServerConnection
{
    
	public static enum UriType {
		DEFAULT_HOSTNAME,
		HOSTNAME_IPV4,
		HOSTNAME_IPV6
	}
	
    private static final String DEBUG_TAG = "ControlServerConnection";
    
    private static String hostname;
    
    private static String hostname4;
    
    private static String hostname6;
    
    private static int port;
    
    private boolean encryption;
    
    private JSONParser jParser;
    
    private Context context;
    
    private String errorMsg = "";
    
    private boolean hasError = false;
    
    private boolean useMapServerPath = false;

    private ControlServerService controlServerService;

    private ControlServerService controlServerService6;
    
    private String getUUID()
    {
        return ConfigHelper.getUUID(context.getApplicationContext());
    }
    
    private URI getUri(final String path) {
    	return getUri(path, UriType.DEFAULT_HOSTNAME);
    }
    
    private URI getUri(final String path, final UriType uriType)
    {
        try
        {
            String protocol = encryption ? "https" : "http";
            final int defaultPort = encryption ? 443 : 80;
            final String totalPath;
            if (useMapServerPath)
                totalPath = path;
            else
                totalPath = Config.RMBT_CONTROL_PATH + path;
            
            String host = hostname;
            
            switch(uriType) {
            case HOSTNAME_IPV4:
            	host = hostname4;
            	break;
            case HOSTNAME_IPV6:
            	host = hostname6;
            	break;
            case DEFAULT_HOSTNAME:
            default:
            	host = hostname;
            }
            
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
    
    public ControlServerConnection(final Context context)
    {
        setupServer(context, false);
    }
    
    public ControlServerConnection(final Context context, final boolean useMapServer)
    {
        setupServer(context, useMapServer);
    }
    
    private void setupServer(final Context context, final boolean useMapServerPath)
    {    	 
        jParser = new JSONParser();
        hasError = false;
        
        this.context = context;
        
        this.useMapServerPath = useMapServerPath;
        
        hostname4 = ConfigHelper.getCachedControlServerNameIpv4(context);
        hostname6 = ConfigHelper.getCachedControlServerNameIpv6(context);

        if (useMapServerPath)
        {
            encryption = ConfigHelper.isMapSeverSSL(context);
            hostname = ConfigHelper.getMapServerName(context);
            port = ConfigHelper.getMapServerPort(context);
        }
        else
        {
            encryption = ConfigHelper.isControlSeverSSL(context);
            hostname = ConfigHelper.getControlServerName(context);
            port = ConfigHelper.getControlServerPort(context);
        }
        //need to initilize client with http 1.1 (2.0 buggy)
        final List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpClientInterceptor())
                .protocols(protocols)
                .build();

        try {
            Retrofit r = new Retrofit.Builder()
                    .baseUrl((encryption ? "https://" : "http://") + hostname + ":" + port + Config.RMBT_CONTROL_PATH + Config.RMBT_CONTROL_MAIN_URL)
                    .addConverterFactory(GsonConverterFactory.create(Converters.registerDateTime(new GsonBuilder()).create()))
                    .client(httpClient)
                    .build();

            System.out.println("BASE URL: " + r.baseUrl().toString());
            controlServerService = r.create(ControlServerService.class);


            Retrofit r6 = new Retrofit.Builder()
                    .baseUrl((encryption ? "https://" : "http://") + hostname6 + ":" + port + Config.RMBT_CONTROL_PATH + Config.RMBT_CONTROL_MAIN_URL)
                    .addConverterFactory(GsonConverterFactory.create(Converters.registerDateTime(new GsonBuilder()).create()))
                    .client(httpClient)
                    .build();

            System.out.println("BASE URL v6: " + r6.baseUrl().toString());
            controlServerService6 = r6.create(ControlServerService.class);
        }
        catch (final Exception e) {
            //fix crash if no control server is provided (tough it never should occur)
            e.printStackTrace();
        }
    }
    
    public boolean unload()
    {
        jParser = null;
        
        return true;
    }
    
    private JSONArray sendRequest(final URI hostUrl, final JSONObject requestData, final String fieldName)
    {
        // getting JSON string from URL
        //Log.d(DEBUG_TAG, "request to "+ hostUrl);    	
        final JSONObject response = jParser.sendJSONToUrl(hostUrl, requestData);
        
        if (response != null)
            try
            {
                final JSONArray errorList = response.optJSONArray("error");
                
                if (errorList == null || errorList.length() == 0)
                {
                	return getResponseField(response, fieldName);
                }
                else
                {
                    hasError = true;
                    for (int i = 0; i < errorList.length(); i++)
                    {
                        
                        if (i > 0)
                            errorMsg += "\n";
                        errorMsg += errorList.getString(i);
                    }
                  
                    System.out.println(errorMsg);
                    
                    //return getResponseField(response, fieldName);
                }
                
                // }
            }
            catch (final JSONException e)
            {
                hasError = true;
                errorMsg = "Error parsing server response";
                e.printStackTrace();
            }
        else
        {
            hasError = true;
            errorMsg = "No response";
        }
        
        return null;
        
    }
    
    private static JSONArray getResponseField(JSONObject response, String fieldName) throws JSONException {
       	if (fieldName != null) {
            return response.getJSONArray(fieldName);	
    	}
    	else {
    		JSONArray array = new JSONArray();
    		array.put(response);
    		return array;
    	}
    }
    
    public JSONArray requestNews(final long lastNewsUid)
    {
        
        hasError = false;
        
        final URI hostUrl = getUri(Config.RMBT_NEWS_HOST_URL);
        
        Log.i(DEBUG_TAG,"Newsrequest to " + hostUrl);
        
        final JSONObject requestData = new JSONObject();
        
        try
        {
            InformationCollector.fillBasicInfo(requestData, context);
            
            requestData.put("uuid", getUUID());
            requestData.put("lastNewsUid", lastNewsUid);
        }
        catch (final JSONException e)
        {
            hasError = true;
            errorMsg = "Error gernerating request";
        }
        
        return sendRequest(hostUrl, requestData, "news");
        
    }
    
    public JSONArray sendLogReport(final JSONObject requestData)  {        
        final URI hostUrl = getUri(Config.RMBT_LOG_HOST_URL);
        
        try
        {
            Log.i(DEBUG_TAG,"LOG request to " + hostUrl);

            InformationCollector.fillBasicInfo(requestData, context);
            
            requestData.put("uuid", getUUID());
        }
        catch (final Exception e)
        {
            hasError = true;
            errorMsg = "Error gernerating request";
		}
        
        return sendRequest(hostUrl, requestData, null);	
    }

    public JSONArray requestIp(boolean isIpv6)
    {
        try {
            final IpResponse result = isIpv6 ?
                    controlServerService6.requestIp().execute().body() :
                    controlServerService.requestIp().execute().body();
            final JSONArray json = new JSONArray();
            json.put(new JSONObject(new Gson().toJson(result)));
            return json;
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONArray requestTestResult(final String testUuid) {
        try {
            final SpeedtestResultResponse result = controlServerService.requestSpeedTestResult(testUuid).execute().body();
            final JSONArray json = new JSONArray();
            json.put(new JSONObject(new Gson().toJson(result)));
            return json;
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public JSONObject requestOpenDataTestResult(final String testUuid, final String openTestUuid) {
        hasError = false;
        
        final URI hostUrl = getUri(Config.RMBT_TESTRESULT_OPENDATA_HOST_URL + openTestUuid);
        
        Log.i(DEBUG_TAG,"RMBTTest OpenData Result request to " + hostUrl);
        
        final JSONObject requestData = new JSONObject();
        
        try
        {
            InformationCollector.fillBasicInfo(requestData, context);
            requestData.put("test_uuid", testUuid);
            requestData.put("open_test_uuid", openTestUuid);
            requestData.put("uuid", getUUID());
        }
        catch (final JSONException e1)
        {
            hasError = true;
            errorMsg = "Error gernerating request";
            // e1.printStackTrace();
        }
        
        return jParser.getURL(hostUrl);
        //return sendRequest(hostUrl, requestData, null);
    }

    public JSONArray requestTestResultQoS(final String testUuid) {
        try {
            final JsonObject json = controlServerService.getQoSResults(testUuid).execute().body();
            final JSONArray result = new JSONArray();
            result.put(new JSONObject(json.toString()));
            return result;
        }
        catch(final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONArray requestTestResultDetail(final String testUuid) {
        try {
            final SpeedtestDetailResultResponse response = controlServerService.requestSpeedTestDetailResult(testUuid).execute().body();
            System.out.println(response);
            final JSONArray json = new JSONArray();
            return new JSONArray(new Gson().toJson(response.getTestResultDetailList()));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONArray requestTestResultDetailGroup(final String testUuid){
        try {
            final SpeedtestDetailGroupResultResponse response = controlServerService.requestSpeedTestDetailGroupResult(testUuid).execute().body();
            System.out.println(response);
            return new JSONArray(new Gson().toJson(response.getSpeedtestDetailGroups()));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public JSONArray requestSyncCode(final String uuid, final String syncCode)
    {
        
        hasError = false;
        
        final URI hostUrl = getUri(Config.RMBT_SYNC_HOST_URL);
        
        Log.i(DEBUG_TAG,"Sync request to " + hostUrl);
        
        final JSONObject requestData = new JSONObject();
        
        try
        {
            InformationCollector.fillBasicInfo(requestData, context);
            requestData.put("uuid", uuid);
            
            if (syncCode.length() > 0)
                requestData.put("sync_code", syncCode);
            
        }
        catch (final JSONException e1)
        {
            hasError = true;
            errorMsg = "Error gernerating request";
            // e1.printStackTrace();
        }
        
        return sendRequest(hostUrl, requestData, "sync");
    }

    public SettingsResponse requestSettings() {
        final SettingsRequest request = InformationCollector.fillBasicInfo(SettingsRequest.class, context);
        System.out.println(request);

        final Client client = new Client();
        request.setClient(client);
        client.setUuid(getUUID());

        final int tcAcceptedVersion = ConfigHelper.getTCAcceptedVersion(context);
        client.setTermsAndConditionsAcceptedVersion(tcAcceptedVersion);
        if (tcAcceptedVersion > 0) {// for server backward compatibility
            client.setTermsAndConditionsAccepted(true);
        }

        return at.alladin.rmbt.client.helper.ControlServerConnection.requestSettings(request, this.controlServerService);
    }
    
    public JSONObject requestMapOptionsInfo()
    {
        
        hasError = false;
        
        final URI hostUrl = getUri(MapProperties.MAP_OPTIONS_PATH);
        
        final JSONObject requestData = new JSONObject();
        
        try
        {
            requestData.put("language", Locale.getDefault().getLanguage());
        }
        catch (final JSONException e1)
        {
            hasError = true;
            errorMsg = "Error gernerating request";
        }
        
        Log.i(DEBUG_TAG, "request to " + hostUrl);
        final JSONObject response = jParser.sendJSONToUrl(hostUrl, requestData);
        return response;
    }
    
    public JSONArray requestMapMarker(final double lat, final double lon, final int zoom, final int size,
            final Map<String, String> optionMap)
    {
        hasError = false;

        final URI hostUrl = getUri(MapProperties.MARKER_PATH);
        
        Log.i(DEBUG_TAG,"MapMarker request to " + hostUrl);
        
        final JSONObject requestData = new JSONObject();
        
        try
        {
            requestData.put("language", Locale.getDefault().getLanguage());
            
            final JSONObject coords = new JSONObject();
            coords.put("lat", lat);
            coords.put("lon", lon);
            coords.put("z", zoom);
            coords.put("size", size);
            requestData.put("coords", coords);

            //send client uuid to get measurement uuids for own tests
            //requestData.put("client_uuid", getUUID());

            final JSONObject filter = new JSONObject();
            final JSONObject options = new JSONObject();
            
            for (final String key : optionMap.keySet())
            {
                
                if (MapProperties.MAP_OVERLAY_KEY.equals(key))
                    // skip map_overlay_key
                    continue;
                
                final String value = optionMap.get(key);
                
                if (value != null && value.length() > 0)
                    if (key.equals("map_options")) {
                        options.put(key, value);
                    }
                    else {
                        filter.put(key, value);
                    }
            }

            //remove highlight from markers request or else foreign tests will not be returned by map server
            filter.put("highlight_uuid", getUUID());
            //instead add the prioritize filter to affect the result ordering in favor of the current client uuid
            filter.put("prioritize", getUUID());
            requestData.put("filter", filter);
            requestData.put("options", options);

            //System.out.println(requestData);
        }
        catch (final JSONException e1)
        {
            hasError = true;
            errorMsg = "Error gernerating request";
            // e1.printStackTrace();
        }

        return sendRequest(hostUrl, requestData, "measurements");
    }
    
    public boolean hasError()
    {
        return hasError;
    }
    
}
