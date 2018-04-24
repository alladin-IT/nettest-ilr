package at.alladin.rmbt.android.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

import at.alladin.openrmbt.android.BuildConfig;
import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.test.IlrSimpleTestFragment;
import at.alladin.rmbt.android.test.TestViewable;
import at.alladin.rmbt.android.util.ConfigHelper;
import at.alladin.rmbt.client.helper.TestStatus;

/**
 * CADLabProgView
 * Created by fk on 1/9/18.
 */

public class CanvasArcDoubleGaugeWithLabelsAndProgressBarView extends View implements TestViewable {

    private final String[] speedLabels = new String[] {"", "1Mb", "10Mb", "100Mb", "1Gb"};

    private String[] currentProgressLabels; //{"init", "ping", "down", "up"};//, "qos"};

    private String[] allProgressLabels;

    private final static int SMALL_POINTER_SIZE = 9;

    private final static int REALLY_SMALL_POINTER_SIZE = SMALL_POINTER_SIZE / 3;

    private final static int BIG_POINTER_SIZE = 9;

    private final static float ARC_ANGLE = 270f;

    private final static boolean IS_TOP_STATUS_BAR_VISIBLE = true;

    private final static float MIN_PROGRESS_VALUE = 0.07f;

    private Paint arcPaint;
    private Paint textPaint;
    private Paint unitPaint;
    private Paint bitmapPaint;

    private View statusView;
    private View topStatusBarView;
    private View topBarSpeedView;
    private TextView topBarSpeedTextView;
    private TextView topBarSpeedIconTextView;
    private TextView topBarProgressTextView;
    //the view for the speed text inside the button that started the test
    private TextView buttonSpeedTextView;

    private double achievedDl = -1;
    private double achievedUl = -1;
    private double achievedPing = -1;
    private double achievedProgress = -1;

    private ProgressBar progressBar;
    private TextView progressBarInit;
    private TextView progressBarPing;
    private TextView progressBarDown;
    private TextView progressBarUp;
    private TextView progressBarQos;

    private TextView progressGaugePing;
    private TextView progressGaugeDown;
    private TextView progressGaugeUp;

    private AlladinTextView progressGaugePingSymbol;
    private AlladinTextView progressGaugeDownSymbol;
    private AlladinTextView progressGaugeUpSymbol;

    //Pre initialize the draw() objects for INSANE performance gains!
    private CanvasArcDoubleGaugeWithLabelsAndProgressBarView.RingArc innerRing;
    private CanvasArcDoubleGaugeWithLabelsAndProgressBarView.RingArc ulSpeedRing;
    private CanvasArcDoubleGaugeWithLabelsAndProgressBarView.RingArc dlSpeedRing;

    private double speedProgressValue = 0f;
    private String speedString = "";
    private double progressValue = 0f;
    private String progressString = "";
    private boolean turningEnabled = false;
    private int turnVal = 0, count = 0;
    private int colVal = 0, colMod = 12, colValMax = 220, colValMin = 50, modR = 1, modG = 1, modB = 1;

    int requestedCanvasHeight = 0;
    int requestedCanvasWidth  = 0;

    int defaultHeight = 100;
    int defaultWidth = 100;

    int centerX = 50;
    int centerY = 50;

    int bigPointerSize = BIG_POINTER_SIZE;
    int smallPointerSize = SMALL_POINTER_SIZE;

    float translateStatusY = 0;
    double speedValueRelative = 0;

    float singleArcAngle = 0f;

    boolean isQosEnabled;

    TestStatus currentTestStatus = TestStatus.WAIT;

    String upString;
    String downString;
    String pingString;


    public CanvasArcDoubleGaugeWithLabelsAndProgressBarView(Context context) {
        super(context);
    }

    public CanvasArcDoubleGaugeWithLabelsAndProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasArcDoubleGaugeWithLabelsAndProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initialize() {
        isQosEnabled =  ConfigHelper.isQosEnabled(getContext());
        //we need to re-init the currentProgresslabels here, for a change in settings would otherwise NOT reflect a change in the gauge (only after switching to another fragment)
        //In order to prevent a lot of String Building, the allProgressLabels array is constructed once on startup (and then just copied)
        currentProgressLabels = new String[isQosEnabled ? 5 : 4];
        System.arraycopy(allProgressLabels, 0, currentProgressLabels, 0, isQosEnabled ? 5 : 4);

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(coordFW(SMALL_POINTER_SIZE, defaultWidth));
        arcPaint.setAntiAlias(true);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getResources().getColor(R.color.app_text_color));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(coordFH(20, defaultHeight));

        unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setColor(getResources().getColor(R.color.app_text_color));
        unitPaint.setTextAlign(Paint.Align.CENTER);
        unitPaint.setTextSize(coordFH(8, defaultHeight));

        bitmapPaint = new Paint();
        bitmapPaint.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.app_text_color), PorterDuff.Mode.SRC_ATOP));
        bitmapPaint.setFilterBitmap(true);

        bigPointerSize = (int)coordFH(BIG_POINTER_SIZE, defaultHeight);
        smallPointerSize = (int)coordFH(SMALL_POINTER_SIZE, defaultHeight);

        upString = getResources().getString(R.string.ifont_up);
        downString = getResources().getString(R.string.ifont_down);
        pingString = getResources().getString(R.string.ifont_ping);

        translateStatusY = coordFH(-5, defaultHeight);

        singleArcAngle = ARC_ANGLE;
        innerRing = new CanvasArcDoubleGaugeWithLabelsAndProgressBarView.RingArc(centerX, centerY, SMALL_POINTER_SIZE, .350f);
        ulSpeedRing = new CanvasArcDoubleGaugeWithLabelsAndProgressBarView.RingArc(centerX, centerY, arcPaint.getStrokeWidth(), .650f);
        dlSpeedRing = new CanvasArcDoubleGaugeWithLabelsAndProgressBarView.RingArc(centerX, centerY, arcPaint.getStrokeWidth(), .925f);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        isQosEnabled =  ConfigHelper.isQosEnabled(getContext());
        allProgressLabels = new String[5];

        allProgressLabels[0] = getResources().getString(R.string.test_gauge_label_init);
        allProgressLabels[1] = getResources().getString(R.string.test_gauge_label_ping);
        allProgressLabels[2] = getResources().getString(R.string.test_gauge_label_down);
        allProgressLabels[3] = getResources().getString(R.string.test_gauge_label_up);
        //QOS shall always be the last elem
        allProgressLabels[allProgressLabels.length - 1] = getResources().getString(R.string.test_gauge_label_qos);

        for(int i = 0; i < speedLabels.length; i++) {
            speedLabels[i] = this.getStringWithWhitespace(speedLabels[i], 1);
        }
        for(int i = 0; i < allProgressLabels.length; i++) {
            allProgressLabels[i] = this.getStringWithWhitespace(allProgressLabels[i], 2);
        }

        if (!ConfigHelper.isSAPEnabled(getContext())) {
            colMod = 0;
        }
        else {
            final Random rnd = new Random();
            modR = rnd.nextInt(3)+1;
            modG = rnd.nextInt(3)+1;
            modB = rnd.nextInt(3)+1;
        }
    }

    protected float coordFW(final float x, final float y) {
        return x / y * (float)requestedCanvasWidth;
    }

    protected float coordFH(final float x, final float y) {
        return x / y * (float)requestedCanvasHeight;
    }

    protected float coordFW(final int x, final int y) {
        return (float) x / y * requestedCanvasWidth;
    }

    protected float coordFH(final int x, final int y) {
        return (float) x / y * requestedCanvasHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // extract their requested height and width.
        int requestedWidth  = MeasureSpec.getSize(widthMeasureSpec);
        int requestedHeight = MeasureSpec.getSize(heightMeasureSpec);

        // make sure we're a square by re-writing the requested dimensions, favoring the smaller of the two as the new h & w of square.
        if (requestedHeight != requestedWidth) {

            // overwrite the larger of the dimensions.
            if (requestedWidth > requestedHeight)
                requestedWidth = requestedHeight;
            else
                requestedHeight = requestedWidth;
        }

        // set the local member variables to the newly discovered desired dimensions.
        requestedCanvasHeight = requestedHeight;
        requestedCanvasWidth  = requestedWidth;

        // calculate the origin.
        centerX = requestedCanvasHeight / 2;
        centerY = requestedCanvasWidth  / 2;

        // spit back the requested dimensions as our accepted dimensions. We'll do our best.
        setMeasuredDimension(requestedWidth, requestedHeight);

        // this seems like a good place to initialize things.
        initialize();
    }

    private static class RingArc {
        final RectF bounds;
        final Path path = new Path();
        final int startX;
        final int startY;
        final float strokeWidth;

        public RingArc(final int arcCenterX, final int arcCenterY, final float strokeWidth, final float absPosition) {
            final int gaugeWidth = (int)((float) arcCenterX * absPosition) - BIG_POINTER_SIZE;
            final int gaugeHeight = (int)((float) arcCenterY * absPosition) - BIG_POINTER_SIZE;
            bounds = new RectF(arcCenterX - gaugeWidth + strokeWidth/2f, arcCenterY - gaugeHeight + strokeWidth/2f,
                    arcCenterX + gaugeWidth - strokeWidth/2f, arcCenterY + gaugeHeight - strokeWidth/2f);
            startX = arcCenterX - gaugeWidth;
            startY = arcCenterY;
            path.addArc(getBounds(), 0, ARC_ANGLE);
            this.strokeWidth = strokeWidth;
        }

        public RectF getBounds() {
            return bounds;
        }

        public RectF getBounds(float offset) {
            final RectF newRect = new RectF(bounds);
            newRect.inset(offset, offset);
            return newRect;
        }

        public float getStrokeWidth() {
            return this.strokeWidth;
        }

        public Path getPath() {
            return path;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (count > 10/*Integer.MAX_VALUE*/) { //make it impossible to reach that number
            this.turningEnabled = true;
        }
        final int saveCount = canvas.save();
//        canvas.save();

        if (turningEnabled) {
            this.turnVal = (turnVal + 4) % 360;
            if (colMod != 0) {
                this.colVal += colMod;
                if (this.colVal > this.colValMax) {
                    colMod *= -1;
                    this.colVal = this.colValMax;
                } else if (this.colVal < this.colValMin) {
                    colMod *= -1;
                    this.colVal = this.colValMin;
                }
            }
        }
        canvas.rotate(45 + ((360f-ARC_ANGLE)/2f) + turnVal, centerX, centerY);

        arcPaint.setColor(getResources().getColor(R.color.calm_gauge_speed_background));
        if (turningEnabled && colVal > 0) {
            arcPaint.setColor((255 << 24) | ((colVal/modR) << 16) | ((colVal/modG) << 8) | ((colVal/modB)));
        }

        canvas.drawArc(ulSpeedRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);
        arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_background));
        if (turningEnabled && colVal > 0) {
            arcPaint.setColor((255 << 24) | ((colVal/modR) << 16) | ((colVal/modG) << 8) | ((colVal/modB)));
        }

        canvas.drawArc(dlSpeedRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);
        //TODO: get ColorUtils going to make it pulse
        if (currentTestStatus == TestStatus.PING) {
            arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_foreground));
        } else {
            arcPaint.setColor(getResources().getColor(R.color.calm_gauge_speed_foreground));
        }
        final float tempStrokeWidth = arcPaint.getStrokeWidth();
        arcPaint.setStrokeWidth(SMALL_POINTER_SIZE);
        canvas.drawArc(innerRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);

        arcPaint.setStrokeWidth(tempStrokeWidth);

        arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_foreground));

        switch(currentTestStatus) {
            case INIT:
            case PING:
                setStatusText(getResources().getString(R.string.ifont_ping));
                break;
            case DOWN:
            case INIT_UP:
                setStatusText(getResources().getString(R.string.ifont_down));
                arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_foreground));
                canvas.drawArc(dlSpeedRing.getBounds(), 0, (float)(ARC_ANGLE*speedValueRelative), false, arcPaint);
                break;
            case UP:
                setStatusText(getResources().getString(R.string.ifont_up));
                arcPaint.setColor(getResources().getColor(R.color.calm_gauge_speed_foreground));
                canvas.drawArc(ulSpeedRing.getBounds(), 0, (float)(ARC_ANGLE*speedValueRelative), false, arcPaint);
                break;
            case SPEEDTEST_END:
            case QOS_TEST_RUNNING:
            case QOS_END:
                if (ConfigHelper.isQosEnabled(getContext())) {
                    //show QoS progress only if qos has been enabled in the settings
                    setStatusText(getResources().getString(R.string.ifont_qos));
                }
                break;
        }

        //draw the speed bars @ the levels they previously achieved
        if (BuildConfig.TEST_LEAVE_SPEED_GAUGE_FILLED_AFTER_TEST) {
            if (achievedUl != -1) {
                arcPaint.setColor(getResources().getColor(R.color.calm_gauge_speed_foreground));
                canvas.drawArc(ulSpeedRing.getBounds(), 0, (float) (ARC_ANGLE * Math.max(0D, achievedUl)), false, arcPaint);
            }
            if (achievedDl != -1) {
                arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_foreground));
                canvas.drawArc(dlSpeedRing.getBounds(), 0, (float) (ARC_ANGLE * Math.max(0D, achievedDl)), false, arcPaint);
            }
        }



        final Paint textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.calm_text_color));
        textPaint.setTextSize(coordFH(4, defaultHeight));

        /////////////////////////////////
        //  ARCS

        final float radSpeed = ((float) (2f*Math.PI*(dlSpeedRing.getBounds().width()/2f))) * (ARC_ANGLE/360f);
        final float radSpeedPart = radSpeed / (speedLabels.length-1);

        arcPaint.setColor(getResources().getColor(R.color.app_background));

        for (int i = 0; i < speedLabels.length; i++) {
            if (i != 0 && i < (speedLabels.length-1)) {
                canvas.drawTextOnPath(speedLabels[i], dlSpeedRing.getPath(), i * radSpeedPart - (textPaint.measureText(speedLabels[i]) / 2) , arcPaint.getStrokeWidth() * 11 / 12, textPaint);
            }
            else if (i == 0){
                canvas.drawTextOnPath(speedLabels[i], dlSpeedRing.getPath(), i * radSpeedPart + coordFW(2, defaultWidth), arcPaint.getStrokeWidth()  * 11 / 12, textPaint);
            }
            else {
                canvas.drawTextOnPath(speedLabels[i], dlSpeedRing.getPath(), i * radSpeedPart - textPaint.measureText(speedLabels[i]) - coordFW(2, defaultWidth), arcPaint.getStrokeWidth()  * 11 / 12, textPaint);
            }
        }

        arcPaint.setStrokeWidth(coordFW(SMALL_POINTER_SIZE, defaultWidth));
        for (int i = 1; i < speedLabels.length - 1; i++) {
            canvas.drawArc(dlSpeedRing.getBounds(), i * (ARC_ANGLE / (speedLabels.length-1)), 1f, false, arcPaint);
            canvas.drawArc(ulSpeedRing.getBounds(), (i) * (ARC_ANGLE / (speedLabels.length - 1)), 1f, false, arcPaint);
        }

        canvas.rotate(45 + ((360f-ARC_ANGLE)/2f), centerX, centerY);

        canvas.restore();

        canvas.restoreToCount(saveCount);

    }

    private void setStatusText(final String statusText) {
        if (statusView != null && statusView instanceof TextView) {
            if ("QoS".equals(statusText)) {
                ((TextView) statusView).setTypeface(Typeface.SANS_SERIF);
            }
            ((TextView) statusView).setText(statusText);
        }
    }

    @Override
    public void setSpeedString(String speedString) {
        this.speedString = speedString;
    }

    @Override
    public void setSpeedValue(double speedValueRelative) {
        this.speedValueRelative = Math.max(speedValueRelative, 0d);
    }

    @Override
    public double setSpeedProgressValue(double speedProgressValue) {
        //TODO: Did we do nothing on purpose?
        //speedProgressValue = speedProgressValue;
        this.speedProgressValue = speedProgressValue;
        return speedProgressValue;
    }

    @Override
    public double setProgressValue(double progressValue) {
        final double tempProgressValue = progressValue;
        //actually don't think this is necessary, as the currentTestStatus is set to End anyway
        if (this.achievedProgress != -1) {
            progressValue = this.achievedProgress;
        }
        switch (currentTestStatus) {
            case INIT:
                progressValue = ((progressValue * IlrSimpleTestFragment.PROGRESS_SEGMENTS_PROGRESS_RING)
                        / (float) IlrSimpleTestFragment.PROGRESS_SEGMENTS_INIT * 50d) / 200d;
                break;
            case PING:
                if (progressBarInit != null) {
                    progressBarInit.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (progressGaugePing != null) {
                    progressGaugePing.setText(R.string.test_idle);
                }
                progressValue = (((progressValue * IlrSimpleTestFragment.PROGRESS_SEGMENTS_PROGRESS_RING) - IlrSimpleTestFragment.PROGRESS_SEGMENTS_INIT)
                        / (float) (IlrSimpleTestFragment.PROGRESS_SEGMENTS_PING) * 50d);
                progressValue += 50d;
                progressValue /= 200d;
                break;
            case DOWN:
            case INIT_UP:
                if (progressGaugeDown != null && this.achievedDl < 0) {
                    progressGaugeDown.setText(R.string.test_idle);
                }
                if (progressBarPing != null) {
                    progressBarPing.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (buttonSpeedTextView != null && this.speedString != null && !this.speedString.equals("")) {
                    buttonSpeedTextView.setText(this.speedString + " " + getResources().getString(R.string.test_mbps));
                }
                progressValue = (((progressValue * IlrSimpleTestFragment.PROGRESS_SEGMENTS_PROGRESS_RING) - IlrSimpleTestFragment.PROGRESS_SEGMENTS_INIT - IlrSimpleTestFragment.PROGRESS_SEGMENTS_PING)
                        / (float) (IlrSimpleTestFragment.PROGRESS_SEGMENTS_DOWN) * 50d);
                progressValue += 100d;
                progressValue /= 200d;
                break;
            case UP:
                if (progressGaugeUp != null && this.achievedUl < 0) {
                    progressGaugeUp.setText(R.string.test_idle);
                }
                if (progressBarDown != null) {
                    progressBarDown.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (buttonSpeedTextView != null && this.speedString != null && !this.speedString.equals("")) {
                    buttonSpeedTextView.setText(this.speedString + " " + getResources().getString(R.string.test_mbps));
                }
                progressValue = (((progressValue * IlrSimpleTestFragment.PROGRESS_SEGMENTS_PROGRESS_RING) - IlrSimpleTestFragment.PROGRESS_SEGMENTS_INIT - IlrSimpleTestFragment.PROGRESS_SEGMENTS_PING - IlrSimpleTestFragment.PROGRESS_SEGMENTS_DOWN)
                        / (float) (IlrSimpleTestFragment.PROGRESS_SEGMENTS_UP) * 50d);
                progressValue += 150d;
                progressValue /= 200d;
                break;
            case QOS_TEST_RUNNING:
            case SPEEDTEST_END:
                if (progressBarUp != null) {
                    progressBarUp.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (isQosEnabled && buttonSpeedTextView != null) {
                    buttonSpeedTextView.setText(IlrSimpleTestFragment.PERCENT_FORMAT.format(progressValue));
                }
                break;
            case QOS_END:
            case END:
                if (progressBarUp != null) {
                    progressBarUp.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (progressBarQos != null) {
                    progressBarQos.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (buttonSpeedTextView != null) {
                    buttonSpeedTextView.setText("");
                }
                break;
        }

        if(!isQosEnabled && currentTestStatus.ordinal() < TestStatus.SPEEDTEST_END.ordinal()) {
            this.progressValue = progressValue;
        } else if (isQosEnabled && currentTestStatus.ordinal() < TestStatus.SPEEDTEST_END.ordinal()) {
            this.progressValue = progressValue * (4d/5d);
        } else if (isQosEnabled && currentTestStatus.ordinal() < TestStatus.QOS_END.ordinal()) {
            this.progressValue = Math.min(progressValue, .9f) * (1d/5d) + (4d/5d);
        } else {
            this.progressValue = 1d;
        }

        if (progressBar != null) {
            progressBar.setProgress((int) (this.progressValue * 100));
        }

        return tempProgressValue;
    }

    @Override
    public void setSignalValue(double relativeSignal) {

    }

    @Override
    public void setHeaderString(String headerString) {

    }

    @Override
    public void setSubHeaderString(String subHeaderString) {

    }

    @Override
    public void setProgressString(String progressString) {
        this.progressString = progressString;
    }

    @Override
    public void setTestStatus(TestStatus testStatus) {
        this.currentTestStatus = testStatus;
    }

    @Override
    public void setSignalString(String signalString) {

    }

    @Override
    public void setSignalType(int signalType) {

    }

    @Override
    public void setStatusIconView(View v) {
        this.statusView = v.findViewById(R.id.test_view_status_button_text);
        statusView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
            }
        });
        this.buttonSpeedTextView = (TextView) v.findViewById(R.id.simple_test_status_speed_text);
    }

    @Override
    public void recycle() {

    }

    @Override
    public void setTestStatusProgress(TestStatus testStatus, double logValue, String stringVal) {
        switch (testStatus) {
            case DOWN:
                this.achievedDl = logValue;
                if (progressGaugeDown != null) {
                    progressGaugeDown.setTextAppearance(getContext(), R.style.Alladin_Test_InactiveText);
                    progressGaugeDown.setText(stringVal + " " + getResources().getString(R.string.test_mbps));
                }
                break;
            case UP:
                this.achievedUl = logValue;
                if (progressGaugeUp != null) {
                    progressGaugeUp.setTextAppearance(getContext(), R.style.Alladin_Test_InactiveText);
                    progressGaugeUp.setText(stringVal + " " + getResources().getString(R.string.test_mbps));
                }
                break;
            case PING:
                if (progressGaugePing != null) {
                    progressGaugePing.setTextAppearance(getContext(), R.style.Alladin_Test_InactiveText);
                    progressGaugePing.setText(stringVal);
                }
                this.achievedPing = logValue;
                break;
            case END:
                //in the end case we have to change the color of the given progress icons
                if (progressBarInit != null) {
                    progressBarInit.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (progressBarUp != null) {
                    progressBarUp.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (progressBarDown != null) {
                    progressBarDown.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (progressBarPing != null) {
                    progressBarPing.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (progressBarQos != null) {
                    progressBarQos.setTextColor(getResources().getColor(R.color.app_background));
                }
                if (statusView != null && statusView instanceof TextView) {
                    ((TextView) statusView).setText("");
                }
                //change the text and symbol colours to black
                if (this.progressGaugeUp != null) {
                    this.progressGaugeUp.setTextColor(getResources().getColor(R.color.calm_text_color));
                }
                if (this.progressGaugeUpSymbol != null) {
                    this.progressGaugeUpSymbol.setTextColor(getResources().getColor(R.color.calm_text_color));
                }
                if (this.progressGaugeDown != null) {
                    this.progressGaugeDown.setTextColor(getResources().getColor(R.color.calm_text_color));
                }
                if (this.progressGaugeDownSymbol != null) {
                    this.progressGaugeDownSymbol.setTextColor(getResources().getColor(R.color.calm_text_color));
                }
                if (this.progressGaugePing != null) {
                    this.progressGaugePing.setTextColor(getResources().getColor(R.color.calm_text_color));
                }
                if (this.progressGaugePingSymbol != null) {
                    this.progressGaugePingSymbol.setTextColor(getResources().getColor(R.color.calm_text_color));
                }

                this.currentTestStatus = TestStatus.END;
                this.achievedProgress = 1.0d;
                this.setProgressValue(this.achievedProgress);
                break;
        }
    }

    @Override
    public void setTopStatusBar(View v) {
        this.topStatusBarView = v;

        //init top status bar
        if (IS_TOP_STATUS_BAR_VISIBLE && topStatusBarView != null) {
            topStatusBarView.setVisibility(View.VISIBLE);
            topBarSpeedView = topStatusBarView.findViewById(R.id.simple_test_status_top_speed);
            if (topBarSpeedView != null) {
                topBarSpeedView.setVisibility(View.INVISIBLE);
                topBarSpeedTextView = (TextView) topStatusBarView.findViewById(R.id.simple_test_status_top_speed_text);
                topBarSpeedIconTextView = (TextView) topStatusBarView.findViewById(R.id.simple_test_status_top_speed_icon);
            }

            topBarProgressTextView = (TextView) topStatusBarView.findViewById(R.id.simple_test_status_top_progress_text);
        }
    }

    @Override
    public void setProgressableElements(View v) {
        if (v != null) {
            this.progressBar = (ProgressBar) v.findViewById(R.id.ilr_progress_bar);
            final boolean isQosEnabled = ConfigHelper.isQosEnabled(getContext());
            //store the textviews inside the progress bar for changing the colors
            final View layout = isQosEnabled ? v.findViewById(R.id.ilr_progress_bar_container_qos) : v.findViewById(R.id.ilr_progress_bar_container);
            if (layout != null) {
                this.progressBarInit = (TextView) (isQosEnabled ? layout.findViewById(R.id.ilr_progress_bar_qos_init) : layout.findViewById(R.id.ilr_progress_bar_init));
                if (this.progressBarInit != null) {
                    this.progressBarInit.setText(R.string.test_gauge_label_init);
                }
                this.progressBarPing = (TextView) (isQosEnabled ? layout.findViewById(R.id.ilr_progress_bar_qos_ping) : layout.findViewById(R.id.ilr_progress_bar_ping));
                if (this.progressBarPing != null) {
                    this.progressBarPing.setText(R.string.test_gauge_label_ping);
                }
                this.progressBarDown = (TextView) (isQosEnabled ? layout.findViewById(R.id.ilr_progress_bar_qos_down) : layout.findViewById(R.id.ilr_progress_bar_down));
                if (this.progressBarDown != null) {
                    this.progressBarDown.setText(R.string.test_gauge_label_down);
                }
                this.progressBarUp = (TextView) (isQosEnabled ? layout.findViewById(R.id.ilr_progress_bar_qos_up) : layout.findViewById(R.id.ilr_progress_bar_up));
                if (this.progressBarUp != null) {
                    this.progressBarUp.setText(R.string.test_gauge_label_up);
                }
                this.progressBarQos = (TextView) (isQosEnabled ? layout.findViewById(R.id.ilr_progress_bar_qos_qos) : null);
                if (this.progressBarQos  != null) {
                    this.progressBarQos.setText(R.string.test_gauge_label_qos);
                }
                layout.setVisibility(View.VISIBLE);
            }
            //store the textviews next to the gauges to update values correctly
            this.progressGaugePing = (TextView) v.findViewById(R.id.ilr_gauge_value_ping);
            this.progressGaugeDown = (TextView) v.findViewById(R.id.ilr_gauge_value_down);
            this.progressGaugeUp = (TextView) v.findViewById(R.id.ilr_gauge_value_up);
            this.progressGaugePingSymbol = (AlladinTextView) v.findViewById(R.id.ilr_gauge_symbol_ping);
            this.progressGaugeDownSymbol = (AlladinTextView) v.findViewById(R.id.ilr_gauge_symbol_down);
            this.progressGaugeUpSymbol = (AlladinTextView) v.findViewById(R.id.ilr_gauge_symbol_up);
        }
    }

    private String getStringWithWhitespace(final String text, final int whiteSpaces) {
        final StringBuilder builder = new StringBuilder();
        final char[] textChars = text.toCharArray();
        for(int i = 0; i < textChars.length; i++) {
            builder.append(textChars[i]);
            if(i < (textChars.length - 1)) {
                for (int j = 0; j < whiteSpaces; j++) {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();
    }
}