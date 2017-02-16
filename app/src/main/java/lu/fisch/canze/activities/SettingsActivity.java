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
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.database.CanzeDataSource;

import static lu.fisch.canze.activities.MainActivity.toast;

// import java.util.zip.ZipEntry;
// import java.util.zip.ZipFile;

public class SettingsActivity extends AppCompatActivity {

    //public static final int YES_NO_CALL = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tryTofillDeviceList();

        // load settings
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        String remoteDevice = settings.getString("device", "ELM327");

        // device address
        final EditText deviceAddress = (EditText) findViewById(R.id.editTextDeviceAddress);

        // remote Device
        final Spinner deviceType = (Spinner) findViewById(R.id.deviceType);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        arrayAdapter.add("ELM327");
        arrayAdapter.add("Bob Due");
        //arrayAdapter.add("ELM327Http");
        deviceType.setAdapter(arrayAdapter);

        if("HTTP Gateway".equals(remoteDevice)) {
            deviceAddress.setText(settings.getString("gatewayUrl",""));
            deviceAddress.setEnabled(true);
            deviceType.setEnabled(false);
        } else {
            deviceAddress.setText(settings.getString("deviceAddress",""));
            deviceAddress.setEnabled(false);
            int index = 0;
            switch (remoteDevice) {
                case "ELM327":
                    index = 0;
                    break;
                case "Bob Due":
                    index = 1;
                    break;
            }
            deviceType.setSelection(index);
            deviceType.setEnabled(true);
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(deviceAddress.getWindowToken(), 0);

        final String gatewayUrl = settings.getString("gatewayUrl", "");
        final Spinner deviceList = (Spinner) findViewById(R.id.bluetoothDeviceList);
        deviceList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String device = (String) deviceList.getSelectedItem();
                String[] pieces = device.split("\n");
                //if(deviceList.getSelectedItemPosition()>=4){
                // if(device.substring(0,4).compareTo("HTTP") == 0){
                if("HTTP Gateway".equals(pieces[0])) {
                    //deviceAddress.setText("");
                    MainActivity.debug("Settings: gatewayUrl = "+gatewayUrl);
                    deviceAddress.setText(gatewayUrl);
                    deviceAddress.setEnabled(true);
                    deviceType.setEnabled(false);
                }
                else {
                    //String device = (String) deviceList.getSelectedItem();
                    deviceAddress.setText(pieces[1]);
                    deviceAddress.setEnabled(false);
                    deviceType.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // fill cars
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        //arrayAdapter.add("Zoé");
        arrayAdapter.add("ZOE Q210");
        arrayAdapter.add("ZOE R240");
        arrayAdapter.add("ZOE Q90");
        arrayAdapter.add("ZOE R90");
        arrayAdapter.add("Fluence");
        arrayAdapter.add("Kangoo");
        arrayAdapter.add("X10");

        int index = 0;
        if(MainActivity.car==MainActivity.CAR_ZOE_Q210) index=0;
        else if (MainActivity.car == MainActivity.CAR_ZOE_R240) index = 1;
        else if (MainActivity.car == MainActivity.CAR_ZOE_Q90) index = 2;
        else if (MainActivity.car == MainActivity.CAR_ZOE_R90) index = 3;
        else if(MainActivity.car==MainActivity.CAR_FLUENCE) index=4;
        else if(MainActivity.car==MainActivity.CAR_KANGOO) index=5;
        else if(MainActivity.car==MainActivity.CAR_X10) index=6;

        // display the list
        Spinner carList = (Spinner) findViewById(R.id.car);
        carList.setAdapter(arrayAdapter);
        // select the actual device
        carList.setSelection(index);
        carList.setSelected(true);

        // fill Toastlevel
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        arrayAdapter.add("None");
        arrayAdapter.add("Only device");
        arrayAdapter.add("All");

        index = 0;
        if(MainActivity.toastLevel==Fields.TOAST_NONE) index=0;
        else if(MainActivity.toastLevel==Fields.TOAST_DEVICE) index=1;
        else if(MainActivity.toastLevel==Fields.TOAST_ALL) index=2;

        // display the list
        Spinner toastList = (Spinner) findViewById(R.id.toastLevel);
        toastList.setAdapter(arrayAdapter);
        // select the actual device
        toastList.setSelection(index);
        toastList.setSelected(true);

        // options
        final CheckBox safe = (CheckBox) findViewById(R.id.safeDrivingMode);
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

        final CheckBox miles = (CheckBox) findViewById(R.id.milesMode);
        miles.setChecked(MainActivity.milesMode);

        final CheckBox btBackground = (CheckBox) findViewById(R.id.btBackgrounding);
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

        final CheckBox dataExport = (CheckBox) findViewById(R.id.dataExportMode);
        dataExport.setChecked(MainActivity.dataExportMode);
        dataExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add code here to check external SDcard is avail, writeable and has sufficient space
                if ( !MainActivity.dataLogger.isExternalStorageWritable() ) {
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

        final CheckBox debugLog = (CheckBox) findViewById(R.id.debugLogMode);
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

        final CheckBox fieldLog = (CheckBox) findViewById(R.id.fieldLogMode);
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
        TextView tv = (TextView) findViewById(R.id.build);
        try{
        /*  ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd @ HH:mm");
            String s = sdf.format(new java.util.Date(time));
            zf.close(); */

            Date buildDate = new Date(BuildConfig.TIMESTAMP);
            SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHM), Locale.getDefault());
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tv.setText(MainActivity.getStringSingle(R.string.version)+pInfo.versionName+"  //  " + MainActivity.getStringSingle(R.string.build)+sdf.format(buildDate));

        } catch(Exception e) {
            e.printStackTrace();
        }

        Button button = (Button) findViewById(R.id.buttonClearSettings);
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
                CanzeDataSource.getInstance().clear();

                MainActivity.fields.clearAllFields();
                toast(MainActivity.getStringSingle(R.string.toast_CacheCleared));
            }
        });
    }

    @Override
    public void onBackPressed() {
        toast(MainActivity.getStringSingle(R.string.toast_PleaseUseTop));
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

        if (id == R.id.action_ok) {
            // save settings
            SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
            SharedPreferences.Editor editor = settings.edit();
            Spinner remoteDevice = (Spinner) findViewById(R.id.bluetoothDeviceList);
            Spinner deviceType = (Spinner) findViewById(R.id.deviceType);
            Spinner car = (Spinner) findViewById(R.id.car);
            CheckBox safe = (CheckBox) findViewById(R.id.safeDrivingMode);
            CheckBox miles = (CheckBox) findViewById(R.id.milesMode);
            CheckBox dataExport = (CheckBox) findViewById(R.id.dataExportMode);
            CheckBox debugLog = (CheckBox) findViewById(R.id.debugLogMode);
            CheckBox fieldLog = (CheckBox) findViewById(R.id.fieldLogMode);
            CheckBox btBackground = (CheckBox) findViewById(R.id.btBackgrounding);
            Spinner toastLevel = (Spinner) findViewById(R.id.toastLevel);
            EditText deviceAddress = (EditText) findViewById(R.id.editTextDeviceAddress);
            if(remoteDevice.getSelectedItem()!=null) {
                MainActivity.debug("Settings.deviceAddress = " + remoteDevice.getSelectedItem().toString().split("\n")[1].trim());
                MainActivity.debug("Settings.deviceName = " + remoteDevice.getSelectedItem().toString().split("\n")[0].trim());
                //editor.putString("deviceAddress", deviceList.getSelectedItem().toString().split("\n")[1].trim());
                String deviceNameString = remoteDevice.getSelectedItem().toString().split("\n")[0].trim();
                editor.putString("deviceName", deviceNameString);
                editor.putString("deviceAddress", String.valueOf(deviceAddress.getText()));
                if("HTTP Gateway".equals(deviceNameString)){
                    editor.putString("gatewayUrl", String.valueOf(deviceAddress.getText()));
                    editor.putString("device", "ELM327Http");
                } else {
                    editor.putString("device", deviceType.getSelectedItem().toString().trim());
                }
                editor.putString("car", car.getSelectedItem().toString().split("\n")[0].trim());
                editor.putBoolean("optBTBackground", btBackground.isChecked());
                editor.putBoolean("optSafe", safe.isChecked());
                editor.putBoolean("optMiles", miles.isChecked());
                editor.putBoolean("optDataExport", dataExport.isChecked());
                editor.putBoolean("optDebugLog", debugLog.isChecked());
                editor.putBoolean("optFieldLog", fieldLog.isChecked());
                editor.putInt("optToast", toastLevel.getSelectedItemPosition());
            }
            // editor.commit();
            editor.apply();
            // finish
            finish();
            return true;
        }
        else if (id == R.id.action_cancel) {
            // finish without saving the settings
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==MainActivity.REQUEST_ENABLE_BT)
        {
            fillDeviceList();
        }

        MainActivity.debug("Code = "+requestCode);
    }

    private void tryTofillDeviceList() {
        // get the bluetooth adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            MainActivity.toast(R.string.toast_NoBluetooth);
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                // launch the system activity
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
            }
        }
        fillDeviceList(); // if no BT, still allow the http devices
    }

    private void fillDeviceList()
    {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        String deviceAddress=settings.getString("deviceAddress", null);
        String deviceName=settings.getString("deviceName", null);
        MainActivity.debug("SELECT: deviceAddress = "+deviceAddress);
        MainActivity.debug("SELECT: deviceName = "+deviceName);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        int index=-1;
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

                    String deviceAlias = device.getName();
                    try {
                        Method method = device.getClass().getMethod("getAliasName");
                        if (method != null) {
                            deviceAlias = (String) method.invoke(device);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        //} catch (InvocationTargetException e) {
                        // e.printStackTrace();
                        //} catch (IllegalAccessException e) {
                        // e.printStackTrace();
                    }

                    arrayAdapter.add(deviceAlias + "\n" + device.getAddress());
                    // get the index of the selected item
                    //if(device.getAddress().equals(deviceAddress))
                    if (deviceAlias.equals(deviceName)) {
                        index = i; // plus one as HTTP is always first in list
                        //MainActivity.debug("SELECT: found = "+i+" ("+deviceAlias+")");
                    }
                    i++;
                }

            }
        }

        arrayAdapter.add("HTTP Gateway\n-");
        if("HTTP Gateway".equals(deviceName))
            index = i;
        //i++;

        // display the list
        Spinner deviceList = (Spinner) findViewById(R.id.bluetoothDeviceList);
        deviceList.setAdapter(arrayAdapter);

        // select the actual device
        deviceList.setSelection(index);
        deviceList.setSelected(true);
    }
}
