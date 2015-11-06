package lu.fisch.canze.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Dtcs;
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

        clearResult ();

        appendResult("\nSending initialisation sequence\n");
        if (!MainActivity.device.initDevice (1)) {
            appendResult("\nInitialisation failed\n");
            return;
        }

/*
        appendResult("\nSending a reset\n");
        sendNoWait("atws\r");
        appendResult(getResponseUntil(2000));

        appendResult("\nSending initialisation sequence\n");
        // ate0 (no echo)
        sendNoWait("ate0\r");
        appendResult(getResponseUntil(400, '>'));
        // ats0 (no spaces)
        sendNoWait("ats0\r");
        appendResult(getResponseUntil(400, '>'));
        // atsp6 (CAN 500K 11 bit)
        sendNoWait("atsp6\r");
        appendResult(getResponseUntil(400, '>'));
        // atat1 (auto timing)
        sendNoWait("atat1\r");
        appendResult(getResponseUntil(400, '>'));
        // atcaf0 (no formatting)
        sendNoWait("atcaf0\r");
        appendResult(getResponseUntil(400, '>'));
        // experimenting with auto-flow control

        sendNoWait("atfcsh77b\r");
        appendResult(getResponseUntil(400, '>'));
        sendNoWait("atfcsd300010\r");
        appendResult(getResponseUntil(400, '>'));
        sendNoWait("atfcsm1\r");
        appendResult(getResponseUntil(400, '>'));
*/
/*
        appendResult("\nPreparing a raw ISO-TP command\n");
        // set header to 743 (CLUSTER)
        sendNoWait("atsh743\r");
        appendResult(getResponseUntil(400, '>'));
        // set flow control header to 743 (CLUSTER)
        sendNoWait("atfcsh743\r");
        appendResult(getResponseUntil(400, '>'));

        appendResult("\nSending an ISO-TP command\n");
        sendNoWait("022180\r");
        appendResult(getResponseUntil(400));
*/
        appendResult("\nProcessing prepped ISO-TP command CLUSTER SW \n");
        field = Fields.getInstance().getBySID("763.6180.144");
        if (field != null) {
            Message message = MainActivity.device.requestField(field);
            String backRes = message.getData();
            if (backRes != null)
                if (!backRes.equals("")) {
                    appendResult(backRes.replace('\r', '•'));
                } else {
                    appendResult("empty");
                }
            else
                appendResult("null");
        }
        else
            appendResult("- field does not exist\n");

        // There was spurious error here, that immediately sending the below atcra command STOPPED the still not entirely finished ISO-TP command.
        // It was probably sending "OK>" or just ">".
        // This made the atcra fail, and therefor the following ATMA immediately overwhelmed the ELM as no filter was set.
        // As a solution, added in ELM327 to specifically wait up to 500ms (!) for a > after an ISO-TP command.
        appendResult("\nSetting filter for free frame capture\n");
        sendNoWait("atcra4f8\r");
        appendResult(getResponseUntil(400, '>'));

        appendResult("\nShowing a free frame capture\n");
        sendNoWait("atma\r");
        appendResult(getResponseUntil(200));

        appendResult("\nStopping free frame capture\n");
        appendResult(getResponseUntil(200, '>'));
        sendNoWait("x");
        // this will result in STOPPED. We need to double check if the ELM also sends a >

        appendResult(getResponseUntil(400, '>'));
        appendResult("\nResetting free frame capture\n");
        sendNoWait("atar\r");
        appendResult(getResponseUntil(400, '>'));

        appendResult("\nProcessing prepped free frame\n");
        field = Fields.getInstance().getBySID("4f8.4");
        if (field != null) {
            Message message = MainActivity.device.requestField(field);
            String backRes = message.getData();
            if (backRes != null) {
                if (!backRes.equals("")) {
                    appendResult(backRes.replace('\r', '•'));
                } else {
                    appendResult("empty");
                }
            } else
                appendResult("null");
        }
        else
            appendResult("- field does not exist\n");
    }


    void doFindAllEcus () {
        int ecu;
        String filter;
        String result;
        clearResult();
        for (ecu = 0x700; ecu <= 0x7ff; ecu++) {
            filter = Integer.toHexString(ecu);
            sendNoWait("atsh" + filter + "\r");
            getResponseUntil(400, '>');
            sendNoWait("atfcsh" + filter + "\r");
            getResponseUntil(400, '>');
            sendNoWait("022180\r");
            result = getResponseUntil(400, '>');
            appendResult(filter + ":" + result + "\n");
        }
/*
        for (ecu = 0x700; ecu <= 0x7ff; ecu++) {

        }
*/

    }

    void doQueryEcu(int ecu) {
        doQueryEcu(ecu, false);
    }

    void doQueryEcu(int ecu, boolean sourceEcu) {
        Field field = null;
        String filter;
        String result;
        clearResult();

        filter = Integer.toHexString(ecu);
        if (!sourceEcu) {
            field = Fields.getInstance().getBySID(filter + ".6180.144"); // get sw version
            if (field == null) {
                appendResult("- field does not exist\n");
                return;
            }
            filter = field.getRequestId();
        }

/*        sendNoWait("atsh" + filter + "\r");
        result = getResponseUntil(400, '>');
        // appendResult("atsh:" + filter + ":" + result + "\n");

        sendNoWait("atfcsh" + filter + "\r");
        result = getResponseUntil(400, '>');
        // appendResult("atfcsh:" + filter + ":" + result + "\n");

        sendNoWait("atfcsd300000\r");
        result = getResponseUntil(400, '>');
        // appendResult("atsh:" + filter + ":" + result + "\n");

        sendNoWait("atfcsm1\r");
        result = getResponseUntil(400, '>');
        // appendResult("atfcsm1:" + filter + ":" + result + "\n");

        sendNoWait("0210C0\r"); //wakeup
        result = getResponseUntil(600, '>');
        appendResult("0210C0:" + filter + ":" + result + "\n");

        sendNoWait("0210C0\r"); //wakeup
        result = getResponseUntil(600, '>');
        appendResult("0210C0:" + filter + ":" + result + "\n");

        sendNoWait("0210C0\r"); //wakeup
        result = getResponseUntil(600, '>');
        appendResult("0210C0:" + filter + ":" + result + "\n");
*/
        if (field != null) {
            Message message = MainActivity.device.requestField(field);
            String backRes = message.getData();
            if (backRes != null)
                if (backRes.equals("")) {
                    MainActivity.device.initDevice(2);
                    appendResult("Oops, reset\n");
                } else {
                    appendResult(backRes.replace('\r', '•'));
                }
            else
                appendResult("null");
        }

        filter = Integer.toHexString(ecu);
        field = Fields.getInstance().getBySID(filter + ".5902ff.0"); // get DTC
        if (field != null) {
            Message message = MainActivity.device.requestField(field);
            String backRes = message.getData();
            if (backRes != null) {
                if (backRes.contains(",")) {
                    appendResult("[" + backRes.replace('\r', '•') + "]\n");
                    backRes = backRes.split(",")[1];
                    // loop trough all DTC's
                    for (int i = 6; i < backRes.length() - 7; i += 8) {
                        int bits = Integer.parseInt(backRes.substring(i + 6, i + 8), 16);
                        // exclude 50 / 10 as it means something like "I have this DTC code, but I have never tested it"
                        if (bits != 0x50 && bits != 0x10) {
                            appendResult("\n" + backRes.substring(i, i + 6) + ":" + backRes.substring(i + 6, i + 8) + ":" + Dtcs.getDescription(backRes.substring(i, i + 6)));
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
                } else {
                    MainActivity.device.initDevice(100);
                    appendResult("Oeps, reset\n");
                }
            } else
                appendResult("null\n");
        } else {
            appendResult("- field does not exist\n");
            return;
        }

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
        getMenuInflater().inflate(R.menu.menu_elm_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // start the settings activity
        switch (id) {
            case R.id.action_findAllEcus:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doFindAllEcus();
                    }
                }).start();
                return true;
            case R.id.action_query7f1:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doQueryEcu(0x7f1, true);
                    }
                }).start();
                return true;
            case R.id.action_queryBroadcast:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doQueryEcu(0x7d, true);
                    }
                }).start();
                return true;
            case R.id.action_doTest:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doTest();
                    }
                }).start();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}