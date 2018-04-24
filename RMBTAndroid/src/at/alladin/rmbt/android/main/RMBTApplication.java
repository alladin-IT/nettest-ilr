package at.alladin.rmbt.android.main;

import android.app.Application;
import android.content.Context;

/**
 * Created by fk on 8/1/17.
 */

public class RMBTApplication  extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        RMBTApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return RMBTApplication.context;
    }
}
