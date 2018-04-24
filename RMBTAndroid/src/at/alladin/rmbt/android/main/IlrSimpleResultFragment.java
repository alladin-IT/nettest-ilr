package at.alladin.rmbt.android.main;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import at.alladin.openrmbt.android.BuildConfig;
import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.fragments.nested.SwitchToResultsFragment;
import at.alladin.rmbt.android.test.AbstractTestFragment;
import at.alladin.rmbt.android.test.TestViewable;
import at.alladin.rmbt.android.views.network.NetworkInfoView;
import at.alladin.rmbt.client.helper.TestStatus;

/**
 * Created by lb on 16.01.18.
 */

public class IlrSimpleResultFragment extends AbstractTestFragment {

    private final static String ARG_TEST_RESULT = "test_result";

    public static class IlrTestResult implements Serializable {
        double speedDownLog;

        String speedDown;

        double speedUpLog;

        String speedUp;

        double pingNs;

        String ping;

        String networkType;

        String networkName;

        String signalStrength;

        public double getSpeedDownLog() {
            return speedDownLog;
        }

        public void setSpeedDownLog(double speedDownLog) {
            this.speedDownLog = speedDownLog;
        }

        public double getSpeedUpLog() {
            return speedUpLog;
        }

        public void setSpeedUpLog(double speedUpLog) {
            this.speedUpLog = speedUpLog;
        }

        public double getPingNs() {
            return pingNs;
        }

        public void setPingNs(double pingNs) {
            this.pingNs = pingNs;
        }

        public String getNetworkName() {
            return networkName;
        }

        public void setNetworkName(String networkName) {
            this.networkName = networkName;
        }

        public String getSignalStrength() {
            return signalStrength;
        }

        public void setSignalStrength(String signalStrength) {
            this.signalStrength = signalStrength;
        }

        public String getSpeedDown() {
            return speedDown;
        }

        public void setSpeedDown(String speedDown) {
            this.speedDown = speedDown;
        }

        public String getSpeedUp() {
            return speedUp;
        }

        public void setSpeedUp(String speedUp) {
            this.speedUp = speedUp;
        }

        public String getPing() {
            return ping;
        }

        public void setPing(String ping) {
            this.ping = ping;
        }

        public String getNetworkType() {
            return networkType;
        }

        public void setNetworkType(String networkType) {
            this.networkType = networkType;
        }
    }

    public static IlrSimpleResultFragment getInstance(final IlrTestResult testResult) {
        final IlrSimpleResultFragment f = new IlrSimpleResultFragment();
        final Bundle b = new Bundle();
        b.putSerializable(ARG_TEST_RESULT, testResult);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.ilr_simple_test, container, false);
        final Bundle b = getArguments();
        final IlrTestResult testResult = (IlrTestResult) b.getSerializable(ARG_TEST_RESULT);

        final TestViewable testViewable = (TestViewable) v.findViewById(R.id.test_view_gauge);
        testViewable.setProgressableElements(v.findViewById(R.id.test_view_gauge_container));
        testViewable.setStatusIconView(v.findViewById(R.id.test_view_status_button));
        testViewable.setTestStatusProgress(TestStatus.DOWN, testResult.getSpeedDownLog(), testResult.getSpeedDown());
        testViewable.setTestStatusProgress(TestStatus.UP, testResult.getSpeedUpLog(), testResult.getSpeedUp());
        testViewable.setTestStatusProgress(TestStatus.PING, testResult.getPingNs(), testResult.getPing());
        testViewable.setTestStatusProgress(TestStatus.END, 100d, "");

        final NetworkInfoView networkInfoView = (NetworkInfoView) v.findViewById(R.id.infocollector_status_view);
        if (networkInfoView != null) {
            networkInfoView.setNetworkName(testResult.getNetworkName());
            networkInfoView.setSignalStrength(testResult.getSignalStrength());
            if (testResult.getNetworkType() != null) {
                networkInfoView.setNetworkType(testResult.getNetworkType());
            }
        }

        final View historyButton = v.findViewById(R.id.ilr_title_page_history_button);
        historyButton.setVisibility(View.VISIBLE);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((RMBTMainActivity) getActivity()).showResultsFromStartScreen(((RMBTMainActivity) getActivity()).getLastTestUuid(),
                            R.anim.slide_out_top, R.anim.slide_in_from_bottom, R.anim.slide_out_bottom, R.anim.slide_in_from_top);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //if a redo button is to be displayed after a test is finished
        if (BuildConfig.TEST_SHOW_REDO_BUTTON) {
            final View redoButton = v.findViewById((R.id.test_redo_button));
            if (redoButton != null) {
                redoButton.setVisibility(View.VISIBLE);
                redoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((RMBTMainActivity) getActivity()).startTestFromResultsScreen(true, Integer.MIN_VALUE, Integer.MIN_VALUE, true);
                    }
                });
            }
        }

        return v;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onBackPressedHandler() {
        return false;
    }
}
