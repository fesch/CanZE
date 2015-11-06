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
    protected String data;
    private String responseId = null;
    
    private static final String EMPTY = "";
    
    /**
     * Full constructor for a new frame
     * @param id            the ID
     * @param timestamp     the timestamp
     * @param data          the data
     */
    public Message(int id, long timestamp, String data) {
        this(id,timestamp,data,null);
    }

    public Message(int id, long timestamp, String data, String responseId) {
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
    
    public Message(int id, String data) {
        this(id, -1, data);
    }
    
    /**
     * Clones this message
     * @return      the cloned message
     */
    @Override
    public Message clone()
    {
        return new Message(id,timestamp,data+"",responseId);
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

    public String getData() {
        return data;
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
        String res = "ID: "+Integer.toHexString(id)+"\nData: "+data;
        if(responseId !=null) res+="\nReply: "+ responseId;
        return res;
    }
    
    public String getAsBinaryString()
    {
        String result = "";
        for(int i=0; i<data.length(); i+=2)
        {
            /*String hexS = getByte(data[i]);
            result+=hexS;
            */
            result += String.format("%8s", Integer.toBinaryString(Integer.parseInt(data.substring(i,i+2),16) & 0xFF)).replace(' ', '0');
        }
        return result;
    }

}
