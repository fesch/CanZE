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

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

/**
 * Heatmap by jeroen on 27-10-15.
 */
public class HeatmapCellvoltageActivity extends CanzeActivity implements FieldListener, DebugListener {

    public static final String SID_Preamble_CellVoltages1 = "7bb.6141."; // (LBC)
    public static final String SID_Preamble_CellVoltages2 = "7bb.6142."; // (LBC)

    private double mean = 0;
    private double cutoff;
    private double lastVoltage[] = {0,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4};
    private int lastCell = 96;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap_cellvoltage);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        // Battery compartment temperatures
        for (int i = 1; i <= 62; i++) {
            String sid = SID_Preamble_CellVoltages1 + (i * 16); // remember, first is pos 16, i starts s at 1
            addField(sid);
        }
        for (int i = 63; i <= 96; i++) {
            String sid = SID_Preamble_CellVoltages2 + ((i - 62) * 16); // remember, first is pos 16, i starts s at 1
            addField(sid);
        }
    }

    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
    @Override
    public void onFieldUpdateEvent(final Field field) {
        // get the text field
        final String fieldId = field.getSID();
        int cell = 0;
        if (fieldId.startsWith(SID_Preamble_CellVoltages1)) {
            cell = (Integer.parseInt(fieldId.split("[.]")[2])) / 16; // cell is 1-based
        } else if (fieldId.startsWith(SID_Preamble_CellVoltages2)) {
            cell = (Integer.parseInt(fieldId.split("[.]")[2])) / 16 + 62; // cell is 1-based
        }
        if (cell > 0 && cell <= lastCell) {
            final double value = field.getValue();

            lastVoltage[cell] = value;
            if (cell == lastCell) {
                mean = 0;
                double lowest = 5;
                double highest = 3;
                // lastVoltage[20] = 3.5; fake for test
                for (int i = 1; i <= lastCell; i++) {
                    mean += lastVoltage[i];
                    if (lastVoltage[i] < lowest ) lowest  = lastVoltage[i];
                    if (lastVoltage[i] > highest) highest = lastVoltage[i];
                }
                mean /= lastCell;
                cutoff = lowest < 3.712 ? mean - (highest - mean) * 1.5 : 2;

                        // the update has to be done in a separate thread
                // otherwise the UI will not be repainted
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 1; i <= lastCell; i++) {
                            TextView tv = (TextView) findViewById(getResources().getIdentifier("text_cell_" + i + "_voltage", "id", getPackageName()));
                            if (tv != null) {
                                // tv.setText(String.format("%.3f", lastVoltage[i]));
                                tv.setText(String.format(Locale.getDefault(), "%.3f", lastVoltage[i]));
                                int color = (int) (5000 * (lastVoltage[i] - mean)); // color is temp minus mean. 1mV difference is 5 color ticks
                                if (lastVoltage[i] <= cutoff) {
                                    color = 0xffff4040;
                                } else if (color > 62) {
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