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

import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Dtcs;
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


public class DtcActivity extends CanzeActivity {

    private TextView textView;
    private BufferedWriter bufferedDumpWriter = null;
    private boolean dumpInProgress = false;

    private StoppableThread queryThread;
    private long ticker = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtc);

        textView = findViewById(R.id.textResult);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for (Ecu ecu : Ecus.getInstance().getAllEcus()) {
            if (ecu.getFromId() != 0 && (ecu.getFromId() < 0x800 || ecu.getFromId() >= 0x900)) {
                arrayAdapter.add(ecu.getMnemonic()); // only list real, known, reachable ECUs
            }
        }
        // display the list
        final Spinner spinnerEcu = findViewById(R.id.ecuList);
        spinnerEcu.setAdapter(arrayAdapter);

        final Button btnQuery = findViewById(R.id.ecuQuery);
        btnQuery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doQueryEcu(Ecus.getInstance().getByMnemonic(String.valueOf(spinnerEcu.getSelectedItem())));
            }
        });

        final Button btnClear = findViewById(R.id.ecuClear);
        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doClearEcu(Ecus.getInstance().getByMnemonic(String.valueOf(spinnerEcu.getSelectedItem())));
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                appendResult(R.string.message_PollerStopping);

                if (MainActivity.device != null) {
                    // stop the poller thread
                    MainActivity.device.stopAndJoin();
                }

                if (!BluetoothManager.getInstance().isConnected()) {
                    appendResult(R.string.message_NoConnection);
                    return;
                }
                appendResult(MainActivity.getStringSingle(R.string.message_Ready));
            }
        }).start();
    }

    protected void initListeners() {
    }

    private void testerKeepalive() {
        ticker = Calendar.getInstance().getTimeInMillis();
    }
    private void testerKeepalive(Ecu ecu) {
        if (!ecu.getSessionRequired()) return;
        if (Calendar.getInstance().getTimeInMillis() < ticker) return;
        MainActivity.device.requestFrame(Frames.getInstance().getById(ecu.getFromId(), "7e01"));
        ticker = ticker + 1500;
    }

    private boolean testerInit(Ecu ecu) {
        if (!ecu.getSessionRequired()) return true;
        String filter = Integer.toHexString(ecu.getFromId());
        // we are a testerInit
        appendResult(MainActivity.getStringSingle(R.string.message_StartTestSession) + " (testerInit)\n");
        //field = Fields.getInstance().getBySID(filter + ".7e01.0");
        Field field = Fields.getInstance().getBySID(filter + ".50c0.0");
        if (field != null) {
            // query the Field
            Message message = MainActivity.device.requestFrame(field.getFrame());
            if (!message.isError()) {
                String backRes = message.getData();
                // check the response
                if (backRes.toLowerCase().startsWith("50c0")) {
                    testerKeepalive();
                    return true;
                } else {
                    appendResult("Start Diag Session, unexpected result [" + backRes + "]\n");
                }
            } else {
                appendResult("Start Diag Session, error result [" + message.getError() + "]\n");
            }
        } else {
            appendResult(R.string.message_NoTestSessionField);
        }
        return false;
    }

    private void doQueryEcu(final Ecu ecu) {

        clearResult();

        if (ecu == null) {
            appendResult("No ECU selected\n");
            return;
        }

        appendResult("Query " + ecu.getName() + " (renault ID:" + ecu.getRenaultId() + ")\n");

        // here initialize this particular ECU diagnostics fields
        try {
            //Object diagEcu = Class.forName("lu.fisch.canze.actors.EcuDiag" + ecu.getMnemonic()).newInstance();
            //java.lang.reflect.Method methodLoad;

            //methodLoad = diagEcu.getClass().getMethod("load");
            //methodLoad.invoke(diagEcu);
            Frames.getInstance().load (ecu);
            Fields.getInstance().load (ecu.getMnemonic() + "_Fields.csv");
            Dtcs.getInstance().load(ecu.getMnemonic() + "_Dtcs.csv", ecu.getMnemonic() + "_Tests.csv");
        } catch (Exception e) {
            appendResult(R.string.message_NoEcuDefinition);
            // Reload the default frame & timings
            Frames.getInstance().load();
            Fields.getInstance().load();
            // Don't care about DTC's and tests
        }

        // re-initialize the device
        appendResult(R.string.message_SendingInit);

        // try to stop previous thread
        if (queryThread != null)
            if (queryThread.isAlive()) {
                queryThread.tryToStop();
                try {
                    queryThread.join();
                } catch (Exception e) {
                    MainActivity.logExceptionToCrashlytics(e);
                    MainActivity.debug(e.getMessage());
                }
            }

        queryThread = new StoppableThread(new Runnable() {
            @Override
            public void run() {

                Field field;
                String filter;
                Message message;
                String backRes;

                // get the from ID from the selected ECU
                filter = Integer.toHexString(ecu.getFromId());


                if (!MainActivity.device.initDevice(1)) {
                    appendResult(R.string.message_InitFailed);
                    return;
                }

                // still trying desperately to get to the BCB!!!1
                if (!testerInit(ecu)) {
                    appendResult(R.string.message_InitFailed);
                    return;
                }

                boolean onePrinted = false;
                for (String getDtc : ecu.getGetDtcs().split(";")) {
                    testerKeepalive(ecu);
                    // compile the field query and get the Field object
                    appendResult(MainActivity.getStringSingle(R.string.message_GetDtcs) + getDtc + "]\n");
                    field = Fields.getInstance().getBySID(filter + "." + getDtc + ".0"); // get DTC
                    if (field != null) {
                        // query the Field
                        message = MainActivity.device.requestFrame(field.getFrame());
                        if (!message.isError()) {
                            // check the response
                            backRes = message.getData();
                            if (backRes.startsWith("59")) {

                                // loop trough all DTC's
                                // format of the message is
                                // blocks of 4 bytes
                                //   first 2 bytes is the DTC
                                //     first nibble of DTC is th P0 etc encoding, but we are nit using that
                                //   next byte is the test that triggered the DTC
                                //   next byte contains the flags
                                // All decoding is done in the Dtcs class
                                for (int i = 6; i < backRes.length() - 7; i += 8) {
                                    // see if we need to stop right now
                                    if (((StoppableThread) Thread.currentThread()).isStopped())
                                        return;

                                    int flags = Integer.parseInt(backRes.substring(i + 6, i + 8), 16);
                                    // exclude 50 / 10 as it means something like "I have this DTC code, but I have never tested it"
                                    if (flags != 0x50 && flags != 0x10) {
                                        onePrinted = true;
                                        appendResult(
                                                "\n*** DTC" + backRes.substring(i, i + 6) + " (" + Dtcs.getInstance().getDisplayCodeById(backRes.substring(i, i + 6)) + ") ***\n"
                                                        + Dtcs.getInstance().getDescriptionById(backRes.substring(i, i + 6))
                                                        + "\nFlags:" + Dtcs.getInstance().getFlagDescription(flags)
                                        );
                                    }
                                }

                            } else {
                                appendResult(MainActivity.getStringSingle(R.string.message_UnexpectedResult) + backRes + "]\n");
                            }

                        } else {
                            appendResult(MainActivity.getStringSingle(R.string.message_UnexpectedResult) + message.getError() + "]\n");
                        }

                    } else {
                        appendResult(R.string.message_NoGetDtcsField);
                    }


                }
                if (!onePrinted) appendResult(R.string.message_NoActiveDtcs);
            }

        });
        queryThread.start();
    }

    private void doClearEcu(final Ecu ecu) {

        clearResult();

        if (ecu == null) {
            appendResult("No ECU selected\n");
            return;
        }

        appendResult(MainActivity.getStringSingle(R.string.message_clear) + ecu.getName() + " (renault ID:" + ecu.getRenaultId() + ")\n");

        // here initialize this particular ECU diagnostics fields
        try {
            //Object diagEcu = Class.forName("lu.fisch.canze.actors.EcuDiag" + ecu.getMnemonic()).newInstance();
            //java.lang.reflect.Method methodLoad;

            //methodLoad = diagEcu.getClass().getMethod("load");
            //methodLoad.invoke(diagEcu);
            Frames.getInstance().load (ecu);
            Fields.getInstance().load (ecu.getMnemonic() + "_Fields.csv");
        } catch (Exception e) {
            appendResult(R.string.message_NoEcuDefinition);
            // Reload the default frame & timings
            Frames.getInstance().load();
            Fields.getInstance().load();
            // Don't care about DTC's and tests
        }

        // re-initialize the device
        appendResult(R.string.message_SendingInit);

        // try to stop previous thread
        if (queryThread != null)
            if (queryThread.isAlive()) {
                queryThread.tryToStop();
                try {
                    queryThread.join();
                } catch (Exception e) {
                    MainActivity.debug(e.getMessage());
                }
            }

        queryThread = new StoppableThread(new Runnable() {
            @Override
            public void run() {

                Field field;
                Frame frame;
                String filter;

                // get the from ID from the selected ECU
                filter = Integer.toHexString(ecu.getFromId());


                // compile the field query and get the Field object
                field = Fields.getInstance().getBySID(filter + ".54.0"); // get DTC Clear
                if (field == null) {
                    appendResult(R.string.message_NoClearDtcField);
                    return;
                }
                frame = field.getFrame();

                // re-initialize the device
                appendResult(R.string.message_SendingInit);
                if (!MainActivity.device.initDevice(1)) {
                    appendResult(R.string.message_InitFailed);
                    return;
                }

                testerInit(ecu);
                testerKeepalive();

                // query the Field
                Message message = MainActivity.device.requestFrame(frame);
                if (message.isError()) {
                    appendResult(message.getError());
                    return;
                }

                String backRes = message.getData();
                // check the response
                if (!backRes.startsWith("54")) {
                    appendResult(MainActivity.getStringSingle(R.string.message_UnexpectedResult) + backRes + "]\n");
                    return;
                }

                appendResult(R.string.message_ClearSuccessful);
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

    private void appendResult(final String str) {
        if (dumpInProgress) log(str);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(str);
            }
        });
    }

    private void appendResult(int strResource) {
        String localStr = MainActivity.getStringSingle(strResource);
        appendResult(localStr);
    }

    private void log(String text) {
        try {
            bufferedDumpWriter.append(text);
            bufferedDumpWriter.append(System.getProperty("line.separator"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDump(Ecu ecu) {

        dumpInProgress = false;
        SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHMS), Locale.getDefault());


        // ensure that there is a CanZE Folder in SDcard
        if (!MainActivity.getInstance().isExternalStorageWritable()) {
            debug("DiagDump: SDcard not writeable");
            return;
        }

        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CanZE/";
        File dir = new File(file_path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                debug("DiagDump: Can't create directory:" + file_path);
                return;
            }
        }
        debug("DiagDump: file_path:" + file_path);

        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
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
            }
        }

        try {
            //BufferedWriter for performance, true to set append to file flag
            bufferedDumpWriter = new BufferedWriter(new FileWriter(logFile, true));
            dumpInProgress = true;
            MainActivity.toast (MainActivity.TOAST_NONE, MainActivity.getStringSingle(R.string.format_DumpWriting), exportdataFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void closeDump() {
        try {
            if (dumpInProgress) {
                bufferedDumpWriter.close();
                MainActivity.toast (MainActivity.TOAST_NONE, "Done."); // resources
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // UI elements
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
