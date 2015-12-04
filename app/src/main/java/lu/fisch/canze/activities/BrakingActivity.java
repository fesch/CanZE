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
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

public class BrakingActivity extends CanzeActivity implements FieldListener {

    // for ISO-TP optimization to work, group all identical CAN ID's together when calling addListener

    // free data
    public static final String SID_Coasting_Torque                   = "18a.27"; //10ms Friction torque means EMULATED friction, what we'd call coasting
//  public static final String SID_ElecBrakeWheels_Torque_Request       = "130.20"; // wheel torque the car wants from the motor
//  public static final String SID_TotalPotentialResistiveWheelsTorque  = "1f8.16"; //10ms
    public static final String SID_ElecBrakeWheelsTorqueApplied         = "1f8.28"; //10ms
    //  public static final String SID_HBB_Malfunction                      = "130.11"; //10ms
//  public static final String SID_EB_Malfunction                       = "130.16";
//  public static final String SID_EB_In_Progress                       = "130.18";
//  public static final String SID_HBA_Activation_Request               = "130.40";
//  public static final String SID_Pressure_Buildup                     = "130.42";
    public static final String SID_DriverBrakeWheel_Torque_Request      = "130.44"; // braking wheel torque the driver wants

//    public static final String SID_Braking_Pressure                     = "352.24"; //40ms We still don't know if braking pressure correlates to torque

    public static final String hbb_Malfunction  [] = {"unavailable", "OK", "Not OK"};
    public static final String eb_Malfunction   [] = {"unavailable", "OK", "Not OK"};
    public static final String eb_Inprogress    [] = {"unavailable", "In progress", "Not in progress"};
    public static final String hba_actReq       [] = {"unavailable", "Activation request", "No activation request"};
    public static final String pressure_buildup [] = {"unavailable", "False", "True"};

    private double driverBrakeWheel_Torque_Request = 0;
    private double coasting_Torque = 0;

    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braking);
        initListeners();
    }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            MainActivity.device.addActivityField(field);
            subscribedFields.add(field);
        }
        else
        {
            MainActivity.toast("sid " + sid + " does not exist in class Fields");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // free up the listeners again
        for (Field field : subscribedFields) {
            field.removeListener(this);
        }
        subscribedFields.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // initialise the widgets
        initListeners();
    }

    private void initListeners() {

        subscribedFields = new ArrayList<>();

        // Make sure to add ISO-TP listeners grouped by ID

        addListener(SID_DriverBrakeWheel_Torque_Request);
        addListener(SID_ElecBrakeWheelsTorqueApplied);
//      addListener(SID_ElecBrakeWheels_Torque_Request);
//      addListener(SID_TotalPotentialResistiveWheelsTorque);
        addListener(SID_Coasting_Torque);
//      addListener(SID_Braking_Pressure);
//      addListener(SID_HBB_Malfunction);
//      addListener(SID_EB_Malfunction);
//      addListener(SID_EB_In_Progress);
//      addListener(SID_HBA_Activation_Request);
//      addListener(SID_Pressure_Buildup);
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
                //String value = "";
                ProgressBar pb;

                // get the text field
                switch (fieldId) {
                    case SID_DriverBrakeWheel_Torque_Request:
                        driverBrakeWheel_Torque_Request = field.getValue() + coasting_Torque;
                        pb = (ProgressBar) findViewById(R.id.pb_driver_torque_request);
                        pb.setProgress((int) driverBrakeWheel_Torque_Request);
                        tv = (TextView) findViewById(R.id.text_driver_torque_request);
                        if (tv != null) tv.setText("" + ((int)driverBrakeWheel_Torque_Request) + " Nm");
                        break;
                    case SID_ElecBrakeWheelsTorqueApplied:
                        double elecBrakeWheelsTorqueApplied = field.getValue() + coasting_Torque;
                        pb = (ProgressBar) findViewById(R.id.pb_ElecBrakeWheelsTorqueApplied);
                        pb.setProgress((int) elecBrakeWheelsTorqueApplied);
                        tv = (TextView) findViewById(R.id.text_ElecBrakeWheelsTorqueApplied);
                        if (tv != null) tv.setText("" + ((int)elecBrakeWheelsTorqueApplied) + " Nm");

                        double diff_friction_torque = driverBrakeWheel_Torque_Request - elecBrakeWheelsTorqueApplied;
                        pb = (ProgressBar) findViewById(R.id.pb_diff_friction_torque);
                        pb.setProgress((int) diff_friction_torque);
                        tv = (TextView) findViewById(R.id.text_diff_friction_torque);
                        if (tv != null) tv.setText("" + ((int) diff_friction_torque) + " Nm");
                        break;
/*                    case SID_TotalPotentialResistiveWheelsTorque:
                      pb = (ProgressBar) findViewById(R.id.pb_TotalPotentialResistiveWheelsTorque);
                      pb.setProgress((int) field.getValue());
                      break;
                    case SID_ElecBrakeWheels_Torque_Request:
                      pb = (ProgressBar) findViewById(R.id.pb_eb_torque_request);
                      pb.setProgress((int) field.getValue());
                      break;*/
                    case SID_Coasting_Torque:
                        coasting_Torque = field.getValue() * 9.3; // it seems this torque is given in motor torque, not in wheel torque. Maybe another adjustment by a factor 05 is needed (two wheels)
                        //  pb = (ProgressBar) findViewById(R.id.pb_friction_torque);
                        //  pb.setProgress((int) field.getValue());
                        break;
/*                    case SID_Braking_Pressure:
                        pb = (ProgressBar) findViewById(R.id.pb_braking_pressure);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_HBB_Malfunction:
                        tv = (TextView) findViewById(R.id.text_hbb_malfunction);
                        value = hbb_Malfunction[(int)field.getValue()];
                        break;
                    case SID_EB_Malfunction:
                        tv = (TextView) findViewById(R.id.text_eb_malfunction);
                        value = eb_Malfunction[(int)field.getValue()];
                        break;
                    case SID_EB_In_Progress:
                        tv = (TextView) findViewById(R.id.text_eb_in_progress);
                        value = eb_Inprogress[(int)field.getValue()];
                        break;
                    case SID_HBA_Activation_Request:
                        tv = (TextView) findViewById(R.id.text_hba_activation_request);
                        value = hba_actReq[(int)field.getValue()];
                        break;
                    case SID_Pressure_Buildup:
                        tv = (TextView) findViewById(R.id.text_pressure_buildup);
                        value = pressure_buildup[(int)field.getValue()];
                        break;*/
                }
                // set regular new content, all exeptions handled above
/*                if (tv != null) {
                    tv.setText(value);
                }*/

                tv = (TextView) findViewById(R.id.textDebug);
                tv.setText(fieldId);
            }
        });

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

}