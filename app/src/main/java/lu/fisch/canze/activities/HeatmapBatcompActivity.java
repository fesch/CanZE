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
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

/**
 * Heatmap by jeroen on 27-10-15.
 */
public class HeatmapBatcompActivity extends CanzeActivity implements FieldListener {

    public static final String SID_Preamble_CompartmentTemperatures = "7bb.6104."; // (LBC)

    private double mean = 0;
    private double lastVal [] = {0,15,15,15,15,15,15,15,15,15,15,15,15};
    private int lastCell = 4;

    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MainActivity.car == MainActivity.CAR_ZOE_Q210 || MainActivity.car == MainActivity.CAR_ZOE_R240 ? R.layout.activity_heatmap_batcomp : R.layout.activity_heatmap_batcomp2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // initialise the widgets
        initListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListeners ();
    }

    private void initListeners() {
        subscribedFields = new ArrayList<>();
        if(MainActivity.car == MainActivity.CAR_ZOE_Q210 || MainActivity.car == MainActivity.CAR_ZOE_R240) {
            lastCell = 12;
        }
        for (int i = 1; i <= lastCell; i++) {
            String sid = SID_Preamble_CompartmentTemperatures + (8 + i * 24); // remember, first is pos 32, i starts s at 1
            addListener(sid);
        }
    }

    private void removeListeners () {
        // empty the query loop
        MainActivity.device.clearFields();
        // free up the listeners again
        for (Field field : subscribedFields) {
            field.removeListener(this);
        }
        subscribedFields.clear();
    }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            // activate callback to this object when a value is updated
            field.addListener(this);
            // add querying this field in the queryloop
            MainActivity.device.addActivityField(field);
            subscribedFields.add(field);
        } else {
            MainActivity.toast("sid " + sid + " does not exist in class Fields");
        }

    }

    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
    @Override
    public void onFieldUpdateEvent(final Field field) {
        final String fieldId = field.getSID();

        // get the text field

        if (fieldId.startsWith(SID_Preamble_CompartmentTemperatures)) {
            int cell = (Integer.parseInt(fieldId.split("[.]")[2]) - 8) / 24; // cell is 1-based
            final double value = field.getValue();

            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  TextView tv = (TextView) findViewById(R.id.textDebug);
                                  tv.setText(fieldId + ":" + value);
                              }
                          });

            lastVal[cell] = value;
            // calculate the mean value of the previous full round
            if (cell == lastCell) {
                mean = 0;
                for (int i = 1; i <= lastCell; i++) {
                    mean += lastVal[i];
                }
                mean /= lastCell;

                // the update has to be done in a separate thread
                // otherwise the UI will not be repainted doing that here only when the entire temperature buls is (supposed to be) in,
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 1; i <= lastCell; i++) {
                            TextView tv = (TextView) findViewById(getResources().getIdentifier("text_comp_" + i + "_temp", "id", getPackageName()));
                            if (tv != null) {
                                tv.setText("" + lastVal[i]);
                                int color = (int) (50 * (lastVal[i] - mean)); // color is temp minus mean
                                if (color > 62) {
                                    color = 0xffffc0c0;
                                } else if (color > 0) {
                                    color = 0xffc0c0c0 + (color * 0x010000); // one tick is one red
                                } else if (color >= -62) {
                                    color = 0xffc0c0c0 - color; // one degree below is a 16th blue added
                                } else {
                                    color = 0xffc0c0ff;
                                }
                                tv.setBackgroundColor(color);
                            }
                        }
                    }
                });
            }
        }

    }
}