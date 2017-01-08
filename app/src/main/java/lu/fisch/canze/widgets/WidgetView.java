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
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Constructor;

import lu.fisch.awt.Color;
import lu.fisch.awt.Graphics;
import lu.fisch.canze.activities.CanzeActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.classes.ColorRanges;
import lu.fisch.canze.classes.Intervals;
import lu.fisch.canze.classes.Options;
import lu.fisch.canze.interfaces.DrawSurfaceInterface;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.R;
import lu.fisch.canze.activities.WidgetActivity;

public class WidgetView extends SurfaceView implements DrawSurfaceInterface, SurfaceHolder.Callback, View.OnTouchListener {

	// a reference to the drawing thread
	private DrawThread drawThread = null;

	// your application certainly needs some data model
	private Drawable drawable = null;
    private String fieldSID = "";

    private boolean clickable = true;

    protected boolean landscape = true;

    private CanzeActivity canzeActivity = null;



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
        setOnTouchListener(this);
	}

	public WidgetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
        setOnTouchListener(this);
	}

	public WidgetView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
        setOnTouchListener(this);
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

    public String extractCarValue(String[] values)
    {
        // the first value is the default one
        String carValue = values[0];

        for(int i=1; i<values.length; i++)
            if(values[i].startsWith(String.valueOf(MainActivity.car)+":"))
                carValue=values[i].split(":")[1];

        return carValue;
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
                    //Constructor<?> constructor = clazz.getConstructor(null);
                    Constructor<?> constructor = clazz.getConstructor();
                    drawable = (Drawable) constructor.newInstance();
                    drawable.setDrawSurface(WidgetView.this);
                    // apply attributes
                    drawable.setMin(Integer.valueOf(extractCarValue(attributes.getString(R.styleable.WidgetView_min).split(","))));
                    drawable.setMax(Integer.valueOf(extractCarValue(attributes.getString(R.styleable.WidgetView_max).split(","))));
                    //drawable.setMin(attributes.getInt(R.styleable.WidgetView_min, 0));
                    //drawable.setMax(attributes.getInt(R.styleable.WidgetView_max, 0));
                    drawable.setMajorTicks(Integer.valueOf(extractCarValue(attributes.getString(R.styleable.WidgetView_majorTicks).split(","))));
                    drawable.setMinorTicks(Integer.valueOf(extractCarValue(attributes.getString(R.styleable.WidgetView_minorTicks).split(","))));
                    //drawable.setMajorTicks(attributes.getInt(R.styleable.WidgetView_majorTicks, 0));
                    //drawable.setMinorTicks(attributes.getInt(R.styleable.WidgetView_minorTicks, 0));
                    drawable.setTitle(attributes.getString(R.styleable.WidgetView_text));
                    drawable.setShowLabels(attributes.getBoolean(R.styleable.WidgetView_showLabels, true));
                    drawable.setShowValue(attributes.getBoolean(R.styleable.WidgetView_showValue, true));
                    drawable.setInverted(attributes.getBoolean(R.styleable.WidgetView_isInverted, false));

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

                    String optionsJson =attributes.getString(R.styleable.WidgetView_options);
                    if(optionsJson!=null && !optionsJson.trim().isEmpty())
                        drawable.setOptions(new Options(optionsJson.replace("'", "\"")));

                    //drawable.setMinAlt(attributes.getInt(R.styleable.WidgetView_minAlt, -1));
                    //drawable.setMaxAlt(attributes.getInt(R.styleable.WidgetView_maxAlt, -1));

                    String minAlt = attributes.getString(R.styleable.WidgetView_minAlt);
                    if(minAlt!=null && !minAlt.trim().isEmpty())
                        drawable.setMinAlt(Integer.valueOf(extractCarValue(minAlt.split(","))));

                    String maxAlt = attributes.getString(R.styleable.WidgetView_maxAlt);
                    if(maxAlt!=null && !maxAlt.trim().isEmpty())
                        drawable.setMaxAlt(Integer.valueOf(extractCarValue(maxAlt.split(","))));

                    drawable.setTimeScale(attributes.getInt(R.styleable.WidgetView_timeScale,1));

                    fieldSID = attributes.getString(R.styleable.WidgetView_fieldSID);
                    if(fieldSID!=null) {
                        String[] sids = fieldSID.split(",");
                        for (int s = 0; s < sids.length; s++) {
                            Field field = MainActivity.fields.getBySID(sids[s]);
                            if (field == null) {
                                MainActivity.debug("WidgetView: init: Field with SID <" + sids[s] + "> (index <" + s + "> in <" + R.styleable.WidgetView_text + "> not found!");
                            } else {
                                // add field to list of registered sids for this widget
                                drawable.addField(field.getSID());
                                // add listener
                                field.addListener(drawable);
                                // add filter to reader
                                int interval = drawable.getIntervals().getInterval(field.getSID());
                                if (interval == -1)
                                    MainActivity.device.addActivityField(field);
                                else
                                    MainActivity.device.addActivityField(field, interval);
                            }
                        }
                    }
                    //MainActivity.debug("WidgetView: My SID is "+fieldSID);

                    if(MainActivity.milesMode) drawable.setTitle(drawable.getTitle().replace("km","mi"));
                }
                else
                {
                    MainActivity.debug("WidgetView: init: WidgetIndex " + widgetIndex + " is wrong!? Not registered in <WidgetView>?");
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

    private boolean motionDown = false;
    private boolean motionMove = false;
    private float downX, downY;

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
		// react on touch events
		// get pointer index from the event object
	    int pointerIndex = event.getActionIndex();

	    // get pointer ID
	    int pointerId = event.getPointerId(pointerIndex);

	    // get masked (not specific to a pointer) action
	    int maskedAction = event.getActionMasked();

        MainActivity.debug("WidgetView: maskedAction = " + maskedAction);

	    switch (maskedAction) {
		    case MotionEvent.ACTION_DOWN:
		    case MotionEvent.ACTION_POINTER_DOWN:{
                motionDown=true;
                downX=event.getX();
                downY=event.getY();
                break;
            }
		    case MotionEvent.ACTION_MOVE: {
                if (Math.abs(downX - event.getX()) + Math.abs(downY - event.getY()) > 20) {
                    motionMove=true;
                }

			    break;
		    }

		    case MotionEvent.ACTION_UP:
		    case MotionEvent.ACTION_POINTER_UP:
            {
                if(!motionMove && clickable && MainActivity.isSafe()) {
                    canzeActivity.setWidgetClicked(true);
                    Intent intent = new Intent(this.getContext(), WidgetActivity.class);
                    selectedDrawable = this.getDrawable();
                    this.getContext().startActivity(intent);
                }

                motionDown=false;
                motionMove=false;
                break;
            }
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
        // load data from the database
        (new Thread(new Runnable() {
            @Override
            public void run() {
                drawable.loadValuesFromDatabase();
                repaint();
            }
        })).start();
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
        // set parent
        //if(canzeActivity!=null)
        //    canzeActivity.setWidgetClicked(false);
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

    public void setCanzeActivity(CanzeActivity canzeActivity) {
        this.canzeActivity = canzeActivity;
    }
}
