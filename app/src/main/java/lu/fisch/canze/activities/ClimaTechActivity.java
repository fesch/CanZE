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

    private static final String SID_EngineFanSpeed                   = "42e.20";
    private static final String SID_DcPower                          = "800.6109.24";
    // private static final String SID_ChargingPower                    = "42e.56";
    private static final String SID_HvCoolingState                   = "430.38";
    private static final String SID_HvEvaporationTemp                = "430.40";
    //private static final String SID_ClimaCompressorPower             = "7ec.6233A7.24";
    private static final String SID_Pressure                         = "764.6143.134";
    private static final String SID_BatteryConditioningMode          = "432.36";
    private static final String SID_ClimaLoopMode                    = "42a.48";
    private static final String SID_PtcRelay1                        = "7ec.623498.31";
    private static final String SID_PtcRelay2                        = "7ec.62349a.31";
    private static final String SID_PtcRelay3                        = "7ec.62349c.31";

    private final String[] cooling_Status = MainActivity.getStringList(R.array.list_CoolingStatus);
    private final String[] conditioning_Status = MainActivity.getStringList (R.array.list_ConditioningStatus);
    private final String[] climate_Status = MainActivity.getStringList(R.array.list_ClimateStatus);
    private final String[] ptc_Relay = MainActivity.getStringList(R.array.list_PtcRelay);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climatech);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        //if (MainActivity.isZOE()) {
            addField(SID_EngineFanSpeed, 0);
            addField(SID_DcPower, 0);
            // addListener(SID_ChargingPower);
            addField(SID_HvCoolingState, 0);
            addField(SID_HvEvaporationTemp, 10000);
            // addField(SID_ClimaCompressorPower,0);
            addField(SID_Pressure, 1000);
            addField(SID_BatteryConditioningMode, 0);
            addField(SID_ClimaLoopMode, 0);
            addField(SID_PtcRelay1, 1000);
            addField(SID_PtcRelay2, 1000);
            addField(SID_PtcRelay3, 1000);
        //}
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
                int value;

                // get the text field
                switch (fieldId) {

                    case SID_EngineFanSpeed:
                        tv = findViewById(R.id.text_EFS);
                        break;
                    case SID_DcPower:
                        tv = findViewById(R.id.text_DCP);
                        break;
                    // case SID_ChargingPower:
                    //     tv = (TextView) findViewById(R.id.text_CPO);
                    //     break;
                    case SID_HvCoolingState:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_HCS);
                        if (tv != null && cooling_Status != null && value >= 0 && value < cooling_Status.length)
                            tv.setText(cooling_Status[value]);
                        tv = null;
                        break;
                    case SID_HvEvaporationTemp:
                        tv = findViewById(R.id.text_HET);
                        break;
                    case SID_Pressure:
                        tv = findViewById(R.id.text_PRE);
                        break;
                    case SID_BatteryConditioningMode:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_HCM);
                        if (tv != null && conditioning_Status != null && value >= 0 && value < conditioning_Status.length)
                            tv.setText(conditioning_Status[value]);
                        tv = null;
                        break;
                    case SID_ClimaLoopMode:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_CLM);
                        if (tv != null && climate_Status != null && value >= 0 && value < climate_Status.length)
                            tv.setText(climate_Status[value]);
                        tv = null;
                        break;
                    //case SID_ClimaCompressorPower:
                        // tv = findViewById(R.id.text_CPW);
                        // break;
                    case SID_PtcRelay1:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_PTC1);
                        if (tv != null && ptc_Relay != null && value >= 0 && value < ptc_Relay.length)
                            tv.setText(ptc_Relay[value]);
                        tv = null;
                        break;
                    case SID_PtcRelay2:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_PTC2);
                        if (tv != null && ptc_Relay != null && value >= 0 && value < ptc_Relay.length)
                            tv.setText(ptc_Relay[value]);
                        tv = null;
                        break;
                    case SID_PtcRelay3:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_PTC3);
                        if (tv != null && ptc_Relay != null && value >= 0 && value < ptc_Relay.length)
                            tv.setText(ptc_Relay[value]);
                        tv = null;
                        break;

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