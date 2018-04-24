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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.android.map.MapListEntry;
import at.alladin.rmbt.android.map.MapListSection;
import at.alladin.rmbt.android.map.MapProperties;
import at.alladin.rmbt.android.map.MapProperties.MapOverlay;

import at.alladin.openrmbt.android.R;

public class GetMapOptionsInfoTask extends AsyncTask<Void, Void, JSONObject>
{
    /**
     * 
     */
    private static final String DEBUG_TAG = "GetMapOptionsInfoTask";
    
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
    public GetMapOptionsInfoTask(final RMBTMainActivity activity)
    {
        this.activity = activity;
    }
    
    /**
     * 
     */
    @Override
    protected JSONObject doInBackground(final Void... params)
    {
        JSONObject result = null;
        
        serverConn = new ControlServerConnection(activity.getApplicationContext(), true);
        
        try
        {
            result = serverConn.requestMapOptionsInfo();
            return result;
        }
        catch (Exception e)
        {
            return null;
        }
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
    protected void onPostExecute(final JSONObject result)
    {
        if (serverConn.hasError())
            hasError = true;
        else if (result != null)
        {
            try
            {
                final JSONObject mapSettingsObject = result.getJSONObject("mapfilter");
                
                // /
                
                // ////////////////////////////////////////////////////
                // MAP / CHOOSE
                
                final JSONArray mapTypeArray = mapSettingsObject.getJSONArray("mapTypes");
                
//                Log.d(DEBUG_TAG, mapTypeArray.toString(4));
                
                // /
                
                final ArrayList<MapListSection> mapListSectionList = new ArrayList<MapListSection>();
                
                for (int cnt = 0; cnt < mapTypeArray.length(); cnt++)
                {
                    
                    final JSONObject t = mapTypeArray.getJSONObject(cnt);
                    
                    final String sectionTitle = t.getString("title");
                    
                    final JSONArray objectOptionsArray = t.getJSONArray("options");
                    
                    // /
                    
                    final List<MapListEntry> mapListEntryList = new ArrayList<MapListEntry>();
                    
                    for (int cnt2 = 0; cnt2 < objectOptionsArray.length(); cnt2++)
                    {
                        
                        final JSONObject s = objectOptionsArray.getJSONObject(cnt2);
                        
                        final String entryTitle = s.getString("title");
                        final String entrySummary = s.getString("summary");
                        final String value = s.getString("map_options");
                        final String overlayType = s.getString("overlay_type");
                        
                        final MapListEntry mapListEntry = new MapListEntry(entryTitle, entrySummary);
                        
                        mapListEntry.setKey("map_options");
                        mapListEntry.setValue(value);
                        mapListEntry.setOverlayType(overlayType);
                        
                        mapListEntryList.add(mapListEntry);
                    }
                    
                    final MapListSection mapListSection = new MapListSection(sectionTitle, mapListEntryList);
                    mapListSectionList.add(mapListSection);
                }
                
                // ////////////////////////////////////////////////////
                // MAP / FILTER
                
                final JSONObject mapFiltersObject = mapSettingsObject.getJSONObject("mapFilters");
                final HashMap<String,List<MapListSection>> mapFilterListSectionListHash = new HashMap<String,List<MapListSection>>();
                
//                Log.d(DEBUG_TAG, mapFilterArray.toString(4));

                Iterator<String> keyIt = mapFiltersObject.keys();
                while (keyIt.hasNext())
                //for (final String typeKey : new String[]{ "mobile", "wifi", "all" })
                {
                    final String typeKey = keyIt.next();
                    final JSONArray mapFilterArray = mapFiltersObject.getJSONArray(typeKey);
                    final List<MapListSection> mapFilterListSectionList = new ArrayList<MapListSection>();
                    mapFilterListSectionListHash.put(typeKey, mapFilterListSectionList);
                    
                    
                 // add map appearance option (satellite, no satellite)
                    final MapListSection appearanceSection = new MapListSection(
                            activity.getString(R.string.map_appearance_header), Arrays.asList(
                                    new MapListEntry(activity.getString(R.string.map_appearance_nosat_title), activity
                                            .getString(R.string.map_appearance_nosat_summary), true,
                                            MapProperties.MAP_SAT_KEY, MapProperties.MAP_NOSAT_VALUE, true),
                                    new MapListEntry(activity.getString(R.string.map_appearance_sat_title), activity
                                            .getString(R.string.map_appearance_sat_summary), MapProperties.MAP_SAT_KEY,
                                            MapProperties.MAP_SAT_VALUE)));
                    
                    mapFilterListSectionList.add(appearanceSection);
                    
                    // add overlay option (heatmap, points)
                    final MapListSection overlaySection = new MapListSection(
                            activity.getString(R.string.map_overlay_header), Arrays.asList(
                                    new MapListEntry(activity.getString(R.string.map_overlay_auto_title), activity
                                            .getString(R.string.map_overlay_auto_summary), true,
                                            MapProperties.MAP_OVERLAY_KEY, MapOverlay.AUTO.name(), true),
                                    new MapListEntry(activity.getString(R.string.map_overlay_heatmap_title), activity
                                            .getString(R.string.map_overlay_heatmap_summary),
                                            MapProperties.MAP_OVERLAY_KEY, MapOverlay.HEATMAP.name()),
                                    new MapListEntry(activity.getString(R.string.map_overlay_points_title), activity
                                            .getString(R.string.map_overlay_points_summary), MapProperties.MAP_OVERLAY_KEY,
                                            MapOverlay.POINTS.name())
                                    /*
                                    new MapListEntry(activity.getString(R.string.map_overlay_regions_title), activity
                                            .getString(R.string.map_overlay_regions_summary), MapProperties.MAP_OVERLAY_KEY,
                                            MapOverlay.REGIONS.name())
                                            */
                                    ));
                    
                    mapFilterListSectionList.add(overlaySection);
                    
                    // add other filter options
                    
                    for (int cnt = 0; cnt < mapFilterArray.length(); cnt++)
                    {
                        
                        final JSONObject t = mapFilterArray.getJSONObject(cnt);
                        
                        final String sectionTitle = t.getString("title");
                        
                        final JSONArray objectOptionsArray = t.getJSONArray("options");
                        
                        // /
                        
                        final List<MapListEntry> mapListEntryList = new ArrayList<MapListEntry>();
                        
                        boolean haveDefault = false;
                        
                        for (int cnt2 = 0; cnt2 < objectOptionsArray.length(); cnt2++)
                        {
                            
                            final JSONObject s = objectOptionsArray.getJSONObject(cnt2);
                            
                            final String entryTitle = s.getString("title");
                            final String entrySummary = s.getString("summary");
                            final boolean entryDefault = s.optBoolean("default", false);
                            
                            s.remove("title");
                            s.remove("summary");
                            s.remove("default");
                            
                            //
                            
                            final MapListEntry mapListEntry = new MapListEntry(entryTitle, entrySummary);
                            
                            //
                            
                            final JSONArray sArray = s.names();
                            
                            if (sArray != null && sArray.length() > 0)
                            {
                                
                                final String key = sArray.getString(0);
                                
                                mapListEntry.setKey(key);
                                mapListEntry.setValue(s.getString(key));
                            }
                            
                            mapListEntry.setChecked(entryDefault && ! haveDefault);
                            mapListEntry.setDefault(entryDefault);
                            if (entryDefault)
                                haveDefault = true;
                            
                            // /
                            
                            mapListEntryList.add(mapListEntry);
                        }
                        
                        if (! haveDefault && mapListEntryList.size() > 0)
                        {
                            final MapListEntry first = mapListEntryList.get(0);
                            first.setChecked(true); // set first if we had no default
                            first.setDefault(true);
                        }
                        
                        final MapListSection mapListSection = new MapListSection(sectionTitle, mapListEntryList);
                        mapFilterListSectionList.add(mapListSection);
                    }
                }
                
                
                
                // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                
                // map type
                final MapListEntry entry = mapListSectionList.get(0).getMapListEntryList().get(0);
                
                activity.setCurrentMapType(entry);
                                
                activity.setMapTypeListSectionList(mapListSectionList);
                activity.setMapFilterListSectionListMap(mapFilterListSectionListHash);
                
            }
            catch (final JSONException e)
            {
                e.printStackTrace();
            }
            
        }
        else
            Log.i(DEBUG_TAG, "LEERE LISTE");
        
        if (endTaskListener != null)
        {
            final JSONArray array = new JSONArray();
            array.put(result);
            endTaskListener.taskEnded(array);
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
