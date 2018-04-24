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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import at.alladin.rmbt.android.util.InformationCollector;
import at.alladin.rmbt.client.helper.TestStatus;

import at.alladin.openrmbt.android.R;

public class TestView extends View implements ChangeableSpeedTestStatus, TestViewable {
	public enum SpeedRingType {
		UP_DOWN,
		QOS
	}
	
	public final static int REL_H = 560;
	public final static int REL_W = 630;
	
	public final static int POS_PING_Y  = 78;
	public final static int POS_DOWN_Y  = 115;
	public final static int POS_UP_Y  = 152;
	
    private boolean recycled;
    
    private final Bitmap genBackgroundBitmap;
    
    private float scale = 1f;
    
    private final int internalPaddingLeft;
    private final int internalPaddingTop;
    
    private final Bitmap speedStatusDownBitmap;
    private final Bitmap speedStatusUpBitmap;
    
    private final int width;
    private final int height;
    private final Gauge speedGauge;
    private final Gauge progressGauge;
    private final Gauge signalGauge;
    
    private float speedX;
    private float speedY;
    private final float speedStatusDownX;
    private final float speedStatusDownY;
    private final float speedStatusUpX;
    private final float speedStatusUpY;
    private final Bitmap speedRingQos;
    private final Bitmap speedRingBitmap;
    final float speedRingX;
    final float speedRingY;
    private SpeedRingType speedRingType = SpeedRingType.UP_DOWN;
    
    private final Paint bitmapPaint;
    private final Paint resultPaint;
    private final Paint progressPaint;
    private final Paint speedPaint;
    private final Paint signalPaint;
    
    private final float progressX;
    private final float progressY;
    
    private final float signalX;
    private final float signalY;
    
    private String headerString;
    private String speedString;
    private String subHeaderString;
    private String progressString;
    private String signalString;
    private int signalType = InformationCollector.SINGAL_TYPE_NO_SIGNAL;
    private String resultPingString;
    private String resultDownString;
    private String resultUpString;
    private TestStatus testStatus;

    private float signalRingX;
    private float signalRingY;
    private Bitmap signalRingBitmapMobile;
    private Bitmap signalRingBitmapRsrp;
    private Bitmap signalRingBitmapWlan;

    private String signalDescriptionString;
    private float signalDescriptionX;
    private float signalDescriptionY;
    private Paint signalDescriptionPaint;

    public TestView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        
        final Resources res = context.getResources();
        
        bitmapPaint = new Paint();
        bitmapPaint.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.app_text_color), Mode.SRC_ATOP));
        bitmapPaint.setFilterBitmap(true);
        
        final Bitmap backgroundBitmap = getBitmap(res,R.drawable.test_box_large);
        width = backgroundBitmap.getWidth();
        height = backgroundBitmap.getHeight();
        
        internalPaddingLeft = coordW(-5, REL_W);
        internalPaddingTop = coordW(-35, REL_H);
                        
        genBackgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(genBackgroundBitmap);
        
        //if (! isInEditMode())
        //    backgroundBitmap.recycle();
        
        resultPaint = new Paint();
        resultPaint.setAntiAlias(true);
        resultPaint.setLinearText(true);
        resultPaint.setTypeface(Typeface.DEFAULT_BOLD);
        resultPaint.setColor(Color.parseColor("#ffffff")); //#002c44
        resultPaint.setTextSize(coordFH(23, REL_H));
        resultPaint.setTextAlign(Align.LEFT);
        
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setFakeBoldText(true);
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setTextSize(coordFH(23, REL_H));
        paint.setTextAlign(Align.LEFT);
                
        Bitmap background, dynamic, foreground;
        int ih, iw, x, y;

        //////////////////////////////////////////////////////////////////
        
        /////////////////////////
        // Speed ring:
        /////////////////////////
        speedRingBitmap = getBitmap(res, R.drawable.ringskala_speed);
        speedRingX = coordFW(44, REL_W);
        speedRingY = coordFH(238, REL_H);
        speedRingQos = getBitmap(res, R.drawable.ringskala_qos);
        
        /////////////////////////
        // Speed gauge:
        /////////////////////////
        background = getBitmap(res, R.drawable.test_gauge_speed_background);
        dynamic = getBitmap(res, R.drawable.test_gauge_speed_dynamic);
        foreground = getBitmap(res, R.drawable.test_gauge_speed_foreground);
        speedGauge = new Gauge(0f, 305.0f, background, dynamic, foreground, 14f / 325, 14f / 325, 309f / 325,
                309f / 325, getResources().getColor(R.color.gauge_speed_dynamic), getResources().getColor(R.color.gauge_speed_background));
        iw = speedGauge.getIntrinsicWidth();
        ih = speedGauge.getIntrinsicHeight();
        x = coordW(44, REL_W);
        y = coordH(238, REL_H);
        speedGauge.setBounds(x, y, x + iw, y + ih);

        /////////////////////////
        // Speed up/down/QoS image/coordinates:
        /////////////////////////
        speedStatusDownBitmap = getBitmap(res, R.drawable.statuscircles_speed_download);
        speedStatusDownX = coordFW(194, REL_W);
        speedStatusDownY = coordFH(354, REL_H);
        speedStatusUpBitmap = getBitmap(res, R.drawable.statuscircles_speed_upload);
        speedStatusUpX = coordFW(194, REL_W);
        speedStatusUpY = coordFH(349, REL_H);
        
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(coordFH(19, REL_H));
        speedPaint = new Paint(paint);
        speedPaint.setColor(getResources().getColor(R.color.gauge_speed_dynamic));
        speedX = coordFW(216, REL_W);
        speedY = coordFH(418, REL_H);
        speedString = res.getString(R.string.test_mbps);

        //////////////////////////////////////////////////////////////////
        
        /////////////////////////
        // Progress ring:
        /////////////////////////
        final Bitmap progressRingBitmap = getBitmap(res, R.drawable.ringskala_progress);
        final float progressRingX = coordFW(247, REL_W);
        final float progressRingY = coordFH(33, REL_H);
        canvas.drawBitmap(progressRingBitmap, progressRingX, progressRingY, bitmapPaint);
        progressRingBitmap.recycle();
        
        /////////////////////////
        // Progress gauge:
        /////////////////////////        
        background = getBitmap(res, R.drawable.test_gauge_progress_background);
        dynamic = getBitmap(res, R.drawable.test_gauge_progress_dynamic);
        foreground = getBitmap(res, R.drawable.test_gauge_progress_foreground);
        progressGauge = new Gauge(204f, 283f, background, dynamic, foreground, -3 / 300, 11f / 317, 290f / 300,
                308f / 317, getResources().getColor(R.color.gauge_progress_dynamic), getResources().getColor(R.color.gauge_progress_background),
                getResources().getColor(R.color.gauge_progress_overlay));
        iw = progressGauge.getIntrinsicWidth();
        ih = progressGauge.getIntrinsicHeight();
        x = coordW(247, REL_W);
        y = coordH(33, REL_H);
        progressGauge.setBounds(x, y, x + iw, y + ih);

        /////////////////////////
        // Progress paint:
        /////////////////////////    
        progressPaint = new Paint(resultPaint);
        progressPaint.setColor(getResources().getColor(R.color.gauge_progress_dynamic));
        progressPaint.setTextSize(coordFH(52, REL_H));
        progressPaint.setTextAlign(Align.CENTER);
        progressX = coordFW(394, REL_W);
        progressY = coordFH(208, REL_H);        

        //////////////////////////////////////////////////////////////////
        
        /////////////////////////
        // Signal ring:
        /////////////////////////                        
        signalRingBitmapMobile = getBitmap(res, R.drawable.ringskala_signal_mobile);
        signalRingBitmapRsrp = getBitmap(res, R.drawable.ringskala_signal_rsrp);
        signalRingBitmapWlan = getBitmap(res, R.drawable.ringskala_signal_wlan);
        signalRingX = coordFW(363, REL_W);
        signalRingY = coordFH(323, REL_H);

        /////////////////////////
        // Signal gauge:
        /////////////////////////                
        background = getBitmap(res, R.drawable.test_gauge_signal_background);
        dynamic = getBitmap(res, R.drawable.test_gauge_signal_dynamic);
        foreground = getBitmap(res, R.drawable.test_gauge_signal_foreground);
        signalGauge = new Gauge(120f, -157f, background, dynamic, foreground, -43 / 221f, -32 / 232f, 212 / 221f,
                223 / 232f, getResources().getColor(R.color.gauge_signal_dynamic), getResources().getColor(R.color.gauge_signal_background));
        iw = signalGauge.getIntrinsicWidth();
        ih = signalGauge.getIntrinsicHeight();
        x = coordW(364, REL_W);
        y = coordH(322, REL_H);
        signalGauge.setBounds(x, y, x + iw, y + ih);
        
        /////////////////////////
        // Signal/Signal description paint:
        /////////////////////////      
        paint.setColor(getResources().getColor(R.color.gauge_signal_dynamic));
        signalDescriptionString = res.getString(R.string.test_dbm);
        signalDescriptionX = coordFW(444, REL_W);
        signalDescriptionY = coordFH(452, REL_H);
        signalDescriptionPaint = paint;
        
        signalPaint = new Paint(signalDescriptionPaint);
        signalPaint.setTextSize(coordFH(27, REL_H));
        signalX = coordFW(436, REL_W);
        signalY = coordFH(424, REL_H);        
    }
    
    protected Bitmap getBitmap(Resources res, int id)
    {
        return BitmapFactory.decodeResource(res, id);
    }
    
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
    {
        final int paddingH = getPaddingLeft() + internalPaddingLeft + getPaddingRight();
        final int paddingW = getPaddingTop() + internalPaddingTop + getPaddingBottom();
        
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        final int newW = width + paddingH;
        switch (MeasureSpec.getMode(widthMeasureSpec))
        {
        case MeasureSpec.AT_MOST:
            if (newW < w)
                w = newW;
            break;
        
        case MeasureSpec.EXACTLY:
            break;
        
        case MeasureSpec.UNSPECIFIED:
            w = newW;
            break;
        }
        scale = (float) (w - getPaddingLeft() - getPaddingRight()) / (width + internalPaddingLeft);
        
        int h = MeasureSpec.getSize(heightMeasureSpec);
        final int newH = Math.round((height + 20) * scale) + paddingW;
        switch (MeasureSpec.getMode(heightMeasureSpec))
        {
        case MeasureSpec.AT_MOST:
            if (newH < h)
                h = newH;
            break;
        
        case MeasureSpec.EXACTLY:
            break;
        
        case MeasureSpec.UNSPECIFIED:
            h = newH;
            break;
        }
        
        setMeasuredDimension(w, h);
    }
    
    protected float coordFW(final int x, final int y)
    {
        return (float) x / y * width;
    }
    
    protected float coordFH(final int x, final int y)
    {
        return (float) x / y * height;
    }
    
    protected int coordW(final int x, final int y)
    {
        return Math.round((float) x / y * width);
    }
    
    protected int coordH(final int x, final int y)
    {
        return Math.round((float) x / y * height);
    }
    
    public TestView(final Context context, final AttributeSet attrs)
    {
        this(context, attrs, 0);
    }
    
    public TestView(final Context context)
    {
        this(context, null, 0);
    }
    
    @Override
    public void setSpeedString(final String speedString) {
    	this.speedString = speedString;
    }
    
    @Override
    public void setSpeedValue(final double speedValueRelative)
    {
        speedGauge.setValue(speedValueRelative);
    }
    
    @Override
    public double setSpeedProgressValue(final double speedValue) {
    	final double currentValue = speedGauge.value;
        speedGauge.setValue(speedValue >= currentValue ? speedValue : currentValue);
        return speedGauge.value;
    }
    
    @Override
    public double setProgressValue(final double progressValue)
    {
    	final double currentValue = progressGauge.value;
        progressGauge.setValue(progressValue >= currentValue ? progressValue : currentValue);
        return progressGauge.value;
    }
    
    @Override
    public void setSignalValue(final double relativeSignal)
    {
        signalGauge.setValue(relativeSignal);
    }
    
    @Override
    public void setHeaderString(final String headerString)
    {
        this.headerString = headerString;
    	//this.headerString = "TestTest_0TestTest_0TestTest_012";
    }
    
    @Override
    public void setSubHeaderString(final String subHeaderString)
    {
        this.subHeaderString = subHeaderString;
    }
    
    public void setResultPingString(final String resultPingString)
    {
        this.resultPingString = resultPingString;
    }
    
    public void setResultDownString(final String resultDownString)
    {
        this.resultDownString = resultDownString;
    }
    
    public void setResultUpString(final String resultUpString)
    {
        this.resultUpString = resultUpString;
    }
    
    @Override
    public void setProgressString(final String progressString)
    {
        this.progressString = progressString;
    }
    
    public void setSignalString(final String signalString)
    {
        this.signalString = signalString;
    }
    
    public void setSignalType(final int signalType)
    {
        this.signalType = signalType;
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
    public void setTestStatus(final TestStatus testStatus)
    {
        this.testStatus = testStatus;
    }
    
    @Override
    protected void onDraw(final Canvas canvas)
    {
        if (recycled)
            return;
        
        final int saveCount = canvas.save();
        
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.scale(scale, scale);
        canvas.translate(internalPaddingLeft, internalPaddingTop);
        
        canvas.drawBitmap(genBackgroundBitmap, 0, 0, bitmapPaint);
        
        if (testStatus != null) {
            switch (testStatus)
            {
            case DOWN:
            case INIT_UP:
                canvas.drawBitmap(speedStatusDownBitmap, speedStatusDownX, speedStatusDownY, bitmapPaint);
                break;
            
            case UP:
                canvas.drawBitmap(speedStatusUpBitmap, speedStatusUpX, speedStatusUpY, bitmapPaint);
                break;

            case SPEEDTEST_END:
                speedGauge.setStartAngle(306f);
                speedGauge.setMaxAngle(-306f);
            	speedRingType = SpeedRingType.QOS;
            	speedX = coordFW(213, REL_W);
                speedY = coordFH(408, REL_H);        
            	speedPaint.setTextSize(coordFH(36, REL_H));
            	break;
            	
            default:
            	break;
            }
            
            switch (speedRingType) {
            case UP_DOWN:
            	canvas.drawBitmap(speedRingBitmap, speedRingX, speedRingY, bitmapPaint);
            	break;
            	
            case QOS:
            	canvas.drawBitmap(speedRingQos, speedRingX, speedRingY, bitmapPaint);
            	break;
            	
            default:
            	break;
            }
        }
        
        final Bitmap signalRingBitmap;
        switch (signalType)
        {
        case InformationCollector.SINGAL_TYPE_MOBILE:
            signalRingBitmap = signalRingBitmapMobile;
            break;
            
        case InformationCollector.SINGAL_TYPE_WLAN:
            signalRingBitmap = signalRingBitmapWlan;
            break;
            
        case InformationCollector.SINGAL_TYPE_RSRP:
            signalRingBitmap = signalRingBitmapRsrp;
            break;
            
        default:
            signalRingBitmap = null;
        }
        
        if (signalRingBitmap != null)
        {
            canvas.drawBitmap(signalRingBitmap, signalRingX, signalRingY, bitmapPaint);
            canvas.drawText(signalDescriptionString, signalDescriptionX, signalDescriptionY, signalDescriptionPaint);
            if (signalString != null)
                canvas.drawText(signalString, signalX, signalY, signalPaint);
            signalGauge.draw(canvas);
        }

        if (progressString != null) {
            canvas.drawText(progressString, progressX, progressY, progressPaint);
        }
        
        if (speedString != null) {
        	canvas.drawText(speedString, speedX, speedY, speedPaint);
        }
        
        speedGauge.draw(canvas);
        progressGauge.draw(canvas);
        
        canvas.restoreToCount(saveCount);
    }
    
    public void recycle()
    {
        recycled = true;
        genBackgroundBitmap.recycle();
        speedStatusDownBitmap.recycle();
        speedStatusUpBitmap.recycle();
        signalRingBitmapMobile.recycle();
        signalRingBitmapWlan.recycle();
        signalRingBitmapRsrp.recycle();
        speedRingQos.recycle();
        speedRingBitmap.recycle();
        speedGauge.recycle();
        progressGauge.recycle();
        signalGauge.recycle();
    }

	public String getHeaderString() {
		return headerString;
	}

	public String getSubHeaderString() {
		return subHeaderString;
	}

	public String getResultPingString() {
		return resultPingString;
	}

	public String getResultDownString() {
		return resultDownString;
	}

	public String getResultUpString() {
		return resultUpString;
	}

    @Override
    public void setView(View container) {
        // nothing to do in this view
    }

    @Override
	public String getResultInitString() {
		// missing in this view
		return null;
	}

	@Override
	public void setResultDownString(String s, Object flag) {
		setResultDownString(s);
	}

	@Override
	public void setResultUpString(String s, Object flag) {
		setResultUpString(s);
	}

	@Override
	public void setResultInitString(String s, Object flag) {
		//nothing to do in this view;
	}

	@Override
	public void setResultPingString(String s, Object flag) {
		setResultPingString(s);
	}

    @Override
    public void setTestStatusProgress(TestStatus testStatus, double logValue, String stringVal) {

    }

    @Override
	public void setForceHideProgressBar(boolean isVisible) {
		//nothing to do in this view;
		
	}

    @Override
	public boolean isForceHideProgressBar() {
		return false;
	}
}
