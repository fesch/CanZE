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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;
import lu.fisch.canze.activities.MainActivity;

public class DrivingActivity extends CanzeActivity implements FieldListener, DebugListener {

    // for ISO-TP optimization to work, group all identical CAN ID's together when calling addListener

    // free data
    public static final String SID_Consumption                          = "1fd.48"; //EVC
    public static final String SID_Pedal                                = "186.40"; //EVC
    public static final String SID_MeanEffectiveTorque                  = "186.16"; //EVC
    public static final String SID_Coasting_Torque                      = "18a.27"; //10ms Friction torque means EMULATED friction, what we'd call coasting
    public static final String SID_RealSpeed                            = "5d7.0";  //ESC-ABS
    public static final String SID_SoC                                  = "654.25"; //EVC
    public static final String SID_RangeEstimate                        = "654.42"; //EVC
    public static final String SID_DriverBrakeWheel_Torque_Request      = "130.44"; //UBP braking wheel torque the driver wants
    public static final String SID_ElecBrakeWheelsTorqueApplied         = "1f8.28"; //UBP 10ms
    public static final String SID_TotalPotentialResistiveWheelsTorque  = "1f8.16"; //UBP 10ms

    // ISO-TP data
    public static final String SID_MaxCharge                            = "7bb.6101.336";
    public static final String SID_EVC_Odometer                         = "7ec.622006.24"; //  (EVC)

    private int    odo                              = 0;
    private int    destOdo                          = 0; // have to init from save file
    private double realSpeed                        = 0;
    private double driverBrakeWheel_Torque_Request  = 0;
    private double coasting_Torque                  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        final TextView distkmToDest = (TextView) findViewById(R.id.LabelDistToDest);
        distkmToDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDistanceToDestination();
            }
        });

        if (MainActivity.milesMode) {
            TextView tv;
            tv = (TextView) findViewById(R.id.textSpeedUnit);
            tv.setText(MainActivity.getStringSingle(R.string.unit_SpeedMi));
            tv = (TextView) findViewById(R.id.textConsumptionUnit);
            tv.setText(MainActivity.getStringSingle(R.string.unit_ConsumptionMi));
        }
    }

    protected void initListeners() {
        getDestOdo();

        // Make sure to add ISO-TP listeners grouped by ID
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_Consumption, 0);
        addField(SID_Pedal, 0);
        addField(SID_MeanEffectiveTorque, 0);
        addField(SID_DriverBrakeWheel_Torque_Request, 0);
        addField(SID_ElecBrakeWheelsTorqueApplied, 0);
        addField(SID_Coasting_Torque, 0);
        addField(SID_TotalPotentialResistiveWheelsTorque, 7200);
        addField(SID_RealSpeed, 0);
        addField(SID_SoC, 7200);
        addField(SID_RangeEstimate, 7200);
        addField(SID_EVC_Odometer, 6000);
    }

    void setDistanceToDestination () {
        // don't react if we do not have a live odo yet
        if (odo == 0) return;
        final Context context = DrivingActivity.this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        // we allow this SuppressLint as this is a pop up Dialog
        @SuppressLint("InflateParams")
        final View distToDestView = inflater.inflate(R.layout.alert_dist_to_dest, null);

        // set dialog message
        alertDialogBuilder
                .setView(distToDestView)
                .setTitle(R.string.prompt_Distance)
                .setMessage(MainActivity.getStringSingle(R.string.prompt_SetDistance))

                .setCancelable(true)
                .setPositiveButton(R.string.default_Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
                        EditText dialogDistToDest = (EditText) distToDestView.findViewById(R.id.dialog_dist_to_dest);
                        if (dialogDistToDest != null) {
                            saveDestOdo(odo + Integer.parseInt(dialogDistToDest.getText().toString()));
                        }
                        dialog.cancel();
                    }
                })
                .setNeutralButton(R.string.button_Double, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
                        EditText dialogDistToDest = (EditText) distToDestView.findViewById(R.id.dialog_dist_to_dest);
                        if (dialogDistToDest != null) {
                            saveDestOdo(odo + 2 * Integer.parseInt(dialogDistToDest.getText().toString()));
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.default_Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        // show it
        alertDialog.show();
    }

    private void saveDestOdo (int d) {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("destOdo", d);
        editor.apply();
        destOdo = d;
        Field field = MainActivity.fields.getBySID(SID_RangeEstimate);
        int distInBat = (int) field.getValue();
        if (destOdo > odo) {
            setDestToDest("" + (destOdo - odo), "" + (distInBat - destOdo + odo));
        } else {
            setDestToDest("0", "0");
        }
    }

    private void getDestOdo () {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        destOdo = settings.getInt("destOdo", 0);
        // get last persistent odo to calc
        Field field = MainActivity.fields.getBySID(SID_EVC_Odometer);
        odo = (int)field.getValue();
        field = MainActivity.fields.getBySID(SID_RangeEstimate);
        int distInBat = (int) field.getValue();
        if (destOdo > odo) {
            setDestToDest("" + (destOdo - odo), "" + (distInBat - destOdo + odo));
        } else {
            setDestToDest("0", "0");
        }
    }

    private void setDestToDest(String distance1, String distance2) {
        TextView tv;
        tv = (TextView) findViewById(R.id.textDistToDest);
        tv.setText(distance1);
        tv = (TextView) findViewById(R.id.textDistAVailAtDest);
        tv.setText(distance2);
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
                ProgressBar pb;

                // get the text field
                switch (fieldId) {
                    case SID_SoC:
//                  case SID_EVC_SoC:
                        tv = (TextView) findViewById(R.id.textSOC);
                        break;
                    case SID_Pedal:
//                  case SID_EVC_Pedal:
                        pb = (ProgressBar) findViewById(R.id.pedalBar);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_MeanEffectiveTorque:
                        pb = (ProgressBar) findViewById(R.id.MeanEffectiveAccTorque);
                        pb.setProgress((int) (field.getValue() * MainActivity.reduction)); // --> translate from motor torque to wheel torque
                        break;
                    case SID_EVC_Odometer:
                        odo = (int ) field.getValue();
                        //odo = (int) Utils.kmOrMiles(field.getValue());
                        tv = null;
                        break;
                    case SID_MaxCharge:
                        tv = (TextView) findViewById(R.id.text_max_charge);
                        break;
                    case SID_RealSpeed:
                        realSpeed = (Math.round(field.getValue() * 10.0) / 10.0);
                        tv = (TextView) findViewById(R.id.textRealSpeed);
                        break;
                    case SID_Consumption:
                        double dcPwr = field.getValue();
                        tv = (TextView) findViewById(R.id.textConsumption);
                        if (realSpeed > 5) {
                            tv.setText(String.format(Locale.getDefault(), "%.1f", 100.0 * dcPwr / realSpeed));
                            // tv.setText("" + (Math.round(1000.0 * dcPwr / realSpeed) / 10.0));
                        } else {
                            tv.setText("-");
                        }
                        tv = null;
                        break;
                    case SID_RangeEstimate:
                        //int rangeInBat = (int) Utils.kmOrMiles(field.getValue());
                        int rangeInBat = (int) field.getValue();
                        if (rangeInBat > 0 && odo > 0 && destOdo > 0) { // we update only if there are no weird values
                            try {
                                if (destOdo > odo) {
                                    setDestToDest("" + (destOdo - odo), "" + (rangeInBat - destOdo + odo));
                                } else {
                                    setDestToDest("0", "0");
                                }
                            } catch (Exception e) {
                                // empty
                            }
                        }
                        tv = null;
                        break;

                    case SID_Coasting_Torque:
                        coasting_Torque = field.getValue() * MainActivity.reduction; // this torque is given in motor torque, not in wheel torque
                        break;

                    case SID_TotalPotentialResistiveWheelsTorque:
                        int tprwt = - ((int) field.getValue());
                        pb = (ProgressBar) findViewById(R.id.MaxBreakTorque);
                        if (pb != null) pb.setProgress(tprwt < 2047 ? tprwt : 10);
                        tv = null; // (TextView) findViewById(R.id.textTPRWT);
                        break;

                    case SID_DriverBrakeWheel_Torque_Request:
                        driverBrakeWheel_Torque_Request = field.getValue() + coasting_Torque;
                        pb = (ProgressBar) findViewById(R.id.pb_driver_torque_request);
                        if (pb != null) pb.setProgress((int) driverBrakeWheel_Torque_Request);
                        tv = null;
                        break;
                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText(String.format(Locale.getDefault(), "%.1f", field.getValue()));
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present. This menu
        // is another way to set the distance
        getMenuInflater().inflate(R.menu.menu_driving, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setDistanceToDestination) {
            setDistanceToDestination();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}