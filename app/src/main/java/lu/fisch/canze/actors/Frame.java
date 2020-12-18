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

    // A frame is one of
    // - a representation of a free frame floating on the bus (Ph1 only). Though we do not use the
    //   term "packet" like in ethernet, it's totally equivalent. Size is max 8 bytes
    //   A free frame is identified by an ID below 0x700, at least on Ph1. We have not detected yet
    //   accessible free frames on Ph2.
    // - an ISOTP formatted frame, which can (not need) consist of more than one "packet". ISOTP
    //   carries out sequencing, (dis)assembly and flow control
    //   ISOTP frames are almost exclusively use for "request-response" traffic, meant for
    //   diagnstic tools. Since the ID of many ISOTP frames are the same (in principle, all ECUs
    //   have one rc and one tx ID only), sub-frames are defined for each single ISOTP frames.
    //   An ISOTP sub-frame is identified by an ID and a response string.

    // A frames is the basic unit of communication. A frame is received from the car or potentially
    // send to the car. Once a frame is received, it's content is stored in a Message, which is
    // processed further down the line.

    // A frame usually contains one or more Fields.

    private final int fromId;
    private final String responseId;
    private final Ecu sendingEcu;
    private final ArrayList<Field> fields = new ArrayList<>();
    private final ArrayList<Field> queriedFields = new ArrayList<>();
    private final Frame containingFrame;

    private int interval; // in ms
    private long lastRequest = 0;


    public Frame (int fromId, int interval, Ecu sendingEcu, String responseId, Frame containingFrame) {
        this.fromId = fromId;
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
        return (fromId >= 0x700 && fromId != 0x801); // All 29 bits and the VFC is considered ISOTP too
    }

    public int getFromId() {
        return fromId;
    }

    public String getFromIdHex() {
        return String.format(isExtended() ? "%08x" : "%03x", fromId);
    }

    public String getFromIdHexLSB() {
        return String.format(isExtended() ? "%06x" : "%03x", fromId & 0xffffff);
    }
    public String getFromIdHexMSB() {
        return String.format("%02x", (fromId & 0x1f000000) >> 24);
    }

    public String getRID() { // RID is the from ID, plus the response ID (for ISOTP)
        if(responseId != null && !responseId.trim().isEmpty()) {
            return (getFromIdHex() + "." + responseId.trim()).toLowerCase();
        } else {
            return (getFromIdHex()).toLowerCase();
        }
    }


    private int getToId() {
        Ecu ecu = Ecus.getInstance().getByFromId(fromId);
        return ecu != null ? ecu.getToId() : 0;
    }

    public String getToIdHex() {
        return String.format(isExtended() ? "%08x" : "%03x", getToId ());
    }

    public String getToIdHexLSB() {
        return String.format(isExtended() ? "%06x" : "%03x", getToId() & 0xffffff);
    }

    public String getToIdHexMSB() {
        return String.format("%02x", (getToId() & 0x1f000000) >> 24);
    }


    public boolean isExtended () {
        // 0-6ff = free frame
        // 700-7ff = 11 bits ISOTP
        // 800 = VFC
        // 801 = FFC
        // 802-FFF = reserved
        // 1000-1FFFFFFF = 29 bits ISOTP
        // We are ignoring the possibility for sub 1000 29 bits for now
        return (fromId >= 0x1000);
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
