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

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import lu.fisch.canze.BuildConfig;
import lu.fisch.canze.R;
import lu.fisch.canze.actors.Ecu;
import lu.fisch.canze.actors.Ecus;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Frames;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.actors.StoppableThread;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

import static lu.fisch.canze.activities.MainActivity.debug;

// Jeroen

public class TwingoTestActivity extends CanzeActivity implements FieldListener, DebugListener {

    // displays firmware version of an eCU, or
    // writes firmware version of all ECUs to a CSv

    private StoppableThread queryThread;
    private BufferedWriter bufferedDumpWriter = null;
    private boolean dumpInProgress = false;
    private long ticker = 0;

    @SuppressLint("StringFormatMatches")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware);

        final Button btnCsvSave = findViewById(R.id.csvFirmware);
        btnCsvSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doGetAllFirmware();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.device != null) {
                    // stop the poller thread
                    // note tht for the ZE50, the gateway is now not triggered
                    MainActivity.device.stopAndJoin();
                }
            }
        }).start();
    }


    private void doGetAllFirmware(){
        // try to stop previous thread
        if(queryThread!=null) {
            if (queryThread.isAlive()) {
                queryThread.tryToStop();
                try {
                    queryThread.join();
                } catch (Exception e) {
                    MainActivity.debug(e.getMessage());
                }
            }
        }

        queryThread = new StoppableThread(new Runnable() {
            @Override
            public void run() {
                freezeOrientation();
                Frame frame;

                displayProgress(true, R.id.progressBar_cyclic3, R.id.csvFirmware);
                // re-initialize the device
                if (!MainActivity.device.initDevice(1)) {
                    return;
                }

                Ecus.getInstance().load("Twingo_3_Ph2/_Ecus.csv");
                Frames.getInstance().load("Twingo_3_Ph2/_Frames.csv");
                Fields.getInstance().load("Twingo_3_Ph2/_Fields.csv");

                createDump();
                ticker = Calendar.getInstance().getTimeInMillis();
                log("ECU, Version Type, Version data");

                if (MainActivity.isPh2()) {
                    frame = getFrame(0x18daf1d2, "5003"); // open the gateway, as the poller is stopped
                    queryFrame(frame);
                }

                for (Ecu ecu : Ecus.getInstance().getAllEcus()) {
                    // see if we need to stop right now
                    if (((StoppableThread) Thread.currentThread()).isStopped()) return;
                    if (ecu.getFromId() > 0 && (ecu.getFromId() < 0x800 || ecu.getFromId() >= 0x900)) {
                        keepAlive();
                        if (ecu.getSessionRequired()) {
                            frame = getFrame(ecu.getFromId(), ecu.getStartDiag()); // open the ecu, as the poller is stopped
                            queryFrame(frame);
                        }
                        processOneEcu(ecu, 0, 0, 0, 0);
                     }
                }
                closeDump();
                displayProgress(false, R.id.progressBar_cyclic3, R.id.csvFirmware);
            }
        });
        queryThread.start();
    }

    private void processOneEcu (Ecu ecu, int idDiag, int idSupplier, int idSoft, int idVersion) {
        // query the Frame
        Frame frame = getFrame(ecu.getFromId(), "6180");
        if (frame != null) {
            frame = queryFrame(frame);
            if (frame != null) {

                for (Field field : frame.getAllFields()) {
                    switch (field.getFrom()) {
                        case 56:
                            setSoftwareValue(idDiag, field, ecu.getMnemonic() + ", diagVersion");
                            break;
                        case 64:
                            setSoftwareValue(idSupplier, field, ecu.getMnemonic() + ", supplier");
                            break;
                        case 128:
                            setSoftwareValue(idSoft, field, ecu.getMnemonic() + ", soft");
                            break;
                        case 144:
                            setSoftwareValue(idVersion, field, ecu.getMnemonic() + ", version");
                            break;
                    }
                }
            }
            return;
        }

        // else 2nd approach
        frame = getFrame(ecu.getFromId(), "62f1a0");
        if (frame != null) {
            frame = queryFrame(frame);
            if (frame != null) {
                Field field = frame.getAllFields().get(0);
                setSoftwareValue(idDiag, field, ecu.getMnemonic() + ", diagVersion");
            }
        }
        frame = getFrame(ecu.getFromId(), "62f18a");
        if (frame != null) {
            frame = queryFrame(frame);
            if (frame != null) {
                Field field = frame.getAllFields().get(0);
                setSoftwareValue(idSupplier, field, ecu.getMnemonic() + ", supplier");
            }
        }
        frame = getFrame(ecu.getFromId(), "62f194");
        if (frame != null) {
            frame = queryFrame(frame);
            if (frame != null) {
                Field field = frame.getAllFields().get(0);
                setSoftwareValue(idSoft, field, ecu.getMnemonic() + ", soft:");
            }
        }
        frame = getFrame(ecu.getFromId(), "62f195");
        if (frame != null) {
            frame = queryFrame(frame);
            if (frame != null) {
                Field field = frame.getAllFields().get(0);
                setSoftwareValue(idVersion, field, ecu.getMnemonic() + ", version:");
            }
        }

    }

    private Frame getFrame (int fromId, String responseId) {
        Frame frame = Frames.getInstance().getById(fromId, responseId);
        if (frame == null) {
            MainActivity.getInstance().dropDebugMessage(String.format(Locale.getDefault(), "Frame for this ECU %X.%s not found", fromId, responseId));
            return null;
        }
        MainActivity.getInstance().dropDebugMessage(frame.getFromIdHex() + "." + frame.getResponseId());
        return frame;
    }

    private Frame queryFrame (Frame frame) {
        if (frame == null) return null;
        Message message = MainActivity.device.requestFrame(frame); //  field.getFrame());
        if (message.isError()) {
            MainActivity.getInstance().dropDebugMessage(message.getError());
            return null;
        }

        MainActivity.debug("msg:" + message.getData());

        message.onMessageCompleteEvent(); // set the value of all fields in the frame of this message
        return frame;
    }

    private void setSoftwareValue(final int id, final Field field, final String label) {
        final String toDisplay;
        if (field == null) {
            toDisplay = "";
        } else if (field.isString()) {
            toDisplay = label + ":" + field.getStringValue();
        } else if ((field.getTo() - field.getFrom()) < 8) {
            toDisplay = label + String.format(Locale.getDefault(), ":%02X", (int)field.getValue());
        } else {
            toDisplay = label + String.format(Locale.getDefault(), ":%04X", (int)field.getValue());
        }

        if (id == 0) { //log
            log (toDisplay);
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = findViewById(id);
                if (tv != null) {
                    tv.setText(toDisplay);
                }
            }
        });
    }

    private void log(String text) {
        if (!dumpInProgress) return;
        try {
            bufferedDumpWriter.append(text);
            bufferedDumpWriter.append(System.getProperty("line.separator"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDump() {
        dumpInProgress = false;

        if (!MainActivity.storageIsAvailable) {
            debug("FirmwareActivity.createDump: SDcard not available");
            return;
        }

        // ensure that there is a CanZE Folder in SDcard
        if (!MainActivity.getInstance().isExternalStorageWritable()) {
            debug("FirmwareActivity.createDump: SDcard not writeable");
            return;
        }

        String file_path = MainActivity.getInstance().getExternalFolder();
        File dir = new File(file_path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                debug("DiagDump: Can't create directory:" + file_path);
                return;
            }
        }
        debug("DiagDump: file_path:" + file_path);

        SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHMS), Locale.getDefault());
        String exportdataFileName = file_path + "Firmwares-" + sdf.format(Calendar.getInstance().getTime()) + ".csv";

        File logFile = new File(exportdataFileName);
        if (!logFile.exists()) {
            try {
                if (!logFile.createNewFile()) {
                    debug("DiagDump: Can't create file:" + exportdataFileName);
                    return;
                }
                debug("DiagDump: NewFile:" + exportdataFileName);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            //BufferedWriter for performance, true to set append to file flag
            bufferedDumpWriter = new BufferedWriter(new FileWriter(logFile, true));
            dumpInProgress = true;
            MainActivity.toast(MainActivity.TOAST_NONE, MainActivity.getStringSingle(R.string.format_DumpWriting), exportdataFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void keepAlive() {
        if (!MainActivity.isPh2()) return; // quit ticker if no gateway and no session
        if (Calendar.getInstance().getTimeInMillis() < ticker) return; // then, quit if no timeout
        if (MainActivity.isPh2()) {
            // open the gateway
            MainActivity.device.requestFrame(Frames.getInstance().getById(0x18daf1d2, "5003"));
        }
        ticker = ticker + 3000;
    }

    private void closeDump() {
        try {
            if (dumpInProgress) {
                bufferedDumpWriter.close();
                if (!isFinishing())
                    MainActivity.toast(MainActivity.TOAST_NONE, "Done.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // UI elements

    private void displayProgress(final boolean on, final int id_spinner, final int id_button) {
        // remove progress spinners
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pb = findViewById(id_spinner);
                if (pb != null){ pb.setVisibility(on ? View.VISIBLE : View.GONE);}
                Button btn = findViewById(id_button);
                if (btn != null){btn.setEnabled(!on);}
            }
        });
    }

    @SuppressLint("SourceLockedOrientationActivity")
    // this is done to avoid restarting the long running activity
    private void freezeOrientation () {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (rotation == android.view.Surface.ROTATION_90 || rotation == android.view.Surface.ROTATION_180) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                if (rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {

        // stop the query thread if still running
        if(queryThread!=null)
            if(queryThread.isAlive()) {
                queryThread.tryToStop();
                try {
                    queryThread.join();
                }
                catch(Exception e)
                {
                    MainActivity.debug(e.getMessage());
                }
            }

        closeDump();

        // Reload the frame & timings
        Ecus.getInstance().load();
        Frames.getInstance().load();
        Fields.getInstance().load();

        // restart the poller
        if (MainActivity.device != null) {
            MainActivity.device.initConnection();
            // register application wide fields
            MainActivity.getInstance().registerApplicationFields();
        }

        super.onDestroy();
    }

    protected void initListeners () {
        MainActivity.getInstance().setDebugListener(this);
    }
    @Override
    public void onFieldUpdateEvent(final Field field) {
        // do nothing, no fields will ne updated
    }
}
