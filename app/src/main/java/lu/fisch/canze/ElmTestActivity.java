package lu.fisch.canze;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;

import lu.fisch.canze.bluetooth.ConnectedBluetoothThread;


public class ElmTestActivity extends CanzeActivity {

    private ConnectedBluetoothThread connectedBluetoothThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elm_test);
        //MainActivity.device.stopAndJoin(); // stop the poller thread

        connectedBluetoothThread = MainActivity.device.getConnectedBluetoothThread();

        TextView tv = (TextView) findViewById(R.id.textResult);
        tv.setText("");

        sendNoWait("atws\r");
        tv.append("atws\n");
        displayResponseUntil(2000, tv);

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
                        end = end + 2;
                        // convert it to a character
                        char ch = (char) data;
                        if (ch == '\r') ch = '\n';
                        tv.append("" + ch);
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
