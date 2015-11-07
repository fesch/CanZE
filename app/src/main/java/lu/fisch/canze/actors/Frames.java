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
 * Frames
 */
public class Frames {

    private final ArrayList<Frame> frames = new ArrayList<>();

    private static Frames instance = null;

    private Frames() {
        fillStatic();
    }

    public static Frames getInstance()
    {
        if(instance==null) instance=new Frames();
        return instance;
    }

    private void fillStatic() {
        String frameDef = // Id, interval, sendingEcu
                ""
                        +"0x023,4,AIBAG\n"
                        +"0x0C6,10,EPS\n"
                        +"0x12E,10,ESC\n"
                        +"0x130,10,UBP\n"
                        +"0x17A,10,EVC\n"
                        +"0x17E,10,EVC\n"
                        +"0x186,10,EVC\n"
                        +"0x18A,10,EVC\n"
                        +"0x1F6,10,EVC\n"
                        +"0x1F8,10,EVC\n"
                        +"0x1FD,100,EVC\n"
                        +"0x212,20,USM\n"
                        +"0x242,20,ESC\n"
                        +"0x29A,20,ESC\n"
                        +"0x29C,20,ESC\n"
                        +"0x2B7,20,ESC\n"
                        +"0x352,40,ESC\n"
                        +"0x354,40,ESC\n"
                        +"0x35C,100,BCM\n"
                        +"0x391,50,CLIMA\n"
                        +"0x3B7,100,CLUSTER\n"
                        +"0x3F7,60,EVC\n"
                        +"0x427,100,EVC\n"
                        +"0x42A,100,CLIMA\n"
                        +"0x42E,100,EVC\n"
                        +"0x430,100,CLIMA\n"
                        +"0x432,100,EVC\n"
                        +"0x433,1000,CLUSTER\n"
                        +"0x4F8,100,CLUSTER\n"
                        +"0x500,100,BCM\n"
                        +"0x505,100,BCM\n"
                        +"0x511,100,EVCBRIDGE\n"
                        +"0x552,100,BCM\n"
                        +"0x563,100,EPS\n"
                        +"0x581,100,ESCL\n"
                        +"0x5D1,0,CLUSTER\n"
                        +"0x5D7,100,ESC\n"
                        +"0x5DA,100,EVC\n"
                        +"0x5DE,100,BCM\n"
                        +"0x5E9,100,UPA via BCMBRIDGE\n"
                        +"0x5EE,100,BCM\n"
                        +"0x62C,100,EPS\n"
                        +"0x62D,500,EVC\n"
                        +"0x634,100,TCU\n"
                        +"0x637,500,EVC\n"
                        +"0x638,100,EVC\n"
                        +"0x646,500,CLUSTER\n"
                        +"0x650,100,EVC\n"
                        +"0x652,100,\n"
                        +"0x653,100,AIRBAG\n"
                        +"0x654,500,EVC\n"
                        +"0x656,100,CLUSTER\n"
                        +"0x657,100,BCM\n"
                        +"0x658,3000,EVC\n"
                        +"0x65B,100,EVC\n"
                        +"0x665,3000,CLUSTER\n"
                        +"0x666,100,ESC\n"
                        +"0x668,100,CLIMA\n"
                        +"0x66A,100,EVC\n"
                        +"0x66D,100,UBP\n"
                        +"0x673,100,BCM\n"
                        +"0x68B,100,CLUSTER\n"
                        +"0x68C,1000,CLUSTER\n"
                        +"0x699,1000,CLIMA\n"
                        +"0x69F,1000,BCM\n"
                        +"0x6F8,100,USM\n"
                        +"0x6FB,3000,CLUSTER\n"
                        +"0x722,0,LINSCH\n"
                        +"0x760,0,ESC\n"
                        +"0x762,0,EPS\n"
                        +"0x763,0,CLUSTER\n"
                        +"0x764,0,CLIM\n"
                        +"0x765,0,BCM\n"
                        +"0x76D,0,USM\n"
                        +"0x76E,0,UPA\n"
                        +"0x772,0,AIBAG\n"
                        +"0x77E,0,PEB\n"
                        +"0x793,0,BCB\n"
                        +"0x7B6,0,LBC2\n"
                        +"0x7BB,0,LBC\n"
                        +"0x7BC,0,UBP\n"
                        +"0x7DA,0,TCU\n"
                        +"0x7EC,0,EVC\n"
                        +"\n"
                        +"";
        String[] lines = frameDef.split("\n");
        for (String line : lines) {
            //Get all tokens available in line
            String[] tokens = line.split(",");
            if (tokens.length > 0) {
                //Create a new field object and fill his  data
                Ecu ecu = Ecus.getInstance().getByMnemonic(tokens[2].trim());
                Frame frame = new Frame(
                        Integer.parseInt(tokens[0].trim().replace("0x", ""), 16),
                        Integer.parseInt(tokens[1].trim(), 10),
                        ecu
                );
                // add the field to the list of available fields
                add(frame);
            }
        }
    }

    public void add(Frame frame) {
        frames.add(frame);
    }

    public Frame getById (int id) {
        for (Frame frame : frames) {
            if (frame.getId() == id) return frame;
        }
        return null;
    }

}
