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

package at.alladin.rmbt.android.preferences;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.ListView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.main.FeatureConfig;
import at.alladin.rmbt.android.terms.RMBTCheckFragment.CheckType;
import at.alladin.rmbt.android.terms.RMBTTermsActivity;
import at.alladin.rmbt.android.util.ConfigHelper;

public class RMBTPreferenceActivity extends PreferenceActivity
{
    protected static final int REQUEST_NDT_CHECK = 1;
    protected static final int REQUEST_IC_CHECK = 2;
    
    protected Method mLoadHeaders = null;
    protected Method mHasHeaders = null;
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case android.R.id.home:
    		finish();
    		break;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    /**
     * Checks to see if using new v11+ way of handling PrefsFragments.
     * 
     * @return Returns false pre-v11, else checks to see if using headers.
     */
    public boolean isNewV11Prefs()
    {
        if (mHasHeaders != null && mLoadHeaders != null)
            try
            {
                return (Boolean) mHasHeaders.invoke(this);
            }
            catch (final IllegalArgumentException e)
            {
            }
            catch (final IllegalAccessException e)
            {
            }
            catch (final InvocationTargetException e)
            {
            }
        return false;
    }
    
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        // onBuildHeaders() will be called during super.onCreate()
        try
        {
            mLoadHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class);
            mHasHeaders = getClass().getMethod("hasHeaders");
        }
        catch (final NoSuchMethodException e)
        {
        }
        super.onCreate(savedInstanceState);
        if (!isNewV11Prefs())
        {
            addPreferencesFromResource(R.xml.preferences);
            if (ConfigHelper.isDevEnabled(this))
                addPreferencesFromResource(R.xml.preferences_dev);
        }
        
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);
       
        final ListView v = getListView();
        v.setCacheColorHint(0);
        
        //final float scale = getResources().getDisplayMetrics().density;
        //final int padding = Helperfunctions.dpToPx(10, scale);
        
        //final ViewGroup vg = (ViewGroup) v.getRootView();
        //vg.setPadding(padding, padding, padding, padding);
        
        //final int paddingTopBottom = Helperfunctions.dpToPx(3, scale);
        //final int paddingLeftRight = Helperfunctions.dpToPx(10, scale);
        //v.setBackgroundResource(R.drawable.box_large);
        //v.setBackgroundResource(R.drawable.app_bgdn_radiant);
        //v.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);

        final Preference ndtPref = (Preference) findPreference("ndt");
        if (ndtPref != null)
        {
            ndtPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    if (preference instanceof CheckBoxPreference)
                    {
                        final CheckBoxPreference cbp = (CheckBoxPreference) preference;
                        
                        if (cbp.isChecked())
                        {
                            cbp.setChecked(false);
                            final Intent intent = new Intent(getBaseContext(), RMBTTermsActivity.class);
                            intent.putExtra(RMBTTermsActivity.EXTRA_KEY_CHECK_TYPE, CheckType.NDT.name());
                            startActivityForResult(intent, REQUEST_NDT_CHECK);
                        }
                    }
                    return true;
                }
            });
        }
        
        final Preference icPref = (Preference) findPreference("information_commissioner");
        if (icPref != null)
        {
        	if (FeatureConfig.TEST_USE_PERSONAL_DATA_FUZZING) {
	        	icPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
	                @Override
	                public boolean onPreferenceClick(Preference preference)
	                {
	                    if (preference instanceof CheckBoxPreference)
	                    {
	                        final CheckBoxPreference cbp = (CheckBoxPreference) preference;
	                        
	                        if (cbp.isChecked())
	                        {
	                            cbp.setChecked(false);
	                            final Intent intent = new Intent(getBaseContext(), RMBTTermsActivity.class);
	                            intent.putExtra(RMBTTermsActivity.EXTRA_KEY_CHECK_TYPE, CheckType.INFORMATION_COMMISSIONER.name());
	                            startActivityForResult(intent, REQUEST_IC_CHECK);
	                        }
	                    }
	                    return true;
	                }
	            });
        	}
        	else {
        		final PreferenceCategory cat = (PreferenceCategory) findPreference("preference_category_test");
        		cat.removePreference(icPref);
        	}
        }
        
        final Preference gpsPref = (Preference) findPreference("location_settings");
        if (gpsPref != null)
        {
            gpsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    return true;
                }
            });
        }

        final Preference langPref = (Preference) findPreference("language_settings");
        if (langPref != null)
        {
            langPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                    return true;
                }
            });
        }

        // v.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        
        // addPreferencesFromResource(R.xml.preferences);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NDT_CHECK)
        {
            ((CheckBoxPreference) findPreference("ndt")).setChecked(ConfigHelper.isNDT(this));
        }
        else if (requestCode == REQUEST_IC_CHECK) {
        	((CheckBoxPreference) findPreference("information_commissioner")).setChecked(ConfigHelper.isInformationCommissioner(this));
        }
    }
    
    
    /*
     * @Override public void onBuildHeaders(List<Header> aTarget) { try {
     * mLoadHeaders.invoke(this,new Object[]{R.xml.pref_headers,aTarget}); }
     * catch (IllegalArgumentException e) { } catch (IllegalAccessException e) {
     * } catch (InvocationTargetException e) { } }
     * 
     * @TargetApi(11) static public class PrefsFragment extends
     * PreferenceFragment {
     * 
     * @Override public void onCreate(Bundle aSavedState) {
     * super.onCreate(aSavedState); Context anAct =
     * getActivity().getApplicationContext(); int thePrefRes =
     * anAct.getResources
     * ().getIdentifier(getArguments().getString("pref-resource"),
     * "xml",anAct.getPackageName()); addPreferencesFromResource(thePrefRes);
     * //addPreferencesFromResource(R.xml.preferences);
     * Log.i("test","Preferences Loaded"); } }
     */
}
