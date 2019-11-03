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

    private static final String SID_TotalPotentialResistiveWheelsTorque  = "1f8.16"; //UBP 10ms
    private static final String SID_TotalPositiveTorque = "800.610b.24";
    private static final String SID_TotalNegativeTorque = "800.610c.24";


    public void initListeners () {
        addField(SID_TotalPotentialResistiveWheelsTorque, 7200);
        //addField(SID_Instant_Consumption, 0);
        addField(SID_TotalPositiveTorque, 0);
        addField(SID_TotalNegativeTorque, 0);
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

                    case SID_TotalPotentialResistiveWheelsTorque: //blue bar
                        int tprwt = -((int) field.getValue());
                        pb = findViewById(R.id.MaxBrakeTorque);
                        if (pb != null) pb.setProgress(tprwt < 2047 ? tprwt : 10);
                        tv = null; // findViewById(R.id.textTPRWT);
                        break;

                    case SID_TotalNegativeTorque:
                        pb = findViewById(R.id.pb_driver_torque_request);
                        if (pb != null) pb.setProgress((int) field.getValue());
                        tv = null;
                        break;

                    case SID_TotalPositiveTorque:
                        pb = findViewById(R.id.MeanEffectiveAccTorque);
                        pb.setProgress((int)field.getValue()); // --> translate from motor torque to wheel torque
                        break;
                }
            }
        });
    }

}
