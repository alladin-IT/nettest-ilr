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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import at.alladin.rmbt.android.main.AppConstants;
import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.android.util.ConfigHelper;

import at.alladin.openrmbt.android.R;

public class RMBTCheckFragment extends Fragment
{
	public enum CheckType {
		NDT("file:///android_asset/ndt_info.html", AppConstants.PAGE_TITLE_NDT_CHECK,
				R.string.terms_ndt_header, R.string.terms_ndt_accept_text, false),
		INFORMATION_COMMISSIONER("file:///android_asset/ic_info.html", AppConstants.PAGE_TITLE_CHECK_INFORMATION_COMMISSIONER,
				R.string.terms_ic_header, R.string.terms_ic_accept_text, true);


        private final String templateFile;
		private final String fragmentTag;
		private final boolean defaultIsChecked;
		private final int titleId;
		private final int textId;
		
		CheckType(final String templateFile, final String fragmentTag, final int titleId, final int textId, final boolean defaultIsChecked) {
			this.templateFile = templateFile;
			this.fragmentTag = fragmentTag;
			this.titleId = titleId;
			this.textId = textId;
			this.defaultIsChecked = defaultIsChecked;
		}

		public String getTemplateFile() {
			return templateFile;
		}

		public String getFragmentTag() {
			return fragmentTag;
		}

		public int getTitleId() {
			return titleId;
		}

		public int getTextId() {
			return textId;
		}

		public boolean isDefaultIsChecked() {
			return defaultIsChecked;
		}
	}
	
	private final CheckType checkType;
	
    private CheckBox checkBox;
    
    boolean firstTime = true;
    
    public static RMBTCheckFragment newInstance(final CheckType checkType) {
    	return new RMBTCheckFragment(checkType);
    }
    
    private RMBTCheckFragment(final CheckType checkType) {
    	this.checkType = checkType;
	}
    
    public CheckType getCheckType() {
		return checkType;
	}

	@Override
    public void onSaveInstanceState(final Bundle b)
    {
        b.putBoolean("isChecked", checkBox.isChecked());
        super.onSaveInstanceState(b);
    }
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        if (! (getActivity() instanceof RMBTMainActivity))
            firstTime = false;
        
        final View v = inflater.inflate(R.layout.ndt_check, container, false);
                
        if (! firstTime)
            v.findViewById(R.id.termsNdtButtonBack).setVisibility(View.GONE);
        
        final TextView textTitle = (TextView) v.findViewById(R.id.check_fragment_title);
        textTitle.setText(checkType.getTitleId());
        
        checkBox = (CheckBox) v.findViewById(R.id.ndtCheckBox);
        checkBox.setText(checkType.getTextId());
        
        if (savedInstanceState != null) {
            checkBox.setChecked(savedInstanceState.getBoolean("isChecked"));
        }
        else {
        	checkBox.setChecked(checkType.isDefaultIsChecked());
        }

        
        final Button buttonAccept = (Button) v.findViewById(R.id.termsNdtAcceptButton);
        
        if (! firstTime)
        {    
            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    buttonAccept.setEnabled(isChecked);
                }
            });
        }
        
        final WebView wv = (WebView) v.findViewById(R.id.ndtInfoWebView);
        wv.loadUrl(checkType.getTemplateFile());
        
        buttonAccept.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final Activity activity = getActivity();
                switch (checkType) {
                case INFORMATION_COMMISSIONER:
                    PreferenceManager
                            .getDefaultSharedPreferences(getActivity())
                            .edit()
                            .putBoolean("anonymous_mode", !checkBox.isChecked())
                            .apply();

                    //TODO probably deprecated and not needed anymore: information commissioner settings
                    ConfigHelper.setInformationCommissioner(activity, checkBox.isChecked());
                    ConfigHelper.setICDecisionMade(activity, true);                	
                	break;
                case NDT:
                    ConfigHelper.setNDT(activity, checkBox.isChecked());
                    ConfigHelper.setNDTDecisionMade(activity, true);                	
                	break;
                }
                ((FragmentActivity) activity).getSupportFragmentManager().popBackStack(checkType.getFragmentTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (firstTime && CheckType.INFORMATION_COMMISSIONER.equals(checkType))
                    ((RMBTMainActivity) activity).initApp(false);
                else
                {
                    getActivity().setResult(checkBox.isChecked() ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
                    getActivity().finish();
                }
            }
        });
        
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                buttonAccept.setEnabled(firstTime || checkBox.isChecked());
            }
        }, 500);
        
        final Button buttonBack = (Button) v.findViewById(R.id.termsNdtBackButton);
        buttonBack.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        
        return v;
    }

}
