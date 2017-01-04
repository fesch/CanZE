package lu.fisch.canze.activities;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import lu.fisch.canze.R;
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                appendResult("\n\nPlease wait while the poller thread is stopped...");

                if (MainActivity.device != null){
                    // stop the poller thread
                    MainActivity.device.stopAndJoin();
                }

                if (!BluetoothManager.getInstance().isConnected()) {
                    appendResult("\nNo connection. Close this screen and make sure your device paired and connected\n");
                    return;
                }

                doTest();

            }
        }).start();
    }

    void doTest () {

        Field field;
        Message message;
        String backRes;
        clearResult();

        appendResult("\nSending initialisation sequence...\n");
        if (!MainActivity.device.initDevice(1)) {
            appendResult("\nInitialisation failed\n");
            appendResult("Problem:" + MainActivity.device.getLastInitProblem() + "\n");
            return;
        }
        appendResult("Received expected result\n==========\n");

        appendResult("\nProcessing prepped ISO-TP command CLUSTER SW \n");
        field = Fields.getInstance().getBySID("763.6180.144");
        if (field == null) {
            appendResult("Requested field does not exist. This is an error in the test quite, please report\n");
            return;
        }
        message = MainActivity.device.requestFrame(field.getFrame());
        if (message == null) {
            appendResult("Msg is null. Is the car switched on?\n");
            return;
        }
        backRes = message.getData();
        if (backRes == null) {
            appendResult("Data is null. This should never happen, please report\n");
            return;
        }
        if (backRes.equals("")) {
            appendResult("Result is empty. Your dongle will not work\n");
            return;
        }
        if (!backRes.startsWith("6180")) {
            appendResult("Unexpected result:" + backRes.replace('\r', '•') + "\n");
            return;
        }
        appendResult("Received expected result\n==========\n");

        appendResult("\nProcessing prepped free frame PARK BRAKE\n");
        field = Fields.getInstance().getBySID("4f8.4");
        if (field == null) {
            appendResult("Requested field does not exist. This is an error in the test quite, please report\n");
            return;
        }
        message = MainActivity.device.requestFrame(field.getFrame());
        if (message == null) {
            appendResult("Msg is null. Is the car switched on?\n");
            return;
        }
        backRes = message.getData();
        if (backRes == null) {
            appendResult("Data is null. This should never happen, please report\n");
            return;
        }
        if (backRes.equals("")) {
            appendResult("Result is empty. Your dongle will not work\n");
            return;
        }
        if (backRes.length() != 10) {
            appendResult("Unexpected result:" + backRes.replace('\r', '•') + "\n");
            return;
        }
        appendResult("Received expected result\n==========\n");

        appendResult("\nYour device passed all the tests, it will probably work just fine\n");
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