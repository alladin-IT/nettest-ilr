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

import android.content.res.Resources;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.alladin.openrmbt.android.R;

public class MainMenuUtil {
	
    /**
     * 
     */
    public static final int MENU_STATISTICS_INDEX = 3;

	public static List<String> getMenuTitles(final Resources res) {
		final List<String> menuList = new ArrayList<String>();
		Collections.addAll(menuList, res.getStringArray(R.array.navigation_main_titles));
        if (!FeatureConfig.USE_OPENDATA) {
        	menuList.remove(MENU_STATISTICS_INDEX);
        }
        
        return menuList;
	}
	
	public static List<Integer> getMenuIds(final Resources res) {
		final List<Integer> menuIds = new ArrayList<Integer>();
		final TypedArray iconIds = res.obtainTypedArray(R.array.navigation_main_icon_ids);

		for (int x = 0; x < iconIds.length(); x++) {
			menuIds.add(iconIds.getResourceId(x, 0));
		}

		if (!FeatureConfig.USE_OPENDATA) {
        	menuIds.remove(MENU_STATISTICS_INDEX);
        }
        
		return menuIds;
	}
	

	public static List<Integer> getMenuActionIds(final Resources res) {
		final List<Integer> menuIds = new ArrayList<Integer>();
		final TypedArray iconIds = res.obtainTypedArray(R.array.navigation_main_menu_ids);

		for (int x = 0; x < iconIds.length(); x++) {
			menuIds.add(iconIds.getResourceId(x, 0));
		}

		if (!FeatureConfig.USE_OPENDATA) {
        	menuIds.remove(MENU_STATISTICS_INDEX);
        }
        
		return menuIds;
	}
}
