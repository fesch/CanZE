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


import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.classes.FieldLogger;

/**
 *
 * @author robertfisch
 */
public class Message {

    // A message represents a message coming from a device as a result from a frame request.
    // Note that for ISO-TP frames, a frame is always a subframe
    // If an error occurs while fetching a message, the error flag is set to true and the data
    // part now represents a readable error. The methods ensure that the data part is only
    // processed when there is no error condition set

    protected Frame frame;
    protected String data;
    protected boolean error;

    public Message(Frame frame, String data, boolean error) {
        this.frame=frame;
        this.data=data;
        this.error=error;
    }

    /* --------------------------------
     * Getters & setters
     \ ------------------------------ */

    public String getData() {
        return (error || data == null) ? "" : data;
    }

    /* public void setData(String data) {
        this.data = data;
    }*/

    public Frame getFrame() {
        return frame;
    }

    /* public void setFrame(Frame frame) {
        this.frame = frame;
    } */

    public boolean isError () {return error;}

    public String getError () { return error ? data : ""; }

    public void onMessageIncompleteEvent () {
        for (Field field : frame.getAllFields()) {
            field.updateLastRequest();
        }
    }


    public void onMessageCompleteEvent() {

        // If a message frame comes in, simply update all fields that are defined for it.
        // Note that for an IsoTP field, the getFrame() method returns as subframe. The subframe's
        // getAllFields() method only returns the fields with the ssame responseId.

        // this function is called from DtcActivity ("manual mode") and
        // Device.queryNextFilter ("auto mode")

        if (error) return;

        String binString = getAsBinaryString();
        for (Field field : frame.getAllFields()) {
            onMessageCompleteEventField(binString, field);
        }
    }

    private void onMessageCompleteEventField(String binString, Field field) {
        if(binString.length()>= field.getTo()) {
            // parseInt --> signed, so the first bit is "cut-off"!
            try {
                binString = binString.substring(field.getFrom(), field.getTo() + 1);
                if (field.isString()) {
                    String val = "";
                    for (int i = 0; i < binString.length(); i += 8) {
                        val += Character.toString((char) Integer.parseInt("0" + binString.substring(i, i+8), 2));
                    }
                    field.setValue(val);
                    // do field logging
                    if (MainActivity.fieldLogMode)
                        FieldLogger.getInstance().log(field.getSID() + "," + val);

                } else if (binString.length() <= 4 || binString.contains("0")) {
                    // experiment with unavailable: any field >= 5 bits whose value contains only 1's
                    int val;

                    if (field.isSigned() && binString.startsWith("1")) {
                        // ugly method: flip bits, add a minus in front and substract one
                        val = Integer.parseInt("-" + binString.replace('0', 'q').replace('1','0').replace('q','1'), 2) - 1;
                    } else {
                        val = Integer.parseInt("0" + binString, 2);
                    }
                    //MainActivity.debug("Value of " + field.getHexId() + "." + field.getResponseId() + "." + field.getFrom()+" = "+val);
                    //MainActivity.debug("Fields: onMessageCompleteEvent > "+field.getSID()+" = "+val);

                    // update the value of the field. This triggers updating all of all listeners of that field
                    field.setValue(val);
                    // do field logging
                    if(MainActivity.fieldLogMode)
                        FieldLogger.getInstance().log(field.getSID()+","+val);

                } else {
                    field.setValue(Double.NaN);
                    // do field logging
                    if(MainActivity.fieldLogMode)
                        FieldLogger.getInstance().log(field.getSID()+",NaN");
                }
                // update the fields last request date
                field.updateLastRequest();

/*
                        int val = Integer.parseInt("0" + binString.substring(field.getFrom(), field.getTo() + 1), 2);
                        //MainActivity.debug("Value of " + field.getHexId() + "." + field.getResponseId() + "." + field.getFrom()+" = "+val);
                        //MainActivity.debug("Fields: onMessageCompleteEvent > "+field.getSID()+" = "+val);
                        field.setValue(val);
                        // update the fields last request date
                        field.updateLastRequest();
*/
            } catch (Exception e)
            {
                MainActivity.debug("Message.onMessageCompleteEventField: Exception!!");
                // ignore
            }
        }
    }



    /* --------------------------------
     * Some utilities
     \ ------------------------------ */

    public String getAsBinaryString()
    {
        String result = "";
        if (!error) {
            for (int i = 0; i < data.length(); i += 2) {
                try {
                    result += String.format("%8s", Integer.toBinaryString(Integer.parseInt(data.substring(i, i + 2), 16) & 0xFF)).replace(' ', '0');
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

}