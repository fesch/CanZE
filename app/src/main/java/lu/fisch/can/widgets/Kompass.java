package lu.fisch.can.widgets;

import android.graphics.Point;

import lu.fisch.awt.Color;
import lu.fisch.awt.Graphics;
import lu.fisch.awt.Polygon;
import lu.fisch.can.interfaces.DrawSurfaceInterface;
import lu.fisch.canze.DrawSurface;

/**
 *
 * @author robertfisch
 */
public class Kompass extends Tacho {

    protected boolean inverted = false;

    public Kompass() {
        super();
    }

    public Kompass(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public Kompass(DrawSurfaceInterface drawSurface, int x, int y, int width, int height) {
        super(drawSurface, x, y, width, height);
    }

    @Override
    public void setMax(int max) {
        super.setMax(max);
        super.setMin(-max);
    }

    @Override
    public void setMin(int min) {
        super.setMin(min);
        super.setMax(-min);
    }

    @Override
    public void draw(Graphics g)
    {
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

        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
        // draw the frame
        g.setColor(Color.GRAY);
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
        g.setColor(Color.BLACK);
        double dist = 360-2*alpha;
        int actual = 0;
        // draw the minor ticks
        if(minorTicks >0 || majorTicks>0)
        {
            g.setColor(Color.GRAY);
            int toTicks = minorTicks;
            if(toTicks==0) toTicks=majorTicks;
            double accel = dist/((max-min)/toTicks);
            int sum = 0;
            // min --> 0
            for(double i=-90+alpha+dist/2; i<=360-90-alpha; i+=accel)
            {
                if(minorTicks >0)
                {
                    ax = center.x+(rayon)*Math.cos(mkRad(i));
                    ay = center.y-(rayon)*Math.sin(mkRad(i));
                    bx = center.x+(rayon-6)*Math.cos(mkRad(i));
                    by = center.y-(rayon-6)*Math.sin(mkRad(i));
                    g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                }
                // draw majorTicks
                if(majorTicks!=0 && sum % majorTicks == 0) {
                    if(majorTicks>0)
                    {
                        ax = center.x+(rayon)*Math.cos(mkRad(i));
                        ay = center.y-(rayon)*Math.sin(mkRad(i));
                        bx = center.x+(rayon-12)*Math.cos(mkRad(i));
                        by = center.y-(rayon-12)*Math.sin(mkRad(i));
                        g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                    }

                    // draw String
                    if(showLabels)
                    {
                        bx = center.x+(rayon-24)*Math.cos(mkRad(i));
                        by = center.y-(rayon-24)*Math.sin(mkRad(i));
                        String text = (actual)+"";
                        double sw = g.stringWidth(text);
                        g.drawString(text, (int) (bx - (sw/2. + sw/2.*Math.cos(mkRad(i))) ), (int) (by + g.stringHeight(text) / 2));
                    }

                    actual-=majorTicks;
                }
                sum+= minorTicks;
            }
            // 0 --> max
            actual = 0;
            sum=0;
            //for(double i=-90+alpha+dist/2; i<=360-90-alpha; i+=accel)
            for(double i=-90+alpha+dist/2; i>=-90+alpha; i-=accel)
            {
                if(minorTicks >0)
                {
                    ax = center.x+(rayon)*Math.cos(mkRad(i));
                    ay = center.y-(rayon)*Math.sin(mkRad(i));
                    bx = center.x+(rayon-6)*Math.cos(mkRad(i));
                    by = center.y-(rayon-6)*Math.sin(mkRad(i));
                    g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                }
                // draw majorTicks
                if(majorTicks!=0 && sum % majorTicks == 0) {
                    if(majorTicks>0)
                    {
                        ax = center.x+(rayon)*Math.cos(mkRad(i));
                        ay = center.y-(rayon)*Math.sin(mkRad(i));
                        bx = center.x+(rayon-12)*Math.cos(mkRad(i));
                        by = center.y-(rayon-12)*Math.sin(mkRad(i));
                        g.drawLine((int)ax, (int)ay, (int)bx, (int)by);
                    }

                    // draw String
                    if(showLabels)
                    {
                        bx = center.x+(rayon-24)*Math.cos(mkRad(i));
                        by = center.y-(rayon-24)*Math.sin(mkRad(i));
                        String text = (actual)+"";
                        double sw = g.stringWidth(text);
                        g.drawString(text, (int) (bx - (sw/2. + sw/2.*Math.cos(mkRad(i))) ), (int) (by + g.stringHeight(text) / 2));
                    }

                    actual+=majorTicks;
                }
                sum+= minorTicks;
            }
        }
        // draw the needle
        g.setColor(Color.RED);
        double rota = 90+alpha+dist*(value-min)/(max-min);
        if (inverted) rota = 90+alpha+dist*(-value-min)/(max-min);
        Polygon p = new Polygon();
        double needleLength = rayon-35;
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
            g.setTextSize(50);
            int tw = g.stringWidth(value+"");
            int th = g.stringHeight(value + "");
            int tx = center.x-tw/2-3;
            int ty = center.y-32;
            g.setColor(Color.WHITE);
            g.fillRect(tx - 1, ty - th, tw + 7, th + 5);
            //g.setColor(Color.BLACK);
            //g.drawRect(tx - 1, ty - th, tw + 7, th +5);
            g.setColor(Color.GREEN_DARK);
            g.drawString(value+"",tx,ty);
        }
        // draw the title
        if(title!=null && !title.equals(""))
        {
            g.setColor(Color.BLUE);
            g.setTextSize(20);
            int tw = g.stringWidth(title);
            //int th = g.stringHeight(title);
            int tx = getX()+getWidth()/2-tw/2;
            int ty = getY()+getHeight()-8;
            g.drawString(title,tx,ty);
        }
    }


    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
}
