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

/**
 * Quick and dirty DTC lookup. Needs to be made fast and als needs an int lookup.
 */
public class Dtcs {

    private static final String [] dtc = {
            "061263,BATTERY CHARGE CIRCUIT",
            "066263,BATTERY CHARGE CIRCUIT",
            "064913,BATTERY CHARGE CIRCUIT",
            "064764,BATTERY CHARGE CIRCUIT",
            "C10000,EVC ABSENT MULTIPLEX SIGNAL SENT",
            "518196,ESP MULTIPLEX INFORMATION PLAUSIBILITY",
            "C12200,NO ABS/ESP MULTIPLEX SIGNAL",
            "518296,EVC MULTIPLEX INFORMATION PLAUSIBILITY",
            "508E55,UBP SOLENOID VALVE",
            "C07300,MULTIPLEXED NETWORK",
            "C00100,NO MULTIPLEX SIGNAL",
            "921215,LEFT-HAND DIPPED HEADLIGHT CIRCUIT",
            "921315,RIGHT-HAND DIPPED HEADLIGHT CIRCUIT",
            "AE01F0,COMMUNICATION PROTOCOL ERROR",
            "041168,CAN COMMUNICATION",
            "1525F3,CONSISTENT MULTIPLEX SIGNALS FOR CC/SL",
            "060198,ELECTRIC MOTOR PERFORMANCE"
    };

    static public String getDescription (String dtcCode) {
        for (String aDtc : dtc) {
            if (aDtc.startsWith(dtcCode)) {
                return (aDtc.substring(7));
            }
        }
        return "";
    }
}
