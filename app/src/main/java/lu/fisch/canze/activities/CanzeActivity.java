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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.interfaces.FieldListener;
import lu.fisch.canze.widgets.WidgetView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(MainActivity.device!=null)
            if(!BluetoothManager.getInstance().isConnected()) {
                // restart Bluetooth
                MainActivity.debug("CanzeActivity: restarting BT");
                BluetoothManager.getInstance().connect();
            }
        MainActivity.debug("CanzeActivity: onCreate ("+this.getClass().getSimpleName()+")");
        //if(!widgetView) {
            // register all fields
            // --> not needed as these frames are now application bound and will not be cleared anyway
            // MainActivity.registerFields();
            // initialise the widgets (if any present)
            // --> not needed as onResume will call it!
            //initWidgets();
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.debug("CanzeActivity: onPause");

        // stop here if BT should stay on!
        if(MainActivity.bluetoothBackgroundMode)
        {
            return;
        }

        // if we are not coming back from somewhere, stop Bluetooth
        if(!back && !widgetClicked) {
            MainActivity.debug("CanzeActivity: onPause > stopBluetooth");
            MainActivity.getInstance().stopBluetooth(false);
        }
        if(!widgetClicked) {
            // remember we paused ourselves
            iLeftMyOwn=true;
        }
        removeFieldListeners();
        MainActivity.getInstance().setDebugListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.debug("CanzeActivity: onResume");
        // if we paused ourselvers
        if (iLeftMyOwn && !widgetClicked) {
            MainActivity.debug("CanzeActivity: onResume > reloadBluetooth");
            // restart Bluetooth
            MainActivity.getInstance().reloadBluetooth(false);
            iLeftMyOwn = false;
        }

        if(BluetoothManager.getInstance().isDummyMode())
            MainActivity.device.initConnection();

        if(!widgetClicked) {
            MainActivity.debug("CanzeActivity: onResume > initWidgets");
            // initialise the widgets (if any present)
            initWidgets();
        }
        widgetClicked=false;
        initListeners();
    }

    @Override
    protected void onDestroy() {
        MainActivity.debug("CanzeActivity: onDestroy");
        if(!widgetView) {
            // free the widget listerners
            freeWidgetListeners();
            // free field listeners
            removeFieldListeners();
            if (isFinishing()) {
                MainActivity.debug("CanzeActivity: onDestroy (finishing)");
                // clear filters
                MainActivity.device.clearFields();
                //MainActivity.registerFields();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.isSafe()) {
            super.onBackPressed();
            back = true;
        }
    }

    /********************************************************/

    protected void initWidgets()
    {
        final ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(android.R.id.content));
        if(!widgets.isEmpty())
            MainActivity.toast(R.string.toast_InitWidgets);

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

                    MainActivity.debug("CanzeActivity: initWidgets: Widget: " + wv.getDrawable().getTitle() + " ("+wv.getFieldSID()+")");
                }
            }
        }).start();
    }

    protected void freeWidgetListeners()
    {
        // free up the listener again
        ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
        for(int i=0; i<widgets.size(); i++) {
            WidgetView wv = widgets.get(i);
            String sid = wv.getFieldSID();
            if(sid!=null) {
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


    protected ArrayList<WidgetView> getWidgetViewArrayList(ViewGroup viewGroup)
    {
        ArrayList<WidgetView> result = new ArrayList<>();

        if(viewGroup!=null)
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

    /*
    public static String compress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return Base64.encodeToString(compressed, Base64.NO_WRAP);
    }

    public static String decompress(String zipText) throws IOException {
        byte[] compressed = Base64.decode(zipText,Base64.NO_WRAP);
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }
    */

    /******* activity field stuff ********************/

    protected ArrayList<Field> subscribedFields = new ArrayList<>();

    protected void addField(String sid) {
        addField(sid, 0);
    }

    protected void addField(String sid, int intervalMs)
    {
        Field field = MainActivity.fields.getBySID(sid);
        if (field != null)
        {
            // add a listener to the field
            field.addListener(this);
            // register it in the queue
            MainActivity.device.addActivityField(field, intervalMs);
            // remeber this field has been added (filter out doubles)
            if(!subscribedFields.contains(field))
                subscribedFields.add(field);
        } else {
            MainActivity.debug(this.getClass().getSimpleName()+" (CanzeActivity): SID " + sid + " does not exist in class Fields");
        }
    }

    private void removeFieldListeners()
    {
        // free up the listeners again
        for (Field field : subscribedFields)
        {
            field.removeListener(this);
        }
        subscribedFields.clear();
    }

    public void dropDebugMessage (final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView) findViewById(R.id.textDebug);
                if (tv != null) tv.setText(msg);
            }
        });
    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        // empty --> descents should override this
    }

    protected abstract void initListeners();
}

