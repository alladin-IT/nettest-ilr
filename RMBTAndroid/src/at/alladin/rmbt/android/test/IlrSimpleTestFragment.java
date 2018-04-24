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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Locale;

import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.main.IlrSimpleResultFragment;
import at.alladin.rmbt.android.main.RMBTMainActivity;
import at.alladin.rmbt.android.test.RMBTService.RMBTBinder;
import at.alladin.rmbt.android.test.SmoothGraph.SmoothingFunction;
import at.alladin.rmbt.android.util.ConfigHelper;
import at.alladin.rmbt.android.util.Helperfunctions;
import at.alladin.rmbt.android.util.InformationCollector;
import at.alladin.rmbt.android.util.net.NetworkFamilyEnum;
import at.alladin.rmbt.android.util.net.NetworkUtil;
import at.alladin.rmbt.android.util.net.NetworkUtil.MinMax;
import at.alladin.rmbt.android.views.GroupCountView;
import at.alladin.rmbt.android.views.ProgressView;
import at.alladin.rmbt.android.views.network.NetworkInfoView;
import at.alladin.rmbt.client.helper.IntermediateResult;
import at.alladin.rmbt.client.helper.NdtStatus;
import at.alladin.rmbt.client.helper.TestStatus;
import at.alladin.rmbt.client.v2.task.QoSTestEnum;

public class IlrSimpleTestFragment extends AbstractTestFragment implements ServiceConnection
{
    private static final String TAG = "RMBTTestFragment";

    /**
     * used for smoothing the speed graph: amount of data needed for smoothing function
     */
    public static final int SMOOTHING_DATA_AMOUNT = 5;
    
    /**
     * smoothing function used for speed graph.
     * BEWARE: different functions could require different data amounts
     */
    public static final SmoothingFunction SMOOTHING_FUNCTION = SmoothingFunction.CENTERED_MOVING_AVARAGE;

    public final static Format PING_FORMAT = new DecimalFormat("@@ ms");
    public final static Format INIT_FORMAT = new DecimalFormat("@@@@ ms");
    public final static Format PERCENT_FORMAT = new DecimalFormat("0%");
    public final static Format SPEED_FORMAT = new DecimalFormat("@@");

    private static final long UPDATE_DELAY = 100;
    private static final int SLOW_UPDATE_COUNT = 20;
    
    public static final int PROGRESS_SEGMENTS_TOTAL = 266;
    public static final int PROGRESS_SEGMENTS_PROGRESS_RING = 133;
    public static final int PROGRESS_SEGMENTS_INIT = 16; //50
    public static final int PROGRESS_SEGMENTS_PING = 17; //50
    public static final int PROGRESS_SEGMENTS_DOWN = 50;
    public static final int PROGRESS_SEGMENTS_UP = 50;
    public static final int PROGRESS_SEGMENTS_QOS = 133;
    
    private static final long GRAPH_MAX_NSECS = 8000000000L;

    private boolean qosMode = false;
    
    private Context context;
    
    private TestViewable testView;
    private NetworkInfoView networkInfoView;
    private ChangeableSpeedTestStatus speedTestStatViewController;
	private boolean uploadGraph;
    private boolean graphStarted;
    private TextView textViewServerName;
    private TextView textViewInfoTitle;
    private ViewGroup groupCountContainerView;
    private ViewGroup qosProgressView;
    private ViewGroup infoView;

    private IntermediateResult intermediateResult;
    private IntermediateResult lastIntermediateResult;
    private int lastNetworkType;
    private String lastNetworkTypeString;
    private NetworkFamilyEnum lastNetworkFamily = NetworkFamilyEnum.UNKNOWN;
    
    private Integer lastSignal;
    private int lastSignalType;
    
    private RMBTService rmbtService;
    
    private long updateCounter;
    
    private Location lastLocation;
    private String lastOperatorName;
    private String lastServerName;
    private String lastIP;
    private TestStatus lastStatus;
    private String lastStatusString;
    private long lastShownWaitTime = -1;
    private String waitText;
    private boolean stopLoop;
    
    private Dialog errorDialog;
    private Dialog abortDialog;
    private ProgressDialog progressDialog;
    
    private boolean showQoSErrorToast = false;
    
    private Handler handler;
    
    private static final long MAX_COUNTER_WITHOUT_RESULT = 100;
    
    private final Runnable resultSwitcherRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            final RMBTMainActivity mainActivity = getMainActivity();
            if (mainActivity == null)
                return;

            mainActivity.setHistoryDirty(true);
            mainActivity.checkSettings(true);
            if (getMainActivity() == null)
                return;
            if (isVisible())
                switchToResult();
        }
    };
    
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        handler = new Handler();
        context = getActivity().getApplicationContext();
        
        waitText = getResources().getString(R.string.test_progress_text_wait);
    }
    
    protected RMBTMainActivity getMainActivity()
    {
        return (RMBTMainActivity) getActivity();
    }
    
    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service)
    {
        Log.d(TAG, "service connected");
        final RMBTBinder binder = (RMBTBinder) service;
        rmbtService = binder.getService();
    }
    
    @Override
    public void onServiceDisconnected(final ComponentName name)
    {
        Log.d(TAG, "service disconnected");
        rmbtService = null;
    }
    
    @Override
    public void onStop()
    {
        super.onStop();
        
        handler.removeCallbacks(updateTask);
        handler.removeCallbacks(resultSwitcherRunnable);
        
        context.unbindService(this);
        
        //getActivity().getActionBar().show();

        ((RMBTMainActivity) getActivity()).setLockNavigationDrawer(false);
        ((RMBTMainActivity) getActivity()).getActionBar().setDisplayHomeAsUpEnabled(true);
        ((RMBTMainActivity) getActivity()).getActionBar().setHomeButtonEnabled(true);
        ((RMBTMainActivity) getActivity()).getActionBar().setDisplayUseLogoEnabled(false);
        ((RMBTMainActivity) getActivity()).getActionBar().setDisplayShowTitleEnabled(true);
        //((RMBTMainActivity) getActivity()).getActionBar().setIcon(R.drawable.app_icon);

        //getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    @Override
    public void onStart()
    {
        super.onStart();

        //getActivity().getActionBar().hide();
        ((RMBTMainActivity) getActivity()).setLockNavigationDrawer(true);
        ((RMBTMainActivity) getActivity()).getActionBar().setDisplayHomeAsUpEnabled(false);
        ((RMBTMainActivity) getActivity()).getActionBar().setHomeButtonEnabled(false);
        ((RMBTMainActivity) getActivity()).getActionBar().setDisplayUseLogoEnabled(true);
        ((RMBTMainActivity) getActivity()).getActionBar().setDisplayShowTitleEnabled(false);
        //((RMBTMainActivity) getActivity()).getActionBar().setIcon(android.R.color.transparent);

        // Bind to RMBTService
        final Intent serviceIntent = new Intent(context, RMBTService.class);
        context.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
        
        handler.post(updateTask);
        
        //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        //final View view = inflater.inflate(R.layout.simple_test, container, false);
        final View view = inflater.inflate(R.layout.ilr_simple_test, container, false);
        return createView(view, inflater, savedInstanceState);
    }
    
    /**
     * 
     * @param view
     * @param inflater
     * @return
     */
    private View createView(final View view, final LayoutInflater inflater, final Bundle savedInstanceState) {
        testView = (TestViewable) view.findViewById(R.id.test_view_gauge);
        networkInfoView = (NetworkInfoView) view.findViewById(R.id.infocollector_status_view);
        infoView = (ViewGroup) view.findViewById(R.id.test_view_info_container);

        textViewServerName = (TextView) view.findViewById(R.id.test_view_server_name);
        textViewInfoTitle = (TextView) view.findViewById(R.id.info_network_name);
        qosProgressView = (ViewGroup) view.findViewById(R.id.test_view_qos_container);
        groupCountContainerView = (ViewGroup) view.findViewById(R.id.test_view_group_count_container);
        
        //uploadGraph = false;
        graphStarted = false;

        //speedTestStatViewController = new SimpleTestResultFragment();
        //getChildFragmentManager().beginTransaction().add(R.id.test_view_speed_info_container, (SimpleTestResultFragment) speedTestStatViewController).commit();

        graphStarted = false;

        final Resources res = getActivity().getResources();
        final String progressTitle = res.getString(R.string.test_progress_title);
        final String progressText = res.getString(R.string.test_progress_text);
        
        lastShownWaitTime = -1;
        if (progressDialog == null) {
        	progressDialog = ProgressDialog.show(getActivity(), progressTitle, progressText, true, false);
        	progressDialog.setOnKeyListener(backKeyListener);
        }

        if (testView != null) {
            final View statusView = view.findViewById(R.id.test_view_status_button);
            testView.setStatusIconView(statusView);
            final View progressBarView = view.findViewById(R.id.test_view_gauge_container);
            testView.setProgressableElements(progressBarView);
        }

        return view;
    }
    
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (testView != null)
            testView.recycle();
        dismissDialogs();
        System.gc();
    }
    
    @Override
    public void onDetach()
    {
        super.onDetach();
        dismissDialogs();
    }
    
    private void dismissDialogs()
    {
        dismissDialog(errorDialog);
        errorDialog = null;
        dismissDialog(abortDialog);
        abortDialog = null;
        dismissDialog(progressDialog);
        progressDialog = null;
    }
    
    private static void dismissDialog(Dialog dialog)
    {
        if (dialog != null)
            dialog.dismiss();
    }
    
    
    private void resetGraph()
    {
        uploadGraph = false;
        graphStarted = false;
    }
    
    private final Runnable updateTask = new Runnable()
    {
        @Override
        public void run()
        {
            if (getActivity() == null)
                return;
            
            if (qosMode && ConfigHelper.isQosEnabled(context)) {
                updateQoSUI();
            }
            else {
                updateUI();
            }
            
            if (rmbtService != null) {
                if (rmbtService.isCompleted() && rmbtService.getTestUuid(false) != null && !rmbtService.isConnectionError()) {
                    if (IlrSimpleTestFragment.this.intermediateResult == null) {
                        IlrSimpleTestFragment.this.intermediateResult = rmbtService.getLastIntermediateResult();
                    }
                    IlrSimpleTestFragment.this.lastIntermediateResult = rmbtService.getLastIntermediateResult();
                   	handler.postDelayed(resultSwitcherRunnable, 300);
                }
            }
            
            if (!stopLoop)
                handler.postDelayed(this, UPDATE_DELAY);
        }
    };

    private final DialogInterface.OnKeyListener backKeyListener = new DialogInterface.OnKeyListener()
    {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
        {
            if (keyCode == KeyEvent.KEYCODE_BACK)
                return onBackPressedHandler();
            return false;
        }
    };
    
    @SuppressWarnings("unchecked")
	private void updateUI()
    {
        String teststatus;
        updateCounter++;
        
        if (rmbtService == null)
            return;
        
        intermediateResult = rmbtService.getIntermediateResult(intermediateResult);
        
        if (intermediateResult == null)
        {
            if (rmbtService.isConnectionError())
            {
                if (progressDialog != null)
                {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                if (! rmbtService.isLoopMode())
                {
                    showErrorDialog(R.string.test_dialog_error_control_server_conn);
                    return;
                }
            }
            
            if (!rmbtService.isTestRunning() && updateCounter > MAX_COUNTER_WITHOUT_RESULT)
                getActivity().getSupportFragmentManager().popBackStack(); // leave
            return;
        }
        
        if (intermediateResult.status == TestStatus.WAIT)
        {
            if (progressDialog != null)
            {
                long wait = (intermediateResult.remainingWait + 999) / 1000; // round
                                                                             // up
                if (wait < 0)
                    wait = 0;
                if (wait != lastShownWaitTime)
                {
                    lastShownWaitTime = wait;
                    progressDialog.setMessage(MessageFormat.format(waitText, wait));
                }
            }
            return;
        }
        
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
        
        boolean forceUpdate = false;
        
        if (rmbtService.getNdtStatus() == NdtStatus.RUNNING)
        {
            final String ndtStatus = String.format(Locale.US, "NDT (%d%%)", Math.round(rmbtService.getNDTProgress() * 100));
            if (lastStatusString == null || !ndtStatus.equals(lastStatusString))
            {
                forceUpdate = true;
                lastStatusString = ndtStatus;
            }
        }
        else if (lastStatus != intermediateResult.status)
        {
            lastStatus = intermediateResult.status;
            lastStatusString = Helperfunctions.getTestStatusString(getResources(), intermediateResult.status);
            forceUpdate = true;
        }
        
        if (updateCounter % SLOW_UPDATE_COUNT == 0 || forceUpdate)
        {
            final int networkType = rmbtService.getNetworkType();
            if (lastNetworkType != networkType && networkType != 0) {
                lastNetworkType = networkType;
                lastNetworkTypeString = Helperfunctions.getNetworkTypeName(networkType);
                lastNetworkFamily = NetworkFamilyEnum.getFamilyByNetworkId(lastNetworkTypeString);
            }

            final String operatorName = rmbtService.getOperatorName();
            if (operatorName != null)
                lastOperatorName = operatorName;
            testView.setHeaderString(lastOperatorName);
            testView.setSubHeaderString(lastNetworkTypeString);

            if (networkInfoView != null) {
                networkInfoView.setNetworkName(lastOperatorName);
                if (!NetworkFamilyEnum.UNKNOWN.equals(lastNetworkFamily) && !lastNetworkTypeString.equals(lastNetworkFamily.getNetworkFamily())) {
                    networkInfoView.setNetworkType(lastNetworkFamily.getNetworkFamily() + "/" + lastNetworkTypeString);
                }
                else {
                    networkInfoView.setNetworkType(lastNetworkTypeString);
                }
            }
            else {
                textViewInfoTitle.setText(lastOperatorName + " " + lastNetworkTypeString);
            }
            
            final String serverName = rmbtService.getServerName();
            if (serverName != null)
                lastServerName = serverName;
            if (lastServerName == null)
                lastServerName = "?";
            
            final Location location = rmbtService.getLocation();
            if (location != null && !location.equals(lastLocation))
                lastLocation = location;
            
            final String locationStr_line1;
            final String locationStr_line2;
            if (lastLocation != null)
            {    
                locationStr_line1 = Helperfunctions.getLocationString(context, getResources(), lastLocation,1);
                locationStr_line2 = Helperfunctions.getLocationString(context, getResources(), lastLocation,2);
            }    
            else
            {    
                locationStr_line1 = "";
                locationStr_line2 = "";
            }
            
            final String ip = rmbtService.getIP();
            if (ip != null)
                lastIP = ip;
            if (lastIP == null)
                lastIP = "?";
            
            teststatus = lastStatusString;

            if (textViewServerName != null) {
                textViewServerName.setText(getString(R.string.test_bottom_test_status_server) + " " + lastServerName);
            }
        }
        
        double speedValueRelative = 0d;
        int progressSegments = 0;
        switch (intermediateResult.status)
        {
        case WAIT:
            break;
        
        case INIT:
            progressSegments = Math.round(PROGRESS_SEGMENTS_INIT * intermediateResult.progress);
            if (graphStarted)
                resetGraph();
            break;
        
        case PING:
            progressSegments = PROGRESS_SEGMENTS_INIT
                    + Math.round(PROGRESS_SEGMENTS_PING * intermediateResult.progress);
            break;
        
        case DOWN:
            progressSegments = PROGRESS_SEGMENTS_INIT + PROGRESS_SEGMENTS_PING
                    + Math.round(PROGRESS_SEGMENTS_DOWN * intermediateResult.progress);
            speedValueRelative = intermediateResult.downBitPerSecLog;
            break;
        
        case INIT_UP:
            progressSegments = PROGRESS_SEGMENTS_INIT + PROGRESS_SEGMENTS_PING + PROGRESS_SEGMENTS_DOWN;
            speedValueRelative = intermediateResult.downBitPerSecLog;
            break;
        
        case UP:
            progressSegments = PROGRESS_SEGMENTS_INIT + PROGRESS_SEGMENTS_PING + PROGRESS_SEGMENTS_DOWN
                    + Math.round(PROGRESS_SEGMENTS_UP * intermediateResult.progress);
            speedValueRelative = intermediateResult.upBitPerSecLog;
            break;
                    
        case SPEEDTEST_END:
        case QOS_TEST_RUNNING:
            progressSegments = PROGRESS_SEGMENTS_INIT + PROGRESS_SEGMENTS_PING + PROGRESS_SEGMENTS_DOWN
                    + PROGRESS_SEGMENTS_UP;
            speedValueRelative = intermediateResult.upBitPerSecLog;
            testView.setSpeedValue(0d);
            testView.setSpeedString("");
            qosMode = true;
        	
            break;
        
        case ERROR:
        case ABORTED:
            progressSegments = 0;
            resetGraph();
            
            if (intermediateResult.status == TestStatus.ERROR) // && !ConfigHelper.isRepeatTest(getActivity()))
            {
                if (! rmbtService.isLoopMode())
                    showErrorDialog(R.string.test_dialog_error_text);
                return;
            }
            
        default:
        	break;
        }

        testView.setSpeedValue(speedValueRelative);

        updateSignalUI();

        final double progressValue = (double) progressSegments / PROGRESS_SEGMENTS_PROGRESS_RING;
        final double correctProgressValue = testView.setProgressValue(progressValue);
        final double totalProgressValue = (double) (correctProgressValue * PROGRESS_SEGMENTS_PROGRESS_RING) / (double)PROGRESS_SEGMENTS_TOTAL;
        testView.setProgressString(PERCENT_FORMAT.format(totalProgressValue));
        
        final String initStr;
        
        if (intermediateResult.initNano < 0) {
            initStr = "-";
        }
        else {
            initStr = INIT_FORMAT.format(intermediateResult.initNano / 1000000.0);
        }
        
        if (getSpeedTestStatus() != null) {
        	getSpeedTestStatus().setResultInitString(initStr,
        			intermediateResult.status.equals(TestStatus.INIT) ?
        			SpeedTestStatViewController.FLAG_SHOW_PROGRESSBAR : SpeedTestStatViewController.FLAG_HIDE_PROGRESSBAR);	
        }
        
        final String pingStr;
        if (intermediateResult.pingNano < 0) {
            pingStr = "...";
        }
        else {
            pingStr = PING_FORMAT.format(intermediateResult.pingNano / 1000000.0);
        }
        
        if (getSpeedTestStatus() != null) {
        	getSpeedTestStatus().setResultPingString(pingStr,
        			intermediateResult.status.equals(TestStatus.PING) ?
        			SpeedTestStatViewController.FLAG_SHOW_PROGRESSBAR : SpeedTestStatViewController.FLAG_HIDE_PROGRESSBAR);
        }

        if (intermediateResult.status.ordinal() > TestStatus.PING.ordinal() && testView != null && !"...".equals(pingStr)) {
            testView.setTestStatusProgress(TestStatus.PING, intermediateResult.pingNano, pingStr);
        }
        
        final String downStr;
        if (intermediateResult.downBitPerSec < 0) {
            downStr = "...";
        }
        else {
            downStr = SPEED_FORMAT.format(intermediateResult.downBitPerSec / 1000000.0);
        }

        if (intermediateResult.status.ordinal() > TestStatus.DOWN.ordinal() && testView != null && !"...".equals(downStr)) {
            testView.setTestStatusProgress(TestStatus.DOWN, intermediateResult.downBitPerSecLog, downStr);
        }

        if (getSpeedTestStatus() != null && !"...".equals(downStr)) {
        	getSpeedTestStatus().setResultDownString(downStr,
        			intermediateResult.status.equals(TestStatus.DOWN) ?
        					SpeedTestStatViewController.FLAG_SHOW_PROGRESSBAR : SpeedTestStatViewController.FLAG_HIDE_PROGRESSBAR);
        }
        
        final String upStr;
        if (intermediateResult.upBitPerSec < 0) {
            upStr = "...";
        }
        else {
            upStr = SPEED_FORMAT.format(intermediateResult.upBitPerSec / 1000000.0);
        }

        if (intermediateResult.status.ordinal() > TestStatus.UP.ordinal() && testView != null && !"...".equals(upStr)) {
            testView.setTestStatusProgress(TestStatus.UP, intermediateResult.upBitPerSecLog, upStr);
        }
        
        if (getSpeedTestStatus() != null) {
        	getSpeedTestStatus().setResultUpString(upStr, 
        			intermediateResult.status.equals(TestStatus.UP) || intermediateResult.status.equals(TestStatus.INIT_UP) ? 
        					SpeedTestStatViewController.FLAG_SHOW_PROGRESSBAR : SpeedTestStatViewController.FLAG_HIDE_PROGRESSBAR);	
        }

        switch(intermediateResult.status) {
            case INIT:
                testView.setSpeedString("INIT");
                break;
            case PING:
                testView.setSpeedString("PING");
                break;
            case DOWN:
            case INIT_UP:
                testView.setSpeedString(downStr);
                break;
            case UP:
                testView.setSpeedString(upStr);
                break;
        }
        
        testView.setTestStatus(intermediateResult.status);
        
        testView.invalidate();
    }

    private boolean updateSignalUI() {
        Integer signal = rmbtService.getSignal();
        int signalType = rmbtService.getSignalType();

        if (signal == null || signal == 0) {
            signalType = InformationCollector.SINGAL_TYPE_NO_SIGNAL;
        }

        boolean signalTypeChanged = false;

        if (signalType != InformationCollector.SINGAL_TYPE_NO_SIGNAL) {
            signalTypeChanged = lastSignalType != signalType;
            lastSignal = signal;
            lastSignalType = signalType;
        }

        if (signalType == InformationCollector.SINGAL_TYPE_NO_SIGNAL && lastSignalType != InformationCollector.SINGAL_TYPE_NO_SIGNAL) {
            // keep old signal if we had one before
            signal = lastSignal;
            signalType = lastSignalType;
        }

        Double relativeSignal = null;
        MinMax<Integer> signalBoungs = NetworkUtil.getSignalStrengthBounds(signalType);
        if (! (signalBoungs.min == Integer.MIN_VALUE || signalBoungs.max == Integer.MAX_VALUE)) {
            relativeSignal = (double) (signal - signalBoungs.min) / (double) (signalBoungs.max - signalBoungs.min);
        }

        testView.setSignalType(signalType);
        if (signal != null) {
            testView.setSignalString(String.valueOf(signal));

            String signalString = String.valueOf(signal) + " " + getResources().getString(R.string.test_dbm);
            if (signalType == InformationCollector.SINGAL_TYPE_RSRP) {
                Integer signalRsrq = rmbtService.getSignalRsrq();
                if (signalRsrq != null) {
                    signalString = "RSRP: " + String.valueOf(signal) + " "
                            + getResources().getString(R.string.test_dbm) + ", RSRQ: " + String.valueOf(signalRsrq) + " dB";
                }
                else {
                    signalString = "RSRP: " + String.valueOf(signal) + " "
                            + getResources().getString(R.string.test_dbm);
                }
            }

            if (networkInfoView != null) {
                networkInfoView.setSignalStrength(signalString);
            }

        }
        if (relativeSignal != null) {
            testView.setSignalValue(relativeSignal);
        }

        return signalTypeChanged;
    }
    
    public boolean onBackPressed()
    {
        if (rmbtService == null || !rmbtService.isTestRunning())
            return false;
        return onBackPressedHandler();
    }
    
    public boolean onBackPressedHandler()
    {
    	Log.d("RMBTTestFragment", "onbackpressed");
        final RMBTMainActivity activity = (RMBTMainActivity) getActivity();
        if (activity == null)
            return false;
        if ((errorDialog != null && errorDialog.isShowing())
                ||
            (progressDialog != null && progressDialog.isShowing()))
        {
            if (rmbtService != null)
                rmbtService.stopTest();
            else
            {
                // to be sure test is stopped:
                final Intent service = new Intent(RMBTService.ACTION_ABORT_TEST, null, context, RMBTService.class);
                context.startService(service);
            }
            dismissDialogs();
            activity.getSupportFragmentManager().popBackStack();
            return true;
        }
        
        
        if (abortDialog != null && abortDialog.isShowing())
        {
            dismissDialog(abortDialog);
            abortDialog = null;
        }
        else
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.test_dialog_abort_title);
            builder.setMessage(R.string.test_dialog_abort_text);
            builder.setPositiveButton(R.string.test_dialog_abort_yes, new OnClickListener()
            {
                @Override
                public void onClick(final DialogInterface dialog, final int which)
                {
                    if (rmbtService != null)
                        rmbtService.stopTest();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            builder.setNegativeButton(R.string.test_dialog_abort_no, null);
            
            dismissDialogs();
            abortDialog = builder.show();
        }
        return true;
    }

    
    protected void showErrorDialog(int errorMessageId)
    {
        stopLoop = true;
        
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.test_dialog_error_title);
        builder.setMessage(errorMessageId);
        builder.setNeutralButton(android.R.string.ok, null);
        dismissDialogs();
        errorDialog = builder.create();
        errorDialog.setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                final RMBTMainActivity activity = (RMBTMainActivity) getActivity();
                if (activity != null)
                    activity.getSupportFragmentManager().popBackStack();
            }
        });
        errorDialog.show();
    }
    
    private void switchToResult()
    {
        if (!isVisible()) {
            return;
        }
        
        if (showQoSErrorToast)
        {
            final Toast toast = Toast.makeText(getActivity(), R.string.test_toast_error_text_qos, Toast.LENGTH_LONG);
            toast.show();
        }
        
        dismissDialogs();
        
        final String testUuid = rmbtService == null ? null : rmbtService.getTestUuid();
        if (testUuid == null || (intermediateResult == null && lastIntermediateResult == null))
        {
        	showErrorDialog(R.string.test_dialog_error_text);
        	return;
        }

        final IntermediateResult tempResult = intermediateResult == null ? lastIntermediateResult : intermediateResult;
        
        //((RMBTMainActivity) getActivity()).showResultsAfterTest(testUuid);
        if (tempResult.status != TestStatus.ERROR) {
            final IlrSimpleResultFragment.IlrTestResult testResult = new IlrSimpleResultFragment.IlrTestResult();
            testResult.setSpeedUp(SPEED_FORMAT.format(tempResult.upBitPerSec / 1000000.0));
            testResult.setSpeedUpLog(tempResult.upBitPerSecLog);
            testResult.setSpeedDown(SPEED_FORMAT.format(tempResult.downBitPerSec / 1000000.0));
            testResult.setSpeedDownLog(tempResult.downBitPerSecLog);
            testResult.setPing(PING_FORMAT.format(tempResult.pingNano / 1000000.0));
            testResult.setPingNs(tempResult.pingNano);
            testResult.setSignalStrength(networkInfoView.getSignalStrength());
            testResult.setNetworkName(networkInfoView.getNetworkName());
            if (!NetworkFamilyEnum.UNKNOWN.equals(lastNetworkFamily) && !lastNetworkTypeString.equals(lastNetworkFamily.getNetworkFamily())) {
                testResult.setNetworkType(lastNetworkFamily.getNetworkFamily() + "/" + lastNetworkTypeString);
            }
            else {
                testResult.setNetworkType(lastNetworkTypeString);
            }
            ((RMBTMainActivity) getActivity()).showStartScreenAfterTestWithoutSwitchingToResults(testUuid,
                    rmbtService.getIntermediateResult(tempResult), testResult);
        }
    }
    

    private ProgressView extendedProgressView;
    private Button extendedResultButtonCancel;
    private Button extendedButtonDetails;
    
    private QoSTestEnum lastQoSTestStatus;

    public void updateQoSUI() {
    	
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
        
        QoSTestEnum status = null;
        float progress = 0f;

    	try {
            if (rmbtService != null)
            {
                QoSTestEnum _status = rmbtService.getQoSTestStatus();
                if (_status == null) {
                    _status = lastQoSTestStatus == null ? QoSTestEnum.START : lastQoSTestStatus;	
                }
                status = _status;
                lastQoSTestStatus = status;
                progress = rmbtService.getQoSTestProgress();
            }
            else
            {
//                        Log.d(DEBUG_TAG, "we have no service");
                QoSTestEnum _status = lastQoSTestStatus;
                if (_status == null)
                    _status = QoSTestEnum.START;
                if (_status == QoSTestEnum.QOS_RUNNING)
                    _status = QoSTestEnum.STOP;
                
                status = _status;
            }
            
//            Log.d(" DEBUG TEST", String.format("status: %s", status == null ? "null" : status.toString()));
            
            switch (status)
            {
            case START:
            case ERROR:
                progress = 0f;
                break;
            
            case STOP:
                progress = 1f;
                break;
                
            default:
                break;
            }
            
            double progressSegments = 0;
            
            if (extendedProgressView != null) {
                extendedProgressView.setProgress((progress/rmbtService.getQoSTestSize()));
            }

            
            switch (status)
            {
            case START:
                progressSegments = Math.round(PROGRESS_SEGMENTS_QOS * progress / rmbtService.getQoSTestSize());
                break;
            
            case QOS_RUNNING:
                progressSegments = Math.round(PROGRESS_SEGMENTS_QOS * (float)(progress / rmbtService.getQoSTestSize()));
                break;

            case QOS_FINISHED:
            case NDT_RUNNING:
            	progressSegments = PROGRESS_SEGMENTS_QOS - 1;
            	progressSegments = PROGRESS_SEGMENTS_QOS - 1;
            	break;
            	
            case STOP:
                progressSegments = PROGRESS_SEGMENTS_QOS;
                break;
            
            case ERROR:
            default:
                break;
            }	    

            updateSignalUI();

            final double progressValue = progressSegments / PROGRESS_SEGMENTS_QOS;
            testView.setSpeedValue(progressValue);
            
            //final double totalProgressValue = (double) ((double)(PROGRESS_SEGMENTS_PROGRESS_RING + progressSegments) / (double)PROGRESS_SEGMENTS_TOTAL);
            final double totalProgressValue = (progressSegments / (double)PROGRESS_SEGMENTS_QOS);
            testView.setProgressValue(totalProgressValue);
            testView.setProgressString(PERCENT_FORMAT.format(totalProgressValue));
            testView.invalidate();
            
            if (status == QoSTestEnum.QOS_RUNNING && extendedResultButtonCancel != null && extendedResultButtonCancel.getVisibility() == View.GONE)
            {
                extendedResultButtonCancel.setVisibility(View.VISIBLE);
                if (extendedButtonDetails != null)
                extendedButtonDetails.setVisibility(View.GONE);
            }

            if (qosProgressView != null && qosProgressView.getVisibility()!=View.VISIBLE 
        			&& rmbtService != null && rmbtService.getQoSTest() != null) {
        		final GroupCountView groupCountView = new GroupCountView(getMainActivity());
        		qosProgressView.setVisibility(View.VISIBLE);
        		//register group counter view as a test progress listener:
        		rmbtService.getQoSTest().getTestSettings().addTestProgressListener(groupCountView);
        		groupCountView.setTaskMap(rmbtService.getQoSTest().getTestMap());
        		groupCountContainerView.addView(groupCountView);
        		groupCountContainerView.invalidate();
        		((GroupCountView) groupCountContainerView.getChildAt(0)).setNdtProgress(rmbtService.getNDTProgress());
        		if (infoView != null) {
        			infoView.setVisibility(View.GONE);
        		}
        	}
        	else if (qosProgressView != null 
        			&& qosProgressView.getVisibility()==View.VISIBLE && rmbtService.getQoSGroupCounterMap() != null) {
        		((GroupCountView) groupCountContainerView.getChildAt(0)).setTaskMap(rmbtService.getQoSTest().getTestMap());
        		((GroupCountView) groupCountContainerView.getChildAt(0)).setNdtProgress(rmbtService.getNDTProgress());
        		((GroupCountView) groupCountContainerView.getChildAt(0)).setQoSTestStatus(rmbtService.getQoSTestStatus());
        		((GroupCountView) groupCountContainerView.getChildAt(0)).updateView(rmbtService.getQoSGroupCounterMap());
        	}
            
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	finally
    	{
    	    if (status != null && status == QoSTestEnum.ERROR)
    	        showQoSErrorToast = true;
    	}
    }
    
	public ChangeableSpeedTestStatus getSpeedTestStatus() {
		return speedTestStatViewController;
	}
}
