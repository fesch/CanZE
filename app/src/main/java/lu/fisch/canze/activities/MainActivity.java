package lu.fisch.canze.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import lu.fisch.awt.Color;
import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.bluetooth.ConnectedBluetoothThread;
import lu.fisch.canze.devices.ArduinoDue;
import lu.fisch.canze.devices.BobDue;
import lu.fisch.canze.devices.Device;
import lu.fisch.canze.devices.ELM327;
import lu.fisch.canze.devices.ELM327Experimental;
import lu.fisch.canze.fragments.ExperimentalFragment;
import lu.fisch.canze.fragments.MainFragment;
import lu.fisch.canze.interfaces.BluetoothEvent;
import lu.fisch.canze.interfaces.FieldListener;
import lu.fisch.canze.widgets.WidgetView;

public class MainActivity extends AppCompatActivity implements FieldListener {
    public static final String TAG = "CanZE";

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public final static String PREFERENCES_FILE = "lu.fisch.canze.settings";
    public final static String DATA_FILE = "lu.fisch.canze.data";

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
    public boolean leaveBluetoothOn = false;
    private boolean returnFromWidget = false;

    public static Fields fields = Fields.getInstance();

    public static Device device = null;

    private static MainActivity instance = null;

    public static boolean safeDrivingMode = true;
    private static boolean isDriving = false;

    public static boolean milesMode = false;
    public static int toastLevel = 1;

    private Fragment actualFragment;

    public static boolean dataexportMode = false;

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
        milesMode = settings.getBoolean("optMiles", false);
        dataexportMode = settings.getBoolean("optDataExport", false);
        toastLevel = settings.getInt("optToast", 1);

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

    protected void loadFragement(Fragment newFragment)
    {
        if(actualFragment==null || !actualFragment.getClass().equals(newFragment.getClass())) {
            actualFragment=newFragment;
            // Create fragment and give it an argument specifying the article it should show
            //MainFragment newFragment = new MainFragment();
            /*Bundle args = new Bundle();
            args.putInt(MainFragment.ARG_POSITION, position);
            newFragment.setArguments(args);*/
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.main, newFragment);
            //transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

            //checkButtons();
        }
    }

    /*private void checkButtons()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                }
                catch(Exception e) {
                    // ignore
                }

                if(device==null && actualFragment.getView()!=null)
                {
                    ArrayList<Button> buttons = getButtonArrayList((ViewGroup) actualFragment.getView().findViewById(R.id.table));
                    for(int i=0; i<buttons.size(); i++)
                    {
                        buttons.get(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toast("You first need to adjust the settings ...");
                            }
                        });
                    }
                }
            }
        }).start();
    }

    protected ArrayList<Button> getButtonArrayList(ViewGroup viewGroup)
    {
        ArrayList<Button> result = new ArrayList<Button>();

        if(viewGroup!=null)
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                if (v instanceof ViewGroup) {
                    result.addAll(getButtonArrayList((ViewGroup) v));
                }
                else if (v instanceof Button)
                {
                    result.add((Button)v);
                }
            }

        return result;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        debug("MainActivity: onCreate");

        instance = this;

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(TAG + " - not connected");


        // tabs
        final ActionBar actionBar = getSupportActionBar();
        // Specify that tabs should be displayed in the action bar.


        // load settings
        // - includes the reader
        // - includes the decoder
        loadSettings();

        // load fields from static code
        debug("Loaded fields: " + fields.size());


        // load fields
        final SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE, 0);
        for(int i=0; i<fields.size(); i++)
        {
            Field f = fields.get(i);
            f.setValue(settings.getFloat(f.getUniqueID(), 0));
        }

        loadFragement(new MainFragment());

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
                debug("MainActivity: onAfterConnect > assign the BT thread to the device");
                if (device != null)
                    device.setConnectedBluetoothThread(connectedBluetoothThread, visible);
                else
                    debug("... but device is null!");

                // register fields this activity needs to get
                // but only if this activity is visible
                if (visible)
                    registerFields();

                device.registerFilters();

                // set title
                debug("MainActivity: onAfterConnect > set title");
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
                //if (device != null)
                //    device.clearFields();
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

        final SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE, 0);
        if(settings.getBoolean("disclaimer",false)==false) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set title
            alertDialogBuilder.setTitle("Formal Disclaimer");

            // set dialog message
            alertDialogBuilder
                    .setMessage(Html.fromHtml("<html>CanZE (“the software”) is provided as is. Use the software at your own risk. " +
                            "The authors make no warranties as to performance or fitness for a particular purpose, " +
                            "or any other warranties whether expressed or implied. No oral or written communication " +
                            "from or information provided by the authors shall create a warranty. Under no circumstances " +
                            "shall the authors be liable for direct, indirect, special, incidental, or consequential " +
                            "damages resulting from the use, misuse, or inability to use the software, even if the author " +
                            "has been advised of the possibility of such damages. These exclusions and limitations may not " +
                            "apply in all jurisdictions. You may have additional rights and some of these limitations may not " +
                            "apply to you. This software is only intended for scientific usage." +
                            "<br>" +
                            "<br>" +
                            "<b>By using this software you are interfering with your car and doing that with hardware and " +
                            "software beyond your control, created by a loose team of interested amateurs in this field. Any " +
                            "car is a possibly lethal piece of machinery and you might hurt or kill yourself or others using " +
                            "it, or even paying attention to the displays instead of watching the road.</b></html>"))
                    .setCancelable(true)
                    .setPositiveButton("Yes, I got it!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("disclaimer", true);
                            editor.commit();
                            // current activity
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No, I didn't understand a word ...",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                    //MainActivity.this.finishAffinity(); requires API16
                                    MainActivity.this.finish();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(0);
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

    public void reloadBluetooth() {
        reloadBluetooth(true);
    }

    public void reloadBluetooth(boolean reloadSettings)
    {
        // re-load the settings if asked to
        if(reloadSettings)
            loadSettings();

        (new Thread(new Runnable() {
            @Override
            public void run() {
            // try to get a new BT thread)
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

    public void stopBluetooth() {
        stopBluetooth(true);
    }

    public void stopBluetooth(boolean reset)
    {
        if(device!=null) {
            // stop the device
            device.stopAndJoin();
            // remove reference
            device.setConnectedBluetoothThread(null, reset);
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

        if(device!=null) {
            // stop the device nicely
            device.stopAndJoin();
            device.setConnectedBluetoothThread(null);
        }
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
        else if (id == R.id.action_main) {
            loadFragement(new MainFragment());
        }
        else if (id == R.id.action_experimental) {
            loadFragement(new ExperimentalFragment());
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
