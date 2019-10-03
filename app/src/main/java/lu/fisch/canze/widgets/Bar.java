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


import lu.fisch.awt.Color;
import lu.fisch.awt.Graphics;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.interfaces.DrawSurfaceInterface;

/**
 *
 * @author robertfisch
 */
public class Bar extends Drawable {

    public Bar() {
        super();
    }

    public Bar(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        // test
    }

    public Bar(DrawSurfaceInterface drawSurface, int x, int y, int width, int height) {
        this.drawSurface=drawSurface;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics g) {

        // black border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        int value = this.value;
        if (inverted) value=-value;

            // calculate fill height
        int fillHeight = (int) ((value-min)/(double)(max-min)*(height-1));
        if(value<min) fillHeight = (int) ((min-min)/(double)(max-min)*(height-1));
        else if(value>max) fillHeight = (int) ((max-min)/(double)(max-min)*(height-1));

        int barWidth = width-Math.max(g.stringWidth(min+""),g.stringWidth(max+""))-10-10-g.stringHeight(title)-4;
        int startY = y+height-fillHeight;

        // if min is below zero, we want to start at that position
        if(min<0)
        {
            int zero = (int) ((0-min)/(double)(max-min)*(height-1));
            startY = y+zero;

            if(value>0)
            {
                fillHeight=zero-(height-fillHeight);
                startY-=fillHeight;
            }
            else
            {
                fillHeight=zero-fillHeight;
            }
        }

        // draw the filled part
        g.drawRect(x + width - barWidth, y, barWidth, height);
        g.setColor(Color.RED);

        String sid = getSids().get(0);
        if(getOptions().getOption(sid)!=null &&
                getOptions().getOption(sid).contains("gradient")) {

            int[] colors = colorRanges.getColors(sid);
            float[] spacings = colorRanges.getSpacings(sid, min, max);
            if(colors.length==spacings.length)
            {
                g.setGradient(0, height-1, 0, 0, colors, spacings);

            }
        }

        g.fillRect(x + 1 + width - barWidth, startY, barWidth - 2, fillHeight);

        g.clearGradient();

        // draw the ticks
        if(minorTicks>0 || majorTicks>0)
        {
            g.setColor(Color.GRAY_DARK);
            int toTicks = minorTicks;
            if(toTicks==0) toTicks=majorTicks;
            double accel = (double)height/((max-min)/(double)toTicks);
            double ax,ay,bx=0,by=0;
            int actual = min;
            int sum = 0;
            for(double i=height; i>=0; i-=accel)
            {
                if(minorTicks>0)
                {
                    ax = x+width-barWidth-5;
                    ay = y+i;
                    bx = x+width-barWidth;
                    by = y+i;
                    g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                }
                // draw majorTicks
                if(majorTicks!=0 && sum % majorTicks == 0) {
                    if(majorTicks>0)
                    {
                        ax = x+width-barWidth-10;
                        ay = y+i;
                        bx = x+width-barWidth;
                        by = y+i;
                        g.drawLine((int)ax, (int)ay, (int)bx, (int)by);

                        if(ay!=y+height && (int)i!=0)
                        {
                            g.setColor(Color.WHITE);
                            g.drawLine(x+1+width-barWidth, (int)ay, x+width-1, (int)by);
                            g.setColor(Color.GRAY_DARK);
                        }
                    }

                    // draw String
                    if(showLabels)
                    {
                        String text = (actual)+"";
                        double sw = g.stringWidth(text);
                        bx = x+width-barWidth-16-sw;
                        by = y+i;
                        g.drawString(text, (int)(bx), (int)(by+g.stringHeight(text)*(1-i/height)));
                    }

                    actual+=majorTicks;
                }
                sum+=minorTicks;
            }
        }
        // draw the value
        if(showValue)
        {
            g.setTextSize(Math.min(width/7,40));
            String text = String.format("%." + String.valueOf(field.getDecimals()) + "f", field.getValue());
            int tw = g.stringWidth(text);
            int th = g.stringHeight(text);
            int tx = x+width-barWidth/2-tw/2;
            int ty = y+height/2+th/2;
            g.setColor(Color.BLACK);
            g.drawString(text, tx, ty);
        }
        // draw the title
        if(title!=null && !title.equals(""))
        {
            g.setColor(getTitleColor());
            g.setTextSize(16);
            int tw = g.stringWidth(title);
            int th = g.stringHeight(title);
            int tx = x; //x+width-barWidth/2-tw/2;
            int ty = y+height; //getY()+getHeight()-8;
            g.rotate(-90, tx, ty);
            g.drawString(title,tx+4,ty+th+2);
            g.rotate(90,tx,ty);
        }

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
