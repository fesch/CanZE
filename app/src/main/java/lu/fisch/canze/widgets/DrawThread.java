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

import lu.fisch.awt.Graphics;
import lu.fisch.canze.widgets.Drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
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
            // enable anti-aliasing
			c.setDrawFilter(new PaintFlagsDrawFilter(1, Paint.ANTI_ALIAS_FLAG));
			
			// clean background
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			c.drawRect(0, 0, c.getWidth(), c.getHeight(), paint);

            // do your paintings here ...
			drawable.draw(new Graphics(c));
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
