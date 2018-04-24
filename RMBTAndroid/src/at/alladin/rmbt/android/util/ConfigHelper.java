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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import at.alladin.nettest.shared.model.ClientContractInformation;
import at.alladin.nettest.shared.model.Settings;
import at.alladin.nettest.shared.model.response.SettingsResponse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.adapter.result.QoSCategoryPagerAdapter;
import at.alladin.rmbt.client.helper.Config;
import at.alladin.rmbt.client.v2.task.result.QoSTestResultEnum;

public final class ConfigHelper
{
	public final static String PREF_KEY_SWIPE_INTRO_COUNTER = "swipe_intro_counter";
	public final static int SWIPE_INTRO_COUNTER_MAX = 2; 
	
	/**
	 * save log in files locally?
	 */
	public final static boolean DEFAULT_REDIRECT_SYSOUT_TO_FILE = false;
	
	/**
	 * send log files to control server?
	 */
	public final static boolean DEFAULT_SEND_LOG_TO_CONTROL_SERVER = false;

    public static SharedPreferences getSharedPreferences(final Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    public static void setUUID(final Context context, final String uuid)
    {
        final SharedPreferences pref = getSharedPreferences(context);
        final boolean devMode = isOverrideControlServer(pref);
        pref.edit().putString(devMode ? "uuid_dev" : "uuid", uuid).apply();
    }
    
    public static String getUUID(final Context context)
    {
        final SharedPreferences pref = getSharedPreferences(context);
        final boolean devMode = isOverrideControlServer(pref);
        return pref.getString(devMode ? "uuid_dev" : "uuid", "");
    }
    
    public static void setLastNewsUid(final Context context, final long newsUid)
    {
        getSharedPreferences(context).edit().putLong("lastNewsUid", newsUid).apply();
    }
    
    public static void setControlServerVersion(final Context context, final String controlServerVersion)
    {
        getSharedPreferences(context).edit().putString("controlServerVersion", controlServerVersion).apply();
    }
    
    public static String getControlServerVersion(final Context context) {
    	return getSharedPreferences(context).getString("controlServerVersion", null);
    }
    
    public static void setLastIp(final Context context, final String lastIp)
    {
        getSharedPreferences(context).edit().putString("lastIp", lastIp).apply();
    }

    public static String getLastIp(final Context context)
    {
       return getSharedPreferences(context).getString("lastIp", null);
    }

    public static void setLastTestUuid(final Context context, final String lastUuid)
    {
        getSharedPreferences(context).edit().putString("lastTestUuid", lastUuid).apply();
    }

    public static String getLastTestUuid(final Context context, boolean isDeleteOldValue)
    {
    	String lastTestUuid = getSharedPreferences(context).getString("lastTestUuid", null);
    	if (isDeleteOldValue) {
    		setLastTestUuid(context, null);
    	}
    	
    	return lastTestUuid;
    }

    public static long getLastNewsUid(final Context context)
    {
        return getSharedPreferences(context).getLong("lastNewsUid", 0);
    }
    
    public static boolean isSystemOutputRedirectedToFile(final Context context) {
    	return getSharedPreferences(context).getBoolean("dev_debug_output", DEFAULT_REDIRECT_SYSOUT_TO_FILE);
    }

    public static boolean isDontShowMainMenuOnClose(final Context context) {
    	return getSharedPreferences(context).getBoolean("dont_show_menu_before_exit", false);
    }

    public static boolean isGPS(final Context context)
    {
        return ! getSharedPreferences(context).getBoolean("no_gps", false);
    }
    
    public static boolean isLoopMode(final Context context)
    {
        if (! isDevEnabled(context))
            return false;
        return getSharedPreferences(context).getBoolean("loop_mode", false);
    }
    
    public static boolean isLoopModeGPS(final Context context)
    {
        return getSharedPreferences(context).getBoolean("loop_mode_gps", true);
    }

    public static boolean isPlayJingleEnabled(final Context context) {
        return getSharedPreferences(context).getBoolean("play_stupid_jingle", false);
    }

    private static int getInt(final Context context, String key, int defaultId)
    {
        final int def = context.getResources().getInteger(defaultId);
        final String string = getSharedPreferences(context).getString(key, Integer.toString(def));
        try
        {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException e)
        {
            return def;
        }
    }
    
    public static int getLoopModeMaxTests(final Context context)
    {
        return getInt(context, "loop_mode_max_tests", R.integer.default_loop_max_tests);
    }
    
    public static int getLoopModeMinDelay(final Context context)
    {
        return getInt(context, "loop_mode_min_delay", R.integer.default_loop_min_delay);
    }
    
    public static int getLoopModeMaxDelay(final Context context)
    {
        return getInt(context, "loop_mode_max_delay", R.integer.default_loop_max_delay);
    }
    
    public static int getLoopModeMaxMovement(final Context context)
    {
        return getInt(context, "loop_mode_max_movement", R.integer.default_loop_max_movement);
    }

    public static boolean isAnonymousMode(final Context context)
    {
        return getSharedPreferences(context).getBoolean("anonymous_mode", false);
    }

    public static boolean isNDT(final Context context)
    {
        return getSharedPreferences(context).getBoolean("ndt", false);
    }
    
    public static void setNDT(final Context context, final boolean value)
    {
        getSharedPreferences(context).edit().putBoolean("ndt", value).apply();
    }
    
    public static boolean isNDTDecisionMade(final Context context)
    {
        return getSharedPreferences(context).getBoolean("ndt_decision", false);
    }
    
    public static void setNDTDecisionMade(final Context context, final boolean value)
    {
        getSharedPreferences(context).edit().putBoolean("ndt_decision", value).apply();
    }
    
    /////////////////////////////////////////////////////////////////
    // Information Commissioner feature (requested by AKOS)
    /////////////////////////////////////////////////////////////////
    
    public static boolean isInformationCommissioner(final Context context)
    {
        return getSharedPreferences(context).getBoolean("information_commissioner", false);
    }
    
    public static void setInformationCommissioner(final Context context, final boolean value)
    {
        getSharedPreferences(context).edit().putBoolean("information_commissioner", value).apply();
    }
    
    public static boolean isICDecisionMade(final Context context)
    {
        return getSharedPreferences(context).getBoolean("ic_decision", false);
    }
    
    public static void setICDecisionMade(final Context context, final boolean value)
    {
        getSharedPreferences(context).edit().putBoolean("ic_decision", value).apply();
    }
    
    /////////////////////////////////////////////////////////////////   

    public static String getPreviousTestStatus(final Context context)
    {
        return getSharedPreferences(context).getString("previous_test_status", null);
    }
    
    public static void setPreviousTestStatus(final Context context, final String status)
    {
        getSharedPreferences(context).edit().putString("previous_test_status", status).apply();
    }
    
    public static int getTestCounter(final Context context)
    {
        int counter = getSharedPreferences(context).getInt("test_counter", 0);
        return counter;
    }
    
    public static int incAndGetNextTestCounter(final Context context)
    {
        int lastValue = getSharedPreferences(context).getInt("test_counter", 0);
        lastValue++;
        getSharedPreferences(context).edit().putInt("test_counter", lastValue).apply();
        return lastValue;
    }

    public static boolean useNetworkInterfaceIpMethod(final Context context)
    {
        return getSharedPreferences(context).getBoolean("dev_debug_ip_method", false);
    }
    
    public static boolean isRetryRequiredOnIpv6SocketTimeout(final Context context)
    {
        return getSharedPreferences(context).getBoolean("dev_debug_ip_retry_on_socket_timeout", false);
    }

    public static boolean isIpPolling(final Context context)
    {
        return getSharedPreferences(context).getBoolean("dev_debug_ip_poll", true);
    }
    
    public static boolean isTCAccepted(final Context context)
    {
        final int tcNeedVersion = context.getResources().getInteger(R.integer.rmbt_terms_version);
        final int tcAcceptedVersion = getTCAcceptedVersion(context);
        return tcAcceptedVersion == tcNeedVersion;
    }
    
    public static int getTCAcceptedVersion(final Context context)
    {
        return getSharedPreferences(context).getInt("terms_and_conditions_accepted_version", 0);
    }
    
    public static void setTCAccepted(final Context context, final boolean accepted)
    {
        final int tcVersion = context.getResources().getInteger(R.integer.rmbt_terms_version);
        if (accepted)
            getSharedPreferences(context).edit().putInt("terms_and_conditions_accepted_version", tcVersion).apply();
        else
            getSharedPreferences(context).edit().remove("terms_and_conditions_accepted_version").apply();
    }
    
    private static boolean isOverrideControlServer(final SharedPreferences pref)
    {
        return pref.getBoolean("dev_control_override", false);
    }
    
    private static boolean isOverrideMapServer(final SharedPreferences pref)
    {
        return pref.getBoolean("dev_map_override", false);
    }
    
    private static String getDefaultControlServerName(final Context context, final SharedPreferences pref)
    {
        final boolean ipv4Only = pref.getBoolean("ipv4_only", false);
        if (ipv4Only)
            return getCachedControlServerNameIpv4(context);
        else
            return context.getResources().getString(R.string.default_control_host);
    }
    
    public static void setCachedIpv4CheckUrl(String url, Context context) {
    	getSharedPreferences(context).edit().putString("url_ipv4_check", url).apply();
    }

    public static String getCachedIpv4CheckUrl(Context context) {
    	return getSharedPreferences(context).getString("url_ipv4_check", context.getString(R.string.default_control_check_ipv4_url));
    }

    public static void setCachedIpv6CheckUrl(String url, Context context) {
    	getSharedPreferences(context).edit().putString("url_ipv6_check", url).apply();
    }

    public static String getCachedIpv6CheckUrl(Context context) {
    	return getSharedPreferences(context).getString("url_ipv6_check", context.getString(R.string.default_control_check_ipv6_url));
    }

    public static void setCachedControlServerNameIpv4(String url, Context context) {
    	getSharedPreferences(context).edit().putString("cache_control_ipv4", url).apply();
    }

    public static String getCachedControlServerNameIpv4(Context context) {
    	return getSharedPreferences(context).getString("cache_control_ipv4", context.getString(R.string.default_control_host_ipv4_only));
    }

    public static void setCachedControlServerNameIpv6(String url, Context context) {
    	getSharedPreferences(context).edit().putString("cache_control_ipv6", url).apply();
    }

    public static String getCachedControlServerNameIpv6(Context context) {
    	return getSharedPreferences(context).getString("cache_control_ipv6", context.getString(R.string.default_control_host_ipv6_only));
    }

    public static String getControlServerName(final Context context)
    {
        final SharedPreferences pref = getSharedPreferences(context);
        final boolean devMode = isOverrideControlServer(pref);
        if (devMode)
        {
            final boolean noControlServer = pref.getBoolean("dev_no_control_server", false);
            if (noControlServer)
                return null;
            return pref.getString("dev_control_hostname", getDefaultControlServerName(context, pref));
        }
        else
            return getDefaultControlServerName(context, pref);
    }
    
    public static int getControlServerPort(final Context context)
    {
        final SharedPreferences pref = getSharedPreferences(context);
        final boolean devMode = isOverrideControlServer(pref);
        if (devMode)
        {
            final boolean noControlServer = pref.getBoolean("dev_no_control_server", false);
            if (noControlServer)
                return -1;
            
            try
            {
                return Integer.parseInt(pref.getString("dev_control_port", "443"));
            }
            catch (final NumberFormatException e)
            {
                return Config.RMBT_CONTROL_PORT;
            }
        }
        else
            return Config.RMBT_CONTROL_PORT;
    }
    
    public static boolean isControlSeverSSL(final Context context)
    {
        final SharedPreferences pref = getSharedPreferences(context);
        final boolean devMode = isOverrideControlServer(pref);
        if (devMode)
        {
            final boolean noControlServer = pref.getBoolean("dev_no_control_server", false);
            if (noControlServer)
                return false;
            if (pref.contains("dev_control_port"))
                return pref.getBoolean("dev_control_ssl", Config.RMBT_CONTROL_SSL);
            return Config.RMBT_CONTROL_SSL;
        }
        else
            return Config.RMBT_CONTROL_SSL;
    }

    public static boolean isQoSSeverSSL(final Context context)
    {
        final SharedPreferences pref = getSharedPreferences(context);
        return pref.getBoolean("dev_debug_qos_ssl", Config.RMBT_QOS_SSL);
    }

    public static String getMapServerName(final Context context)
    {
        final SharedPreferences pref = getSharedPreferences(context);
        final boolean devMode = isOverrideMapServer(pref);
        if (devMode)
            return pref.getString("dev_map_hostname", "develop");
        else
        {
            if (mapServerSettings.get() != null && mapServerSettings.get().getHost() != null) {
                return mapServerSettings.get().getHost();
            }
            else {
                return getControlServerName(context);
            }
        }
    }
    
    public static int getMapServerPort(final Context context)
    {
        final SharedPreferences pref = getSharedPreferences(context);
        final boolean devMode = isOverrideMapServer(pref);

        if (devMode)
        {
            try
            {
                return Integer.parseInt(pref.getString("dev_map_port", "443"));
            }
            catch (final NumberFormatException e)
            {
                return -1;
            }
        }
        else
        {
            if (mapServerSettings.get() != null && mapServerSettings.get().getPort() > 0) {
                return mapServerSettings.get().getPort();
            }
            else {
                return getControlServerPort(context);
            }
        }
    }
    
    public static boolean isMapSeverSSL(final Context context)
    {
        final SharedPreferences pref = getSharedPreferences(context);
        final boolean devMode = isOverrideMapServer(pref);
        if (devMode)
            return pref.getBoolean("dev_map_ssl", true);
        else
        {
            System.out.println(mapServerSettings.get());
            if (mapServerSettings.get() != null) {
                return mapServerSettings.get().getUseSsl();
            }
            else {
                return isControlSeverSSL(context);
            }
        }
    }

    public static ClientContractInformation getClientContractInformation(final Context context) {
        final SharedPreferences pref = getSharedPreferences(context);
        final ClientContractInformation info = new ClientContractInformation();
        final String contractName = pref.getString("contract_info_contract_name", null);
        if (contractName == null) {
            return null;
        }
        info.setContractName(contractName);

        final Long dl = pref.getLong("contract_info_dl_mbits", Long.MIN_VALUE);
        if (dl == Long.MIN_VALUE) {
            return null;
        }
        info.setDownloadKbps(dl * 1000);

        final Long ul = pref.getLong("contract_info_ul_mbits", Long.MIN_VALUE);
        if (ul == Long.MIN_VALUE) {
            return null;
        }
        info.setUploadKbps(ul * 1000);

        return info;
    }
    
    public static String getTag(final Context context)
    {
        return getSharedPreferences(context).getString("tag", null);
    }
    
    private static AtomicReference<Settings.ServerSettings> mapServerSettings = new AtomicReference<>();
    private static AtomicReference<Settings.ServerSettings> statisticServerSettings = new AtomicReference<>();

    public static void setMapServer(final String host, final int port, final boolean ssl)
    {
        mapServerSettings.set(new Settings.ServerSettings());
        mapServerSettings.get().setHost(host);
        mapServerSettings.get().setPort(port);
        mapServerSettings.get().setUseSsl(ssl);
    }

    public static void setStatisticServer(final Settings.ServerSettings statisticServer) {
        statisticServerSettings.set(statisticServer);
    }
    
    private static ConcurrentMap<String, String> volatileSettings = new ConcurrentHashMap<String, String>();
    
    public static ConcurrentMap<String, String> getVolatileSettings()
    {
        return volatileSettings;
    }
    
    public static String getVolatileSetting(String key)
    {
        return volatileSettings.get(key);
    }



    public static void setCachedStatisticsUrl(String url, Context context) {
    	getSharedPreferences(context).edit().putString("cache_statistics_url", url).apply();
    }
    
    public static String getCachedStatisticsUrl(Context context) {
    	return context.getString(R.string.url_statistics);
    	//return getSharedPreferences(context).getString("cache_statistics_url", null);
    }

    public static void setCachedPositionOptions(final Settings.AdvancedPosition<SettingsResponse.TranslatedPositionOption> positionOptions,
                                                final Context context) {

        getSharedPreferences(context).edit().putString("position_options", new Gson().toJson(positionOptions)).apply();
    }

    public static Settings.AdvancedPosition<SettingsResponse.TranslatedPositionOption> getCachedPositionOptions(final Context context) {
        final String positionJson = getSharedPreferences(context).getString("position_options", "{}");
        return new Gson().fromJson(positionJson,
                new TypeToken< Settings.AdvancedPosition<SettingsResponse.TranslatedPositionOption>>() {}.getType());
    }

    public static void setCachedHelpUrl(String url, Context context) {
    	getSharedPreferences(context).edit().putString("cache_help_url", url).apply();
    }
    
    public static String getCachedHelpUrl(Context context) {
    	return context.getString(R.string.url_help);
    	//return getSharedPreferences(context).getString("cache_help_url", null);
    }

    public static boolean isNerdModeEnabled(final Context context) {
        return getSharedPreferences(context).getBoolean("nerd_mode", false);
    }

    public static boolean isQosEnabled(final Context context) {
        return getSharedPreferences(context).getBoolean("enable_qos", false);
    }

    public static void setAdvancedPositionForTest(final String advancedPositionValue, final Context context) {
        getSharedPreferences(context).edit().putString("advanced_position", advancedPositionValue).apply();
    }

    public static String getAdvancedPositionForTest(final Context context) {
        return getSharedPreferences(context).getString("advanced_position", null);
    }

    /**
     * 
     * @param context
     * @param qosNamesMap
     */
    public static void setCachedQoSNames(Map<String, String> qosNamesMap, Context context) {
    	final SharedPreferences prefs = context.getSharedPreferences("cache_qos_names", Context.MODE_PRIVATE);
    	final SharedPreferences.Editor edit = prefs.edit();
    	for (Entry<String, String> e : qosNamesMap.entrySet()) {
    		edit.putString(e.getKey(), e.getValue());
    	}
    	edit.apply();
    }
    
    /**
     * 
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<QoSTestResultEnum, String> getCachedQoSNames(Context context) {
    	final Map<QoSTestResultEnum, String> namesMap = new HashMap<QoSTestResultEnum, String>();
    	final SharedPreferences prefs = context.getSharedPreferences("cache_qos_names", Context.MODE_PRIVATE);
    	final Map<String, ?> cacheMap = prefs.getAll();
    	
    	if (cacheMap != null && cacheMap.size() > 0) {
    		Iterator<?> namesIterator = cacheMap.entrySet().iterator();
    		while(namesIterator.hasNext()) {
    			Entry<String, String> name = (Entry<String, String>) namesIterator.next();
    			try {
    				namesMap.put(QoSTestResultEnum.valueOf(name.getKey().toUpperCase(Locale.US)), name.getValue());
    			}
    			catch (IllegalArgumentException e) {
    				//in case the qos type doesn't exist
    				e.printStackTrace();
    			}
    		}
    	}
    	else {
    		for (Entry<QoSTestResultEnum, Integer> e : QoSCategoryPagerAdapter.TITLE_MAP.entrySet()) {
    			String name = context.getString(e.getValue());
    			namesMap.put(e.getKey(), name);
    		}
    	}
    	
    	return namesMap;
    }
    
    /**
     * 
     * @param testType
     * @param context
     * @return
     */
    public static String getCachedQoSNameByTestType(QoSTestResultEnum testType, Context context) {
    	final SharedPreferences prefs = context.getSharedPreferences("cache_qos_names", Context.MODE_PRIVATE);
    	String name = prefs.getString(testType.name(), null);
    	if (name == null) {
    		if (QoSCategoryPagerAdapter.TITLE_MAP.containsKey(testType)) {
    			name = context.getString(QoSCategoryPagerAdapter.TITLE_MAP.get(testType));
    		}
    		else {
    			name = testType.name();
    		}
    	}

    	return name;
    }

    public static void setSAP(final boolean sapEnabled, final Context context) {
        getSharedPreferences(context).edit().putBoolean("super_ant_powers", sapEnabled).apply();
    }

    public static boolean isSAPEnabled(final Context context) {
        return getSharedPreferences(context).getBoolean("super_ant_powers", false);
    }


    public static boolean isDevEnabled(final Context ctx)
    {
        return PackageManager.SIGNATURE_MATCH == ctx.getPackageManager().checkSignatures(ctx.getPackageName(), at.alladin.rmbt.android.util.Config.RMBT_DEV_UNLOCK_PACKAGE_NAME);
    }
}
