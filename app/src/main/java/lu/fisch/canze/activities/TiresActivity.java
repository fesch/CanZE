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

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Ecu;
import lu.fisch.canze.actors.Ecus;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Frames;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;


public class TiresActivity extends CanzeActivity implements FieldListener, DebugListener {

    private static final String SID_TireSpdPresMisadaption = "673.0";
    private static final String SID_TireFLState = "673.11";
    private static final String SID_TireFLPressure = "673.40";
    private static final String SID_TireFRState = "673.8";
    private static final String SID_TireFRPressure = "673.32";
    private static final String SID_TireRLState = "673.5";
    private static final String SID_TireRLPressure = "673.24";
    private static final String SID_TireRRState = "673.2";
    private static final String SID_TireRRPressure = "673.16";
    private static final String SID_TpmsState = "765.6171.16";

    private static final String[] val_TireSpdPresMisadaption = {MainActivity.getStringSingle(R.string.default_Ok), MainActivity.getStringSingle(R.string.default_NotOk)};
    private static final String[] val_TireState = MainActivity.getStringList(R.array.list_TireStatus);
    private static final String val_Unavailable = MainActivity.getStringSingle(R.string.default_Dash);
    @ColorInt
    private int baseColor;
    @ColorInt private int alarmColor;
    private boolean dark;
    static int previousState = -1;
    private final Ecu ecu = Ecus.getInstance().getByMnemonic("BCM");
    private final int ecuFromId = ecu.getFromId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tires);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorButtonNormal, typedValue, true);
        baseColor = typedValue.data;
        dark = ((baseColor & 0xff0000) <= 0xa00000);
        alarmColor = dark ? baseColor + 0x200000 : baseColor - 0x00002020;

        // do not display the keyboard immediately
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // set the two button handlers
        Button button = findViewById(R.id.button_TiresRead);
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

        button = findViewById(R.id.button_TiresWrite);
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

    protected void onResume () {
        super.onResume();
        tpmsState(-1);
    }

    // set the fields the poller should query
    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_TireSpdPresMisadaption, 6000);
        addField(SID_TireFLState, 6000);
        addField(SID_TireFLPressure, 6000);
        addField(SID_TireFRState, 6000);
        addField(SID_TireFRPressure, 6000);
        addField(SID_TireRLState, 6000);
        addField(SID_TireRLPressure, 6000);
        addField(SID_TireRRState, 6000);
        addField(SID_TireRRPressure, 6000);
        addField(SID_TpmsState, 6000);
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
                int color = baseColor;

                // get the text field
                switch (fieldId) {

                    case SID_TireSpdPresMisadaption:
                        tv = findViewById(R.id.text_TireSpdPresMisadaption);
                        color = 0; // don't set color
                        value = val_TireSpdPresMisadaption[intValue];
                        break;
                    case SID_TireFLState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TireFLState);
                        if (intValue > 1) color = alarmColor;
                        value = val_TireState != null ? val_TireState[intValue] : "";
                        break;
                    case SID_TireFLPressure:
                        tv = findViewById(R.id.text_TireFLPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case SID_TireFRState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TireFRState);
                        if (intValue > 1) color = alarmColor;
                        value = val_TireState != null ? val_TireState[intValue] : "";
                        break;
                    case SID_TireFRPressure:
                        tv = findViewById(R.id.text_TireFRPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case SID_TireRLState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TireRLState);
                        if (intValue > 1) color = alarmColor;
                        value = val_TireState != null ? val_TireState[intValue] : "";
                        break;
                    case SID_TireRLPressure:
                        tv = findViewById(R.id.text_TireRLPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case SID_TireRRState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TireRRState);
                        if (intValue > 1) color = alarmColor;
                        value = val_TireState != null ? val_TireState[intValue] : "";
                        break;
                    case SID_TireRRPressure:
                        tv = findViewById(R.id.text_TireRRPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case SID_TpmsState:
                        tpmsState (intValue);
                        tv = null;
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

    private void tpmsState (int state) {
        if (state == previousState) return;
        previousState = state;
        boolean isEnabled = (state == 1);
        Button button = findViewById(R.id.button_TiresRead);
        button.setEnabled(isEnabled);
        button = findViewById(R.id.button_TiresWrite);
        button.setEnabled(isEnabled);
        EditText edittext;
        edittext = findViewById(R.id.text_TireFLId);
        edittext.setEnabled(isEnabled);
        edittext = findViewById(R.id.text_TireFRId);
        edittext.setEnabled(isEnabled);
        edittext = findViewById(R.id.text_TireRLId);
        edittext.setEnabled(isEnabled);
        edittext = findViewById(R.id.text_TireRRId);
        edittext.setEnabled(isEnabled);
        if (!isEnabled) MainActivity.toast(MainActivity.TOAST_NONE, "Your car has no TPMS system");
    }

    private void displayId(final int fieldId, final int val) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText et = findViewById(fieldId);
                if (et != null)
                    et.setText(String.format("%06X", val));
            }
        });
    }


    private void buttonRead() {
        int idFrontLeft = 0;
        int idFrontRight = 0;
        int idRearLeft = 0;
        int idRearRight = 0;

        Frame frame = Frames.getInstance().getById(ecuFromId, "6171"); // get TPMS ids

        if (MainActivity.device == null)
            return; // this should not happen as the fragment checks the device property, but it does
        Message message = MainActivity.device.injectRequest(frame); // return result message of last request (get TPMS ids)
        if (message == null) {
            MainActivity.toast(MainActivity.TOAST_NONE, "Could not read TPMS valves");
            return;
        }
        if (message.isError()) {
            MainActivity.toast(MainActivity.TOAST_NONE, "Could not read TPMS valves:" + message.getError());
            return;
        }

        // process the frame by going through all the containing fields
        // setting their values and notifying all listeners (there should be none)
        message.onMessageCompleteEvent();

        // now process all fields in the frame. Select only the ones we are interested in
        for (Field field : frame.getAllFields()) {
            switch (field.getFrom()) {
                case 24:
                    idFrontLeft = (int) field.getValue();
                    break;
                case 48:
                    idFrontRight = (int) field.getValue();
                    break;
                case 72:
                    idRearLeft = (int) field.getValue();
                    break;
                case 96:
                    idRearRight = (int) field.getValue();
                    break;
            }
        }

        // display the fetched values
        displayId(R.id.text_TireFLId, idFrontLeft);
        displayId(R.id.text_TireFRId, idFrontRight);
        displayId(R.id.text_TireRLId, idRearLeft);
        displayId(R.id.text_TireRRId, idRearRight);
        MainActivity.toast(MainActivity.TOAST_NONE, "TPMS valves read");
    }

    private int simpleIntParse(int fieldId) {
        EditText et = findViewById(fieldId);
        if (et != null) {
            try {
                return Integer.parseInt(et.getText().toString(), 16);
            } catch (Exception e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private void buttonWrite() {
        int[] ids = new int[4];
        ids[0] = simpleIntParse(R.id.text_TireFLId);
        ids[1] = simpleIntParse(R.id.text_TireFRId);
        ids[2] = simpleIntParse(R.id.text_TireRRId);
        ids[3] = simpleIntParse(R.id.text_TireRLId);

        if (ids[0] == -1 || ids[1] == -1 || ids[2] == -1 || ids[3] == -1) {
            MainActivity.toast(MainActivity.TOAST_NONE, "Those are not all valid hex values");
            return;
        }
        if (ids[0] == 0 && ids[1] == 0 && ids[2] == 0 && ids[3] == 0) {
            MainActivity.toast(MainActivity.TOAST_NONE, "All values are 0");
            return;
        }

        for (int i = 0; i < ids.length; i++) {
            if (ids[i]!= 0) {
                Frame frame = new Frame(ecuFromId, 0, ecu, String.format("7b5e%02x%06x", i, ids[i]), null);
                if (MainActivity.device == null)
                    return; // this should not happen as the fragment checks the device property, but it does
                Message message = MainActivity.device.injectRequest(frame);
                if (message == null) {
                    MainActivity.toast(MainActivity.TOAST_NONE, "Could not write TPMS valve " + i);
                } else if (message.isError()) {
                    MainActivity.toast(MainActivity.TOAST_NONE, "Could not write TPMS valve " + i + ":" + message.getError());
                } else if (!message.getData().startsWith("7b5e")) {
                    MainActivity.toast(MainActivity.TOAST_NONE, "Could not write TPMS valve " + i + ":" + message.getData());
                } else {
                    MainActivity.toast(MainActivity.TOAST_NONE, "Wrote TPMS valve " + i);
                }
                try {
                    Thread.sleep(250);
                } catch (Exception e) {
                    // ignore a sleep exception
                }
            }
        }

        MainActivity.toast(MainActivity.TOAST_NONE, "TPMS valves written. Read again to verify");
    }
}