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

public class DrivingActivity extends CanzeActivity implements FieldListener, DebugListener {

    // for ISO-TP optimization to work, group all identical CAN ID's together when calling addListener

    // free data
    public static final String SID_DcPower                              = "1fd.48"; //EVC
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
    public static final String SID_EVC_Odometer                         = "7ec.622006.24";
    public static final String SID_EVC_TripBmeter                       = "7ec.6233de.24";
    public static final String SID_EVC_TripBenergy                      = "7ec.6233dd.24";

    private float  odo                              = 0;
    private float  destOdo                          = 0; // have to init from save file
    private float  tripBdistance                    = -1;
    private float  tripBenergy                      = -1;
    private float  startBdistance                   = -1;
    private float  startBenergy                     = -1;
    private float  tripDistance                     = -1;
    private float  tripEnergy                       = -1;
    private float  savedTripStart                   = 0;
    private double realSpeed                        = 0;
    private double driverBrakeWheel_Torque_Request  = 0;
    private double coasting_Torque                  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        final TextView distkmToDest = findViewById(R.id.LabelDistToDest);
        distkmToDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDistanceToDestination();
            }
        });

        final TextView tripConsumption = findViewById(R.id.LabelTripConsumption);
        tripConsumption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSavedTripStart();
            }
        });

        if (MainActivity.milesMode) {
            TextView tv;
            tv = findViewById(R.id.textSpeedUnit);
            tv.setText(MainActivity.getStringSingle(R.string.unit_SpeedMi));
            tv = findViewById(R.id.textConsumptionUnit);
            //tv.setText(MainActivity.getStringSingle(R.string.unit_ConsumptionMi));
            tv.setText(MainActivity.getStringSingle(R.string.unit_ConsumptionMiAlt));
        }
    }

    protected void initListeners() {
        getDestOdo();
        getSavedTripStart();

        // Make sure to add ISO-TP listeners grouped by ID
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_DcPower, 0);
        addField(SID_Pedal, 0);
        addField(SID_MeanEffectiveTorque, 0);
        addField(SID_DriverBrakeWheel_Torque_Request, 0);
        addField(SID_ElecBrakeWheelsTorqueApplied, 0);
        addField(SID_Coasting_Torque, 0);
        addField(SID_TotalPotentialResistiveWheelsTorque, 0);
        addField(SID_RealSpeed, 0);
        addField(SID_SoC, 7200);
        addField(SID_RangeEstimate, 7200);
        addField(SID_EVC_Odometer, 6000);
        addField(SID_EVC_TripBmeter, 6000);
        addField(SID_EVC_TripBenergy, 6000);
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
                        if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
                        EditText dialogDistToDest = distToDestView.findViewById(R.id.dialog_dist_to_dest);
                        if (dialogDistToDest != null) {
                            saveDestOdo(odo + Integer.parseInt(dialogDistToDest.getText().toString()));
                        }
                        dialog.cancel();
                    }
                })
                .setNeutralButton(R.string.button_Double, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
                        EditText dialogDistToDest = distToDestView.findViewById(R.id.dialog_dist_to_dest);
                        if (dialogDistToDest != null) {
                            saveDestOdo(odo + 2 * Integer.parseInt(dialogDistToDest.getText().toString()));
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.default_Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        // show it
        alertDialog.show();
    }


    private void saveDestOdo (float d) {
        if (!Float.isNaN(d)) {
            SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat("destOdo", d);
            editor.apply();
            destOdo = d;
            setDestToDest();
        }
    }

    private void getDestOdo () {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        destOdo = 0;
        try {
            destOdo = settings.getFloat("destOdo", 0);
        } catch (ClassCastException e) {
            // This happens when an old integer value is in the pref file
            destOdo = settings.getInt("destOdo", 0);
        }
        setDestToDest();
    }

    private void setDestToDest(int distance1, int distance2) {
        TextView tv;
        tv = findViewById(R.id.textDistToDest);
        tv.setText("" + distance1);
        tv = findViewById(R.id.textDistAVailAtDest);
        tv.setText("" + distance2);
    }

    private void setDestToDest() {
        TextView tv;
        tv = findViewById(R.id.textDistToDest);
        tv.setText("-");
        tv = findViewById(R.id.textDistAVailAtDest);
        tv.setText("-");
    }

    private void setSavedTripStart () {
        if (!Float.isNaN(odo) && odo != 0 && !Float.isNaN(tripBdistance) && tripBdistance != -1 && !Float.isNaN(tripBenergy) && tripBenergy != -1) {
            savedTripStart = odo - tripBdistance;
            startBdistance = tripBdistance;
            startBenergy = tripBenergy;
            SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat("savedTripStart", savedTripStart);
            editor.putFloat("startBdistance", startBdistance);
            editor.putFloat("startBenergy", startBenergy);
            editor.apply();
            MainActivity.toast(-100, "Trip Consumption reset");
        } else {
            MainActivity.toast(-100, "Could not reset Trip Consumption yet");
        }
    }

    private void getSavedTripStart () {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        savedTripStart = settings.getFloat("savedTripStart", 0);
        startBdistance = settings.getFloat("startBdistance", 0);
        startBenergy = settings.getFloat("startBenergy", 0);
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
                        tv = findViewById(R.id.textSOC);
                        break;
                    case SID_Pedal:
//                  case SID_EVC_Pedal:
                        pb = findViewById(R.id.pedalBar);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_MeanEffectiveTorque:
                        pb = findViewById(R.id.MeanEffectiveAccTorque);
                        pb.setProgress((int) (field.getValue() * MainActivity.reduction)); // --> translate from motor torque to wheel torque
                        break;
                    case SID_EVC_Odometer:
                        odo = (float)field.getValue();
                        //MainActivity.toast(String.format(Locale.getDefault(), "O:%.1f", odo));
                        tv = null;
                        break;
                    case SID_EVC_TripBmeter:
                        tripBdistance = (float)field.getValue();
                        tripDistance = tripBdistance - startBdistance;
                        //MainActivity.toast(String.format(Locale.getDefault(), "D:%.1f", tripBdistance));
                        tv = findViewById(R.id.textTripConsumption);
                        if ((odo - tripBdistance - 1) > savedTripStart) {
                            tv.setText("reset");
                            tv = findViewById(R.id.textTripDistance);
                            tv.setText("");
                            tv = findViewById(R.id.textTripEnergy);
                            tv.setText("");
                        } else if (tripEnergy <= 0 || tripDistance <= 0){
                            tv.setText("...");
                            tv = findViewById(R.id.textTripDistance);
                            tv.setText("...");
                            tv = findViewById(R.id.textTripEnergy);
                            tv.setText("...");
                        } else {
                            tv.setText(String.format(Locale.getDefault(), "%.1f", MainActivity.milesMode ? (tripDistance / tripEnergy) : (tripEnergy * 100.0 / tripDistance)));
                            tv = findViewById(R.id.textTripDistance);
                            tv.setText(String.format(Locale.getDefault(), "%.1f", tripDistance));
                            tv = findViewById(R.id.textTripEnergy);
                            tv.setText(String.format(Locale.getDefault(), "%.1f", tripEnergy));
                        }
                        tv = null;
                        break;
                    case SID_EVC_TripBenergy:
                        tripBenergy = (float)field.getValue();
                        tripEnergy = tripBenergy - startBenergy;
                        //MainActivity.toast(String.format(Locale.getDefault(), "E:%.1f", tripBenergy));
                        tv = null;
                        break;
                    case SID_MaxCharge:
                        tv = findViewById(R.id.text_max_charge);
                        break;
                    case SID_RealSpeed:
                        realSpeed = (Math.round(field.getValue() * 10.0) / 10.0);
                        tv = findViewById(R.id.textRealSpeed);
                        break;
                    case SID_DcPower:
                        double dcPwr = field.getValue();
                        tv = findViewById(R.id.textConsumption);
                        if (!MainActivity.milesMode && realSpeed > 5) {
                            tv.setText(String.format(Locale.getDefault(), "%.1f", 100.0 * dcPwr / realSpeed));
                        } else if (MainActivity.milesMode && dcPwr != 0) {
                            // real speed has already been returned in miles, so no conversions should be done
                            tv.setText(String.format(Locale.getDefault(), "%.2f", realSpeed / dcPwr));
                        } else {
                            tv.setText("-");
                        }
                        tv = null;
                        break;
                    case SID_RangeEstimate:
                        //int rangeInBat = (int) Utils.kmOrMiles(field.getValue());
                        int rangeInBat = (int) field.getValue();
                        if (rangeInBat > 0 && odo > 0 && destOdo > 0) { // we update only if there are no weird values
                            if (destOdo > odo) {
                                setDestToDest((int)(destOdo - odo), (int)(rangeInBat - destOdo + odo));
                            } else {
                                setDestToDest(0, 0);
                            }
                        } else {
                            setDestToDest();
                        }
                        tv = null;
                        break;

                    case SID_Coasting_Torque:
                        coasting_Torque = field.getValue() * MainActivity.reduction; // this torque is given in motor torque, not in wheel torque
                        break;

                    case SID_TotalPotentialResistiveWheelsTorque:
                        int tprwt = - ((int) field.getValue());
                        pb = findViewById(R.id.MaxBreakTorque);
                        if (pb != null) pb.setProgress(tprwt < 2047 ? tprwt : 10);
                        tv = null; // findViewById(R.id.textTPRWT);
                        break;

                    case SID_DriverBrakeWheel_Torque_Request:
                        driverBrakeWheel_Torque_Request = field.getValue() + coasting_Torque;
                        pb = findViewById(R.id.pb_driver_torque_request);
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
        } else if (id == R.id.action_resetTripConsumption) {
            setSavedTripStart();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}