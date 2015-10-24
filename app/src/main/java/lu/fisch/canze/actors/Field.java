/*
 * This class represents a field.
 * Other objects can register to be notified.
 */
package lu.fisch.canze.actors;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.interfaces.FieldListener;

import java.util.ArrayList;

/**
 *
 * @author robertfisch
 */
public class Field {

    private final ArrayList<FieldListener> fieldListeners = new ArrayList<>();

    private int from;
    private int to;
    private double offset;
    private int id;
    private int divider;
    private int multiplier;
    private int decimals;
    private String format;
    private String unit;
    private String requestId;
    private String responseId;
    private String description;
    private int car;
    private int skips;
    private int frequency;

    private double value = 0;
    private int skipsCount = 0;
    
    public Field(int id, int from, int to, int divider, int multiplier, double offset, int decimals, String format, String unit, String requestId, String responseId, String description, int car, int skips, int frequency) {
        this.from=from;
        this.to=to;
        this.offset=offset;
        this.id=id;
        this.divider = divider;
        this.multiplier = multiplier;
        this.decimals = decimals;
        this.format = format.trim();
        this.unit = unit;
        this.requestId=requestId;
        this.responseId=responseId;
        this.description=description;
        this.car=car;
        this.skips=skips;
        this.frequency=frequency;
    }
    
    @Override
    public Field clone()
    {
        Field field = new Field(id, from, to, divider, multiplier, offset, decimals, format, unit, requestId, responseId, description, car, skips,frequency);
        field.value = value;
        return field;
    }
    
    @Override
    public String toString()
    {
        /*return Integer.toHexString(id)+" "
                +from+" "
                +to+" "
                +divider+" "
                +multiplier+" "
                +offset+" "
                +decimals+" "
                +format+" "
                +unit;*/
        return format.substring(0, format.indexOf("%")-1).trim();
    }

    public boolean isIsoTp()
    {
        return !responseId.trim().isEmpty();
    }

    public String getSID()
    {
        if(!responseId.trim().isEmpty())
            return (Integer.toHexString(id)+"."+responseId.trim()+"."+from).toLowerCase();
        else
            return (Integer.toHexString(id)+"."+from).toLowerCase();
    }

    public String getUniqueID()
    {
        return getCar()+"."+getSID();
    }
    
    public String getPrintValue()
    {
        return format.substring(0, format.indexOf("%")-1).trim()+" "+getValue()+" "+unit;
    }

    public double getValue()
    {
        double val =  ((value-offset)/(double) divider *multiplier)/(decimals==0?1:decimals);
        if (MainActivity.milesMode) {
            if (getUnit().toLowerCase().startsWith("km"))
                val = val / 1.609344;
            else if (getUnit().toLowerCase().endsWith("km"))
                val = val*1.609344;
            setUnit(getUnit().replace("km","mi"));
            return val;
        }
        return val;
    }
    
    public double getMax()
    {
        double val = (int) Math.pow(2, to-from+1);
        return ((val-offset)/(double) divider *multiplier)/(decimals==0?1:decimals);
        
    }

    public double getMin()
    {
        double val = 0;
        return ((val-offset)/(double) divider *multiplier)/(decimals==0?1:decimals);
    }

    /* --------------------------------
     * Listeners management
     \ ------------------------------ */
    
    public void addListener(FieldListener fieldListener)
    {
        if(!fieldListeners.contains(fieldListener)) {
            fieldListeners.add(fieldListener);
            // trigger immediate update to pass the reference to this field
            fieldListener.onFieldUpdateEvent(this);
        }
    }
    
    public void removeListener(FieldListener fieldListener)
    {
        fieldListeners.remove(fieldListener);
    }
    
    /**
     * Notify all listeners synchronously
     */
    public void notifyFieldListeners()
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

    public double getOffset() {
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
    public String getHexId() {
        return Integer.toHexString(id);
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    public int getDivider() {
        return divider;
    }

    public void setDivider(int divider) {
        this.divider = divider;
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public int getSkips() {
        return skips;
    }

    public void setSkips(int skips) {
        this.skips = skips;
    }

    public int getSkipsCount() {
        return skipsCount;
    }

    public void decSkipCount() {
        skipsCount--;
    }

    public void setSkipsCount(int skipCount) {
        this.skipsCount = skipCount;
    }

    public void resetSkipsCount() {
        this.skipsCount = skips;
    }

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
