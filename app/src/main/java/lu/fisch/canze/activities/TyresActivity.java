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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Frames;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.database.CanzeDataSource;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

import static lu.fisch.canze.activities.MainActivity.toast;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tyres);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button button = findViewById(R.id.button_TyresRead);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonRead();
            }
        });

        button = findViewById(R.id.button_TyresWrite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonWrite();
            }
        });

    }

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

    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
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
                        tv = findViewById(R.id.text_TyreFLState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreFLPressure:
                        tv = findViewById(R.id.text_TyreFLPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case SID_TyreFRState:
                        tv = findViewById(R.id.text_TyreFRState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreFRPressure:
                        tv = findViewById(R.id.text_TyreFRPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case SID_TyreRLState:
                        tv = findViewById(R.id.text_TyreRLState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreRLPressure:
                        tv = findViewById(R.id.text_TyreRLPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case SID_TyreRRState:
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


    private void onButtonRead() {
        EditText et;
        int valuefl = 0;
        int valuefr = 0;
        int valuerl = 0;
        int valuerr = 0;
        Frame frame;

        frame = Frames.getInstance().getById(0x765, "50c0");
        MainActivity.device.requestFrame(frame);

        frame = Frames.getInstance().getById(0x765, "7e01");
        MainActivity.device.requestFrame(frame);

        // query the Frame
        frame = Frames.getInstance().getById(0x765, "6171");
        Message message = MainActivity.device.requestFrame(frame);
        if (message.isError()) {
            MainActivity.toast(-100, "Could not read TPMS valves");
            return;
        }

        // process the frame by going through all the containing fields
        // setting their values and notifying all listeners (there should be none)
        // Fields.getInstance().onMessageCompleteEvent(message);
        message.onMessageCompleteEvent();
        for (Field field : frame.getAllFields()) {
            switch (field.getFrom()) {
                case 24:
                    valuefl = (int)field.getValue();
                case 48:
                    valuefr = (int)field.getValue();
                case 72:
                    valuerl = (int)field.getValue();
                case 96:
                    valuerr = (int)field.getValue();
            }
        }
        if (valuefl == 0 || valuefr == 0 || valuerl == 0 || valuerr == 0) {
            MainActivity.toast(-100, "No TPMS valves found");
        }


        et = findViewById(R.id.text_TyreFLId);
        et.setText(String.format("%06x", valuefl));
        et = findViewById(R.id.text_TyreFRId);
        et.setText(String.format("%06x", valuefr));
        et = findViewById(R.id.text_TyreRLId);
        et.setText(String.format("%06x", valuerl));
        et = findViewById(R.id.text_TyreRRId);
        et.setText(String.format("%06x", valuerr));
        MainActivity.toast(-100, "TPMS valves read");
    }

    private int simpleIntParse (String p) {
        try {
            return Integer.parseInt(p, 16);
        } catch (Exception e) {
            return 0;
        }
    }


    public void onButtonWrite() {
        String result = "7b5d";
        int valuefl;
        int valuefr;
        int valuerl;
        int valuerr;
        EditText et;

        et = findViewById(R.id.text_TyreFLId);
        valuefl = simpleIntParse(et.getText().toString());
        et = findViewById(R.id.text_TyreFRId);
        valuefr = simpleIntParse(et.getText().toString());
        et = findViewById(R.id.text_TyreRLId);
        valuerl = simpleIntParse(et.getText().toString());
        et = findViewById(R.id.text_TyreRRId);
        valuerr = simpleIntParse(et.getText().toString());

        if (valuefl == 0 || valuefr == 0 || valuerl == 0 || valuerr == 0) {
            MainActivity.toast(-100, "Those are not all valid hex values other than 000000");
        }

        result = result + String.format ("%06x", valuefl);
        result = result + String.format ("%06x", valuefr);
        result = result + String.format ("%06x", valuerl);
        result = result + String.format ("%06x", valuerr);

        MainActivity.toast(-100, result);

        Frame frame;
        frame = Frames.getInstance().getById(0x765, "50c0");
        MainActivity.device.requestFrame(frame);
        frame = Frames.getInstance().getById(0x765, "7e01");
        MainActivity.device.requestFrame(frame);

        frame = new Frame (0x765, 0, null, result, null);
        Message message = MainActivity.device.requestFrame(frame);
        if (!message.isError() || !message.getData().startsWith("7b5d")) {
            MainActivity.toast(-100, "Could not write TPMS valves");
            return;
        }
    }

}