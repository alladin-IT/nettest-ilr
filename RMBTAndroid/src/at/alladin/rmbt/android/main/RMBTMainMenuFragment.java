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

package at.alladin.rmbt.android.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.openrmbt.android.BuildConfig;
import at.alladin.openrmbt.android.R;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.alladin.rmbt.android.impl.CpuStatAndroidImpl;
import at.alladin.rmbt.android.impl.CpuStatAndroidImpl.CpuMemClassificationEnum;
import at.alladin.rmbt.android.impl.MemInfoAndroidImpl;
import at.alladin.rmbt.android.main.InfoCollector.InfoCollectorType;
import at.alladin.rmbt.android.main.InfoCollector.OnInformationChangedListener;
import at.alladin.rmbt.android.test.ChangeableSpeedTestStatus;
import at.alladin.rmbt.android.util.ConfigHelper;
import at.alladin.rmbt.android.util.Helperfunctions;
import at.alladin.rmbt.android.util.InformationCollector;
import at.alladin.rmbt.android.util.net.InterfaceTrafficGatherer;
import at.alladin.rmbt.android.util.net.InterfaceTrafficGatherer.TrafficClassificationEnum;
import at.alladin.rmbt.android.util.net.NetworkFamilyEnum;
import at.alladin.rmbt.android.util.net.NetworkInfoCollector;
import at.alladin.rmbt.android.util.net.NetworkInfoCollector.CaptivePortalStatusEnum;
import at.alladin.rmbt.android.util.net.NetworkInfoCollector.IpStatus;
import at.alladin.rmbt.android.util.net.NetworkInfoCollector.OnNetworkInfoChangedListener;
import at.alladin.rmbt.android.util.net.NetworkUtil;
import at.alladin.rmbt.android.util.net.NetworkUtil.MinMax;
import at.alladin.rmbt.android.views.nerdmode.BgTrafficView;
import at.alladin.rmbt.android.views.nerdmode.GeoLocationView;
import at.alladin.rmbt.android.views.network.NetworkInfoView;
import at.alladin.rmbt.util.tools.CpuStat;
import at.alladin.rmbt.util.tools.MemInfo;

/**
 * 
 * @author
 * 
 */
public class RMBTMainMenuFragment extends AbstractMainMenuFragment
{
	/**
	 * invisibility value for "signal strength" text view
	 */
	private static final int INVISIBILITY_SIGNAL_STRENGTH = View.GONE;

	/**
	 * invisibility value for "signal strength extra" text view
	 */
	private static final int INVISIBILITY_SIGNAL_STRENGTH_EXTRA = View.GONE;

	public static enum OverlayType {
		IPV4(R.string.title_screen_ipv4, R.id.title_page_ipv4_button), 
		IPV6(R.string.title_screen_ipv6, R.id.title_page_ipv6_button), 
		IP(R.string.title_screen_ip, R.id.title_page_ip_button),
		CPU_MEM(R.string.title_screen_cpu_mem_info, R.id.title_page_stats_button),
		TRAFFIC(R.string.title_screen_traffic, R.id.title_page_traffic_button), 
		LOCATION(R.string.result_page_title_map, R.id.title_page_location_button);
		
		protected int resId;
		protected int buttonId;
		
		OverlayType(int resId, int buttonId) {
			this.resId = resId;
			this.buttonId = buttonId;
		}
		
		public int getResourceId() {
			return resId;
		}
		
		public int getButtonId() {
			return buttonId;
		}
	}
	
	public final static String BUNDLE_INFO_LAST_ANTENNA_IMAGE = "last_antenna_image";
	public final static String BUNDLE_INFO_CAPTIVE_PORTAL_STATUS = "captive_portal_status";
	public final static String BUNDLE_INFO_COLLECTOR = "info";

	public final static int BACKGROUND_TRAFFIC_MEASUREMENT_TIME = 1000;
	public final static int INFORMATION_COLLECTOR_TIME = 1000;
	
    private InformationCollector informationCollector;

	private ChangeableSpeedTestStatus speedTestStatus;

	private View rootView;

	private View nerdmodeContainerView;

    /**
	 * 
	 */
    private static final String DEBUG_TAG = "RMBTMainMenuFragment";
    
    private TextView startButtonText;
    private TextView infoNetwork;
    private TextView infoNetworkLabel;
    private TextView infoNetworkType;
    private TextView infoSignalStrength;
    private TextView infoSignalStrengthExtra;
    private TextView infoCpuStat;
    private TextView infoMemStat;
    //private TextView infoIp;
    //private TextView infoTraffic;
    private View ipv4Button;
    private View ipv6Button;
    private View ipButton;
    private View locationButton;
    private View trafficButton;
    private View cpuMemStatsButton;
    private View startButton;

	/**
	 * NERDMODE views
	 */
	private GeoLocationView gpsView;
	private BgTrafficView bgTrafficView;

	/**
	 * new network info view
	 */
	private NetworkInfoView networkInfoView;
    
    private ImageView ipv4View;
	private TextView ipv4TextView;
    private ProgressBar ipv4ProgressView;
    private ImageView ipv6View;
	private TextView ipv6TextView;
    private ProgressBar ipv6ProgressView;
    private ImageView locationView;
    private ImageView antennaView;
    private ImageView ulSpeedView;
    private ImageView dlSpeedView;
    private ImageView captivePortalWarning;
    public boolean runInfoRunnable = false;
    public Handler infoHandler = new Handler();

	private InfoCollector infoCollector = InfoCollector.getInstance();
	private InterfaceTrafficGatherer interfaceTrafficGatherer;
	private Animation pulseAnimation;
    
	private Integer lastSignal;
    private ListView infoOverlayList;
    private RelativeLayout infoOverlay;
    private TextView infoOverlayTitle;
    private Map<OverlayType, InfoArrayAdapter> infoValueListAdapterMap = new HashMap<RMBTMainMenuFragment.OverlayType, InfoArrayAdapter>();
    
    private final CpuStat cpuStat = new CpuStatAndroidImpl();
    private final MemInfo memInfo = new MemInfoAndroidImpl();
    private final DecimalFormat percentFormat = new DecimalFormat("##0.0");
    
    /**
     * number format used for ul/dl traffic
     */
    DecimalFormat speedFormat;

    /**
	 * 
	 */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    	Log.i(DEBUG_TAG, "onCreate");
        pulseAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
        interfaceTrafficGatherer = new InterfaceTrafficGatherer();
        informationCollector = new InformationCollector(getActivity(), false, false);
        speedFormat = new DecimalFormat(String.format("@@ %s", getActivity().getResources().getString(R.string.test_mbps)));
    }
    
    /**
     * 
     * @return
     */
    public RMBTMainActivity getMainActivity()
    {
        return (RMBTMainActivity) getActivity();
    }

	/**
	 * 
	 */
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
		rootView = inflater.inflate(R.layout.simple_title_screen, container, false);
		return createView(rootView, inflater, savedInstanceState);
    }

    /**
     * 
     * @param view
     * @param inflater
     * @param savedInstanceState
     * @return
     */
    private View createView(View view, LayoutInflater  inflater, Bundle savedInstanceState) {

        startButton = view.findViewById(R.id.title_page_start_button);
        startButtonText = (TextView) view.findViewById(R.id.title_page_start_button_text);
        
        if (startButton != null)
        {
			startButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (BuildConfig.TEST_SHOW_DATA_CONSUMPTION_WARNING) {
						final String message = getResources().getString(R.string.start_data_consumption_warning);
						final String appName = getResources().getString(R.string.app_name);
						final String customerName = getResources().getString(R.string.customer_name);
						AlertDialog alert = new AlertDialog.Builder(getActivity()).
							setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									((RMBTMainActivity) getActivity()).startTest(true);
								}
							}).
							setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									System.out.println("Decline");
								}
							}).
							setMessage(MessageFormat.format(message, appName, customerName)).
							setCancelable(false).
							create();

						alert.show();
					}
					else {
					   ((RMBTMainActivity) getActivity()).startTest(true);
					}
				}
			});

			if (((RMBTMainActivity) getActivity()).getLastIntermediateResult() != null) {
				startButtonText.setText(R.string.menu_button_start_again_icon);
			}
		}
        
        final View startButtonLayout = view.findViewById(R.id.title_page_start_button_layout);
        if (startButtonLayout != null)
        {
            final Animation delayedPulseAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.delayed_pulse);
            delayedPulseAnimation.setAnimationListener(new Animation.AnimationListener()
            {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationRepeat(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation)
                {
                    
                    startButtonLayout.startAnimation(animation);
                }
            });
            startButtonLayout.startAnimation(delayedPulseAnimation);
        }

		speedTestStatus = (ChangeableSpeedTestStatus) getActivity().getSupportFragmentManager().findFragmentById(R.id.title_page_speed_test_status);

		//infoLocation = (TextView) view.findViewById(R.id.info_gps_location);
        //infoLocation.setVisibility(View.GONE);
        infoNetwork = (TextView) view.findViewById(R.id.info_network_name);
        setViewVisibility(infoNetwork, View.GONE);
        
        infoNetworkLabel = (TextView) view.findViewById(R.id.info_network_name_label);
        setViewVisibility(infoNetworkLabel, View.GONE);

        infoNetworkType = (TextView) view.findViewById(R.id.info_network_type);
        setViewVisibility(infoNetworkType, View.GONE);
        
        infoSignalStrength = (TextView) view.findViewById(R.id.info_signal_strength);
        setViewVisibility(infoSignalStrength, INVISIBILITY_SIGNAL_STRENGTH);

        infoSignalStrengthExtra = (TextView) view.findViewById(R.id.info_signal_strength_extra);
        setViewVisibility(infoSignalStrengthExtra, INVISIBILITY_SIGNAL_STRENGTH_EXTRA);

        infoCpuStat = (TextView) view.findViewById(R.id.cpu_status);
        infoMemStat = (TextView) view.findViewById(R.id.ram_status);

        locationView = (ImageView) view.findViewById(R.id.location_image);
        setViewVisibility(locationView, View.INVISIBLE);

        locationButton = view.findViewById(R.id.title_page_location_button);
        if (locationButton != null) {
        	locationButton.setOnClickListener(detailShowOnClickListener);
        }

		nerdmodeContainerView = (View) view.findViewById(R.id.nerdmode_view);

		gpsView = (GeoLocationView) view.findViewById(R.id.nerdmode_gps_view);

        ipv4View = (ImageView) view.findViewById(R.id.ipv4_status);
        ipv4ProgressView = (ProgressBar) view.findViewById(R.id.ipv4_status_progress_bar);
        ipv4Button = view.findViewById(R.id.title_page_ipv4_button);
        if (ipv4Button != null) {
        	ipv4Button.setOnClickListener(detailShowOnClickListener);
        }
		ipv4TextView = (TextView) view.findViewById(R.id.ipv4_text_status);

        ipv6View = (ImageView) view.findViewById(R.id.ipv6_status);
        ipv6ProgressView = (ProgressBar) view.findViewById(R.id.ipv6_status_progress_bar);
        ipv6Button = view.findViewById(R.id.title_page_ipv6_button);
        if (ipv6Button != null) {
        	ipv6Button.setOnClickListener(detailShowOnClickListener);
        }
		ipv6TextView = (TextView) view.findViewById(R.id.ipv6_text_status);

        ipButton = view.findViewById(R.id.title_page_ip_button);
        if (ipButton != null) {
        	ipButton.setOnClickListener(detailShowOnClickListener);
        }


		//////////////
		// Network Info
		networkInfoView = (NetworkInfoView) view.findViewById(R.id.infocollector_status_view);

		//////////////
		// BG-Traffic

		bgTrafficView = (BgTrafficView) view.findViewById(R.id.nerdmode_bg_traffic_view);

		if (bgTrafficView != null) {
			trafficButton = (View) bgTrafficView;
		}
		else {
			trafficButton = view.findViewById(R.id.title_page_traffic_button);
		}

		if (trafficButton != null) {
			trafficButton.setOnClickListener(detailShowOnClickListener);
		}

		//////////////
		// CPU/MEM Stat Button

		cpuMemStatsButton = view.findViewById(R.id.title_page_stats_button);
        if (cpuMemStatsButton != null) {
        	cpuMemStatsButton.setOnClickListener(detailShowOnClickListener);
        }
        
        infoOverlayList = (ListView) view.findViewById(R.id.info_overlay_list);
        if (infoOverlayList != null) {
        	infoValueListAdapterMap.put(OverlayType.TRAFFIC, new InfoArrayAdapter(getActivity(), OverlayType.TRAFFIC,
        			InfoOverlayEnum.UL_TRAFFIC, InfoOverlayEnum.DL_TRAFFIC));
        	
        	infoValueListAdapterMap.put(OverlayType.IPV4, new InfoArrayAdapter(getActivity(), OverlayType.IPV4,
        			InfoOverlayEnum.IPV4, InfoOverlayEnum.IPV4_PUB));
        	
        	infoValueListAdapterMap.put(OverlayType.IPV6, new InfoArrayAdapter(getActivity(), OverlayType.IPV6, 
        			InfoOverlayEnum.IPV6, InfoOverlayEnum.IPV6_PUB));
        	
        	infoValueListAdapterMap.put(OverlayType.IP, new InfoArrayAdapter(getActivity(), OverlayType.IP, InfoOverlayEnum.IPV4,
        			InfoOverlayEnum.IPV4_PUB, InfoOverlayEnum.IPV6, InfoOverlayEnum.IPV6_PUB));
        	
        	infoValueListAdapterMap.put(OverlayType.LOCATION, new InfoArrayAdapter(getActivity(), OverlayType.LOCATION,
        			InfoOverlayEnum.LOCATION));
        	
        	infoValueListAdapterMap.put(OverlayType.CPU_MEM, new InfoArrayAdapter(getActivity(), OverlayType.CPU_MEM,
        			InfoOverlayEnum.CPU_USAGE, InfoOverlayEnum.MEM_USAGE,
        			InfoOverlayEnum.MEM_FREE, InfoOverlayEnum.MEM_TOTAL));
        }

        infoOverlayTitle = (TextView) view.findViewById(R.id.info_overlay_title);
        
        infoOverlay = (RelativeLayout) view.findViewById(R.id.info_overlay);
        if (infoOverlay != null) {
        	if (infoOverlay.getVisibility() == View.GONE) {
        		infoOverlay.setOnClickListener(detailHideOnClickListener);
        		infoOverlayList.setOnItemClickListener(detailHideOnItemClickListener);
        	}
        }

        captivePortalWarning = (ImageView) view.findViewById(R.id.captive_portal_image);
        
        antennaView = (ImageView) view.findViewById(R.id.antenne_image);
        if (antennaView != null) {
        	//antennaView.setOnClickListener(detailShowOnClickListener);
        	
        	if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_INFO_LAST_ANTENNA_IMAGE)) {
        		int antennaImageId = savedInstanceState.getInt(BUNDLE_INFO_LAST_ANTENNA_IMAGE);
        		antennaView.setImageResource(antennaImageId);
        		antennaView.setTag(antennaImageId);
        	}
        }
        ulSpeedView = (ImageView) view.findViewById(R.id.traffic_ul_image);
        dlSpeedView = (ImageView) view.findViewById(R.id.traffic_dl_image);
        
        return view;
    }
    
    @Override
    public void onPause() {
    	Log.i(DEBUG_TAG, "onPause");
    	super.onPause();
        infoCollector.removeListener(onInfoChangedListener);
        NetworkInfoCollector.getInstance().removeOnNetworkInfoChangedListener(onNetworkChangedListener);
    	runInfoRunnable = false;
        //infoLocation.setVisibility(View.GONE);
    	setViewVisibility(infoNetwork, View.GONE);
        setViewVisibility(infoNetworkLabel, View.GONE);
        setViewVisibility(infoNetworkType, View.GONE);
        setViewVisibility(infoSignalStrength, INVISIBILITY_SIGNAL_STRENGTH);
        setViewVisibility(infoSignalStrengthExtra, INVISIBILITY_SIGNAL_STRENGTH_EXTRA);
        setViewVisibility(locationView, View.INVISIBLE);

        if (informationCollector != null) {
            informationCollector.unload();
        }
        infoHandler.removeCallbacks(infoRunnable);
        infoHandler.removeCallbacks(interfaceTrafficRunnable);

		if (speedTestStatus != null && speedTestStatus instanceof Fragment) {
			getActivity().getSupportFragmentManager().beginTransaction().remove((Fragment) speedTestStatus).commit();
		}

		final Fragment switchFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.title_page_switch_to_results);
		if (switchFragment != null) {
			getActivity().getSupportFragmentManager().beginTransaction().remove(switchFragment).commit();
		}
	}
    
    @Override
    public void onResume() {
    	Log.i(DEBUG_TAG, "onResume");
    	super.onResume();
        infoCollector.addListener(onInfoChangedListener);
        NetworkInfoCollector.getInstance().addOnNetworkChangedListener(onNetworkChangedListener);
        if (informationCollector != null) {
            runInfoRunnable = true;
            informationCollector.init();
        }
        infoHandler.post(infoRunnable);
        infoHandler.post(interfaceTrafficRunnable);
        infoCollector.refresh();        
        
        getActivity().getActionBar().show();
        ((RMBTMainActivity) getActivity()).setLockNavigationDrawer(false);

		toggleNerdModeVisibility(getMainActivity().getLastTestUuid() == null);

		/*
		if (gpsView != null && getMainActivity().getLastTestUuid()!=null) {
			((View)gpsView).setVisibility(View.GONE);
			//System.out.println("VISIBILITY = " + ((View)gpsView).getVisibility());
		}
		*/
	}

	public void toggleNerdModeVisibility(boolean isVisible) {
		if (nerdmodeContainerView != null) {
			nerdmodeContainerView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
		}
	}

    public Runnable interfaceTrafficRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (interfaceTrafficGatherer != null) {
				interfaceTrafficGatherer.run();
				final long rxRate = interfaceTrafficGatherer.getRxRate();
				final long txRate = interfaceTrafficGatherer.getTxRate();
				TrafficClassificationEnum rxTrafficClass = TrafficClassificationEnum.classify(rxRate);
				TrafficClassificationEnum txTrafficClass = TrafficClassificationEnum.classify(txRate);
				
				infoCollector.setUlTraffic(txTrafficClass);
				infoCollector.setDlTraffic(rxTrafficClass);
			}
			
			if (infoOverlay != null && infoOverlay.getVisibility() == View.VISIBLE) {
				((BaseAdapter) infoOverlayList.getAdapter()).notifyDataSetChanged();
			}
			
			infoHandler.postDelayed(interfaceTrafficRunnable, BACKGROUND_TRAFFIC_MEASUREMENT_TIME);
		}
	};
    
    /**
     * 
     */
    public Runnable infoRunnable = new Runnable() {
		@Override
		public void run() {			
			if (informationCollector != null) {
				if (cpuStat != null) {
					final float[] cpus = cpuStat.update(false);
					if (cpus != null && cpus.length > 0) {
						float total = 0f;
						for (float cpu : cpus) {
							total += cpu;
						}
						
						infoCollector.setCpuUsage((total / (float)cpus.length) * 100f);
					}
				}
				
				if (memInfo != null) {
					memInfo.update();
					infoCollector.setMemUsage(100f - (((float)memInfo.getFreeMem() / (float)memInfo.getTotalMem()) * 100f));
				}

				//prefix (signal term) for single signal information (= not 4G)
				//String signalTerm = getActivity().getString(R.string.term_signal) + ": ";
				String signalTerm = "";
				
				Location loc = informationCollector.getLocationInfo();
		        infoCollector.setLocation(loc);
				
				int lastNetworkType = informationCollector.getNetwork();
                String lastNetworkTypeString = Helperfunctions.getNetworkTypeName(lastNetworkType);
				//System.out.println("lastNetworkType: " + lastNetworkType + ", lastNetworkTypeString: " + lastNetworkTypeString);
				
				Integer curSignal = lastSignal;
				if (!"UNKNOWN".equals(lastNetworkTypeString)) {
					setViewVisibility(infoSignalStrength, View.VISIBLE);
					
	                Integer signal = informationCollector.getSignal();
	                if (!"BLUETOOTH".equals(lastNetworkTypeString) && !"ETHERNET".equals(lastNetworkTypeString)) {
	                	if (signal != null && signal > Integer.MIN_VALUE) {
		                	int signalType = informationCollector.getSignalType();
		                	curSignal = signal;

		                	if (signalType == InformationCollector.SINGAL_TYPE_RSRP) {
		                	    setViewText(infoSignalStrength, "RSRP: " + signal + " dBm");
			                	infoCollector.setSignal(signal);
			                	Integer signalRsrq = informationCollector.getSignalRsrq();
								if (signalRsrq != null) {
										if (infoSignalStrengthExtra != null) {
											setViewVisibility(infoSignalStrengthExtra, View.VISIBLE);
											infoCollector.setSignalRsrq(signalRsrq);
											setViewText(infoSignalStrengthExtra, "RSRQ: " + signalRsrq + " dB");
										}
										else {
											setViewText(infoSignalStrength, "RSRP: " + signal + " dBm, RSRQ: " + signalRsrq + " dB");
										}
								} else {
									setViewVisibility(infoSignalStrengthExtra, INVISIBILITY_SIGNAL_STRENGTH_EXTRA);
								}
		                	}
		                	else {
		                		infoCollector.setSignal(curSignal);
		                		setViewText(infoSignalStrength, signalTerm + signal + " dBm");
		                		setViewVisibility(infoSignalStrengthExtra, INVISIBILITY_SIGNAL_STRENGTH_EXTRA);
		                	}
	                	}
	                	else {
	                		curSignal = Integer.MIN_VALUE;
	                		setViewVisibility(infoSignalStrength, INVISIBILITY_SIGNAL_STRENGTH);
	                		setViewVisibility(infoSignalStrengthExtra, INVISIBILITY_SIGNAL_STRENGTH_EXTRA);
	                	}
	                }
				}

				if (lastNetworkTypeString != null && !"UNKNOWN".equals(lastNetworkTypeString))  {

					if (infoNetwork != null) {
						if (infoNetwork.getVisibility() == View.GONE) {
							infoNetwork.setVisibility(View.VISIBLE);
							setViewVisibility(infoNetworkLabel, View.VISIBLE);
						}

						String networkName = informationCollector.getOperatorName();
						if (networkName != null && !"()".equals(networkName)) {
							infoCollector.setNetworkName(networkName);
							infoNetwork.setText(networkName);
						}
						else {
							infoCollector.setNetworkName(networkName);
							infoNetwork.setVisibility(View.GONE);
							setViewVisibility(infoNetworkLabel, View.GONE);
						}
					}

					if (antennaView != null && antennaView.getVisibility() != View.VISIBLE) {
						antennaView.setVisibility(View.VISIBLE);
					}

					lastSignal = curSignal;

                	NetworkFamilyEnum networkFamily = NetworkFamilyEnum.getFamilyByNetworkId(lastNetworkTypeString);
					if (NetworkFamilyEnum.UNKNOWN.equals(networkFamily)) {
                		setViewVisibility(infoNetworkType, View.GONE);
                		infoCollector.setNetworkTypeString(lastNetworkTypeString);
                		infoNetworkType.setText(lastNetworkTypeString);	
                	}
                	else {
						setViewVisibility(infoNetworkType, View.VISIBLE);

						if (lastNetworkTypeString.equals(networkFamily.getNetworkFamily())) {
							infoCollector.setNetworkTypeString(lastNetworkTypeString);
							setViewText(infoNetworkType, lastNetworkTypeString);
						}
						else {
							infoCollector.setNetworkTypeString(networkFamily.getNetworkFamily() + "/" + lastNetworkTypeString);
							setViewText(infoNetworkType, networkFamily.getNetworkFamily() + "/" + lastNetworkTypeString);
						}
                	}
                }
                else {
	                curSignal = Integer.MIN_VALUE;
	                lastSignal = curSignal;
//		                Log.d(DEBUG_TAG,"lastNetworkTypeString: " + lastNetworkTypeString);
	                infoCollector.setSignal(lastSignal);
	                infoCollector.setNetworkName(null);
            		setViewVisibility(infoNetworkType, View.GONE);
            		setViewVisibility(infoNetwork, View.GONE);
					setViewVisibility(infoNetworkLabel, View.GONE);
					setViewVisibility(infoSignalStrength, INVISIBILITY_SIGNAL_STRENGTH);
					setViewVisibility(infoSignalStrengthExtra, INVISIBILITY_SIGNAL_STRENGTH_EXTRA);
					if (antennaView != null && antennaView.getVisibility() != View.VISIBLE) {
					       refreshAntennaImage(curSignal);
					}
                }
				
				if (antennaView != null && antennaView.getVisibility() != View.VISIBLE) {
//					Log.d(DEBUG_TAG,"lastNetworkTypeString: " + lastNetworkTypeString);
                	curSignal = Integer.MIN_VALUE;
                	lastSignal = curSignal;
                	infoCollector.setSignal(lastSignal);
			        refreshAntennaImage(Integer.MIN_VALUE);
				}
				
				
				if (curSignal != null) {
					refreshAntennaImage(curSignal);
				}
				
				//infoCollector.setIpv4(ConfigHelper.getLastIp(getActivity())); 

				final NetworkInfoCollector netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
				if (netInfo != null) {
					if (ConfigHelper.isIpPolling(getActivity())) {
						netInfo.gatherIpInformation(true);
					}
					else {
						if (NetworkInfoCollector.IP_METHOD == NetworkInfoCollector.IP_METHOD_NETWORKINTERFACE) {
							netInfo.gatherInterfaceInformation(true);
						}
						else {
							netInfo.gatherIpInformation(false);
						}
					}
					
					infoCollector.setHasControlServerConnection(netInfo.hasIpFromControlServer());
					infoCollector.setCaptivePortalFound(netInfo.getCaptivePortalStatus().equals(CaptivePortalStatusEnum.FOUND));
					infoCollector.refreshIpAndAntenna();
					infoCollector.dispatchInfoChangedEvent(InfoCollectorType.IPV4, null, infoCollector.getIpv4());
					infoCollector.dispatchInfoChangedEvent(InfoCollectorType.IPV6, null, infoCollector.getIpv6());
					
					netInfo.onNetworkChange(getActivity(), null);
				}
				
				if (netInfo.getCaptivePortalStatus() == CaptivePortalStatusEnum.FOUND 
						|| netInfo.getCaptivePortalStatus() == CaptivePortalStatusEnum.NOT_FOUND) {
					setCaptivePortalStatus(netInfo.getCaptivePortalStatus() == CaptivePortalStatusEnum.FOUND);
				}
			}
			
			if (runInfoRunnable) { 
				informationCollector.reInit();
				infoHandler.postDelayed(infoRunnable, INFORMATION_COLLECTOR_TIME);	
			}
		}
	};
	
	public void refreshAntennaImage(int signal) {
		if (antennaView != null) {
			if (antennaView.getVisibility() != View.VISIBLE) {
				antennaView.setVisibility(View.VISIBLE);
			}

			int antennaImageRes = getAntennaImageResourceId(signal);
			antennaView.setImageResource(antennaImageRes);
			antennaView.setTag(antennaImageRes);
		}
	}
	
	/**
	 * 
	 * @param signal
	 * @return
	 */
	private int getAntennaImageResourceId(int signal) {
		int lastNetworkType = informationCollector.getNetwork();
		String lastNetworkTypeString = Helperfunctions.getNetworkTypeName(lastNetworkType);
		
		if (lastNetworkType == TelephonyManager.NETWORK_TYPE_UNKNOWN) // || signal == Integer.MIN_VALUE)
		    return R.drawable.signal_no_connection;
		
		boolean wlan = "WLAN".equals(lastNetworkTypeString);
		
    	int signalType = informationCollector.getSignalType();
    	
    	double relativeSignal = -1d;
    	MinMax<Integer> signalBounds = NetworkUtil.getSignalStrengthBounds(signalType);

        if (! (signalBounds.min == Integer.MIN_VALUE || signalBounds.max == Integer.MAX_VALUE)) {
            relativeSignal = (double)(signal - signalBounds.min) / (double)(signalBounds.max - signalBounds.min);
        }
		//System.out.println("relativeSignal: " + relativeSignal + ", networkType: " + networkType + ", lastNetworkTypeString: " + lastNetworkTypeString);
		if (relativeSignal < 0.25d) {
			return (wlan ? R.drawable.signal_wlan_25 : R.drawable.signal_mobile_25);
		}
		else if (relativeSignal < 0.5d) {
			return (wlan ? R.drawable.signal_wlan_50 : R.drawable.signal_mobile_50);
		}
		else if (relativeSignal < 0.75d) {
			return (wlan ? R.drawable.signal_wlan_75 : R.drawable.signal_mobile_75);
		}
		else {
			return (wlan ? R.drawable.signal_wlan : R.drawable.signal_mobile);
		}
	}
	/**
	 * 
	 */
	private final OnInformationChangedListener onInfoChangedListener = new OnInformationChangedListener() {		
		@Override
		public void onInformationChanged(InfoCollectorType type, Object oldValue, Object newValue) {
			switch (type) {
			case CPU:
				setViewText(infoCpuStat, percentFormat.format(newValue) + "%");
				if (BuildConfig.NERDMODE_CPU_CLASSIFICATION) {
					setViewTextColorResource(infoCpuStat, CpuMemClassificationEnum.classify((Float) newValue).getResId());
				}
				break;
			case MEMORY:
				setViewText(infoMemStat, percentFormat.format(newValue) + "%");
				//setViewTextColorResource(infoMemStat, CpuMemClassificationEnum.classify((Float) newValue).getResId());
				break;
				
			case LOCATION:
				if (locationView != null) {
					if (locationView.getVisibility() == View.INVISIBLE) {
						locationView.setVisibility(View.VISIBLE);
					}
					locationView.setImageResource(newValue != null ? R.drawable.ic_action_location_found : R.drawable.ic_action_location_off);
				}

				if (gpsView != null) {
					gpsView.updateGeoLocationViewState(newValue != null ? GeoLocationView.GeoLocationViewState.ON_GPS : GeoLocationView.GeoLocationViewState.TURNED_OFF);
					gpsView.updateGeoLocationString(getGeolocationString((Location) newValue));
				}

				if (newValue == null) {
					infoValueListAdapterMap.get(OverlayType.LOCATION).removeElement(InfoOverlayEnum.LOCATION_ACCURACY);
					infoValueListAdapterMap.get(OverlayType.LOCATION).removeElement(InfoOverlayEnum.LOCATION_AGE);
					infoValueListAdapterMap.get(OverlayType.LOCATION).removeElement(InfoOverlayEnum.LOCATION_SOURCE);
					infoValueListAdapterMap.get(OverlayType.LOCATION).removeElement(InfoOverlayEnum.LOCATION_ALTITUDE);
				}
				else {
					infoValueListAdapterMap.get(OverlayType.LOCATION).addElement(InfoOverlayEnum.LOCATION_ACCURACY);
					infoValueListAdapterMap.get(OverlayType.LOCATION).addElement(InfoOverlayEnum.LOCATION_AGE);
					infoValueListAdapterMap.get(OverlayType.LOCATION).addElement(InfoOverlayEnum.LOCATION_SOURCE);
					infoValueListAdapterMap.get(OverlayType.LOCATION).addElement(InfoOverlayEnum.LOCATION_ALTITUDE);
				}
				break;
			case NETWORK_TYPE:
				if (antennaView != null) {
					//System.out.println("NETWORK_TYPE changed to: " + newValue);
				    Integer signal = informationCollector.getSignal();
				    refreshAntennaImage(signal != null ? signal : Integer.MIN_VALUE);
				}
				//no break; here!!
				//if the network type or the network family changes, the same label TextView is used
			case NETWORK_FAMILY:
				if (infoNetworkType != null && infoNetworkType.getVisibility() == View.VISIBLE) {
					infoNetworkType.startAnimation(pulseAnimation);
					//((RMBTMainActivity)getActivity()).checkIp();
				}
				break;
			case NETWORK_NAME:
				//reset all IPs on network change name:
				NetworkInfoCollector.getInstance().resetAllPrivateIps();
				NetworkInfoCollector.getInstance().resetAllPublicIps();

				if (infoNetwork != null && infoNetwork.getVisibility() == View.VISIBLE) {
					if (newValue != null && !((String) newValue).trim().equals("()")) {
						Log.d(DEBUG_TAG, "networkName: " + newValue);
						infoNetwork.startAnimation(pulseAnimation);
					}
				}
				break;
			case SIGNAL_RSRQ:
				if (infoSignalStrengthExtra != null && infoSignalStrengthExtra.getVisibility() == View.VISIBLE) {
					if (oldValue != null && ((Integer)oldValue != 0)) {
						infoSignalStrengthExtra.startAnimation(pulseAnimation);
					}
				}
				break;
			case SIGNAL:
				if (antennaView != null && antennaView.getVisibility() == View.VISIBLE && newValue != null) {
					refreshAntennaImage((Integer) newValue);
				}
			
				if (type.equals(InfoCollectorType.SIGNAL) && infoSignalStrength != null && infoSignalStrength.getVisibility() == View.VISIBLE) {
					if (oldValue != null && ((Integer)oldValue != 0)) {
						infoSignalStrength.startAnimation(pulseAnimation);
					}
				}
//				else if (infoSignalStrengthExtra != null && infoSignalStrengthExtra.getVisibility() == View.VISIBLE) {
//					infoSignalStrengthExtra.startAnimation(pulseAnimation);
//				}
				break;
			case IPV4:
			case IPV6:
				if (getActivity() != null) {
					NetworkInfoCollector netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
					if (netInfo != null) {
						if (netInfo.getPublicIpv4() != null) {
							netInfo.setCaptivePortalStatus(CaptivePortalStatusEnum.NOT_FOUND);
							infoValueListAdapterMap.get(OverlayType.IPV4).addElement(InfoOverlayEnum.IPV4_PUB);
							infoValueListAdapterMap.get(OverlayType.IP).addElement(InfoOverlayEnum.IPV4_PUB, 1);
						}
						else {
							infoValueListAdapterMap.get(OverlayType.IPV4).removeElement(InfoOverlayEnum.IPV4_PUB);
							infoValueListAdapterMap.get(OverlayType.IP).removeElement(InfoOverlayEnum.IPV4_PUB);
						}
						
						if (netInfo.getPublicIpv6() != null) {
							netInfo.setCaptivePortalStatus(CaptivePortalStatusEnum.NOT_FOUND);
							infoValueListAdapterMap.get(OverlayType.IPV6).addElement(InfoOverlayEnum.IPV6_PUB);
							infoValueListAdapterMap.get(OverlayType.IP).addElement(InfoOverlayEnum.IPV6_PUB, 2);
						}
						else {
							infoValueListAdapterMap.get(OverlayType.IPV6).removeElement(InfoOverlayEnum.IPV6_PUB);
							infoValueListAdapterMap.get(OverlayType.IP).removeElement(InfoOverlayEnum.IPV6_PUB);
						}
						
						//Log.d(DEDBUG_TAG, "IPv4: " + netInfo.getIpv4Status() + ", IPv6: " + netInfo.getIpv6Status());
						if (ipv4ProgressView != null) {
							if (netInfo.getIpv4Status() == IpStatus.STATUS_NOT_AVAILABLE) {
								ipv4ProgressView.setVisibility(View.VISIBLE);
								setViewVisibility(ipv4View, View.GONE);
								setViewVisibility(ipv4TextView, View.GONE);
							}
							else {
								ipv4ProgressView.setVisibility(View.GONE);
								setViewVisibility(ipv4View, View.VISIBLE);
								setViewImageResource(ipv4View, netInfo.getIpv4Status().getResourceId());
								setViewVisibility(ipv4TextView, View.VISIBLE);
								setViewText(ipv4TextView, getString(netInfo.getIpv4Status().getStringId()));
								setViewTextColorResource(ipv4TextView, netInfo.getIpv4Status().getColorId());
							}
						}
						if (ipv6ProgressView != null) {
							if (netInfo.getIpv6Status() == IpStatus.STATUS_NOT_AVAILABLE) {
								ipv6ProgressView.setVisibility(View.VISIBLE);
								setViewVisibility(ipv6View, View.GONE);
								setViewVisibility(ipv6TextView, View.GONE);
							}
							else {
								ipv6ProgressView.setVisibility(View.GONE);
								setViewVisibility(ipv6View, View.VISIBLE);
								setViewImageResource(ipv6View, netInfo.getIpv6Status().getResourceId());
								setViewVisibility(ipv6TextView, View.VISIBLE);
								setViewText(ipv6TextView, getString(netInfo.getIpv6Status().getStringId()));
								setViewTextColorResource(ipv6TextView, netInfo.getIpv6Status().getColorId());
							}
						}
						
						if (netInfo.getIpv4Status().equals(IpStatus.CONNECTED_NAT) 
								|| netInfo.getIpv4Status().equals(IpStatus.CONNECTED_NO_NAT) 
								|| netInfo.getIpv6Status().equals(IpStatus.CONNECTED_NAT) 
								|| netInfo.getIpv6Status().equals(IpStatus.CONNECTED_NO_NAT)) {
						}
					}
				}
				break;
			case UL_TRAFFIC:
				if (ulSpeedView != null) {
					ulSpeedView.setImageResource(((TrafficClassificationEnum) newValue).getResId());
					ulSpeedView.setRotation(180f);
				}
				if (bgTrafficView != null) {
					bgTrafficView.updateBgTrafficOut((TrafficClassificationEnum) newValue);
				}
				break;
			case DL_TRAFFIC:
				if (dlSpeedView != null) {
					dlSpeedView.setImageResource(((TrafficClassificationEnum) newValue).getResId());
				}
				if (bgTrafficView != null) {
					bgTrafficView.updateBgTrafficIn((TrafficClassificationEnum) newValue);
				}
				break;
			case CAPTIVE_PORTAL_STATUS:
				setCaptivePortalStatus((Boolean) newValue);
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 
	 * @param hasCaptivePortal
	 */
	private void setCaptivePortalStatus(boolean hasCaptivePortal) {
		if (captivePortalWarning != null) {
			captivePortalWarning.setVisibility(hasCaptivePortal ? View.VISIBLE : View.GONE);

		}

		if (!hasCaptivePortal) {
			infoValueListAdapterMap.get(OverlayType.IPV4).removeElement(InfoOverlayEnum.CAPTIVE_PORTAL_STATUS);
			infoValueListAdapterMap.get(OverlayType.IPV6).removeElement(InfoOverlayEnum.CAPTIVE_PORTAL_STATUS);
			infoValueListAdapterMap.get(OverlayType.IP).removeElement(InfoOverlayEnum.CAPTIVE_PORTAL_STATUS);
		}
		else {
			infoValueListAdapterMap.get(OverlayType.IPV4).addElement(InfoOverlayEnum.CAPTIVE_PORTAL_STATUS);
			infoValueListAdapterMap.get(OverlayType.IPV6).addElement(InfoOverlayEnum.CAPTIVE_PORTAL_STATUS);
			infoValueListAdapterMap.get(OverlayType.IP).addElement(InfoOverlayEnum.CAPTIVE_PORTAL_STATUS);
		}
	}

	/**
	 * 
	 */
	private final OnClickListener detailShowOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (infoOverlay != null) {
				switch (v.getId()) {
				case R.id.title_page_stats_button:
					infoOverlayTitle.setText(getResources().getText(OverlayType.CPU_MEM.getResourceId()));
					infoOverlayList.setAdapter(infoValueListAdapterMap.get(OverlayType.CPU_MEM));
					break;					
				case R.id.title_page_ip_button:
					infoOverlayTitle.setText(getResources().getText(OverlayType.IP.getResourceId()));
					infoOverlayList.setAdapter(infoValueListAdapterMap.get(OverlayType.IP));
					break;
				case R.id.title_page_ipv4_button:
					infoOverlayTitle.setText(getResources().getText(OverlayType.IPV4.getResourceId()));
					infoOverlayList.setAdapter(infoValueListAdapterMap.get(OverlayType.IPV4));
					break;
				case R.id.title_page_ipv6_button:
					infoOverlayTitle.setText(getResources().getText(OverlayType.IPV6.getResourceId()));
					infoOverlayList.setAdapter(infoValueListAdapterMap.get(OverlayType.IPV6));
					break;
				case R.id.title_page_location_button:
					infoOverlayTitle.setText(getResources().getText(R.string.title_screen_info_overlay_location));
					infoOverlayList.setAdapter(infoValueListAdapterMap.get(OverlayType.LOCATION));
					break;
				case R.id.title_page_traffic_button:
				default:
					infoOverlayTitle.setText(getResources().getText(OverlayType.TRAFFIC.getResourceId()));
					infoOverlayList.setAdapter(infoValueListAdapterMap.get(OverlayType.TRAFFIC));
					break;
				}
				//System.out.println("SHOWING INFO OVERLAY");
				if (ipv4Button != null) {
					ipv4Button.setOnClickListener(detailHideOnClickListener);
				}
				if (ipv6Button != null) {
					ipv6Button.setOnClickListener(detailHideOnClickListener);
				}
				if (ipButton != null) {
					ipButton.setOnClickListener(detailHideOnClickListener);
				}
				//antennaView.setOnClickListener(detailHideOnClickListener);
		        if (cpuMemStatsButton != null) {
		        	cpuMemStatsButton.setOnClickListener(detailHideOnClickListener);
		        }
				if (trafficButton != null) {
					trafficButton.setOnClickListener(detailHideOnClickListener);
				}
				if (locationButton != null) {
					locationButton.setOnClickListener(detailHideOnClickListener);
				}
				infoOverlay.setVisibility(View.VISIBLE);
				infoOverlay.bringToFront();
				infoOverlayList.invalidate();
			}
		}
	};
	
	/**
	 * 
	 */
	private final OnClickListener detailHideOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (infoOverlay != null) {
				if ((((InfoArrayAdapter) infoOverlayList.getAdapter()).getOverlayType().getButtonId() == v.getId()) ||
						v.getId() == R.id.info_overlay) {
					hideOverlayAndReenableOnClickListeners();
				}
				else {
					detailShowOnClickListener.onClick(v);
				}
			}			
		}
	};
	
	/**
	 * 
	 */
	OnItemClickListener detailHideOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			hideOverlayAndReenableOnClickListeners();
		}
	};
	
	/**
	 * 
	 */
	private void hideOverlayAndReenableOnClickListeners() {
		infoOverlay.setVisibility(View.GONE);
		if (ipv4Button != null) {
			ipv4Button.setOnClickListener(detailShowOnClickListener);
		}
		if (ipv6Button != null) {
			ipv6Button.setOnClickListener(detailShowOnClickListener);
		}
		if (ipButton != null) {
			ipButton.setOnClickListener(detailShowOnClickListener);
		}
        if (cpuMemStatsButton != null) {
        	cpuMemStatsButton.setOnClickListener(detailShowOnClickListener);
        }
		if (trafficButton != null) {
			trafficButton.setOnClickListener(detailShowOnClickListener);
		}
		if (locationButton != null) {
			locationButton.setOnClickListener(detailShowOnClickListener);
		}
	}
	
	/**
	 * 
	 */
	private OnNetworkInfoChangedListener onNetworkChangedListener = new OnNetworkInfoChangedListener() {

		@Override
		public void onChange(InfoFlagEnum infoFlag, Object newValue) {

			switch (infoFlag) {
			case NETWORK_CONNECTION_CHANGED:
				if ((Boolean) newValue) {
					if (startButton != null && startButtonText != null) {
						startButton.setAlpha(1f);
						startButton.setEnabled(true);
						startButtonText.setAlpha(1f);
					}
				}
				else {
					if (startButton != null && startButtonText != null) {
						startButton.setAlpha(0.3f);
						startButtonText.setAlpha(0.3f);
						startButton.setEnabled(false);
					}
				}
				break;
			default:
				if (infoCollector != null) {
					if (infoFlag == InfoFlagEnum.PRIVATE_IPV4_CHANGED || infoFlag == InfoFlagEnum.PRIVATE_IPV6_CHANGED) {
						if (NetworkInfoCollector.getInstance().hasConnectionFromAndroidApi()) {
							infoCollector.dispatchInfoChangedEvent(InfoCollectorType.SIGNAL, 0, infoCollector.getSignal());
							infoCollector.dispatchInfoChangedEvent(InfoCollectorType.SIGNAL_RSRQ, 0, infoCollector.getSignalRsrq());
						}
						else {
							infoCollector.setSignal(Integer.MIN_VALUE);
							infoCollector.setSignalRsrq(null);
						}
					}

					infoCollector.dispatchInfoChangedEvent(InfoCollectorType.IPV4, infoCollector.getIpv4(), newValue);
					infoCollector.dispatchInfoChangedEvent(InfoCollectorType.IPV6, infoCollector.getIpv6(), newValue);
				}						
			}
		}
    	
    };

	protected String getGeolocationString(final Location location) {
		String locationString = "";
		if (location != null || infoCollector.getLocation() != null) {
			locationString = Helperfunctions.getLocationString(getActivity(), getResources(), location == null ? infoCollector.getLocation() : location, 0);
		}
		else {
			locationString = getResources().getString(R.string.not_available);
		}

		return locationString;
	}

	/**
	 * 
	 * @author lb
	 *
	 */
	private class InfoArrayAdapter extends ArrayAdapter<InfoOverlayEnum> {

		private List<InfoOverlayEnum> infoList;
		private final Activity context;
		private final OverlayType overlayType;
		
		class ViewHolder {
			 public TextView name;
			 public TextView value;
		}
		
		public InfoArrayAdapter(Activity context, OverlayType overlayType, InfoOverlayEnum... infoArray) {
			super(context, R.layout.test_result_detail_item);
			this.context = context;
			this.overlayType = overlayType;
			this.infoList = new ArrayList<RMBTMainMenuFragment.InfoOverlayEnum>();
			for (InfoOverlayEnum e : infoArray) {
				infoList.add(e);
			}
		}
		
		public OverlayType getOverlayType() {
			return overlayType;
		}
		
		public void removeElement(InfoOverlayEnum e) {
			if (this.infoList.contains(e)) {
				//System.out.println("removing element: " + e);
				this.infoList.remove(e);					
				notifyDataSetChanged();
			}
		}
		
		public void addElement(InfoOverlayEnum e) {
			addElement(e, Integer.MAX_VALUE);
		}
		
		public void addElement(InfoOverlayEnum e, int index) {
			if (!this.infoList.contains(e)) {
				//System.out.println("adding element: " + e);
				if (index > this.infoList.size()) {
					this.infoList.add(e);
				}
				else {
					this.infoList.add(index, e);	
				}
				notifyDataSetChanged();
			}
		}
		
		@Override
		public int getCount() {
			return (infoList != null ? infoList.size() : 0);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		    View rowView = convertView;
		    // reuse views
		    if (rowView == null) {
			      LayoutInflater inflater = context.getLayoutInflater();
			      rowView = inflater.inflate(R.layout.info_overlay_detail_item, null);
			      // configure view holder
			      ViewHolder viewHolder = new ViewHolder();
			      viewHolder.name = (TextView) rowView.findViewById(R.id.name);
			      viewHolder.value = (TextView) rowView.findViewById(R.id.value);
			      rowView.setTag(viewHolder);
		    }
		    
		    // fill data
		    ViewHolder holder = (ViewHolder) rowView.getTag();
		    holder.name.setText(infoList.get(position).getTitle(context));
		    switch (infoList.get(position)) {
		    case DL_TRAFFIC:
		    	holder.value.setText(speedFormat.format(((double)interfaceTrafficGatherer.getRxRate() / 125000D)));
		    	break;
		    case UL_TRAFFIC:
		    	holder.value.setText(speedFormat.format(((double)interfaceTrafficGatherer.getTxRate() / 125000D)));
		    	break;
		    case LOCATION:
	    		holder.value.setText(getGeolocationString(null));
		    	break;
		    case LOCATION_ACCURACY:
		    	String locationString = "";
		    	if (infoCollector.getLocation() != null) { 
		    		final int satellites;
		    		if (infoCollector.getLocation().getExtras() != null)
		    		    satellites = infoCollector.getLocation().getExtras().getInt("satellites");
		    		else
		    		    satellites = 0;
                    locationString = Helperfunctions.convertLocationAccuracy(getResources(), 
		    				infoCollector.getLocation().hasAccuracy(), 
		    				infoCollector.getLocation().getAccuracy(),
		    				satellites);
		    	}
	    		holder.value.setText(locationString);		    	
		    	break;
		    case LOCATION_AGE:
		    	locationString = "";
		    	if (infoCollector.getLocation() != null) {
		    		locationString = Helperfunctions.convertLocationTime(infoCollector.getLocation().getTime());
		    	}
	    		holder.value.setText(locationString);		    	
		    	break;
		    case LOCATION_SOURCE:
		    	locationString = "";
		    	if (infoCollector.getLocation() != null) {
		    		locationString = Helperfunctions.convertLocationProvider(getResources(), infoCollector.getLocation().getProvider());
		    	}
	    		holder.value.setText(locationString);
		    	break;
		    case LOCATION_ALTITUDE:
		    	locationString = "";
		    	if (infoCollector.getLocation() != null) {
		    		if (infoCollector.getLocation().hasAltitude()) {
		    			locationString = Helperfunctions.convertLocationAltitude(getResources(), 
		    					infoCollector.getLocation().hasAltitude(), infoCollector.getLocation().getAltitude());
		    		}
		    		else {
		    			locationString = getActivity().getString(R.string.not_available);
		    		}
		    	}
	    		holder.value.setText(locationString);
		    	break;		    	
		    case IPV4:
		    	NetworkInfoCollector netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
		    	if (netInfo != null && netInfo.getPrivateIpv4() != null) {
			    	holder.value.setText(netInfo.getPrivateIpv4().getHostAddress());
		    	}
		    	else {
		    		holder.name.setText(getResources().getString(R.string.title_screen_ipv4));
		    		holder.value.setText(getResources().getString(R.string.not_available));
		    	}
		    	break;
		    case IPV6:
		    	netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
		    	if (netInfo != null && netInfo.getPrivateIpv6() != null) {
			    	holder.value.setText(netInfo.getPrivateIpv6String());
		    	}
		    	else {
		    		holder.name.setText(getResources().getString(R.string.title_screen_ipv6));
		    		holder.value.setText(getResources().getString(R.string.not_available));
		    	}
		    	break;
		    case IPV4_PUB:
		    	netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
		    	if (netInfo != null && netInfo.getPublicIpv4() != null) {
			    	holder.value.setText(netInfo.getPublicIpv4());
		    	}
		    	else {
		    		holder.value.setText(getResources().getString(R.string.not_available));
		    	}
		    	break;
		    case IPV6_PUB:
		    	netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
		    	if (netInfo != null && netInfo.getPublicIpv6() != null) {
			    	holder.value.setText(netInfo.getPublicIpv6());
		    	}
		    	else {
		    		holder.value.setText(getResources().getString(R.string.not_available));
		    	}
		    	break;
		    case CAPTIVE_PORTAL_STATUS:
		    	netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
		    	holder.value.setText(netInfo.getCaptivePortalStatus().getTitle(context));
		    	break;
		    case CONTROL_SERVER_CONNECTION:
		    	netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
		    	holder.value.setText("" + (netInfo != null ? netInfo.hasIpFromControlServer() : false));
		    	break;
		    case IS_LINK_LOCAL6:
		    	netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
		    	if (netInfo != null && netInfo.getPrivateIpv6() != null) {
		    		holder.value.setText("" + netInfo.getPrivateIpv6().isLinkLocalAddress());
		    	}
		    	break;
		    case IS_LOOPBACK6:
		    	netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
		    	if (netInfo != null && netInfo.getPrivateIpv6() != null) {
		    		holder.value.setText("" + netInfo.getPrivateIpv6().isLoopbackAddress());
		    	}
		    	break;
		    case IS_LOOPBACK4:
		    	netInfo = ((RMBTMainActivity) getActivity()).getNetworkInfoCollector();
		    	if (netInfo != null && netInfo.getPrivateIpv4() != null) {
		    		holder.value.setText("" + netInfo.getPrivateIpv4().isLoopbackAddress());
		    	}
		    	break;
		    case CPU_CORES:
		    	if (cpuStat != null) {
		    		holder.value.setText("" + cpuStat.getLastCpuUsage().getNumCores());
		    	}
		    	break;
		    case CPU_USAGE:
	    		holder.value.setText("" + percentFormat.format(infoCollector.getCpuUsage()) + "%");
		    	break;
		    case MEM_USAGE:
	    		holder.value.setText("" + percentFormat.format(infoCollector.getMemUsage()) + "%");
		    	break;
		    case MEM_FREE:
		    	if (memInfo != null) {
		    		holder.value.setText("" + memInfo.getFreeMem() + " kB");
		    	}
		    	break;
		    case MEM_TOTAL:
		    	if (memInfo != null) {
		    		holder.value.setText("" + memInfo.getTotalMem() + " kB");
		    	}
		    	break;
		    }
		    return rowView;
		    
		}
	}
	
	public static enum InfoOverlayEnum {
		IPV4(R.string.title_screen_info_overlay_ipv4_private),
		IS_LOOPBACK4(R.string.title_screen_info_overlay_is_loopback),
		IPV6(R.string.title_screen_info_overlay_ipv6_private),
		IS_LOOPBACK6(R.string.title_screen_info_overlay_is_loopback),
		IS_LINK_LOCAL6(R.string.title_screen_info_overlay_is_link_local),
		IPV4_PUB(R.string.title_screen_info_overlay_ipv4_public),
		IPV6_PUB(R.string.title_screen_info_overlay_ipv6_public), 
		UL_TRAFFIC(R.string.title_screen_info_overlay_ul_traffic), 
		DL_TRAFFIC(R.string.title_screen_info_overlay_dl_traffic), 
		CONTROL_SERVER_CONNECTION(R.string.title_screen_info_overlay_control_server_conn),
		CAPTIVE_PORTAL_STATUS(R.string.title_screen_info_overlay_captive_portal_status),
		LOCATION_ACCURACY(R.string.title_screen_info_overlay_location_accuracy),
		LOCATION_AGE(R.string.title_screen_info_overlay_location_age),
		LOCATION_SOURCE(R.string.title_screen_info_overlay_location_source),
		LOCATION_ALTITUDE(R.string.title_screen_info_overlay_location_altitude),
		LOCATION(R.string.title_screen_info_overlay_location_position),
		CPU_USAGE(R.string.title_screen_info_overlay_cpu_usage),
		CPU_CORES(R.string.title_screen_info_overlay_cpu_cores),
		MEM_USAGE(R.string.title_screen_info_overlay_mem_usage),
		MEM_FREE(R.string.title_screen_info_overlay_mem_free),
		MEM_TOTAL(R.string.title_screen_info_overlay_mem_total);
		
		protected final int resourceId;
		protected final String title;
		
		private InfoOverlayEnum(final String title) {
			this.title = title;
			this.resourceId = -1;
		}
		
		private InfoOverlayEnum(final int resourceId) {
			this.resourceId = resourceId;
			this.title = null;
		}
		
		public int getResourceId() {
			return this.resourceId;
		}
		
		public String getTitle(Context context) {
			if (title != null) {
				return title;
			}
			
			return context.getString(resourceId);
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean onBackPressed() {
		int screenSize = getResources().getConfiguration().screenLayout &
		        Configuration.SCREENLAYOUT_SIZE_MASK;

		if (infoOverlay.getVisibility() == View.VISIBLE && screenSize < Configuration.SCREENLAYOUT_SIZE_LARGE) {
			infoOverlay.setVisibility(View.GONE);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (antennaView != null && antennaView.getTag() != null) {
			outState.putInt(BUNDLE_INFO_LAST_ANTENNA_IMAGE, (Integer) antennaView.getTag());
		}
	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	LayoutInflater inflater = LayoutInflater.from(getActivity());
    	populateViewForOrientation(inflater, (ViewGroup) getView());
    }

    /**
     * 
     * @param inflater
     * @param view
     */
	private void populateViewForOrientation(LayoutInflater inflater, ViewGroup view) {
		int antennaResId = Integer.MIN_VALUE;
		if (antennaView != null && antennaView.getTag() != null) {
			antennaResId = (Integer) antennaView.getTag();
		}
		
		view.removeAllViewsInLayout();
        View v = inflater.inflate(R.layout.title_screen, view);
        
        createView(v, inflater, null);
        
        if (antennaResId != Integer.MIN_VALUE) {
        	antennaView.setImageResource(antennaResId);
        	antennaView.setVisibility(View.VISIBLE);
        }
        
        //restore all information now:
        infoCollector.refresh();
        infoCollector.dispatchInfoChangedEvent(InfoCollectorType.DL_TRAFFIC, null, TrafficClassificationEnum.classify(interfaceTrafficGatherer.getRxRate()));
        infoCollector.dispatchInfoChangedEvent(InfoCollectorType.UL_TRAFFIC, null, TrafficClassificationEnum.classify(interfaceTrafficGatherer.getTxRate()));
	}
	
	/**
	 * 
	 * @return
	 */
	public InformationCollector getInformationCollector() {
		return informationCollector;
	}

	public void setViewVisibility(View view, int visibility) {
		if (view != null) {
			view.setVisibility(visibility);
		}
	}
	
	public void setViewText(TextView view, String text) {
		setViewText(view, text, null);
	}

	public void setViewTextColorResource(TextView view, int colorResId) {
		if (view != null) {
			view.setTextColor(getActivity().getResources().getColor(colorResId));
		}
	}

	public void setViewText(TextView view, String text, TextView alternativeView) {
		if (view != null) {
			view.setText(text);
		}
		else if (alternativeView != null) {
			alternativeView.setText(alternativeView.getText() + " " + text);
		}
	}

	public void setViewImageResource(final ImageView view, final int resourceId) {
		if (view != null) {
			view.setImageResource(resourceId);
		}
	}
}
