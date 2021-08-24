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

package lu.fisch.canze.classes;

/**
 * Created by robertfisch on 26.10.2015.
 */
public class ColorRange {
    public int from;
    public int to;
    public String color;
    public String sid;

    public ColorRange() {}

    public ColorRange(int from, int to, String color) {
        this.color = color;
        this.from = from;
        this.to = to;
    }

    public boolean isInside(double value)
    {
        return (from<=value && value<=to);
    }
}
