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

import lu.fisch.canze.activities.MainActivity;

/**
 * Frames
 */
public class Frames {

    private static final int FRAME_ID               = 0; // to be stated in HEX, no leading 0x
    private static final int FRAME_INTERVAL_ZOE     = 1; // decimal
    private static final int FRAME_INTTERVAL_FLUKAN = 2; // decimal
    private static final int FRAME_ECU              = 3; // double

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
        String frameDef = // Id, intervalZOE, intervalFLUKAN, sendingEcu
                ""

                        + "023,4,4,AIBAG\n"
                        + "0C6,10,10,EPS\n"
                        + "12E,10,10,ESC\n"
                        + "130,10,10,UBP\n"
                        + "17A,10,10,EVC\n"
                        + "17E,10,10,EVC\n"
                        + "186,10,10,EVC\n"
                        + "18A,10,10,EVC\n"
                        + "1F6,10,10,EVC\n"
                        + "1F8,10,10,EVC\n"
                        + "1FD,100,100,EVC\n"
                        + "212,20,20,USM\n"
                        + "242,20,20,ESC\n"
                        + "29A,20,20,ESC\n"
                        + "29C,20,20,ESC\n"
                        + "2B7,20,20,ESC\n"
                        + "352,40,200,ESC\n"
                        + "354,40,40,ESC\n"
                        + "35C,100,100,BCM\n"
                        + "391,50,50,CLIM\n"
                        + "3B7,100,100,CLUSTER\n"
                        + "3F7,60,60,EVC\n"
                        + "427,100,100,EVC\n"
                        + "42A,100,100,CLIM\n"
                        + "42E,100,100,EVC\n"
                        + "430,100,100,CLIM\n"
                        + "432,100,100,EVC\n"
                        + "433,1000,1000,CLUSTER\n"
                        + "4F8,100,100,CLUSTER\n"
                        + "500,100,100,BCM\n"
                        + "505,100,100,BCM\n"
                        + "511,100,100,EVC\n"
                        + "534,100,100,BCM\n"
                        + "552,100,100,BCM\n"
                        + "563,100,100,EPS\n"
                        + "5D1,0,0,CLUSTER\n"
                        + "5D7,100,100,ESC\n"
                        + "5DA,100,100,EVC\n"
                        + "5DE,100,100,BCM\n"
                        + "5E9,100,100,UPA\n"
                        + "5EE,100,100,BCM\n"
                        + "62C,100,100,EPS\n"
                        + "62D,500,500,EVC\n"
                        + "634,100,100,TCU\n"
                        + "637,500,500,EVC\n"
                        + "638,100,100,EVC\n"
                        + "646,500,500,CLUSTER\n"
                        + "650,100,300,EVC\n"
                        + "653,100,100,AIRBAG\n"
                        + "654,500,500,EVC\n"
                        + "656,100,100,CLUSTER\n"
                        + "657,100,100,BCM\n"
                        + "658,3000,3000,EVC\n"
                        + "65B,100,100,EVC\n"
                        + "665,3000,3000,CLUSTER\n"
                        + "666,100,200,ESC\n"
                        + "668,100,100,CLIMA\n"
                        + "66A,100,100,EVC\n"
                        + "66D,100,100,UBP\n"
                        + "671,9999,100,\n"
                        + "673,100,100,BCM\n"
                        + "68B,100,100,CLUSTER\n"
                        + "68C,1000,1000,CLUSTER\n"
                        + "699,1000,1000,CLIMA\n"
                        + "69F,1000,1000,BCM\n"
                        + "6F8,100,100,USM\n"
                        + "6FB,3000,3000,CLUSTER\n"
                        + "702,0,0,\n"
                        + "722,0,0,LINSCH\n"
                        + "740,0,0,\n"
                        + "742,0,0,\n"
                        + "743,0,0,\n"
                        + "744,0,0,\n"
                        + "745,0,0,\n"
                        + "74D,0,0,\n"
                        + "74E,0,0,\n"
                        + "752,0,0,\n"
                        + "75A,0,0,\n"
                        + "760,0,0,ESC\n"
                        + "762,0,0,EPS\n"
                        + "763,0,0,CLUSTER\n"
                        + "764,0,0,CLIM\n"
                        + "765,0,0,BCM\n"
                        + "76D,0,0,USM\n"
                        + "76E,0,0,UPA\n"
                        + "772,0,0,AIBAG\n"
                        + "77E,0,0,PEB\n"
                        + "792,0,0,\n"
                        + "793,0,0,BCB\n"
                        + "796,0,0,\n"
                        + "79B,0,0,\n"
                        + "79C,0,0,\n"
                        + "7B6,0,0,LBC2\n"
                        + "7BB,0,0,LBC\n"
                        + "7BC,0,0,UBP\n"
                        + "7CA,0,0,\n"
                        + "7DA,0,0,TCU\n"
                        + "7DF,0,0,\n"
                        + "7E4,0,0,\n"
                        + "7EC,0,0,EVC\n"

                        + "800,0,0,VFC";

        fillDynamic(frameDef);
    }


    private void fillDynamic(String frameDef){

        String[] lines = frameDef.split("\n");
        for (String line : lines) {
            //Get all tokens available in line
            String[] tokens = line.split(",");
            if (tokens.length == 4) {
                //Create a new field object and fill his  data
                Ecu ecu = Ecus.getInstance().getByMnemonic(tokens[FRAME_ECU].trim());
                if (ecu == null) {
                    MainActivity.debug("Ecu does not exist:" + tokens[FRAME_ECU].trim());
                } else {
                    int frameId = Integer.parseInt(tokens[FRAME_ID].trim(), 16);
                    int interval = MainActivity.car == MainActivity.CAR_ZOE_Q210 || MainActivity.car == MainActivity.CAR_ZOE_R240 ? Integer.parseInt(tokens[FRAME_INTERVAL_ZOE].trim(), 10) : Integer.parseInt(tokens[FRAME_INTTERVAL_FLUKAN].trim(), 10);
                    Frame frame = getById(frameId);
                    if (frame == null) {
                        frame = new Frame(
                                frameId,
                                interval,
                                ecu,
                                null,
                                null
                        );
                    } else {
                        frame.setInterval(interval);
                    }
                    // add the field to the list of available fields
                    add(frame);
                }
            }
        }
    }

    public void add(Frame frame) {
        frames.add(frame);
    }

    public Frame getById (int id) {
        for(int i=0; i<frames.size(); i++)
        {
            Frame frame = frames.get(i);
            if (frame.getId() == id && frame.getResponseId() == null) return frame;
        }
        return null;
    }

    public Frame getById (int id, String responseId) {
        for(int i=0; i<frames.size(); i++)
        {
            Frame frame = frames.get(i);
            if (frame.getId() == id && frame.getResponseId() != null) {
                if (frame.getResponseId().compareTo(responseId) == 0) return frame;
            }
        }
        return null;
    }

    public ArrayList<Frame> getAllFrames () {
        return frames;
    }

    public void load ()
    {
        frames.clear();
        fillStatic();
    }

    public void load (String initString)
    {
        frames.clear();
        fillDynamic(initString);
    }
}
