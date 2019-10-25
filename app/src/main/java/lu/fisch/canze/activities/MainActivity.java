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

package lu.fisch.canze.activities;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
//import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.actors.Frames;
import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.classes.DataLogger;
import lu.fisch.canze.classes.DebugLogger;
import lu.fisch.canze.database.CanzeDataSource;
import lu.fisch.canze.devices.CanSee;
import lu.fisch.canze.devices.Device;
import lu.fisch.canze.devices.ELM327;
import lu.fisch.canze.devices.ELM327OverHttp;
import lu.fisch.canze.interfaces.BluetoothEvent;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;
import lu.fisch.canze.ui.AppSectionsPagerAdapter;
import me.drakeet.support.toast.ToastCompat;

public class MainActivity extends AppCompatActivity implements FieldListener /*, android.support.v7.app.ActionBar.TabListener */ {
    public static final String TAG = "CanZE";

    // SPP UUID service
    // private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public final static String PREFERENCES_FILE = "lu.fisch.canze.settings";
    public final static String DATA_FILE = "lu.fisch.canze.data";

    // MAC-address of Bluetooth module (you must edit this line)
    private static String bluetoothDeviceAddress = null;
    private static String bluetoothDeviceName = null;

    // url of gateway if in use
    private static String gatewayUrl = null;

    // public final static int RECEIVE_MESSAGE      = 1;
    public final static int REQUEST_ENABLE_BT = 3;
    public final static int SETTINGS_ACTIVITY = 7;
    public final static int LEAVE_BLUETOOTH_ON = 11;

    // note that the CAR constants are stored in the option property of the field object
    // this is a short

    // public static final short CAR_MASK            = 0xff;

    public static final short CAR_NONE = 0x000;
    //public static final int CAR_ANY               = 0x0ff;
    //public static final short CAR_FLUENCE = 0x001;
    public static final short CAR_ZOE_Q210 = 0x002;
    //public static final short CAR_KANGOO = 0x004;
    //public static final short CAR_TWIZY = 0x008;     // you'll never know ;-)
    //public static final short CAR_X10 = 0x010;     // not used
    public static final short CAR_ZOE_R240 = 0x020;
    public static final short CAR_ZOE_Q90 = 0x040;
    public static final short CAR_ZOE_R90 = 0x080;

    public static final short FIELD_TYPE_MASK = 0x700;
    //public static final short FIELD_TYPE_UNSIGNED = 0x000;
    public static final short FIELD_TYPE_SIGNED = 0x100;
    public static final short FIELD_TYPE_STRING = 0x200;      // not implemented yet

    public static final double reduction = 9.32;     // update suggested by Loc Dao

    // private StringBuilder sb = new StringBuilder();
    // private String buffer = "";

    // private int count;
    // private long start;

    private boolean visible = true;
    public boolean leaveBluetoothOn = false;
    private boolean returnFromWidget = false;

    public static final Fields fields = Fields.getInstance();

    public static Device device = null;

    private static MainActivity instance = null;
    //private static Context context = null;

    public static boolean safeDrivingMode = true;
    public static boolean bluetoothBackgroundMode = false;
    public static boolean debugLogMode = false;
    public static boolean fieldLogMode = false;

    public static boolean dataExportMode = false;
    public static DataLogger dataLogger = null; // rather use singleton in onCreate

    public static int car = CAR_NONE;
    private static boolean isDriving = false;
    public static boolean milesMode = false;
    public static boolean altFieldsMode = false;

    public static final boolean storageIsAvailable = false;

    public static final short TOAST_NONE = 0;
    public static final short TOAST_ELM = 1;
    public static final short TOAST_ELMCAR = 2;
    public static int toastLevel = TOAST_NONE; // The lower toastlevel is set, the less messages come through

    private DebugListener debugListener = null;

    // private Fragment actualFragment;

    static private Resources res;
    private Resources.Theme localTheme;

    // bluetooth stuff
    private MenuItem bluetoothMenutItem = null;
    private final static int BLUETOOTH_DISCONNECTED = 21;
    private final static int BLUETOOTH_SEARCH = 22;
    private final static int BLUETOOTH_CONNECTED = 23;


    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected

                // only resume if this activity is also visible
                if (visible) {
                    // stop reading
                    if (device != null)
                        device.stopAndJoin();

                    // inform user
                    setTitle(TAG + " - disconnected");
                    setBluetoothState(BLUETOOTH_DISCONNECTED);
                    toast(R.string.toast_BluetoothLost);

                    // try to reconnect
                    onResume();
                }
            }
        }
    };

    public static MainActivity getInstance() {
        if (instance == null)
            /*  TODO I wonder if this is safe beavior. instance should never be null and if it is,
                something is pretty wrong and it is probably not a good plan to create a new object,
                unless I am missing something
             */
            instance = new MainActivity();
        return instance;
    }

    public static void debug(String text) {
        if (text == null) text = "null";
        Log.d(TAG, text);
        if (storageIsAvailable && debugLogMode) {
            SimpleDateFormat sdf = new SimpleDateFormat(getStringSingle(R.string.format_YMDHMSs), Locale.getDefault());
            DebugLogger.getInstance().log(sdf.format(Calendar.getInstance().getTime()) + ": " + text);
        }
    }

    // ***** Toasts *******

    public static void toast(int level, final String message) {
        // the lower the level is set, the higer the prio by convension. TOAST_NONE is the highest prio possible
        if (level > toastLevel) return;
        if (instance != null && !instance.isFinishing()) {
            instance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Context c = instance.getApplicationContext();
                        if (c != null) {
                            ToastCompat.makeText(c, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        // do nothing. getApplicationContext sometimes trips on a null pointer
                        // exception and when that happens, it's accepable to simplu give up
                        // on a transient toast message. An alternative approach might be touse
                        // https://www.dev2qa.com/android-get-application-context-from-anywhere-example/
                        // but it seems that overwriting Application is frowed upon a bit.
                    }

                }
            });
        }
    }

    private static void toast(final String message) {
        toast(TOAST_NONE, message);
    }

    public static void toast(int level, String format, Object... arguments) {
        String finalMessage = String.format(Locale.getDefault(), format, arguments);
        toast(level, finalMessage);
    }

    private static void toast(String format, Object... arguments) {
        toast(TOAST_NONE, format, arguments);
    }

    public static void toast(int level, final int resource) {
        final String finalMessage = getStringSingle(resource);
        toast(level, finalMessage);
    }

    private static void toast(final int resource) {
        toast(TOAST_NONE, resource);
    }

    private void loadSettings() {
        debug("MainActivity: loadSettings");
        try {
            SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE, 0);
            versionChangeCheck(settings);
            bluetoothDeviceName = settings.getString("deviceName", null);
            bluetoothDeviceAddress = settings.getString("deviceAddress", null);
            gatewayUrl = settings.getString("gatewayUrl", null);
            // String dataFormat = settings.getString("dataFormat", "crdt");
            String deviceType = settings.getString("device", "Arduino");
            safeDrivingMode = settings.getBoolean("optSafe", true);
            bluetoothBackgroundMode = settings.getBoolean("optBTBackground", false);
            milesMode = settings.getBoolean("optMiles", false);
            altFieldsMode = settings.getBoolean("optAltFields", false);
            dataExportMode = settings.getBoolean("optDataExport", false);
            debugLogMode = settings.getBoolean("optDebugLog", false);
            fieldLogMode = settings.getBoolean("optFieldLog", false);
            toastLevel = settings.getInt("optToast", 1);


            if (bluetoothDeviceName != null && !bluetoothDeviceName.isEmpty() && bluetoothDeviceName.length() > 4)
                BluetoothManager.getInstance().setDummyMode(bluetoothDeviceName.substring(0, 4).compareTo("HTTP") == 0);

            String carStr = settings.getString("car", "None");
            switch (carStr) {
                case "None":
                    car = CAR_NONE;
                    break;
                case "Zoé":
                case "ZOE":
                case "ZOE Q210":
                    car = CAR_ZOE_Q210;
                    break;
                case "ZOE R240":
                    car = CAR_ZOE_R240;
                    break;
                case "ZOE Q90":
                    car = CAR_ZOE_Q90;
                    break;
                case "ZOE R90":
                case "ZOE R90/110":
                    car = CAR_ZOE_R90;
                    break;
                //case "Fluence":
                //    car = CAR_FLUENCE;
                //    break;
                //case "Kangoo":
                //    car = CAR_KANGOO;
                //    break;
                //case "Twizy":
                //    car = CAR_TWIZY;
                 //   break;
                //case "X10":
                //    car = CAR_X10;
                //    break;
            }

            // as the settings may have changed, we need to reload different things

            // create a new device
            switch (deviceType) {
                case "Bob Due":
                case "CanSee":
                    device = new CanSee();
                    break;
                case "ELM327":
                    device = new ELM327();
                    break;
                case "ELM327Http":
                    device = new ELM327OverHttp();
                    break;
                default:
                    device = null;
                    break;
            }

            // since the car type may have changed, reload the frame timings and fields
            Frames.getInstance().load();
            fields.load();

            if (device != null) {
                // initialise the connection
                device.initConnection();

                // register application wide fields
                // registerApplicationFields(); // now done in Fields.load
            }

            // after loading PREFERENCES we may have new values for "dataExportMode"
            dataExportMode = dataLogger.activate(dataExportMode);
        } catch (Exception e) {
            MainActivity.debug(e.getMessage());
            for (StackTraceElement traceElement : e.getStackTrace()) {
                MainActivity.debug(traceElement.toString());
            }
        }
    }

    public void registerApplicationFields() {
        if (safeDrivingMode) {
            // speed
            Field field = fields.getBySID("5d7.0");
            if (field != null) {
                field.addListener(MainActivity.getInstance()); // callback is onFieldUpdateEvent
                if (device != null)
                    device.addApplicationField(field, 10000); // query every second
            }
        } else {
            Field field = fields.getBySID("5d7.0");
            if (field != null) {
                field.removeListener(MainActivity.getInstance());
                if (device != null)
                    device.removeApplicationField(field);
            }
        }
    }

    private void updateActionBar() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                actionBar.setIcon(R.mipmap.ic_launcher);
                break;
            case 1:
                actionBar.setIcon(R.mipmap.fragement_technical);
                break;
            case 2:
                actionBar.setIcon(R.mipmap.fragement_experimental);
                break;
            default:
                break;
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1234;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }




    private ViewPager viewPager;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // always create an instance
        instance = this;

        // needed to get strings from resources in non-Activity classes
        res = getResources();

        if (ContextCompat.checkSelfPermission(instance, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(instance, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        // dataLogger = DataLogger.getInstance();
        dataLogger = new DataLogger();

        debug("MainActivity: onCreate");

        //MainActivity.context = getApplicationContext();

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // navigation bar
        AppSectionsPagerAdapter appSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewPager = findViewById(R.id.main);
        viewPager.setAdapter(appSectionsPagerAdapter);
        // viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //actionBar.setSelectedNavigationItem(position);
                updateActionBar();
            }
        });
        updateActionBar();

        setTitle(TAG + " - not connected");
        setBluetoothState(BLUETOOTH_DISCONNECTED);

        // open the database
        CanzeDataSource.getInstance(getBaseContext()).open();
        // cleanup
        CanzeDataSource.getInstance().cleanUp();

        // setup cleaning (once every hour)
        // Extra check for non null CanzeDatasource instance
        Runnable cleanUpRunnable = new Runnable() {
            @Override
            public void run() {
                CanzeDataSource dsInstance = CanzeDataSource.getInstance();
                if (dsInstance != null) {
                    dsInstance.cleanUp();
                }
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(cleanUpRunnable, 60 * 1000);


        // register for bluetooth changes
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(broadcastReceiver, intentFilter);

        // configure Bluetooth manager
        BluetoothManager.getInstance().setBluetoothEvent(new BluetoothEvent() {
            @Override
            public void onBeforeConnect() {
                setBluetoothState(BLUETOOTH_SEARCH);
            }

            @Override
            public void onAfterConnect(BluetoothSocket bluetoothSocket) {
                device.init(visible);

                // set title
                debug("MainActivity: onAfterConnect > set title");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(TAG + " - connected to <" + bluetoothDeviceName + "@" + bluetoothDeviceAddress + ">");
                        setBluetoothState(BLUETOOTH_CONNECTED);
                    }
                });
            }

            @Override
            public void onBeforeDisconnect(BluetoothSocket bluetoothSocket) {
            }

            @Override
            public void onAfterDisconnect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(TAG + " - disconnected");
                    }
                });
            }
        });
        // detect hardware status
        int BT_STATE = BluetoothManager.getInstance().getHardwareState();
        if (BT_STATE == BluetoothManager.STATE_BLUETOOTH_NOT_AVAILABLE)
            toast("Sorry, but your device doesn't seem to have Bluetooth support!");
        else if (BT_STATE == BluetoothManager.STATE_BLUETOOTH_NOT_ACTIVE) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        // load settings
        // - includes the reader
        // - includes the decoder
        //loadSettings(); --> done in onResume

        // load fields from static code
        debug("Loaded fields: " + fields.size());

        // load fields
        //final SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE, 0);
        (new Thread(new Runnable() {
            @Override
            public void run() {
                debug("Loading fields last field values from database");
                for (int i = 0; i < fields.size(); i++) {
                    Field field = fields.get(i);
                    if (field != null)
                        field.setCalculatedValue(CanzeDataSource.getInstance().getLast(field.getSID()));
                    //debug("MainActivity: Setting "+field.getSID()+" = "+field.getValue());
                    //f.setValue(settings.getFloat(f.getUniqueID(), 0));
                }
                debug("Loading fields last field values from database (done)");
            }
        })).start();
    }


    @Override
    public void onResume() {
        debug("MainActivity: onResume");

        SharedPreferences set = getSharedPreferences(PREFERENCES_FILE, 0);
        if (set.getBoolean("optDark", false)) {
            //if (Build.VERSION.SDK_INT > 23)
            if(AppCompatDelegate.getDefaultNightMode()!=AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                if(Build.VERSION.SDK_INT < 28) {
                    finish();
                    startActivity(new Intent(MainActivity.this, MainActivity.this.getClass()));
                }
            }
        } else
        {

            if(AppCompatDelegate.getDefaultNightMode()!=AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                if(Build.VERSION.SDK_INT <28) {
                    finish();
                    startActivity(new Intent(MainActivity.this, MainActivity.this.getClass()));
                }
            }
        }

        instance = this; // If I am not mistaken, instance should only ever be populated in onCreate

        visible = true;
        super.onResume();

        // if returning from a single widget activity, we have to leave here!
        if (returnFromWidget) {
            returnFromWidget = false;
            return;
        }

        // remove progress spinners
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar pb;
                pb = findViewById(R.id.progressBar_cyclic0);
                if (pb != null) pb.setVisibility(View.GONE);
                pb = findViewById(R.id.progressBar_cyclic1);
                if (pb != null) pb.setVisibility(View.GONE);
                pb = findViewById(R.id.progressBar_cyclic2);
                if (pb != null) pb.setVisibility(View.GONE);
            }
        });

        if (!leaveBluetoothOn) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setBluetoothState(BLUETOOTH_DISCONNECTED);
                }
            });
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    reloadBluetooth();
                }
            })).start();
        }

        final SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE, 0);
        if (!settings.getBoolean("disclaimer", false)) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set title
            alertDialogBuilder.setTitle(R.string.prompt_Disclaimer);

            // set dialog message
            String yes = getStringSingle(R.string.prompt_Accept);
            String no = getStringSingle(R.string.prompt_Decline);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float width = size.x;
            float height = size.y;
            // int height = size.y;
            width = width / getResources().getDisplayMetrics().scaledDensity;
            height = height / getResources().getDisplayMetrics().scaledDensity;
            if (width <= 480 || height <= 480) {
                yes = getStringSingle(R.string.default_Yes);
                no = getStringSingle(R.string.default_No);
            }

            alertDialogBuilder
                    .setMessage(Html.fromHtml(getStringSingle(R.string.prompt_DisclaimerText)))
                    .setCancelable(true)
                    .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("disclaimer", true);
                            // editor.commit();
                            editor.apply();
                            // current activity
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(no,
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
            //alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
            //alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
        }
    }

    private void reloadBluetooth() {
        reloadBluetooth(true);
    }

    public void reloadBluetooth(boolean reloadSettings) {
        // re-load the settings if asked to
        if (reloadSettings)
            loadSettings();

        // try to get a new BT thread
        BluetoothManager.getInstance().connect(bluetoothDeviceAddress, true, BluetoothManager.RETRIES_INFINITE);
    }

    @Override
    public void onPause() {
        debug("MainActivity.onPause");
        debug("MainActivity.onPause > leaveBluetoothOn = " + leaveBluetoothOn);
        visible = false;

        // stop here if BT should stay on!
        if (bluetoothBackgroundMode) {
            super.onPause();
            return;
        }

        if (!leaveBluetoothOn) {
            if (device != null)
                device.clearFields();
            debug("MainActivity.onPause: stopping BT");
            stopBluetooth();
        }

        super.onPause();
    }

    private void stopBluetooth() {
        stopBluetooth(true);
    }

    public void stopBluetooth(boolean reset) {
        if (device != null) {
            // stop the device
            debug("MainActivity.stopBluetooth > stopAndJoin");
            device.stopAndJoin();
            // remove reference
            if (reset) {
                device.clearFields();
            }
        }
        // disconnect BT
        debug("MainActivity.stopBluetooth > BT disconnect");
        BluetoothManager.getInstance().disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.debug("MainActivity.onActivityResult");
        MainActivity.debug("MainActivity.onActivityResult > requestCode = " + requestCode);
        MainActivity.debug("MainActivity.onActivityResult > resultCode = " + resultCode);

        // this must be set in any case
        leaveBluetoothOn = false;

        if (requestCode == SETTINGS_ACTIVITY) {
            // load settings
            loadSettings();
        } else if (requestCode == LEAVE_BLUETOOTH_ON) {
            MainActivity.debug("MainActivity.onActivityResult > " + LEAVE_BLUETOOTH_ON);
            returnFromWidget = true;
            // register fields this activity needs
            /*
            registerFields();
             */
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        debug("MainActivity: onDestroy");

        dataLogger.destroy(); // clean up

        if (device != null) {
            // stop the device nicely
            device.stopAndJoin();
            device.clearFields();
        }
        // disconnect the bluetooth
        BluetoothManager.getInstance().disconnect();

        // un-register for bluetooth changes
        this.unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // get a reference to the bluetooth action button
        bluetoothMenutItem = menu.findItem(R.id.action_bluetooth);
        // and put the right view on it
        bluetoothMenutItem.setActionView(R.layout.animated_menu_item);
        // set the correct initial state
        setBluetoothState(BLUETOOTH_DISCONNECTED);
        // get access to the image view
        ImageView imageView = bluetoothMenutItem.getActionView().findViewById(R.id.animated_menu_item_action);
        // define an action
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        toast(getStringSingle(R.string.toast_Reconnecting));
                        stopBluetooth();
                        reloadBluetooth();
                    }
                })).start();
            }
        });

        return true;
    }


    private void setBluetoothState(int btState) {
        if (bluetoothMenutItem != null) {
            View view = bluetoothMenutItem.getActionView();
            if (view == null) return;
            final ImageView imageView = view.findViewById(R.id.animated_menu_item_action);

            // stop the animation if there is one running
            AnimationDrawable frameAnimation;
            if (imageView.getBackground() instanceof AnimationDrawable) {
                frameAnimation = (AnimationDrawable) imageView.getBackground();
                if (frameAnimation.isRunning())
                    frameAnimation.stop();
            }

            switch (btState) {
                case BLUETOOTH_DISCONNECTED:
                    imageView.setBackgroundResource(R.mipmap.bluetooth_none);
                    break;
                case BLUETOOTH_CONNECTED:
                    imageView.setBackgroundResource(R.mipmap.bluetooth_3);
                    break;
                case BLUETOOTH_SEARCH:
                    runOnUiThread(new Runnable() {
                        @SuppressLint("NewApi")
                        @Override
                        public void run() {
                            imageView.setBackgroundResource(R.drawable.animation_bluetooth);
                            AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();
                            frameAnimation.start();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // start the settings activity
        if (id == R.id.action_settings) {

            if (isSafe()) {
                // run a toast
                // toast(R.string.toast_WaitingSettings);

                // display the spinner
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBar pb = null;
                        if (viewPager.getCurrentItem() == 0) {
                            pb = findViewById(R.id.progressBar_cyclic0);
                        } else if (viewPager.getCurrentItem() == 1) {
                            pb = findViewById(R.id.progressBar_cyclic1);
                        } else if (viewPager.getCurrentItem() == 2) {
                            pb = findViewById(R.id.progressBar_cyclic2);
                        }
                        if (pb != null) pb.setVisibility(View.VISIBLE);
                    }
                });

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // give the toast a moment to appear
                        /* try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } */

                        if (device != null) {
                            // stop the BT device
                            device.stopAndJoin();
                            device.clearFields();
                            BluetoothManager.getInstance().disconnect();
                        }

                        // load the activity
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivityForResult(intent, SETTINGS_ACTIVITY);
                    }
                })).start();
                return true;
            }

            // see AppSectionsPagerAdapter for the right sequence
        } else if (id == R.id.action_main) {
            viewPager.setCurrentItem(0, true);
            updateActionBar();

        } else if (id == R.id.action_technical) {
            viewPager.setCurrentItem(1, true);
            updateActionBar();

        } else if (id == R.id.action_experimental) {
            viewPager.setCurrentItem(2, true);
            updateActionBar();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        if (field.getSID().equals("5d7.0")) {
            //debug("Speed "+field.getValue());
            isDriving = (field.getValue() > 10);
        }
    }

    public static boolean isSafe() {
        boolean safe = !isDriving || !safeDrivingMode;
        if (!safe) {
            Toast.makeText(MainActivity.instance, R.string.toast_NotWhileDriving, Toast.LENGTH_LONG).show();
        }
        return safe;
    }

    public static boolean isZOE() {
        return (car == CAR_ZOE_Q90 || car == CAR_ZOE_Q210 || car == CAR_ZOE_R90 || car == CAR_ZOE_R240);
        //car == CAR_X10 ||
    }

    //public static boolean isFluKan() {
    //    return (car == CAR_FLUENCE || car == CAR_KANGOO);
    //}

    //public static boolean isTwizy() {
    //    return (car == CAR_TWIZY);
    //}


    public static String getBluetoothDeviceAddress() {
        if ("HTTP Gateway".equals(bluetoothDeviceName))
            return gatewayUrl;
        return bluetoothDeviceAddress;
    }

    private void versionChangeCheck(SharedPreferences settings) {
        // get the current and the saved version of the app
        String previousVersion = settings.getString("appVersion", "");
        String currentVersion = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = pInfo.versionName;
        } catch (Exception e) {
            // ignore this error, currentVersion = ""
        }

        if (currentVersion.equals(previousVersion)) return;

        // this case statement contains optional code to move a previous instance of the app to the
        // current state
        switch (previousVersion) {
            case "":
            default:
                // clear database
                CanzeDataSource.getInstance().clear();
                break;
        }

        // if we successfully got the current version of the app, we save it in the preferences
        if (!currentVersion.equals("")) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("appVersion", currentVersion);
            editor.apply();
            finish();
        }
    }


    public static String getStringSingle(int resId) {
        if (res == null) return "";
        try {
            return res.getString(resId);
        } catch (Resources.NotFoundException e) {
            return "";
        }
    }

    public static String[] getStringList(int resId) {
        if (res == null) return null;
        try {
            return res.getStringArray(resId);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    public void setDebugListener(DebugListener debugListener) {
        this.debugListener = debugListener;
    }

    public void dropDebugMessage(String msg) {
        if (debugListener != null) debugListener.dropDebugMessage(msg);
    }

    public void appendDebugMessage(String msg) {
        if (debugListener != null) debugListener.appendDebugMessage(msg);
    }

    private int getScreenOrientation() {
        WindowManager wm = getWindowManager();
        if (wm == null) return Configuration.ORIENTATION_PORTRAIT;
        Display screenOrientation = wm.getDefaultDisplay();
        if (screenOrientation == null) return Configuration.ORIENTATION_PORTRAIT;
        if (screenOrientation.getWidth() == screenOrientation.getHeight()) {
            return Configuration.ORIENTATION_SQUARE;
        } else if (screenOrientation.getWidth() > screenOrientation.getHeight()) {
            return Configuration.ORIENTATION_LANDSCAPE;
        }
        return Configuration.ORIENTATION_PORTRAIT;
    }

    public boolean isLandscape() {
        return getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE;
    }

    public boolean isPortrait() {
        return getScreenOrientation() == Configuration.ORIENTATION_PORTRAIT;
    }

    public void setLocalTheme (Resources.Theme localTheme) {
        this.localTheme = localTheme;
    }

    public Resources.Theme getLocalTheme () {
        return this.localTheme;
    }

}

