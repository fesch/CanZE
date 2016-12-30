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

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
public class ClimaTechActivity extends CanzeActivity implements FieldListener {

    public static final String SID_EngineFanSpeed                   = "42e.20";
    public static final String SID_DcPower                          = "800.6103.24";
    public static final String SID_ChargingPower                    = "42e.56";
    public static final String SID_HvCoolingState                   = "430.38";
    public static final String SID_HvEvaporationTemp                = "430.40";
    public static final String SID_BatteryConditioningMode          = "432.36";
    public static final String SID_ClimaLoopMode                    = "42a.48";

    public static final String cst_Status [] = {"No", "Cooling alone", "Cooling coupled", "Unavail"};
    public static final String plu_Status [] = {"Blow req", "Cool cond req", "Heat cond req", "Unavail"};
    public static final String clm_Status [] = {"Unavail", "AC", "AC deiceing","", "Heat pump", "", "Demisting", "Idle"};


    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climatech);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // initialise the widgets
        initListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListeners();
    }

    private void initListeners() {

        subscribedFields = new ArrayList<>();

        if (MainActivity.car==MainActivity.CAR_ZOE_Q210 || MainActivity.car == MainActivity.CAR_ZOE_R240) {
            addListener(SID_EngineFanSpeed);
            addListener(SID_DcPower);
            addListener(SID_ChargingPower);
            addListener(SID_HvCoolingState);
            addListener(SID_HvEvaporationTemp);
            addListener(SID_BatteryConditioningMode);
            addListener(SID_ClimaLoopMode);
        }
    }

    private void removeListeners () {
        // empty the query loop
        MainActivity.device.clearFields();
        // free up the listeners again
        for (Field field : subscribedFields) {
            field.removeListener(this);
        }
        subscribedFields.clear();
    }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            MainActivity.device.addActivityField(field);
            subscribedFields.add(field);
        } else {
            MainActivity.toast("sid " + sid + " does not exist in class Fields");
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
                    case SID_ChargingPower:
                        tv = (TextView) findViewById(R.id.text_CPO);
                        break;
                    case SID_HvCoolingState:
                        tv = (TextView) findViewById(R.id.text_HCS);
                        tv.setText(cst_Status[(int) field.getValue()]);
                        tv = null;
                        break;
                    case SID_HvEvaporationTemp:
                        tv = (TextView) findViewById(R.id.text_HET);
                        break;
                    case SID_BatteryConditioningMode:
                        tv = (TextView) findViewById(R.id.text_HCM);
                        tv.setText(cst_Status[(int) field.getValue()]);
                        tv = null;
                        break;
                    case SID_ClimaLoopMode:
                        tv = (TextView) findViewById(R.id.text_CLM);
                        tv.setText(clm_Status[(int) field.getValue()]);
                        tv = null;
                        break;

               }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText("" + (Math.round(field.getValue() * 10.0) / 10.0));
                }

                tv = (TextView) findViewById(R.id.textDebug);
                tv.setText(fieldId);
            }
        });

    }
}