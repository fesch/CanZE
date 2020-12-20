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
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.actors.Field;

public class DashActivity extends CanzeActivity {

    public void initListeners () {
        addField(Sid.TotalPotentialResistiveWheelsTorque, 7200);
        //addField(Sid.Instant_Consumption, 0);
        addField(Sid.TotalPositiveTorque, 0);
        addField(Sid.TotalNegativeTorque, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
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

                    case Sid.TotalPotentialResistiveWheelsTorque: //blue bar
                        int tprwt = -((int) field.getValue());
                        pb = findViewById(R.id.MaxBrakeTorque);
                        if (pb != null) pb.setProgress(tprwt < 2047 ? tprwt : 10);
                        tv = null; // findViewById(R.id.textTPRWT);
                        break;

                    case Sid.TotalNegativeTorque:
                        pb = findViewById(R.id.pb_driver_torque_request);
                        if (pb != null) pb.setProgress((int) field.getValue());
                        tv = null;
                        break;

                    case Sid.TotalPositiveTorque:
                        pb = findViewById(R.id.MeanEffectiveAccTorque);
                        pb.setProgress((int)field.getValue()); // --> translate from motor torque to wheel torque
                        break;
                }
            }
        });
    }

}
