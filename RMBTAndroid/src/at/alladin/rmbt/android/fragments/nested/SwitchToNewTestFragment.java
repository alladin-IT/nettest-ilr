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

package at.alladin.rmbt.android.fragments.nested;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.alladin.openrmbt.android.BuildConfig;
import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.main.AppConstants;
import at.alladin.rmbt.android.main.RMBTMainActivity;

/**
 * Created by lb on 23.06.16.
 */
public class SwitchToNewTestFragment extends Fragment {

    private View rootView;

    private RMBTMainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.switch_to_new_test, null);

        try {
            if (activity.getLastTestUuid() == null) { // || BuildConfig.WORKFLOW_TEST_WORKFLOW == AppConstants.TestWorkflow.ILR) {
                rootView.setVisibility(View.GONE);
            } else {
                rootView.setVisibility(View.VISIBLE);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activity.startTestFromResultsScreen(true, R.anim.slide_out_bottom,
                            R.anim.slide_in_from_top, true);
                }
                catch (final Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (RMBTMainActivity) activity;
    }

    public void setVisibility(final int visibility) {
        this.rootView.setVisibility(visibility);
    }
}
