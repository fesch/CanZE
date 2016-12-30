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
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.database.CanzeDataSource;
import lu.fisch.canze.interfaces.DrawSurfaceInterface;

/**
 * Created by robertfisch on 26.10.2015.
 */
public class BarGraph extends Plotter {

    public BarGraph() {
        super();
    }

    public BarGraph(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public BarGraph(DrawSurfaceInterface drawSurface, int x, int y, int width, int height) {
        super(drawSurface,x,y,width,height);
    }

    @Override
    public void draw(Graphics g) {
        // black border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // calculate fill height
        int fillHeight = (int) ((value-min)/(double)(max-min)*(height-1));
        int barWidth = width-Math.max(g.stringWidth(min+""),g.stringWidth(max+""))-10-10;

        // draw the ticks
        if(minorTicks>0 || majorTicks>0)
        {
            int toTicks = minorTicks;
            if(toTicks==0) toTicks=majorTicks;
            double accel = (double)height/((max-min)/(double)toTicks);
            double ax,ay,bx,by;
            int actual = min;
            int sum = 0;
            for(double i=height; i>=0; i-=accel)
            {
                if(minorTicks>0)
                {
                    g.setColor(Color.GRAY);
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
                        g.setColor(Color.GRAY_LIGHT);
                        ax = x+width-barWidth-10;
                        ay = y+i;
                        bx = x+width;
                        by = y+i;
                        g.drawLine((int)ax, (int)ay, (int)bx, (int)by);

                        g.setColor(Color.GRAY);
                        ax = x+width-barWidth-10;
                        ay = y+i;
                        bx = x+width-barWidth;
                        by = y+i;
                        g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                    }

                    // draw String
                    if(showLabels)
                    {
                        g.setColor(Color.GRAY);
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

        // draw the graph
        g.drawRect(x+width-barWidth, y, barWidth, height);
        if(values.size()>0)
        {
            double w = (double) barWidth/values.size();
            double h = (double) getHeight()/(getMax()-getMin());

            for(int i=0; i<values.size(); i++)
            {
                double mx = i*w;
                double my;
                int padding = 2;
                // max value
                /*
                if(i<maxValues.size()) {
                    my = getHeight() - (maxValues.get(i) - getMin()) * h;
                    g.setColor(Color.GREEN_DARK);
                    g.fillRect(
                            (float) (getX() + getWidth() - barWidth + (int) mx) + padding,
                            (float) (getY() + (int) my),
                            (float) w - 2 * padding,
                            (float) 2 //(getHeight()-my)
                    );
                }
                // min value
                if(i<minValues.size()) {
                    my = getHeight() - (minValues.get(i) - getMin()) * h;
                    g.setColor(Color.GREEN_DARK);
                    g.fillRect(
                            (float) (getX() + getWidth() - barWidth + (int) mx) + padding,
                            (float) (getY() + (int) my),
                            (float) w - 2 * padding,
                            (float) 2 //(getHeight() - my)
                    );
                }
                */
                // value
                my = getHeight()-(values.get(i)-getMin())*h;
                g.setColor(Color.RED);
                g.fillRect(
                        (float) (getX() + getWidth() - barWidth + (int) mx)+padding,
                        (float) (getY() + (int) my),
                        (float) w-2*padding,
                        (float) 2 //(getHeight()-my)
                );
            }
        }

        // draw the title
        if(title!=null && !title.equals(""))
        {
            g.setColor(Color.BLUE);
            g.setTextSize(20);
            int th = g.stringHeight(title);
            int tx = getX()+width-barWidth+8;
            int ty = getY()+th+4;
            g.drawString(title,tx,ty);
        }
    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        // only take data fofr valid cars
        if(field.getCar()==0 || field.getCar()== MainActivity.car) {
            String sid = field.getSID();

            //MainActivity.debug("Plotter: "+sid+" --> "+field.getValue());

            int index = sids.indexOf(sid);
            if (index == -1) {
                sids.add(sid);
                values.add(field.getValue());
                //minValues.add(CanzeDataSource.getInstance().getMin(sid));
                //maxValues.add(CanzeDataSource.getInstance().getMax(sid));
            } else setValue(index, field.getValue());
            // only repaint if the last field has been updated
            //if(index==sids.size()-1)
            super.onFieldUpdateEvent(field);
        }
    }

}
