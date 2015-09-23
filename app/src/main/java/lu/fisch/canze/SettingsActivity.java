package lu.fisch.canze;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import lu.fisch.canze.actors.Fields;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tryTofillDeviceList();

        // load settings
        SharedPreferences settings = getSharedPreferences("lu.fisch.canze.settings", 0);
        String device=settings.getString("device", "Arduino");

        // fill devices
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        arrayAdapter.add("ELM327");
        arrayAdapter.add("Arduino Due");
        arrayAdapter.add("Bob Due");

        int index = 0;
        if(device.equals("ELM327")) index=0;
        else if(device.equals("Arduino Due")) index=1;
        else if(device.equals("Bob Due")) index=2;

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
            SharedPreferences settings = getSharedPreferences("lu.fisch.canze.settings", 0);
            SharedPreferences.Editor editor = settings.edit();
            Spinner deviceList = (Spinner) findViewById(R.id.bluetoothDeviceList);
            Spinner device = (Spinner) findViewById(R.id.remoteDevice);
            Spinner car = (Spinner) findViewById(R.id.car);
            if(deviceList.getSelectedItem()!=null) {
                MainActivity.debug("Settings.deviceAddress = " + deviceList.getSelectedItem().toString().split("\n")[1].trim());
                MainActivity.debug("Settings.deviceName = " + deviceList.getSelectedItem().toString().split("\n")[0].trim());
                editor.putString("deviceAddress", deviceList.getSelectedItem().toString().split("\n")[1].trim());
                editor.putString("deviceName", deviceList.getSelectedItem().toString().split("\n")[0].trim());
                editor.putString("device", device.getSelectedItem().toString().split("\n")[0].trim());
                editor.putString("car", car.getSelectedItem().toString().split("\n")[0].trim());
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
    }

    private void tryTofillDeviceList() {
        // get the bluetooth adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(SettingsActivity.this, "This device does not have any bluetooth adapter.\n\nSorry...", Toast.LENGTH_SHORT).show();
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
            SharedPreferences settings = getSharedPreferences("lu.fisch.canze.settings", 0);
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
