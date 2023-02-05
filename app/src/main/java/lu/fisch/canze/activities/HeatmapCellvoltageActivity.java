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

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

/**
 * Heatmap by jeroen on 27-10-15.
 */
public class HeatmapCellvoltageActivity extends CanzeActivity implements FieldListener, DebugListener {

    private double mean = 0;
    private double lowest, highest;
    private double cutoff;
    private final double[] lastVoltage = {0,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4};
    private int lastCell = 96;
    private int totalCells = 96;
    @ColorInt
    private int baseColor;
    private boolean dark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MainActivity.isSpring() ? R.layout.activity_heatmap_cellvoltages : R.layout.activity_heatmap_cellvoltage);

        //define the number of cells depending on the car
        if (MainActivity.isSpring()) {
            lastCell = 72;
            totalCells = 72;
        }

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorButtonNormal, typedValue, true);
        baseColor = typedValue.data;
        dark = ((baseColor & 0xff0000) <= 0xa00000);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        // Battery compartment temperatures
        for (int i = 1; i <= 62; i++) {
            String sid = Sid.Preamble_CellVoltages1 + (i * 16); // remember, first is pos 16, i starts s at 1
            addField(sid);
        }
        for (int i = 63; i <= totalCells; i++) {
            String sid = Sid.Preamble_CellVoltages2 + ((i - 62) * 16); // remember, first is pos 16, i starts s at 1
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
        if (fieldId.startsWith(Sid.Preamble_CellVoltages1)) {
            cell = (Integer.parseInt(fieldId.split("[.]")[2])) / 16; // cell is 1-based
        } else if (fieldId.startsWith(Sid.Preamble_CellVoltages2)) {
            cell = (Integer.parseInt(fieldId.split("[.]")[2])) / 16 + 62; // cell is 1-based
        }
        if (cell > 0 && cell <= lastCell) {
            final double value = field.getValue();

            lastVoltage[cell] = value;
            if (cell == lastCell) {
                mean = 0;
                lowest = 5;
                highest = 3;
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
                            TextView tv = findViewById(getResources().getIdentifier("text_cell_" + i + "_voltage", "id", getPackageName()));
                            if (tv != null) {
                                // tv.setText(String.format("%.3f", lastVoltage[i]));
                                tv.setText(String.format(Locale.getDefault(), "%.3f", lastVoltage[i]));
                                int delta = (int) (5000 * (lastVoltage[i] - mean)); // color is temp minus mean. 1mV difference is 5 color ticks
                                tv.setBackgroundColor(makeColor (delta));
                            }
                        }

                        // Only update the high-low if we have realistic data
                        if (highest >= lowest) {
                            TextView tv = findViewById(R.id.text_CellVoltageTop);
                            if (tv != null) {
                                tv.setText(String.format("%.3f", highest));
                            }
                            tv = findViewById(R.id.text_CellVoltageBottom);
                            if (tv != null) {
                                tv.setText(String.format("%.3f", lowest));
                            }
                            tv = findViewById(R.id.text_CellVoltageDelta);
                            if (tv != null) {
                                tv.setText(String.format("%d", Math.round(1000 * (highest - lowest))));
                            }
                        }
                    }
                });
            }
        }
    }


    private @ColorInt int makeColor (int delta) {
        @ColorInt int color = baseColor;

        if (delta > 62) delta = 62; else if (delta < -62) delta = -62;

        if (dark) {
            if (delta > 0) {
                return baseColor + (delta * 0x010000); // one tick is one red
            } else {
                return baseColor - (delta * 0x000001); // one degree below is a 16th blue added
            }

        } else { // light
            if (delta > 0) {
                return baseColor - (delta * 0x000101); // one tick is one red
            } else {
                return baseColor + (delta * 0x010100); // one degree below is a 16th blue added
            }
        }
    }
}