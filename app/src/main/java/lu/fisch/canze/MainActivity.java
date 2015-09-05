package lu.fisch.canze;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.actors.Stack;
import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.bluetooth.ConnectedBluetoothThread;
import lu.fisch.canze.interfaces.BluetoothEvent;
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

    private ConnectedBluetoothThread connectedBluetoothThread;

    public final static int RECIEVE_MESSAGE   = 1;
    public final static int REQUEST_ENABLE_BT = 3;
    public final static int SETTINGS_ACTIVITY = 7;
    public final static int LEAVE_BLUETOOTH_ON= 11;

    private StringBuilder sb = new StringBuilder();
    private String buffer = "";

    private int count;
    private long start;

    private boolean visible = true;
    private boolean leaveBluetoothOn = false;
    private boolean returnFromWidget = false;

    private Drawables drawables = new Drawables();
    public static Fields fields = new Fields();
    private static Stack stack = new Stack();

    public static DataReader reader = null;

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected

                // only resume if this activity is also visible
                if(visible)
                {
                    // stop reading
                    // assign the BT thread to the reader
                    if (reader != null)
                        reader.setConnectedBluetoothThread(null);

                    // inform user
                    setTitle(TAG + " - disconnected");
                    Toast.makeText(MainActivity.this.getBaseContext(),"Bluetooth connection lost!",Toast.LENGTH_LONG).show();

                    // try to reconnect
                    onResume();
                }
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
        ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
        for(int i=0; i<widgets.size(); i++)
        {
            final WidgetView wv = widgets.get(i);
            fields.getBySID(wv.getFieldSID()).addListener(wv.getDrawable());

            wv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // get pointer index from the event object
                    int pointerIndex = event.getActionIndex();

                    // get pointer ID
                    int pointerId = event.getPointerId(pointerIndex);

                    // get masked (not specific to a pointer) action
                    int maskedAction = event.getActionMasked();

                    switch (maskedAction) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN:
                        {
                            leaveBluetoothOn=true;
                            Intent intent = new Intent(MainActivity.this, WidgetActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            WidgetView.selectedDrawable = wv.getDrawable();
                            MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
                            break;
                        }
                        case MotionEvent.ACTION_MOVE:
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            break;
                        }
                    }

                    wv.invalidate();

                    return true;
                }
            });
        }

        Button button;
        button = (Button) findViewById(R.id.buttonTacho);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, TachoActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });


        // link the fields to the stack
        stack.addListener(fields);

        // register for bluetooth changes
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, intentFilter);

        // configure Bluetooth manager
        BluetoothManager.getInstance().setBluetoothEvent(new BluetoothEvent() {
            @Override
            public void onBeforeConnect() {

            }

            @Override
            public void onAfterConnect(BluetoothSocket bluetoothSocket, ConnectedBluetoothThread connectedBluetoothThread) {
                MainActivity.this.connectedBluetoothThread=connectedBluetoothThread;
                // assign the stack to the BT thread
                debug("assign the stack to the BT thread");
                connectedBluetoothThread.setStack(stack);
                // assign the BT thread to the reader
                debug("assign the BT thread to the reader");
                if (reader != null)
                    reader.setConnectedBluetoothThread(connectedBluetoothThread);
                /*
                // set all filters
                debug("set all filters & connect widgets");
                WidgetView wv;
                ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
                for (int i = 0; i < widgets.size(); i++) {
                    wv = widgets.get(i);
                    // add filter
                    if (reader != null) {
                        reader.addFilter(wv.getFieldID());
                    }
                    // connect to correct surface
                    wv.getDrawable().setDrawSurface(wv);
                }
                */
                // register filters
                reader.registerFilters();
                // set title
                debug("set title");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(TAG + " - connected to <" + deviceName + "@" + deviceAddress + ">");
                    }
                });
            }

            @Override
            public void onBeforeDisconnect(BluetoothSocket bluetoothSocket, ConnectedBluetoothThread connectedBluetoothThread) {
                // clear all filters
                reader.clearFilters();
            }

            @Override
            public void onAfterDisconnect() {

            }
        });
        // detect hardware status
        int BT_STATE = BluetoothManager.getInstance().getHardwareState();
        if(BT_STATE==BluetoothManager.STATE_BLUETOOTH_NOT_AVAILABLE)
            Toast.makeText(this.getBaseContext(),"Sorry, but your device does not seam to have Bluettoot support!",Toast.LENGTH_LONG).show();
        else if (BT_STATE==BluetoothManager.STATE_BLUETOOTH_NOT_ACTIVE)
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    @Override
    public void onResume() {
        visible=true;
        super.onResume();
        debug("MainActivity: onResume");

        // connect all widgets
        debug("MainActivity: onResume > connect all widgets");
        ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
        for (int i = 0; i < widgets.size(); i++) {
            WidgetView wv = widgets.get(i);
            // connect to correct surface
            wv.getDrawable().setDrawSurface(wv);
        }

        // if returning from a single widget activity, we have to leave here!
        if(returnFromWidget) {
            returnFromWidget=!returnFromWidget;
            return;
        }

        if(!leaveBluetoothOn) {
            loadSettings();

            (new Thread(new Runnable() {
                @Override
                public void run() {
                    connectedBluetoothThread = BluetoothManager.getInstance().connect(deviceAddress, true, BluetoothManager.RETRIES_INFINITE);
                }
            })).start();
        }
    }

    @Override
    public void onPause() {
        visible=false;

        super.onPause();

        debug("MainActivity: onPause");

        if(!leaveBluetoothOn)
            BluetoothManager.getInstance().disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==SETTINGS_ACTIVITY)
        {
            // load settings
            loadSettings();
        }
        else if(requestCode==LEAVE_BLUETOOTH_ON)
        {
            returnFromWidget=true;
            leaveBluetoothOn=false;
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
            startActivityForResult(intent, SETTINGS_ACTIVITY);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
