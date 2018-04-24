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

package at.alladin.rmbt.android.adapter.result;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import at.alladin.openrmbt.android.BuildConfig;
import at.alladin.openrmbt.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.alladin.rmbt.android.main.RMBTApplication;
import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.android.util.CheckTestResultDetailTask;
import at.alladin.rmbt.android.util.CheckTestResultTask;
import at.alladin.rmbt.android.util.EndTaskListener;
import at.alladin.rmbt.android.util.Helperfunctions;
import at.alladin.rmbt.android.views.ResultDetails;
import at.alladin.rmbt.android.views.ResultDetailsUngroupedView;
import at.alladin.rmbt.android.views.ResultDetailsView;
import at.alladin.rmbt.android.views.ResultDetailsView.ResultDetailType;
import at.alladin.rmbt.android.views.ResultGraphView;
import at.alladin.rmbt.android.views.ResultQoSDetailBoxView;
import at.alladin.rmbt.android.views.ResultQoSDetailListView;
import at.alladin.rmbt.client.v2.task.result.QoSServerResultCollection;

/**
 * 
 * @author lb
 *
 */
public class RMBTResultPagerAdapter extends PagerAdapter {
	public final static int RESULT_PAGE_MAIN_MENU = 0;
	public final static int RESULT_PAGE_QOS = 2;
	public final static int RESULT_PAGE_TEST = 1;
	public final static int RESULT_PAGE_GRAPH = 4;
	public final static int RESULT_PAGE_MAP = 3;

//	public final static SparseIntArray RESULT_PAGE_TAB_TITLE_MAP;
    public final static String[] RESULT_PAGE_TAB_TITLE_ARRAY =  new String[RMBTApplication.getAppContext().getResources().getStringArray(R.array.result_page_title).length];
    public final static int[] RESULT_PAGE_TAB_TITLE_INDICES_ARRAY =  new int[RMBTApplication.getAppContext().getResources().getStringArray(R.array.result_page_title_indices).length];

    static {
        final String[] resultTitles = RMBTApplication.getAppContext().getResources().getStringArray(R.array.result_page_title);
        final int[] resultTitleIndices = RMBTApplication.getAppContext().getResources().getIntArray(R.array.result_page_title_indices);
        for(int i = 0; i < resultTitleIndices.length; i++){
            RESULT_PAGE_TAB_TITLE_ARRAY[i] = resultTitles[i];
            RESULT_PAGE_TAB_TITLE_INDICES_ARRAY[i] = resultTitleIndices[i];
        }
    }


//	static {
//        final String[] resultPages = RMBTApplication.getAppContext().getResources().getStringArray(R.array.result_page_title);
//		RESULT_PAGE_TAB_TITLE_MAP = new SparseIntArray();
//        for(int i = 0; i < resultPages.length; i++){
//            RESULT_PAGE_TAB_TITLE_MAP.put(, i);
//        }
//		RESULT_PAGE_TAB_TITLE_MAP.put(RESULT_PAGE_MAIN_MENU, 0);
//		RESULT_PAGE_TAB_TITLE_MAP.put(RESULT_PAGE_QOS, 1);
//		RESULT_PAGE_TAB_TITLE_MAP.put(RESULT_PAGE_TEST, 2);
//		//RESULT_PAGE_TAB_TITLE_MAP.put(RESULT_PAGE_GRAPH, 3);
//        if() {
//            RESULT_PAGE_TAB_TITLE_MAP.put(RESULT_PAGE_MAP, 3);    //TODO: I just commented this out, make this better!
//        }
//	}

    private static final String DEBUG_TAG = "RMBTResultPagerAdapter";
    
    private final RMBTMainActivity activity;
    
    private final String testUuid;
    private String openTestUuid = null;
    private final boolean isFromMapView;

    private final Handler handler;
    private boolean hasMap = true;
    private Runnable progressUpdater;
    private JSONArray testResult;
    private JSONArray testResultDetails;
	private JSONArray testGraphResult;
    private QoSServerResultCollection testResultQoSDetails;
    
    private LatLng testPoint = null;
    private String mapType = null;
    
    private OnCompleteListener completeListener;
    private OnDataChangedListener dataChangedListener;
    
    //private ResultGraphView graphView = null;
    private LinearLayout measurementLayout;
    
    public RMBTResultPagerAdapter(final RMBTMainActivity _activity, final Handler _handler, final String testUuid, final boolean isFromMapView)
    {
    	this.activity = _activity;
        this.handler = _handler;
        this.testUuid = testUuid;
        this.isFromMapView = isFromMapView;
    }
    
    /**
     * 
     * @param listener
     */
    public void setOnDataChangedListener(OnDataChangedListener listener) {
    	this.dataChangedListener = listener;
    }
    
    @Override
    public Parcelable saveState()
    {
    	Log.d("RMBT SAVE STATE", "Saving state in ResultPagerAdapter");
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.saveState());
        if (testResult != null) {
            bundle.putString("test_result", testResult.toString());	
        }
        if (testResultDetails != null) {
            bundle.putString("test_result_details", testResultDetails.toString());
        }
        if (testGraphResult != null) {
        	bundle.putString("test_result_graph", testGraphResult.toString());
        }
        if (testResultQoSDetails != null) {
        	bundle.putString("test_result_qos", testResultQoSDetails.getTestResultArray().toString());
        }
        return bundle;
    }
    
    @Override
    public void restoreState(Parcelable state, ClassLoader cl)
    {
        if (state instanceof Bundle)
        {
        	Log.d("RMBT RESTORE STATE", "Restoring state in ResultPagerAdapter");
            Bundle bundle = (Bundle) state;
            super.restoreState(bundle.getParcelable("instanceState"), cl);
            try {
				String testResultJson = bundle.getString("test_result");
				if (testResultJson != null) {
					testResult = new JSONArray(testResultJson);	
				}
				
				String testDetailsJson = bundle.getString("test_result_details");
				if (testDetailsJson != null) {
					testResultDetails = new JSONArray(testDetailsJson);	
				}
				
				String testGraphJson = bundle.getString("test_result_graph");
				if (testGraphJson != null) {
					testGraphResult = new JSONArray(testGraphJson);
					//graphViewEndTaskListener.taskEnded(testGraphResult);
				}
				
				String testDetailsQos = bundle.getString("test_result_qos");
				if (testDetailsQos != null) {
					setTestResultQoSDetails(new QoSServerResultCollection(new JSONArray(testDetailsQos)));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
            return;
        }
    }
    
    /**
     * 
     * @param vg
     * @param inflater
     * @return
     */
    public View instantiateResultPage(final ViewGroup vg, final LayoutInflater inflater) {
    	final View view = inflater.inflate(R.layout.result_menu, vg, false);
    	        
        if (this.testResult == null) {
            final CheckTestResultTask testResultTask = new CheckTestResultTask(activity);
            testResultTask.setEndTaskListener( new EndTaskListener<JSONArray>()
            {
                @Override
                public void taskEnded(final JSONArray testResult)
                {
                	if (testResult != null) {
                        System.out.println("testResultTask.hasError() = " + testResultTask.hasError() + ", testResult.length() = " + testResult.length());
                        
                        RMBTResultPagerAdapter.this.testResult = testResult;
                        
                        displayResult(view, inflater, vg);

                    	JSONObject testResultItem;
						try {
							testResultItem = testResult.getJSONObject(0);
	                        if (testResultItem.has("geo_lat") && testResultItem.has("geo_long") && !hasMap) {
                        		hasMap = true;
                        		if (dataChangedListener != null) {
                        			dataChangedListener.onChange(false, true, "HAS_MAP");
                        		}
                        		notifyDataSetChanged();
	                        }
	                        else if (!testResultItem.has("geo_lat") && !testResultItem.has("geo_long") && hasMap) {
	                        	hasMap = false;
                            	if (dataChangedListener != null) {
                            		dataChangedListener.onChange(true, false, "HAS_MAP");
                            	}
                            	notifyDataSetChanged();
	                        }
						} catch (JSONException e) {
							hasMap = false;
							e.printStackTrace();
						}
                        
                        if (completeListener != null) {
                        	completeListener.onComplete(OnCompleteListener.DATA_LOADED, this);
                        }
                	}
                }
            });
            
            testResultTask.execute(testUuid);	
        }
        else {
            displayResult(view, inflater, vg);
        }
        

        if (this.testResultQoSDetails == null) {
            if (RESULT_PAGE_QOS > 1) {
            	initializeQoSResults(testUuid);
            }
        }

    	return view;   
    }
    
    /**
     * 
     * @param view
     */
    private void displayResult(View view, LayoutInflater inflater, ViewGroup vg) {
    	/*
        final Button shareButton = (Button) view.findViewById(R.id.resultButtonShare);
        if (shareButton != null)
            shareButton.setEnabled(false);
            */

        //final LinearLayout measurementLayout = (LinearLayout) view.findViewById(R.id.resultMeasurementList);
        measurementLayout = (LinearLayout) view.findViewById(R.id.resultMeasurementList);
        measurementLayout.setVisibility(View.GONE);
        
        final LinearLayout resultLayout = (LinearLayout) view.findViewById(R.id.result_layout);
        resultLayout.setVisibility(View.INVISIBLE);
        
        final LinearLayout netLayout = (LinearLayout) view.findViewById(R.id.resultNetList);
        netLayout.setVisibility(View.GONE);
        
        final TextView measurementHeader = (TextView) view.findViewById(R.id.resultMeasurement);
        measurementHeader.setVisibility(View.GONE);

        final TextView netHeader = (TextView) view.findViewById(R.id.resultNet);
        netHeader.setVisibility(View.GONE);
        
        final TextView emptyView = (TextView) view.findViewById(R.id.infoText);
        emptyView.setVisibility(View.GONE);
        final float scale = activity.getResources().getDisplayMetrics().density;
                
        final ProgressBar progessBar = (ProgressBar) view.findViewById(R.id.progressBar);

    	if (testResult != null && testResult.length() > 0)
    	{
            
            JSONObject resultListItem;
            
            try
            {
                resultListItem = testResult.getJSONObject(0);
                
                openTestUuid = resultListItem.optString("open_test_uuid");
                if (graphView != null) {
                	graphView.setOpenTestUuid(openTestUuid);
                	graphView.initialize(graphViewEndTaskListener);
                }
                
            	JSONObject testResultItem;
				try {
					testResultItem = testResult.getJSONObject(0);
                    if (testResultItem.has("geo_lat") && testResultItem.has("geo_long") && !hasMap) {
                    	hasMap = true;
                    	if (dataChangedListener != null) {
                    		dataChangedListener.onChange(false, true, "HAS_MAP");
                    	}
                    	notifyDataSetChanged();
                    }
                    else if (!testResultItem.has("geo_lat") && !testResultItem.has("geo_long") && hasMap) {
                    	System.out.println("hasMap = " + hasMap);
                    	hasMap = false;
                    	if (dataChangedListener != null) {
                    		dataChangedListener.onChange(true, false, "HAS_MAP");
                    	}
                    	notifyDataSetChanged();
                    }
				} catch (JSONException e) {
					hasMap = false;
					e.printStackTrace();
				}
				
				if (completeListener != null) {
					completeListener.onComplete(OnCompleteListener.DATA_LOADED, this);
				}
                
                final JSONArray measurementArray = resultListItem.getJSONArray("measurement");
                
                final JSONArray netArray = resultListItem.getJSONArray("net");
                
                final int leftRightDiv = Helperfunctions.dpToPx(0, scale);
                final int topBottomDiv = Helperfunctions.dpToPx(0, scale);
                final int heightDiv = Helperfunctions.dpToPx(1, scale);
                
                for (int i = 0; i < measurementArray.length(); i++)
                {
                	final View measurementItemView = inflater.inflate(R.layout.classification_list_item, vg, false);
                	
                    final JSONObject singleItem = measurementArray.getJSONObject(i);

                    final TextView itemTitle = (TextView) measurementItemView.findViewById(R.id.classification_item_title);
                    itemTitle.setText(singleItem.getString("title"));
                    
                    final ImageView itemClassification = (ImageView) measurementItemView.findViewById(R.id.classification_item_color);
                    final String classificationColor = singleItem.optString("classificationColor", null);
                    if (classificationColor == null) {
                        itemClassification.setImageResource(Helperfunctions.getClassificationColor(singleItem.getInt("classification")));
                    }else {

                        itemClassification.setColorFilter(Helperfunctions.getClassificationColor(classificationColor), PorterDuff.Mode.SRC_ATOP);
                    }
                    
                    itemClassification.setOnClickListener(new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            activity.showHelp(R.string.url_help_result, false);
                        }
                    });

                    if (!BuildConfig.SHOW_CLASSIFICATION_FOR_SPEED_RESULTS) {
                        itemClassification.setVisibility(View.INVISIBLE);
                    }
                    
                    final TextView itemValue = (TextView) measurementItemView.findViewById(R.id.classification_item_value);
                    itemValue.setText(singleItem.getString("value"));
                    
                    measurementLayout.addView(measurementItemView);
                    
                    final View divider = new View(activity);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, heightDiv, 1));
                    divider.setPadding(leftRightDiv, topBottomDiv, leftRightDiv, topBottomDiv);
                    
                    divider.setBackgroundResource(R.drawable.bg_trans_light);
                    
                    measurementLayout.addView(divider);
                    
                    measurementLayout.invalidate();
                }
                
                for (int i = 0; i < netArray.length(); i++)
                {
                    
                    final JSONObject singleItem = netArray.getJSONObject(i);
                    
                    addResultListItem(singleItem.getString("title"), singleItem.optString("value", null), netLayout);
                }
                
               	addQoSResultItem();
                
            }
            catch (final JSONException e)
            {
                e.printStackTrace();
            }
                        
            progessBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            
            resultLayout.setVisibility(View.VISIBLE);
            measurementHeader.setVisibility(View.VISIBLE);
            netHeader.setVisibility(View.VISIBLE);
            
            measurementLayout.setVisibility(View.VISIBLE);
            netLayout.setVisibility(View.VISIBLE);
            
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
     */
    public synchronized void addQoSResultItem() {
        if (testResultQoSDetails != null && measurementLayout != null) {
        	QoSServerResultCollection.QoSResultStats stats = testResultQoSDetails.getQoSStatistics();
            addResultListItem(activity.getString(R.string.result_qos_stats), stats.getPercentageForTests() + "% (" + (stats.getTestCounter() - stats.getFailedTestsCounter()) + "/" + stats.getTestCounter() + ")" , measurementLayout);	
        }
    }
    
    public void addResultListItem(String title, String value, LinearLayout netLayout) {
    	final float scale = activity.getResources().getDisplayMetrics().density;
        final int leftRightDiv = Helperfunctions.dpToPx(0, scale);
        final int topBottomDiv = Helperfunctions.dpToPx(0, scale);
        final int heightDiv = Helperfunctions.dpToPx(1, scale);        
        
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
    	final View measurementItemView = inflater.inflate(R.layout.classification_list_item, netLayout, false);
    	
        final TextView itemTitle = (TextView) measurementItemView.findViewById(R.id.classification_item_title);
        itemTitle.setText(title);
        
        final ImageView itemClassification = (ImageView) measurementItemView.findViewById(R.id.classification_item_color);                    
        itemClassification.setImageResource(Helperfunctions.getClassificationColor(-1));
        
        itemClassification.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                activity.showHelp(R.string.url_help_result, false);
            }
        });
        
        final TextView itemValue = (TextView) measurementItemView.findViewById(R.id.classification_item_value);
        itemValue.setText(value);
        
        netLayout.addView(measurementItemView);
        
        final View divider = new View(activity);
        divider.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, heightDiv, 1));
        divider.setPadding(leftRightDiv, topBottomDiv, leftRightDiv, topBottomDiv);
        
        divider.setBackgroundResource(R.drawable.bg_trans_light_10);
        
        netLayout.addView(divider);
        
        netLayout.invalidate();
    }
    
    /**
     * 
     * @param vg
     * @param inflater
     * @return
     */
    public View instantiateDetailView(final ViewGroup vg, final LayoutInflater inflater) {
        ResultDetails view;

        switch (BuildConfig.RESULT_DETAILS_VIEW) {
            case GROUPED:
                view = new ResultDetailsView(activity.getApplicationContext(), activity, testUuid, testResultDetails);
                break;
            case SIMPLE:
            default:
                view = new ResultDetailsUngroupedView(activity.getApplicationContext(), activity, testUuid, testResultDetails);
                break;
        }

    	view.initialize( new EndTaskListener<JSONArray>()  {
			@Override
			public void taskEnded(JSONArray result) {
                System.out.println("instantiate detail view");
                System.out.println(result);
                RMBTResultPagerAdapter.this.testResultDetails = result;
			}
		});

    	return (View) view;
    }
    
    /**
     * 
     * @param vg
     * @param inflater
     * @return
     */
    public View instantiateQoSDetailView(final ViewGroup vg, final LayoutInflater inflater) {
        View view = null;

        switch (BuildConfig.RESULT_QOS_CATEGORY) {
            case BOX:
                view = new ResultQoSDetailBoxView(activity.getApplicationContext(), activity, testUuid,
                        (testResultQoSDetails != null ? testResultQoSDetails.getTestResultArray() : null));

                //if (!isCheckingQoSResult.getAndSet(true)) {
                ((ResultQoSDetailBoxView) view).initialize(new EndTaskListener<JSONArray>() {
                    @Override
                    public void taskEnded(JSONArray result) {
                        //isCheckingQoSResult.set(false);
                        try {
                            RMBTResultPagerAdapter.this.setTestResultQoSDetails(new QoSServerResultCollection(result));
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                    }
                });
                break;
            case LIST:
                view = new ResultQoSDetailListView(activity.getApplicationContext(), activity, testUuid,
                        (testResultQoSDetails != null ? testResultQoSDetails.getTestResultArray() : null));

                //if (!isCheckingQoSResult.getAndSet(true)) {
                ((ResultQoSDetailListView) view).initialize(new EndTaskListener<JSONArray>() {
                    @Override
                    public void taskEnded(JSONArray result) {
                        //isCheckingQoSResult.set(false);
                        try {
                            RMBTResultPagerAdapter.this.setTestResultQoSDetails(new QoSServerResultCollection(result));
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                    }
                });
                break;
        }
    	//}
    	return view;    	
    }
    
    /**
     * 
     * @param uid
     */
    public void initializeQoSResults(String uid) {
        CheckTestResultDetailTask testResultDetailTask = new CheckTestResultDetailTask(activity, ResultDetailType.QUALITY_OF_SERVICE_TEST);
        
        testResultDetailTask.setEndTaskListener( new EndTaskListener<JSONArray>()  {
			@Override
			public void taskEnded(JSONArray result) {
				//isCheckingQoSResult.set(false);
				try {
					RMBTResultPagerAdapter.this.setTestResultQoSDetails(new QoSServerResultCollection(result));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
        
        testResultDetailTask.execute(uid);        		
    }
    
    /**
     * 
     * @param vg
     * @param inflater
     * @return
     */
    public View instantiateMapView(final ViewGroup vg, final LayoutInflater inflater) {
    	//final ResultMapView view = new ResultMapView(activity.getApplicationContext(), activity, testUuid, testResult, inflater);
        //final View view = inflater.inflate(R.layout.result_map, this);
    	final View view = inflater.inflate(R.layout.result_map, vg, false);
    	//final View view = inflater.inflate(R.layout.result_map, null);
        
    	TextView locationView = (TextView) view.findViewById(R.id.result_map_location);
    	TextView locationProviderView = (TextView) view.findViewById(R.id.result_map_location_provider);
    	TextView motionView = (TextView) view.findViewById(R.id.result_map_motion);

    	TextView notAvailableView = (TextView) view.findViewById(R.id.result_mini_map_not_available);
    	MapView miniMapView = (MapView) view.findViewById(R.id.result_mini_map_view);
    	Button overlayButton = (Button) view.findViewById(R.id.result_mini_map_view_button);
    	
        try{
        	System.out.println(testResult.toString());
        	JSONObject testResultItem = testResult.getJSONObject(0);

        	if (testResultItem.has("geo_lat") && testResultItem.has("geo_long")) {
        		notAvailableView.setVisibility(View.GONE);
        		miniMapView.setVisibility(View.VISIBLE);
        		overlayButton.setVisibility(View.VISIBLE);
        		
            	if (dataChangedListener != null) {
            		dataChangedListener.onChange(false, true, "HAS_MAP");
            	}

        	
                final double geoLat = testResultItem.getDouble("geo_lat");
                final double geoLong = testResultItem.getDouble("geo_long");
                final int networkType = testResultItem.getInt("network_type");
                mapType = Helperfunctions.getMapType(networkType) + "/download";

               	if (testResultItem.has("motion")) {
               		motionView.setText(testResultItem.getString("motion"));
               		motionView.setVisibility(View.VISIBLE);
               	}
               	if (testResultItem.has("location")) {
               		String loc = testResultItem.getString("location");
               		int i = -1;
               		if (loc != null) {
                   		if ((i = loc.indexOf("(")) >= 0) {
                       		locationView.setText(loc.substring(0, i - 1).trim());
                       		locationProviderView.setText(loc.substring(i).trim());
                       		locationProviderView.setVisibility(View.VISIBLE);
                   		}
                   		else {
                       		locationView.setText(loc);
                   		}
                   		locationView.setVisibility(View.VISIBLE);
               		}
               	}

                	            
                testPoint = new LatLng(geoLat, geoLong);
                                        
                if (miniMapView != null) {
                	
                    try {
                        MapsInitializer.initialize(activity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    miniMapView.onCreate(null);
                    miniMapView.onResume();
                	miniMapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(testPoint, 16));
                	miniMapView.getMap().addMarker(new MarkerOptions().position(testPoint));
                	
                    final UiSettings uiSettings = miniMapView.getMap().getUiSettings();
                    uiSettings.setZoomControlsEnabled(false); // options.isEnableAllGestures());
                    uiSettings.setMyLocationButtonEnabled(false);
                    uiSettings.setCompassEnabled(false);
                    uiSettings.setRotateGesturesEnabled(false);
                    uiSettings.setScrollGesturesEnabled(false);
                    uiSettings.setZoomGesturesEnabled(false);
                    uiSettings.setAllGesturesEnabled(false);
                    
                    miniMapView.getMap().setTrafficEnabled(false);
                    miniMapView.getMap().setIndoorEnabled(false);
                    
                    miniMapView.getMap().addMarker(new MarkerOptions().position(testPoint).draggable(false).
                            	icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    
                    miniMapView.getMap().setMapType(activity.getMapTypeSatellite() ? GoogleMap.MAP_TYPE_SATELLITE : GoogleMap.MAP_TYPE_NORMAL);

                    miniMapView.getMap().setOnMapClickListener(new OnMapClickListener() {
    					@Override
    					public void onMapClick(LatLng arg0) {
    	                    final Runnable runnable = new Runnable()
    	                    {
    	                        @Override
    	                        public void run()
    	                        {
    	                            activity.showMap(mapType, testPoint, true, isFromMapView);
    	                        }
    	                    };
    	                    
    	                    runnable.run();												
    					}
    				});
                }

                if (overlayButton != null) {
                    overlayButton.setOnClickListener(new OnClickListener() {
        				
        				@Override
        				public void onClick(View v) {
        					activity.showMap(mapType, testPoint, true, isFromMapView);
        				}
        			});
                 
                    overlayButton.bringToFront();
                }
                
            Log.d("ResultMapView", "TESTRESULT OK. Drawing MapView");
        	}
        	else {
        		notAvailableView.setVisibility(View.VISIBLE);
        		miniMapView.setVisibility(View.GONE);
        		overlayButton.setVisibility(View.GONE);

            	if (dataChangedListener != null) {
            		dataChangedListener.onChange(true, false, "HAS_MAP");
            	}
        	}
        }
        catch (Exception e) {
        	e.printStackTrace();        	
        }
        
        return view;
    }
    
    
    private EndTaskListener<JSONArray> graphViewEndTaskListener = new EndTaskListener<JSONArray>() {
		
		@Override
		public void taskEnded(JSONArray result) {
			RMBTResultPagerAdapter.this.testGraphResult = result;
			System.out.println("REFRESHING GRAPHVIEW");
			graphView.refresh(result);
		}
	};
	
	private ResultGraphView graphView;
    
    /**
     * 
     * @param vg
     * @param inflater
     * @return
     */
    public View instantiateGraphView(final ViewGroup vg, final LayoutInflater inflater) {
    	System.out.println("instantiateGraphView");
    	if (graphView != null) {
    		graphView.recycle();
    		graphView = null;
    	}
   		graphView = new ResultGraphView(activity.getApplicationContext(), activity, testUuid, openTestUuid, testGraphResult, vg);

    	if (openTestUuid != null) {
        	graphView.initialize(graphViewEndTaskListener);    		
    	}

    	return graphView.getView();    	
    }
    
    @Override
    public Object instantiateItem(final ViewGroup vg, final int i)
    {
        final Context context = vg.getContext();
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        System.out.println("instantiateItem: " + i);
        
        View view = null;
        
        switch (RESULT_PAGE_TAB_TITLE_INDICES_ARRAY[i]) {
        case RESULT_PAGE_QOS:
        	view = instantiateQoSDetailView(vg, inflater);
        	break;
        case RESULT_PAGE_TEST:
        	view = instantiateDetailView(vg, inflater);
        	break;
        case RESULT_PAGE_MAIN_MENU:
        	view = instantiateResultPage(vg, inflater);
        	break;
        case RESULT_PAGE_MAP:
        	view = instantiateMapView(vg, inflater);
        	break;
        case RESULT_PAGE_GRAPH:
        	view = instantiateGraphView(vg, inflater);
        	break;
        }
        
        if (view != null)
            vg.addView(view);
        return view;
    }
    
    /**
	 * 
	 */
    @Override
    public void destroyItem(final ViewGroup vg, final int i, final Object obj)
    {
        final View view = (View) obj;
        vg.removeView(view);
    }
    
    @Override
    public boolean isViewFromObject(final View view, final Object object)
    {
        return view == object;
    }
    
    @Override
    public int getCount()
    {
    	return RESULT_PAGE_TAB_TITLE_ARRAY.length;//(RMBTResultPagerFragment.MAP_INDICATOR_DYNAMIC_VISIBILITY ? (hasMap ? 5 : 4) : 5);
    }
    
    @Override
    public CharSequence getPageTitle(final int position)
    {
        return activity.getResources().getStringArray(R.array.result_page_title)[position];
    }
    
    public void destroy()
    {
        if (handler != null) {
        	if (progressUpdater != null) {
        		handler.removeCallbacks(progressUpdater);
        	}
        }
    }
    
    public void onPause()
    {
        if (handler != null) {
        	if (progressUpdater != null) {
        		handler.removeCallbacks(progressUpdater);
        	}
        }
    }
    
    /**
     * 
     * @param listener
     */
    public void setOnCompleteListener(OnCompleteListener listener) {
    	this.completeListener = listener;
    }    

    /**
     * 
     * @param qosResults
     */
    public synchronized void setTestResultQoSDetails(QoSServerResultCollection qosResults) {
        boolean firstSet = false;
        
    	if (testResultQoSDetails == null) {
    		firstSet = true;
    	}
    	
    	testResultQoSDetails = qosResults;
    	
    	if (firstSet) {
			addQoSResultItem();
    	}
    }
    
    /**
     * 
     */
    public void startShareResultsIntent() {
    	try {
        	JSONObject resultListItem = testResult.getJSONObject(0);
        	final String shareText = resultListItem.getString("share_text");
        	final String shareSubject = resultListItem.getString("share_subject");
        	
            final Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
            sendIntent.setType("text/plain");
            activity.startActivity(Intent.createChooser(sendIntent, null));            		
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * 
     */
    public void showMap() {
    	if (mapType != null && testPoint != null) {
    		activity.showMap(mapType, testPoint, true, isFromMapView);
    	}
    	else {
    		activity.showMap(isFromMapView);
    	}
    }
}
