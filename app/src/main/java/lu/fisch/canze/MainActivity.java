package lu.fisch.canze;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.actors.Stack;
import lu.fisch.canze.widgets.Drawables;
import lu.fisch.canze.widgets.WidgetView;
import lu.fisch.canze.readers.DataReader;
import lu.fisch.canze.readers.DueReader;
import lu.fisch.canze.readers.ElmReader;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "CanZE";

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String deviceAddress = null;
    private static String deviceName = null;
    private static String dataFormat = "bob";
    private static String device = "Arduino";

    //private BluetoothAdapter mBluetoothAdapter;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private ConnectedBluetoothThread connectedBluetoothThread;

    public final static int RECIEVE_MESSAGE = 1;
    public final static int REQUEST_ENABLE_BT = 3;
    public final static int SETTINGS_ACTIVITY = 7;

    private StringBuilder sb = new StringBuilder();
    private String buffer = "";

    private int count;
    private long start;

    private boolean connected = false;

    private Drawables drawables = new Drawables();
    private static Fields fields = new Fields();
    private static Stack stack = new Stack();

    private DataReader reader = null;

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                // device connected
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                setTitle(TAG + " - disconnected");
                debug("BT: disconnected");
                connected=false;
                // start a timer trying to reconnect
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // canel timer if we are connected again
                        if(connected)
                            timer.cancel();
                        else {
                            // try to reconnect
                            debug("BT: trying to re-connect to <"+deviceName+"> on ("+deviceAddress+")");
                            reconnect();
                        }
                    }
                }, 100,5000);
            }
        }
    };

    public static void debug(String text)
    {
        Log.d(TAG, text);
    }

    private void loadSettings()
    {
        SharedPreferences settings = getSharedPreferences("lu.fisch.canze.settings", 0);
        deviceAddress=settings.getString("deviceAddress", null);
        deviceName=settings.getString("deviceName", null);
        dataFormat = settings.getString("dataFormat", "crdt");
        device = settings.getString("device", "Arduino");

        // as the settings may have changed, we need to reload different things
            // pass the dataFormat to the stack
            stack.setDataFormat(dataFormat);
            // initialise a new dataReader
            if(device.equals("Arduino")) reader=new DueReader(stack);
            else if(device.equals("ELM327")) reader=new ElmReader(stack);
            else reader=null;

        if(reader!=null)
            reader.initConnection();
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

        setTitle(TAG+" - not connected");

        // load settings
        // - includes the reader
        // - includes the decoder
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
        /*
        stack.addListener(new StackListener() {
            @Override
            public void onFrameCompleteEvent(Frame frame) {
                TextView tv = (TextView) findViewById(R.id.textView);
                tv.setText(frame.toString());
                tv.invalidate();
            }
        });*/

        // register for bluetooth changes
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);

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
                //final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                final Method m = device.getClass().getMethod("createRfcommSocketToServiceRecord", new Class[] { UUID.class });
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

    private void reconnect()
    {
        debug("BT: (re)connect");

        if(btSocket!=null)
        {
            if (btSocket.isConnected())
                try {
                    btSocket.close();
                } catch (IOException e) {
                    debug("BT: error while disconnecting > " + e.getMessage());
                }
        }

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
            debug("BT: connection ok");

            // Create a data stream so we can talk to server.
            debug("BT: create socket");

            connectedBluetoothThread = new ConnectedBluetoothThread(btSocket,stack);
            // pass the thread to the reader: this may start it if needed
            reader.setConnectedBluetoothThread(connectedBluetoothThread);

            // apply filters
            WidgetView wv;
            ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
            for(int i=0; i<widgets.size(); i++)
            {
                wv = widgets.get(i);
                if(reader!=null)
                {
                    reader.addFilter(wv.getFieldID());
                }
            }

            //Device is now connected
            debug("BT: connected");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTitle(TAG + " - connected to <" + deviceName + "@" + deviceAddress + ">");
                }
            });
            connected=true;


        } catch (IOException e) {
            e.printStackTrace();
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        debug("BT: in onResume()");

        // only reconnect if we are not connected yet
        if(btSocket!=null) {
            if (!btSocket.isConnected())
                reconnect();
        }
        else
            reconnect();

        // re-connect the widgets to the respective fields
        WidgetView wv;
        ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
        for(int i=0; i<widgets.size(); i++)
        {
            wv = widgets.get(i);
            wv.getDrawable().setDrawSurface(wv);
        }

        // register all filters (to be sure ...)
        reader.registerFilters();

    }

    @Override
    public void onPause() {
        super.onPause();

        debug("BT: in onPause()");

        if(isFinishing()) {
            try {
                debug("BT: closing");

                // remove filters
                reader.clearFilters();

                // close the socket
                if (btSocket != null)
                    btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
            }
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
            reconnect();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
