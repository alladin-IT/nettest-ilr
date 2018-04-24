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
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;

import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.android.views.ResultDetailsView.ResultDetailType;
import at.alladin.rmbt.client.db.model.DbMeasurementItem;

public class CheckTestResultDetailTask extends AsyncTask<String, Void, JSONArray>
{
    private final String TAG = CheckTestResultDetailTask.class.getName();

	private final ResultDetailType resultType;
    
    private final RMBTMainActivity activity;
    
    private JSONArray resultList;

    private String testUuid;

    private Dao<DbMeasurementItem, String> measurementDao;

    private ControlServerConnection serverConn;

    private EndTaskListener<JSONArray> endTaskListener;
    
    private boolean hasError = false;
    
    public CheckTestResultDetailTask(final RMBTMainActivity activity, final ResultDetailType resultType)
    {
        this.activity = activity;
        this.resultType = resultType;
        this.measurementDao = activity.getDatabaseHelper().getMeasurementDao();
    }
    
    @Override
    protected JSONArray doInBackground(final String... uid)
    {
        serverConn = new ControlServerConnection(activity.getApplicationContext());
        try {
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

            	switch(this.resultType) {
            	case SPEEDTEST:
                    //resultList = serverConn.requestTestResultDetail(uid[0]);
                	resultList = dbItem != null && dbItem.getDetailedResults() != null ?
                            new JSONArray(dbItem.getDetailedResults()) : serverConn.requestTestResultDetail(testUuid);
                	break;
            	case QUALITY_OF_SERVICE_TEST:
            		//resultList = serverConn.requestTestResultQoS(uid[0]);
                    resultList = serverConn.requestTestResultQoS(testUuid);
                    if (resultList == null && dbItem != null && dbItem.getQosResults() != null) {
                        resultList = new JSONArray(dbItem.getQosResults());
                    }
            		break;
            	case OPENDATA:
            		resultList = new JSONArray();
    				resultList.put(0, serverConn.requestOpenDataTestResult(testUuid, uid[1]));
            		break;
                case SPEEDTEST_GROUPED:
                    resultList = new JSONArray();
                    resultList.put(serverConn.requestTestResultDetailGroup(testUuid));
                    break;
            	}
            }	
        }
        catch (JSONException e) {
        	e.printStackTrace();
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
                            measurementDao.create(dbItem);
                        }

                        Log.d(TAG, "measurement: " + dbItem);
                    }

                    if (dbItem != null) {
                        switch (this.resultType) {
                            case SPEEDTEST:
                                dbItem.setDetailedResults(resultList.toString());
                                break;
                            case QUALITY_OF_SERVICE_TEST:
                                dbItem.setQosResults(resultList.toString());
                                break;
                            case OPENDATA:
                                break;
                        }

                        measurementDao.update(dbItem);
                        Log.d(TAG, "saved measurement obejct in DB: " + dbItem);
                    }
                }
            }
            catch (final SQLException e) {
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
