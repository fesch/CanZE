/*
    CanZE
    Take a closer look at your ZE car

    Copyright (C) 2015 - The CanZE Team
    http://canze.fisch.lu

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.canze.widgets;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.lang.reflect.Constructor;

import lu.fisch.awt.Color;
import lu.fisch.awt.Graphics;
import lu.fisch.canze.classes.ColorRanges;
import lu.fisch.canze.classes.Intervals;
import lu.fisch.canze.interfaces.DrawSurfaceInterface;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.R;
import lu.fisch.canze.activities.WidgetActivity;

public class WidgetView extends SurfaceView implements DrawSurfaceInterface, SurfaceHolder.Callback {

	// a reference to the drawing thread
	private DrawThread drawThread = null;

	// your application certainly needs some data model
	private Drawable drawable = null;
    private String fieldSID = "";

    private boolean clickable = true;

    protected boolean landscape = true;

    // for data sharing
    public static Drawable selectedDrawable = null;

	public void setDrawable(Drawable drawable)
    {
        this.drawable=drawable;
        //if(drawable.getDrawSurface()==null)
        drawable.setDrawSurface(this);
        repaint();
    }

	public Drawable getDrawable()
	{
		return drawable;
	}

	public WidgetView(Context context) {
		super(context);
		init(context, null);
	}

	public WidgetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public WidgetView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context,attrs);
	}

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        landscape = (right-left)>(bottom-top);
        if(changed) drawable.onLayout(landscape);
    }

    public void reset()
    {
        drawable.reset();
        repaint();
    }

    public void init(final Context context, AttributeSet attrs)
	{
        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        // make sure we get key events
        setFocusable(true);

    	WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    	Display display = wm.getDefaultDisplay();
    	Point size = new Point();
    	display.getSize(size);

        // read attributes
        if(attrs!=null)
        {
            try
            {
                // create configured widget
                String[] widgets = {"Tacho","Kompass", "Bar","BatteryBar","Plotter","Label","Timeplot","BarGraph"};
                TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WidgetView, 0, 0);
                int widgetIndex = attributes.getInt(R.styleable.WidgetView_widget, 0);
                if(widgetIndex<widgets.length)
                {
                    String widget = widgets[widgetIndex];
                    //MainActivity.debug("WidgetView: I am a "+widget);
                    Class clazz = Class.forName("lu.fisch.canze.widgets." + widget);
                    Constructor<?> constructor = clazz.getConstructor(null);
                    drawable = (Drawable) constructor.newInstance();
                    drawable.setDrawSurface(this);
                    // apply attributes
                    drawable.setMin(attributes.getInt(R.styleable.WidgetView_min, 0));
                    drawable.setMax(attributes.getInt(R.styleable.WidgetView_max, 0));
                    drawable.setMajorTicks(attributes.getInt(R.styleable.WidgetView_majorTicks, 0));
                    drawable.setMinorTicks(attributes.getInt(R.styleable.WidgetView_minorTicks, 0));
                    drawable.setTitle(attributes.getString(R.styleable.WidgetView_text));
                    drawable.setShowLabels(attributes.getBoolean(R.styleable.WidgetView_showLabels, true));
                    drawable.setShowValue(attributes.getBoolean(R.styleable.WidgetView_showValue, true));
                    drawable.setInverted(attributes.getBoolean(R.styleable.WidgetView_isInverted, false));
                    fieldSID = attributes.getString(R.styleable.WidgetView_fieldSID);

                    String colorRangesJson =attributes.getString(R.styleable.WidgetView_colorRanges);
                    if(colorRangesJson!=null && !colorRangesJson.trim().isEmpty())
                        drawable.setColorRanges(new ColorRanges(colorRangesJson.replace("'", "\"")));

                    String foreground =attributes.getString(R.styleable.WidgetView_foregroundColor);
                    if(foreground!=null && !foreground.isEmpty())
                        drawable.setForeground(Color.decode(foreground));

                    String background =attributes.getString(R.styleable.WidgetView_backgroundColor);
                    if(background!=null && !background.isEmpty())
                        drawable.setBackground(Color.decode(background));

                    String intermediate =attributes.getString(R.styleable.WidgetView_intermediateColor);
                    if(intermediate!=null && !intermediate.isEmpty())
                        drawable.setIntermediate(Color.decode(intermediate));

                    String titleColor =attributes.getString(R.styleable.WidgetView_titleColor);
                    if(titleColor!=null && !titleColor.isEmpty())
                        drawable.setTitleColor(Color.decode(titleColor));

                    String intervalJson =attributes.getString(R.styleable.WidgetView_intervals);
                    if(intervalJson!=null && !intervalJson.trim().isEmpty())
                        drawable.setIntervals(new Intervals(intervalJson.replace("'", "\"")));

                    //MainActivity.debug("WidgetView: My SID is "+fieldSID);

                    if(MainActivity.milesMode) drawable.setTitle(drawable.getTitle().replace("km","mi"));

                    repaint();
                }
                else
                {
                    MainActivity.debug("WidgetIndex "+widgetIndex+" is wrong!? Not registered in <WidgetView>?");
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        // in case your application needs one or more timers,
        // you have to put them here
        /*Timer timer = new Timer();
        timer.schedule(new TimerTask() {
			@Override
			public void run() {
				repaint();
			}
		}, 100, 100);
		*/
	}

	//@Override
    public boolean onTouchEvent__disabled(MotionEvent event)
    {
		// react on touch events
		// get pointer index from the event object
	    int pointerIndex = event.getActionIndex();

	    // get pointer ID
	    int pointerId = event.getPointerId(pointerIndex);

	    // get masked (not specific to a pointer) action
	    int maskedAction = event.getActionMasked();

	    switch (maskedAction) {
		    case MotionEvent.ACTION_DOWN:
		    case MotionEvent.ACTION_POINTER_DOWN:{
                if(clickable && MainActivity.isSafe()) {
                    Intent intent = new Intent(this.getContext(), WidgetActivity.class);
                    selectedDrawable = this.getDrawable();
                    this.getContext().startActivity(intent);
                }
                break;
            }
		    case MotionEvent.ACTION_MOVE: {

			    break;
		    }
		    /*case MotionEvent.ACTION_MOVE: { // a pointer was moved

			    break;
		    }*/
		    case MotionEvent.ACTION_UP:
		    case MotionEvent.ACTION_POINTER_UP:
		    case MotionEvent.ACTION_CANCEL: {

		    	break;
		    }
	    }


	    invalidate();

	    return true;
    }

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0)
	{
		// do a first painting
		repaint();
	}

    // DIRECT repaint method
    public void repaint2() {
        Canvas c = null;
        try {
            c = getHolder().lockCanvas();
            if (c != null) {
                // enable anti-aliasing
                c.setDrawFilter(new PaintFlagsDrawFilter(1, Paint.ANTI_ALIAS_FLAG));
                // clean background
                Paint paint = new Paint();
                paint.setColor(drawable.getBackground().getAndroidColor());
                c.drawRect(0, 0, c.getWidth(), c.getHeight(), paint);
                // set dimensions
                drawable.setWidth(getWidth());
                drawable.setHeight(getHeight());
                // do the drawing
                drawable.draw(new Graphics(c));
            }
        }
        catch(Exception e)
        {
            // ignore
        }
        finally
        {
            if (c != null) {
                getHolder().unlockCanvasAndPost(c);
            }
        }
    }

    // INDIRECT repaint method (using a separate thread
	public void repaint()
	{
        if(drawThread==null || !drawThread.isRunning())
        {
            // gargabe collect
            System.gc();
            // post a task to the UI thread
            this.post(new Runnable() {
                @Override
                public void run() {
                    // create a new drawThread
                    drawThread = new DrawThread(getHolder(), getContext(), new Handler() {
                        @Override
                        public void handleMessage(Message m) {
                        }
                    });
                    // call the setter for the pointer to the model
                    if (drawable != null) {
                        drawable.setWidth(getWidth());
                        drawable.setHeight(getHeight());
                        // draw the widget
                        drawThread.setDrawable(drawable);
                    }
                    // start the thread
                    drawThread.start();
                }
            });
        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		// stop the drawThread properly
        boolean retry = true;
        while (retry)
        {
            try
            {
            	// wait for it to finish
                if(drawThread!=null && drawThread.isRunning())
            	    drawThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
            	// ignore any error
                e.printStackTrace();
            }
        }
        // set it to null, so that a new one can be created in case of a resume
        drawThread=null;
	}

    /* *************************************
     * Getter & Setter
     * *************************************/

    public String getFieldSID()
    {
        return fieldSID;
    }

    @Override
    public boolean isClickable() {
        return clickable;
    }

    @Override
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public void setFieldSID(String fieldSID) {
        this.fieldSID = fieldSID;
    }
}
