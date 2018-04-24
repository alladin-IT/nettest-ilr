package at.alladin.rmbt.android.main;

import android.support.v4.app.Fragment;

import at.alladin.rmbt.android.util.InformationCollector;

/**
 * Created by lb on 10.01.18.
 */

public abstract class AbstractMainMenuFragment extends Fragment {
    public abstract boolean onBackPressed();

    public abstract InformationCollector getInformationCollector();

    public abstract void toggleNerdModeVisibility(boolean isVisible);
}
