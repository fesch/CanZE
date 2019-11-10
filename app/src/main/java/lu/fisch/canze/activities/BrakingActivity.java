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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

public class BrakingActivity extends CanzeActivity implements FieldListener, DebugListener {

    // for ISO-TP optimization to work, group all identical CAN ID's together when calling addListener

    // free data
    private static final String SID_TotalPotentialResistiveWheelsTorque  = "1f8.16"; // UBP 10ms
    private static final String SID_FrictionTorque                       = "800.6101.24";
    private static final String SID_ElecBrakeTorque                      = "800.610a.24";

    private double frictionTorque = 0;
    private double elecBrakeTorque = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braking);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_TotalPotentialResistiveWheelsTorque);
        addField(SID_FrictionTorque);
        addField(SID_ElecBrakeTorque);
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
                TextView tv;
                ProgressBar pb;

                // get the text field
                switch (fieldId) {

                    case SID_TotalPotentialResistiveWheelsTorque: //bluebar
                        int tprwt = - ((int) field.getValue());
                        pb = findViewById(R.id.MaxBreakTorque);
                        if (pb != null) pb.setProgress(tprwt < 2047 ? tprwt : 20);
                        break;

                    case SID_FrictionTorque:
                        frictionTorque = field.getValue();
                        pb = findViewById(R.id.pb_diff_friction_torque);
                        pb.setProgress((int) frictionTorque);
                        tv = findViewById(R.id.text_diff_friction_torque);
                        if (tv != null) tv.setText(String.format(Locale.getDefault(), "%.0f" + MainActivity.getStringSingle(R.string.unit_Nm), frictionTorque));
                        pb = findViewById(R.id.pb_driver_torque_request);
                        pb.setProgress((int)(frictionTorque + elecBrakeTorque));
                        tv = findViewById(R.id.text_driver_torque_request);
                        if (tv != null) tv.setText(String.format(Locale.getDefault(), "%.0f" + MainActivity.getStringSingle(R.string.unit_Nm), frictionTorque + elecBrakeTorque));
                        break;

                    case SID_ElecBrakeTorque:
                        elecBrakeTorque = field.getValue();
                        pb = findViewById(R.id.pb_ElecBrakeWheelsTorqueApplied);
                        pb.setProgress((int) elecBrakeTorque);
                        tv = findViewById(R.id.text_ElecBrakeWheelsTorqueApplied);
                        if (tv != null) tv.setText(String.format(Locale.getDefault(), "%.0f" + MainActivity.getStringSingle(R.string.unit_Nm), elecBrakeTorque));
                        pb = findViewById(R.id.pb_driver_torque_request);
                        pb.setProgress((int)(frictionTorque + elecBrakeTorque));
                        tv = findViewById(R.id.text_driver_torque_request);
                        if (tv != null) tv.setText(String.format(Locale.getDefault(), "%.0f" + MainActivity.getStringSingle(R.string.unit_Nm), frictionTorque + elecBrakeTorque));
                        break;

                }
            }
        });

    }
}