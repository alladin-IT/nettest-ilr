package at.alladin.rmbt.android.views;

import org.json.JSONArray;

import at.alladin.rmbt.android.util.EndTaskListener;

/**
 * Created by lb on 16.11.17.
 */
public interface ResultDetails {
    void initialize (EndTaskListener<JSONArray> resultFetchEndTaskListener);
}
