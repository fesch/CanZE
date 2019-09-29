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

    /*
    public int[] getColors(String sid, boolean positive)
    {
        ArrayList<Integer> colors = new ArrayList<>();
        for(int i=0; i<colorRanges.size(); i++)
        {
            ColorRange colorRange = colorRanges.get(i);
            if (colorRange.sid.equals(sid) &&
                    (
                            (positive && (colorRange.to>0 || colorRange.from>0))
                                    ||
                                    (!positive && (colorRange.to<0 || colorRange.from<0))
                    ))
                colors.add(Color.decode(colorRange.color).getAndroidColor());
        }
        final int[] result = new int[colors.size()];
        for (int i = 0; i < colors.size(); i++) {
            result[i] = colors.get(i);
        }
        return result;
    }
    */

    public int[] getColors(String sid)
    {
        ArrayList<Integer> colors = new ArrayList<>();
        for(int i=0; i<colorRanges.size(); i++)
        {
            ColorRange colorRange = colorRanges.get(i);
            if (colorRange.sid.equals(sid) )
                colors.add(Color.decode(colorRange.color).getAndroidColor());
        }
        final int[] result = new int[colors.size()];
        //String debug = "Colors: ";
        for (int i = 0; i < colors.size(); i++) {
            //debug+=", "+colors.get(i).toString();
            result[i] = colors.get(i);
        }
        //MainActivity.debug(debug);
        //MainActivity.debug("Colors "+result.length);
        return result;
    }

    /*
    public float[] getSpacings(String sid, int min, int max, boolean positive)
    {
        ArrayList<Integer> values = new ArrayList<>();
        if((positive && min>0) || (!positive && min<0))
            values.add(min);
        for(int i=1; i<colorRanges.size()-1; i++)
        {
            ColorRange colorRange = colorRanges.get(i);
            if (colorRange.sid.equals(sid) &&
                    (
                            (positive && (colorRange.to>0 || colorRange.from>0))
                                    ||
                                    (!positive && (colorRange.to<0 || colorRange.from<0))
                    )) {
                if(colorRange.from>0 && colorRange.to>0)
                    values.add(colorRange.from);
                else
                    values.add(colorRange.to);
            }
        }
        if((positive && max>0) || (!positive && max<0))
            values.add(max);

        final float[] result = new float[values.size()];
        //String spacings = "Spacings: ";
        for (int i = 0; i < values.size(); i++) {
            result[i] = (float) (values.get(i)-min)/(max-min);
            //spacings+=", "+result[i];
        }
        //MainActivity.debug(spacings);
        return result;
    }
    */

    public float[] getSpacings(String sid, int min, int max)
    {
        ArrayList<Float> values = new ArrayList<>();
        //values.add(min+0f);
        for(int i=0; i<colorRanges.size(); i++)
        {
            ColorRange colorRange = colorRanges.get(i);
            if (colorRange.sid.equals(sid))
            {
                /*if(colorRange.from>=0 && colorRange.to>=0) {
                    values.add(colorRange.from);
                }
                else
                {
                    values.add(colorRange.to);
                }*/
                values.add((colorRange.to+colorRange.from)/2.f);
            }
        }
        // replace the first one with min
        if(values.size()>0) values.set(0,min+0f);
        // replace the last one with max
        if(values.size()>0) values.set(values.size()-1,max+0f);
        //values.add(max+0f);

        final float[] result = new float[values.size()];
        //String debug = "Spacings: ";
        for (int i = 0; i < values.size(); i++) {
            result[i] = (values.get(i)-min)/(max-min);
            //debug+=", "+values.get(i);
        }
        //MainActivity.debug(debug);
        //MainActivity.debug("Spacings "+result.length);
        return result;
    }


    public String getJson() {

        return (new Gson()).toJson(colorRanges.clone());
    }

    public void add(ColorRange colorRange) {

        colorRanges.add(colorRange);
    }
}
