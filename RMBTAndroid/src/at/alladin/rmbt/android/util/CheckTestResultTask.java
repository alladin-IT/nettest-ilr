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

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.client.db.model.DbMeasurementItem;

public class CheckTestResultTask extends AsyncTask<String, Void, JSONArray>
{
    private final static String TAG = CheckTestResultTask.class.getName();
    
    private final RMBTMainActivity activity;
    
    private JSONArray resultList;
    
    private ControlServerConnection serverConn;
    
    private EndTaskListener<JSONArray> endTaskListener;
    
    private boolean hasError = false;

    private String testUuid;

    private Dao<DbMeasurementItem, String> measurementDao;

    public CheckTestResultTask(final RMBTMainActivity activity)
    {
        this.activity = activity;
        this.measurementDao = activity.getDatabaseHelper().getMeasurementDao();
    }
    
    @Override
    protected JSONArray doInBackground(final String... uid)
    {
        serverConn = new ControlServerConnection(activity.getApplicationContext());

        if (uid != null && uid[0] != null)
        {
            testUuid = uid[0];
            DbMeasurementItem dbItem = null;
            if (measurementDao != null) {
                try {
                    synchronized (measurementDao) {
                        dbItem = measurementDao.queryForId(testUuid);
                    }
                    Log.d(TAG, "DB entry: " + dbItem);
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }

            Log.d(TAG, "requesting result data for: " + uid[0]);
            //resultList = serverConn.requestTestResult(uid[0]);
            try {
                resultList = dbItem != null && dbItem.getResults() != null ?
                        new JSONArray(dbItem.getResults()) : serverConn.requestTestResult(uid[0]);
            }
            catch (final JSONException e) {
                e.printStackTrace();
            }
        }
        else  {
        	Log.d(TAG, "no uid given");
        }
        
        return resultList;
    }
    
    @Override
    protected void onCancelled()
    {
        if (serverConn != null)
        {
            serverConn.unload();
            serverConn = null;
        }
    }
    
    @Override
    protected void onPostExecute(final JSONArray resultList)
    {
        if (serverConn.hasError()) {
            hasError = true;
        }
        else if (resultList != null) {
            DbMeasurementItem dbItem = null;
            try {
                if (measurementDao != null) {
                    synchronized (measurementDao) {
                        dbItem = measurementDao.queryForId(testUuid);
                        if (dbItem == null) {
                            Log.d(TAG, "measurement not found. inserting...");
                            dbItem = new DbMeasurementItem();
                            dbItem.setUuid(testUuid);
                            dbItem.setResults(resultList.toString());
                            measurementDao.create(dbItem);
                        }
                        else {
                            Log.d(TAG, "measurement found. updating...");
                            dbItem.setResults(resultList.toString());
                            measurementDao.update(dbItem);
                        }

                        Log.d(TAG, "measurement: " + dbItem);
                    }

                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }


        if (endTaskListener != null) {
            endTaskListener.taskEnded(resultList);
        }
    }
    
    public void setEndTaskListener(final EndTaskListener<JSONArray> endTaskListener)
    {
        this.endTaskListener = endTaskListener;
    }
    
    public boolean hasError()
    {
        return hasError;
    }
    
}
