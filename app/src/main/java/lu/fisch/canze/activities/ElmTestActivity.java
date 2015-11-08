package lu.fisch.canze.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Dtcs;
import lu.fisch.canze.actors.Ecus;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.bluetooth.BluetoothManager;

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

public class ElmTestActivity extends CanzeActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elm_test);
        textView = (TextView) findViewById(R.id.textResult);

        final Button btnTest = (Button) findViewById(R.id.elmTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doTest();
                    }
                }).start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                appendResult("\n\nPlease wait while the poller thread is stopped...\n");

                if (MainActivity.device != null){
                    // stop the poller thread
                    MainActivity.device.stopAndJoin();
                }

                if (!BluetoothManager.getInstance().isConnected()) {
                    appendResult("\nIs your device paired and connected?\n");
                    return;
                }

                appendResult("\nReady");
            }
        }).start();
    }

    void doTest () {

        Field field;

        clearResult();

        appendResult("\nSending initialisation sequence...\n");
        if (!MainActivity.device.initDevice (1)) {
            appendResult("\nInitialisation failed\n");
            appendResult("Problem:" + MainActivity.device.getLastInitProblem() + "\n");
            return;
        }
        appendResult("Done\n");

        appendResult("\nProcessing prepped ISO-TP command CLUSTER SW \n");
        field = Fields.getInstance().getBySID("763.6180.144");
        if (field != null) {
            Message message = MainActivity.device.requestField(field);
            if (message != null) {
                String backRes = message.getData();
                if (backRes != null)
                    if (!backRes.equals("")) {
                        appendResult(backRes.replace('\r', '•'));
                    } else {
                        appendResult("empty");
                    }
                else
                    appendResult("data null");
            }
            else
                appendResult("msg null");
        }
        else
            appendResult("field does not exist\n");


        appendResult("\nProcessing prepped free frame\n");
        field = Fields.getInstance().getBySID("4f8.4");
        if (field != null) {
            Message message = MainActivity.device.requestField(field);
            if (message != null) {
                String backRes = message.getData();
                if (backRes != null) {
                    if (!backRes.equals("")) {
                        appendResult(backRes.replace('\r', '•'));
                    } else {
                        appendResult("empty");
                    }
                }
                else
                    appendResult("data null");
            }
            else
                appendResult("msg null");
        }
        else
            appendResult("- field does not exist\n");
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
        final String localStr = str;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(localStr);
            }
        });
    }
/*

    // ELM functions not available or reachable through the device Class
    private void sendNoWait(String command) {
        if(!BluetoothManager.getInstance().isConnected()) return;
        if(command!=null) {
            BluetoothManager.getInstance().write(command);
        }
    }

    private String getResponseUntil(int timeout) {
        return getResponseUntil(timeout, '\0');
    }


    private String getResponseUntil(int timeout, char stopChar) {
        long end = Calendar.getInstance().getTimeInMillis() + timeout;
        boolean timedOut = false;
        boolean lastWasCr = false;
        String result = "";
        while(Calendar.getInstance().getTimeInMillis() <= end)
        {
            try {
                // read a byte
                if(BluetoothManager.getInstance().isConnected() && BluetoothManager.getInstance().available()>0) {
                    //MainActivity.debug("Reading ...");
                    int data = BluetoothManager.getInstance().read();
                    // if it is a real one
                    if (data != -1) {
                        // we might be JUST approaching the TIMEOUT, so give it a chance to get to the EOM,
                        // end = end + 2;
                        // convert it to a character
                        char ch = (char) data;
                        if (ch == '\r') {
                            result += "\u2022";
                            lastWasCr = true;
                        } else {
                            if (lastWasCr) result += "\n";
                            result += ch;
                            lastWasCr = false;
                        }
                        // quit on stopchar after making sure the stop character is added to the output and
                        // a possible newline was indeed added
                        if (ch == stopChar) return result;
                    }
                }
                else
                {
                    // let the system breath if there was no data
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            catch (IOException e)
            {
                // ignore: e.printStackTrace();
            }
        }
        // quit on timeout
        return result;
    }
*/
    // UI elements
    @Override
    protected void onDestroy() {
        // restart the poller
        if(MainActivity.device!=null)
            MainActivity.device.initConnection();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }
}