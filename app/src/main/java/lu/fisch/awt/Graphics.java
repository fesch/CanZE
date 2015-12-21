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

package lu.fisch.awt;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.style.TextAppearanceSpan;

public class Graphics 
{
	private Canvas canvas;
	private Color color = Color.BLACK;
	private Paint paint = new Paint();
	private float textSize = 12;
	
	public Graphics(Canvas canvas)
	{
		this.canvas=canvas;
		paint.setAntiAlias(true);
	}
	
	public Canvas getCanvas()
	{
		return canvas;
	}
	
	public int getWidth()
	{
		return canvas.getWidth();
	}
	
	public int getHeight()
	{
		return canvas.getHeight();
	}
	
	public void fillOval(int x, int y, int width, int height)
	{
		fillOval((float) x, (float) y, (float) width, (float) height);
	}
	
	public void fillOval(float x, float y, float width, float height)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.FILL);
		RectF oval = new RectF(x,y,x+width,y+height);
		canvas.drawOval(oval, paint);
	}
	
	public void drawOval(int x, int y, int width, int height)
	{
		drawOval((float) x, (float) y, (float) width, (float) height);
	}
	
	public void drawOval(float x, float y, float width, float height)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.STROKE);
		RectF oval = new RectF(x,y,x+width,y+height);
		canvas.drawOval(oval, paint);
	}
	
	public void fillRect(int x, int y, int width, int height)
	{
		fillRect((float) x, (float) y, (float) width, (float) height);
	}
	
	public void fillRect(float x, float y, float width, float height)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.FILL);
		RectF rect = new RectF(x,y,x+width,y+height);
		canvas.drawRect(rect, paint);
	}
	
	public void drawRect(int x, int y, int width, int height)
	{
		drawRect((float) x, (float) y, (float) width, (float) height);
	}
	
	public void drawRect(float x, float y, float width, float height)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.STROKE);
		RectF rect = new RectF(x,y,x+width,y+height);
		canvas.drawRect(rect, paint);
	}
	
	public void drawRoundRect(int x, int y, int width, int height, int rx, int ry)
	{
		drawRoundRect((float) x, (float) y, (float) width, (float) height, (float) rx, (float) ry);
	}
	
	public void drawRoundRect(float x, float y, float width, float height, float rx, float ry)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.STROKE);
		RectF rect = new RectF(x,y,x+width,y+height);
		canvas.drawRoundRect(rect, rx, ry, paint);
	}
	
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		drawLine((float) x1, (float) y1, (float) x2, (float) y2);
	}
	
	public void drawLine(float x1, float y1, float x2, float y2)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawLine(x1, y1, x2, y2, paint);
	}
	
	public void drawString(String text, int x, int y)
	{
		drawString(text, (float) x, (float) y);
	}
	
	public void drawString(String text, float x, float y)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.FILL);
		canvas.drawText(text, x, y, paint);
	}
	
	public void drawString(String text, int x, int y, int size)
	{
		drawString(text, (float) x, (float) y, (float) size);
	}
	
	public void drawString(String text, float x, float y, float size)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(size);
		canvas.drawText(text, x, y, paint);
	}
	
	public void setColor(Color color)
	{
		this.color=color;
	}
	
	public Color getColor(Color color)
	{
		return color;
	}

	public void drawPolygon(Polygon p)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.STROKE);

		Path path = new Path();
		path.moveTo(p.get(p.size()-1).x,p.get(p.size()-1).y);
		for(int i=0; i<p.size(); i++)
		{
			Point from = p.get(i);
			path.lineTo(from.x,from.y);
		}
		canvas.drawPath(path,paint);
	}

	public void fillPolygon(Polygon p)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.FILL_AND_STROKE);

		Path path = new Path();
		path.moveTo(p.get(p.size()-1).x,p.get(p.size()-1).y);
		for(int i=0; i<p.size(); i++)
		{
			Point from = p.get(i);
			path.lineTo(from.x,from.y);
		}
		canvas.drawPath(path, paint);
	}

	public void setGradient(int x1, int y1, int x2, int y2, int[] colors, float[] spacings)
	{
		LinearGradient lg = new LinearGradient(x1, y1, x2, y2,
				colors,
				spacings,
				Shader.TileMode.REPEAT);
		paint.setShader(lg);
	}

	public void clearGradient()
	{
		paint.setShader(null);
	}

	/*public void fillPolygon(Polygon p, int x1, int y1, int x2, int y2, int[] colors, float[] spacings)
	{
		paint.setColor(color.getAndroidColor());
		paint.setStyle(Paint.Style.FILL_AND_STROKE);

		LinearGradient lg = new LinearGradient(x1, y1, x2, y2,
				colors,
				spacings,
				Shader.TileMode.REPEAT);
		paint.setShader(lg);

		Path path = new Path();
		path.moveTo(p.get(p.size()-1).x,p.get(p.size()-1).y);
		for(int i=0; i<p.size(); i++)
		{
			Point from = p.get(i);
			path.lineTo(from.x,from.y);
		}
		canvas.drawPath(path, paint);

        paint.setShader(null);
	}*/


	public void rotate(float degrees, float cx, float cy)
    {
        canvas.rotate(degrees,cx,cy);
    }

	/* *********************************
	 * String things ...
	 * *********************************/

	public void setTextSize(float textSize)
	{
        this.textSize=textSize;
		paint.setTextSize(textSize);
	}

	public float getTextSize()
	{
		return paint.getTextSize();
	}

	public int stringWidth(String _string)
	{
		android.graphics.Rect bounds = new android.graphics.Rect();
		Paint textPaint = new Paint();
		textPaint.setTextSize(textSize);
		textPaint.getTextBounds(_string,0,_string.length(),bounds);
		return bounds.width();
        //return (int) textPaint.measureText(_string);
	}


	public int stringHeight(String _string)
	{
		if(_string.equals("")) _string="O";
		android.graphics.Rect bounds = new android.graphics.Rect();
		Paint textPaint = new Paint();
		textPaint.setTextSize(textSize);
		textPaint.getTextBounds(_string, 0, _string.length(),bounds);
		return bounds.height();
	}

	public Paint getPaint() {
		return paint;
	}
}
