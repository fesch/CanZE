/*
 * This class represents a field.
 * Other objects can register to be notified.
 */
package lu.fisch.can;

import lu.fisch.can.interfaces.FieldListener;
import lu.fisch.canze.MainActivity;

import java.util.ArrayList;

/**
 *
 * @author robertfisch
 */
public class Field {

    private final ArrayList<FieldListener> fieldListeners = new ArrayList<>();

    private int from;
    private int to;
    private int offset;
    private int id;
    private int devider;
    private int multiplier;
    private int decimals;
    private String format;
    private String unit;
    
    private double value = 0;
    
    public Field(int id, int from, int to, int devider, int multiplier, int offset, int decimals, String format, String unit) {
        this.from=from;
        this.to=to;
        this.offset=offset;
        this.id=id;
        this.devider = devider;
        this.multiplier = multiplier;
        this.decimals = decimals;
        this.format = format.trim();
        this.unit = unit;
    }
    
    @Override
    public Field clone()
    {
        Field field = new Field(id, from, to, devider, multiplier, offset, decimals, format, unit);
        field.value = value;
        return field;
    }
    
    @Override
    public String toString()
    {
        /*return Integer.toHexString(id)+" "
                +from+" "
                +to+" "
                +devider+" "
                +multiplier+" "
                +offset+" "
                +decimals+" "
                +format+" "
                +unit;*/
        return format.substring(0, format.indexOf("%")-1).trim();
    }

    public String getSID()
    {
        return Integer.toHexString(id)+"."+from;
    }
    
    public String getPrintValue()
    {
        double temp = ((value-offset)/(double)devider*multiplier)/(decimals==0?1:decimals);
        return format.substring(0, format.indexOf("%")-1).trim()+" "+temp+" "+unit;
    }

    public double getValue()
    {
        /*MainActivity.debug("Field: "+getFormat());
        MainActivity.debug("Value: "+value);
        MainActivity.debug("Offset: "+offset);
        MainActivity.debug("devider: "+devider);
        MainActivity.debug("multiplier: "+multiplier);*/
        return ((value-offset)/(double)devider*multiplier)/(decimals==0?1:decimals);
    }
    
    public double getMax()
    {
        double val = (int) Math.pow(2, to-from+1);
        return ((val-offset)/(double)devider*multiplier)/(decimals==0?1:decimals);
        
    }

    public double getMin()
    {
        double val = 0;
        return ((val-offset)/(double)devider*multiplier)/(decimals==0?1:decimals);
    }

    /* --------------------------------
     * Listeners management
     \ ------------------------------ */
    
    public void addListener(FieldListener fieldListener)
    {
        if(!fieldListeners.contains(fieldListener))
            fieldListeners.add(fieldListener);
    }
    
    public void removeSackListener(FieldListener fieldListener)
    {
        fieldListeners.remove(fieldListener);
    }
    
    /**
     * Notify all listeners synchronously
     */
    private void notifyFieldListeners()
    {
        notifyFieldListeners(false);
    }

    /**
     * Notify all listeners
     * @param async     true for asynchronous notifications (one thread per listener)
     */
    private void notifyFieldListeners(boolean async)
    {
        if(async == false) {
            for(int i=0; i<fieldListeners.size(); i++) {
                fieldListeners.get(i).onFieldUpdateEvent(this.clone());
            }
        } else {
            // clone the frame to make sure modifications will 
            final Field clone = this.clone();
            for(int i=0; i<fieldListeners.size(); i++) {
                final int index = i;
                (new Thread(new Runnable() {

                    @Override
                    public void run() {
                        fieldListeners.get(index).onFieldUpdateEvent(clone.clone());                   
                    }
                })).start();
            }
        }
    }
    
    /* --------------------------------
     * Getters & setters
    \ ------------------------------ */

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public double getRawValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
        notifyFieldListeners();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    public int getDevider() {
        return devider;
    }

    public void setDevider(int devider) {
        this.devider = devider;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
