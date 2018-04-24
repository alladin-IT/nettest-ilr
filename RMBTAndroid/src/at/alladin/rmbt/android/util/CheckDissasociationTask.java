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

import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.client.helper.ControlServerConnection;

public class CheckDissasociationTask extends AsyncTask<Void, Void, Boolean>
{
    private final static String TAG = CheckDissasociationTask.class.getName();

    private final RMBTMainActivity activity;

    private String testUuid;

    private at.alladin.rmbt.client.helper.ControlServerConnection serverConn;

    private EndTaskListener<Boolean> endTaskListener;

    private boolean hasError = false;

    private final String measurementUuid;

    public CheckDissasociationTask(final RMBTMainActivity rmbtMainActivity, final String measurementUuid)
    {
        this.activity = rmbtMainActivity;
        this.measurementUuid = measurementUuid;
    }
    
    @Override
    protected Boolean doInBackground(final Void... params)
    {
        final String controlServer = ConfigHelper.getControlServerName(activity);
        final int controlPort = ConfigHelper.getControlServerPort(activity);
        final boolean controlSSL = ConfigHelper.isControlSeverSSL(activity);

        serverConn = new ControlServerConnection(controlSSL, controlServer, null, controlPort);
        
        testUuid = ConfigHelper.getUUID(activity.getApplicationContext());
        
        if (testUuid.length() > 0) {
            try {
                final boolean disassociationResult = serverConn.requestDisassociation(ConfigHelper.getUUID(activity), measurementUuid);
                if (disassociationResult) {
                    //if disassociation was successful we need to remove the entire test result from the local db too:
                    try {
                        activity.getDatabaseHelper().getMeasurementDao().deleteById(testUuid);
                        activity.getDatabaseHelper().getHistoryDao().deleteById(testUuid);
                    }
                    catch (final Exception e) {
                        Log.d(TAG, "error while removing test from local db: " + testUuid, e);
                    }
                }
                return disassociationResult;
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    @Override
    protected void onCancelled()
    {
        //nothing to do here
    }
    
    @Override
    protected void onPostExecute(final Boolean result)
    {
        if (endTaskListener != null) {
            endTaskListener.taskEnded(result);
        }
    }
    
    public void setEndTaskListener(final EndTaskListener<Boolean> endTaskListener)
    {
        this.endTaskListener = endTaskListener;
    }
}
