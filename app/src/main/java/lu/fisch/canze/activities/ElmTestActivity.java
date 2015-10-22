package lu.fisch.canze.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.bluetooth.ConnectedBluetoothThread;


public class ElmTestActivity extends CanzeActivity {

    private ConnectedBluetoothThread connectedBluetoothThread = null;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elm_test);

        textView = (TextView) findViewById(R.id.textResult);

        // run the test in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                doTest();
            }
        }).start();
    }

    void doTest () {

        Field field;

        clearResult ();

        if (MainActivity.device != null){
            // stop the poller thread
            MainActivity.device.stopAndJoin();
            // Get the bluetooth thread
            connectedBluetoothThread = MainActivity.device.getConnectedBluetoothThread();
        }

        if (connectedBluetoothThread == null) {
            appendResult("\nCan't get to BluetoothThread. Is your device paired and connected?\n");
            return;
        }

        appendResult("\nSending a reset\n");
        sendNoWait("atws\r");
        appendResult(displayResponseUntil(2000));

        appendResult("\nSending initialisation sequence\n");
        sendNoWait("ate0\r");
        appendResult(displayResponseUntil(400));
        sendNoWait("ats0\r");
        appendResult(displayResponseUntil(400));
        sendNoWait("atsp6\r");
        appendResult(displayResponseUntil(400));
        sendNoWait("atat1\r");
        appendResult(displayResponseUntil(400));
        sendNoWait("atcaf0\r");
        appendResult(displayResponseUntil(400));
        sendNoWait("atfcsh77b\r");
        appendResult(displayResponseUntil(400));
        sendNoWait("atfcsd300010\r");
        appendResult(displayResponseUntil(400));
        sendNoWait("atfcsm1\r");
        appendResult(displayResponseUntil(400));

        appendResult("\nPreparing a raw ISO-TP command\n");
        sendNoWait("atsh743\r");
        appendResult(displayResponseUntil(400));
        sendNoWait("atfcsh743\r");
        appendResult(displayResponseUntil(400));
        appendResult("\nSending an ISO-TP command\n");
        sendNoWait("03222001\r");
        appendResult(displayResponseUntil(400));

        appendResult("\nProcessing prepped ISO-TP command\n");
        field = Fields.getInstance().getBySID("763.622001.24");
        if (field != null) {
            String backRes = MainActivity.device.requestField(field);
            if (backRes != null)
                appendResult(backRes.replace('\r', '•'));
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
        appendResult(displayResponseUntil(400));

        appendResult("\nShowing a free frame capture\n");
        sendNoWait("atma\r");
        appendResult(displayResponseUntil(200));

        appendResult("\nStopping free frame capture\n");
        appendResult(displayResponseUntil(200));
        sendNoWait("x");
        // this will result in STOPPED. We need to double check if the ELM also sends a >

        appendResult(displayResponseUntil(400));
        appendResult("\nResetting free frame capture\n");
        sendNoWait("atar\r");
        appendResult(displayResponseUntil(400));

        appendResult("\nProcessing prepped free frame\n");
        field = Fields.getInstance().getBySID("4f8.4");
        if (field != null) {
            String backRes = MainActivity.device.requestField(field);
            if (backRes != null)
                appendResult(backRes.replace('\r', '\u2022'));
            else
                appendResult("null");
        }
        else
            appendResult("- field does not exist\n");
    }


    void doFindEcu () {
        int ecu;
        String filter;
        String result;
        clearResult ();
        for (ecu = 0x700; ecu <= 0x7ff; ecu++) {
            filter = Integer.toHexString(ecu);
            sendNoWait("atsh" + filter + "\r");
            displayResponseUntil(400);
            sendNoWait("atfcsh" + filter + "\r");
            displayResponseUntil(400);
            sendNoWait("022180\r");
            result = displayResponseUntil(400);
            if (result.length() > 20) result = result.substring(0,20);
            appendResult(filter + ":" + result + "\n");
        }
    }

    void doFindBcb () {
        int ecu;
        String filter;
        String result;
        clearResult ();

        ecu = 0x792;
        filter = Integer.toHexString(ecu);
        sendNoWait("atsh" + filter + "\r");
        displayResponseUntil(400);
        sendNoWait("atfcsh" + filter + "\r");
        displayResponseUntil(400);
        sendNoWait("atfcsm1\r");
        appendResult(displayResponseUntil(400));
        sendNoWait("022180\r");
        result = displayResponseUntil(400);
        //if (result.length() > 20) result = result.substring(0,20);
        appendResult(filter + ":" + result + "\n");
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
        if(connectedBluetoothThread==null) return;
        if(command!=null) {
            connectedBluetoothThread.write(command);
        }
    }

    private String displayResponseUntil (int timeout) {
        long end = Calendar.getInstance().getTimeInMillis() + timeout;
        boolean timedOut = false;
        boolean lastWasCr = false;
        String result = "";
        while(Calendar.getInstance().getTimeInMillis() <= end)
        {
            try {
                // read a byte
                if(connectedBluetoothThread!=null && connectedBluetoothThread.available()>0) {
                    //MainActivity.debug("Reading ...");
                    int data = connectedBluetoothThread.read();
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
        if (id == R.id.action_findEcu) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doFindEcu();
                }
            }).start();
            return true;
        } else if (id == R.id.action_findBcb) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doFindBcb();
                }
            }).start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}