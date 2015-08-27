package lu.fisch.can.widgets;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
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

import lu.fisch.can.interfaces.DrawSurfaceInterface;
import lu.fisch.canze.DrawThread;
import lu.fisch.canze.MainActivity;
import lu.fisch.canze.R;
import lu.fisch.canze.WidgetActivity;

public class WidgetView extends SurfaceView implements DrawSurfaceInterface, SurfaceHolder.Callback {

	// a reference to the drawing thread
	private DrawThread drawThread = null;

	// your application certainly needs some data model
	private Drawable drawable = null;
    private String fieldSID = "";

    private boolean clickable = true;

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
                String[] widgets = {"Tacho","Kompass"};
                TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs,R.styleable.WidgetView,0,0);
                int widgetIndex = attributes.getInt(R.styleable.WidgetView_widget, 0);
                if(widgetIndex<widgets.length)
                {
                    String widget = widgets[widgetIndex];
                    Class clazz = Class.forName("lu.fisch.can.widgets." + widget);
                    Constructor<?> constructor = clazz.getConstructor(null);
                    drawable = (Drawable) constructor.newInstance();
                    drawable.setDrawSurface(this);
                    // apply attributes
                    setMin(attributes.getInt(R.styleable.WidgetView_min, 0));
                    setMax(attributes.getInt(R.styleable.WidgetView_max, 0));
                    setMajorTicks(attributes.getInt(R.styleable.WidgetView_majorTicks, 0));
                    setMinorTicks(attributes.getInt(R.styleable.WidgetView_minorTicks, 0));
                    setTitle(attributes.getString(R.styleable.WidgetView_text));
                    setShowLabels(attributes.getBoolean(R.styleable.WidgetView_showLabels, true));
                    setShowValue(attributes.getBoolean(R.styleable.WidgetView_showValue, true));
                    fieldSID = attributes.getString(R.styleable.WidgetView_fieldSID);
                    repaint();
                }
                else
                {
                    MainActivity.debug("Widgetindex "+widgetIndex+" is wrong!? Not registered in <WidgetView>?");
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        // in case your application needs one or more timers,
        // you have to put them here
        //Timer timer = new Timer();
        /*timer.schedule(new TimerTask() {
			@Override
			public void run() {
				repaint();
			}
		}, 100, 100);*/

        /*
        timer = new Timer();
        timer.schedule(new TimerTask() {

			@Override
			public void run() {
				drawables.move();
				repaint();
			}
		}, 100, 2000); /**/
	}

	@Override
    public boolean onTouchEvent(MotionEvent event)
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
                if(clickable) {
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

	public void repaint()
	{
        if(drawThread==null || !drawThread.isRunning())
        {
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
                        drawThread.setDrawable(drawable);
                    }
                    // start the thread
                    drawThread.start();
                }
            });
        }

		/*
		// determine if we need to create a new drawing thread
		boolean createNew = false;
        if(drawThread==null) createNew=true;
        else if(!drawThread.isRunning())
        {
        	createNew=true;
        }

        if(createNew)
        {
        	drawThread = new DrawThread(getHolder(), getContext(), new Handler() {
	            @Override
	            public void handleMessage(Message m) {
	            }
	        });
	        // call the setter for the pointer to the model
	        drawThread.setDataModel(dataModel);
	        // start the thread
			drawThread.start();
        }*/
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

    /* *************************************
         * Deleguations
         * *************************************/
	public void setMin(int min) {
        if(drawable!=null)
            drawable.setMin(min);
	}

	public void setMax(int max) {
        if(drawable!=null)
            drawable.setMax(max);
	}

	public void setMajorTicks(int majorTicks) {
        if(drawable!=null)
            drawable.setMajorTicks(majorTicks);
	}

	public void setMinorTicks(int minorticks) {
        if(drawable!=null)
            drawable.setMinorTicks(minorticks);
	}

	public void setShowLabels(boolean showLabels) {
        if(drawable!=null)
            drawable.setShowLabels(showLabels);
	}

    public void setTitle(String title) {
        if(drawable!=null)
            drawable.setTitle(title);
    }

    public void setShowValue(boolean showValue) {
        if(drawable!=null)
            drawable.setShowValue(showValue);
    }


}
