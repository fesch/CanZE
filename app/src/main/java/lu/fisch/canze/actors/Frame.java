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

/**
 * Frame
 */
public class Frame {

    private int id;
    private String responseId = null;
    private int interval; // in ms
    private Ecu sendingEcu;
    private final ArrayList<Field> fields = new ArrayList<>();
    private Frame containingFrame = null;

    public Frame (int id, int interval, Ecu sendingEcu, String responseId, Frame containingFrame) {
        this.id = id;
        this.interval = interval;
        this.sendingEcu = sendingEcu;
        this.responseId = responseId;
        this.containingFrame = containingFrame;
    }

    public boolean isIsoTp()
    {
        if (this.responseId == null) return false;
        return !responseId.trim().isEmpty();
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
        return Integer.toHexString(id);
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval (int interval) { this.interval = interval; }

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
