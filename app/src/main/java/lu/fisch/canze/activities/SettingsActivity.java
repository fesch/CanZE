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

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.text.Html;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import lu.fisch.canze.BuildConfig;
import lu.fisch.canze.R;
import lu.fisch.canze.classes.Activity;
import lu.fisch.canze.classes.ActivityRegistry;
import lu.fisch.canze.database.CanzeDataSource;

public class SettingsActivity extends AppCompatActivity {

    //public static final int YES_NO_CALL = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings);

        // load settings
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);

        // dark mode
        //CheckBox darkMode = findViewById(R.id.darkMode);
        //darkMode.setChecked(settings.getBoolean("optDark",false));
        final Spinner darkMode = findViewById(R.id.darkMode);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("System");
        arrayAdapter.add("Dark");
        arrayAdapter.add("Light");
        darkMode.setAdapter(arrayAdapter);
        int darkModeSetting;
        try { // handle old boolean preference
            darkModeSetting = settings.getInt("optDark", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } catch (ClassCastException e) {
            darkModeSetting = settings.getBoolean("optDark", false) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        darkMode.setSelection(darkModeSetting == AppCompatDelegate.MODE_NIGHT_YES ? 1 : (darkModeSetting == AppCompatDelegate.MODE_NIGHT_NO ? 2 : 0));

        // device type
        String deviceTypeString = settings.getString("device", "ELM327");
        final Spinner deviceTypeSpinner = findViewById(R.id.deviceType);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("ELM327");
        arrayAdapter.add("CanSee");
        arrayAdapter.add("Http");
        deviceTypeSpinner.setAdapter(arrayAdapter);
        int index;
        if (deviceTypeString == null) deviceTypeString = "";
        switch (deviceTypeString) {
            case "ELM327Http":
            case "Http":
                index = 2;
                break;
            case "CanSee":
            case "Bob Due":
                index = 1;
                break;
            default:
                index = 0;
                break;
        }
        deviceTypeSpinner.setSelection(index);
        deviceTypeSpinner.setEnabled(false);

        // device list and address
        tryTofillDeviceList();
        final String deviceAddressString = settings.getString("deviceAddress", "");
        final String gatewayUrlString = settings.getString("gatewayUrl", "");
        final String deviceNameString = settings.getString("deviceName", "").toLowerCase();
        final EditText deviceAddressEditText = findViewById(R.id.editTextDeviceAddress);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(deviceAddressEditText.getWindowToken(), 0);
        final Spinner deviceListSpinner = findViewById(R.id.bluetoothDeviceList);
        if ("http gateway".equals(deviceNameString)) {
            //deviceAddress.setText("");
            //MainActivity.debug("Settings: gatewayUrl = " + deviceAddressString);
            deviceAddressEditText.setText(gatewayUrlString);
            deviceAddressEditText.setEnabled(true);
            deviceTypeSpinner.setSelection(2); // must be something ELM-ish
            deviceTypeSpinner.setEnabled(false);
        } else if (deviceNameString.startsWith("cansee")) {
            deviceAddressEditText.setText(deviceAddressString);
            deviceAddressEditText.setEnabled(false);
            deviceTypeSpinner.setSelection(1); // set CANsee
            deviceTypeSpinner.setEnabled(false);
        } else {
            deviceAddressEditText.setText(deviceAddressString);
            deviceAddressEditText.setEnabled(false);
            deviceTypeSpinner.setSelection(0); // must be something ELM-ish
            deviceTypeSpinner.setEnabled(false);
        }
        deviceListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String device = (String) deviceListSpinner.getSelectedItem();
                String[] pieces = device.split("\n");
                final CheckBox altFields = findViewById(R.id.altFieldsMode);
                //if(deviceList.getSelectedItemPosition()>=4){
                // if(device.substring(0,4).compareTo("HTTP") == 0){
                if ("http gateway".equals(pieces[0].toLowerCase())) {
                    //deviceAddress.setText("");
                    //MainActivity.debug("Settings: gatewayUrl = " + deviceAddressString);
                    deviceAddressEditText.setText(gatewayUrlString);
                    deviceAddressEditText.setEnabled(true);
                    deviceTypeSpinner.setSelection(2); // must be something ELM-ish
                    deviceTypeSpinner.setEnabled(false);
                } else if (pieces[0].toLowerCase().startsWith("cansee")) {
                    deviceAddressEditText.setText(pieces[1]);
                    deviceAddressEditText.setEnabled(false);
                    deviceTypeSpinner.setSelection(1); // set CANsee
                    deviceTypeSpinner.setEnabled(false);
                    altFields.setChecked(false);
                } else {
                    deviceAddressEditText.setText(pieces[1]);
                    deviceAddressEditText.setEnabled(false);
                    deviceTypeSpinner.setSelection(0); // must be something ELM-ish
                    deviceTypeSpinner.setEnabled(false);
                    altFields.setChecked(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // fill cars
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        //arrayAdapter.add("Zoé");
        arrayAdapter.add("ZOE Q210");
        arrayAdapter.add("ZOE R240");
        arrayAdapter.add("ZOE Q90");
        arrayAdapter.add("ZOE R90/110");
        if (BuildConfig.VERSION_NAME.contains("beta")) arrayAdapter.add("ZOE ZE50");
        //arrayAdapter.add("Fluence");
        //arrayAdapter.add("Kangoo");
        //arrayAdapter.add("Twizy");

        index = 0; // assume default MainActivity.CAR_ZOE_Q210
        if (MainActivity.car == MainActivity.CAR_ZOE_R240) index = 1;
        else if (MainActivity.car == MainActivity.CAR_ZOE_Q90) index = 2;
        else if (MainActivity.car == MainActivity.CAR_ZOE_R90) index = 3;
        else if (MainActivity.car == MainActivity.CAR_X10PH2 && BuildConfig.VERSION_NAME.contains("beta")) index = 4;

        // display the list
        Spinner carList = findViewById(R.id.car);
        carList.setAdapter(arrayAdapter);
        // select the actual device
        carList.setSelection(index);
        carList.setSelected(true);

        // fill Toastlevel
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("None");
        arrayAdapter.add("Device");
        arrayAdapter.add("All");

        if (MainActivity.toastLevel == MainActivity.TOAST_ELM) index = 1;
        else if (MainActivity.toastLevel == MainActivity.TOAST_ELMCAR) index = 2;
        else index = 0; // assume Fields.TOAST_NONE)

        // display the list
        Spinner toastList = findViewById(R.id.toastLevel);
        toastList.setAdapter(arrayAdapter);
        // select the actual device
        toastList.setSelection(index);
        toastList.setSelected(true);

        // fill startup Activity

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("-- Main --");              // 0
        for (int i = 0; i <= 6; i++) arrayAdapter.add(ActivityRegistry.getInstance().getById(i).getTitle());
        arrayAdapter.add("-- Technical --");        // 7
        for (int i = 7; i <= 20; i++) arrayAdapter.add(ActivityRegistry.getInstance().getById(i).getTitle());
        arrayAdapter.add("-- Experimental --");     // 22
        for (int i = 20; i <= 22; i++) arrayAdapter.add(ActivityRegistry.getInstance().getById(i).getTitle());
        arrayAdapter.add("-- Custom --");           // 26
        Activity a = null;
        try {
            a = ActivityRegistry.getInstance().getById(settings.getInt("startActivity", -1));
        } catch (Exception e) {
            // do nothing
        }
        String activityName = (a != null) ? a.getTitle() : "";
        if (activityName.equals("")) {
            switch (settings.getInt("startMenu", -1)) {
                case 0: activityName = "-- Main --"; break;
                case 1: activityName = "-- Technical --"; break;
                case 2: activityName = "-- Experimental --"; break;
            }
        }
        final Spinner activityListSpinner = findViewById(R.id.startActivity);
        activityListSpinner.setAdapter(arrayAdapter);
        activityListSpinner.setSelection(arrayAdapter.getPosition(activityName));
        activityListSpinner.setSelected(true);

        // options
        final CheckBox safe = findViewById(R.id.safeDrivingMode);
        safe.setChecked(MainActivity.safeDrivingMode);
        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!safe.isChecked()) {

                    // set dialog message
                    String yes = MainActivity.getStringSingle(R.string.prompt_YesIKnow);
                    String no = MainActivity.getStringSingle(R.string.prompt_NoSecureWay);

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    float width = size.x;
                    //int height = size.y;
                    width = width / getResources().getDisplayMetrics().density * getResources().getDisplayMetrics().scaledDensity;
                    if (width <= 480) {
                        yes = MainActivity.getStringSingle(R.string.default_Yes);
                        no = MainActivity.getStringSingle(R.string.default_No);
                    }

                    final Context context = SettingsActivity.this;
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle(R.string.prompt_Attention);

                    // set dialog message
                    alertDialogBuilder
                            .setMessage(MainActivity.getStringSingle(R.string.prompt_WarningDriving))
                            .setCancelable(true)
                            .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(no,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // if this button is clicked, just close
                                            // the dialog box and do nothing
                                            safe.setChecked(true);
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }
        });

        final CheckBox miles = findViewById(R.id.milesMode);
        miles.setChecked(MainActivity.milesMode);

        final CheckBox altFields = findViewById(R.id.altFieldsMode);
        altFields.setChecked(MainActivity.altFieldsMode);

        final CheckBox btBackground = findViewById(R.id.btBackgrounding);
        btBackground.setChecked(MainActivity.bluetoothBackgroundMode);
        btBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btBackground.isChecked()) {
                    // set dialog message
                    String yes = MainActivity.getStringSingle(R.string.prompt_YesIKnow);
                    String no = MainActivity.getStringSingle(R.string.prompt_NoThanks);

                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    float width = size.x;
                    //int height = size.y;
                    width = width / getResources().getDisplayMetrics().scaledDensity;
                    if (width <= 480) {
                        yes = MainActivity.getStringSingle(R.string.default_Yes);
                        no = MainActivity.getStringSingle(R.string.default_No);
                    }

                    final Context context = SettingsActivity.this;
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle("ATTENTION");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage(Html.fromHtml(MainActivity.getStringSingle(R.string.prompt_BluetoothOn)))
                            .setCancelable(true)
                            .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(no,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // if this button is clicked, just close
                                            // the dialog box and do nothing
                                            btBackground.setChecked(false);
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }
        });

        final CheckBox dataExport = findViewById(R.id.dataExportMode);
        dataExport.setChecked(MainActivity.dataExportMode);
        dataExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add code here to check external SDcard is avail, writeable and has sufficient space
                if (!MainActivity.getInstance().isExternalStorageWritable()) {
                    final Context context = SettingsActivity.this;
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle(R.string.prompt_Sorry);

                    // set dialog message
                    alertDialogBuilder
                            .setMessage(Html.fromHtml(MainActivity.getStringSingle(R.string.prompt_NoSd)))
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    dataExport.setChecked(false);
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    MainActivity.dataExportMode = false; // due to SDcard not writeable
                }
            }
        });

        final CheckBox debugLog = findViewById(R.id.debugLogMode);
        debugLog.setChecked(MainActivity.debugLogMode);
        debugLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add code here to check external SDcard is avail, writeable and has sufficient space
                final boolean sdcardCheck = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()); // check for space later
                if (!sdcardCheck) {
                    final Context context = SettingsActivity.this;
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle(R.string.prompt_Sorry);

                    // set dialog message
                    alertDialogBuilder
                            .setMessage(Html.fromHtml(MainActivity.getStringSingle(R.string.prompt_NoSd)))
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    debugLog.setChecked(false);
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }
        });

        final CheckBox fieldLog = findViewById(R.id.fieldLogMode);
        fieldLog.setChecked(MainActivity.fieldLogMode);
        fieldLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add code here to check external SDcard is avail, writeable and has sufficient space
                final boolean sdcardCheck = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()); // check for space later
                if (!sdcardCheck) {
                    final Context context = SettingsActivity.this;
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle(R.string.prompt_Sorry);

                    // set dialog message
                    alertDialogBuilder
                            .setMessage(Html.fromHtml(MainActivity.getStringSingle(R.string.prompt_NoSd)))
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    fieldLog.setChecked(false);
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }
        });

        // display build version
        TextView tv = findViewById(R.id.build);
        try {
        /*  ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd @ HH:mm");
            String s = sdf.format(new java.util.Date(time));
            zf.close(); */

            Date buildDate = new Date(BuildConfig.TIMESTAMP);
            SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHM), Locale.getDefault());
            String version = MainActivity.getStringSingle(R.string.version) + BuildConfig.VERSION_NAME + " (" + BuildConfig.BUILD_TYPE + "-" + BuildConfig.BRANCH + ") " + MainActivity.getStringSingle(R.string.build) + sdf.format(buildDate);
            tv.setText(version);

        } catch (Exception e) {
            MainActivity.logExceptionToCrashlytics(e);
        }

        Button button = findViewById(R.id.buttonClearSettings);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear preferences file
                SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                // editor.commit();
                editor.apply();

                // clear data file
                settings = getSharedPreferences(MainActivity.DATA_FILE, 0);
                editor = settings.edit();
                editor.clear();
                // editor.commit();
                editor.apply();

                // clear database
                CanzeDataSource cds = CanzeDataSource.getInstance();
                if (cds != null) CanzeDataSource.getInstance().clear();

                MainActivity.fields.clearAllFields();
                MainActivity.toast(MainActivity.TOAST_NONE, R.string.toast_CacheCleared);
            }
        });

        button = findViewById(R.id.buttonCustomFragment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, SettingsCustomActivity.class);
                startActivity(i);
            }
        });

        final Button logButton = findViewById(R.id.logButton);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, LoggingActivity.class);
                startActivityForResult(intent, 4);
            }
        });
        logButton.setVisibility(View.INVISIBLE);

        final Button btsCanSee = findViewById(R.id.btnCanSee);
        btsCanSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, CanSeeActivity.class);
                startActivityForResult(intent, 4);
            }
        });
        btsCanSee.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        // MainActivity.toast(MainActivity.TOAST_NONE, R.string.toast_PleaseUseTop);
        saveSettings();
        MainActivity.getInstance().handleDarkMode();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==android.R.id.home) {
            saveSettings();
            MainActivity.getInstance().handleDarkMode();
            finish();
            return true;
        } else if (id == R.id.action_cancel) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.REQUEST_ENABLE_BT) {
            fillDeviceList();
        }

        MainActivity.debug("Code = " + requestCode);
    }

    private void tryTofillDeviceList() {
        // get the bluetooth adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            MainActivity.toast(MainActivity.TOAST_NONE, R.string.toast_NoBluetooth);
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                // launch the system activity
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
            }
        }
        fillDeviceList(); // if no BT, still allow the http devices
    }

    private void fillDeviceList() {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        String deviceAddress = settings.getString("deviceAddress", null);
        String deviceName = settings.getString("deviceName", "");
        MainActivity.debug("SELECT: deviceAddress = " + deviceAddress);
        MainActivity.debug("SELECT: deviceName = " + deviceName);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        int index = -1;
        int i = 0;

        // get the bluetooth adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            // get the devices
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            // if there are paired devices
            if (pairedDevices.size() > 0) {
                // loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // add the name and address to an array adapter to show in a ListView
                    // see https://stackoverflow.com/questions/20658142/getting-the-renamed-name-of-an-android-bluetoothdevice
                    String deviceAlias = device.getName();
                    try {
                        // getAliasName is preferred as it returns the user naming. This simplifies
                        // identification if having several dongles, so you can name them "KONNWEI", "blue"
                        // etcetera in Android's Bluetooth settings.
                        // this will lint warning as getAliasName has @hide set.
                        Method method = device.getClass().getMethod("getAliasName");
                        // getMethod is supposed never to return null, but raise an exception instead
                        //if (method != null) {
                        deviceAlias = (String) method.invoke(device);
                        //}
                    } catch (Exception e) {
                        // do nothing. Trapping here is no problem, as we already have the name
                    }

                    arrayAdapter.add(deviceAlias + "\n" + device.getAddress());
                    // get the index of the selected item
                    //if(device.getAddress().equals(deviceAddress))
                    if (deviceAlias != null && deviceAlias.equals(deviceName)) {
                        index = i; // plus one as HTTP is always first in list
                        //MainActivity.debug("SELECT: found = "+i+" ("+deviceAlias+")");
                    }
                    i++;
                }

            }
        }

        arrayAdapter.add("HTTP Gateway\n-");
        if ("HTTP Gateway".equals(deviceName))
            index = i;
        //i++;

        // display the list
        Spinner deviceList = findViewById(R.id.bluetoothDeviceList);
        deviceList.setAdapter(arrayAdapter);

        // select the actual device
        deviceList.setSelection(index, false);
        deviceList.setSelected(true);
    }

    private void saveSettings () {
        // save settings
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        Spinner remoteDevice = findViewById(R.id.bluetoothDeviceList);
        Spinner deviceType = findViewById(R.id.deviceType);
        Spinner car = findViewById(R.id.car);
        CheckBox safe = findViewById(R.id.safeDrivingMode);
        CheckBox miles = findViewById(R.id.milesMode);
        CheckBox altFields = findViewById(R.id.altFieldsMode);
        CheckBox dataExport = findViewById(R.id.dataExportMode);
        CheckBox debugLog = findViewById(R.id.debugLogMode);
        CheckBox fieldLog = findViewById(R.id.fieldLogMode);
        CheckBox btBackground = findViewById(R.id.btBackgrounding);
        Spinner darkMode = findViewById(R.id.darkMode);
        Spinner toastLevel = findViewById(R.id.toastLevel);
        Spinner startActivitySpinner = findViewById(R.id.startActivity);
        EditText deviceAddress = findViewById(R.id.editTextDeviceAddress);
        if (remoteDevice.getSelectedItem() != null) {
            MainActivity.debug("Settings.deviceAddress = " + remoteDevice.getSelectedItem().toString().split("\n")[1].trim());
            MainActivity.debug("Settings.deviceName = " + remoteDevice.getSelectedItem().toString().split("\n")[0].trim());
            //editor.putString("deviceAddress", deviceList.getSelectedItem().toString().split("\n")[1].trim());
            final String deviceNameString = remoteDevice.getSelectedItem().toString().split("\n")[0].trim();
            editor.putString("deviceName", deviceNameString);
            if ("http gateway".equals(deviceNameString.toLowerCase())) {
                editor.putString("gatewayUrl", String.valueOf(deviceAddress.getText()));
            } else {
                editor.putString("deviceAddress", String.valueOf(deviceAddress.getText()));
            }
            editor.putString("device", deviceType.getSelectedItem().toString().trim());
            editor.putString("car", car.getSelectedItem().toString().split("\n")[0].trim());
            editor.putBoolean("optBTBackground", btBackground.isChecked());
            editor.putBoolean("optSafe", safe.isChecked());
            editor.putBoolean("optMiles", miles.isChecked());
            editor.putBoolean("optAltFields", altFields.isChecked());
            editor.putInt("optDark", darkMode.getSelectedItem().toString().equals("Dark") ? AppCompatDelegate.MODE_NIGHT_YES : (darkMode.getSelectedItem().toString().equals("Light") ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
            editor.putBoolean("optDataExport", dataExport.isChecked());
            editor.putBoolean("optDebugLog", debugLog.isChecked());
            editor.putBoolean("optFieldLog", fieldLog.isChecked());
            editor.putInt("optToast", toastLevel.getSelectedItemPosition());
            final Activity a = ActivityRegistry.getInstance().getByTitle(startActivitySpinner.getSelectedItem().toString());
            editor.putInt("startActivity", (a != null ? a.getId() : -1));
            final int position = startActivitySpinner.getSelectedItemPosition();
            if (position <= 6)
                editor.putInt("startMenu", 0);
            else if (position <= 21)
                editor.putInt("startMenu", 1);
            else if (position <= 25)
                editor.putInt("startMenu", 2);
            else if (position <= 26)
                editor.putInt("startMenu", 3);
            else
                editor.putInt("startMenu", -1);

            /*if(darkMode.isChecked())
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }*/

        }
        // editor.commit();
        editor.apply();

    }
}
