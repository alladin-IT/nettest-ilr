package at.alladin.rmbt.android.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.main.RMBTMainActivity;

/**
 * Created by fk on 1/10/18.
 */

public abstract class AbstractTestFragment extends Fragment {
    public abstract boolean onBackPressed();

    public abstract boolean onBackPressedHandler();

}
