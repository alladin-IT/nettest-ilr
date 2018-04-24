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

package at.alladin.rmbt.android.map;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import at.alladin.openrmbt.android.BuildConfig;
import at.alladin.openrmbt.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import at.alladin.rmbt.android.main.AppConstants;
import at.alladin.rmbt.android.main.FeatureConfig;
import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.android.map.MapProperties.MapOverlay;
import at.alladin.rmbt.android.map.overlay.RMBTBalloonOverlayItem;
import at.alladin.rmbt.android.map.overlay.RMBTBalloonOverlayView;
import at.alladin.rmbt.android.util.CheckMarker;
import at.alladin.rmbt.android.util.ConfigHelper;
import at.alladin.rmbt.android.util.EndTaskListener;
import at.alladin.rmbt.android.util.GeoLocation;

import static com.google.android.gms.internal.a.U;

public class RMBTMapFragment extends SupportMapFragment implements OnClickListener, OnCameraChangeListener, OnMapClickListener, InfoWindowAdapter, OnInfoWindowClickListener, OnMyLocationChangeListener, OnMarkerClickListener
{
//    private static final String DEBUG_TAG = "RMBTMapFragment";
    
	public final static String OPTION_SHOW_INFO_TOAST = "show_info_toast";
	public final static String OPTION_ENABLE_ALL_GESTURES = "enable_all_gestures";
	public final static String OPTION_ENABLE_CONTROL_BUTTONS = "enable_control_buttons";
	public final static String OPTION_ENABLE_OVERLAY = "enable_overlay";
	
    private GoogleMap gMap;
    
    private TileOverlay heatmapOverlay;
    private TileOverlay pointsOverlay;
    private TileOverlay shapesOverlay;
    
    private Marker myLocationMarker;
    private MyGeoLocation geoLocation;
    private BitmapDescriptor markerIconBitmapDescriptor;
    
    private boolean firstStart = true;

    private RMBTMapFragmentOptions options = new RMBTMapFragmentOptions();
    
    private OnClickListener additionalMapClickListener;
    
    public class RMBTMapFragmentOptions {
    	private boolean showInfoToast = true;
    	private boolean enableAllGestures = true;
    	private boolean enableControlButtons = true;
    	private boolean enableOverlay = true;
    	
		public boolean isShowInfoToast() {
			return showInfoToast;
		}
		public void setShowInfoToast(boolean showInfoToast) {
			this.showInfoToast = showInfoToast;
		}
		public boolean isEnableAllGestures() {
			return enableAllGestures;
		}
		public void setEnableAllGestures(boolean enableAllGestures) {
			this.enableAllGestures = enableAllGestures;
		}
		public boolean isEnableControlButtons() {
			return enableControlButtons;
		}
		public void setEnableControlButtons(boolean enableControlButtons) {
			this.enableControlButtons = enableControlButtons;
		}
		public boolean isEnableOverlay() {
			return enableOverlay;
		}
		public void setEnableOverlay(boolean enableOverlay) {
			this.enableOverlay = enableOverlay;
		}
    }
    
    private class MyGeoLocation extends GeoLocation
    {
        public MyGeoLocation(Context context)
        {
            super(context, ConfigHelper.isGPS(context));
        }
        
        @Override
        public void onLocationChanged(Location location)
        {
            if (onLocationChangedListener != null)
                onLocationChangedListener.onLocationChanged(location);
        }
    }
    
    private OnLocationChangedListener onLocationChangedListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        geoLocation = new MyGeoLocation(getActivity());
        
        final Bundle bundle = getArguments();
        
        if (bundle != null) {                	
            if (bundle.containsKey(OPTION_ENABLE_ALL_GESTURES)) {
            	options.setEnableAllGestures(bundle.getBoolean(OPTION_ENABLE_ALL_GESTURES));
            }
            if (bundle.containsKey(OPTION_SHOW_INFO_TOAST)) {
            	options.setShowInfoToast(bundle.getBoolean(OPTION_SHOW_INFO_TOAST));
            }
            if (bundle.containsKey(OPTION_ENABLE_CONTROL_BUTTONS)) {
            	options.setEnableControlButtons(bundle.getBoolean(OPTION_ENABLE_CONTROL_BUTTONS));
            }
            if (bundle.containsKey(OPTION_ENABLE_OVERLAY)) {
            	options.setEnableOverlay(bundle.getBoolean(OPTION_ENABLE_OVERLAY));
            }
        }
    }
    
    @Override
    public void onStart()
    {
        super.onStart();
        geoLocation.start();
        
        gMap = getMap();
        if (gMap != null)
        {
            if (firstStart)
            {
                final Bundle bundle = getArguments();
                
                firstStart = false;
                
                final UiSettings uiSettings = gMap.getUiSettings();
                uiSettings.setZoomControlsEnabled(false); // options.isEnableAllGestures());
                uiSettings.setMyLocationButtonEnabled(false);
                uiSettings.setCompassEnabled(false);
                uiSettings.setRotateGesturesEnabled(false);
                uiSettings.setScrollGesturesEnabled(options.isEnableAllGestures());
                
                gMap.setTrafficEnabled(false);
                gMap.setIndoorEnabled(false);
                
                LatLng latLng = MapProperties.DEFAULT_MAP_CENTER;
                float zoom = (float) BuildConfig.MAP_DEFAULT_ZOOM_FACTOR;
                
                final Location lastKnownLocation = geoLocation.getLastKnownLocation();
                if (lastKnownLocation != null)
                {
                    latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    zoom = MapProperties.DEFAULT_MAP_ZOOM_LOCATION;
                }

                if (bundle != null)
                {
                    final LatLng initialCenter = bundle.getParcelable("initialCenter");
                    if (initialCenter != null)
                    {
                        latLng = initialCenter;
                        zoom = MapProperties.POINT_MAP_ZOOM;
                        
                        if (balloonMarker != null)
                            balloonMarker.remove();
                        balloonMarker = gMap.addMarker(
                            new MarkerOptions().
                            position(initialCenter).
                            draggable(false).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            );
                    }
                }
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
                
                gMap.setLocationSource(new LocationSource()
                {
                    
                    @Override
                    public void deactivate()
                    {
                        onLocationChangedListener = null;
                    }
                    
                    @Override
                    public void activate(OnLocationChangedListener listener)
                    {
                        onLocationChangedListener = listener;
                    }
                });
                
                gMap.setMyLocationEnabled(true);
                gMap.setOnMyLocationChangeListener(this);
                gMap.setOnMarkerClickListener(this);
                gMap.setOnMapClickListener(this);
                gMap.setInfoWindowAdapter(this);
                gMap.setOnInfoWindowClickListener(this);
                
                
                final Location myLocation = gMap.getMyLocation();
                if (myLocation != null)
                    onMyLocationChange(myLocation);
            }
            
            
            final RMBTMainActivity activity = (RMBTMainActivity) getActivity();
            gMap.setMapType(activity.getMapTypeSatellite() ? GoogleMap.MAP_TYPE_SATELLITE : GoogleMap.MAP_TYPE_NORMAL);
            
            final Map<String, String> mapOptions = ((MapProperties) getActivity()).getCurrentMapOptions();

            boolean needHeatmapOverlay = false;
            boolean needPointsOverlay = false;

            final MapOverlay mapOverlayType = activity.getMapOverlayType();
            System.out.println("addition parameters for " + mapOverlayType + " = " + mapOverlayType.getAdditionalParameters());
            mapOptions.putAll(mapOverlayType.getAdditionalParameters());
            
            final String mapType = activity.getCurrentMainMapType();
            if (mapOverlayType == MapOverlay.AUTO)
            {
                gMap.setOnCameraChangeListener(this);
                needPointsOverlay = true;
                if (mapType != null && mapType.equals("browser")) {
                	needHeatmapOverlay = false;
                }
                else {
                    needHeatmapOverlay = true;
                }
            }
            else
            {
                gMap.setOnCameraChangeListener(null);
                switch (mapOverlayType) {
                case HEATMAP:
                	needPointsOverlay = false;
                    needHeatmapOverlay = true;
                	break;

                case POINTS:
                    needPointsOverlay = true;
                    needHeatmapOverlay = false;
                	break;

                default:
                	break;
                }
            }
            
            if (!options.isEnableOverlay()) {
            	needHeatmapOverlay = false;
            	needPointsOverlay = false;
            }
              
            final String protocol = ConfigHelper.isMapSeverSSL(getActivity()) ? "https" : "http";
            final String host = ConfigHelper.getMapServerName(getActivity());
            final int port = ConfigHelper.getMapServerPort(getActivity());
            
            if (needHeatmapOverlay)
            {
                final RMBTTileSourceProvider heatmapProvider = new RMBTTileSourceProvider(protocol, host, port, MapProperties.TILE_SIZE);
                mapOptions.put("overlay_type", MapOverlay.HEATMAP.toString().toLowerCase(Locale.US));
                mapOptions.remove("highlight_uuid");
                heatmapProvider.setOptionMap(mapOptions);
                heatmapProvider.setPath(MapOverlay.HEATMAP.getPath());
                heatmapOverlay = gMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapProvider).zIndex(100000000));
            }

            if (needPointsOverlay)
            {
                final RMBTTileSourceProvider pointsProvider = new RMBTTileSourceProvider(protocol, host, port, MapProperties.TILE_SIZE * 2);
                mapOptions.put("overlay_type", MapOverlay.POINTS.toString().toLowerCase(Locale.US));
                mapOptions.put("highlight_uuid", ConfigHelper.getUUID(getActivity()));
                pointsProvider.setOptionMap(mapOptions);
                pointsProvider.setPath(MapOverlay.POINTS.getPath());
                pointsOverlay = gMap.addTileOverlay(new TileOverlayOptions().tileProvider(pointsProvider).zIndex(200000000));
                
                if ((mapOverlayType == MapOverlay.AUTO) || (mapOverlayType.isShapeLayer()))
                    onCameraChange(gMap.getCameraPosition());
            }
        }
        
        //gMap.getUiSettings().setAllGesturesEnabled(options.isEnableAllGestures());
        //gMap.getUiSettings().setCompassEnabled(false);
    }
    
    @Override
    public void onMyLocationChange(Location location)
    {
        if (myLocationMarker != null)
            myLocationMarker.remove(); 
        
        if (markerIconBitmapDescriptor == null)
            markerIconBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.bg_trans_light);

        myLocationMarker = gMap.addMarker(new MarkerOptions() 
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .icon(markerIconBitmapDescriptor)); 
    }
    
    @Override
    public boolean onMarkerClick(Marker marker)
    {
        if (myLocationMarker != null && marker.equals(myLocationMarker))
        {
            // redirect to map click
            onMapClick(marker.getPosition());
            return true;
        }
        return false;
    }

    
    @Override
    public void onStop()
    {
        super.onStop();
        cancelCheckMarker();
        geoLocation.stop();
        if (heatmapOverlay != null)
        {
            heatmapOverlay.clearTileCache();
            heatmapOverlay.remove();
            heatmapOverlay = null;
        }
        if (pointsOverlay != null)
        {
            pointsOverlay.clearTileCache();
            pointsOverlay.remove();
            pointsOverlay = null;
        }        
        if (shapesOverlay != null)
        {
            shapesOverlay.clearTileCache();
            shapesOverlay.remove();
            shapesOverlay = null;
        }
    }
    
    @Override
    public void onResume()
    {
        super.onResume();

        if (options.showInfoToast) {
            showInfoToast();	
        }
    }
    
    @Override
    public void onPause()
    {
        super.onPause();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.map_google, container, false);
        registerListeners(view);
        
        final int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (errorCode != ConnectionResult.SUCCESS)
        {
            final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), 0);
            errorDialog.show();
            getFragmentManager().popBackStack();
            return view;
        }
        
        View mapView = super.onCreateView(inflater, container, savedInstanceState);
        
        final RelativeLayout mapViewContainer = (RelativeLayout) view.findViewById(R.id.mapViewContainer);
        mapViewContainer.addView(mapView);
        
        
        ProgressBar progessBar = new ProgressBar(getActivity());
        final RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
        progessBar.setLayoutParams(layoutParams);
        progessBar.setVisibility(View.GONE);
        view.addView(progessBar);
        
        
        return view;
    }
    
    private void registerListeners(View view)
    {
        final Button mapChooseButton = (Button) view.findViewById(R.id.mapChooseButton);
        final Button mapFilterButton = (Button) view.findViewById(R.id.mapFilterButton);
        final Button mapLocateButton = (Button) view.findViewById(R.id.mapLocateButton);
        final Button mapHelpButton = (Button) view.findViewById(R.id.mapHelpButton);
        final Button mapInfoButton = (Button) view.findViewById(R.id.mapInfoButton);
        final Button mapZoomInButton = (Button) view.findViewById(R.id.mapZoomInButton);
        final Button mapZoomOutButton = (Button) view.findViewById(R.id.mapZoomOutButton);

        if (options.isEnableControlButtons()) {
            mapChooseButton.setOnClickListener(this);
            mapFilterButton.setOnClickListener(this);
            mapLocateButton.setOnClickListener(this);
            mapHelpButton.setOnClickListener(this);
            mapInfoButton.setOnClickListener(this);
            mapZoomInButton.setOnClickListener(this);
            mapZoomOutButton.setOnClickListener(this);	
        }        
        else {
        	mapChooseButton.setVisibility(View.GONE);
        	mapFilterButton.setVisibility(View.GONE);
        	mapLocateButton.setVisibility(View.GONE);
        	mapHelpButton.setVisibility(View.GONE);
        	mapInfoButton.setVisibility(View.GONE);
        	mapZoomInButton.setVisibility(View.GONE);
        	mapZoomOutButton.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onCameraChange(CameraPosition cp)
    {
        //System.out.println("pointsOverlay: " + pointsOverlay);
        if (pointsOverlay != null)
        {
            //System.out.println("cp zoom: " + cp.zoom +  " >= " + MapProperties.MAP_AUTO_SWITCH_VALUE + ", visibile: " + pointsOverlay.isVisible());
            final boolean automaticShowPoints = cp.zoom >= MapProperties.MAP_AUTO_SWITCH_VALUE;
            if (automaticShowPoints && ! pointsOverlay.isVisible())
                pointsOverlay.setVisible(true);
            else if (! automaticShowPoints && pointsOverlay.isVisible())
                pointsOverlay.setVisible(false);
        }
    }
    
    @Override
    public void onClick(View v)
    {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        final FragmentTransaction ft;
        
        final GoogleMap map = getMap();

        switch (v.getId())
        {

        case R.id.mapChooseButton:

            ft = fm.beginTransaction();
            ft.replace(R.id.fragment_content, new RMBTMapChooseFragment(), "map_choose");
            ft.addToBackStack("map_choose");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();

            break;

        case R.id.mapFilterButton:

            ft = fm.beginTransaction();
            ft.replace(R.id.fragment_content, new RMBTMapFilterFragment(), "map_filter");
            ft.addToBackStack("map_filter");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();

            break;

        case R.id.mapLocateButton:

            if (map != null)
            {
                final Location location = geoLocation.getLastKnownLocation();
                if (location != null)
                {
                    final LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                    gMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
                }
            }
            break;

        case R.id.mapHelpButton:
            ((RMBTMainActivity) getActivity()).showHelp("", false, AppConstants.PAGE_TITLE_HELP); // TODO: put correct
                                                        // help url
            break;

        case R.id.mapInfoButton:
            showInfoToast();
            break;

        case R.id.mapZoomInButton:

            if (map != null)
                map.animateCamera(CameraUpdateFactory.zoomIn());

            break;

        case R.id.mapZoomOutButton:

            if (map != null)
                map.animateCamera(CameraUpdateFactory.zoomOut());

            break;

        default:
            break;
        }

    }

    private void showInfoToast()
    {
        final Map<String, String> currentMapOptionTitles = ((RMBTMainActivity) getActivity())
                .getCurrentMapOptionTitles();
        //If the current information has not yet been loaded, hide the mapInfoButton
        final Button mapInfoButton = (Button) this.getView().findViewById(R.id.mapInfoButton);
        if(currentMapOptionTitles.isEmpty()){
            mapInfoButton.setVisibility(View.INVISIBLE);
        }else{
            mapInfoButton.setVisibility(View.VISIBLE);
        }

        String infoString = "";
        for (final String s : currentMapOptionTitles.values())
        {
            if (infoString.length() > 0)
                infoString += "\n";

            infoString += s;
        }
        if (infoString.length() > 0)
        {
            final Toast toast = Toast.makeText(getActivity(), infoString, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }
    
    private CheckMarker checkMarker;
    private RMBTBalloonOverlayItem balloon;
    private String openTestUUIDURL;
    private Marker balloonMarker;
    private final EndTaskListener<JSONArray> checkMarkerEndTaskListener = new EndTaskListener<JSONArray>()
    {
        @Override
        public void taskEnded(JSONArray result)
        {
            if (! isVisible())
                return;
            final GoogleMap gMap = getMap();
            if (gMap == null || result == null)
                return;
            
            try
            {
                if (result.length() == 0)
                    return;

                final JSONObject resultListItem = result.getJSONObject(0);
                
                final LatLng latLng = new LatLng(resultListItem.getDouble("lat"), resultListItem.getDouble("lon"));
                
                openTestUUIDURL = null;
                final String openDataPrefix = ConfigHelper.getVolatileSetting("url_open_data_prefix");
                if (openDataPrefix != null && openDataPrefix.length() > 0)
                {
                    final String openUUID = resultListItem.optString("open_test_uuid", null);
                    if (openUUID != null && openUUID.length() > 0) {
                        openTestUUIDURL = openDataPrefix +
                                (openDataPrefix.endsWith("/") ? "" : "/") +
                                openUUID + "#noMMenu";
                    }
                }
                
                balloon = new RMBTBalloonOverlayItem(latLng, getResources().getString(R.string.map_balloon_overlay_header), result);
                
                if (balloonMarker != null)
                    balloonMarker.remove();
                
                balloonMarker = gMap.addMarker(new MarkerOptions().
                        position(latLng).
                        draggable(false).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                balloonMarker.showInfoWindow();
                
                
                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                gMap.moveCamera(CameraUpdateFactory.scrollBy(0, getResources().getDisplayMetrics().density * -175));                
            }
            catch (final JSONException e)
            {
                e.printStackTrace();
            }
        }
    };
    
    private void cancelCheckMarker()
    {
        if (checkMarker != null)
            checkMarker.cancel(true);
    }
    
    @Override
    public void onMapClick(LatLng latLng)
    {
    	if (additionalMapClickListener != null) {
    		additionalMapClickListener.onClick(getView());
    	}
    	
    	if (FeatureConfig.USE_OPENDATA) {
    		//show bubble only if opendata is enabled
	    	if (options.isEnableAllGestures() || options.isEnableControlButtons()) {
	    		cancelCheckMarker();
	    		checkMarker = new CheckMarker(getActivity(), latLng.latitude, latLng.longitude,
	    				(int) gMap.getCameraPosition().zoom, 20); // TODO correct params (zoom, size)
	    		checkMarker.setEndTaskListener(checkMarkerEndTaskListener);
	    		checkMarker.execute();
	    	}
    	}
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        if (balloon == null)
            return null;
        
        if (balloonMarker == null || ! balloonMarker.equals(marker))
            return null;
        
//        final FrameLayout view = new FrameLayout(getActivity());
        final RMBTBalloonOverlayView bv = new RMBTBalloonOverlayView(getActivity());
        final View view = bv.setupView(getActivity(), null);
        bv.setBalloonData(balloon, null);
//        view.addView(bv);
        return view;
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        if (balloonMarker == null || ! balloonMarker.equals(marker))
            return;
        
        balloonMarker.hideInfoWindow();

        final JSONArray results = balloon.getResultItems();
        boolean hasMeasurementId = false;
        try {
            for (int i = 0; i < results.length(); i++) {
                final JSONObject result = results.getJSONObject(i);
                if (result.has("measurement_uuid")) {
                    hasMeasurementId = true;
                    final RMBTMainActivity activity = (RMBTMainActivity) getActivity();
                    activity.showResultsFromMap(result.getString("measurement_uuid"));
                    break;
                }
            }
        }
        catch (final JSONException e) {
            e.printStackTrace();
        }

        //if no measurement id was found and an open test URL is available then open it:
        if (openTestUUIDURL != null && !hasMeasurementId)
        {
            Log.d(getTag(), "go to url: " + openTestUUIDURL);
            final RMBTMainActivity activity = (RMBTMainActivity) getActivity();
            activity.showHelp(openTestUUIDURL, false, AppConstants.PAGE_TITLE_MAP);
        }
    }

    /**
     * 
     * @return
     */
	public OnClickListener getAdditionalMapClickListener() {
		return additionalMapClickListener;
	}

	/**
	 * 
	 * @param additionalMapClickListener
	 */
	public void setAdditionalMapClickListener(OnClickListener additionalMapClickListener) {
		this.additionalMapClickListener = additionalMapClickListener;
	}

}
