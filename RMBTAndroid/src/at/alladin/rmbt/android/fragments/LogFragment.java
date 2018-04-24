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

package at.alladin.rmbt.android.fragments;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.android.util.ControlServerConnection;
import at.alladin.rmbt.android.util.LogTask;

import at.alladin.openrmbt.android.R;

/**
 * 
 * @author lb
 *
 */
public class LogFragment extends Fragment implements OnItemClickListener {
		
	
	final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMAN);
	
	List<File> logFiles = new ArrayList<File>();
	
	ControlServerConnection serverConn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		File f = new File(Environment.getExternalStorageDirectory(), "qosdebug");
        final File[] logs = f.listFiles();
        
        for (File l : logs) {
        	if (l.length() > 0) {
        		logFiles.add(l);
        	}
        }
        
        serverConn = new ControlServerConnection(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		//reuse netstat_fragment for this debug view
		final View view = inflater.inflate(R.layout.netstat_fragment, container, false);			
		final ListView listView = (ListView) view.findViewById(R.id.valueList);
		TextView infoView = (TextView) view.findViewById(R.id.cpu_usage);
		
		final ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String,String>>();		
        HashMap<String, String> viewItem;

        if (logFiles != null && logFiles.size() > 0) {
        	infoView.setText("Found log files: " + logFiles.size());
        	
	        for (int i = 0; i < logFiles.size(); i++)
	        {
	        	viewItem = new HashMap<String, String>();
	            viewItem.put("title", logFiles.get(i).getName());
	            logFiles.get(i).lastModified();
	            Date date = new Date(logFiles.get(i).lastModified());
	            viewItem.put("text1", "Size: " + (logFiles.get(i).length() / 1024) + "Kb"
	            		+ "\nLast modified: " + SIMPLE_DATE_FORMAT.format(date));
	
	            itemList.add(viewItem);
	        }	
	        
	        SimpleAdapter valueList = new SimpleAdapter(getActivity(), itemList, R.layout.about_item, 
	        		new String[] {"title", "text1"}, new int[] { R.id.title, R.id.text1});
	        
	        listView.setAdapter(valueList);
	        listView.setOnItemClickListener(this);
	        listView.invalidate();
        }
        else {
        	infoView.setText("No log files found.");
        	listView.setVisibility(View.GONE);
        }
		
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		System.out.println(logFiles.get(position).getName());
		Bundle b = new Bundle();
		b.putString("logfileabs", logFiles.get(position).getAbsolutePath());
		b.putString("logfile", logFiles.get(position).getName());
		SendDialogFragment sendFragment = new SendDialogFragment();
		sendFragment.setArguments(b);
		sendFragment.show(getFragmentManager(), "SendDialogFragment");
	}
	
	/**
	 * 
	 * @author lb
	 *
	 */
	public static class SendDialogFragment extends DialogFragment {
	    
		String logFile = "";
		String logFileAbsolute = "";
	    
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	logFile = getArguments().getString("logfile");
	    	logFileAbsolute = getArguments().getString("logfileabs");

	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getActivity());
	        alertDialogBuilder.setTitle("Send file?");
	        alertDialogBuilder.setMessage("Send log file '" + logFile + "' to ControlServer?");
	        //null should be your on click listener
	        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					LogTask logTask = new LogTask((RMBTMainActivity) SendDialogFragment.this.getActivity(), null);
					logTask.execute(logFile, logFileAbsolute);
				}
			});
	        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	            }
	        });


	        return alertDialogBuilder.create();
	    }
	}
}

