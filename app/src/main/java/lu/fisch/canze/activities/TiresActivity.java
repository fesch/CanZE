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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import lu.fisch.canze.R;
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.actors.Ecu;
import lu.fisch.canze.actors.Ecus;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Frames;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

import static lu.fisch.canze.activities.MainActivity.debug;


public class TiresActivity extends CanzeActivity implements FieldListener, DebugListener {
    
    private static final String[] val_TireSpdPresMisadaption = {MainActivity.getStringSingle(R.string.default_Ok), MainActivity.getStringSingle(R.string.default_NotOk)};
    private static final String[] val_TireState = MainActivity.getStringList(R.array.list_TireStatus);
    private static final String val_Unavailable = MainActivity.getStringSingle(R.string.default_Dash);
    @ColorInt
    private int baseColor;
    @ColorInt private int alarmColor;
    static int previousState = -2; // uninitialized
    private final Ecu ecu = Ecus.getInstance().getByMnemonic("BCM");
    private final int ecuFromId = (ecu == null) ? 0 : ecu.getFromId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tires);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorButtonNormal, typedValue, true);
        baseColor = typedValue.data;
        boolean dark = ((baseColor & 0xff0000) <= 0xa00000);
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

        button = findViewById(R.id.button_TiresLoadA);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load ("A");
            }
        });

        button = findViewById(R.id.button_TiresSaveA);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save("A");
            }
        });

        button = findViewById(R.id.button_TiresLoadB);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load ("B");
            }
        });

        button = findViewById(R.id.button_TiresSaveB);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save("B");
            }
        });

        button = findViewById(R.id.button_TiresSwap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSwap();
            }
        });

        tpmsState(-1); // initialize to "no TPMS, but don't Toast that". This ensures disabled fields, also after a rotate
    }

    // set the fields the poller should query
    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(Sid.TpmsState, 1000);
        addField(Sid.TireSpdPresMisadaption, 6000);
        addField(Sid.TireFLState, 6000);
        addField(Sid.TireFLPressure, 6000);
        addField(Sid.TireFRState, 6000);
        addField(Sid.TireFRPressure, 6000);
        addField(Sid.TireRLState, 6000);
        addField(Sid.TireRLPressure, 6000);
        addField(Sid.TireRRState, 6000);
        addField(Sid.TireRRPressure, 6000);
    }

    // fired event when any of the registered fields are getting updated by the device
    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Double.isNaN(field.getValue())) return;
                String fieldId = field.getSID();
                TextView tv = null;
                String value = "";
                int intValue = (int) field.getValue();
                int color = baseColor;

                // get the text field
                switch (fieldId) {
                    case Sid.TpmsState:
                        tpmsState (intValue);
                        tv = null;
                        break;
                    case Sid.TireSpdPresMisadaption:
                        tv = findViewById(R.id.text_TireSpdPresMisadaption);
                        color = 0; // don't set color
                        value = val_TireSpdPresMisadaption[intValue];
                        break;
                    case Sid.TireFLState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TireFLState);
                        if (intValue > 1) color = alarmColor;
                        value = val_TireState != null ? val_TireState[intValue] : "";
                        break;
                    case Sid.TireFLPressure:
                        tv = findViewById(R.id.text_TireFLPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case Sid.TireFRState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TireFRState);
                        if (intValue > 1) color = alarmColor;
                        value = val_TireState != null ? val_TireState[intValue] : "";
                        break;
                    case Sid.TireFRPressure:
                        tv = findViewById(R.id.text_TireFRPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case Sid.TireRLState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TireRLState);
                        if (intValue > 1) color = alarmColor;
                        value = val_TireState != null ? val_TireState[intValue] : "";
                        break;
                    case Sid.TireRLPressure:
                        tv = findViewById(R.id.text_TireRLPressure);
                        value = (intValue >= 3499) ? val_Unavailable : ("" + intValue);
                        break;
                    case Sid.TireRRState:
                        if (intValue < 0 || intValue > 6) return;
                        tv = findViewById(R.id.text_TireRRState);
                        if (intValue > 1) color = alarmColor;
                        value = val_TireState != null ? val_TireState[intValue] : "";
                        break;
                    case Sid.TireRRPressure:
                        tv = findViewById(R.id.text_TireRRPressure);
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

    private void tpmsState (int state) {
        if (state == previousState || ecu == null || ecuFromId == 0 || MainActivity.device == null) return;
        previousState = state;
        boolean isEnabled = (state == 1);
        EditText edittext;
        edittext = findViewById(R.id.text_TireFLId);
        edittext.setEnabled(isEnabled);
        edittext = findViewById(R.id.text_TireFRId);
        edittext.setEnabled(isEnabled);
        edittext = findViewById(R.id.text_TireRLId);
        edittext.setEnabled(isEnabled);
        edittext = findViewById(R.id.text_TireRRId);
        edittext.setEnabled(isEnabled);
        Button button;
        button = findViewById(R.id.button_TiresRead);
        button.setEnabled(isEnabled);
        button = findViewById(R.id.button_TiresWrite);
        button.setEnabled(isEnabled);
        button = findViewById(R.id.button_TiresSaveA);
        button.setEnabled(isEnabled);
        button = findViewById(R.id.button_TiresLoadA);
        button.setEnabled(isEnabled);
        button = findViewById(R.id.button_TiresSaveB);
        button.setEnabled(isEnabled);
        button = findViewById(R.id.button_TiresLoadB);
        button.setEnabled(isEnabled);
        button = findViewById(R.id.button_TiresSwap);
        button.setEnabled(isEnabled);
        // do not use !enabled as a rotate will reinitialize the activity, setting state to -1
        if (state == 0) {
            if (!isFinishing())
                MainActivity.toast(MainActivity.TOAST_NONE, "Your car has no TPMS system");
        } else if (state == 1) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    buttonRead();
                }
            })).start();
        }
    }

    private boolean ensureFolderThere () {
        if (!MainActivity.storageIsAvailable) {
            debug("AllDataActivity.createDump: SDcard not available");
            return false;
        }
        if (!MainActivity.getInstance().isExternalStorageWritable()) {
            debug("DiagDump: SDcard not writeable");
            return false;
        }
        String file_path = MainActivity.getInstance().getExternalFolder();

        File dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return true;
    }

    private void load (String set) {
        String file_path = MainActivity.getInstance().getExternalFolder();
        String filename = file_path + "_Tires" + set + ".csv";
        ensureFolderThere();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            String line = bufferedReader.readLine();
            bufferedReader.close();
            String[] idsRead = line.split(",");
            displayId(R.id.text_TireFLId, idsRead[0]);
            displayId(R.id.text_TireFRId, idsRead[1]);
            displayId(R.id.text_TireRRId, idsRead[2]);
            displayId(R.id.text_TireRLId, idsRead[3]);
        } catch (IOException e ) {
            if (!isFinishing())
                MainActivity.toast(MainActivity.TOAST_NONE, "Can't access file " + filename);
        } catch (ArrayIndexOutOfBoundsException e ) {
            if (!isFinishing())
                MainActivity.toast(MainActivity.TOAST_NONE, "Wrong format in file " + filename);
        }
    }

    private void save (String set) {
        String file_path = MainActivity.getInstance().getExternalFolder();
        String filename = file_path + "_Tires" + set + ".csv";
        ensureFolderThere();
        try {
            String line = simpleStringParse(R.id.text_TireFLId) + "," +simpleStringParse(R.id.text_TireFRId) + "," +simpleStringParse(R.id.text_TireRRId) + "," +simpleStringParse(R.id.text_TireRLId) + "\n";
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, false));
            bufferedWriter.append(line);
            bufferedWriter.close();
        } catch (IOException e ) {
            if (!isFinishing())
                MainActivity.toast(MainActivity.TOAST_NONE, "Can't access file " + filename);
        }
    }

    private void displayId(final int fieldId, final int val) {
        displayId (fieldId, String.format("%06X", val));
    }

    private void displayId(final int fieldId, final String val) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText et = findViewById(fieldId);
                if (et != null)
                    et.setText(val);
            }
        });
    }

    private void readTpms (int[] idsRead) {
        for (int i = 0; i < idsRead.length; i++) {
            idsRead[i] = 0;
        }
        if (ecu == null || ecuFromId == 0) return;
        Frame frame = Frames.getInstance().getById(ecuFromId, "6171"); // get TPMS ids
        if (frame == null) {
            MainActivity.debug("frame does not exist:" + ecu.getHexFromId() + ".6171");
            return;
        }
        if (MainActivity.device == null)
            return; // this should not happen as the fragment checks the device property, but it does
        Message message = MainActivity.device.injectRequest(frame); // return result message of last request (get TPMS ids)
        if (message == null) {
            if (!isFinishing())
                MainActivity.toast(MainActivity.TOAST_NONE, "Could not read TPMS sensors");
            return;
        }
        if (message.isError()) {
            if (!isFinishing())
                MainActivity.toast(MainActivity.TOAST_NONE, "Could not read TPMS sensors:" + message.getError());
            return;
        }

        // process the frame by going through all the containing fields
        // setting their values and notifying all listeners (there should be none)
        message.onMessageCompleteEvent();

        // now process all fields in the frame. Select only the ones we are interested in
        for (Field field : frame.getAllFields()) {
            switch (field.getFrom()) {
                case 24: // CodeIdentite(1)_(0) --> Ident code, left front wheel (Wheel 1, set 0)
                    idsRead[0] = (int) field.getValue();
                    break;
                case 48: // CodeIdentite(2)_(0) --> Ident code, right front wheel (Wheel 2, set 0)
                    idsRead[1] = (int) field.getValue();
                    break;
                case 72: // CodeIdentite(3)_(0) --> Ident code, right rear wheel (Wheel 3, set 0)
                    idsRead[2] = (int) field.getValue();
                    break;
                case 96: // CodeIdentite(4)_(0) --> IIdent code, left rear wheel (Wheel 4, set 0)
                    idsRead[3] = (int) field.getValue();
                    break;
            }
        }
        bit23on(idsRead);
    }


    private void buttonRead() {
        int[] idsRead = new int[4];
        readTpms(idsRead);

        if (!isFinishing()) {
            // display the fetched values
            displayId(R.id.text_TireFLId, idsRead[0]);
            displayId(R.id.text_TireFRId, idsRead[1]);
            displayId(R.id.text_TireRRId, idsRead[2]);
            displayId(R.id.text_TireRLId, idsRead[3]);
            MainActivity.toast(MainActivity.TOAST_NONE, "TPMS sensors read");
        }
    }

    private int simpleIntParse(int fieldId) {
        try {
            return Integer.parseInt(simpleStringParse(fieldId), 16);
        } catch (Exception e) {
            return -1;
        }
    }

    private String simpleStringParse(int fieldId) {
        EditText et = findViewById(fieldId);
        if (et != null) {
            return et.getText().toString();
        } else {
            return "-1";
        }
    }



    private boolean compareTpms (int[] idsRead, int[] idsWrite) {
        for (int i = 0; i < idsRead.length; i++) {
            if (idsRead[i] == 0) return false;
            if (idsWrite[i] == 0) return false;
            if (idsRead[i] != idsWrite[i]) return false;
        }
        return true;
    }

    private void buttonWrite() {
        int[] idsWrite = new int[4];
        int[] idsRead  = new int[4];

        idsWrite[0] = simpleIntParse(R.id.text_TireFLId); // front left / AVG
        idsWrite[1] = simpleIntParse(R.id.text_TireFRId); // front right / AVD
        idsWrite[2] = simpleIntParse(R.id.text_TireRRId); // back right / ARD
        idsWrite[3] = simpleIntParse(R.id.text_TireRLId); // back left / ARG

        bit23on(idsWrite);

        if (ecu == null || ecuFromId == 0) return;
        if (idsWrite[0] == -1 || idsWrite[1] == -1 || idsWrite[2] == -1 || idsWrite[3] == -1) {
            MainActivity.toast(MainActivity.TOAST_NONE, "Those are not all valid hex values");
            return;
        }
        if (idsWrite[0] == 0 && idsWrite[1] == 0 && idsWrite[2] == 0 && idsWrite[3] == 0) {
            MainActivity.toast(MainActivity.TOAST_NONE, "All values are 0");
            return;
        }

        for (int retries = 0; retries < 3; retries++) {
            // write the values
            for (int i = 0; i < idsWrite.length; i++) {
                if (idsWrite[i] != 0) {
                    Frame frame = new Frame(ecuFromId, 0, ecu, String.format("7b5e%02x%06x", i + 1, idsWrite[i]), null);
                    if (MainActivity.device == null)
                        return; // this should not happen as the fragment checks the device property, but it does
                    Message message = MainActivity.device.injectRequest(frame);
                    if (message == null) {
                        if (!isFinishing())
                            MainActivity.toast(MainActivity.TOAST_NONE, "Could not write TPMS valve " + i);
                    } else if (message.isError()) {
                        if (!isFinishing())
                            MainActivity.toast(MainActivity.TOAST_NONE, "Could not write TPMS valve " + i + ":" + message.getError());
                    } else if (!message.getData().startsWith("7b5e")) {
                        if (!isFinishing())
                            MainActivity.toast(MainActivity.TOAST_NONE, "Could not write TPMS valve " + i + ":" + message.getData());
                    } else {
                        if (!isFinishing())
                            MainActivity.toast(MainActivity.TOAST_NONE, "Wrote TPMS valve " + i);
                    }
                    try {
                        Thread.sleep(250);
                    } catch (Exception e) {
                        // ignore a sleep exception
                    }
                }
            }

            // now read the values
            readTpms(idsRead);
            if (compareTpms(idsRead, idsWrite)) {
                if (!isFinishing())
                    MainActivity.toast(MainActivity.TOAST_NONE, "TPMS sensors written. Read again to verify");
                return;
            }
            try {
                Thread.sleep(250);
            } catch (Exception e) {
                // ignore a sleep exception
            }
        }
        if (!isFinishing())
            MainActivity.toast(MainActivity.TOAST_NONE, "Failed to write all TPMS sensors");
    }

    private void buttonSwap() {
        int[] ids = new int[4];

        ids[0] = simpleIntParse(R.id.text_TireFLId); // front left / AVG
        ids[1] = simpleIntParse(R.id.text_TireFRId); // front right / AVD
        ids[2] = simpleIntParse(R.id.text_TireRRId); // back right / ARD
        ids[3] = simpleIntParse(R.id.text_TireRLId); // back left / ARG

        bit23on(ids);

        displayId(R.id.text_TireFLId, ids[3]);
        displayId(R.id.text_TireFRId, ids[2]);
        displayId(R.id.text_TireRRId, ids[1]);
        displayId(R.id.text_TireRLId, ids[0]);
    }

    private void bit23on(int[] ids) {
        for (int i = 0; i < ids.length; i++) {
            ids[i] = 0x800000 | (ids[i] & 0x7fffff);
        }
    }
}