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


/*
 * This class represents a CAN frame
 */
package lu.fisch.canze.actors;



/**
 *
 * @author robertfisch
 */
public class Message {
    
    protected int id;
    protected long timestamp;
    protected int[] data;
    private double rate;
    private String responseId = null;
    
    private static final int[] EMPTY = {};
    
    /**
     * Full constructor for a new frame
     * @param id            the ID
     * @param timestamp     the timestamp
     * @param data          the data
     */
    public Message(int id, long timestamp, int[] data) {
        this(id,timestamp,data,null);
    }

    public Message(int id, long timestamp, int[] data, String responseId) {
        this.id=id;
        this.timestamp=timestamp;
        this.data=data;
        this.responseId=responseId;
    }

    public Message(int id) {
        this(id, -1, EMPTY);
    }
    
    public Message(int id, long timestamp) {
        this(id, timestamp, EMPTY);
    }
    
    public Message(int id, int[] data) {
        this(id, -1, data);
    }
    
    /**
     * Clones this frame
     * @return      the cloned frame
     */
    @Override
    public Message clone()
    {
        return new Message(id,timestamp,data.clone(),responseId);
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

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
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
        String res = "ID: "+Integer.toHexString(id)+"\nData: "+hexData;
        if(responseId !=null) res+="\nReply: "+ responseId;
        return res;
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
    }


}
