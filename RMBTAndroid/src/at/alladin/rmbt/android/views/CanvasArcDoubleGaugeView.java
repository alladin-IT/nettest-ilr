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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import at.alladin.openrmbt.android.R;
import at.alladin.rmbt.android.test.TestViewable;
import at.alladin.rmbt.android.util.ConfigHelper;
import at.alladin.rmbt.client.helper.TestStatus;

/**
 * Created by lb on 07.06.16.
 */
public class CanvasArcDoubleGaugeView extends View implements TestViewable {

    private final static int SMALL_POINTER_SIZE = 9;

    private final static int BIG_POINTER_SIZE = 9;

    private final static float ARC_ANGLE = 240f;

    private Paint arcPaint;
    private Paint textPaint;
    private Paint unitPaint;
    private Paint bitmapPaint;

    private View statusView;
    private View topStatusBarView;

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

    TestStatus currentTestStatus = TestStatus.WAIT;

    String upString;
    String downString;
    String pingString;

    public CanvasArcDoubleGaugeView(Context context) {
        super(context);
    }

    public CanvasArcDoubleGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasArcDoubleGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initialize() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(coordFW(SMALL_POINTER_SIZE, defaultWidth));

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
        final int startX;
        final int startY;

        public RingArc(final int arcCenterX, final int arcCenterY, final float strokeWidth, final float absPosition) {
            final int gaugeWidth = (int)((float) arcCenterX * absPosition) - BIG_POINTER_SIZE;
            final int gaugeHeight = (int)((float) arcCenterY * absPosition) - BIG_POINTER_SIZE;
            bounds = new RectF(arcCenterX - gaugeWidth + strokeWidth/2f, arcCenterY - gaugeHeight + strokeWidth/2f,
                    arcCenterX + gaugeWidth - strokeWidth/2f, arcCenterY + gaugeHeight - strokeWidth/2f);
            startX = arcCenterX - gaugeWidth;
            startY = arcCenterY;
        }

        public RectF getBounds() {
            return bounds;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int saveCount = canvas.save();

        final RingArc speedRing = new RingArc(centerX, centerY, arcPaint.getStrokeWidth(), .625f);
        final RingArc progressRing = new RingArc(centerX, centerY, arcPaint.getStrokeWidth(), .95f);

        canvas.save();

        canvas.rotate(90 + ((360f-ARC_ANGLE)/2f), centerX, centerY);
        arcPaint.setColor(getResources().getColor(R.color.calm_gauge_speed_background));
        canvas.drawArc(speedRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);
        arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_background));
        canvas.drawArc(progressRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);

        arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_foreground));

        switch(currentTestStatus) {
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
                    arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_foreground));
                    canvas.drawArc(progressRing.getBounds(), 0, ARC_ANGLE, false, arcPaint);
                    arcPaint.setColor(getResources().getColor(R.color.calm_gauge_progress_background));
                    canvas.drawArc(progressRing.getBounds(), 0, (float)(ARC_ANGLE*progressValue), false, arcPaint);
                }
                break;
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
        speedProgressValue = speedProgressValue;
        return speedProgressValue;
    }

    @Override
    public double setProgressValue(double progressValue) {
        this.progressValue = progressValue;
        return progressValue;
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
    public void setTopStatusBar(View v) {
        this.topStatusBarView = v;
    }

    @Override
    public void setProgressableElements(View v) {

    }

    @Override
    public void setTestStatusProgress(TestStatus testStatus, double logValue, String stringVal) {

    }

    @Override
    public void recycle() {

    }
}