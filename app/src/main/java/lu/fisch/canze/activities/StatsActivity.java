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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

public class StatsActivity extends CanzeActivity implements FieldListener {
    // ISO-TP data
    public static final String SID_EVC_SoC                              = "7ec.622002.24"; //  (EVC)
    public static final String SID_EVC_Odometer                         = "7ec.622006.24"; //  (EVC)
    public static final String SID_Preamble_CompartmentTemperatures = "7bb.6104."; // (LBC)

    int lastOdo = 0;
    int kmInBat = 0;
    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        subscribedFields = new ArrayList<>();

        // Make sure to add ISO-TP listeners grouped by ID


        addListener(SID_EVC_SoC);
        addListener(SID_EVC_Odometer);
        // Battery compartment temperatures

        int lastCell;
        if(MainActivity.car==MainActivity.CAR_ZOE_Q210 || MainActivity.car == MainActivity.CAR_ZOE_R240) {
            lastCell = 296;
        }
        else
        {
            lastCell = 128;
        }
        for (int i = 32; i <= lastCell; i += 24) {
            String sid = SID_Preamble_CompartmentTemperatures + i;
            addListener(sid);
        }

    }
    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            MainActivity.device.addActivityField(field);
            subscribedFields.add(field);
        }
        else
        {
            MainActivity.toast("sid " + sid + " does not exist in class Fields");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // free up the listeners again
        for (Field field : subscribedFields) {
            field.removeListener(this);
        }
        subscribedFields.clear();
    }

    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                TextView tv = null;
                ProgressBar pb = null;

                // get the text field
                switch (fieldId) {
                    case SID_EVC_SoC:
                        tv = (TextView) findViewById(R.id.textSOC);
                        break;
                    case SID_EVC_Odometer:
                        int odo = (int) field.getValue();

                        tv = null;
                        break;

                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText("" + field.getValue());
                }

                //tv = (TextView) findViewById(R.id.textDebug);
                //tv.setText(fieldId);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
