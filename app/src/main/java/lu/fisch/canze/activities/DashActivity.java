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
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;

public class DashActivity extends CanzeActivity {

    private static final String SID_MeanEffectiveTorque                  = "186.16"; //EVC
    private static final String SID_TotalPotentialResistiveWheelsTorque  = "1f8.16"; //UBP 10ms
    private static final String SID_DriverBrakeWheel_Torque_Request      = "130.44"; //UBP braking wheel torque the driver wants
    private static final String SID_Coasting_Torque                      = "18a.27"; //10ms Friction torque means EMULATED friction, what we'd call coasting
    private static final String SID_Instant_Consumption                  = "800.6100.24";

    private int coasting_Torque                     = 0;
    private int driverBrakeWheel_Torque_Request     = 0;
    private int tempTorque                          = 0;

    public void initListeners () {
        addField(SID_MeanEffectiveTorque, 0);
        addField(SID_DriverBrakeWheel_Torque_Request, 0);
        addField(SID_Coasting_Torque, 0);
        addField(SID_TotalPotentialResistiveWheelsTorque, 7200);
        addField(SID_Instant_Consumption, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                ProgressBar pb;
                TextView tv;
                double consumption;

                switch (fieldId) {
                    // positive torque
                    case SID_MeanEffectiveTorque:
                        tempTorque = (int)(field.getValue() * MainActivity.reduction); // --> translate from motor torque to wheel torque
                        pb = findViewById(R.id.MeanEffectiveAccTorque);
                        pb.setProgress(tempTorque);
                        if (tempTorque <= 1) break;
                        tv = findViewById(R.id.text_wheel_torque);
                        if (tv != null) tv.setText(tempTorque + " " + field.getUnit());
                        break;

                    // negative torque
                    case SID_DriverBrakeWheel_Torque_Request:
                        driverBrakeWheel_Torque_Request = (int)field.getValue();
                        tempTorque = driverBrakeWheel_Torque_Request + coasting_Torque;
                        pb = findViewById(R.id.pb_driver_torque_request);
                        if (pb != null) pb.setProgress(tempTorque);
                        if (tempTorque <= 1) break;
                        tv = findViewById(R.id.text_wheel_torque);
                        if (tv != null) tv.setText(-tempTorque + " " + field.getUnit());
                        break;
                    case SID_Coasting_Torque:
                        coasting_Torque = (int)(field.getValue() * MainActivity.reduction); // torque is given in motor torque, not in wheel torque
                        tempTorque = driverBrakeWheel_Torque_Request + coasting_Torque;
                        pb = findViewById(R.id.pb_driver_torque_request);
                        if (pb != null) pb.setProgress(tempTorque);
                        if (tempTorque <= 1) break;
                        tv = findViewById(R.id.text_wheel_torque);
                        if (tv != null) tv.setText(-tempTorque + " " + field.getUnit());
                        break;

                    // negative blue bar
                    case SID_TotalPotentialResistiveWheelsTorque:
                        int tprwt = -((int) field.getValue());
                        pb = findViewById(R.id.MaxBreakTorque);
                        if (pb != null) pb.setProgress(tprwt < 2047 ? tprwt : 10);
                        break;
                }
            }
        });
    }

}
