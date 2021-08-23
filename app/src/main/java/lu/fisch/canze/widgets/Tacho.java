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

import android.content.res.Resources;
import android.graphics.Point;
import android.util.TypedValue;

import com.google.gson.Gson;

import lu.fisch.awt.Color;
import lu.fisch.awt.Graphics;
import lu.fisch.awt.Polygon;
import lu.fisch.canze.interfaces.DrawSurfaceInterface;

/**
 *
 * @author robertfisch
 */
public class Tacho extends Drawable {

    protected double angle = 240;

    protected boolean autoRange = false;

    protected int padding = 5;

    public Tacho() {
        super();
    }

    public Tacho(DrawSurfaceInterface drawSurface, int x, int y, int width, int height) {
        this.drawSurface=drawSurface;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Tacho(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g)
    {
        // clean the surface
        //g.setColor(Color.WHITE);
        //g.fillRect(0,0,getWidth(),getHeight());
        // background
        g.setColor(getBackground());
        g.fillRect(x, y, width, height);

        // black border
        g.setColor(getForeground());
        g.drawRect(x, y, width, height);

        g.setTextSize(12);

        double alpha = (360-angle)/2.;
        
        // determine the rayon to be used
        double rayon = 0;
        if(angle>=180)
            rayon = Math.min(width/2.,height/(1+Math.sin(mkRad(90-alpha))))-padding;
        else
            rayon = Math.min((width/2)/Math.cos(mkRad(90-angle/2)),height)-padding;
        // determine center point
        Point center = new Point(x+(width/2),y+(int)(rayon+padding));

        //g.setColor(Color.BLACK);
        g.setColor(getForeground());
        g.drawRect(x, y, width, height);
        // draw the frame
            //g.setColor(Color.GRAY_DARK);
            g.setColor(getIntermediate());
            double ax,ay,bx=0,by=0;
            // draw right arm
            ax = center.x+rayon*Math.cos(mkRad(-90+alpha));
            ay = center.y-rayon*Math.sin(mkRad(-90+alpha));
            //g.drawLine(center.x,center.y,(int)ax,(int)ay);
            // draw arc
            for(double i=-90+alpha; i<=360-90-alpha; i+=2)
            {
                bx = center.x+rayon*Math.cos(mkRad(i));
                by = center.y-rayon*Math.sin(mkRad(i));
                g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                ax=bx;
                ay=by;
            }
            // draw left arm
            ax = center.x+rayon*Math.cos(mkRad(-90-alpha));
            ay = center.y-rayon*Math.sin(mkRad(-90-alpha));
            //g.drawLine(center.x,center.y,(int)ax,(int)ay);
            // draw last chunk of arc
            g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
        // draw the labels
            g.setColor(getForeground());
            //g.setColor(Color.BLACK);
            double dist = 360-2*alpha;
            int actual = min;
        // draw the minor ticks
            if(minorTicks >0 || majorTicks>0) {
                //g.setColor(Color.GRAY_DARK);
                g.setColor(getIntermediate());
                int toTicks = minorTicks;
                if (toTicks == 0) toTicks = majorTicks;
                double accel = dist / ((max - min) / toTicks);
                int sum = 0;
                for (double i = 360 - 90 - alpha; i > (int)( -90 + alpha)-accel; i -= accel) {
                    if (minorTicks > 0) {
                        ax = center.x + (rayon) * Math.cos(mkRad(i));
                        ay = center.y - (rayon) * Math.sin(mkRad(i));
                        bx = center.x + (rayon - 6) * Math.cos(mkRad(i));
                        by = center.y - (rayon - 6) * Math.sin(mkRad(i));
                        g.drawLine((int) ax, (int) ay, (int) bx, (int) by);
                    }
                    // draw majorTicks
                    if (majorTicks != 0 && sum % majorTicks == 0) {
                        if (majorTicks > 0) {
                            ax = center.x + (rayon) * Math.cos(mkRad(i));
                            ay = center.y - (rayon) * Math.sin(mkRad(i));
                            bx = center.x + (rayon - 12) * Math.cos(mkRad(i));
                            by = center.y - (rayon - 12) * Math.sin(mkRad(i));
                            g.drawLine((int) ax, (int) ay, (int) bx, (int) by);
                        }

                        // draw String
                        if (showLabels) {
                            bx = center.x + (rayon - 24) * Math.cos(mkRad(i));
                            by = center.y - (rayon - 24) * Math.sin(mkRad(i));
                            String text = (actual) + "";
                            double sw = g.stringWidth(text);
                            g.drawString(text, (int) (bx - (sw/2. + sw/2.*Math.cos(mkRad(i))) ), (int) (by + g.stringHeight(text) / 2));
                        }

                        actual += majorTicks;
                    }
                    sum += minorTicks;
                }
            }
        // draw the needle
            g.setColor(Color.RED);
            double rota = 90+alpha+dist*(value-min)/(max-min);
            Polygon p = new Polygon();
            double needleLength = rayon-45;
            double pointerLength = rayon*0.1;
            double angleDiff = 5;
            p.addPoint(center.x, 
                       center.y);
            p.addPoint((int)(center.x+(needleLength-pointerLength)*Math.cos(mkRad(rota + angleDiff))),
                       (int)(center.y+(needleLength-pointerLength)*Math.sin(mkRad(rota + angleDiff))));
            p.addPoint((int)(center.x+needleLength*Math.cos(mkRad(rota))),
                       (int)(center.y+needleLength*Math.sin(mkRad(rota))));
            p.addPoint((int)(center.x+(needleLength-pointerLength)*Math.cos(mkRad(rota-angleDiff))),
                       (int)(center.y+(needleLength-pointerLength)*Math.sin(mkRad(rota - angleDiff))));
            g.fillPolygon(p);
        // draw the value
            if(showValue)
            {
                if(field !=null) {
                    //g.setTextSize(Math.min(width / 7, 40));
                    g.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, Resources.getSystem().getDisplayMetrics()));
                    String text = String.format("%." + String.valueOf(field.getDecimals()) + "f", field.getValue());
                    int tw = g.stringWidth(text);
                    int th = g.stringHeight(text);
                    int tx = center.x-tw/2-3;
                    int ty = center.y+th;
                        g.setColor(getBackground());
                        //g.setColor(Color.WHITE);
                        g.fillRect(tx - 1, ty - th, tw + 7, th + 5);
                    //g.setColor(Color.BLACK);
                    //g.drawRect(tx - 1, ty - th, tw + 7, th +5);
                    g.setColor(getForeground());
                    //g.setColor(Color.GREEN_DARK);
                    g.drawString(text, tx, ty);
                }
            }
        // draw the title
            if(title!=null && !title.equals(""))
            {
                //g.setColor(Color.BLUE);
                g.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, Resources.getSystem().getDisplayMetrics()));
                //g.setTextSize(20);
                int tw = g.stringWidth(title);
                //int th = g.stringHeight(title);
                int tx = getX()+getWidth()/2-tw/2;
                int ty = getY()+getHeight()-8;
                g.drawString(title,tx,ty);
            }
    }
    
    /* --------------------------------
     * Getters & setters
     \ ------------------------------ */

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setValue(int value) {
        if(autoRange)
        {
            if(value<min)      min=value;
            else if(value>max) max=value;
        }
        else
        {
            if(value<min)      value=min;
            else if(value>max) value=max;
        }
        this.value = value;
    }


    /* --------------------------------
     * Serialization
     \ ------------------------------ */

    @Override
    public String dataToJson() {
        return "";
    }

    @Override
    public void dataFromJson(String json) {
    }
    
    
}
