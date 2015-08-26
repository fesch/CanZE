package lu.fisch.canze;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import lu.fisch.can.Fields;
import lu.fisch.can.Stack;
import lu.fisch.can.exeptions.NoDecoderException;
import lu.fisch.can.widgets.Drawables;
import lu.fisch.can.widgets.WidgetView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "CanZE";

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String deviceAddress = null;
    private static String deviceName = null;

    //private BluetoothAdapter mBluetoothAdapter;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ConnectedThread mConnectedThread;
    private Handler h;

    public final static int RECIEVE_MESSAGE = 1;
    public final static int REQUEST_ENABLE_BT = 3;
    public final static int SETTINGS_ACTIVITY = 7;

    private ListView lv;
    private TextView txtArduino;
    private TextView txtFreq;
    private Button resetButton;
    private DrawSurface drawSurface;

    private StringBuilder sb = new StringBuilder();
    private String buffer = "";

    private int count;
    private long start;

    private Drawables drawables = new Drawables();
    private Fields fields = new Fields();
    private Stack stack = new Stack();

    public static void debug(String text)
    {
        Log.d(TAG, text);
    }

    private void loadSettings()
    {
        SharedPreferences settings = getSharedPreferences("lu.fisch.canze.settings", 0);
        deviceAddress=settings.getString("deviceAddress", null);
        deviceName=settings.getString("deviceName", null);
        debug("Loaded settings: device = " + deviceAddress);
    }

    private ArrayList<WidgetView> getWidgetViewArrayList(ViewGroup viewGroup)
    {
        ArrayList<WidgetView> result = new ArrayList<WidgetView>();

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                result.addAll(getWidgetViewArrayList((ViewGroup) v));
            }
            else if (v instanceof WidgetView)
            {
                result.add((WidgetView)v);
            }
        }

        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("CanZE - not connected");

        // load settings
        loadSettings();

        // load fields from static code
        fields.fillStatic();
        debug("Loaded fields: " + fields.size());

        // connect the widgets to the respective fields
        WidgetView wv;
        ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
        for(int i=0; i<widgets.size(); i++)
        {
            wv = widgets.get(i);
            fields.getBySID(wv.getFieldSID()).addListener(wv.getDrawable());
        }

        // link the fields to the stack
        stack.addListener(fields);

        // this is the handler that is being executed when data from
        // the bluetooth device is being received
        h = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    // if recieve a massage
                    case RECIEVE_MESSAGE:
                        // get the raw data
                        byte[] readBuf = (byte[]) msg.obj;
                        // create string from bytes array
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        // add it to the buffer
                        buffer += strIncom;
                        // split up the buffer using either space separator
                        String[] lines = {};
                        if(buffer.contains("|"))
                        {
                            lines=buffer.split("\\|");
                            // when the last symbol in the buffer if the separator, it will be cut of,
                            // but as we will not process the last message and save it for later usage,
                            // we need to add it again, if needed.
                            if(lines.length>0 && buffer.endsWith("|"))
                                lines[lines.length-1]+="|";
                        }
                        else if(buffer.contains("\r\n"))
                        {
                            lines=buffer.split("\r\n");
                            // when the last symbol in the buffer if the separator, it will be cut of,
                            // but as we will not process the last message and save it for later usage,
                            // we need to add it again, if needed.
                            if(lines.length>0 && buffer.endsWith("\r\n"))
                                lines[lines.length-1]+="\r\n";
                        }
                        // now process each message, except the last one (mostly empty anyway)
                        for(int i=0; i<lines.length-1; i++)
                        {
                            // stats
                            /*
                            if(count==0) start= Calendar.getInstance().getTimeInMillis();
                            count++;
                            double freg = (double) count/((Calendar.getInstance().getTimeInMillis()-start)/1000);
                            txtFreq.setText("Message rate: "+freg);
                            txtArduino.setText("Data from Arduino: " + lines[i]);
                            */

                            try
                            {
                                // process the message
                                stack.process(lines[i].trim());
                            }
                            catch (NoDecoderException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        // if there were some lines
                        if(lines.length>0)
                            // set the buffer to te last message (mostly empty anyway)
                            buffer = lines[lines.length-1];
                        else
                            buffer="";
                }
            };
        };

        // get Bluetooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // check it's state
        checkBTState();
    }

    /**
     * Create a new bluetooth socket on a given device
     * @param device
     * @return
     * @throws IOException
     */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    /**
     * Exit the appliction with a given error message
     * @param title
     * @param message
     */
    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        debug("BT: onResume - try connect");

        if(deviceAddress==null)
        {
            debug("BT: no device selected");
            return;
        }

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        debug("BT: connecting");
        try {
            btSocket.connect();
            debug("BT:  connection ok");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        debug("BT: create socket");

        mConnectedThread = new ConnectedThread(btSocket,h);
        mConnectedThread.start();

        setTitle("CanZE - connected to <"+deviceName+"@"+deviceAddress+">");
    }

    @Override
    public void onPause() {
        super.onPause();

        debug("BT: in onPause()");

        try
        {
            // close the socket
            if(btSocket!=null)
                btSocket.close();
        }
        catch (IOException e2)
        {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                debug("BT: bluetooth ON");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==SETTINGS_ACTIVITY)
        {
            // load settings
            loadSettings();
        }
        else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // start the settings activity
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivityForResult(intent,SETTINGS_ACTIVITY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
