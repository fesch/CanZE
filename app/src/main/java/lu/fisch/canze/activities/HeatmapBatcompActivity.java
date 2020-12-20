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
public class HeatmapBatcompActivity extends CanzeActivity implements FieldListener, DebugListener {
    
    private double mean = 0;
    private final double[] lastVal = {0,15,15,15,15,15,15,15,15,15,15,15,15};
    private final int lastCell = 12; //4;
    @ColorInt private int baseColor;
    private boolean dark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(MainActivity.isZOE() ? R.layout.activity_heatmap_batcomp : R.layout.activity_heatmap_batcomp2);
        setContentView(R.layout.activity_heatmap_batcomp);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorButtonNormal, typedValue, true);
        baseColor = typedValue.data;
        dark = ((baseColor & 0xff0000) <= 0xa00000);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        //if(MainActivity.isZOE()) {
        //    lastCell = 12;
        //}
        for (int i = 1; i <= lastCell; i++) {
            String sid = Sid.Preamble_CompartmentTemperatures + (8 + i * 24); // remember, first is pos 32, i starts s at 1
            addField(sid, 5000);
        }
    }

    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
    @Override
    public void onFieldUpdateEvent(final Field field) {
        final String fieldId = field.getSID();

        // get the text field

        if (fieldId.startsWith(Sid.Preamble_CompartmentTemperatures)) {
            int cell = (Integer.parseInt(fieldId.split("[.]")[2]) - 8) / 24; // cell is 1-based
            final double value = field.getValue();
            lastVal[cell] = value;
            // calculate the mean value of the previous full round
            if (cell == lastCell) {
                mean = 0;
                for (int i = 1; i <= lastCell; i++) {
                    mean += lastVal[i];
                }
                mean /= lastCell;

                // the update has to be done in a separate thread
                // otherwise the UI will not be repainted doing that here only when the entire temperature array is (supposed to be) in,
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 1; i <= lastCell; i++) {
                            TextView tv = findViewById(getResources().getIdentifier("text_comp_" + i + "_temp", "id", getPackageName()));
                            if (tv != null) {
                                // tv.setText("" + lastVal[i]);
                                tv.setText(String.format(Locale.getDefault(), "%.0f", lastVal[i]));
                                int delta = (int) (50 * (lastVal[i] - mean)); // color is temp minus mean
                                tv.setBackgroundColor(makeColor (delta));
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