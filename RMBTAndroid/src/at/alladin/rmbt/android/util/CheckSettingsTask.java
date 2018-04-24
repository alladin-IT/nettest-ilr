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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.json.JSONArray;

import android.os.AsyncTask;
import android.util.Log;

import at.alladin.nettest.shared.model.Settings;
import at.alladin.nettest.shared.model.response.SettingsResponse;

import at.alladin.rmbt.android.main.RMBTMainActivity;

/**
 * 
 * @author
 * 
 */
public class CheckSettingsTask extends AsyncTask<Void, Void, SettingsResponse>
{
    
    /**
	 * 
	 */
    private static final String DEBUG_TAG = "CheckSettingsTask";
    
    /**
	 * 
	 */
    private final RMBTMainActivity activity;
    
    /**
	 * 
	 */
    private ControlServerConnection serverConn;
    
    /**
	 * 
	 */
    private EndTaskListener<JSONArray> endTaskListener;
    
    /**
	 * 
	 */
    private boolean hasError = false;
    
    /**
     * 
     * @param activity
     */
    public CheckSettingsTask(final RMBTMainActivity activity)
    {
        this.activity = activity;
        
    }
    
    /**
	 * 
	 */
    @Override
    protected SettingsResponse doInBackground(final Void... params)
    {
        JSONArray resultList = null;
        
        serverConn = new ControlServerConnection(activity.getApplicationContext());
        
        return serverConn.requestSettings();
    }
    
    /**
	 * 
	 */
    @Override
    protected void onCancelled()
    {
        if (serverConn != null)
        {
            serverConn.unload();
            serverConn = null;
        }
    }
    
    /**
	 * 
	 */
    @Override
    protected void onPostExecute(final SettingsResponse settings)
    {
        try {
            if (serverConn.hasError()) {
                hasError = true;
            }
            else if (settings != null) {
                /* UUID */

                final String uuid = settings.getClient().getUuid();
                if (uuid != null && uuid.length() != 0) {
                    ConfigHelper.setUUID(activity.getApplicationContext(), uuid);
                }

                settings.getAdvancedPosition();

                /* urls */

                final ConcurrentMap<String, String> volatileSettings = ConfigHelper.getVolatileSettings();

                final Settings.UrlSettings urls = settings.getSettings().getUrls();

                if (urls != null)
                {
                    //TODO: volatileSettings (especially url_open_data_prefix)
                    //volatileSettings.put("url_" + key, value);
                    if (urls.getStatistics() != null) {
                        ConfigHelper.setCachedStatisticsUrl(urls.getStatistics(), activity);
                    }

                    if (settings.getSettings().getControlServerIpv4Host() != null) {
                        ConfigHelper.setCachedControlServerNameIpv4(settings.getSettings().getControlServerIpv4Host(), activity);
                    }

                    if (settings.getSettings().getControlServerIpv6Host() != null) {
                        ConfigHelper.setCachedControlServerNameIpv6(settings.getSettings().getControlServerIpv6Host(), activity);
                    }

                    if (urls.getIpv4IpCheck() != null) {
                        ConfigHelper.setCachedIpv4CheckUrl(urls.getIpv4IpCheck(), activity);
                    }

                    if (urls.getIpv6IpCheck() != null) {
                        ConfigHelper.setCachedIpv6CheckUrl(urls.getIpv6IpCheck(), activity);
                    }

                    if (urls.getOpendataPrefix() != null) {
                        ConfigHelper.getVolatileSettings().put("url_open_data_prefix", urls.getOpendataPrefix());
                    }
                }

                /* qos names */

                if (settings.getQosMeasurementTypes() != null) {

                    final Map<String, String> qosNamesMap = new HashMap<String, String>();
                    for (SettingsResponse.QosMeasurementTypeResponse type : settings.getQosMeasurementTypes()) {
                        //qosNamesMap.put(type.getType().name(), type.getName());
                    }
                    ConfigHelper.setCachedQoSNames(qosNamesMap, activity);
                }

                /* advanced position options */

                if (settings.getAdvancedPosition() != null) {
                    ConfigHelper.setCachedPositionOptions(settings.getAdvancedPosition(), activity);
                    System.out.println(ConfigHelper.getCachedPositionOptions(activity));
                }

                /* map server */

                if (settings.getSettings().getMapServer() != null) {
                    final String host = settings.getSettings().getMapServer().getHost();
                    final int port= settings.getSettings().getMapServer().getPort();
                    final boolean ssl = settings.getSettings().getMapServer().getUseSsl();
                    if (host != null && port > 0)
                        ConfigHelper.setMapServer(host, port, ssl);
                }

                if (settings.getSettings().getStatisticServer() != null) {
                    if (settings.getSettings().getStatisticServer().getHost() != null && settings.getSettings().getStatisticServer().getPort() > 0) {
                        ConfigHelper.setStatisticServer(settings.getSettings().getStatisticServer());
                    }
                }

                /* control server version */
                if (settings.getSettings().getVersions() != null)
                {
                    if (settings.getSettings().getVersions().getControlServerVersion() != null) {
                        ConfigHelper.setControlServerVersion(activity, settings.getSettings().getVersions().getControlServerVersion());
                    }
                }

                // ///////////////////////////////////////////////////////
                // HISTORY / FILTER

                /*
                final JSONObject historyObject = resultListItem.getJSONObject("history");

                final JSONArray deviceArray = historyObject.getJSONArray("devices");
                final JSONArray networkArray = historyObject.getJSONArray("networks");

                final String historyDevices[] = new String[deviceArray.length()];

                for (int i = 0; i < deviceArray.length(); i++)
                    historyDevices[i] = deviceArray.getString(i);

                final String historyNetworks[] = new String[networkArray.length()];

                for (int i = 0; i < networkArray.length(); i++)
                    historyNetworks[i] = networkArray.getString(i);

                // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                activity.setSettings(historyDevices, historyNetworks);

                */

                activity.setHistoryDirty(true);

            }
            else {
                Log.i(DEBUG_TAG, "LEERE LISTE");
            }
        }
        finally  {
            if (endTaskListener != null)
                endTaskListener.taskEnded(null);
        }
    }
    
    /**
     * 
     * @param endTaskListener
     */
    public void setEndTaskListener(final EndTaskListener<JSONArray> endTaskListener)
    {
        this.endTaskListener = endTaskListener;
    }
    
    /**
     * 
     * @return
     */
    public boolean hasError()
    {
        return hasError;
    }
}
