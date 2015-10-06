package lu.fisch.canze.widgets;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Calendar;

import lu.fisch.awt.Color;
import lu.fisch.awt.Graphics;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DrawSurfaceInterface;

/**
 *
 * @author robertfisch
 */
public class Timeplot extends Drawable {

    class TimePoint {
        public long date;
        public double value;

        public TimePoint(long date, double value) {
            this.date = date;
            this.value = value;
        }
    }

    protected ArrayList<TimePoint> values = new ArrayList<>();

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

    public void addValue(double value)
    {
        values.add(new TimePoint(Calendar.getInstance().getTimeInMillis(), value));
        if(value<min) setMin((int) value - 1);
        else if(value>max) setMax((int) value + 1);

        /*setMinorTicks(0);
        setMajorTicks(1);
        if(getMax()-getMin()>100) setMajorTicks(10);
        else if(getMax()-getMin()>1000) setMajorTicks(100);
        else if(getMax()-getMin()>10000) setMajorTicks(1000);
        /**/
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
            double ax,ay,bx=0,by=0;
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
            double h = (double) getHeight()/(getMax()-getMin()+1);

            double lastX = Double.NaN;
            double lastY = Double.NaN;
            g.setColor(Color.RED);

            long maxTime = values.get(values.size()-1).date;

            for(int i=values.size()-1; i>=0; i--)
            {
                TimePoint tp = values.get(i);

                double mx = barWidth-(maxTime-tp.date)/1000;

                if(mx<0)
                {
                    values.remove(i);
                }
                else {
                    double my = getHeight() - (tp.value - getMin()) * h;
                    int rayon = 2;
                    g.fillOval(getX() + getWidth() - barWidth + (int) mx - rayon,
                            getY() + (int) my - rayon,
                            2 * rayon + 1,
                            2 * rayon + 1);
                    if (i < values.size() - 1) {
                        g.drawLine(getX() + getWidth() - barWidth + (int) lastX,
                                getY() + (int) lastY,
                                getX() + getWidth() - barWidth + (int) mx,
                                getY() + (int) my);
                    }
                    lastX = mx;
                    lastY = my;
                }
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

        // draw the value
        if(showValue)
        {
            if(field !=null) {
                String text = String.format("%." + (String.valueOf(field.getDecimals()).length() - 1) + "f", field.getValue());

                g.setTextSize(40);
                g.setColor(Color.GREEN_DARK);
                int tw = g.stringWidth(text);
                int th = g.stringHeight(text);
                int tx = getX()+width-tw-8;
                int ty = getY()+th+4;
                g.drawString(text, tx, ty);
            }
        }

    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        addValue(field.getValue());

        super.onFieldUpdateEvent(field);
    }
}
