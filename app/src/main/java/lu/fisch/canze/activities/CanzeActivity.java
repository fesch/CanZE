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

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.interfaces.FieldListener;
import lu.fisch.canze.widgets.WidgetView;

import static lu.fisch.canze.devices.Device.INTERVAL_ASAP;

/**
 * Created by robertfisch on 30.09.2015.
 */
public abstract class CanzeActivity extends AppCompatActivity implements FieldListener {

    private boolean iLeftMyOwn = false;
    private boolean back = false;

    protected boolean widgetView = false;

    public void setWidgetClicked(boolean widgetClicked) {
        this.widgetClicked = widgetClicked;
    }

    protected boolean widgetClicked = false;
    protected boolean doReStartQueueOnResume = true;

    protected Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // enable hardware acceleration, except for dubious Android versions
        /*if (Build.VERSION.SDK_INT != 26 && Build.VERSION.SDK_INT != 27) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }*/

        super.onCreate(savedInstanceState);

        // save the local theme do it can be used decoding colors for the graphs
        MainActivity.getInstance().setLocalTheme(this.getTheme());

        // display the home button (it will automatically act as a menu tap (onOption)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // reconnect bluetooth if needed
        if (MainActivity.device != null) {
            if (!BluetoothManager.getInstance().isConnected()) {
                // restart Bluetooth
                MainActivity.debug("CanzeActivity: restarting BT");
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BluetoothManager.getInstance().connect();
                        } catch (InvalidParameterException e) {
                            if (!isFinishing())
                                MainActivity.toast(MainActivity.TOAST_NONE, R.string.message_CantConnect);
                        }
                    }
                })).start();
                //BluetoothManager.getInstance().connect();
            }
        }
        MainActivity.debug("CanzeActivity: onCreate (" + this.getClass().getSimpleName() + ")");
    }

    void setDoRestartQueueOnResume (boolean start) {
        doReStartQueueOnResume = start;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.debug("CanzeActivity: onPause");

        // stop here if we are in an activity with a halted poller
        if (!doReStartQueueOnResume) return;

        // stop here if BT should stay on!
        if (MainActivity.bluetoothBackgroundMode) {
            return;
        }

        // if we have not pressed the back or home buton
        if (!back && !widgetClicked) {
            MainActivity.debug("CanzeActivity: onPause > stopBluetooth");
            // stop the background process, do NOT clear the queue, disconnect
            MainActivity.getInstance().stopBluetooth(false);
        }
        if (!widgetClicked) {
            // remember we paused ourselves
            iLeftMyOwn = true;
        }
        // stop the self propelled fields (i.e. GPS)
        selfPropelFields(false);
        // remove this activity from being a listener to field updates it subscribed itself to
        removeFieldListeners();
        // remove this activity from being a listener to debug events
        if (MainActivity.getInstance() != null)
            MainActivity.getInstance().setDebugListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set the menu globally
        MainActivity.getInstance().setBluetoothMenuItem(mOptionsMenu);
        MainActivity.debug("CanzeActivity: onResume");
        // save the local theme do it can be used decoding colors for the graphs
        MainActivity.getInstance().setLocalTheme(this.getTheme());

        // when initlisteners is called, the activity adds the fields to the device queue
        // moved up because sef propelled fields need to already be added to subscribedFields
        // note that onResume (re)adds the fields, while onPause does not clear them out
        // this is done by onDestroy->removeFieldListeners
        initListeners();
        // start the self propelled fields (i.e. GPS)
        selfPropelFields(true);

        // if we paused ourselvers
        if (iLeftMyOwn && !widgetClicked) {
            MainActivity.debug("CanzeActivity: onResume > reloadBluetooth");
            // restart Bluetooth
            // jm moved to a thread to avoid ANR
            Thread t = new Thread() {
                public void run() {
                    MainActivity.getInstance().reloadBluetooth(false);
                }
            };
            t.start();
            iLeftMyOwn = false;
        }

        if (BluetoothManager.getInstance().isDummyMode())
            if (MainActivity.device != null)
                MainActivity.device.initConnection();

        if (!widgetClicked) {
            MainActivity.debug("CanzeActivity: onResume > initWidgets");
            // initialise the widgets (if any present)
            initWidgets();
        }
        widgetClicked = false;
    }

    @Override
    protected void onDestroy() {
        MainActivity.debug("CanzeActivity: onDestroy");
        if (!widgetView) {
            // free the widget listerners
            freeWidgetListeners();
            // remove this activity from being a listener to field updates it subscribed itself to
            removeFieldListeners();
            if (isFinishing()) {
                MainActivity.debug("CanzeActivity: onDestroy (finishing)");
                // clear filters
                if (MainActivity.device != null)
                    MainActivity.device.clearFields();
                //MainActivity.registerFields();
            }
        }
        super.onDestroy();
    }

    void selfPropelFields(boolean startStop) {
        for (Field field : subscribedFields) {
            if (field.isSelfPropelled()) {
                Fields.getInstance().selfPropel(field.getResponseId(), startStop);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // save the menu. it is being set globally here and in resume to allow Bluetooth to change
        // it's appearance
        mOptionsMenu = menu;
        // inflate the menu; this adds items to the action bar if it is present. Empty contains
        // only the bluetooth icon.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        // set the menu globally
        MainActivity.getInstance().setBluetoothMenuItem(mOptionsMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!MainActivity.isSafe()) return true; // do nothing
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            back = true;
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!MainActivity.isSafe()) return;
        super.onBackPressed();
        back = true;
    }

    /********************************************************/

    private void initWidgets() {
        final ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(android.R.id.content));
        if (!widgets.isEmpty())
            // MainActivity.toast(R.string.toast_InitWidgets);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // connect the widgets to the respective fields
                    // and add the filters to the reader
                    for (int i = 0; i < widgets.size(); i++) {
                        final WidgetView wv = widgets.get(i);
                        // connect widgets to fields
                        if (wv == null) {
                            throw new ExceptionInInitializerError("CanzeActivity: initWidgets: Widget <" + i + "> is NULL!");
                        }
                        wv.setCanzeActivity(CanzeActivity.this);
                        MainActivity.debug("CanzeActivity: initWidgets: Widget: " + wv.getDrawable().getTitle() + " (" + wv.getFieldSID() + ")");
                    }
                }
            }).start();
    }

    private void freeWidgetListeners() {
        // free up the listener again
        ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
        for (int i = 0; i < widgets.size(); i++) {
            WidgetView wv = widgets.get(i);
            String sid = wv.getFieldSID();
            if (sid != null) {
                String[] sids = sid.split(",");
                for (String sid1 : sids) {
                    Field field = MainActivity.fields.getBySID(sid1);
                    if (field != null) {
                        field.removeListener(wv.getDrawable());
                    }
                }
            }
        }
    }


    private ArrayList<WidgetView> getWidgetViewArrayList(ViewGroup viewGroup) {
        ArrayList<WidgetView> result = new ArrayList<>();
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                if (v instanceof ViewGroup) {
                    result.addAll(getWidgetViewArrayList((ViewGroup) v));
                } else if (v instanceof WidgetView) {
                    result.add((WidgetView) v);
                }
            }
        }
        return result;
    }

    /******* activity field stuff ********************/

    private final ArrayList<Field> subscribedFields = new ArrayList<>();

    protected void addField(String sid) {
        addField(sid, INTERVAL_ASAP); // follows frame rate
    }

    protected void addField(Field field) {
        addField(field, INTERVAL_ASAP); // follows frame rate
    }

    protected void addField(String sid, int intervalMs) {
        Field field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            if (!field.getResponseId().equals ("999999")) {
                addField(field, intervalMs);
            }
        } else {
            MainActivity.debug(this.getClass().getSimpleName() + " (CanzeActivity): SID " + sid + " does not exist in class Fields");
            if (!isFinishing())
                MainActivity.toast(MainActivity.TOAST_NONE, String.format(Locale.getDefault(), MainActivity.getStringSingle(R.string.format_NoSid), this.getClass().getSimpleName(), sid));
        }
    }

    protected void addField(Field field, int intervalMs) {
        if (field != null && !field.getResponseId().equals("999999")) {
            // add a listener to the field
            field.addListener(this);
            // register it in the queue // enable it to self propel
            MainActivity.device.addActivityField(field, intervalMs);
            // remember this field has been added (filter out doubles)
            if (!subscribedFields.contains(field))
                subscribedFields.add(field);
        }
    }

    private void removeFieldListeners() {
        // free up the listeners again
        for (Field field : subscribedFields) {
            field.removeListener(this);
        }
        subscribedFields.clear();
    }

    public void dropDebugMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = findViewById(R.id.textDebug);
                if (tv != null) tv.setText(msg);
            }
        });
    }

    public void appendDebugMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = findViewById(R.id.textDebug);
                if (tv != null) {
                    String newMsg = tv.getText() + " " + msg;
                    tv.setText(newMsg);
                }
            }
        });
    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        // empty --> descents should override this
    }

    protected abstract void initListeners();
}
