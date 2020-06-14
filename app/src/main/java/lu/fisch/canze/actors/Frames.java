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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import lu.fisch.canze.R;
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
        load();
    }

    public static Frames getInstance()
    {
        if(instance==null) instance=new Frames();
        return instance;
    }


    private void fillOneLine(String line) {
        if (line.contains("#")) line = line.substring(0, line.indexOf('#'));
        //Get all tokens available in line
        String[] tokens = line.split(",");
        if (tokens.length == 4) {
            //Create a new field object and fill his  data
            Ecu ecu = Ecus.getInstance().getByMnemonic(tokens[FRAME_ECU].trim());
            if (ecu == null) {
                MainActivity.debug("Ecu does not exist:'" + tokens[FRAME_ECU].trim() + "'");
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

    private void fillFromAsset (String assetName) {
        //Read text from asset
        AssetLoadHelper assetLoadHelper = new AssetLoadHelper(MainActivity.getInstance());
        BufferedReader bufferedReader = assetLoadHelper.getBufferedReaderFromAsset(assetName);
        if (bufferedReader == null) {
            MainActivity.toast(MainActivity.TOAST_NONE, MainActivity.getStringSingle(R.string.format_NoAsset),assetName);
            return;
        }
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                fillOneLine(line);
            bufferedReader.close();
        }
        catch (IOException e) {
            MainActivity.logExceptionToCrashlytics(e);
        }
    }

    public void load ()
    {
        load ("");
    }


    public void load (String assetName)
    {
        frames.clear();
        if (assetName.equals("")) {
            fillFromAsset(getDefaultAssetName());
        } else {
            fillFromAsset(assetName);
        }
    }

    public void load (Ecu ecu)
    {
        if (ecu.getFromId() != 0x801) { // for all but the Free Frame ECU, just load it's diagnostic frame. Subframes will be created automatically for each field
            frames.clear();
            Frame frame = new Frame(
                    ecu.getFromId(),
                    0,
                    ecu,
                    null,
                    null
            );
            // add the field to the list of available fields
            add(frame);
        } else { // for the FCC, load all Free Frames
            this.load("FFC_Frames.csv");
        }
    }

    public void add(Frame frame) {
        frames.add(frame);
    }

    public Frame get (int position) {
        return frames.get(position);
    }

    // Lint mark as can be private so probably never used externally
    public Frame getById (int id) {
        for(int i=0; i<frames.size(); i++)
        {
            Frame frame = frames.get(i);
            if (frame.getFromId() == id && frame.getResponseId() == null) return frame;
        }
        return null;
    }

    public Frame getById (int id, String responseId) {
        for(int i=0; i<frames.size(); i++)
        {
            Frame frame = frames.get(i);
            if (frame.getFromId() == id && frame.getResponseId() != null) {
                if (frame.getResponseId().compareTo(responseId.toLowerCase()) == 0) return frame;
            }
        }
        return null;
    }

    public ArrayList<Frame> getAllFrames () {
        return frames;
    }

    private String getDefaultAssetName() {
        // note - we might ditch non-alt mode. I doubt if it's worth the effort for CanSee dongle only
        if (MainActivity.isZOE()) {
            if (MainActivity.altFieldsMode) {
                return "_FramesAlt.csv";
            } else {
                return "_Frames.csv";
            }
        } else if (MainActivity.isZOEZE50())
            return "_FramesZE50.csv";
        return "_Fields.csv";
    }


}
