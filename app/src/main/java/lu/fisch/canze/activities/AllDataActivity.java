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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import lu.fisch.canze.bluetooth.BluetoothManager;

import static lu.fisch.canze.activities.MainActivity.debug;


public class AllDataActivity extends CanzeActivity {

    // queries all known fields for


    private TextView textView;
    private BufferedWriter bufferedDumpWriter = null;
    private boolean dumpInProgress = false;
    private StringBuffer resultbuffer;
    private int bufferedLines = 0;

    private StoppableThread queryThread;
    private long ticker = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alldata);

        textView = findViewById(R.id.textResult);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for (Ecu ecu : Ecus.getInstance().getAllEcus()) {
            if (ecu.getFromId() > 0 && ecu.getFromId() != 0x800 && ecu.getFromId() != 0x801 &&
                    (!BuildConfig.BRANCH.equals("master") || (
                            !ecu.getAliases().contains("AIRBAG") &&
                                    !ecu.getAliases().contains("ESC"))
                    ))
            {
                arrayAdapter.add(ecu.getMnemonic()); // all reachable ECU's plus the Free Fields Computer. We skip the Virtual Fields Computer for now as it requires real fields and thus frames.
            }
        }

        // display the list
        final Spinner spinnerEcu = findViewById(R.id.ecuList);
        spinnerEcu.setAdapter(arrayAdapter);

        final Button btnDiag = findViewById(R.id.allData);
        btnDiag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String mne = String.valueOf(spinnerEcu.getSelectedItem());
                Ecu ecu = Ecus.getInstance().getByMnemonic(mne);
                if (ecu != null) {
                    dogetAllData(ecu);
                } else {
                    appendResult("Can't find ECU:" + mne);
                }
            }
        });

        setDoRestartQueueOnResume(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                displayProgressSpinner(true, R.id.progressBar_cyclic);
                //appendResult(R.string.message_PollerStopping);
                if (MainActivity.device != null) {
                    // stop the poller thread
                    MainActivity.device.stopAndJoin();
                }

                if (!BluetoothManager.getInstance().isConnected()) {
                    appendResult(R.string.message_NoConnection);
                    //} else {
                    //    appendResult(MainActivity.getStringSingle(R.string.message_Ready));
                }
                displayProgressSpinner(false, R.id.progressBar_cyclic);
            }
        }).start();
    }

    protected void initListeners() {
    }

    private void testerKeepalive() {
        ticker = Calendar.getInstance().getTimeInMillis();
    }

    private void testerKeepalive(Ecu ecu) {
        if (!ecu.getSessionRequired() && !MainActivity.isPh2()) return; // quit ticker if no gateway and no session
        if (Calendar.getInstance().getTimeInMillis() < ticker) return; // then, quit if no timeout
        if (MainActivity.isPh2()) {
            // open the gateway
            MainActivity.device.requestFrame(Frames.getInstance().getById(0x18daf1d2, "5003"));
        }
        //MainActivity.device.requestFrame(Frames.getInstance().getById(ecu.getFromId(), "7e01"));
        MainActivity.device.requestFrame(Frames.getInstance().getById(ecu.getFromId(), ecu.getStartDiag()));
        ticker = ticker + 3000;
    }

    private boolean testerInit(Ecu ecu) {
        if (!ecu.getSessionRequired()) return true;
        String filter = Integer.toHexString(ecu.getFromId());
        // we are a testerInit
        appendResult(MainActivity.getStringSingle(R.string.message_StartTestSession) + " (testerInit)");
        //field = Fields.getInstance().getBySID(filter + ".7e01.0");
        Field field = Fields.getInstance().getBySID(filter + "." + ecu.getStartDiag() + ".0");
        if (field != null) {
            // query the Field
            Message message = MainActivity.device.requestFrame(field.getFrame());
            if (!message.isError()) {
                String backRes = message.getData();
                // check the response
                if (backRes.toLowerCase().startsWith(ecu.getStartDiag())) {
                    testerKeepalive(); // start the keepalive timer
                    return true;
                } else {
                    appendResult("Start Diag Session, unexpected result [" + backRes + "]");
                }
            } else {
                appendResult("Start Diag Session, error result [" + message.getError() + "]");
            }
        } else {
            appendResult(R.string.message_NoTestSessionField);
        }
        return false;
    }


    private void dogetAllData(final Ecu ecu) {

        // try to stop previous thread
        if (queryThread != null) {
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

                // Stop responding to rotation
                freezeOrientation();
                // clear the screen
                clearResult();
                displayProgressSpinner(true, R.id.progressBar_cyclic);
                appendResult("Query " + ecu.getName() + " (renault ID:" + ecu.getRenaultId() + ")");

                // here initialize this particular ECU diagnostics fields
                try {
                    Frames.getInstance().load(ecu);
                    Fields.getInstance().load( MainActivity.getAssetPrefix() + ecu.getMnemonic() + "_Fields.csv");
                } catch (Exception e) {
                    appendResult(R.string.message_NoEcuDefinition);
                    // Reload the default frame & timings
                    Frames.getInstance().load();
                    Fields.getInstance().load();
                    // Don't care about DTC's and tests
                    return;
                }

                // re-initialize the device
                appendResult(R.string.message_SendingInit);

                // re-initialize the device
                if (!MainActivity.device.initDevice(1)) {
                    appendResult(R.string.message_InitFailed);
                    displayProgressSpinner(false, R.id.progressBar_cyclic);
                    return;
                }

                createDump(ecu);
                testerInit(ecu);

                // for (Frame frame : Frames.getInstance().getAllFrames()) {    <<< this is not thread-safe!
                for (int i = 0; i < Frames.getInstance().getAllFrames().size(); i++) {
                    // see if we need to stop right now
                    if (((StoppableThread) Thread.currentThread()).isStopped()) return;

                    Frame frame = Frames.getInstance().get(i);
                    if (frame.getResponseId() != null && !frame.getResponseId().startsWith("5")) { // ship dtc commands and mode controls

                        testerKeepalive(ecu); // may need to set a keepalive/session

                        if (frame.getContainingFrame() != null || ecu.getFromId() == 0x801) { // only use subframes and free frames

                            // query the Frame
                            Message message = MainActivity.device.requestFrame(frame);
                            if (!message.isError()) { // isError is also true when a 7f is returned
                                // process the frame by going through all the contained fields
                                // setting their values and notifying all listeners (there should be none)
                                message.onMessageCompleteEvent();
                                // output all the contained fields
                                for (Field field : frame.getAllFields()) {
                                    appendResultBuffered(field.getDebugValue());
                                }

                            } else {
                                // for readability output all contained fields, but instead of the field value state the frame error
                                for (Field field : frame.getAllFields()) {
                                    appendResultBuffered(field.getDebugValue(message.getError()));
                                }
                                if (!message.isError7f()) { // it makes no sense to re-initialize the device if a 7f is returned
                                    if (!MainActivity.device.initDevice(1)) {
                                        appendResultBuffered(MainActivity.getStringSingle(R.string.message_InitFailed));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                appendResultBuffered(""); // empty buffer
                closeDump();
                if (!isFinishing())
                    MainActivity.toast(MainActivity.TOAST_NONE, R.string.message_DumpDone);
                displayProgressSpinner(false, R.id.progressBar_cyclic);
                // allow rotation again
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            }
        });
        queryThread.start();
    }

    // Ensure all UI updates are done on the UiThread
    private void clearResult() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText("");
            }
        });
    }

    private void appendResultBuffered (String str) {
        // optimize speed and avoid ANRs by reducing UI work
        if (dumpInProgress) log(str);
        if (bufferedLines-- == 0 || str.isEmpty()) {
            if (resultbuffer != null) { // empty buffer
                appendResult(resultbuffer.toString());
            }
            resultbuffer = new StringBuffer();
            bufferedLines = 100;
        }
        resultbuffer.append(str).append("\n");
    }

    private void appendResult(String str) {
        if (dumpInProgress) log(str);
        final String localStr = str;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(localStr);
            }
        });
    }

    private void appendResult(int strResource) {
        final String localStr = MainActivity.getStringSingle(strResource);
        appendResult(localStr);
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

    private void createDump(Ecu ecu) {

        dumpInProgress = false;

        if (!MainActivity.storageIsAvailable) {
            debug("AllDataActivity.createDump: SDcard not available");
            return;
        }

        // ensure that there is a CanZE Folder in SDcard
        if (!MainActivity.getInstance().isExternalStorageWritable()) {
            debug("AllDataActivity.createDump: SDcard not writeable");
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
        String exportdataFileName = file_path + ecu.getMnemonic() + "-" + sdf.format(Calendar.getInstance().getTime()) + ".txt";

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
            if (!isFinishing())
                MainActivity.toast(MainActivity.TOAST_NONE, MainActivity.getStringSingle(R.string.format_DumpWriting), exportdataFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
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


    private void displayProgressSpinner(final boolean on, final int id) {
        // remove progress spinners
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pb = findViewById(id);
                if (pb != null) pb.setVisibility(on ? View.VISIBLE : View.GONE);
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
        if (queryThread != null)
            if (queryThread.isAlive()) {
                queryThread.tryToStop();
                try {
                    queryThread.join();
                } catch (Exception e) {
                    MainActivity.debug(e.getMessage());
                }
            }

        closeDump();

        // Reload the frame & timings
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

}
