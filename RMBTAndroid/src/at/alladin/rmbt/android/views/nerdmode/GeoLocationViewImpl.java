/*******************************************************************************
 * Copyright 2016-2017 alladin-IT GmbH
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

package at.alladin.rmbt.android.views.nerdmode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.main.RMBTMainActivity;

/**
 * Created by lb on 14.12.16.
 */
public class GeoLocationViewImpl extends RelativeLayout implements GeoLocationView {
    private TextView gpsTextView;
    private TextView locationTextView;
    private GeoLocationViewState gpsState = GeoLocationViewState.TURNED_OFF;

    public GeoLocationViewImpl(Context context) {
        super(context);
        init();
    }

    public GeoLocationViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GeoLocationViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        inflate(getContext(), R.layout.nerdmode_gps, this);
        setVisibility(View.GONE);
        gpsTextView = (TextView) findViewById(R.id.nerdmode_gps_image);
        locationTextView = (TextView) findViewById(R.id.nerdmode_gps_location_text);
    }

    @Override
    public void updateGeoLocationViewState(GeoLocationViewState state) {
        switch(state) {
            case TURNED_OFF:
                if (getVisibility() == View.VISIBLE) {
                    setVisibility(View.GONE);
                }
                break;
            case ON_GPS:
            case ON_WIFI:
            default:
                if ((getVisibility() == View.GONE || getVisibility() == View.INVISIBLE) && ((RMBTMainActivity) getContext()).getLastTestUuid()==null) {
                    setVisibility(View.VISIBLE);
                }
                if (gpsTextView != null) {

                }
                break;
        }

        this.gpsState = state;
    }

    @Override
    public GeoLocationViewState getGeoLocationViewState() {
        return gpsState;
    }

    @Override
    public void updateGeoLocationString(String gpsString) {
        if (locationTextView != null) {
            locationTextView.setText(gpsString);
        }
    }

    @Override
    public String getGeoLocationString() {
        return locationTextView != null ? locationTextView.getText().toString() : null;
    }
}
