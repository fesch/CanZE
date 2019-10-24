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
public class AuxBattTechActivity extends CanzeActivity implements FieldListener, DebugListener {

    private final String[] charging_Status = MainActivity.getStringList(R.array.list_ChargingStatus2);
    private final String[] vehicle_Status = MainActivity.getStringList(R.array.list_VehicleState);
    private final String[] aux_Status = MainActivity.getStringList(R.array.list_AuxStatus);

    private  static final String SID_AuxVoltage                       = "7ec.622005.24"; //"7bb.6101.224";
    private  static final String SID_AuxStatus                        = "638.37";
    private  static final String SID_VehicleState                     = "35c.5";
    private  static final String SID_ChargingStatusDisplay            = "65b.41";
    private  static final String SID_VoltageUnderLoad                 = "7ec.623485.24"; // Voltage measurement given by BCS Battery Current Sensor
    private  static final String SID_CurrentUnderLoad                 = "7ec.623484.24"; // Current measurement given by BCS Battery Current Sensor


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auxbatt);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_AuxVoltage);
        addField(SID_AuxStatus, 1000);
        addField(SID_VehicleState);
        addField(SID_ChargingStatusDisplay, 1000);
        addField(SID_VoltageUnderLoad, 6000);
        addField(SID_CurrentUnderLoad, 6000);
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

                    case SID_AuxVoltage:
                        tv = findViewById(R.id.text_12V);
                        break;
                    case SID_AuxStatus:
                        tv = findViewById(R.id.textAuxStatus);
                        value = (int) field.getValue();
                        if (tv != null && aux_Status != null && value >= 0 && value <= 7)
                            tv.setText(aux_Status[value]);
                        tv = null;
                        break;
                    case SID_VehicleState:
                        tv = findViewById(R.id.text_vehicle_state);
                        value = (int) field.getValue();
                        if (tv != null && vehicle_Status != null && value >= 0 && value <= 7)
                            tv.setText(vehicle_Status[value]);
                        tv = null;
                        break;
                    case SID_ChargingStatusDisplay:
                        tv = findViewById(R.id.textChaStatus);
                        value = (int) field.getValue();
                        if (tv != null && charging_Status != null && value >= 0 && value <= 7)
                            tv.setText(charging_Status[value]);
                        tv = null;
                        break;
                    case SID_VoltageUnderLoad:
                        tv = findViewById(R.id.textVoltageLoad);
                        break;
                    case SID_CurrentUnderLoad:
                        tv = findViewById(R.id.textCurrentLoad);
                        break;
                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText(String.format(Locale.getDefault(), "%.1f", field.getValue()));
                }

            }
        });

    }

}