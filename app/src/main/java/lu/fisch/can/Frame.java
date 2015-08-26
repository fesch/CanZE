/*
 * This class represents a CAN frame
 */
package lu.fisch.can;

import lu.fisch.can.decoders.CRDT;

/**
 *
 * @author robertfisch
 */
public class Frame {
    
    protected int id;
    protected long timestamp;
    protected int[] data;
    private double rate;
    
    private static final int[] EMPTY = {};
    
    /**
     * Full constructor for a new frame
     * @param id            the ID
     * @param timestamp     the timestamp
     * @param data          the data
     */
    public Frame(int id, long timestamp, int[] data) {
        this.id=id;
        this.timestamp=timestamp;
        this.data=data;
    }
    
    public Frame(int id) {
        this(id, -1, EMPTY);
    }
    
    public Frame(int id, long timestamp) {
        this(id, timestamp, EMPTY);
    }
    
    public Frame(int id, int[] data) {
        this(id, -1, data);
    }
    
    /**
     * Clones this frame
     * @return      the cloned frame
     */
    @Override
    public Frame clone()
    {
        return new Frame(id,timestamp,data.clone());
    }
    
    public boolean isMultiFrame()
    {
        return false;
    }
    
    /* --------------------------------
     * Getters & setters
     \ ------------------------------ */

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int[] getData() {
        return data;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    /* --------------------------------
     * Some utilities
     \ ------------------------------ */
    
    
    @Override
    public String toString()
    {
        String hexData = "";
        for(int i=0; i<data.length; i++)
            hexData+= (Integer.toHexString(data[i]).length()==1?"0":"")+Integer.toHexString(data[i])+" ";
        return "ID: "+Integer.toHexString(id)+"\nData: "+hexData;
    }
    
    public String getAsBinaryString()
    {
        String result = "";
        for(int i=0; i<data.length; i++)
        {
            /*String hexS = getByte(data[i]);
            result+=hexS;
            */
            result += String.format("%8s", Integer.toBinaryString(data[i] & 0xFF)).replace(' ', '0');
        }
        return result;
    }

    /*
    private String getByte(int i)
    {
        String r = "";
        if(i / 128 == 1) {r+="1"; i-=128; }else r+="0";
        if(i / 64 == 1)  {r+="1"; i-=64; }else r+="0";
        if(i / 32 == 1)  {r+="1"; i-=32; }else r+="0";
        if(i / 16 == 1)  {r+="1"; i-=16; }else r+="0";
        if(i / 8 == 1)   {r+="1"; i-=8; }else r+="0";
        if(i / 4 == 1)   {r+="1"; i-=4; }else r+="0";
        if(i / 2 == 1)   {r+="1"; i-=2; }else r+="0";
        if(i / 1 == 1)   {r+="1"; i-=1; }else r+="0";
        return r;
    }*/
    
    
    /* --------------------------------
     * Tests ...
     \ ------------------------------ */
    
    public static void main(String[] args)
    {
        Frame f = (new CRDT()).decode("38265528 R11 657 c4 88");
        System.out.println(f);
    }


}
