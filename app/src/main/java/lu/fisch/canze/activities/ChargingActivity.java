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
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

// If you want to monitor changes, you must add a FieldField to the fields.
// For the simple activity, the easiest way is to implement it in the activity itself.
public class ChargingActivity extends CanzeActivity implements FieldListener, DebugListener {

    private static final String SID_MaxCharge                        = "7bb.6101.336";
    private static final String SID_UserSoC                          = "42e.0";
    private static final String SID_RealSoC                          = "7bb.6103.192";
    private static final String SID_AvChargingPower                  = "427.40";
    private static final String SID_HvTemp                           = "42e.44";

    private static final String SID_RangeEstimate                    = "654.42";
    private static final String SID_DcPower                          = "800.6103.24"; // Virtual field
    private static final String SID_SOH                              = "7ec.623206.24";

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
        addField(SID_MaxCharge, 5000);
        addField(SID_UserSoC, 5000);
        addField(SID_RealSoC, 5000);
        addField(SID_SOH, 5000); // state of health gives continuous timeouts. This frame is send at a very low rate
        addField(SID_RangeEstimate, 5000);
        addField(SID_DcPower, 5000);
        addField(SID_AvChargingPower, 5000);
        addField(SID_HvTemp, 5000);
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

                    case SID_MaxCharge:
                        double maxCharge = field.getValue();
                        tv = findViewById(R.id.text_max_charge);
                        tv.setBackgroundColor((maxCharge < (avChPwr * 0.8) && avChPwr < 45.0) ? alarmColor : baseColor);
                        break;
                    case SID_UserSoC:
                        tv = findViewById(R.id.textUserSOC);
                        break;
                    case SID_RealSoC:
                        tv = findViewById(R.id.textRealSOC);
                        break;
                    case SID_HvTemp:
                        tv = findViewById(R.id.textHvTemp);
                        break;
                    case SID_SOH:
                        tv = findViewById(R.id.textSOH);
                        break;
                    case SID_RangeEstimate:
                        tv = findViewById(R.id.textKMA);
                        if (field.getValue() >= 1023) {
                            tv.setText("---");
                        } else {
                            tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                        }
                        tv = null;
                        break;
                    case SID_DcPower:
                        tv = findViewById(R.id.textDcPwr);
                        break;
                    case SID_AvChargingPower:
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