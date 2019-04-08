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

package lu.fisch.canze.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Ecus;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Frames;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;


public class TyresActivity extends CanzeActivity implements FieldListener, DebugListener {

    public static final String SID_TyreSpdPresMisadaption = "673.0";
    public static final String SID_TyreFLState = "673.11";
    public static final String SID_TyreFLPressure = "673.40";
    public static final String SID_TyreFRState = "673.8";
    public static final String SID_TyreFRPressure = "673.32";
    public static final String SID_TyreRLState = "673.5";
    public static final String SID_TyreRLPressure = "673.24";
    public static final String SID_TyreRRState = "673.2";
    public static final String SID_TyreRRPressure = "673.16";

    public static final String val_TyreSpdPresMisadaption[] = {"OK", "Not OK"};
    public static final String val_TyreState[] = {"OK", "No info", "-", "-", "-", "Flat", "Under infl."};
    public static final String val_Unavailable = "-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT != 26 && Build.VERSION.SDK_INT != 27) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tyres);

        // do not display the keyboard immediately
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // set the two button handlers
        Button button = findViewById(R.id.button_TyresRead);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        buttonRead();
                    }
                })).start();

            }
        });

        button = findViewById(R.id.button_TyresWrite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        buttonWrite();
                    }
                })).start();
            }
        });
    }

    // set the fields the poller should query
    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_TyreSpdPresMisadaption, 6000);
        addField(SID_TyreFLState, 6000);
        addField(SID_TyreFLPressure, 6000);
        addField(SID_TyreFRState, 6000);
        addField(SID_TyreFRPressure, 6000);
        addField(SID_TyreRLState, 6000);
        addField(SID_TyreRLPressure, 6000);
        addField(SID_TyreRRState, 6000);
        addField(SID_TyreRRPressure, 6000);
    }

    // fired event when any of the registered fields are getting updated by the device
    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                TextView tv = null;
                String value = "";
                int intValue = (int) field.getValue();
                int color = 0xffc0c0c0;

                // get the text field
                switch (fieldId) {

                    case SID_TyreSpdPresMisadaption:
                        tv = findViewById(R.id.text_TyreSpdPresMisadaption);
                        color = 0; // don't set color
                        value = val_TyreSpdPresMisadaption[intValue];
                        break;
                    case SID_TyreFLState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TyreFLState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreFLPressure:
                        tv = findViewById(R.id.text_TyreFLPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case SID_TyreFRState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TyreFRState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreFRPressure:
                        tv = findViewById(R.id.text_TyreFRPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case SID_TyreRLState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TyreRLState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreRLPressure:
                        tv = findViewById(R.id.text_TyreRLPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case SID_TyreRRState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TyreRRState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreRRPressure:
                        tv = findViewById(R.id.text_TyreRRPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText(value);
                    if (color != 0) tv.setBackgroundColor(color);
                }

                tv = findViewById(R.id.textDebug);
                tv.setText(fieldId);
            }
        });
    }


    private void displayId(final int fieldId, final int val) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText et = findViewById(fieldId);
                if (et != null)
                    et.setText(String.format("%06x", val));
            }
        });
    }


    private void buttonRead() {
        int idFrontLeft = 0;
        int idFrontRight = 0;
        int idRearLeft = 0;
        int idRearRight = 0;
        Frame[] frames = new Frame[3];

        frames[0] = Frames.getInstance().getById(0x765, "50c0"); // tester present to BCM
        frames[1] = Frames.getInstance().getById(0x765, "7e01"); // tester awake to BCM
        frames[2] = Frames.getInstance().getById(0x765, "6171"); // get TPMS ids

        if (MainActivity.device == null)
            return; // this should not happen as the fragment checks the device property, but it does
        Message message = MainActivity.device.injectRequests(frames); // return result message of last request (get TPMS ids)
        if (message.isError()) {
            MainActivity.toast(-100, "Could not read TPMS valves:" + message.getData());
            return;
        }

        // process the frame by going through all the containing fields
        // setting their values and notifying all listeners (there should be none)
        message.onMessageCompleteEvent();

        // now process all fields in the frame. Select only the ones we are interested in
        for (Field field : frames[2].getAllFields()) {
            switch (field.getFrom()) {
                case 24:
                    idFrontLeft = (int) field.getValue();
                case 48:
                    idFrontRight = (int) field.getValue();
                case 72:
                    idRearLeft = (int) field.getValue();
                case 96:
                    idRearRight = (int) field.getValue();
            }
        }

        if (idFrontLeft == 0 || idFrontRight == 0 || idRearLeft == 0 || idRearRight == 0) {
            MainActivity.toast(-100, "No TPMS valves found");
            return;
        }

        // display the fetched values
        displayId(R.id.text_TyreFLId, idFrontLeft);
        displayId(R.id.text_TyreFRId, idFrontRight);
        displayId(R.id.text_TyreRLId, idRearLeft);
        displayId(R.id.text_TyreRRId, idRearRight);
        MainActivity.toast(-100, "TPMS valves read");
    }

    private int simpleIntParse(int fieldId) {
        EditText et = findViewById(fieldId);
        if (et != null) {
            try {
                return Integer.parseInt(et.getText().toString(), 16);
            } catch (Exception e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public void buttonWrite() {
        int idFrontLeft = simpleIntParse(R.id.text_TyreFLId);
        int idFrontRight = simpleIntParse(R.id.text_TyreFRId);
        int idRearLeft = simpleIntParse(R.id.text_TyreRLId);
        int idRearRight = simpleIntParse(R.id.text_TyreRRId);

        if (idFrontLeft == 0 || idFrontRight == 0 || idRearLeft == 0 || idRearRight == 0) {
            MainActivity.toast(-100, "Those are not all valid hex values other than 000000");
            return;
        }

        Frame[] frames = new Frame[3];

        frames[0] = Frames.getInstance().getById(0x765, "50c0"); // tester present to BCM
        frames[1] = Frames.getInstance().getById(0x765, "7e01"); // tester awake to BCM
        frames[2] = new Frame(0x765, 0, Ecus.getInstance().getByMnemonic("BCM"), String.format("7b5d%06X%06X%06X%06X", idFrontLeft, idFrontRight, idRearLeft, idRearRight), null); // set TMPS ids

        if (MainActivity.device == null)
            return; // this should not happen as the fragment checks the device property, but it does
        Message message = MainActivity.device.injectRequests(frames);
        if (message.isError()) {
            MainActivity.toast(-100, "Could not write TPMS valves:" + message.getData());
            return;
        }

        if (!message.getData().startsWith("7b5d")) {
            MainActivity.toast(-100, "Could not write TPMS valves:" + message.getData());
            return;
        }
        MainActivity.toast(-100, "TPMS valves written. Read again to verify");
    }
}