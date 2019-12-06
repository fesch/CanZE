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
import android.os.Environment;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

import static lu.fisch.canze.activities.MainActivity.TOAST_NONE;
import static lu.fisch.canze.activities.MainActivity.debug;
import static lu.fisch.canze.activities.MainActivity.toast;


public class ResearchActivity extends CanzeActivity implements FieldListener, DebugListener {

    ArrayList<Field> fields;
    private final HashMap<String, TextView> viewsBySid = new HashMap<>();
    private final boolean savedFieldLogMode = MainActivity.fieldLogMode;
    private int firstEmptyRow = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_research);

        // stop the poller
        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
                if (MainActivity.device != null) {
                    // stop the poller thread
                    MainActivity.device.stopAndJoin();
                }
        //    }
        //}).start();

        // read the _research.csv file
        if (!MainActivity.storageIsAvailable) {
            toast(TOAST_NONE, "Research.onCreate: SDcard not available");
            finish();
            return;
        }
        if (!MainActivity.getInstance().isExternalStorageWritable()) {
            toast(TOAST_NONE,"Research.onCreate: SDcard not writeable");
            finish();
            return;
        }
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CanZE/";
        String importFieldsFileName = file_path + "_Research.csv";
        File fieldsFile = new File(importFieldsFileName);
        if (!fieldsFile.exists()) {
            toast(TOAST_NONE,"Research.onCreate: " + importFieldsFileName + " does not exist");
            finish();
            return;
        }

        // load the data
        try {
            Fields.getInstance().load(importFieldsFileName);
        } catch (Exception e) {
            toast(TOAST_NONE,"Research.onCreate: could not load " + importFieldsFileName);
            Fields.getInstance().load();
            finish();
            return;
        }

        // force field logging mode on
        MainActivity.fieldLogMode = true;

        // start the poller
        if (MainActivity.device != null) {
            MainActivity.device.initConnection();
            MainActivity.getInstance().registerApplicationFields();
        }

        // add to the screen and build index viewsBySid
        fields = Fields.getInstance().getAllFields();
        TextView tv;
        for (firstEmptyRow = 0; firstEmptyRow < fields.size(); firstEmptyRow++) {
            Field field = fields.get(firstEmptyRow);
            if(field!=null) {
                if (firstEmptyRow <= 19) {
                    fillView ("textResL" + ("0"+firstEmptyRow).substring(firstEmptyRow<10?0:1), field.getName() + " (" + field.getUnit() + ")");
                    tv = findViewById(getResources().getIdentifier("textResV" + ("0"+firstEmptyRow).substring(firstEmptyRow<10?0:1), "id", getPackageName()));
                    fillView (tv, "-");
                    viewsBySid.put (field.getSID(), tv);
                }
            }
        }
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            if(f!=null) {
                addField(f);
            }
        }
    }


    private void fillView(final String id, final String msg) {
        TextView tv = findViewById(getResources().getIdentifier(id, "id", getPackageName()));
        fillView(tv, msg);
    }

    private void fillView(final TextView tv, final String msg) {
        if (tv == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(msg);
            }
        });
    }

    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
    @Override
    public void onFieldUpdateEvent(final Field field) {
        TextView tv = viewsBySid.get (field.getSID());

        // this is added to add stray fields to the screen
        if (tv == null && firstEmptyRow <= 19) {
            fillView ("textResL" + ("0"+firstEmptyRow).substring(firstEmptyRow<10?0:1), field.getName() + "(" + field.getUnit() + ")");
            tv = findViewById(getResources().getIdentifier("textResV" + ("0"+firstEmptyRow).substring(firstEmptyRow<10?0:1), "id", getPackageName()));
            fillView (tv, "-");
            viewsBySid.put (field.getSID(), tv);
            firstEmptyRow++;
        }

        if (field.isString()) {
            fillView(tv, field.getStringValue());
        } else {
            double val =  field.getValue();
            fillView(tv, Double.isNaN(val) ? "Nan" : String.format(Locale.getDefault(), "%.1f", val));
        }
    }

    @Override
    protected void onDestroy() {
        debug ("Research.onDestroy");

        // stop the poller
        if (MainActivity.device != null) {
            // stop the poller thread
            MainActivity.device.stopAndJoin();
        }

        // Reload the frame & timings
        Fields.getInstance().load();

        // restore field logging mode
        MainActivity.fieldLogMode = savedFieldLogMode;

        // restart the poller
        if (MainActivity.device != null) {
            MainActivity.device.initConnection();
            MainActivity.getInstance().registerApplicationFields();
        }

        super.onDestroy();
    }
}