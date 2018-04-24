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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import at.alladin.openrmbt.android.R;

import at.alladin.rmbt.android.test.TestViewable;
import at.alladin.rmbt.client.helper.TestStatus;

/**
 * Created by lb on 07.06.16.
 */
public class CanvasStrokeGaugeView extends View implements TestViewable {

    /**
     * divides the gauge into GAUGE_PARTS parts
     */
    private final static int GAUGE_PARTS = 1;

    /**
     * pointers per gauge part
     */
    private final static int POINTERS_PER_GAUGE_PART = 38;

    /**
     * total pointers (inclusive big pointers)
     */
    private final static int TOTAL_NUM_POINTERS = GAUGE_PARTS * POINTERS_PER_GAUGE_PART;

    /**
     *
     */
    private final static String[] GAUGE_COLORS = {
            //1-10
            "#FF4492BE", "#FF4596C2", "#FF4493BE", "#FF4290BB", "#FF40A3CA", "#FF3FA0C7", "#FF3FA0C6", "#FF3E9CC3", "#FF40A5CB", "#FF41A7CE",
            //11-20
            "#FF41A5CD", "#FF68B5CC", "#FF6BBCD0", "#FF6CBDD2", "#FF6CBCD1", "#FF6DBFD6", "#FF5FAF83", "#FF62B486", "#FF5FAE84", "#FF62B487",
            //21-30
            "#FF62B487", "#FF6BAA3D", "#FF6DAF3D", "#FF69A73D", "#FF6DAE3E", "#FF6EB03E", "#FF69A83D", "#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF",
            //31-38
            "#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF"
    };

    private final static String DEFAULT_POINTER_COLOR = "#ff4682B4";

    private final static int SMALL_POINTER_SIZE = 8;

    private final static int BIG_POINTER_SIZE = 8;

    private final static float POINTER_WIDTH = 1f;

    private final static float ARC_ANGLE = 270f;

    private final static boolean SHOW_BIG_POINTERS = false;

    private final static boolean SHOW_FILLED_ARC = false;

    private Paint arcPaint;
    private Paint pointerPaint;
    private Paint textPaint;
    private Paint unitPaint;
    private Paint bitmapPaint;

    private String progressString = "100%";
    private String speedString = "";

    private String mbpsString = "";
    private String msString = "";

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

    TestStatus currentTestStatus = TestStatus.WAIT;

    Bitmap upBitmap;
    Bitmap downBitmap;
    Bitmap pingBitmap;

    public CanvasStrokeGaugeView(Context context) {
        super(context);
    }

    public CanvasStrokeGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasStrokeGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initialize() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(coordFW(2, defaultWidth));
        arcPaint.setColor(Color.LTGRAY);

        pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointerPaint.setStrokeWidth(coordFW(POINTER_WIDTH, defaultWidth));
        //pointerPaint.setStrokeCap(Paint.Cap.ROUND);
        pointerPaint.setStrokeCap(Paint.Cap.SQUARE);
        pointerPaint.setColor(Color.parseColor(DEFAULT_POINTER_COLOR));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.LTGRAY);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(coordFH(20, defaultHeight));

        unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setColor(Color.LTGRAY);
        unitPaint.setTextAlign(Paint.Align.CENTER);
        unitPaint.setTextSize(coordFH(8, defaultHeight));

        bitmapPaint = new Paint();
        bitmapPaint.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.app_text_color), PorterDuff.Mode.SRC_ATOP));
        bitmapPaint.setFilterBitmap(true);

        bigPointerSize = (int)coordFH(BIG_POINTER_SIZE, defaultHeight);
        smallPointerSize = (int)coordFH(SMALL_POINTER_SIZE, defaultHeight);

        upBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_upload_raw);
        downBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_download_raw);
        pingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_ping_raw);

        mbpsString = getResources().getString(R.string.test_mbps);
        msString = getResources().getString(R.string.test_ms);

        translateStatusY = coordFH(-5, defaultHeight);
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

    @Override
    protected void onDraw(Canvas canvas) {
        final int saveCount = canvas.save();

        int arcCenterX = centerX;
        int arcCenterY = centerY;

        final int gaugeWidth = (int)((float) centerX * .95f) - bigPointerSize;
        final int gaugeHeight = (int)((float) centerY * .95f) - bigPointerSize;

        final RectF arcBounds = new RectF(arcCenterX - gaugeWidth + arcPaint.getStrokeWidth()/2f, arcCenterY - gaugeHeight + arcPaint.getStrokeWidth()/2f,
                arcCenterX + gaugeWidth - arcPaint.getStrokeWidth()/2f, arcCenterY + gaugeHeight);

        // Draw the arc
        if (SHOW_FILLED_ARC) {
            canvas.drawArc(arcBounds, 180f, ARC_ANGLE, false, arcPaint);
        }

        // Draw the pointers
        int startX = arcCenterX - gaugeWidth;
        int startY = arcCenterY;

        final int groupSeparator = TOTAL_NUM_POINTERS / GAUGE_PARTS;

        int pointerHeight;

        canvas.save();

        canvas.rotate((180f - ARC_ANGLE) / 2f, arcCenterX, arcCenterY);

        final int highlightedPointers = (int)((float)TOTAL_NUM_POINTERS * this.speedValueRelative);

        for (int i = 0; i <= TOTAL_NUM_POINTERS; i++) {
            if(i % groupSeparator == 0) {
                pointerHeight = SHOW_BIG_POINTERS ? bigPointerSize : smallPointerSize;
            }
            else {
                pointerHeight = smallPointerSize;
            }

            if ((currentTestStatus == TestStatus.DOWN || currentTestStatus == TestStatus.UP || currentTestStatus == TestStatus.INIT_UP) &&
                    i <= highlightedPointers) {
                pointerPaint.setColor(Color.parseColor(GAUGE_COLORS[i]));
            }
            else {
                pointerPaint.setColor(Color.parseColor(DEFAULT_POINTER_COLOR));
            }

            canvas.drawLine(startX, startY, startX - pointerHeight, startY, pointerPaint);

            canvas.rotate(ARC_ANGLE/(float)TOTAL_NUM_POINTERS, arcCenterX, arcCenterY);
        }

        canvas.restore();

        switch(currentTestStatus) {
            case PING:
                canvas.drawBitmap(pingBitmap, arcCenterX - pingBitmap.getWidth()/2,
                        arcCenterY - textPaint.getTextSize() + translateStatusY, bitmapPaint);
                break;
            case DOWN:
            case INIT_UP:
                canvas.drawBitmap(downBitmap, arcCenterX - downBitmap.getWidth()/2,
                        arcCenterY - textPaint.getTextSize() + translateStatusY, bitmapPaint);
                canvas.drawText(mbpsString, arcCenterX,
                        arcCenterY + textPaint.getTextSize() + unitPaint.getTextSize()/2 + translateStatusY, unitPaint);
                break;
            case UP:
                canvas.drawBitmap(upBitmap, arcCenterX - upBitmap.getWidth()/2,
                        arcCenterY - textPaint.getTextSize() + translateStatusY, bitmapPaint);
                canvas.drawText(mbpsString, arcCenterX,
                        arcCenterY + textPaint.getTextSize() + unitPaint.getTextSize()/2 + translateStatusY, unitPaint);
                break;
            case SPEEDTEST_END:
            case QOS_TEST_RUNNING:
            case QOS_END:
                canvas.drawText("QoS", arcCenterX, arcCenterY - (unitPaint.getTextSize() * 1.5f) + translateStatusY, unitPaint);
                canvas.drawText(progressString, arcCenterX, arcCenterY + textPaint.getTextSize()/2 + translateStatusY, textPaint);;
                break;
        }

        if (speedString != null && currentTestStatus != TestStatus.SPEEDTEST_END) {
            canvas.drawText(speedString, arcCenterX, arcCenterY + textPaint.getTextSize()/2 + translateStatusY, textPaint);;
        }

        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setSpeedString(String speedString) {
        this.speedString = speedString;
    }

    @Override
    public void setSpeedValue(double speedValueRelative) {
        this.speedValueRelative = speedValueRelative;
    }

    @Override
    public double setSpeedProgressValue(double speedValue) {
        return speedValue;
    }

    @Override
    public double setProgressValue(double progressValue) {
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

    }

    @Override
    public void setTopStatusBar(View v) {

    }

    @Override
    public void setProgressableElements(View v) {

    }

    @Override
    public void setTestStatusProgress(TestStatus testStatus, double logValue, String stringVal) {

    }

    @Override
    public void recycle() {
        if (pingBitmap != null) { pingBitmap.recycle(); }
        if (downBitmap != null) { downBitmap.recycle(); }
        if (upBitmap != null) { upBitmap.recycle(); }
    }
}