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

import android.content.pm.PackageInfo;
import android.os.Environment;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Fields;

public class SettingsActivity extends AppCompatActivity {

    public static final int YES_NO_CALL = 13;

    // Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tryTofillDeviceList();

        // load settings
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        String device=settings.getString("device", "Arduino");

        // fill devices
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        arrayAdapter.add("ELM327");
        //arrayAdapter.add("Arduino Due");
        arrayAdapter.add("Bob Due");
        //arrayAdapter.add("ELM327 Experimental");

        int index = 0;
        if(device.equals("ELM327")) index=0;
        //else if(device.equals("Arduino Due")) index=1;
        else if(device.equals("Bob Due")) index=1;
        //else if(device.equals("ELM327 Experimental")) index=3;

        // display the list
        Spinner deviceList = (Spinner) findViewById(R.id.remoteDevice);
        deviceList.setAdapter(arrayAdapter);
        // select the actual device
        deviceList.setSelection(index);
        deviceList.setSelected(true);


        // fill cars
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        arrayAdapter.add("Zoé");
        arrayAdapter.add("Fluence");
        arrayAdapter.add("Kangoo");
        arrayAdapter.add("X10");

        index = 0;
        int car = Fields.getInstance().getCar();
        if(car==Fields.CAR_ZOE) index=0;
        else if(car==Fields.CAR_FLUENCE) index=1;
        else if(car==Fields.CAR_KANGOO) index=2;
        else if(car==Fields.CAR_X10) index=3;

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
                    final Context context = SettingsActivity.this;
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle("ATTENTION");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Driving and not paying full attention to traffic is extremely dangerous " +
                                    "and will put your life and the life of those around you at risk. " +
                                    "Disabling of this mode is not recommended at all!\n\n" +
                                    "Are you sure you want to continue disabling the Safe Driving Mode?")
                            .setCancelable(true)
                            .setPositiveButton("Yes, I know what I'm doing", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("No, I prefer the secure way",
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

        final CheckBox dataexport = (CheckBox) findViewById(R.id.dataexportMode);
        dataexport.setChecked(MainActivity.dataexportMode);
        dataexport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add code here to check external SDcard is avail, writeable and has sufficient space
                final boolean sdcardCheck = isExternalStorageWritable(); // check for space later
                if (!sdcardCheck) {
                    final Context context = SettingsActivity.this;
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle("I am sorry...");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("External SDcard not available " +
                                    "or not writeable " +
                                    "or has not sufficient space left to log data\n\n" +
                                    "Data export cannot be enabled")
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    dataexport.setChecked(false);
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
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd @ HH:mm");
            String s = sdf.format(new java.util.Date(time));

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            tv.setText("Version: "+version+"  //  Build: "+s);
            zf.close();
        }
        catch(Exception e){
        }

        Button button = (Button) findViewById(R.id.buttonClearSettings);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();


                settings = getSharedPreferences(MainActivity.DATA_FILE, 0);
                editor = settings.edit();
                editor.clear();
                editor.commit();

                MainActivity.fields.clearAllFields();
                MainActivity.toast("Cache has been cleared ...");
            }
        });
    }

    @Override
    public void onBackPressed() {
        MainActivity.toast("Please use one of the top buttons to quit the settings ...");
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
            Spinner deviceList = (Spinner) findViewById(R.id.bluetoothDeviceList);
            Spinner device = (Spinner) findViewById(R.id.remoteDevice);
            Spinner car = (Spinner) findViewById(R.id.car);
            CheckBox safe = (CheckBox) findViewById(R.id.safeDrivingMode);
            CheckBox miles = (CheckBox) findViewById(R.id.milesMode);
            CheckBox dataexport = (CheckBox) findViewById(R.id.dataexportMode);
            Spinner toastLevel = (Spinner) findViewById(R.id.toastLevel);
            if(deviceList.getSelectedItem()!=null) {
                MainActivity.debug("Settings.deviceAddress = " + deviceList.getSelectedItem().toString().split("\n")[1].trim());
                MainActivity.debug("Settings.deviceName = " + deviceList.getSelectedItem().toString().split("\n")[0].trim());
                editor.putString("deviceAddress", deviceList.getSelectedItem().toString().split("\n")[1].trim());
                editor.putString("deviceName", deviceList.getSelectedItem().toString().split("\n")[0].trim());
                editor.putString("device", device.getSelectedItem().toString().split("\n")[0].trim());
                editor.putString("car", car.getSelectedItem().toString().split("\n")[0].trim());
                editor.putBoolean("optSafe", safe.isChecked());
                editor.putBoolean("optMiles", miles.isChecked());
                editor.putBoolean("optDataExport", dataexport.isChecked());
                editor.putInt("optToast", toastLevel.getSelectedItemPosition());
            }
            editor.commit();
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
            Toast.makeText(SettingsActivity.this, "This device does not have any bluetooth adapter.\n\nSorry...", Toast.LENGTH_SHORT).show();
            return;
        }
        // test if enabled ...
        if (!bluetoothAdapter.isEnabled())
        {
            // launch the system activity
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
        }
        else
        {
            // fill the list
            fillDeviceList();
        }
    }

    private void fillDeviceList()
    {
        // get the bluetooth adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // get the devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // if there are paired devices
        if (pairedDevices.size() > 0)
        {
            SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
            String deviceAddress=settings.getString("deviceAddress", null);

            ArrayAdapter arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
            // loop through paired devices
            int i = 0;
            int index=-1;
            for (BluetoothDevice device : pairedDevices) {
                // add the name and address to an array adapter to show in a ListView

                String deviceAlias = device.getName();
                try {
                    Method method = device.getClass().getMethod("getAliasName");
                    if(method != null) {
                        deviceAlias = (String)method.invoke(device);
                    }
                } catch (NoSuchMethodException e) {
                    // e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // e.printStackTrace();
                }

                arrayAdapter.add(deviceAlias + "\n" + device.getAddress());
                // get the index of the selected item
                if(device.getAddress().equals(deviceAddress))
                    index=i;
                i++;
            }
            // display the list
            Spinner deviceList = (Spinner) findViewById(R.id.bluetoothDeviceList);
            deviceList.setAdapter(arrayAdapter);
            // select the actual device
            deviceList.setSelection(index);
            deviceList.setSelected(true);
        }
    }
}
