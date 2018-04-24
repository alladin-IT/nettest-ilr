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

package at.alladin.rmbt.android.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.nettest.shared.helper.GsonBasicHelper;
import at.alladin.nettest.shared.model.response.SpeedtestDetailGroupResultResponse;
import at.alladin.nettest.shared.model.response.SpeedtestDetailResultResponse;

import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.android.util.CheckTestResultDetailTask;
import at.alladin.rmbt.android.util.EndTaskListener;

import at.alladin.openrmbt.android.R;

public class ResultDetailsView extends LinearLayout implements EndTaskListener<JSONArray>, ResultDetails {
	
	public static enum ResultDetailType {
		SPEEDTEST,
		QUALITY_OF_SERVICE_TEST,
		OPENDATA,
        SPEEDTEST_GROUPED
	}

	private View view;
	
    private static final String DEBUG_TAG = "ResultDetailsView";
    
    public static final String ARG_UID = "uid";
    
    private RMBTMainActivity activity;
    
    private CheckTestResultDetailTask testResultDetailTask;
    
    private ListAdapter valueList;
    
    private LinearLayout containerView;
    
    private TextView emptyView;
    
    private ProgressBar progessBar;
    
    private ArrayList<HashMap<String, String>> itemList;
    
    private String uid;
    
    private JSONArray testResult;
    
    private EndTaskListener<JSONArray> resultFetchEndTaskListener;

	    
	/**
	 * 
	 * @param context
	 */
	public ResultDetailsView(Context context, RMBTMainActivity activity, String uid, JSONArray testResult) {
		this(context, null, activity, uid, testResult);
	}
	
	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public ResultDetailsView(Context context, AttributeSet attrs, RMBTMainActivity activity, String uid, JSONArray testResult) {
		super(context, attrs);

		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.view = createView(layoutInflater);
		this.activity = activity;
		this.uid = uid;
		this.testResult = testResult;
	}
    
    /**
     * 
     * @param inflater
     * @return
     */
    public View createView(final LayoutInflater inflater)
    {        
        final View view = inflater.inflate(R.layout.test_result_detail_container, this);
        
        containerView = (LinearLayout) view.findViewById(R.id.test_result_group_container);
        containerView.setVisibility(View.GONE);
        
        emptyView = (TextView) view.findViewById(R.id.infoText);
        emptyView.setVisibility(View.GONE);
        
        progessBar = (ProgressBar) view.findViewById(R.id.progressBar);
                
        return view;
    }

    @Override
    public void initialize (EndTaskListener<JSONArray>  resultFetchEndTaskListener) {
        itemList = new ArrayList<HashMap<String, String>>();

    	this.resultFetchEndTaskListener = resultFetchEndTaskListener;
    	
        if ((testResultDetailTask == null || testResultDetailTask != null || testResultDetailTask.isCancelled()) && uid != null)
        {
        	if (this.testResult!=null) {
        		System.out.println("TESTRESULT found ResultDetailsView");
        		taskEnded(this.testResult);
        	}
        	else {
        		System.out.println("TESTRESULT NOT found ResultDetailsView");
            	System.out.println("initializing ResultDetailsView");

                testResultDetailTask = new CheckTestResultDetailTask(activity, ResultDetailType.SPEEDTEST_GROUPED);
                testResultDetailTask.setEndTaskListener(this);
                testResultDetailTask.execute(uid);        		
        	}
        }
    }
    
    @Override
    public void taskEnded(final JSONArray testResultDetail)
    {
        //if (getVisibility()!=View.VISIBLE)
        //    return;
        
    	if (this.resultFetchEndTaskListener != null) {
    		this.resultFetchEndTaskListener.taskEnded(testResultDetail);
    	}
    	
        if (testResultDetail != null && testResultDetail.length() > 0 && (testResultDetailTask==null || !testResultDetailTask.hasError()))
        {
        	this.testResult = testResultDetail;
            System.out.println("testResultDetail: " + testResultDetail);
            //as we get the result as JSONArray (so we can reuse the CheckTestResultDetailTask), we need to put it back into an object to obtain the response object
            final JSONObject responseJson = new JSONObject();
            try {
                this.testResult = this.testResult.getJSONArray(0);
                responseJson.put("testresultDetailGroups", testResult);
            }catch(JSONException ex){
                Log.e(this.getClass().toString(), "Error when parsing test result.");
                Log.e(this.getClass().toString(), ex.getMessage());
            }
            List<SpeedtestDetailGroupResultResponse.SpeedtestDetailResponseGroup> detailGroups = GsonBasicHelper.getDateTimeGsonBuilder().create().
                    fromJson(responseJson.toString(), SpeedtestDetailGroupResultResponse.class).getSpeedtestDetailGroups();

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for(SpeedtestDetailGroupResultResponse.SpeedtestDetailResponseGroup group : detailGroups){
                final RelativeLayout mainView = (RelativeLayout) inflater.inflate(R.layout.test_result_detail_group_item, null);
                final AlladinTextView alladinTextView = (AlladinTextView) mainView.findViewById(R.id.test_result_divider_icon);
                alladinTextView.setText(group.getIcon());
                final TextView dividerText = (TextView) mainView.findViewById(R.id.test_result_divider_title);
                dividerText.setText(group.getTitle());
                final LinearLayout entryList = (LinearLayout) mainView.findViewById(R.id.test_result_entry_list);

                for(SpeedtestDetailResultResponse.SpeedtestDetailItem item : group.getEntries()){
                    final LinearLayout viewItem = (LinearLayout) inflater.inflate(R.layout.test_result_detail_item, null);
                    ((TextView)viewItem.findViewById(R.id.name)).setText(item.getTitle());
                    ((TextView)viewItem.findViewById(R.id.value)).setText(item.getValue());
                    entryList.addView(viewItem);

                    final View divider = new View(activity);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT, 1));
                    divider.setBackgroundResource(R.drawable.bg_trans_light);

                    entryList.addView(divider);
                }

                containerView.addView(mainView);
            }

            containerView.invalidate();
            
            progessBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            containerView.setVisibility(View.VISIBLE);
            
        }
        else
        {
            Log.i(DEBUG_TAG, "LEERE LISTE");
            progessBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(activity.getString(R.string.error_no_data));
            emptyView.invalidate();
        }
        
    }

    /**
     * 
     * @return
     */
    public JSONArray getTestResult() {
    	return testResult;
    }
}
