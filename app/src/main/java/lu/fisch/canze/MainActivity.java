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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.bluetooth.ConnectedBluetoothThread;
import lu.fisch.canze.devices.ArduinoDue;
import lu.fisch.canze.devices.BobDue;
import lu.fisch.canze.devices.Device;
import lu.fisch.canze.devices.ELM327;
import lu.fisch.canze.devices.ELM327Experimental;
import lu.fisch.canze.interfaces.BluetoothEvent;
import lu.fisch.canze.interfaces.FieldListener;
import lu.fisch.canze.widgets.WidgetView;

public class MainActivity extends AppCompatActivity implements FieldListener {
    public static final String TAG = "CanZE";

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public final static String PREFERENCES_FILE = "lu.fisch.canze.settings";

    // MAC-address of Bluetooth module (you must edit this line)
    private static String bluetoothDeviceAddress = null;
    private static String bluetoothDeviceName = null;
    private static String dataFormat = "bob";
    private static String deviceName = "Arduino";

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

    public static Fields fields = Fields.getInstance();
    // old: private static Stack stack = new Stack();

    // old: public static DataReader reader = null;

    public static Device device = null;

    private static MainActivity instance = null;

    public static boolean safeDrivingMode = true;
    private static boolean isDriving = false;

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected

                // only resume if this activity is also visible
                if(visible)
                {
                    // stop reading
                    if (device!=null)
                    {
                        device.stopAndJoin();
                        device.setConnectedBluetoothThread(null);
                    }

                    // inform user
                    setTitle(TAG + " - disconnected");
                    Toast.makeText(MainActivity.this.getBaseContext(),"Bluetooth connection lost!",Toast.LENGTH_LONG).show();

                    // try to reconnect
                    onResume();
                }
            }
        }
    };

    public static MainActivity getInstance()
    {
        return instance;
    }

    public static void debug(String text)
    {
        Log.d(TAG, text);
    }

    public static void toast(final String message)
    {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(instance, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadSettings()
    {
        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE, 0);
        bluetoothDeviceAddress =settings.getString("deviceAddress", null);
        bluetoothDeviceName =settings.getString("deviceName", null);
        dataFormat = settings.getString("dataFormat", "crdt");
        deviceName = settings.getString("device", "Arduino");
        safeDrivingMode = settings.getBoolean("optSafe", true);

        String car = settings.getString("car", "Any");
        switch (car) {
            case "Any":
                Fields.getInstance().setCar(Fields.CAR_ANY);
                break;
            case "Zoé":
                Fields.getInstance().setCar(Fields.CAR_ZOE);
                break;
            case "Fluence":
                Fields.getInstance().setCar(Fields.CAR_FLUENCE);
                break;
            case "Kangoo":
                Fields.getInstance().setCar(Fields.CAR_KANGOO);
                break;
            case "X10":
                Fields.getInstance().setCar(Fields.CAR_X10);
                break;
        }

        // as the settings may have changed, we need to reload different things

        // create a new device
        switch (deviceName) {
            case "Arduino Due":
                device = new ArduinoDue();
                break;
            case "Bob Due":
                device = new BobDue();
                break;
            case "ELM327":
                device = new ELM327();
                break;
            case "ELM327 Experimental":
                device = new ELM327Experimental();
                break;
            default:
                device = null;
                break;
        }
        if(device!=null)
            device.initConnection();

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
        debug("MainActivity: onCreate");

        instance = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(TAG+" - not connected");

        // load settings
        // - includes the reader
        // - includes the decoder
        loadSettings();

        // load fields from static code
        debug("Loaded fields: " + fields.size());


        // load fields
        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE, 0);
        for(int i=0; i<fields.size(); i++)
        {
            Field f = fields.get(i);
            f.setValue(settings.getFloat(f.getUniqueID(), 0));
        }

        // connect the widgets to the respective fields
        /* no more needed as there are none here!
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
        */

        Button button;
        button = (Button) findViewById(R.id.buttonTacho);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, TachoActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonChargingActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, ChargingActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonDrivingActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connexion to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, DrivingActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonBatTemp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, BatteryTempActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonTemperature);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, TemperatureActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonBraking);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, BrakingActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonLeafSpy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, LeafSpyActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonFirmware);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, FirmwareActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonConsumption);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, ConsumptionActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonStats);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonBattery);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, BatteryActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonPgHo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(connectedBluetoothThread==null)
                {
                    Toast.makeText(MainActivity.this,"Please wait for the Bluetooth connection to be established ...",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, HarmActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonPgJm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this, ElmTestActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonExperiments);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSafe()) return;
                leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.this,ExperimentsActivity.class);
                MainActivity.this.startActivityForResult(intent,LEAVE_BLUETOOTH_ON);
            }
        });




        // link the fields to the stack
        // OLD stack.addListener(fields);

        // register for bluetooth changes
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(broadcastReceiver, intentFilter);

        // configure Bluetooth manager
        BluetoothManager.getInstance().setBluetoothEvent(new BluetoothEvent() {
            @Override
            public void onBeforeConnect() {

            }

            @Override
            public void onAfterConnect(BluetoothSocket bluetoothSocket, ConnectedBluetoothThread connectedBluetoothThread) {
                MainActivity.this.connectedBluetoothThread = connectedBluetoothThread;

                // assign the BT thread to the reader
                debug("assign the BT thread to the reader");
                if (device != null)
                    device.setConnectedBluetoothThread(connectedBluetoothThread, visible);

                // register fields this activity needs to get
                // but only if this activity is visible
                if (visible)
                    registerFields();

                device.registerFilters();


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
                // OLD: reader.registerFilters();

                // set title
                debug("set title");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(TAG + " - connected to <" + bluetoothDeviceName + "@" + bluetoothDeviceAddress + ">");
                    }
                });
            }

            @Override
            public void onBeforeDisconnect(BluetoothSocket bluetoothSocket, ConnectedBluetoothThread connectedBluetoothThread) {
                // clear all filters
                //reader.clearFields();
                if (device != null)
                    device.clearFields();
            }

            @Override
            public void onAfterDisconnect() {

            }
        });
        // detect hardware status
        int BT_STATE = BluetoothManager.getInstance().getHardwareState();
        if(BT_STATE==BluetoothManager.STATE_BLUETOOTH_NOT_AVAILABLE)
            Toast.makeText(this.getBaseContext(),"Sorry, but your device doesn't seem to have Bluetooth support!",Toast.LENGTH_LONG).show();
        else if (BT_STATE==BluetoothManager.STATE_BLUETOOTH_NOT_ACTIVE)
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }


    @Override
    public void onResume() {
        debug("MainActivity: onResume");

        visible=true;
        super.onResume();

        // if returning from a single widget activity, we have to leave here!
        if(returnFromWidget) {
            returnFromWidget=!returnFromWidget;
            return;
        }

        if(!leaveBluetoothOn) {
            reloadBluetooth();
        }
    }

    public void reloadBluetooth()
    {
        loadSettings();

        (new Thread(new Runnable() {
            @Override
            public void run() {
                connectedBluetoothThread = BluetoothManager.getInstance().connect(bluetoothDeviceAddress, true, BluetoothManager.RETRIES_INFINITE);
            }
        })).start();
    }

    @Override
    public void onPause() {
        debug("MainActivity: onPause");
        visible=false;

        if(!leaveBluetoothOn)
        {
            if(device!=null)
                device.clearFields();
            stopBluetooth();
        }

        super.onPause();
    }

    public void stopBluetooth()
    {
        if(device!=null) {
            // stop the device
            device.stopAndJoin();
            // remove reference
            device.setConnectedBluetoothThread(null);
        }
        // disconnect BT
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
            // register fields this activity needs
            registerFields();
        }
        else super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveFields()
    {
        // safe fields
        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        for(int i=0; i<fields.size(); i++)
        {
            Field f = fields.get(i);
            editor.putFloat(f.getUniqueID(),(float) f.getRawValue());
            //debug("Setting "+f.getUniqueID()+" = "+f.getRawValue());
        }
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        debug("MainActivity: onDestroy");

        // stop the device nicely
        device.stopAndJoin();
        device.setConnectedBluetoothThread(null);
        // disconnect the bluetooth
        BluetoothManager.getInstance().disconnect();

        // un-register for bluetooth changes
        this.unregisterReceiver(broadcastReceiver);

        saveFields();

        super.onDestroy();
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

            if(isSafe()) {
                // run a toast
                Toast.makeText(MainActivity.this, "Stopping Bluetooth. Settings are being loaded. Please wait ....", Toast.LENGTH_SHORT).show();

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // give the toast a moment to appear
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (device != null) {
                            // stop the BT device
                            device.stopAndJoin();
                            device.setConnectedBluetoothThread(null);
                            BluetoothManager.getInstance().disconnect();
                        }

                        // load the activity
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivityForResult(intent, SETTINGS_ACTIVITY);
                    }
                })).start();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public static void registerFields()
    {
        debug("MainActivity: registerFields");

        // speed
        Field field = fields.getBySID("5d7.0");
        if(field!=null)
        {
            field.addListener(MainActivity.getInstance());

            if(device!=null)
            {
                device.addField(field);
            }
        }
    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        if(field.getSID().equals("5d7.0"))
        {
            //debug("Speed "+field.getValue());
            isDriving = (field.getValue()>10);
        }
    }

    public static boolean isSafe()
    {
        boolean safe = !isDriving || !safeDrivingMode;
        if(!safe)
        {
            Toast.makeText(MainActivity.instance,"Not possible while driving ...",Toast.LENGTH_LONG).show();
        }
        return safe;
    }
}
