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
 * Ecus
 */
public class Ecus {

    private final ArrayList<Ecu> ecus = new ArrayList<>();

    private static Ecus instance = null;

    private Ecus() {
        fillStatic();
    }

    public static Ecus getInstance()
    {
        if(instance==null) instance=new Ecus();
        return instance;
    }

    private void fillStatic() {
        String ecuDef = // name,renaultId,networks,fromId,toId,mnemonic,aliases
                ""
                        + "Electric Vehicle Controller,946,V;E,7ec,7e4,EVC,SCH\n"
                        + "Telematics Control Unit,2152,V;M,7da,7ca,TCU,-\n"
                        + "Lithium Battery Controller,938,E,7bb,79b,LBC,-\n"
                        + "Power Electronics Block,2092,E,77e,75a,PEB,-\n"
                        + "Airbag,756,V,772,752,AIBAG,AIRBAG\n"
                        + "U Safety Module,1337,V,76d,74d,USM,UPC;UCM\n"
                        + "Instrument panel,247,V;M,763,743,CLUSTER,BIC\n"
                        + "Electrical Power Steering,1232,V,762,742,EPS,PAS\n"
                        + "Electronic Stability Control,1094,V,760,740,ESC,ABS\n"
                        + "Uncoupled Braking Pedal,2197,V,7bc,79c,UBP,-\n"
                        + "Body Control Module,645,V;O,765,845,BCM,UCH\n"
                        + "Climate Control,419,V,764,744,CLIM,CLIMA;CLIMBOX\n"
                        + "Park Assist,1222,O,76e,74e,UPA,-\n"
                        + "Battery Connection Box,2093,E,793,792,BCB,-\n"
                        + "Lithium Battery Controller 2,938,E,7b6,796,LBC2,-\n"
                        + "Tuner,261,M,0,0,,-\n"
                        + "Joystick,1657,M,0,0,,-\n"
                        + "R-Link,1127,M,0,0,,-\n"
                        + "Horn,2138,E,0,0,,-\n"

                        +"";
        String[] lines = ecuDef.split("\n");
        for (String line : lines) {
            //Get all tokens available in line
            String[] tokens = line.split(",");
            if (tokens.length == 7) {
                //Create a new field object and fill his  data
                Ecu ecu = new Ecu(
                        tokens[0].trim(),
                        Integer.parseInt(tokens[1].trim()),
                        tokens[2].trim(),
                        Integer.parseInt(tokens[3].trim(), 16),
                        Integer.parseInt(tokens[4].trim(), 16),
                        tokens[5].trim(),
                        tokens[6].trim()
                );
                // add the field to the list of available fields
                add(ecu);
            }
        }
    }

    public void add(Ecu ecu) {
        ecus.add(ecu);
    }

    public Ecu getByMnemonic (String mnemonic) {
        for (Ecu ecu : ecus) {
            if (ecu.getMnemonic().equals(mnemonic) || ecu.getAliases().contains(mnemonic)) return ecu;
        }
        return null;
    }

    public Ecu getByRenaultId (int renaultId) {
        for (Ecu ecu : ecus) {
            if (ecu.getRenaultId() == renaultId) return ecu;
        }
        return null;
    }

    public Ecu getByFromId (int fromId) {
        for (Ecu ecu : ecus) {
            if (ecu.getFromId() == fromId) return ecu;
        }
        return null;
    }

    public Ecu getByToId (int toId) {
        for (Ecu ecu : ecus) {
            if (ecu.getToId() == toId) return ecu;
        }
        return null;
    }

    public ArrayList<Ecu> getAllEcus () {
        return ecus;
    }

}
