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

import android.app.ActionBar;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.transition.Fade;
import android.view.View;
import android.view.animation.AnimationUtils;

import at.alladin.openrmbt.android.R;

/**
 * Created by lb on 06.06.16.
 */
public class SplashActivity extends FragmentActivity {


    public final static int SPLASH_FADE_MS = 8000;

    private final int SPLASH_WAIT_MS = 1250;

    final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        handler.postDelayed(startMainActivityRunnable, SPLASH_WAIT_MS);
        applyLayout();
    }

    private void applyLayout() {
        View outerRing = findViewById(R.id.title_page_outer_ring);
        if (outerRing != null) {
            outerRing.getBackground().setLevel(5000);
            outerRing.setRotation(225);
        }
    }

    final Runnable startMainActivityRunnable = new Runnable() {
        @Override
        public void run() {
            final Intent startMainActivityIntent = new Intent(SplashActivity.this, RMBTMainActivity.class);
            //transitions on API < 21 not possible with Material Design
            //see https://developer.android.com/training/material/animations.html#Transitions

            if (Build.VERSION.SDK_INT >= 21) {
                final Fade fade = new Fade();
                fade.setDuration(SPLASH_FADE_MS);
                getWindow().setExitTransition(fade);

                startActivity(startMainActivityIntent, ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this).toBundle());
            }
            else {
                startActivity(startMainActivityIntent);
            }

            finish();
        }
    };
}
