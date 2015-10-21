package lu.fisch.canze;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.ProgressBar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

public class DrivingActivity extends CanzeActivity implements FieldListener {

    // for ISO-TP optimization to work, group all identical CAN ID's together when calling addListener

    // free data
    public static final String SID_Pedal                                = "186.40";
    public static final String SID_MeanEffectiveTorque                  = "18a.16";
    public static final String SID_BrakePressure                        = "352.24";
    public static final String SID_RealSpeed                            = "5d7.0";
    public static final String SID_SoC                                  = "654.24";
    public static final String SID_RangeEstimate                        = "654.42";

    // ISO-TP data
//  public static final String SID_EVC_SoC                              = "7ec.622002.24"; //  (EVC)
    public static final String SID_EVC_RealSpeed                        = "7ec.622003.24"; //  (EVC)
    public static final String SID_EVC_Odometer                         = "7ec.622006.24"; //  (EVC)
    public static final String SID_EVC_Pedal                            = "7ec.62202e.24"; //  (EVC)
    public static final String SID_EVC_TractionBatteryVoltage           = "7ec.623203.24"; //  (EVC)
    public static final String SID_EVC_TractionBatteryCurrent           = "7ec.623204.24"; //  (EVC)

    double dcVolt = 0; // holds the DC voltage, so we can calculate the power when the amps come in
    int odo = 0;
    int destOdo = 0; // have to init from save file
    double realSpeed = 0;
    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        final TextView kmToDest = (TextView) findViewById(R.id.LabelKmToDest);
        kmToDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // don't react if we do not have a live odo yet
                if (odo == 0) return;
                final Context context = DrivingActivity.this;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                final View kmToDestView = inflater.inflate(R.layout.set_dist_to_dest, null);

                // set dialog message
                alertDialogBuilder
                        .setView(kmToDestView)
                        .setTitle("REMAINING DISTANCE")
                        .setMessage("Please enter the distance to your destination. The display will estimate " +
                                "the remaining driving distance available in your battery on arrival")

                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText dialogKmToDest = (EditText) kmToDestView.findViewById(R.id.dialog_dist_to_dest);
                                if (dialogKmToDest != null){
                                    saveDestOdo(odo + Integer.parseInt(dialogKmToDest.getText().toString()));
                                }
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Double", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText dialogKmToDest = (EditText) kmToDestView.findViewById(R.id.dialog_dist_to_dest);
                                if (dialogKmToDest != null) {
                                    saveDestOdo(odo + 2 * Integer.parseInt(dialogKmToDest.getText().toString()));
                                }
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        initListeners();

    }

    private void saveDestOdo (int d) {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("destOdo", d);
        editor.commit();
        destOdo = d;
        Field field = MainActivity.fields.getBySID(SID_RangeEstimate);
        int kmInBat = (int) field.getValue();
        if (destOdo > odo) {
            setKmToDest("" + (destOdo - odo), "" + (kmInBat - destOdo + odo));
        } else {
            setKmToDest("0", "0");
        }
    }

    private void getDestOdo () {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        destOdo = settings.getInt("destOdo", 0);
        // get last persistent odo to calc
        Field field = MainActivity.fields.getBySID(SID_EVC_Odometer);
        odo = (int)field.getValue();
        field = MainActivity.fields.getBySID(SID_RangeEstimate);
        int kmInBat = (int) field.getValue();
        if (destOdo > odo) {
            setKmToDest("" + (destOdo - odo), "" + (kmInBat - destOdo + odo));
        } else {
            setKmToDest("0", "0");
        }
    }

    private void setKmToDest (String km1, String km2) {
        TextView tv;
        tv = (TextView) findViewById(R.id.textKmToDest);
        tv.setText(km1);
        tv = (TextView) findViewById(R.id.textKmAVailAtDest);
        tv.setText(km2);
    }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            MainActivity.device.addField(field);
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

        getDestOdo();

        // Make sure to add ISO-TP listeners grouped by ID

        addListener(SID_Pedal);
        addListener(SID_MeanEffectiveTorque);
        addListener(SID_BrakePressure);
        addListener(SID_RealSpeed);
        addListener(SID_SoC);
        addListener(SID_RangeEstimate);

        //addListener(SID_EVC_SoC);
        addListener(SID_EVC_Odometer);
        addListener(SID_EVC_TractionBatteryVoltage);
        addListener(SID_EVC_TractionBatteryCurrent);
        //addListener(SID_PEB_Torque);
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
                    case SID_EVC_Pedal:
                        pb = (ProgressBar) findViewById(R.id.pedalBar);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_MeanEffectiveTorque:
                        pb = (ProgressBar) findViewById(R.id.MeanEffectiveTorque);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_EVC_Odometer:
                        odo = (int) field.getValue();
                        tv = null;
                        break;
                    case SID_RealSpeed:
                    case SID_EVC_RealSpeed:
                        realSpeed = (Math.round(field.getValue() * 10.0) / 10.0);
                        tv = (TextView) findViewById(R.id.textRealSpeed);
                        break;
                    //case SID_PEB_Torque:
                    //    tv = (TextView) findViewById(R.id.textTorque);
                    //    break;
                    case SID_EVC_TractionBatteryVoltage: // DC volts
                        // save DC voltage for DC power purposes
                        dcVolt = field.getValue();
                        break;
                    case SID_EVC_TractionBatteryCurrent: // DC amps
                        // calculate DC power
                        double dcPwr = Math.round(dcVolt * field.getValue() / 100.0) / 10.0;
                        tv = (TextView) findViewById(R.id.textDcPwr);
                        tv.setText("" + (dcPwr));
                        tv = (TextView) findViewById(R.id.textConsumption);
                        if (realSpeed > 5) {
                            tv.setText("" + (Math.round(1000.0 * dcPwr / realSpeed) / 10.0) + " kWh/100km");
                        } else {
                            tv.setText("-");
                        }
                        tv = null;
                        break;
                    case SID_RangeEstimate:
                        int kmInBat = (int) field.getValue();
                        if (kmInBat > 0 && odo > 0 && destOdo > 0) { // we update only if there are no weird values
                            try {
                                if (destOdo > odo) {
                                    setKmToDest("" + (destOdo - odo), "" + (kmInBat - destOdo + odo));
                                } else {
                                    setKmToDest("0", "0");
                                }
                            } catch (Exception e) {
                            }
                        }
                        tv = null;
                        break;
                    case SID_BrakePressure:
                        pb = (ProgressBar) findViewById(R.id.FrictionBreaking);
                        pb.setProgress((int) (field.getValue() * realSpeed));
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

}