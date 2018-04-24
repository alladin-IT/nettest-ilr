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

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import at.alladin.openrmbt.android.BuildConfig;
import at.alladin.rmbt.android.main.AppConstants;

/**
 * 
 * @author alladin-IT GmbH
 * 
 */
public interface MapProperties
{
    
    /**
	 * 
	 */
    public static final int TILE_SIZE = 256;
    
    /**
	 * 
	 */
    public static final LatLng DEFAULT_MAP_CENTER = new LatLng(BuildConfig.MAP_DEFAULT_CENTER_LAT, BuildConfig.MAP_DEFAULT_CENTER_LONG);

    public static final float DEFAULT_MAP_ZOOM_LOCATION = 12f;
    
    public static final float POINT_MAP_ZOOM = 14f;
    
    /**
	 * 
	 */
    /* north, east, south, west */
    // 49.5, 17.5, 46.25, 9.25 -> 49.05, 17.25, 46.35, 9.4
    // http://www.openstreetmap.org/?minlon=9.45&minlat=46.355&maxlon=17.20&maxlat=49.00&box=yes
    // public static final BoundingBoxE6 BOUNDING_BOX = new BoundingBoxE6(49,
    // 17.2, 46.355, 9.45);
    
    /**
     * 
     */
    public static final String MARKER_PATH = "/RMBTMapServer/tiles/markers";
    
    /**
     * 
     */
    public static final String MAP_OPTIONS_PATH = "/RMBTMapServer/tiles/info";
    
    /**
	 * 
	 */
    public static final String MAP_SAT_KEY = "_SAT";
    
    /**
	 * 
	 */
    public static final String MAP_SAT_VALUE = "SAT";
    
    /**
	 * 
	 */
    public static final String MAP_NOSAT_VALUE = "NOSAT";
    
    /**
	 * 
	 */
    public static final String MAP_OVERLAY_KEY = "_OVERLAY";
    
    /**
     * 
     */
    public static final int MAP_AUTO_SWITCH_VALUE = 12;

    
    interface ParameterizableMapOverlay {
    	Map<String, String> getAdditionalParameters();
    }
    /**
     * 
     * @author lb
     *
     */
    public enum MapOverlay implements ParameterizableMapOverlay {
    	AUTO,
    	HEATMAP("/RMBTMapServer/tiles/heatmap"),
    	POINTS("/RMBTMapServer/tiles/points");
		/*
    	REGIONS("/RMBTMapServer/tiles/shapes", true) {
    		@Override
    		public Map<String, String> getAdditionalParameters() {
    			final Map<String, String> map = super.getAdditionalParameters();
    			map.put("shapetype", "regions");
    			return map;
    		}
	   	};
	   	*/

    	
    	protected final String path;
    	protected final boolean isShapeLayer;
    	
    	MapOverlay() {
    		this("", false);
		}

    	MapOverlay(final String path) {
    		this(path, false);
		}

    	MapOverlay(final String path, final boolean isShapeLayer) {
    		this.path = path;
    		this.isShapeLayer = isShapeLayer;
		}

		public String getPath() {
			return path;
		}
		
		public boolean isShapeLayer() {
			return isShapeLayer;
		}
		
		@Override
		public Map<String, String> getAdditionalParameters() {
			return new HashMap<String, String>();
		}
		
		public static MapOverlay getByPath(final String path) {
			for (final MapOverlay o : MapOverlay.values()) {
				if (o.getPath().equals(path)) {
					return o;
				}
			}
			
			return null;
		}
    }
    
    /**
     * 
     */
    public static final int POINT_DIAMETER = 12;
    
    /**
     * 
     */
    public static final double TAB_DIAMETER_FACTOR = 2;

    public Map<String, String> getCurrentMapOptions();
}
