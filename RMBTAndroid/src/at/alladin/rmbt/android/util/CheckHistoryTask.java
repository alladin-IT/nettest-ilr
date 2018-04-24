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

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.client.db.model.DbHistoryItem;
import at.alladin.rmbt.client.db.model.DbMeasurementItem;
import at.alladin.rmbt.client.db.model.HistoryFilter;
import at.alladin.rmbt.client.db.util.DbUtil;

public class CheckHistoryTask extends AsyncTask<Void, Void, JSONArray>
{
    
    private final RMBTMainActivity activity;
    
    private JSONArray historyList;
    
    private String uuid;
    
    private at.alladin.rmbt.client.helper.ControlServerConnection serverConn;
    
    private EndTaskListener<JSONArray> endTaskListener;
    
    private Map<HistoryFilter, List<String>> activeHistoryFilters;
    
    private boolean hasError = false;

    private final boolean isDirty;
    
    public CheckHistoryTask(final RMBTMainActivity rmbtMainActivity, final boolean isDirty)
    {
        this.activity = rmbtMainActivity;
        this.isDirty = isDirty;
    }
    
    @Override
    protected JSONArray doInBackground(final Void... params)
    {
        final String controlServer = ConfigHelper.getControlServerName(activity);
        final int controlPort = ConfigHelper.getControlServerPort(activity);
        final boolean controlSSL = ConfigHelper.isControlSeverSSL(activity);

        uuid = ConfigHelper.getUUID(activity.getApplicationContext());

        activeHistoryFilters = activity.getActiveHistoryFilters();
        
        if (uuid.length() > 0) {
            try {
                Dao<DbHistoryItem, String> historyDao = null;
                Dao<DbMeasurementItem, String> measurementDao = null;

                if (activity.getDatabaseHelper() != null) {
                    historyDao = activity.getDatabaseHelper().getHistoryDao();
                    measurementDao = activity.getDatabaseHelper().getMeasurementDao();
                }

                if (isDirty) {
                    serverConn = new at.alladin.rmbt.client.helper.ControlServerConnection(controlSSL, controlServer, null, controlPort);

                    if (historyDao != null && measurementDao != null) {
                        historyList = new JSONArray(new Gson().toJson(serverConn.requestHistory(uuid, historyDao, measurementDao)));
                    } else {
                        historyList = new JSONArray(new Gson().toJson(serverConn.requestHistory(uuid, 0)));
                    }
                }
                else {
                    final List<DbHistoryItem> dbHistoryList = DbUtil.getFilteredHistoryList(historyDao, activeHistoryFilters);

                    historyList = new JSONArray();
                    for (final DbHistoryItem i : dbHistoryList) {
                        historyList.put(new JSONObject(i.getHistoryItem()));
                    }
                }
            }
            catch (final JSONException e) {
                e.printStackTrace();
            }
        }
        
        return historyList;
    }
    
    @Override
    protected void onCancelled()
    {
        //nothing to do here
    }
    
    @Override
    protected void onPostExecute(final JSONArray historyList)
    {
        if (endTaskListener != null) {
            endTaskListener.taskEnded(historyList);
        }
    }
    
    public void setEndTaskListener(final EndTaskListener<JSONArray> endTaskListener)
    {
        this.endTaskListener = endTaskListener;
    }
}
