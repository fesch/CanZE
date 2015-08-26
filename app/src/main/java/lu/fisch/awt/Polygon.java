package lu.fisch.awt;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by robertfisch on 15.08.2015.
 */
public class Polygon {
    private ArrayList<Point> points = new ArrayList<>();

    public void addPoint(Point p)
    {
        points.add(p);
    }

    public void addPoint(int x, int y)
    {
        points.add(new Point(x,y));
    }

    public int size()
    {
        return points.size();
    }

    public Point get(int index)
    {
        return points.get(index);
    }
}
