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

package at.alladin.rmbt.android.fragments.history;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import at.alladin.openrmbt.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.alladin.rmbt.android.fragments.LogFragment;
import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.client.db.model.HistoryFilter;
import at.alladin.rmbt.client.db.util.DbUtil;

public class RMBTFilterFragment extends Fragment
{
    
    private static final String TAG = RMBTFilterFragment.class.getName();

    public final static HistoryFilter[] FILTERS = new HistoryFilter[] {HistoryFilter.DEVICE, HistoryFilter.NETWORK_TYPE};
    
    private RMBTMainActivity activity;
    
    private View view;
    
    private LinearLayout deviceListView;

    private LinearLayout networkListView;

    private ArrayList<String> devicesToShow;

    private ArrayList<String> networksToShow;

    private CheckBox limit25CheckBox;

    private final Map<HistoryFilter, ListView> filterListViewMap = new HashMap<>();

    private Map<HistoryFilter, List<String>> activeFilterMap = new HashMap<>();

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        activity = (RMBTMainActivity) getActivity();
    }
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.history_filter, container, false);

        final LinearLayout filterContainerView = (LinearLayout) view.findViewById(R.id.filterContainer);

        filterListViewMap.clear();
        for(final HistoryFilter filter : FILTERS) {
            final LinearLayout filterLayout = (LinearLayout) inflater.inflate(R.layout.history_filter_container, container, false);
            filterContainerView.addView(filterLayout, 0);
            filterListViewMap.put(filter, (ListView) filterLayout.findViewById(R.id.filterListView));
            final TextView title = (TextView) filterLayout.findViewById(R.id.filterListHeader);
            switch (filter) {
                case DEVICE:
                    title.setText(R.string.history_filter_devices);
                    break;
                case NETWORK_TYPE:
                    title.setText(R.string.history_filter_networks);
                    break;
            }
        }
        
        final RelativeLayout resultLimitView = (RelativeLayout) view.findViewById(R.id.Limit25Wrapper);
        limit25CheckBox = (CheckBox) view.findViewById(R.id.Limit25CheckBox);
        
        if (activity.getHistoryResultLimit() == 25) {
            limit25CheckBox.setChecked(true);
        }
        else {
            limit25CheckBox.setChecked(false);
        }
        
        resultLimitView.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(final View v)
            {
                if (limit25CheckBox.isChecked())
                {
                    limit25CheckBox.setChecked(false);
                    activity.setHistoryResultLimit(0);
                }
                else
                {
                    limit25CheckBox.setChecked(true);
                    activity.setHistoryResultLimit(25);
                }
                
            }
            
        });

        return view;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView()
    {
        //TODO: do we really need this:
        //activity.setActiveHistoryFilters(new HashMap<HistoryFilter, List<String>>(activeFilterMap));
        super.onDestroyView();
    }

    final AdapterView.OnItemClickListener filterItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.filterItemCheckbox);
            if (checkBox != null) {
                checkBox.setChecked(!checkBox.isChecked());
            }

            final TextView item = (TextView) view.findViewById(R.id.filterItemTitle);
            List<String> filterList = activeFilterMap.get(HistoryFilter.valueOf(String.valueOf(parent.getTag())));

            if (filterList == null) {
                filterList = new ArrayList<>();
                activeFilterMap.put(HistoryFilter.valueOf(String.valueOf(parent.getTag())),filterList);
            }

            if (checkBox.isChecked() && !filterList.contains(item.getText())) {
                filterList.add(item.getText().toString());
            }
            else if (!checkBox.isChecked()) {
                filterList.remove(item.getText().toString());
            }
        }
    };

    public boolean isInFilterList(final HistoryFilter filter, final String key) {
        return activeFilterMap.get(filter).contains(key);
    }

    @Override
    public void onResume() {
        super.onResume();

        //reset map with filters and listviews
        final Map<HistoryFilter, List<String>> availableFilterMap = DbUtil.getAvailableHistoryFilters(activity.getDatabaseHelper().getHistoryDao(), FILTERS);

        activeFilterMap = activity.getActiveHistoryFilters();
        if (activeFilterMap == null) {
            activeFilterMap = availableFilterMap;
        }

        System.out.println(activeFilterMap);

        for (Map.Entry<HistoryFilter, List<String>> filterEntry : availableFilterMap.entrySet()) {
            final ListView listView = filterListViewMap.get(filterEntry.getKey());
            if (listView != null) {
                final List<Map<String, Object>> adapterList = new ArrayList<>();
                for (final String value : filterEntry.getValue()) {
                    final Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("filter", value);
                    itemMap.put("checked", isInFilterList(filterEntry.getKey(), value));
                    adapterList.add(itemMap);
                }
                final SimpleAdapter listViewAdapter = new SimpleAdapter(getActivity(), adapterList, R.layout.history_filter_item,
                        new String[] { "filter", "checked" }, new int[] { R.id.filterItemTitle, R.id.filterItemCheckbox});

                listView.setOnItemClickListener(filterItemClickListener);
                listView.setTag(filterEntry.getKey());
                listView.setAdapter(listViewAdapter);
            }
        }
    }
}
