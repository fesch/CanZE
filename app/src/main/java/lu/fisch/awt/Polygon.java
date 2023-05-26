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
