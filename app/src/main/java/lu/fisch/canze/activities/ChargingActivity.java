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

// If you want to monitor changes, you must add a FieldField to the fields.
// For the simple activity, the easiest way is to implement it in the activity itself.
public class ChargingActivity extends CanzeActivity implements FieldListener, DebugListener {

    private double avChPwr;
    @ColorInt private int baseColor;
    @ColorInt private int alarmColor;
    private boolean dark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(R.attr.colorButtonNormal, typedValue, true);
        baseColor = typedValue.data;
        dark = ((baseColor & 0xff0000) <= 0xa00000);
        alarmColor = dark ? baseColor + 0x200000 : baseColor - 0x00002020;
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        if (MainActivity.isPh2()) addField("7ec.5003.0", 2000); // open EVC
        addField(Sid.MaxCharge, 5000);
        addField(Sid.UserSoC, 5000);
        addField(Sid.RealSoC, 5000);
        addField(Sid.SOH, 5000); // state of health gives continuous timeouts. This frame is send at a very low rate
        addField(Sid.RangeEstimate, 5000);
        addField(Sid.DcPowerIn, 5000);
        addField(Sid.AvailableChargingPower, 5000);
        addField(Sid.HvTemp, 5000);
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

                // get the text field
                switch (fieldId) {

                    case Sid.MaxCharge:
                        double maxCharge = field.getValue();
                        tv = findViewById(R.id.text_max_charge);
                        tv.setBackgroundColor((maxCharge < (avChPwr * 0.8) && avChPwr < 45.0) ? alarmColor : baseColor);
                        break;
                    case Sid.UserSoC:
                        tv = findViewById(R.id.textUserSOC);
                        break;
                    case Sid.RealSoC:
                        tv = findViewById(R.id.textRealSOC);
                        break;
                    case Sid.HvTemp:
                        tv = findViewById(R.id.textHvTemp);
                        break;
                    case Sid.SOH:
                        tv = findViewById(R.id.textSOH);
                        tv.setText(String.format(Locale.getDefault(), "%.1f", field.getValue()));
                        break;
                    case Sid.RangeEstimate:
                        tv = findViewById(R.id.textKMA);
                        if (field.getValue() >= 1023) {
                            tv.setText("---");
                        } else {
                            tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                        }
                        tv = null;
                        break;
                    case Sid.DcPowerIn:
                        tv = findViewById(R.id.textDcPwr);
                        break;
                    case Sid.AvailableChargingPower:
                        avChPwr = field.getValue();
                        tv = findViewById(R.id.textAvChPwr);
                        if (avChPwr > 45.0) {
                            tv.setText("---");
                            tv = null;
                        }
                        break;
                }
                // set regular new content, all exceptions handled above
                if (tv != null) {
                    tv.setText(String.format(Locale.getDefault(), "%.1f", field.getValue()));
                }
            }
        });

    }
}
