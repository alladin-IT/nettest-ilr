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
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.util.net.InterfaceTrafficGatherer;

/**
 * Created by lb on 14.12.16.
 */
public class BgTrafficViewImpl extends RelativeLayout implements BgTrafficView {
    private TextView[] inTrafficIndicatorView;
    private TextView[] outTrafficIndicatorView;

    public BgTrafficViewImpl(Context context) {
        super(context);
        init();
    }

    public BgTrafficViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BgTrafficViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        inflate(getContext(), R.layout.nerdmode_bg_traffic, this);
        inTrafficIndicatorView = new TextView[3];
        outTrafficIndicatorView = new TextView[3];
        for (int i = 0; i < 3; i++) {
            //Log.d("BGTRAFFICVIEW", "fetching id for: " + ("nerdmode_traffic_in_arrow_" + i) + ", in package: " + getContext().getPackageName());
            final int inId = this.getResources().getIdentifier("nerdmode_traffic_in_arrow_" + i, "id", getContext().getPackageName());
            final int outId = this.getResources().getIdentifier("nerdmode_traffic_out_arrow_" + i, "id", getContext().getPackageName());
            //Log.d("BGTRAFFICVIEW", "result, IN: " + inId + ", OUT: " + outId);
            inTrafficIndicatorView[i] = (TextView) findViewById(inId);
            outTrafficIndicatorView[i] = (TextView) findViewById(outId);
        }
    }


    @Override
    public void updateBgTrafficIn(InterfaceTrafficGatherer.TrafficClassificationEnum value) {
        for (int i = 0; i < 3; i++) {
            inTrafficIndicatorView[i].setTextColor(getResources().getColor(R.color.calm_gauge_background));
        }
        switch(value) {
            case HIGH:
                inTrafficIndicatorView[2].setTextColor(getResources().getColor(R.color.calm_icon_color));
            case MID:
                inTrafficIndicatorView[1].setTextColor(getResources().getColor(R.color.calm_icon_color));
            case LOW:
                inTrafficIndicatorView[0].setTextColor(getResources().getColor(R.color.calm_icon_color));
            default:
                break;
        }
    }

    @Override
    public void updateBgTrafficOut(InterfaceTrafficGatherer.TrafficClassificationEnum value) {
        for (int i = 0; i < 3; i++) {
            outTrafficIndicatorView[i].setTextColor(getResources().getColor(R.color.calm_gauge_background));
        }
        switch(value) {
            case HIGH:
                outTrafficIndicatorView[2].setTextColor(getResources().getColor(R.color.calm_icon_color));
            case MID:
                outTrafficIndicatorView[1].setTextColor(getResources().getColor(R.color.calm_icon_color));
            case LOW:
                outTrafficIndicatorView[0].setTextColor(getResources().getColor(R.color.calm_icon_color));
            default:
                break;
        }
    }
}
