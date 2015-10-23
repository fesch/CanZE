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


public class DtcActivity  extends CanzeActivity {

    private ConnectedBluetoothThread connectedBluetoothThread = null;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtc);

        textView = (TextView) findViewById(R.id.textResult);

        // run the test in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                doDisplayDtc("764.5902ff.0");
            }
        }).start(); // Clima show dtc
    }

    void doDisplayDtc(String fldId) {

        Field field;

        clearResult();

        if (MainActivity.device != null) {
            // stop the poller thread
            MainActivity.device.stopAndJoin();
            // Get the bluetooth thread
            connectedBluetoothThread = MainActivity.device.getConnectedBluetoothThread();
        }

        if (connectedBluetoothThread == null) {
            appendResult("\nCan't get to BluetoothThread. Is your device paired and connected?\n");
            return;
        }

        appendResult("\nBCB DTC's\n");
        field = Fields.getInstance().getBySID(fldId);

        if (field != null) {
            String dtcString = MainActivity.device.requestField(field);
            if(dtcString!=null) {
//          appendResult(dtcString);
                int i;
                for (i = 6; i < dtcString.length() - 8; i += 8) {
                    appendResult("\nDTC" + dtcString.substring(i, i + 6) + ":" + dtcString.substring(i + 6, i + 8));
                }
                if (i < dtcString.length()) {
                    appendResult("\nDTC" + dtcString.substring(i));
                }
            }

        }
        else
            appendResult("- field does not exist\n");
    }

    void doResetDtc(String fldId) {

        Field field;

        clearResult();

        if (MainActivity.device != null) {
            // stop the poller thread
            MainActivity.device.stopAndJoin();
            // Get the bluetooth thread
            connectedBluetoothThread = MainActivity.device.getConnectedBluetoothThread();
        }

        if (connectedBluetoothThread == null) {
            appendResult("\nCan't get to BluetoothThread. Is your device paired and connected?\n");
            return;
        }

        appendResult("\nClearing BCB DTC's\n");
        field = Fields.getInstance().getBySID(fldId);

        if (field != null) {
            String dtcString = MainActivity.device.requestField(field);
            appendResult("\nDone, check DTC's again to verify\n");
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
        getMenuInflater().inflate(R.menu.menu_dtc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // start the settings activity
        if (id == R.id.action_resetEcu) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doResetDtc("764.54.0"); // clima reset dtc
                }
            }).start(); // clima reset
            return true;
        } else if (id == R.id.action_showEcu) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doDisplayDtc("764.5902ff.0"); // clima dtc
                }
            }).start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
