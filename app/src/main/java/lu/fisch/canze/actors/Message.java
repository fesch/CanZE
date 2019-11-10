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
    // Note that for ISO-TP frames, a frame is always a sub-frame
    // If an error occurs while fetching a message, the error flag is set to true and the data
    // part now represents a readable error. The methods ensure that the data part is only
    // processed when there is no error condition set

    private final Frame frame;
    private final String data;
    private final boolean error;

    public Message(Frame frame, String data, boolean error) {
        MainActivity.debug("Message.new.data:" + data);
        this.frame=frame;
        if (frame.isIsoTp()) {
            if (data.startsWith("7f")) {
                this.error = true;
                this.data = "-E-Message.isotp.startswith7f";
                return;
            }
        }
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
        // simply update the fields last request date to send them to the end of the queue
        for (Field field : frame.getAllFields()) {
            field.updateLastRequest();
            field.setValue(Double.NaN);
        }
    }


    public void onMessageCompleteEvent() {

        // If a message frame comes in, simply update all fields that are defined for it.
        // Note that for an IsoTP field, the getFrame() method returns as sub-frame. The sub-frame's
        // getAllFields() method only returns the fields with the same responseId.

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
                    StringBuilder tmpVal = new StringBuilder();
                    for (int i = 0; i < binString.length(); i += 8) {
                        tmpVal.append ((char) Integer.parseInt("0" + binString.substring(i, i+8), 2));
                    }
                    String val = tmpVal.toString();
                    field.setValue(val);

                } else if (binString.length() <= 4 || binString.contains("0")) {
                    // experiment with unavailable: any field >= 5 bits whose value contains only 1's
                    int val;

                    if (field.isSigned() && binString.startsWith("1")) {
                        // ugly method: flip bits, add a minus in front and subtract one
                        val = Integer.parseInt("-" + binString.replace('0', 'q').replace('1','0').replace('q','1'), 2) - 1;
                    } else {
                        val = Integer.parseInt("0" + binString, 2);
                    }
                    //MainActivity.debug("Value of " + field.getHexId() + "." + field.getResponseId() + "." + field.getFrom()+" = "+val);
                    //MainActivity.debug("Fields: onMessageCompleteEvent > "+field.getSID()+" = "+val);

                    // update the value of the field. This triggers updating all of all listeners of that field
                    field.setValue(val);

                } else {
                    field.setValue(Double.NaN);
                }

                // do field logging
                if(MainActivity.fieldLogMode)
                    FieldLogger.getInstance().log(field.getDebugValue());

                // update the fields last request date
                field.updateLastRequest();

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

    private String getAsBinaryString()
    {
        StringBuilder result = new StringBuilder();
        if (!error) {
            for (int i = 0; i < data.length(); i += 2) {
                try {
                    result.append (String.format("%8s", Integer.toBinaryString(Integer.parseInt(data.substring(i, i + 2), 16) & 0xFF)).replace(' ', '0'));
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        return result.toString();
    }

}