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

    protected Field field;
    protected String data;

    public Message(Field field, String data) {
        this.field=field;
        this.data=data;
    }

    /* --------------------------------
     * Getters & setters
     \ ------------------------------ */

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    /* --------------------------------
     * Some utilities
     \ ------------------------------ */

    public String getAsBinaryString()
    {
        String result = "";
        for(int i=0; i<data.length(); i+=2)
        {
            try {
                result += String.format("%8s", Integer.toBinaryString(Integer.parseInt(data.substring(i, i + 2), 16) & 0xFF)).replace(' ', '0');
            }
            catch (Exception e) {}
        }
        return result;
    }

}