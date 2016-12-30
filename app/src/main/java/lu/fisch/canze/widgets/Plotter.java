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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lu.fisch.awt.Color;
import lu.fisch.awt.Graphics;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.database.CanzeDataSource;
import lu.fisch.canze.fragments.MainFragment;
import lu.fisch.canze.interfaces.DrawSurfaceInterface;

/**
 *
 * @author robertfisch
 */
public class Plotter extends Drawable {

    protected ArrayList<Double> values = new ArrayList<>();
    //protected ArrayList<Double> minValues = new ArrayList<>();
    //protected ArrayList<Double> maxValues = new ArrayList<>();
    protected ArrayList<String> sids = new ArrayList<>();

    public Plotter() {
        super();
    }

    public Plotter(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        // test
    }

    public Plotter(DrawSurfaceInterface drawSurface, int x, int y, int width, int height) {
        this.drawSurface=drawSurface;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setValue(int index, double value)
    {
        values.set(index, value);
        //if(value<minValues.get(index)) minValues.set(index,value);
        //if(value>maxValues.get(index)) maxValues.set(index,value);
    }

    @Override
    public void setValue(int value) {
        super.setValue(value);
        //addValue(value);
    }

    @Override
    public void draw(Graphics g) {
        // black border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // calculate fill height
        int fillHeight = (int) ((value-min)/(double)(max-min)*(height-1));
        int barWidth = width-Math.max(g.stringWidth(min+""),g.stringWidth(max+""))-10-10;
        int spaceAlt = Math.max(g.stringWidth(minAlt+""),g.stringWidth(maxAlt+""))+10+10;
        // reduce with if second y-axe is used
        if (minAlt==-1 && maxAlt==-1)
        {
            spaceAlt=0;
        }
        barWidth-=spaceAlt;

        // what is the graph height
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        int graphHeight = height-g.stringHeight(sdf.format(Calendar.getInstance().getTime()))-5;

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

        // draw the horizontal grid
        /*
        g.setColor(getIntermediate());
        long start = Calendar.getInstance().getTimeInMillis()/1000;
        int interval = 60/timeSale;
        for(long x=width-(start%interval)-spaceAlt; x>=width-barWidth-spaceAlt; x-=interval)
        {
            g.drawLine(x, 1, x, graphHeight + 5);
        }
        */

        /*
        MainActivity.debug("Values "+values.size());
        MainActivity.debug("Values min "+minValues.size());
        MainActivity.debug("Values max "+maxValues.size());/**/

        // draw the graph
        g.drawRect(x+width-barWidth, y, barWidth, height);
        // min & max
        /*
        if(minValues.size()>0)
        {
            double w = (double) barWidth/minValues.size();
            double h = (double) getHeight()/(getMax()-getMin()+1);

            double lastX = Double.NaN;
            double lastY = Double.NaN;
            g.setColor(Color.GREEN_DARK);
            for(int i=0; i<minValues.size(); i++)
            {
                double mx = w/2+i*w;
                double my = getHeight()-(minValues.get(i)-getMin())*h;
                if(minValues.get(i)==getMin())
                    my = getHeight()-(values.get(i)-getMin())*h;
                int rayon = 2;
                g.fillOval(getX()+getWidth()-barWidth+(int)mx-rayon,getY()+(int)my-rayon,2*rayon+1,2*rayon+1);
                if(i>0)
                {
                    g.drawLine(getX()+getWidth()-barWidth+(int)lastX,
                            getY()+(int)lastY,
                            getX()+getWidth()-barWidth+(int)mx,
                            getY()+(int)my);
                }
                lastX=mx;
                lastY=my;
            }
        }
        if(maxValues.size()>0)
        {
            double w = (double) barWidth/maxValues.size();
            double h = (double) getHeight()/(getMax()-getMin()+1);

            double lastX = Double.NaN;
            double lastY = Double.NaN;
            g.setColor(Color.BLUE);
            for(int i=0; i<maxValues.size(); i++)
            {
                double mx = w/2+i*w;
                double my = getHeight()-(maxValues.get(i)-getMin())*h;
                int rayon = 2;
                g.fillOval(getX()+getWidth()-barWidth+(int)mx-rayon,getY()+(int)my-rayon,2*rayon+1,2*rayon+1);
                if(i>0)
                {
                    g.drawLine(getX()+getWidth()-barWidth+(int)lastX,
                            getY()+(int)lastY,
                            getX()+getWidth()-barWidth+(int)mx,
                            getY()+(int)my);
                }
                lastX=mx;
                lastY=my;
            }
        }
        */
        // values
        //MainActivity.debug("PLOTTER SIZE: "+values.size());
        if(values.size()>0)
        {
            double w = (double) barWidth/values.size();
            double h = (double) getHeight()/(getMax()-getMin());

            double lastX = Double.NaN;
            double lastY = Double.NaN;
            g.setColor(Color.RED);
            for(int i=0; i<values.size(); i++)
            {
                //MainActivity.debug("Value "+i+": "+values.get(i));
                //MainActivity.debug("Value "+i+": "+values.get(i)+" Max: "+getMax()+" Min: "+getMin()+" height: "+getHeight()+" h: "+h);
                double mx = w/2+i*w;
                double my = getHeight()-(values.get(i)-getMin())*h;
                int rayon = 2;
                g.fillOval(getX()+getWidth()-barWidth+(int)mx-rayon,getY()+(int)my-rayon,2*rayon+1,2*rayon+1);
                if(i>0)
                {
                    g.drawLine(getX()+getWidth()-barWidth+(int)lastX,
                            getY()+(int)lastY,
                            getX()+getWidth()-barWidth+(int)mx,
                            getY()+(int)my);
                }
                lastX=mx;
                lastY=my;
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
        //MainActivity.debug("Plotter: "+field.getSID()+" --> "+field.getValue());
        //MainActivity.debug("Car = "+MainActivity.car+" / "+field.getCar()+" / "+field.isCar(MainActivity.car));

        if(field.isCar(MainActivity.car)) {
            String sid = field.getSID();

            //MainActivity.debug("!! Plotter: "+sid+" --> "+field.getValue());

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

    /* --------------------------------
     * Serialization
     \ ------------------------------ */

    @Override
    public void loadValuesFromDatabase() {
        super.loadValuesFromDatabase();

        values.clear();
        //maxValues.clear();
        //minValues.clear();

        for(int s=0; s<sids.size(); s++) {
            String sid = sids.get(s);
            values.add(CanzeDataSource.getInstance().getLast(sid));
            //maxValues.add(CanzeDataSource.getInstance().getMax(sid));
            //minValues.add(CanzeDataSource.getInstance().getMin(sid));
        }
    }

    @Override
    public String dataToJson() {
        Gson gson = new Gson();
        ArrayList<ArrayList<Double>> data = new ArrayList<>();
        data.add((ArrayList<Double>) values.clone());
        //data.add((ArrayList<Double>) minValues.clone());
        //data.add((ArrayList<Double>) maxValues.clone());
        return gson.toJson(data);
    }

    @Override
    public void dataFromJson(String json) {
        Gson gson = new Gson();
        Type fooType = new TypeToken<ArrayList<ArrayList<Double>>>() {}.getType();

        ArrayList<ArrayList<Double>> data = gson.fromJson(json, fooType);
        values = data.get(0);
        //minValues=data.get(1);
        //maxValues=data.get(2);
    }


    public void setValues(ArrayList<Double> values) {
        this.values     = values;
    }
}
