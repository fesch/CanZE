package lu.fisch.canze;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.bluetooth.ConnectedBluetoothThread;


public class ElmTestActivity extends CanzeActivity {

    private ConnectedBluetoothThread connectedBluetoothThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elm_test);

        MainActivity.device.stopAndJoin(); // stop the poller thread

        connectedBluetoothThread = MainActivity.device.getConnectedBluetoothThread();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Field field;
                TextView tv = (TextView) findViewById(R.id.textResult);
                tv.setText("");

                tv.append("Sending a reset\n");
                sendNoWait("atws\r");
                displayResponseUntil(2000, tv);

                tv.append("\nSending initialisation commands\n");
                sendNoWait("ate0\r");
                displayResponseUntil(400, tv);
                sendNoWait("ats0\r");
                displayResponseUntil(400, tv);
                sendNoWait("atsp6\r");
                displayResponseUntil(400, tv);
                sendNoWait("atat1\r");
                displayResponseUntil(400, tv);
                sendNoWait("atcaf0\r");
                displayResponseUntil(400, tv);
                sendNoWait("atfcsh77b\r");
                displayResponseUntil(400, tv);
                sendNoWait("atfcsd300010\r");
                displayResponseUntil(400, tv);
                sendNoWait("atfcsm1\r");
                displayResponseUntil(400, tv);

/*                tv.append("\nPreparing a raw ISO-TP command\n");
                sendNoWait("atsh743\r");
                displayResponseUntil(400, tv);
                sendNoWait("atfcsh743\r");
                displayResponseUntil(400, tv);
                tv.append("\nSending an ISO-TP command\n");
                sendNoWait("03222001\r");
                displayResponseUntil(400, tv);
*/
                tv.append("\nProcessing prepped ISO-TP command\n");
                field = Fields.getInstance().getBySID("763.622001.24");
                if (field != null)
                    tv.append(MainActivity.device.requestField(field).replace('\r', '•'));
                else
                    tv.append("- field does not exist\n");
/*

                tv.append("\nPreparing a free frame capture\n");
                sendNoWait("atcra4f8\r");
                displayResponseUntil(400, tv);

                tv.append("\nShowing a free frame capture\n");
                sendNoWait("atma\r");
                displayResponseUntil(200, tv);

                tv.append("\nStopping free frame capture\n");
                sendNoWait("x");
                displayResponseUntil(200, tv);
                sendNoWait("x\r");
                displayResponseUntil(400, tv);
                tv.append("\nResetting free frame capture\n");
                sendNoWait("atar\r");
                displayResponseUntil(400, tv);
*/
                tv.append("\nProcessing prepped free frame\n");
                field = Fields.getInstance().getBySID("4f8.4");
                if (field != null)
                    tv.append(MainActivity.device.requestField(field).replace('\r', '\u2022'));
                else
                    tv.append("- field does not exist\n");

            }
        });

    }


    void flushWithTimeout (int timeout) {
        // empty incoming buffer
        // just make sure there is no previous response
        try {
            // fast track.....
            if (timeout == 0) {
                if (connectedBluetoothThread != null && connectedBluetoothThread.available() > 0) {
                    connectedBluetoothThread.read();
                }
            } else {
                long end = Calendar.getInstance().getTimeInMillis() + timeout;
                while (Calendar.getInstance().getTimeInMillis() < end) {
                    // read a byte
                    if (connectedBluetoothThread == null) return;
                    if (connectedBluetoothThread.available() > 0) {
                        // absorb the characters
                        while (connectedBluetoothThread.available() > 0) connectedBluetoothThread.read();
                        // restart the timer
                        end = Calendar.getInstance().getTimeInMillis() + timeout;
                    } else {
                        // let the system breath if there was no data
                        Thread.sleep(5);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            // ignore
        }
    }


    private void sendNoWait(String command) {
        if(connectedBluetoothThread==null) return;
        if(command!=null) {
            connectedBluetoothThread.write(command);
        }
    }


    private void displayResponseUntil (int timeout, TextView tv) {
        long end = Calendar.getInstance().getTimeInMillis() + timeout;
        boolean timedOut = false;
        boolean lastWasCr = false;
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
                            tv.append("\u2022");
                            lastWasCr = true;
                        } else {
                            if (lastWasCr) tv.append("\n");
                            tv.append("" + ch);
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

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
