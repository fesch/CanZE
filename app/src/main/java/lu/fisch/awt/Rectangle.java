package lu.fisch.awt;

import android.graphics.Point;

/**
 * Created by robertfisch on 14.08.2015.
 */
public class Rectangle {
    private int x, y, width, height;

    public Rectangle(int x, int y, int width, int height)
    {
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

    public boolean contains(Point p)
    {
        return x<=p.x &&
               p.x<=x+width &&
               y<=p.y &&
               p.y<=y+height;
    }
}
