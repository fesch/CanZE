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

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import lu.fisch.awt.Color;
import lu.fisch.awt.Graphics;
import lu.fisch.awt.Polygon;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.classes.TimePoint;
import lu.fisch.canze.database.CanzeDataSource;
import lu.fisch.canze.interfaces.DrawSurfaceInterface;

/**
 *
 * @author robertfisch
 */
public class Timeplot extends Drawable {

    protected HashMap<String,ArrayList<TimePoint>> values = new HashMap<>();

    private boolean backward = true;

    public Timeplot() {
        super();
    }

    public Timeplot(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        // test
    }

    public Timeplot(DrawSurfaceInterface drawSurface, int x, int y, int width, int height) {
        this.drawSurface=drawSurface;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addValue(String fieldSID, double value)
    {
        //MainActivity.debug(values.size()+"");
        if(!values.containsKey(fieldSID)) values.put(fieldSID,new ArrayList<TimePoint>());
        values.get(fieldSID).add(new TimePoint(Calendar.getInstance().getTimeInMillis(), value));

        /*
        if(value<min) setMin((int) value - 1);
        else if(value>max) setMax((int) value + 1);
        */

        /*setMinorTicks(0);
        setMajorTicks(1);
        if(getMax()-getMin()>100) setMajorTicks(10);
        else if(getMax()-getMin()>1000) setMajorTicks(100);
        else if(getMax()-getMin()>10000) setMajorTicks(1000);
        /**/
    }

    private Color getColor(int i)
    {
        if(i==0) return Color.RENAULT_RED;
        else if (i==1) return Color.BLUE;
        else return Color.GREEN_DARK;
    }

    @Override
    public void draw(Graphics g) {
        // background
        g.setColor(getBackground());
        g.fillRect(x, y, width, height);

        // black border
        g.setColor(getForeground());
        g.drawRect(x, y, width, height);

        // calculate fill height
        //int fillHeight = (int) ((value-min)/(double)(max-min)*(height-1));
        int barWidth = width-Math.max(g.stringWidth(min+""),g.stringWidth(max+""))-10-10;
        int spaceAlt = Math.max(g.stringWidth(minAlt+""),g.stringWidth(maxAlt+""))+10+10;
        // reduce with if second y-axe is used
        //MainActivity.debug("Alt: "+minAlt+" - "+maxAlt);
        if (minAlt==0 && maxAlt==0)
        {
            spaceAlt=0;
        }
        barWidth-=spaceAlt;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        int graphHeight = height-g.stringHeight(sdf.format(Calendar.getInstance().getTime()))-5;

        // draw the ticks
        double realMaxAlt=getMaxAlt();
        if(minorTicks>0 || majorTicks>0)
        {
            int toTicks = minorTicks;
            if(toTicks==0) toTicks=majorTicks;
            double intervals = ((max-min)/(double)toTicks);
            double accel = (double)graphHeight/intervals;
            double ax,ay,bx=0,by=0;
            int actual = min;
            int actualAlt = minAlt;
            int sum = 0;
            for(double i=graphHeight; i>=0; i-=accel)
            {
                if(minorTicks>0)
                {
                    g.setColor(getForeground());
                    ax = x+width-barWidth-spaceAlt-5;
                    ay = y+i;
                    bx = x+width-barWidth-spaceAlt;
                    by = y+i;
                    g.drawLine((int)ax, (int)ay, (int)bx, (int)by);

                    if(spaceAlt!=0) {
                        ax = x+width-spaceAlt;
                        ay = y+i;
                        bx = ax+5;
                        by = y+i;
                        g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                    }
                }
                // draw majorTicks
                if(majorTicks!=0 && sum % majorTicks == 0) {
                    if(majorTicks>0)
                    {
                        g.setColor(getIntermediate());
                        ax = x+width-spaceAlt-barWidth-10;
                        ay = y+i;
                        bx = x+width-spaceAlt+(spaceAlt!=0?10:0);
                        by = y+i;
                        g.drawLine((int) ax, (int) ay, (int) bx, (int) by);

                        g.setColor(getForeground());
                        ax = x+width-barWidth-spaceAlt-10;
                        ay = y+i;
                        bx = x+width-barWidth-spaceAlt;
                        by = y+i;
                        g.drawLine((int)ax, (int)ay, (int)bx, (int)by);

                        if(spaceAlt!=0)
                        {
                            ax = x+width-spaceAlt;
                            ay = y+i;
                            bx = ax+10;
                            by = y+i;
                            g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                        }
                    }

                    // draw String
                    if(showLabels)
                    {
                        g.setColor(getForeground());
                        String text = (actual)+"";
                        double sw = g.stringWidth(text);
                        bx = x+width-barWidth-16-sw-spaceAlt;
                        by = y+i;
                        g.drawString(text, (int)(bx), (int)(by+g.stringHeight(text)*(1-i/graphHeight)));

                        // alternative labels
                        if(spaceAlt!=0)
                        {
                            text = (actualAlt)+"";
                            sw = g.stringWidth(text);
                            bx = x+width-spaceAlt+16;
                            by = y+i;
                            g.drawString(text, (int)(bx), (int)(by+g.stringHeight(text)*(1-i/graphHeight)));
                        }
                    }

                    actual+=majorTicks;
                    actualAlt+= Math.round((double) (maxAlt - minAlt) / (max - min) * majorTicks);
                }
                sum+=minorTicks;
            }
            // calculate real max alt
            double altTicks = Math.round((double) (maxAlt - minAlt) / (max - min) * majorTicks)/((double)majorTicks/toTicks);
            realMaxAlt=intervals*altTicks+getMinAlt();
        }

        // draw the vertical grid
        g.setColor(getIntermediate());
        long start = Calendar.getInstance().getTimeInMillis()/1000;
        int interval = 60/timeSale;

        try
        {
            if(backward)
            {
                ArrayList<TimePoint> list = this.values.get(sids.get(0));
                start = list.get(list.size()-1).date;
                for(int s=0; s<sids.size(); s++) {
                    list = this.values.get(sids.get(s));
                    long thisDate = list.get(list.size()-1).date;
                    if(thisDate>start) start=thisDate;
                }
                //MainActivity.debug("Start: "+sdf.format(start));
                for(long x=width-(start%interval)-spaceAlt; x>=width-barWidth-spaceAlt; x-=interval)
                {
                    g.drawLine(x, 1, x, graphHeight + 5);
                }
            }
            else
            {
                ArrayList<TimePoint> list = this.values.get(sids.get(0));
                start = list.get(0).date;
                for(int s=0; s<sids.size(); s++) {
                    list = this.values.get(sids.get(s));
                    long thisDate = list.get(0).date;
                    if(thisDate<start) start=thisDate;
                }
                for(long x=width-barWidth-spaceAlt; x<width-spaceAlt; x+=interval)
                {
                    g.drawLine(x, 1, x, graphHeight + 5);
                }
            }
        }
        catch(Exception e) {
            //MainActivity.debug("Exception: "+e.getMessage());
            for(long x=width-(start%interval)-spaceAlt; x>=width-barWidth-spaceAlt; x-=interval)
            {
                g.drawLine(x, 1, x, graphHeight + 5);
            }
        }


        // draw the graph
        for(int s=0; s<sids.size(); s++) {
            String sid = sids.get(s);
            ArrayList<TimePoint> values = this.values.get(sid);

            // setup an empty list if no list has been found
            if(values==null) {
                values = new ArrayList<>();
                this.values.put(sid,values);
            }

            g.setColor(getForeground());
            g.drawRect(x + width - barWidth-spaceAlt, y, barWidth, graphHeight);
            if (values.size() > 0) {

                double w = (double) barWidth / values.size();
                double h = (double) graphHeight / (getMax() - getMin());
                double hAlt = (double) graphHeight / (realMaxAlt - getMinAlt());

                double lastX = Double.NaN;
                double lastY = Double.NaN;
                g.setColor(getColor(s));

                if(isBackward())
                {

                    long maxTime = start; //values.get(values.size() - 1).date;

                    for (int i = values.size() - 1; i >= 0; i--) {
                        TimePoint tp = values.get(i);

                        if (tp != null) {
                            g.setColor(colorRanges.getColor(sid, tp.value, getColor(s)));

                            double mx = barWidth - ((maxTime - tp.date) / timeSale / 1000);

                            if (mx < 0) {
                                values.remove(i);
                            } else {
                                // determine Y
                                double my;
                                // distinct "alt" vs "normal"
                                if (getOptions().getOption(sid) != null &&
                                        getOptions().getOption(sid).contains("alt"))
                                    my = graphHeight - (tp.value - minAlt) * hAlt;
                                else
                                    my = graphHeight - (tp.value - min) * h;

                                // check if y should be fixed: colorline[value-of-y]
                                if ((getOptions().getOption(sid) != null &&
                                        !getOptions().getOption(sid).isEmpty() &&
                                        getOptions().getOption(sid).contains("colorline"))) {

                                    // parse out position of line
                                    String options = getOptions().getOption(sid);
                                    int index = options.indexOf("colorline");
                                    index += ("colorline").length() + 1;
                                    String value = "";
                                    while (index < options.length() && options.charAt(index) != ']') {
                                        value += options.charAt(index);
                                        index++;
                                    }
                                    if (getOptions().getOption(sid) != null &&
                                            getOptions().getOption(sid).contains("alt"))
                                        my = graphHeight - (Double.valueOf(value) - minAlt) * hAlt;
                                    else
                                        my = graphHeight - (Double.valueOf(value) - min) * h;
                                }

                                // now get ZY
                                double zy;
                                if (getOptions().getOption(sid) != null &&
                                        getOptions().getOption(sid).contains("alt"))
                                    zy = graphHeight - (0 - minAlt) * hAlt;
                                else
                                    zy = graphHeight - (0 - min) * h;

                                int rayon = 2;

                                //MainActivity.debug("HERE: "+sid+" / "+getOptions().getOption(sid));

                                if (getOptions().getOption(sid) == null ||
                                        (getOptions().getOption(sid) != null &&
                                                (getOptions().getOption(sid).isEmpty() || getOptions().getOption(sid).contains("dot")))) {
                                    g.fillOval(getX() + getWidth() - barWidth + (int) mx - rayon - spaceAlt,
                                            getY() + (int) my - rayon,
                                            2 * rayon + 1,
                                            2 * rayon + 1);
                                }

                                if (i < values.size() - 1) {
                                    if (getOptions().getOption(sid) != null &&
                                            getOptions().getOption(sid).contains("full")) {
                                        if ((lastY != Double.NaN) && (lastX != Double.NaN) && (lastY != 0.0) && (lastX != 0.0)) {
                                            Polygon p = new Polygon();
                                            p.addPoint(getX() + getWidth() - barWidth + (int) lastX - spaceAlt,
                                                    getY() + (int) lastY);
                                            p.addPoint(getX() + getWidth() - barWidth + (int) mx - spaceAlt,
                                                    getY() + (int) my);
                                            p.addPoint(getX() + getWidth() - barWidth + (int) mx - spaceAlt,
                                                    (int) (getY() + zy));
                                            p.addPoint(getX() + getWidth() - barWidth + (int) lastX - spaceAlt,
                                                    (int) (getY() + zy));
                                            g.fillPolygon(p);
                                        }
                                    } else if (getOptions().getOption(sid) != null &&
                                            getOptions().getOption(sid).contains("gradient")) {

                                        if (i < values.size() && values.get(i + 1) != null) {
                                            if ((lastY != Double.NaN) && (lastX != Double.NaN) && (lastY != 0.0) && (lastX != 0.0)) {
                                                Polygon p = new Polygon();
                                                p.addPoint(getX() + getWidth() - barWidth + (int) lastX - spaceAlt,
                                                        getY() + (int) lastY);
                                                p.addPoint(getX() + getWidth() - barWidth + (int) mx - spaceAlt,
                                                        getY() + (int) my);
                                                p.addPoint(getX() + getWidth() - barWidth + (int) mx - spaceAlt,
                                                        (int) (getY() + zy));
                                                p.addPoint(getX() + getWidth() - barWidth + (int) lastX - spaceAlt,
                                                        (int) (getY() + zy));

                                                int[] colors = colorRanges.getColors(sid);
                                                float[] spacings = colorRanges.getSpacings(sid, min, max);
                                                if (colors.length == spacings.length)
                                                    g.setGradient(0, graphHeight, 0, 0, colors, spacings);
                                                g.fillPolygon(p);
                                                g.clearGradient();

                                                //else MainActivity.debug("size not equal: "+colors.length+"=="+spacings.length);
                                            }
                                        }
                                    } else {
                                        if(lastX!=Double.NaN && lastY!=Double.NaN && lastX!=0.0 && lastY!=0.0) {

                                                g.drawLine(getX() + getWidth() - barWidth + (int) lastX - spaceAlt,
                                                        getY() + (int) lastY,
                                                        getX() + getWidth() - barWidth + (int) mx - spaceAlt,
                                                        getY() + (int) my);
                                        }
                                    }
                                }
                                lastX = mx;
                                lastY = my;
                            }
                        }
                    }
                }
                else // forward
                {
                    long minTime = start; //values.get(0).date;

                    for (int i = 0; i < values.size() ; i++) {
                            TimePoint tp = values.get(i);

                            if (tp != null) {
                                g.setColor(colorRanges.getColor(sid, tp.value, getColor(s)));

                                double mx =  ((tp.date-minTime) / timeSale / 1000);

                                if (mx > barWidth) {
                                    // ignore point that are out of scope but do not delete them
                                    //values.remove(i);
                                } else {
                                    double my = graphHeight - (tp.value - min) * h;

                                    // check if y should be fixed: colorline[value-of-y]
                                    if ((getOptions().getOption(sid) != null &&
                                            !getOptions().getOption(sid).isEmpty() &&
                                            getOptions().getOption(sid).contains("colorline"))) {

                                        // parse out position of line
                                        String options = getOptions().getOption(sid);
                                        int index = options.indexOf("colorline");
                                        index += ("colorline").length() + 1;
                                        String value = "";
                                        while (index < options.length() && options.charAt(index) != ']') {
                                            value += options.charAt(index);
                                            index++;
                                        }
                                        my = graphHeight - (Double.valueOf(value) - min) * h;
                                    }

                                    double zy = graphHeight - (0 - min) * h;

                                    // draw on alternate scale if requested
                                    if (getOptions().getOption(sid) != null &&
                                            getOptions().getOption(sid).contains("alt")) {
                                        my = graphHeight - (tp.value - minAlt) * hAlt;
                                        zy = graphHeight - (0 - minAlt) * hAlt;
                                    }

                                    int rayon = 2;

                                    //MainActivity.debug("HERE: "+sid+" / "+getOptions().getOption(sid));

                                    if (getOptions().getOption(sid) == null ||
                                            (getOptions().getOption(sid) != null &&
                                                    (getOptions().getOption(sid).isEmpty() || getOptions().getOption(sid).contains("dot")))) {
                                        g.fillOval(getX() + getWidth() - barWidth + (int) mx - rayon - spaceAlt,
                                                getY() + (int) my - rayon,
                                                2 * rayon + 1,
                                                2 * rayon + 1);
                                    }

                                    if (i > 0) {
                                        if (getOptions().getOption(sid) != null &&
                                                getOptions().getOption(sid).contains("full")) {
                                            if ((lastY != Double.NaN) && (lastX != Double.NaN) && (lastY != 0.0) && (lastX != 0.0)) {
                                                Polygon p = new Polygon();
                                                p.addPoint(getX() + getWidth() - barWidth + (int) lastX - spaceAlt,
                                                        getY() + (int) lastY);
                                                p.addPoint(getX() + getWidth() - barWidth + (int) mx - spaceAlt,
                                                        getY() + (int) my);
                                                p.addPoint(getX() + getWidth() - barWidth + (int) mx - spaceAlt,
                                                        (int) (getY() + zy));
                                                p.addPoint(getX() + getWidth() - barWidth + (int) lastX - spaceAlt,
                                                        (int) (getY() + zy));
                                                g.fillPolygon(p);
                                            }
                                        } else if (getOptions().getOption(sid) != null &&
                                                getOptions().getOption(sid).contains("gradient")) {

                                            //if (i < values.size() && values.get(i - 1) != null) {
                                                if ((lastY != Double.NaN) && (lastX != Double.NaN) && (lastY != 0.0) && (lastX != 0.0)) {
                                                    Polygon p = new Polygon();
                                                    p.addPoint(getX() + getWidth() - barWidth + (int) lastX - spaceAlt,
                                                            getY() + (int) lastY);
                                                    p.addPoint(getX() + getWidth() - barWidth + (int) mx - spaceAlt,
                                                            getY() + (int) my);
                                                    p.addPoint(getX() + getWidth() - barWidth + (int) mx - spaceAlt,
                                                            (int) (getY() + zy));
                                                    p.addPoint(getX() + getWidth() - barWidth + (int) lastX - spaceAlt,
                                                            (int) (getY() + zy));

                                                    int[] colors = colorRanges.getColors(sid);
                                                    float[] spacings = colorRanges.getSpacings(sid, min, max);
                                                    if (colors.length == spacings.length)
                                                        g.setGradient(0, graphHeight, 0, 0, colors, spacings);
                                                    g.fillPolygon(p);
                                                    g.clearGradient();

                                                    //else MainActivity.debug("size not equal: "+colors.length+"=="+spacings.length);
                                                }
                                            //}
                                        } else {
                                            if(lastX!=Double.NaN && lastY!=Double.NaN && (lastY != 0.0) && (lastX != 0.0))
                                            {
                                                g.drawLine(getX() + getWidth() - barWidth + (int) lastX - spaceAlt,
                                                        getY() + (int) lastY,
                                                        getX() + getWidth() - barWidth + (int) mx - spaceAlt,
                                                        getY() + (int) my);
                                            }
                                        }
                                    }
                                    lastX = mx;
                                    lastY = my;
                                }
                            }
                    }
                }
            }
        }

        // clean bottom
        g.setColor(getBackground());
        g.fillRect(width - barWidth - 2, graphHeight + 1, barWidth + 1, height - graphHeight - 2);

        // draw bottom axis
        int c = 0;
        int ts = (int) timeSale;

        //MainActivity.debug("Start : "+sdf.format(start));
        if(backward)
        {
            for(long x=width-(start%interval)-spaceAlt; x>=width-barWidth-spaceAlt; x-=interval)
            {
                if(c%(5*ts)==0) {
                    g.setColor(getForeground());
                    g.drawLine(x, graphHeight, x, graphHeight + 10);
                    String date = sdf.format((start - ((start % interval))*timeSale - interval * c*timeSale*1000));
                    g.drawString(date, x - g.stringWidth(date) - 4, height - 2);
                }
                else
                {
                    g.setColor(getForeground());
                    g.drawLine(x, graphHeight, x, graphHeight + 3);
                }
                c++;
            }
        }
        else
        {
            //MainActivity.debug("START: "+sdf.format(start));
            for(long x=width-barWidth-spaceAlt; x<width-spaceAlt; x+=interval)
            {
                if(c%(5*ts)==0) {
                    g.setColor(getForeground());
                    g.drawLine(x, graphHeight, x, graphHeight + 10);
                    String date = sdf.format( start + (interval * c*timeSale*1000));
                    g.drawString(date, x - g.stringWidth(date) - 4, height - 2);
                }
                else
                {
                    g.setColor(getForeground());
                    g.drawLine(x, graphHeight, x, graphHeight + 3);
                }
                c++;
            }
        }




        // draw the title
        if(title!=null && !title.equals(""))
        {
            g.setColor(getTitleColor());
            g.setTextSize(20);
            int th = g.stringHeight(title);
            int tx = getX()+width-barWidth+8-spaceAlt;
            int ty = getY()+th+4;
            g.drawString(title,tx,ty);
        }

        // draw the value
        if(showValue)
        {
            for(int s=0; s<sids.size(); s++) {
                String sid = sids.get(s);
                ArrayList<TimePoint> values = this.values.get(sid);
                Field field = Fields.getInstance().getBySID(sid);

                String text;

                if(field !=null) {
                    text = String.format("%." + String.valueOf(field.getDecimals()) + "f", field.getValue());
                }
                else {
                    if(values.size()==0) text="N/A";
                    else text = String.valueOf(values.get(values.size()-1).value);
                }

                g.setTextSize(40);

                if(values.isEmpty())
                    g.setColor(getColor(s));
                else
                    g.setColor(colorRanges.getColor(sid, values.get(values.size()-1).value, getColor(s)));

                int tw = g.stringWidth(text);
                int th = g.stringHeight(text);
                int tx = getX()+width-tw-8-spaceAlt;
                int ty = getY()+(s+1)*(th+4);
                g.drawString(text, tx, ty);
            }
        }

        // black border
        g.setColor(getForeground());
        g.drawRect(x, y, width, height);
    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        addValue(field.getSID(),field.getValue());

        super.onFieldUpdateEvent(field);
    }

    @Override
    public String dataToJson() {
        Gson gson = new Gson();
        return gson.toJson(values.clone());
    }

    @Override
    public void dataFromJson(String json) {
        Gson gson = new Gson();
        Type fooType = new TypeToken<HashMap<String,ArrayList<TimePoint>>>() {}.getType();

        values = gson.fromJson(json,fooType);
    }

    @Override
    public void loadValuesFromDatabase() {
        super.loadValuesFromDatabase();

        //values.clear(); // not needed as items will be replaced anyway!
        for(int s=0; s<sids.size(); s++) {
            String sid = sids.get(s);
            values.put(sid, CanzeDataSource.getInstance().getData(sid));
        }
    }

    public void addField(String sid)
    {
        super.addField(sid);
        if(!values.containsKey(sid)) {
            values.put(sid, new ArrayList<TimePoint>());
        }
    }

    public void setValues(HashMap<String, ArrayList<TimePoint>> values) {
        sids.clear();

        for (String key : values.keySet())
        {
            sids.add(key);
        }

        this.values = values;
    }


    public boolean isBackward() {
        return backward;
    }

    public void setBackward(boolean backward) {
        this.backward = backward;
    }
}


