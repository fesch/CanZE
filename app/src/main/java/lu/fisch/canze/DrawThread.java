package lu.fisch.canze;

import java.util.Calendar;

import lu.fisch.awt.Graphics;
import lu.fisch.can.widgets.Drawable;
import lu.fisch.can.widgets.Drawables;
import lu.fisch.canze.MainActivity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {

	/** Handle to the surface manager object we interact with */
    private SurfaceHolder mSurfaceHolder;
    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;
    /** Message handler used by thread to interact with TextView */
    private Handler mHandler;
    
    // indicates weather we are running or not
    private boolean running = false;
    
    private Drawable drawable = null;

    public DrawThread(SurfaceHolder surfaceHolder, 
			  Context context,
			  Handler handler) 
    {
		// get handles to some important objects
		mSurfaceHolder = surfaceHolder;
		mHandler = handler;
		mContext = context;
	}
    
    public void setDrawable(Drawable item)
    {
    	this.drawable =item;
    }
    
    
	private void draw(Canvas c) 
	{
		if(running)
		{
			c.setDrawFilter(new PaintFlagsDrawFilter(1, Paint.ANTI_ALIAS_FLAG));
			
	        // do your paintings here ...
			// clean background
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			c.drawRect(0, 0, c.getWidth(), c.getHeight(), paint);

			drawable.draw(new Graphics(c));
			
			//Log.w(MainActivity.TAG, Calendar.getInstance().getTimeInMillis() + " -> Items = " + drawables.size());


            // draw some random lines
            /*
			paint = new Paint();
			paint.setColor(Color.BLACK);
			for(int i=0;i<10;i++)
			{
				c.drawLine((float) (Math.random()*c.getWidth()),(float) (Math.random()*c.getHeight()), 
						   (float) (Math.random()*c.getWidth()),(float) (Math.random()*c.getHeight()), paint);
			}
			/**/
		}
	}
	
    @Override
    public void run() 
    {
        Canvas c = null;
        try 
        {
        	// get the surface
            c = mSurfaceHolder.lockCanvas();
            synchronized (mSurfaceHolder) 
            {
            	if(c!=null)
            	{
	            	draw(c);
            	}
            }
        } 
        finally 
        {
            // do this in a finally so that if an exception is thrown
            // during the above, we don't leave the surface in an
            // inconsistent state
            if (c != null) 
            {
                mSurfaceHolder.unlockCanvasAndPost(c);
            }
            running=false;
        }
    }
	
    @Override
    public void start()
    {
    	running=true;
    	super.start();
    }
    
    public boolean isRunning() {
		return running;
	}
}
