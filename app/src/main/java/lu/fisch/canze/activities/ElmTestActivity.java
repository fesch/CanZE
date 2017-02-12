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
                appendResult(MainActivity.getStringSingle(R.string.message_PollerStopping));

                if (MainActivity.device != null){
                    // stop the poller thread
                    MainActivity.device.stopAndJoin();
                }

                if (!BluetoothManager.getInstance().isConnected()) {
                    appendResult(MainActivity.getStringSingle(R.string.message_NoConnection));
                    return;
                }

                doTest();

            }
        }).start();
    }

    protected void initListeners () {}

    void doTest () {

        Field field;
        Message message;
        String backRes;
        clearResult();

        appendResult(R.string.message_SendingInit);
        if (!MainActivity.device.initDevice(1)) {
            appendResult(MainActivity.getStringSingle(R.string.message_InitFailed));
            appendResult(MainActivity.getStringSingle(R.string.message_Problem) + MainActivity.device.getLastInitProblem() + "\n");
            return;
        }
        appendResult(R.string.message_ExpectedResult);

        appendResult(R.string.message_PrepIsoTp);
        field = Fields.getInstance().getBySID("763.6180.144");
        if (field == null) {
            appendResult(R.string.message_FieldNotExists);
            return;
        }
        message = MainActivity.device.requestFrame(field.getFrame());
        if (message.isError()) {
            appendResult(message.getError() + "\n");
            return;
        }
        backRes = message.getData();
        if (backRes.equals("")) {
            appendResult(R.string.message_ResultEmpty);
            return;
        }
        if (!backRes.startsWith("6180")) {
            appendResult(MainActivity.getStringSingle(R.string.message_UnexpectedResult) + backRes.replace('\r', '•') + "]\n");
            return;
        }
        appendResult(R.string.message_ExpectedResult);

        appendResult(R.string.message_PrepFree);
        field = Fields.getInstance().getBySID("4f8.4");
        if (field == null) {
            appendResult(R.string.message_FieldNotExists);
            return;
        }
        message = MainActivity.device.requestFrame(field.getFrame());
        if (message.isError()) {
            appendResult(R.string.message_MessageNull);
            return;
        }
        backRes = message.getData();
        if (backRes.equals("")) {
            appendResult(R.string.message_ResultEmpty);
            return;
        }
        if (backRes.length() != 10) {
            appendResult(MainActivity.getStringSingle(R.string.message_UnexpectedResult) + backRes.replace('\r', '•') + "]\n");
            return;
        }
        appendResult(R.string.message_ExpectedResult);

        appendResult(R.string.message_DevicePassed);
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

    private void appendResult(int strResource) {
        final String localStr = MainActivity.getStringSingle(strResource);
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