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

import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Dtcs;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.bluetooth.BluetoothManager;


public class DtcActivity  extends CanzeActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtc);
        textView = (TextView) findViewById(R.id.textResult);


        final Spinner spinnerEcu = (Spinner) findViewById(R.id.ecuList);
        final Button btnQuery = (Button) findViewById(R.id.ecuQuery);
        final Button btnReset = (Button) findViewById(R.id.ecuReset);

        btnQuery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity.toast("On Button Click : " + "\n" + String.valueOf(spinnerEcu.getSelectedItem()));
                doQueryEcu(ecuToId(String.valueOf(spinnerEcu.getSelectedItem())));
            }
        });

        btnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity.toast("On Button Click : " + "\n" + String.valueOf(spinnerEcu.getSelectedItem()));
                doResetEcu(ecuToId(String.valueOf(spinnerEcu.getSelectedItem())));
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                appendResult("\n\nPlease wait while the poller thread is stopped...\n");

                if (MainActivity.device != null) {
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

    void doQueryEcu(int ecu) {
        doQueryEcu(ecu, false);
    }

    void doQueryEcu(int ecu, boolean sourceEcu) {
        Field field;
        String filter;
        String result;
        clearResult();

        filter = Integer.toHexString(ecu);
        field = Fields.getInstance().getBySID(filter + ".5902ff.0"); // get DTC
        if (field == null) {
            appendResult("- field does not exist\n");
            return;
        }

        appendResult("\nSending initialisation sequence\n");
        if (!MainActivity.device.initDevice(1)) {
            appendResult("\nInitialisation failed\n");
            return;
        }

        String backRes = MainActivity.device.requestField(field);
        if (backRes == null) {
            appendResult("Request DTC code for this ECU not found, nothing send\n");
            return;
        }

        if (!backRes.contains(",")) {
            appendResult("Request DTC code send, but empty response\n");
            return;
        }

        backRes = backRes.split(",")[1];
        if (!backRes.startsWith("59")) {
            appendResult("Reset send, but unexpected result received:[" + backRes + "\n");
            return;
        }

        // loop trough all DTC's
        for (int i = 6; i < backRes.length() - 7; i += 8) {
            int bits = Integer.parseInt(backRes.substring(i + 6, i + 8), 16);
            // exclude 50 / 10 as it means something like "I have this DTC code, but I have never tested it"
            if (bits != 0x50 && bits != 0x10) {
                appendResult("\nDTC" + backRes.substring(i, i + 6) + ":" + backRes.substring(i + 6, i + 8) + ":" + Dtcs.getDescription(backRes.substring(i, i + 6)));
                if ((bits & 0x01) != 0) appendResult(" tstFail");
                if ((bits & 0x02) != 0) appendResult(" tstFailThisOp");
                if ((bits & 0x04) != 0) appendResult(" pendingDtc");
                if ((bits & 0x08) != 0) appendResult(" confirmedDtc");
                if ((bits & 0x10) != 0) appendResult(" noCplSinceClear");
                if ((bits & 0x20) != 0) appendResult(" faildSinceClear");
                if ((bits & 0x40) != 0) appendResult(" tstNtCpl");
                if ((bits & 0x80) != 0) appendResult(" WrnLght");
            }
        }
    }

    void doResetEcu(int ecu) {
        doResetEcu(ecu, false);
    }

    void doResetEcu(int ecu, boolean sourceEcu) {

        Field field;
        String filter;
        clearResult();

        filter = Integer.toHexString(ecu);
        field = Fields.getInstance().getBySID(filter + ".54.0"); // get DTC reset

        if (field == null) {
            appendResult("- field does not exist\n");
            return;
        }

        appendResult("\nSending initialisation sequence\n");
        if (!MainActivity.device.initDevice(1)) {
            appendResult("\nInitialisation failed\n");
            return;
        }

        String backRes = MainActivity.device.requestField(field);
        if (backRes == null) {
            appendResult("Reset code for this ECU not found, nothing send\n");
            return;
        }

        if (!backRes.contains(",")) {
            appendResult("Reset code send, but empty response\n");
            return;
        }

        backRes = backRes.split(",")[1];
        if (!backRes.startsWith("54")) {
            appendResult("Reset code send, but unexpected result received:[" + backRes + "\n");
            return;
        }

        appendResult("Reset seems succesful, please query DTCs\n");
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

    // ELM functions not available or reachable through the device Class
    private void sendNoWait(String command) {
        if (!BluetoothManager.getInstance().isConnected()) return;
        if (command != null) {
            BluetoothManager.getInstance().write(command);
        }
    }

    private String getResponseUntil(int timeout) {
        return getResponseUntil(timeout, '\0');
    }

    private String getResponseUntil(int timeout, char stopChar) {
        long end = Calendar.getInstance().getTimeInMillis() + timeout;
        boolean lastWasCr = false;
        String result = "";
        while (Calendar.getInstance().getTimeInMillis() <= end) {
            try {
                // read a byte
                if (BluetoothManager.getInstance().isConnected() && BluetoothManager.getInstance().available() > 0) {
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
                } else {
                    // let the system breath if there was no data
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                // ignore: e.printStackTrace();
            }
        }
        // quit on timeout
        return result;
    }

    // UI elements
    @Override
    protected void onDestroy() {
        // restart the poller
        if (MainActivity.device != null)
            MainActivity.device.initConnection();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    private int ecuToId(String ecu) { // this function should be moved to a separate class, or a function in Fields
        switch (ecu) {
            case "BCB":
                return 0x793;
            case "CLIMA":
            case "CLIMBOX":
                return 0x764;
            case "CLUSTER":
                return 0x763;
            case "EVC":
            case "EVCBRIDGE":
            case "SCH":
                return 0x7ec;
            case "TCU":
                return 0x7da;
            case "LBC":
                return 0x7bb;
            case "PEB":
                return 0x77e;
            case "AIBAG":
            case "AIRBAG":
                return 0x772;
            case "USM":
            case "UCM":
            case "UPC":
                return 0x76d;
            case "EPS":
                return 0x762;
            case "ABS":
            case "ESC":
                return 0x760;
            case "UBP":
                return 0x7bc;
            case "BCM":
            case "UCH":
                return 0x765;
            case "UPA":
                return 0x76e;
            case "LBC2":
                return 0x76e;
        }
        return 0;
    }
}