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

package at.alladin.rmbt.android.views.network;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.openrmbt.android.R;

/**
 * Created by lb on 28.02.17.
 */

public class NetworkInfoViewImpl extends FrameLayout implements NetworkInfoView {

    TextView networkNameTextView;
    TextView networkTypeTextView;
    TextView signalStrengtheTextView;

    public NetworkInfoViewImpl(Context context) {
        super(context);
        init();
    }

    public NetworkInfoViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NetworkInfoViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        inflate(getContext(), R.layout.infocollector_status, this);
        networkNameTextView = (TextView) findViewById(R.id.info_network_name);
        networkTypeTextView = (TextView) findViewById(R.id.info_network_type);
        signalStrengtheTextView = (TextView) findViewById(R.id.info_signal_strength);
    }


    @Override
    public void setNetworkName(String networkName) {
        if (networkNameTextView != null) {
            networkNameTextView.setText(networkName);
        }
    }

    @Override
    public void setSignalStrength(String signalStrength) {
        if (signalStrengtheTextView != null) {
            signalStrengtheTextView.setText(signalStrength);
        }
    }

    @Override
    public void setNetworkType(String networkType) {
        if (networkTypeTextView != null) {
            networkTypeTextView.setText(networkType);
        }
    }

    @Override
    public String getNetworkName() {
        if (networkNameTextView != null) {
            final CharSequence r = networkNameTextView.getText();
            return r != null ? r.toString() : null;
        }
        return null;
    }

    @Override
    public String getSignalStrength() {
        if (signalStrengtheTextView != null) {
            final CharSequence r = signalStrengtheTextView.getText();
            return r != null ? r.toString() : null;
        }
        return null;
    }

    @Override
    public String getNetworkType() {
        if (networkTypeTextView != null) {
            final CharSequence r = networkTypeTextView.getText();
            return r != null ? r.toString() : null;
        }
        return null;
    }
}
