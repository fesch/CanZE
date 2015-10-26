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
