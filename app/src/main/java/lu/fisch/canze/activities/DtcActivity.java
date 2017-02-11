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


public class DtcActivity  extends CanzeActivity {

    private TextView textView;
    BufferedWriter bufferedDumpWriter = null;
    boolean dumpInProgress = false;

    private StoppableThread queryThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtc);

        textView = (TextView) findViewById(R.id.textResult);

        ArrayAdapter <String> arrayAdapter = new ArrayAdapter <> (this,android.R.layout.simple_list_item_1);
        for (Ecu ecu : Ecus.getInstance().getAllEcus()) {
            if (ecu.getFromId() != 0) arrayAdapter.add(ecu.getMnemonic());
        }
        // display the list
        final Spinner spinnerEcu = (Spinner) findViewById(R.id.ecuList);
        spinnerEcu.setAdapter(arrayAdapter);

        final Button btnQuery = (Button) findViewById(R.id.ecuQuery);
        btnQuery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doQueryEcu(Ecus.getInstance().getByMnemonic(String.valueOf(spinnerEcu.getSelectedItem())));
            }
        });

        final Button btnClear = (Button) findViewById(R.id.ecuClear);
        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doClearEcu(Ecus.getInstance().getByMnemonic(String.valueOf(spinnerEcu.getSelectedItem())));
            }
        });

        final Button btnDiag = (Button) findViewById(R.id.ecuDiag);
        btnDiag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiagEcu(Ecus.getInstance().getByMnemonic(String.valueOf(spinnerEcu.getSelectedItem())));
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

    protected void initListeners () {}

    void doQueryEcu(final Ecu ecu) {

        clearResult();
        appendResult("Query " + ecu.getName() + " (renault ID:" + ecu.getRenaultId() + ")\n");

        // here initialize this particular ECU diagnostics fields
        try {
            Object diagEcu = Class.forName("lu.fisch.canze.actors.EcuDiag" + ecu.getMnemonic()).newInstance();
            java.lang.reflect.Method methodLoad;

            methodLoad = diagEcu.getClass().getMethod("load");
            methodLoad.invoke(diagEcu);
        } catch (Exception e) {
            appendResult(R.string.message_NoEcuDefinition);
        }


        // re-initialize the device
        appendResult(R.string.message_SendingInit);

        // try to stop previous thread
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
                if (filter.equals("793")) {
                    // we are a tester
                    appendResult(R.string.message_StartTestSession);
                    field = Fields.getInstance().getBySID(filter + ".7e01.0");
                    if (field == null) {
                        appendResult(R.string.message_NoTestSessionField);
                        return;
                    }

                    // query the Field
                    message = MainActivity.device.requestFrame(field.getFrame());
                    if (message.isError()) {
                        appendResult(message.getError() + "\n");
                        return;
                    }

                    backRes = message.getData();
                    // check the response
                    if (!backRes.toLowerCase().startsWith("7e")) {
                        appendResult(MainActivity.getStringSingle(R.string.message_UnexpectedResult) + backRes + "]\n");
                        return;
                    }

                    // start a tester session
                    appendResult(R.string.message_StartTestSession);
                    field = Fields.getInstance().getBySID(filter + ".5081.0");
                    if (field == null) {
                        appendResult(R.string.message_NoTestSessionField);
                        return;
                    }

                    // query the Field
                    message = MainActivity.device.requestFrame(field.getFrame());
                    if (message.isError()) {
                        appendResult(message.getError() + "\n");
                        return;
                    }

                    backRes = message.getData();
                    // check the response
                    if (!backRes.startsWith("50")) {
                        appendResult(MainActivity.getStringSingle(R.string.message_UnexpectedResult) + backRes + "]\n");
                        return;
                    }

                    // start a tester2 session
                    appendResult(R.string.message_StartTestSession);
                    field = Fields.getInstance().getBySID(filter + ".50c0.0");
                    if (field == null) {
                        appendResult(R.string.message_NoTestSessionField);
                        return;
                    }

                    // query the Field
                    message = MainActivity.device.requestFrame(field.getFrame());
                    if (message.isError()) {
                        appendResult(message.getError() + "\n");
                        return;
                    }

                    backRes = message.getData();
                    // check the response
                    if (!backRes.startsWith("50")) {
                        appendResult(MainActivity.getStringSingle(R.string.message_UnexpectedResult) + backRes + "]\n");
                        return;
                    }
                }

                // compile the field query and get the Field object
                appendResult(R.string.message_GetDtcs);
                field = Fields.getInstance().getBySID(filter + "." + ecu.getGetDtcs() + ".0"); // get DTC
                if (field == null) {
                    appendResult(R.string.message_NoGetDtcsField);
                    return;
                }

                // query the Field
                message = MainActivity.device.requestFrame(field.getFrame());
                if (message.isError()) {
                    appendResult(message.getError() + "\n");
                    return;
                }

                backRes = message.getData();
                // check the response
                if (!backRes.startsWith("59")) {
                    appendResult(MainActivity.getStringSingle(R.string.message_UnexpectedResult) + backRes + "]\n");
                    return;
                }

                // loop trough all DTC's
                // format of the message is
                // blocks of 4 bytes
                //   first 2 bytes is the DTC
                //     first nibble of DTC is th P0 etc encoding, but we are nit using that
                //   next byte is the test that triggered the DTC
                //   next byte contains the flags
                // All decoding is done in the Dtcs class
                boolean onePrinted = false;
                for (int i = 6; i < backRes.length() - 7; i += 8) {
                    // see if we need to stop right now
                    if(((StoppableThread) Thread.currentThread()).isStopped()) return;

                    int flags = Integer.parseInt(backRes.substring(i + 6, i + 8), 16);
                    // exclude 50 / 10 as it means something like "I have this DTC code, but I have never tested it"
                    if (flags != 0x50 && flags != 0x10) {
                        onePrinted = true;
                        appendResult(
                                "\n*** DTC" + backRes.substring(i, i + 6) + " (" + Dtcs.getInstance().getDisplayCodeById (backRes.substring(i, i + 6)) + ") ***\n"
                                        + Dtcs.getInstance().getDescriptionById(backRes.substring(i, i + 6))
                                        + "\nFlags:" + Dtcs.getInstance().getFlagDescription(flags)
                        );
                    }
                }
                if (!onePrinted) appendResult(R.string.message_NoActiveDtcs);
            }

        });
        queryThread.start();
    }

    void doClearEcu(final Ecu ecu) {

        clearResult();

        appendResult(MainActivity.getStringSingle(R.string.message_clear) + ecu.getName() + " (renault ID:" + ecu.getRenaultId() + ")\n");

        // try to stop previous thread
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

                // query the Field
                Message message = MainActivity.device.requestFrame (frame);
                if (message.isError()) {
                    appendResult(R.string.message_MessageNull);
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


    void doDiagEcu(final Ecu ecu) {

        clearResult();              // clear the screen

        // here initialize this particular ECU diagnostics fields
        try {
            Object diagEcu = Class.forName("lu.fisch.canze.actors.EcuDiag" + ecu.getMnemonic()).newInstance();
            java.lang.reflect.Method methodLoad;

            methodLoad = diagEcu.getClass().getMethod("load");
            methodLoad.invoke(diagEcu);

            //Frames.getInstance().load (diagEcu.framesString ());
            //Fields.getInstance().load (diagEcu.fieldsString ());
        } catch (Exception e) {
            appendResult(R.string.message_NoEcuDefinition2);
            return;
        }

        appendResult(R.string.message_SendingInit);

        // try to stop previous thread
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

        queryThread = new StoppableThread(new Runnable() {
            @Override
            public void run() {

                // re-initialize the device
                if (!MainActivity.device.initDevice(1)) {
                    appendResult(R.string.message_InitFailed);
                    return;
                }

                createDump(ecu);

                for (Frame frame : Frames.getInstance().getAllFrames()) {
                    // see if we need to stop right now
                    if(((StoppableThread) Thread.currentThread()).isStopped()) return;

                    if (frame.getContainingFrame() != null) { // only use subframes

                        // query the Frame
                        Message message = MainActivity.device.requestFrame(frame);
                        if (!message.isError()) {
                            // process the frame by going through all the containing fields
                            // setting their values and notifying all listeners (there should be none)
                            // Fields.getInstance().onMessageCompleteEvent(message);
                            message.onMessageCompleteEvent();

                            for (Field field : frame.getAllFields()) {
                                if (field.isString()) {
                                    appendResult(field.getName() + ":" + field.getStringValue() + "\n");
                                } else if (field.isList()) {
                                    appendResult(field.getName() + ":" + field.getListValue() + "\n");
                                } else {
                                    appendResult(field.getName() + ":" + field.getValue() + field.getUnit() + "\n");
                                }
                            }
                        } else {
                            appendResult(frame.getHexId() + "." + frame.getResponseId() + ":" + message.getError() + "\n");
                            if (!MainActivity.device.initDevice(1)) {
                                appendResult(MainActivity.getStringSingle(R.string.message_InitFailed));
                                return;
                            }
                        }
                    }
                }
                closeDump();
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

    private void appendResult(String str) {
        if ( dumpInProgress) log (str);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(localStr);
            }
        });
    }

    private void log(String text)
    {
        try {
            bufferedDumpWriter.append(text);
            bufferedDumpWriter.append(System.getProperty("line.separator"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String SDstate = Environment.getExternalStorageState();
        return ( Environment.MEDIA_MOUNTED.equals(SDstate));
    }

    private void createDump (Ecu ecu) {

        dumpInProgress = false;
        SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHMS), Locale.getDefault());


        // ensure that there is a CanZE Folder in SDcard
        if ( ! isExternalStorageWritable()) {
            debug ( "DiagDump: SDcard not writeable");
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
                debug("DiagDump: NewFile:" +  exportdataFileName );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //BufferedWriter for performance, true to set append to file flag
            bufferedDumpWriter = new BufferedWriter(new FileWriter(logFile, true));
            dumpInProgress = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void closeDump () {
        try {
            if (dumpInProgress) bufferedDumpWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // UI elements
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

}
