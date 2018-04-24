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

import java.text.Format;
import java.text.NumberFormat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Gauge extends Drawable
{
    
    /*
     * init: 14 1 ping: 14 1 down: 33 1 up: 32
     */
    
    final static String LOG = "Gauge";
    
    final Bitmap background;
    final Bitmap foreground;
    final Bitmap dynamic;
    final Bitmap drawBitmap;
    final Canvas drawCanvas;
    final Paint paint = new Paint();
    final Paint foregroundPaint = new Paint();
    final Paint backgroundPaint = new Paint();
    final Paint overlayPaint = new Paint();
    final Paint erasePaint;
    final RectF ovalRect;
    float startAngle;
    float maxAngle;
    
    final Format formatter = NumberFormat.getPercentInstance();
    
    final int width;
    final int height;
    
    Rect bounds;
    
    double value = 0;
    
    boolean hasOverlayColor = false;
    
    public Gauge(final float startAngle, final float maxAngle, final Bitmap background, final Bitmap dynamic,
            final Bitmap foreground, final float x1, final float y1, final float x2, final float y2, final int foregroundColor, 
            final int backgroundColor) {
    	this(startAngle, maxAngle, background, dynamic, foreground, x1, y1, x2, y2, foregroundColor, backgroundColor, Integer.MIN_VALUE);
    }
    
    public Gauge(final float startAngle, final float maxAngle, final Bitmap background, final Bitmap dynamic,
            final Bitmap foreground, final float x1, final float y1, final float x2, final float y2, final int foregroundColor, 
            final int backgroundColor, final int overlayColor)
    {
        this.startAngle = startAngle;
        this.maxAngle = maxAngle;
        this.background = background;
        this.dynamic = dynamic;
        this.foreground = foreground;
        width = background.getWidth();
        height = background.getHeight();
        drawBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(drawBitmap);
        
        paint.setFilterBitmap(true);
        
        foregroundPaint.setColorFilter(new PorterDuffColorFilter(foregroundColor, Mode.SRC_ATOP));
        foregroundPaint.setFilterBitmap(true);
        
        backgroundPaint.setColorFilter(new PorterDuffColorFilter(backgroundColor, Mode.SRC_ATOP));
        backgroundPaint.setFilterBitmap(true);
        
        if (overlayColor != Integer.MIN_VALUE) {
        	hasOverlayColor = true;
            overlayPaint.setColorFilter(new PorterDuffColorFilter(overlayColor, Mode.SRC_ATOP));            
            overlayPaint.setFilterBitmap(true);
        }

        Log.d(LOG, "density background: " + background.getDensity());
        
        erasePaint = new Paint();
        erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        erasePaint.setAntiAlias(true);
        // erasePaint.setAlpha(0);
        
        ovalRect = new RectF(width * x1, height * y1, width * x2, height * y2);
    }
    
    public void setValue(double value)
    {
        if (value < 0)
            value = 0;
        else if (value > 1)
            value = 1;
        this.value = value;
        
        invalidateSelf();
    }
    
    @Override
    public void draw(final Canvas canvas)
    {
        final Rect r = getBounds();
        
        final int saveCount = canvas.save();
        
        canvas.clipRect(r, Region.Op.REPLACE);
        canvas.translate(r.left, r.top);
        
        canvas.drawBitmap(background, 0, 0, foregroundPaint);
        
        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawCanvas.drawBitmap(dynamic, 0, 0, backgroundPaint);
        final float angle;
        if (maxAngle >= 0)
            angle = -(float) (360d - (maxAngle * value));
        else
            angle = (float) (360d - (-maxAngle * value));
        drawCanvas.drawArc(ovalRect, startAngle, angle, true, erasePaint);
        
        canvas.drawBitmap(drawBitmap, 0, 0, foregroundPaint);
        
        canvas.drawBitmap(foreground, 0, 0, hasOverlayColor ? overlayPaint : paint);
        
        // canvas.drawText(formatter.format(value), textX, textY, textPaint);
        
        canvas.restoreToCount(saveCount);
    }
    
    @Override
    public int getMinimumHeight()
    {
        return height;
    }
    
    @Override
    public int getMinimumWidth()
    {
        return width;
    }
    
    @Override
    public int getIntrinsicHeight()
    {
        return height;
    }
    
    @Override
    public int getIntrinsicWidth()
    {
        return width;
    }
    
    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }
    
    @Override
    public void setAlpha(final int alpha)
    {
        paint.setAlpha(alpha);
        // textPaint.setAlpha(alpha);
        invalidateSelf();
    }
    
    public float getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(float startAngle) {
		this.startAngle = startAngle;
	}

	public float getMaxAngle() {
		return maxAngle;
	}

	public void setMaxAngle(float maxAngle) {
		this.maxAngle = maxAngle;
	}

	@Override
    public void setColorFilter(final ColorFilter cf)
    {
        paint.setColorFilter(cf);
        // textPaint.setColorFilter(cf);
        invalidateSelf();
    }
    
    public void recycle()
    {
        background.recycle();
        foreground.recycle();
        dynamic.recycle();
        drawBitmap.recycle();
    }
    
}
