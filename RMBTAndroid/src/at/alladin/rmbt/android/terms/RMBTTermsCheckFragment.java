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

package at.alladin.rmbt.android.terms;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import at.alladin.openrmbt.android.BuildConfig;
import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.android.terms.RMBTCheckFragment.CheckType;
import at.alladin.rmbt.android.util.ConfigHelper;

import at.alladin.openrmbt.android.R;

public class RMBTTermsCheckFragment extends Fragment
{
    private static final String TAG = "RMBTTermsCheckFragment";
    
    private boolean firstTime = true;
    
    private View view;
    
    private final CheckType followedByType;
    
    public static RMBTTermsCheckFragment getInstance(final CheckType followedBy) {
    	return new RMBTTermsCheckFragment(followedBy);
    }
    
    private RMBTTermsCheckFragment(final CheckType followedBy) {
    	this.followedByType = followedBy;
	}
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.terms_check, container, false);
        
        final WebView tcWvl = (WebView) view.findViewById(R.id.termsCheckWebViewLong);
        final String localizedTCFile = "terms_conditions_long_" + Locale.getDefault().getLanguage() + ".html";
        boolean hasLocalizedTCFile = false;
        try {
            Log.d(TAG, "looking for TC file: " + localizedTCFile);
            hasLocalizedTCFile = Arrays.asList(getResources().getAssets().list("")).contains(localizedTCFile);
            Log.d(TAG, "localized TC exists: " + hasLocalizedTCFile);
        }
        catch (final IOException e) {
            e.printStackTrace();
        }

        final CheckBox settingsCheckBox = (CheckBox) view.findViewById(R.id.terms_to_settings_checkbox);
        if (BuildConfig.TERMS_SWITCH_TO_SETTINGS_AFTER_ACCEPT && settingsCheckBox != null) {
            settingsCheckBox.setVisibility(View.VISIBLE);
        }

        tcWvl.loadUrl(hasLocalizedTCFile ? "file:///android_asset/" + localizedTCFile : "file:///android_asset/terms_conditions_long.html");
        
        final Activity activity = getActivity();
        if (! (activity instanceof RMBTMainActivity)) {
            firstTime = false;
        }

        if (! firstTime) {
            view.findViewById(R.id.termsButtonDecline).setVisibility(View.GONE);
        }
        else {
            if (!BuildConfig.TERMS_SHOW_TITLE) {
                final TextView title = (TextView) view.findViewById(R.id.terms_check_title);
                if (title != null) {
                    title.setVisibility(View.GONE);
                }
            }
        }
        
        final Button buttonTermsAccept = (Button) view.findViewById(R.id.termsAcceptButton);
        buttonTermsAccept.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                ConfigHelper.setTCAccepted(getActivity(), true);
                if (firstTime)
                {
                    ((RMBTMainActivity)getActivity()).checkSettings(true);
                    final boolean wasNDTTermsNecessary = ((RMBTMainActivity)getActivity()).showChecksIfNecessary();
                    if (! wasNDTTermsNecessary) {
                        final CheckBox checkSettings = (CheckBox) view.findViewById(R.id.terms_to_settings_checkbox);
                        ((RMBTMainActivity) activity).initApp(false, BuildConfig.TERMS_SWITCH_TO_SETTINGS_AFTER_ACCEPT && checkSettings != null ? checkSettings.isChecked() : false);
                    }
                }
                else if (followedByType != null) {
                	switch (followedByType) {
                	case INFORMATION_COMMISSIONER:
                        ((RMBTTermsActivity)getActivity()).showIcCheck();
                		break;
                	case NDT:
                        ((RMBTTermsActivity)getActivity()).showNdtCheck();
                		break;
                	}
                }
            }
        });
        
        final Button buttonTermsDecline = (Button) view.findViewById(R.id.termsDeclineButton);
        buttonTermsDecline.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                onBackPressed();
            }
        });
        
        return view;
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        final Activity activity = getActivity();
        final boolean tcAccepted = ConfigHelper.isTCAccepted(activity);
        if (tcAccepted)
        {
            final TextView buttonTermsAccept = (TextView) view.findViewById(R.id.termsAcceptButton);
            buttonTermsAccept.setText(R.string.terms_accept_button_continue);
            view.findViewById(R.id.termsAcceptText).setVisibility(View.GONE);
        }
    }

    public boolean onBackPressed()
    {
        // user has declined t+c!
        
        ConfigHelper.setTCAccepted(getActivity(), false);
        ConfigHelper.setUUID(getActivity(), "");
        getActivity().finish();
        return true;
    }
}
