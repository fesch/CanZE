package lu.fisch.canze;

import java.util.Timer;
import java.util.TimerTask;

import lu.fisch.can.widgets.Drawables;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback {

	// a reference to the drawing thread
	private DrawThread drawThread = null;

	// your application certainly needs some data model
	private Drawables drawables = null;

	public void setDrawables(Drawables drawables)
    {
        this.drawables=drawables;
        repaint();
    }

	public DrawSurface(Context context) {
		super(context);
		init(context);
	}

	public DrawSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DrawSurface(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(final Context context)
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

        // in case your application needs one or more timers,
        // you have to put them here
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

			@Override
			public void run() {
				repaint();
			}
		}, 100, 100);

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
		    case MotionEvent.ACTION_POINTER_DOWN:
		    case MotionEvent.ACTION_MOVE: {
			    // TODO use data
			    break;
		    }
		    /*case MotionEvent.ACTION_MOVE: { // a pointer was moved
		    	// TODO use data
			    break;
		    }*/
		    case MotionEvent.ACTION_UP:
		    case MotionEvent.ACTION_POINTER_UP:
		    case MotionEvent.ACTION_CANCEL: {
		    	// TODO use data
		    	break;
		    }
	    }


	    invalidate();

	    return true;
    }

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0)
	{
		// do a first painting
		repaint();
	}

	public void repaint()
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
		        drawThread.setDrawables(drawables);
		        // start the thread
				drawThread.start();
			}
		});
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
            }
        }
        // set it to null, so that a new one can be created in case of a resume
        drawThread=null;
	}
}
