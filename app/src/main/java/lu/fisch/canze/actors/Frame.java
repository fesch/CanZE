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

package lu.fisch.canze.actors;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Frame
 */
public class Frame {

    private final int id;
    private final String responseId;
    private final Ecu sendingEcu;
    private final ArrayList<Field> fields = new ArrayList<>();
    private final ArrayList<Field> queriedFields = new ArrayList<>();
    private final Frame containingFrame;

    private int interval; // in ms
    private long lastRequest = 0;


    public Frame (int id, int interval, Ecu sendingEcu, String responseId, Frame containingFrame) {
        this.id = id;
        this.interval = interval;
        this.sendingEcu = sendingEcu;
        this.responseId = responseId == null ? null : responseId.toLowerCase();
        this.containingFrame = containingFrame;
    }

    /* --------------------------------
     * Scheduling
     * ------------------------------ */

    public void updateLastRequest()
    {
        lastRequest = Calendar.getInstance().getTimeInMillis();
    }

    public long getLastRequest()
    {
        return lastRequest;
    }

    public boolean isDue(long referenceTime)
    {
        return lastRequest+interval<referenceTime;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval (int interval) { this.interval = interval; }

    public boolean isIsoTp()
    {
        //if (this.responseId == null) return false;
        //return !responseId.trim().isEmpty();
        return (id >= 0x700 && id <= 0x800); // the VFC is considered ISOTP too
    }

    public int getId() {
        return id;
    }

    public String getRID()
    {
        if(responseId!=null && !responseId.trim().isEmpty())
            return (getHexId()+"."+responseId.trim()).toLowerCase();
        else
            return (getHexId()).toLowerCase();
    }


    public String getHexId() {
        return String.format("%03x", id);
    }

    public Ecu getSendingEcu() {
        return sendingEcu;
    }

    public String getResponseId() {
        return responseId;
    }

    public ArrayList<Field> getAllFields() {
        return fields;
    }

    public void addField(Field field) {
        this.fields.add(field);
    }

    public ArrayList<Field> getQueriedFields() {
        return queriedFields;
    }

    public void addQueriedField(Field field) {
        this.queriedFields.add(field);
    }

    public void removeQueriedField(Field field) {
        this.queriedFields.remove(field);
    }

    public String getRequestId () {
        if (responseId.compareTo("") == 0) return ("");
        char[] tmpChars = responseId.toCharArray();
        tmpChars[0] -= 0x04;
        return String.valueOf(tmpChars);
    }

    public Frame getContainingFrame() {
        return containingFrame;
    }

}
