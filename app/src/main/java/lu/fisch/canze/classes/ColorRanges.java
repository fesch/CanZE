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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import lu.fisch.awt.Color;
import lu.fisch.canze.activities.MainActivity;

/**
 * Created by robertfisch on 26.10.2015.
 */
public class ColorRanges {
    private ArrayList<ColorRange> colorRanges = new ArrayList<>();

    public ColorRanges() {}

    public ColorRanges(String json) {
        Gson gson = new Gson();
        Type tt = new TypeToken<ArrayList<ColorRange>>() {}.getType();
        colorRanges = gson.fromJson(json,tt);
    }

    public Color getColor(String sid, double value, Color defaultColor) {
        for(int i=0; i<colorRanges.size(); i++)
        {
            ColorRange cr = colorRanges.get(i);
            if(cr.sid.equals(sid) && cr.isInside(value)) {
                return Color.decode(cr.color);
            }
        }
        return defaultColor;
    }

    public String getJson() {

        return (new Gson()).toJson(colorRanges.clone());
    }

    public void add(ColorRange colorRange) {

        colorRanges.add(colorRange);
    }
}
