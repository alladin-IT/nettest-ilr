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

import java.util.List;

import at.alladin.openrmbt.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuListAdapter extends BaseAdapter {
    private Context context;
    private List<String> mTitle;
    private List<Integer> mIcon;
    private LayoutInflater inflater;
     
    public MainMenuListAdapter(Context pContext, List<String> pTitle, List<Integer> pIcon) {
        context = pContext;
        mTitle = pTitle;
        mIcon = pIcon;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.navigation_list_item, parent, false);
 
        TextView txtTitle = (TextView) itemView.findViewById(R.id.title);
        //TextView txtSubTitle = (TextView) itemView.findViewById(R.id.subtitle);
        ImageView imgIcon = (ImageView) itemView.findViewById(R.id.icon);
 
        txtTitle.setText(mTitle.get(position));
        //txtSubTitle.setText(mSubTitle[position]);
        imgIcon.setImageResource(mIcon.get(position));
        imgIcon.setColorFilter(context.getResources().getColor(R.color.menu_foreground));
        
        return itemView;
    }
 
    @Override
    public int getCount() {
        return mTitle.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mTitle.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
}