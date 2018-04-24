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

package at.alladin.rmbt.android.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import at.alladin.openrmbt.android.R;

/**
 * 
 * @author lb
 *
 */
public class SpeedTestStatViewController implements ChangeableSpeedTestStatus {	
	
	public final static int FLAG_NONE = 0;
	public final static int FLAG_HIDE_PROGRESSBAR = 1;
	public final static int FLAG_SHOW_PROGRESSBAR = 2;
	
	public enum InfoStat {
		INIT(0, R.string.test_bottom_test_status_init, true, R.drawable.traffic_lights_green),
		PING(1, R.string.test_bottom_test_status_ping, true, Integer.MIN_VALUE),
		DOWNLOAD(2, R.string.test_bottom_test_status_down, false, Integer.MIN_VALUE),
		UPLOAD(3, R.string.test_bottom_test_status_up, false, Integer.MIN_VALUE);
		
		final protected int listPosition;
		final protected int resId;
		final protected boolean hasProgressBar;
		final protected int altImageResId;

		private InfoStat(int listPosition, int resId, boolean hasProgressBar, int alternativeImageResIdAfterFinish) {
			this.listPosition = listPosition;
			this.resId = resId;
			this.hasProgressBar = hasProgressBar;
			this.altImageResId = alternativeImageResIdAfterFinish;
		}

		public int getListPosition() {
			return listPosition;
		}

		public int getResId() {
			return resId;
		}

		public boolean hasProgressBar() {
			return hasProgressBar;
		}

		public int getAltImageResId() {
			return altImageResId;
		}
	}
	
	final class GroupViewArrayAdapter extends ArrayAdapter<GroupView> {

		private final Activity context;
		private final List<GroupView> groups;

		final class ViewHolder {
			public TextView title;
			public TextView result;
			public ImageView image;
			public ProgressBar progressBar;
		}

		public GroupViewArrayAdapter(Activity context, List<GroupView> objects) {
			super(context, R.layout.test_view_info_list_element, objects);
			this.context = context;
			this.groups = objects;
		}
		
		@Override
		public View getView(int position, View rowView, ViewGroup parent) {
			if (rowView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
		        rowView = inflater.inflate(R.layout.test_view_info_list_element, null);
		        // configure view holder
		        ViewHolder viewHolder = new ViewHolder();
		        viewHolder.title = (TextView) rowView.findViewById(R.id.test_view_info_list_title);
		        viewHolder.result = (TextView) rowView.findViewById(R.id.test_view_info_list_result);
		        viewHolder.image = (ImageView) rowView.findViewById(R.id.test_view_info_list_image);
		        viewHolder.progressBar = (ProgressBar) rowView.findViewById(R.id.test_view_info_list_progress_bar);
		        rowView.setTag(viewHolder);
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();
	        holder.progressBar.setVisibility(View.VISIBLE);
			
			holder.title.setText(groups.get(position).title);
			holder.result.setText(groups.get(position).result);
			
			if (groups.get(position).hideProgressBar) {
				holder.result.setText(groups.get(position).result);
				holder.progressBar.setVisibility(View.GONE);
				holder.result.setVisibility(View.VISIBLE);
				
				int imgResId = groups.get(position).infoType.getAltImageResId();
				if (imgResId != Integer.MIN_VALUE) {
					holder.result.setVisibility(View.GONE);
					holder.image.setVisibility(View.VISIBLE);
					holder.image.setImageResource(imgResId);
				}
			}
			else if (!groups.get(position).infoType.hasProgressBar) {
	        	holder.progressBar.setVisibility(View.GONE);		        
			}
			else if (forceProgressBarHide) {
				holder.progressBar.setVisibility(View.GONE);
			}
			
			return rowView;
		}
		
		public void updateStat(InfoStat stat, String result, boolean hideProgressBar) {
			if ((result != null && !result.equals(groups.get(stat.getListPosition()).result)) 
					|| groups.get(stat.getListPosition()).hideProgressBar != hideProgressBar) {
				groups.get(stat.getListPosition()).result = result;
				groups.get(stat.getListPosition()).hideProgressBar = hideProgressBar;
				notifyDataSetChanged();
			}
		}
		
	}
	
	final class GroupView {
		InfoStat infoType;
		String title;
		String result;
		boolean hideProgressBar = false;
		
		public GroupView(final String title, final InfoStat infoType) {
			this.title = title;
			this.infoType = infoType;
		}
		
		@Override
		public String toString() {
			return "GroupView [infoType=" + infoType + ", title=" + title
					+ ", result=" + result + "]";
		}
	}
	
	private final GroupViewArrayAdapter listAdapter;
	
	private boolean forceProgressBarHide = false;
	
	public SpeedTestStatViewController(Activity context) {
				
		List<GroupView> groupViewList = new ArrayList<GroupView>();
		for (InfoStat infoStat : InfoStat.values()) {
			groupViewList.add(infoStat.listPosition, new GroupView(context.getString(infoStat.getResId()), infoStat));
		}
		
		System.out.println(groupViewList);
		
		listAdapter = new GroupViewArrayAdapter(context, groupViewList);
	}
	
	public GroupViewArrayAdapter getListAdaper() {
		return this.listAdapter;
	}

	@Override
	public String getResultInitString() {
		return listAdapter.getItem(InfoStat.INIT.getListPosition()).result;
	}

	@Override
	public String getResultPingString() {
		return listAdapter.getItem(InfoStat.PING.getListPosition()).result;
	}

	@Override
	public String getResultDownString() {
		return listAdapter.getItem(InfoStat.DOWNLOAD.getListPosition()).result;
	}

	@Override
	public String getResultUpString() {
		return listAdapter.getItem(InfoStat.UPLOAD.getListPosition()).result;
	}

	@Override
	public void setView(View container) {
		if (container != null && container instanceof ListView) {
			((ListView) container).setAdapter(getListAdaper());
		}
	}

	@Override
	public void setResultDownString(String s, Object flag) {
		listAdapter.updateStat(InfoStat.DOWNLOAD, s, (Integer)flag == FLAG_HIDE_PROGRESSBAR);
	}

	@Override
	public void setResultUpString(String s, Object flag) {
		listAdapter.updateStat(InfoStat.UPLOAD, s, (Integer)flag == FLAG_HIDE_PROGRESSBAR);
	}

	@Override
	public void setResultInitString(String s, Object flag) {
		listAdapter.updateStat(InfoStat.INIT, s, (Integer)flag == FLAG_HIDE_PROGRESSBAR);
	}

	@Override
	public void setResultPingString(String s, Object flag) {
		listAdapter.updateStat(InfoStat.PING, s, (Integer)flag == FLAG_HIDE_PROGRESSBAR);		
	}

	@Override
	public void setForceHideProgressBar(boolean forceHide) {
		this.forceProgressBarHide = forceHide;
	}

	@Override
	public boolean isForceHideProgressBar() {
		return !this.forceProgressBarHide;
	}

}
