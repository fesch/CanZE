package lu.fisch.canze;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.interfaces.FieldListener;

public class DrivingActivity extends AppCompatActivity implements FieldListener {

    // for ISO-TP optimization to work, group all identical CAN ID's together when calling addListener

    // free data
    //public static final String SID_Pedal                            = "186.40";
    //public static final String SID_CcPedal                          = "18a.16";
    public static final String SID_RealSpeed                        = "5d7.0";
    //public static final String SID_SoC                              = "654.24";

    // ISO-TP data
    public static final String SID_SoC                              = "7ec.622002.24"; //  (EVC)
    public static final String SID_Odometer                         = "7ec.622006.24"; //  (EVC)
    //public static final String SID_Pedal                            = "7ec.62202e.24"; // inquired data (EVC)
    //public static final String SID_Torque                           = "77e.623025.24"; //  (PEB)
    public static final String SID_TractionBatteryVoltage           = "7ec.623203.24"; //  (EVC)
    public static final String SID_TractionBatteryCurrent           = "7ec.623204.24"; //  (EVC)
    public static final String SID_Preamble_CompartmentTemperatures = "7bb.6104.";     // (LBC)
    public static final String SID_KmInBatt                         = "7bb.6161.136";

    double dcVolt = 0; // holds the DC voltage, so we can calculate the power when the amps come in
    int lastOdo = 0;
    int kmInBat = 0;
    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        subscribedFields = new ArrayList<>();
        addListener(SID_RealSpeed);
        addListener(SID_SoC);
        //addListener(SID_Odometer);
        //addListener(SID_Pedal);
        //addListener(SID_CcPedal);
        //addListener(SID_Torque);
        addListener(SID_TractionBatteryVoltage);
        addListener(SID_TractionBatteryCurrent);

        // Battery compartment temperatures
        int car = Fields.getInstance().getCar();
        int lastCompartment = (car==Fields.CAR_ZOE) ? 296 : 128;
        for (int i = 32; i <= lastCompartment; i += 24) {
            String sid = SID_Preamble_CompartmentTemperatures + i;
            addListener(sid);
        }
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
        // clear filters
        MainActivity.device.clearFields();
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
                //ProgressBar pb = null;

                // get the text field
                switch (fieldId) {
                    case SID_SoC:
                        tv = (TextView) findViewById(R.id.textSOC);
                        break;
                    //case SID_Pedal:
                    //    pb = (ProgressBar) findViewById(R.id.pedalBar);
                    //    pb.setProgress((int) field.getValue());
                    //    break;
                    //case SID_CcPedal:
                    //    pb = (ProgressBar) findViewById(R.id.ccPedalBar);
                    //    pb.setProgress((int) field.getValue());
                    //    break;
                    case SID_Odometer:
                        int odo = (int)field.getValue();
                        if (odo != lastOdo) {
                            tv = (TextView) findViewById(R.id.textKmToDest);
                            int newKmToDest = Integer.parseInt("" + tv.getText()) - odo + lastOdo;
                            if (newKmToDest >= 0)
                            {
                                tv.setText("" + newKmToDest);
                                tv = (TextView) findViewById(R.id.textKmAVailAtDest);
                                tv.setText("" + (kmInBat - newKmToDest));
                            }
                            else
                            {
                                tv.setText("0");
                                tv = (TextView) findViewById(R.id.textKmAVailAtDest);
                                tv.setText("0");
                            }
                            lastOdo = odo;
                        }
                        // do not display the odometer itself
                        tv = null;
                    case SID_RealSpeed:
                        tv = (TextView) findViewById(R.id.textRealSpeed);
                        break;
                    //case SID_Torque:
                    //    tv = (TextView) findViewById(R.id.textTorque);
                    //    break;
                    case SID_TractionBatteryVoltage: // DC volts
                        // save DC voltage for DC power purposes
                        dcVolt = field.getValue();
                        // continue
                        tv = (TextView) findViewById(R.id.textVolt);
                        break;
                    case SID_TractionBatteryCurrent: // DC amps
                        // calculate DC power
                        double dcPwr = (double) Math.round(dcVolt * field.getValue());
                        tv = (TextView) findViewById(R.id.textDcPwr);
                        tv.setText("" + (dcPwr));
                        // continue
                        tv = (TextView) findViewById(R.id.textAmps);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "32":
                        tv = (TextView) findViewById(R.id.text_comp_1_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "56":
                        tv = (TextView) findViewById(R.id.text_comp_2_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "80":
                        tv = (TextView) findViewById(R.id.text_comp_3_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "104":
                        tv = (TextView) findViewById(R.id.text_comp_4_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "128":
                        tv = (TextView) findViewById(R.id.text_comp_5_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "152":
                        tv = (TextView) findViewById(R.id.text_comp_6_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "176":
                        tv = (TextView) findViewById(R.id.text_comp_7_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "200":
                        tv = (TextView) findViewById(R.id.text_comp_8_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "224":
                        tv = (TextView) findViewById(R.id.text_comp_9_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "248":
                        tv = (TextView) findViewById(R.id.text_comp_10_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "272":
                        tv = (TextView) findViewById(R.id.text_comp_11_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "296":
                        tv = (TextView) findViewById(R.id.text_comp_12_temp);
                        break;
                    case SID_KmInBatt:
                        kmInBat = (int) field.getValue();
                        tv = null;
                        break;
                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText("" + field.getValue());
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