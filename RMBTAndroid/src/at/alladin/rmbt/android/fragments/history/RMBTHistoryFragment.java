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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import at.alladin.openrmbt.android.R;

import java.util.List;
import java.util.Map;

import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.android.main.RMBTMainActivity.HistoryUpdatedCallback;
import at.alladin.rmbt.android.util.CheckDissasociationTask;
import at.alladin.rmbt.android.util.EndTaskListener;

public class RMBTHistoryFragment extends Fragment implements HistoryUpdatedCallback
{
    
    private static final String DEBUG_TAG = "RMBTHistoryFragment";
    
    private SimpleAdapter historyListAdapter;
    
    private RMBTMainActivity activity;
    
    private View view;
    
    private ListView listView;
    
    private int listViewIdx;
    private int listViewTop;
    
    private ProgressBar progessBar;
    
    private TextView emptyView;

    List<Map<String, String>> historyList;
    
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        activity = (RMBTMainActivity) getActivity();
        
        // devicesToShow = activity.getHistoryFilterDevicesFilter();
        // networksToShow = activity.getHistoryFilterNetworksFilter();
        //
        // if (devicesToShow == null && networksToShow == null) {
        // devicesToShow = new ArrayList<String>();
        // networksToShow = new ArrayList<String>();
        // }
        //
        // Log.i(DEBUG_TAG, devicesToShow.toString());
    }
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        
        super.onCreateView(inflater, container, savedInstanceState);
        
        if (savedInstanceState != null)
        {
            listViewIdx = savedInstanceState.getInt("listViewIdx", listViewIdx);
            listViewTop = savedInstanceState.getInt("listViewTop", listViewTop);
            Log.d(DEBUG_TAG, "loaded: idx:" + listViewIdx + " top:" + listViewTop);
        }
        
        view = inflater.inflate(R.layout.history, container, false);
        
        listView = (ListView) view.findViewById(R.id.historyList);
        emptyView = (TextView) view.findViewById(R.id.infoText);
        progessBar = (ProgressBar) view.findViewById(R.id.progressBar);
        
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        progessBar.setVisibility(View.VISIBLE);
        
        /*
         * if ((historyTask == null || (historyTask != null ||
         * historyTask.isCancelled()))) { historyTask = new
         * CheckHistoryTask(activity);
         * 
         * historyTask.setEndTaskListener(this); historyTask.execute(); }
         */
        
        // listView.setEmptyView(emptyView);
        
        listView.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView<?> l, final View v, final int position, final long id)
            {
                activity.showHistoryPager(historyList.get(position).get("test_uuid"));
            }
            
        });
        
        return view;
    }
    
    private void saveListViewState()
    {
        listViewIdx = listView.getFirstVisiblePosition();
        final View v = listView.getChildAt(0);
        listViewTop = v == null ? 0 : v.getTop();
    }
    
    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (listView != null)
        {
            saveListViewState();
            outState.putInt("listViewIdx", listViewIdx);
            outState.putInt("listViewTop", listViewTop);
            Log.d(DEBUG_TAG, "saved: idx:" + listViewIdx + " top:" + listViewTop);
        }
    }
    
    @Override
    public void onStart()
    {
        super.onStart();
        
        activity.updateHistory(this);
    }
    
    @Override
    public void onStop()
    {
        super.onStop();
        saveListViewState();
    }
    
    @Override
    public void historyUpdated(final int status)
    {
        if (!isVisible())
            return;
        switch (status)
        {
        case SUCCESSFUL:
            historyList = activity.getHistoryItemList();
            historyListAdapter = new SimpleAdapter(getActivity(), historyList, R.layout.history_item,
                    new String[] { "device", "type", "date", "down", "up", "ping" }, new int[] { R.id.device,
                            R.id.type, R.id.date, R.id.down, R.id.up, R.id.ping });
            
            listView.setAdapter(historyListAdapter);
            listView.setSelectionFromTop(listViewIdx, listViewTop);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(choiceModeListener);

            Log.d(DEBUG_TAG, "set: idx:" + listViewIdx + " top:" + listViewTop);
            
            listView.invalidate();
            
            progessBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            break;
            
        case LIST_EMPTY:
            Log.i(DEBUG_TAG, "LEERE LISTE");
            progessBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(getString(R.string.error_no_data));
            emptyView.invalidate();
            break;
            
        case ERROR:
            Log.i(DEBUG_TAG, "FEHLER");
            progessBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(getString(R.string.error_history_no_data_no_connection));
            emptyView.invalidate();
            break;
        }
    }

    ProgressDialog progressDialog;

    final AbsListView.MultiChoiceModeListener choiceModeListener = new AbsListView.MultiChoiceModeListener() {

        int selectedItem = Integer.MIN_VALUE;

        public void deleteSelectedItem() {
            System.out.println("delete selected item: " + selectedItem);
            try {
                if (selectedItem < historyList.size()) {
                    final int itemIndex = selectedItem;
                    System.out.println(historyList.get(itemIndex));
                    final String testUuid = historyList.get(itemIndex).get("test_uuid");
                    if (testUuid != null) {
                        //test has got an UUID so remove it remotely

                        final CheckDissasociationTask dissasociationTask = new CheckDissasociationTask(activity, testUuid);

                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        final String title = getResources().getString(R.string.history_delete_item_progress_title);
                        final String message = getResources().getString(R.string.history_delete_item_progress_info);
                        progressDialog = ProgressDialog.show(getActivity(), title, message, true, false);

                        dissasociationTask.setEndTaskListener(new EndTaskListener<Boolean>() {
                            @Override
                            public void taskEnded(Boolean result) {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

                                if (result) {
                                    historyList.remove(itemIndex);
                                    historyListAdapter.notifyDataSetChanged();
                                } else {
                                    showErrorMessage();
                                }
                            }
                        });

                        dissasociationTask.execute();
                    }
                }
            }
            catch (final Exception e) {
                showErrorMessage();
            }
        }

        private void showErrorMessage() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.test_dialog_error_title);
            builder.setMessage(R.string.history_delete_item_failed);
            builder.setNeutralButton(android.R.string.ok, null);
            builder.create().show();
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if (checked && selectedItem == Integer.MIN_VALUE) {
                selectedItem = position;
            }
            else {
                mode.finish();
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.history_item_menu, menu);
            mode.setTitle(R.string.history_context_menu_delete);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.history_context_menu_delete:
                    deleteSelectedItem();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            selectedItem = Integer.MIN_VALUE;
        }
    };
}
