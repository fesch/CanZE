/*
 * Represents an elemtn which can be draw onto a canvas
 */
package lu.fisch.canze.widgets;

import android.graphics.Point;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import lu.fisch.awt.Graphics;
import lu.fisch.awt.Rectangle;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.aligner.Space;
import lu.fisch.canze.interfaces.DrawSurfaceInterface;
import lu.fisch.canze.interfaces.FieldListener;

/**
 *
 * @author robertfisch
 */
public abstract class Drawable implements Space, FieldListener {
    protected int x, y, width, height;
    protected int min = 0;
    protected int max = 0;
    protected int majorTicks = 10;
    protected int minorTicks = 2;
    protected boolean showLabels = true;
    protected boolean showValue = true;
    protected boolean inverted = false;
    protected int value = 0;
    protected Field field = null;
    protected String title = "";

    protected DrawSurfaceInterface drawSurface = null;

    protected ArrayList<String> sids = new ArrayList<>();


    public Drawable()
    {

    }

    public abstract void draw(Graphics g);
    
    public boolean isInside(Point p)
    {
        Rectangle rect = new Rectangle(x, y, width, height);
        return rect.contains(p);
    }

    protected double mkRad(double degree)
    {
        return degree/180.*Math.PI;
    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        this.field = field;
        setValue((int) field.getValue());
        if(drawSurface!=null)
            drawSurface.repaint();
    }

    public void onLayout(boolean landscape)
    {
        // empty
    }

    public void reset()
    {
        // empty
    }

    /* --------------------------------
     * Serialization
     \ ------------------------------ */

    public abstract String dataToJson();
    public abstract void dataFromJson(String json);

    /* --------------------------------
     * Getters & setters
     \ ------------------------------ */

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMajorTicks() {
        return majorTicks;
    }

    public void setMajorTicks(int majorTicks) {
        this.majorTicks = majorTicks;
    }

    public int getMinorTicks() {
        return minorTicks;
    }

    public void setMinorTicks(int minorticks) {
        this.minorTicks = minorticks;
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    public DrawSurfaceInterface getDrawSurface() {
        return drawSurface;
    }

    public void setDrawSurface(DrawSurfaceInterface drawSurface) {
        this.drawSurface = drawSurface;
    }

    public boolean isShowValue() {
        return showValue;
    }

    public void setShowValue(boolean showValue) {
        this.showValue = showValue;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public Field getField() {
        return field;
    }

    public void addField(String sid)
    {
        if(!sids.contains(sid))
            sids.add(sid);
    }

    public ArrayList<String> getSids() {
        return sids;
    }
}
