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

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
public class ClimaTechActivity extends CanzeActivity implements FieldListener, DebugListener {

    public static final String SID_EngineFanSpeed                   = "42e.20";
    public static final String SID_DcPower                          = "800.6103.24";
    // public static final String SID_ChargingPower                    = "42e.56";
    public static final String SID_HvCoolingState                   = "430.38";
    public static final String SID_HvEvaporationTemp                = "430.40";
    // public static final String SID_ClimaCompressorPower             = "764.6143.88";
    public static final String SID_Pressure                         = "764.6143.134";
    public static final String SID_BatteryConditioningMode          = "432.36";
    public static final String SID_ClimaLoopMode                    = "42a.48";


    final String cooling_Status [] = MainActivity.getStringList(R.array.list_CoolingStatus);
    final String conditioning_Status [] = MainActivity.getStringList (R.array.list_ConditioningStatus);
    final String climate_Status [] = MainActivity.getStringList(R.array.list_ClimateStatus);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climatech);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        if (MainActivity.isZOE()) {
            addField(SID_EngineFanSpeed, 0);
            addField(SID_DcPower, 0);
            // addListener(SID_ChargingPower);
            addField(SID_HvCoolingState, 0);
            addField(SID_HvEvaporationTemp, 0);
            // addField(SID_ClimaCompressorPower,0);
            addField(SID_Pressure, 0);
            addField(SID_BatteryConditioningMode, 0);
            addField(SID_ClimaLoopMode, 0);
        }
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

                    case SID_EngineFanSpeed:
                        tv = (TextView) findViewById(R.id.text_EFS);
                        break;
                    case SID_DcPower:
                        tv = (TextView) findViewById(R.id.text_DCP);
                        break;
                    // case SID_ChargingPower:
                    //     tv = (TextView) findViewById(R.id.text_CPO);
                    //     break;
                    case SID_HvCoolingState:
                        tv = (TextView) findViewById(R.id.text_HCS);
                        tv.setText(cooling_Status[(int) field.getValue()]);
                        tv = null;
                        break;
                    case SID_HvEvaporationTemp:
                        tv = (TextView) findViewById(R.id.text_HET);
                        break;
                    case SID_Pressure:
                        tv = (TextView) findViewById(R.id.text_PRE);
                        break;
                    case SID_BatteryConditioningMode:
                        tv = (TextView) findViewById(R.id.text_HCM);
                        tv.setText(conditioning_Status[(int) field.getValue()]);
                        tv = null;
                        break;
                    case SID_ClimaLoopMode:
                        tv = (TextView) findViewById(R.id.text_CLM);
                        tv.setText(climate_Status[(int) field.getValue()]);
                        tv = null;
                        break;
                    //case SID_ClimaCompressorPower:
                        // tv = (TextView) findViewById(R.id.text_CPW);
                        // break;

                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText(String.format(Locale.getDefault(), "%.1f", field.getValue()));
                }

                //tv = (TextView) findViewById(R.id.textDebug);
                //tv.setText(fieldId);
            }
        });

    }

}