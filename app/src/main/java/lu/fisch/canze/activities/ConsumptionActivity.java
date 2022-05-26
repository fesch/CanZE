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

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

public class ConsumptionActivity extends CanzeActivity implements FieldListener, DebugListener {
    
    private int coasting_Torque                     = 0;
    private int driverBrakeWheel_Torque_Request     = 0;
    private int posTorque                           = 0;
    private int negTorque                           = 0;

    public void initListeners () {
        MainActivity.getInstance().setDebugListener(this);
        addField(Sid.TotalPositiveTorque);
        addField(Sid.TotalNegativeTorque);
        addField(Sid.TotalPotentialResistiveWheelsTorque, 7200);
        addField(Sid.AverageConsumption, 5000);
        if (MainActivity.isSpring()) {
            addField(Sid.DcPowerOut, 5000);
        } else addField(Sid.Instant_Consumption, 0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // dash = 1280x400

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float width = size.x;
        float height = size.y;
        //width = width / getResources().getDisplayMetrics().scaledDensity;
        height = height / getResources().getDisplayMetrics().scaledDensity;

        if(height<480 || width<480) {
            setContentView(R.layout.activity_consumption_dash);
        }
        else
        {
            if (MainActivity.milesMode) {
                setContentView(R.layout.activity_consumption_mi);
                if (MainActivity.isPh2()){
                    TableRow tr;
                    tr = findViewById(R.id.tableRowRangeMi);
                    tr.setVisibility(View.GONE);
                    tr.setEnabled(false);
                }
            } else {
                setContentView(R.layout.activity_consumption);
                if (MainActivity.isPh2()){
                    TableRow tr;
                    tr = findViewById(R.id.tableRowRange);
                    tr.setVisibility(View.GONE);
                    tr.setEnabled(false);
                }
            }
        }
    }

    /********************************/

    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                ProgressBar pb;
                TextView tv, tvS;

                switch (fieldId) {

                    case Sid.TotalPositiveTorque:
                        posTorque = (int)(field.getValue());
                        pb = findViewById(R.id.MeanEffectiveAccTorque);
                        pb.setProgress(posTorque);
                        tv = findViewById(R.id.text_wheel_torque);
                        if (tv != null) tv.setText((posTorque - negTorque) + " " + field.getUnit());
                        break;

                    case Sid.TotalNegativeTorque:
                        negTorque = (int)(field.getValue());
                        pb = findViewById(R.id.pb_driver_torque_request);
                        pb.setProgress(negTorque);
                        tv = findViewById(R.id.text_wheel_torque);
                        if (tv != null) tv.setText((posTorque - negTorque) + " " + field.getUnit());
                        break;

                    // negative blue bar
                    case Sid.TotalPotentialResistiveWheelsTorque:
                        int tprwt = -((int) field.getValue());
                        pb = findViewById(R.id.MaxBreakTorque);
                        if (pb != null) pb.setProgress(tprwt < 2047 ? tprwt : 10);
                        break;

                    // consumption
                    case Sid.Instant_Consumption:
                        double consumptionDbl = field.getValue();
                        int consumptionInt = (int)consumptionDbl;
                        tv = findViewById(R.id.text_instant_consumption_negative);
                        if (!Double.isNaN(consumptionDbl)) {
                            // progress bars are rescaled to miles by the layout
                            ((ProgressBar) findViewById(R.id.pb_instant_consumption_negative)).setProgress(-(Math.min(0, consumptionInt)));
                            ((ProgressBar) findViewById(R.id.pb_instant_consumption_positive)).setProgress(  Math.max(0, consumptionInt) );
                            if (!MainActivity.milesMode) {
                                tv.setText(consumptionInt + " " + field.getUnit());
                            } else if (consumptionDbl != 0.0) { // consumption is now in kWh/100mi, so rescale progress bar
                                // display the value in imperial format (100 / consumption, meaning mi/kwh)
                                tv.setText(String.format (Locale.getDefault(),"%.2f %s", (100.0 / consumptionDbl), MainActivity.getStringSingle(R.string.unit_ConsumptionMiAlt)));
                            } else {
                                tv.setText("-");
                            }
                        } else {
                            tv.setText("-");
                        }
                        break;

                    //consumption for Dacia Spring
                    case Sid.DcPowerOut:
                        double consumptionDblS = field.getValue();
                        int consumptionIntS = (int)consumptionDblS;
                        tvS = findViewById(R.id.text_instant_consumption_negative);
                        if (!Double.isNaN(consumptionDblS)) {
                            // progress bars are rescaled to miles by the layout
                            ((ProgressBar) findViewById(R.id.pb_instant_consumption_negative)).setProgress(-(Math.min(0, consumptionIntS)));
                            ((ProgressBar) findViewById(R.id.pb_instant_consumption_positive)).setProgress(  Math.max(0, consumptionIntS) );
                            if (!MainActivity.milesMode) {
                                tvS.setText(consumptionIntS + " " + field.getUnit());
                            } else if (consumptionDblS != 0.0) { // consumption is now in kWh/100mi, so rescale progress bar
                                // display the value in imperial format (100 / consumption, meaning mi/kwh)
                                tvS.setText(String.format (Locale.getDefault(),"%.2f %s", (100.0 / consumptionDblS), MainActivity.getStringSingle(R.string.unit_ConsumptionMiAlt)));
                            } else {
                                tvS.setText("-");
                            }
                        } else {
                            tvS.setText("-");
                        }
                        break;
                }
            }
            });
    }

}
