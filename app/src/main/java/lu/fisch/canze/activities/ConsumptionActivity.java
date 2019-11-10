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
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

public class ConsumptionActivity extends CanzeActivity implements FieldListener, DebugListener {

    private static final String SID_TotalPositiveTorque = "800.610b.24";
    private static final String SID_TotalNegativeTorque = "800.610c.24";
    private static final String SID_TotalPotentialResistiveWheelsTorque  = "1f8.16"; //UBP 10ms
    private static final String SID_Instant_Consumption                  = "800.6100.24";

    private int coasting_Torque                     = 0;
    private int driverBrakeWheel_Torque_Request     = 0;
    private int posTorque                           = 0;
    private int negTorque                           = 0;

    public void initListeners () {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_TotalPositiveTorque);
        addField(SID_TotalNegativeTorque);
        addField(SID_TotalPotentialResistiveWheelsTorque, 7200);
        addField(SID_Instant_Consumption, 0);
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
            } else {
                setContentView(R.layout.activity_consumption);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
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
                TextView tv;

                switch (fieldId) {

                    case SID_TotalPositiveTorque:
                        posTorque = (int)(field.getValue());
                        pb = findViewById(R.id.MeanEffectiveAccTorque);
                        pb.setProgress(posTorque);
                        tv = findViewById(R.id.text_wheel_torque);
                        if (tv != null) tv.setText((posTorque - negTorque) + " " + field.getUnit());
                        break;

                    case SID_TotalNegativeTorque:
                        negTorque = (int)(field.getValue());
                        pb = findViewById(R.id.pb_driver_torque_request);
                        pb.setProgress(negTorque);
                        tv = findViewById(R.id.text_wheel_torque);
                        if (tv != null) tv.setText((posTorque - negTorque) + " " + field.getUnit());
                        break;

                    // negative blue bar
                    case SID_TotalPotentialResistiveWheelsTorque:
                        int tprwt = -((int) field.getValue());
                        pb = findViewById(R.id.MaxBreakTorque);
                        if (pb != null) pb.setProgress(tprwt < 2047 ? tprwt : 10);
                        break;

                    // consumption
                    case SID_Instant_Consumption:
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
                }
            }
            });
    }

}
