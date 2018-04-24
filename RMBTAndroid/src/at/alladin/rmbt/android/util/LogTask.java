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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Environment;
import at.alladin.rmbt.android.adapter.result.OnCompleteListener;
import at.alladin.rmbt.android.main.RMBTMainActivity;

public class LogTask extends AsyncTask<String, Void, Void>
{
	private final static String LOGFILE_PREFIX = "at.alladin.nettest";
		
    private final RMBTMainActivity activity;
    
    ControlServerConnection serverConn;
    
    private OnCompleteListener onCompleteListener;   
    
    public LogTask(final RMBTMainActivity activity, final OnCompleteListener listener)
    {
        this.activity = activity;
        this.onCompleteListener = listener;
    }
    
    /**
     * 
     * @param listener
     */
    public void setOnCompleteListener(OnCompleteListener listener) {
    	this.onCompleteListener = listener;
    }
    
    @Override
    protected Void doInBackground(final String... params)
    {
    	try {
	        serverConn = new ControlServerConnection(activity);
	
	        final List<File> logFiles = new ArrayList<File>(); 
	        
	        if (params == null || params.length == 0) {
	    		File f = new File(Environment.getExternalStorageDirectory(), "qosdebug");
	            final File[] logs = f.listFiles();
	            
	            if (logs != null) {
		            for (File l : logs) {
		            	if (l.length() > 0) {
		            		logFiles.add(l);
		            	}
		            	else {
		            		//delete old empty log file
		            		l.delete();
		            	}
		            }
	            }
	        }
	        else {
	        	for (String fileName : params) {
	        		File f = new File(fileName);
	        		if (f.exists() && f.length() > 0) {
	        			logFiles.add(f);
	        		}
	        	}
	        }
	        
	        System.out.println("log files found: " + logFiles);
	        
	        if (logFiles.size() > 0) {
	        	for (File logFile : logFiles) {
					System.out.println("Sending file: " + logFile.getAbsolutePath());
					Scanner s = null;
					try {
						BufferedReader br = new BufferedReader(new FileReader(logFile));
						try {
						    StringBuilder sb = new StringBuilder();
						    String line = br.readLine();
						
						    while (line != null) {
						        sb.append(line);
						        sb.append("\n");
						        line = br.readLine();
						    }
							final JSONObject requestData = new JSONObject();
							requestData.put("content", sb.toString());
							requestData.put("logfile", LOGFILE_PREFIX + "_" + ConfigHelper.getUUID(activity) + "_" + logFile.getName());
							final JSONObject fileTimes = new JSONObject();
							fileTimes.put("last_access", TimeUnit.SECONDS.convert(logFile.lastModified(), TimeUnit.MILLISECONDS));
							fileTimes.put("last_modified", TimeUnit.SECONDS.convert(logFile.lastModified(), TimeUnit.MILLISECONDS));
							fileTimes.put("created", TimeUnit.SECONDS.convert(logFile.lastModified(), TimeUnit.MILLISECONDS));
							requestData.put("file_times", fileTimes);
							JSONArray result = serverConn.sendLogReport(requestData);
							if (result != null) {
								final String resultStatus = ((JSONObject) result.get(0)).getString("status");
								if ("OK".equals(resultStatus.toUpperCase(Locale.US))) {
									System.out.println("Log file sent successfully. Deleting.");
									br.close();
									logFile.delete();
								}
							}
						} finally {
						    br.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (s != null) {
							s.close();
						}
					}
	        	}
	        }
    	}
    	catch (Throwable t) {
    		t.printStackTrace();
    	}
        
        return null;
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
    protected void onPostExecute(Void result)
    {
    	if (onCompleteListener != null) {
    		onCompleteListener.onComplete(0, result);
    	}
    }
    
}
