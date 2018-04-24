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

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.test.ChangeableSpeedTestStatus;
import at.alladin.rmbt.android.test.SimpleTestFragment;
import at.alladin.rmbt.client.helper.IntermediateResult;

/**
 * Created by lb on 23.06.16.
 */
public class SimpleTestResultFragment extends Fragment implements ChangeableSpeedTestStatus {

    private final static int LIST_ELEMENT_RESOURCE_ID = R.layout.simple_test_view_speed_info_element_single;

    public final static int FLAG_NONE = 0;
    public final static int FLAG_HIDE_PROGRESSBAR = 1;
    public final static int FLAG_SHOW_PROGRESSBAR = 2;
    public final static int FLAG_FORCE_SHOW = 3;

    private final static int ATTR_SCREEN_TYPE_TEST_SCREEN = 0;
    private final static int ATTR_SCREEN_TYPE_OTHER = 1;

    public enum InfoStat {
        PING(0, R.string.test_bottom_test_status_ping, R.string.test_ms, true, R.string.ifont_ping, Integer.MIN_VALUE),
        DOWNLOAD(1, R.string.test_bottom_test_status_down, R.string.test_mbps, false, R.string.ifont_down, Integer.MIN_VALUE),
        UPLOAD(2, R.string.test_bottom_test_status_up, R.string.test_mbps, false, R.string.ifont_up, Integer.MIN_VALUE);

        final protected int listPosition;
        final protected int resId;
        final protected int unitResId;
        final protected boolean hasProgressBar;
        final protected int iconResId;
        final protected int altImageResId;

        private InfoStat(int listPosition, int resId, int unitResId, boolean hasProgressBar,
                         int iconResId, int alternativeImageResIdAfterFinish) {
            this.listPosition = listPosition;
            this.resId = resId;
            this.unitResId = unitResId;
            this.hasProgressBar = hasProgressBar;
            this.iconResId = iconResId;
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

        public int getUnitResId() {
            return unitResId;
        }

        public int getIconResId() {
            return iconResId;
        }
    }

    final class GroupView {
        InfoStat infoType;
        String title;
        String result;
        String unit;
        boolean hideProgressBar = false;

        public GroupView(final String title, final String unit, final InfoStat infoType) {
            this.title = title;
            this.infoType = infoType;
            this.unit = unit;
        }

        @Override
        public String toString() {
            return "GroupView [infoType=" + infoType + ", title=" + title
                    + ", result=" + result + "]";
        }
    }

    final class ViewHolder {
        ViewGroup parent;
        TextView title;
        TextView result;
        TextView unit;
        TextView icon;
        ProgressBar progressBar;
    }

    List<GroupView> groupViewList = new ArrayList<>();

    List<ViewHolder> viewHolderList = new ArrayList<>();

    private boolean forceProgressBarHide = false;

    private int screenType = ATTR_SCREEN_TYPE_TEST_SCREEN;

    private View containerView;

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity, attrs, savedInstanceState);
        init(activity, attrs);
    }

    /**
     * initialize this fragment with custom attributes
     * @param attrs
     */
    private void init(final Activity context, final AttributeSet attrs) {
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SimpleTestResultFragment);
        screenType = array.getInt(R.styleable.SimpleTestResultFragment_screen_type, ATTR_SCREEN_TYPE_TEST_SCREEN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (InfoStat infoStat : InfoStat.values()) {
            groupViewList.add(infoStat.listPosition, new GroupView(getActivity().getString(infoStat.getResId()), getActivity().getString(infoStat.getUnitResId()),	infoStat));
        }

        System.out.println(groupViewList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.simple_test_result_line, null);
        setView(v);

        if (screenType == ATTR_SCREEN_TYPE_OTHER) {
            if (((RMBTMainActivity) getActivity()).getLastIntermediateResult() == null) {
                v.setVisibility(View.INVISIBLE);
            } else {
                v.setVisibility(View.VISIBLE);

                final IntermediateResult intermediateResult = ((RMBTMainActivity) getActivity()).getLastIntermediateResult();
                final String pingStr = SimpleTestFragment.PING_FORMAT.format(intermediateResult.pingNano / 1000000.0);
                setResultPingString(pingStr, FLAG_HIDE_PROGRESSBAR);

                final String downStr = SimpleTestFragment.SPEED_FORMAT.format(intermediateResult.downBitPerSec / 1000000.0);
                setResultDownString(downStr, FLAG_HIDE_PROGRESSBAR);

                final String upStr = SimpleTestFragment.SPEED_FORMAT.format(intermediateResult.upBitPerSec / 1000000.0);
                setResultUpString(upStr, FLAG_HIDE_PROGRESSBAR);
            }
        }
        return v;
    }

    @Override
    public String getResultInitString() {
        return null;
    }

    @Override
    public String getResultPingString() {
        return groupViewList.get(InfoStat.PING.getListPosition()).result;
    }

    @Override
    public String getResultDownString() {
        return groupViewList.get(InfoStat.DOWNLOAD.getListPosition()).result;
    }

    @Override
    public String getResultUpString() {
        return groupViewList.get(InfoStat.UPLOAD.getListPosition()).result;
    }

    @Override
    public void setResultDownString(String s, Object flag) {
        groupViewList.get(InfoStat.DOWNLOAD.getListPosition()).result = s;
        updateView(InfoStat.DOWNLOAD, (Integer)flag != FLAG_SHOW_PROGRESSBAR, flag);
    }

    @Override
    public void setResultUpString(String s, Object flag) {
        groupViewList.get(InfoStat.UPLOAD.getListPosition()).result = s;
        updateView(InfoStat.UPLOAD, (Integer)flag != FLAG_HIDE_PROGRESSBAR, flag);
    }

    @Override
    public void setResultInitString(String s, Object flag) {
        //ignore
    }

    @Override
    public void setResultPingString(String s, Object flag) {
        groupViewList.get(InfoStat.PING.getListPosition()).result = s;
        updateView(InfoStat.PING, (Integer)flag != FLAG_HIDE_PROGRESSBAR, flag);
    }

    public void updateView(final InfoStat infoStat, boolean hideProgressBar, final Object flag) {
        if (hideProgressBar && containerView.getVisibility() == View.INVISIBLE) {
            containerView.setVisibility(View.VISIBLE);
        }

        if (flag != null && (Integer)flag == FLAG_SHOW_PROGRESSBAR && viewHolderList.get(infoStat.getListPosition()).parent.getVisibility() != View.VISIBLE) {
            viewHolderList.get(infoStat.getListPosition()).parent.setVisibility(View.VISIBLE);
        }

        if (flag != null && (Integer)flag == FLAG_FORCE_SHOW) {
            viewHolderList.get(infoStat.getListPosition()).parent.setVisibility(View.VISIBLE);
        }

        final String result = groupViewList.get(infoStat.getListPosition()).result;

        if ((result != null /* && !result.equals(groupViewList.get(infoStat.getListPosition()).result) */)
                || groupViewList.get(infoStat.getListPosition()).hideProgressBar != hideProgressBar) {

            if (!"...".equals(result)) {
                if (viewHolderList.get(infoStat.getListPosition()).result.getVisibility() != View.VISIBLE) {
                    viewHolderList.get(infoStat.getListPosition()).result.setVisibility(View.VISIBLE);
                    viewHolderList.get(infoStat.getListPosition()).unit.setVisibility(View.VISIBLE);
                }

                groupViewList.get(infoStat.getListPosition()).hideProgressBar = hideProgressBar || (flag != null && (Integer)flag == FLAG_FORCE_SHOW);
                if (result.indexOf(" ") > 0) {
                    viewHolderList.get(infoStat.getListPosition()).result.setText(result.substring(0, result.indexOf(" ")));
                } else {
                    viewHolderList.get(infoStat.getListPosition()).result.setText(result);
                }
            }
            else {
                viewHolderList.get(infoStat.getListPosition()).result.setText(result);
            }
        }
        else if (result != null && "...".equals(result) && !result.equals(groupViewList.get(infoStat.getListPosition()).result)) {
            viewHolderList.get(infoStat.getListPosition()).result.setText(result);
        }
    }

    @Override
    public void setForceHideProgressBar(boolean forceHide) {
        this.forceProgressBarHide = forceHide;
    }

    @Override
    public boolean isForceHideProgressBar() {
        return !this.forceProgressBarHide;
    }

    @Override
    public void setView(View container) {
        if (container != null && container instanceof LinearLayout) {
            this.containerView = container;
            final LayoutInflater inflater = LayoutInflater.from(container.getContext());
            if (inflater != null) {
                for (int i = 0; i < groupViewList.size(); i++) {
                    final LinearLayout ll = (LinearLayout) inflater.inflate(LIST_ELEMENT_RESOURCE_ID, null);
                    ll.setGravity(Gravity.CENTER_HORIZONTAL);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT, (3f/(float)InfoStat.values().length));
                    ll.setLayoutParams(param);
                    ((LinearLayout) container).addView(ll);

                    final ViewHolder holder = new ViewHolder();
                    holder.title = (TextView) ll.findViewById(R.id.test_view_info_list_title);
                    holder.title.setText(groupViewList.get(i).title);
                    holder.result = (TextView) ll.findViewById(R.id.test_view_info_list_result);
                    holder.result.setText(groupViewList.get(i).result);
                    holder.progressBar = (ProgressBar) ll.findViewById(R.id.test_view_info_list_progress_bar);
                    holder.progressBar.setVisibility(View.GONE);
                    holder.unit = (TextView) ll.findViewById(R.id.test_view_info_list_unit);
                    holder.unit.setText("(" + groupViewList.get(i).unit + ")");
                    holder.icon = (TextView) ll.findViewById(R.id.test_view_info_list_image);
                    if (holder.icon != null) {
                        holder.icon.setText(groupViewList.get(i).infoType.getIconResId());
                        //holder.icon.setImageResource(groupViewList.get(i).infoType.getIconResId());
                    }

                    holder.parent = ll;
                    if (screenType != ATTR_SCREEN_TYPE_OTHER) {
                        holder.parent.setVisibility(View.INVISIBLE);
                    }

                    viewHolderList.add(holder);
                }
            }
        }
    }
}
