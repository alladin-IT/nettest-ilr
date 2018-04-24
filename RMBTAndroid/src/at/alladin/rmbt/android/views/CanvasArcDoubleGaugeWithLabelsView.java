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

package at.alladin.rmbt.android.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.test.SimpleTestFragment;
import at.alladin.rmbt.android.test.TestViewable;
import at.alladin.rmbt.android.util.ConfigHelper;
import at.alladin.rmbt.client.helper.TestStatus;

/**
 * Created by lb on 07.06.16.
 */
public class CanvasArcDoubleGaugeWithLabelsView extends View implements TestViewable {

    private final String[] speedLabels = new String[] {"0", "1Mb", "10Mb", "100Mb", "1Gb"};

    private String[] currentProgressLabels; //{"init", "ping", "down", "up"};//, "qos"};

    private String[] allProgressLabels;

    private final static int SMALL_POINTER_SIZE = 9;

    private final static int REALLY_SMALL_POINTER_SIZE = SMALL_POINTER_SIZE / 3;

    private final static int BIG_POINTER_SIZE = 9;

    private final static float ARC_ANGLE = 240f;

    private final static boolean IS_TOP_STATUS_BAR_VISIBLE = true;

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


    private double speedProgressValue = 0f;
    private String speedString = "";
    private double progressValue = 0f;
    private String progressString = "";

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

    public CanvasArcDoubleGaugeWithLabelsView(Context context) {
        super(context);
    }

    public CanvasArcDoubleGaugeWithLabelsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasArcDoubleGaugeWithLabelsView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        public RingArc(final int arcCenterX, final int arcCenterY, final float strokeWidth, final float absPosition) {
            final int gaugeWidth = (int)((float) arcCenterX * absPosition) - BIG_POINTER_SIZE;
            final int gaugeHeight = (int)((float) arcCenterY * absPosition) - BIG_POINTER_SIZE;
            bounds = new RectF(arcCenterX - gaugeWidth + strokeWidth/2f, arcCenterY - gaugeHeight + strokeWidth/2f,
                    arcCenterX + gaugeWidth - strokeWidth/2f, arcCenterY + gaugeHeight - strokeWidth/2f);
            startX = arcCenterX - gaugeWidth;
            startY = arcCenterY;
            path.addArc(getBounds(), 0, ARC_ANGLE);
        }

        public RectF getBounds() {
            return bounds;
        }

        public RectF getBounds(float offset) {
            final RectF newRect = new RectF(bounds);
            newRect.inset(offset, offset);
            return newRect;
        }

        public Path getPath() {
            return path;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int saveCount = canvas.save();

        final RingArc speedRing = new RingArc(centerX, centerY, arcPaint.getStrokeWidth(), .625f);
        final RingArc progressRing = new RingArc(centerX, centerY, arcPaint.getStrokeWidth(), .9f);

        canvas.save();

        canvas.rotate(90 + ((360f-ARC_ANGLE)/2f), centerX, centerY);
        arcPaint.setColor(getResources().getColor(R.color.calm_gauge_speed_background));
        canvas.drawArc(speedRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);
        arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_background));
        canvas.drawArc(progressRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);

        arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_foreground));

        switch(currentTestStatus) {
            case INIT:
            case PING:
                setStatusText(getResources().getString(R.string.ifont_ping));
                canvas.drawText(pingString, centerX, centerY + textPaint.getTextSize()/2 + translateStatusY, textPaint);
                canvas.drawArc(progressRing.getBounds(), 0, (float)(ARC_ANGLE*progressValue), false, arcPaint);
                break;
            case DOWN:
            case INIT_UP:
                setStatusText(getResources().getString(R.string.ifont_down));
                canvas.drawText(downString, centerX, centerY + textPaint.getTextSize()/2 + translateStatusY, textPaint);
                canvas.drawArc(progressRing.getBounds(), 0, (float)(ARC_ANGLE*progressValue), false, arcPaint);
                arcPaint.setColor(getResources().getColor(R.color.calm_gauge_speed_foreground));
                canvas.drawArc(speedRing.getBounds(), 0, (float)(ARC_ANGLE*speedValueRelative), false, arcPaint);
                break;
            case UP:
                setStatusText(getResources().getString(R.string.ifont_up));
                canvas.drawText(upString, centerX, centerY + textPaint.getTextSize()/2 + translateStatusY, textPaint);
                canvas.drawArc(progressRing.getBounds(), 0, (float)(ARC_ANGLE*progressValue), false, arcPaint);
                arcPaint.setColor(getResources().getColor(R.color.calm_gauge_speed_foreground));
                canvas.drawArc(speedRing.getBounds(), 0, (float)(ARC_ANGLE*speedValueRelative), false, arcPaint);
                break;
            case SPEEDTEST_END:
            case QOS_TEST_RUNNING:
            case QOS_END:
                if (ConfigHelper.isQosEnabled(getContext())) {
                    //show QoS progress only if qos has been enabled in the settings
                    setStatusText(getResources().getString(R.string.ifont_qos));
                    /*
                    arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_foreground));
                    canvas.drawArc(progressRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);
                    arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_background));
                    canvas.drawArc(progressRing.getBounds(), 0, (float)(ARC_ANGLE*progressValue), false, arcPaint);
                    */
                    canvas.drawText(upString, centerX, centerY + textPaint.getTextSize()/2 + translateStatusY, textPaint);
                    canvas.drawArc(progressRing.getBounds(), 0, (float)(ARC_ANGLE*progressValue), false, arcPaint);
                }
                break;
        }


        final Paint textPaint = new Paint();
        //textPaint.setFakeBoldText(true);
        textPaint.setColor(getResources().getColor(R.color.app_background));
        textPaint.setTextSize(coordFH(4, defaultHeight));

        /////////////////////////////////
        //  SPEED ARC

        final float radSpeed = ((float) (2f*Math.PI*(speedRing.getBounds().width()/2f))) * (ARC_ANGLE/360f);
        final float radSpeedPart = radSpeed / (speedLabels.length-1);

        arcPaint.setColor(getResources().getColor(R.color.app_background));

        for (int i = 0; i < speedLabels.length; i++) {
            //System.out.println(speedLabels[i] + ": " + textPaint.measureText(speedLabels[i]));
            if (i != 0 && i < (speedLabels.length-1)) {
                canvas.drawTextOnPath(speedLabels[i], speedRing.getPath(), i * radSpeedPart - (textPaint.measureText(speedLabels[i]) / 2) , 0, textPaint);
            }
            else if (i == 0){
                canvas.drawTextOnPath(speedLabels[i], speedRing.getPath(), i * radSpeedPart + coordFW(2, defaultWidth), 0, textPaint);
            }
            else {
                canvas.drawTextOnPath(speedLabels[i], speedRing.getPath(), i * radSpeedPart - textPaint.measureText(speedLabels[i]) - coordFW(2, defaultWidth), 0, textPaint);
            }
        }

        arcPaint.setStrokeWidth(coordFW(REALLY_SMALL_POINTER_SIZE, defaultWidth));
        final float speedRingOffset = coordFW(SMALL_POINTER_SIZE - REALLY_SMALL_POINTER_SIZE, defaultWidth) / 2;
        for (int i = 1; i < speedLabels.length - 1; i++) {
            canvas.drawArc(speedRing.getBounds(speedRingOffset), i * (ARC_ANGLE / (speedLabels.length-1)), 1f, false, arcPaint);
        }

        /////////////////////////////////
        //  PROGRESS ARC

        final float radProgress = ((float) (2f*Math.PI*(progressRing.getBounds().width()/2f))) * (ARC_ANGLE/360f);
        final float radProgressPart = radProgress / currentProgressLabels.length;

        arcPaint.setStrokeWidth(coordFW(SMALL_POINTER_SIZE, defaultWidth));
        for (int i = 0; i < currentProgressLabels.length; i++) {
            canvas.drawTextOnPath(currentProgressLabels[i], progressRing.getPath(), i * radProgressPart + (radProgressPart / 2) - (textPaint.measureText(currentProgressLabels[i]) / 2), coordFH(1, defaultHeight), textPaint);
            if (i < currentProgressLabels.length-1) {
                canvas.drawArc(progressRing.getBounds(), (i + 1) * (ARC_ANGLE / currentProgressLabels.length), 1f, false, arcPaint);
            }
        }

        canvas.restore();

        if (speedString != null && currentTestStatus != TestStatus.SPEEDTEST_END) {
            //canvas.drawText(speedString, centerX, centerY + textPaint.getTextSize()/2 + translateStatusY, textPaint);
        }

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
        switch (currentTestStatus) {
            case INIT:
                progressValue = ((progressValue * SimpleTestFragment.PROGRESS_SEGMENTS_PROGRESS_RING)
                        / (float) SimpleTestFragment.PROGRESS_SEGMENTS_INIT * 50d) / 200d;
                break;
            case PING:
                progressValue = (((progressValue * SimpleTestFragment.PROGRESS_SEGMENTS_PROGRESS_RING) - SimpleTestFragment.PROGRESS_SEGMENTS_INIT)
                        / (float) (SimpleTestFragment.PROGRESS_SEGMENTS_PING) * 50d);
                progressValue += 50d;
                progressValue /= 200d;
                break;
            case DOWN:
            case INIT_UP:
                progressValue = (((progressValue * SimpleTestFragment.PROGRESS_SEGMENTS_PROGRESS_RING) - SimpleTestFragment.PROGRESS_SEGMENTS_INIT - SimpleTestFragment.PROGRESS_SEGMENTS_PING)
                        / (float) (SimpleTestFragment.PROGRESS_SEGMENTS_DOWN) * 50d);
                progressValue += 100d;
                progressValue /= 200d;
                break;
            case UP:
                progressValue = (((progressValue * SimpleTestFragment.PROGRESS_SEGMENTS_PROGRESS_RING) - SimpleTestFragment.PROGRESS_SEGMENTS_INIT - SimpleTestFragment.PROGRESS_SEGMENTS_PING - SimpleTestFragment.PROGRESS_SEGMENTS_DOWN)
                        / (float) (SimpleTestFragment.PROGRESS_SEGMENTS_UP) * 50d);
                progressValue += 150d;
                progressValue /= 200d;
                break;
        }

        if (currentTestStatus.ordinal() >= TestStatus.DOWN.ordinal()) {
            if (topBarSpeedView != null) {
                if (topBarSpeedView.getVisibility() != VISIBLE) {
                    topBarSpeedView.setVisibility(VISIBLE);
                }

                if (topBarSpeedTextView != null) {
                    topBarSpeedTextView.setText(this.speedString);
                }

                if (topBarSpeedIconTextView != null) {
                    switch (currentTestStatus) {
                        case DOWN:
                        case INIT_UP:
                            topBarSpeedIconTextView.setText(R.string.ifont_down);
                            topBarSpeedTextView.setText(speedString + " " + getResources().getString(R.string.test_mbps));
                            break;
                        case UP:
                            topBarSpeedIconTextView.setText(R.string.ifont_up);
                            topBarSpeedTextView.setText(speedString + " " + getResources().getString(R.string.test_mbps));
                            break;
                        case SPEEDTEST_END:
                        case QOS_TEST_RUNNING:
                        case QOS_END:
                            if (!isQosEnabled && topBarSpeedView != null && topBarSpeedView.getVisibility() == View.VISIBLE) {
                                topBarSpeedView.setVisibility(View.INVISIBLE);
                            }
                            else if (isQosEnabled) {
                                topBarSpeedIconTextView.setText(R.string.ifont_qos);
                                topBarSpeedTextView.setText(SimpleTestFragment.PERCENT_FORMAT.format(progressValue));
                            }
                            break;
                        case END:
                            if (!isQosEnabled && topBarSpeedView != null && topBarSpeedView.getVisibility() == View.VISIBLE) {
                                topBarSpeedView.setVisibility(View.INVISIBLE);
                            }
                    }
                }
            }
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

        if (topBarProgressTextView != null) {
            topBarProgressTextView.setText(SimpleTestFragment.PERCENT_FORMAT.format(this.progressValue));
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
        this.statusView = v;
    }

    @Override
    public void recycle() {

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

    }

    @Override
    public void setTestStatusProgress(TestStatus testStatus, double logValue, String stringVal) {

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