package lu.fisch.canze;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

public class DrivingActivity extends AppCompatActivity implements FieldListener {

    public static final String SID_SoC = "654.24";
    public static final String SID_RangeEstimate = "654.42";
    public static final String SID_RealSpeed = "5d7.0";
    public static final String SID_Pedal = "186.40";
    public static final String SID_CcPedal = "18a.16";
    public static final String SID_Torque = "77e.623025.24";
    public static final String SID_TractionBatteryVoltage = "7ec.623203.24";
    public static final String SID_TractionBatteryCurrent = "7ec.623204.24";
    public static final String SID_CompartmentTemperaturesPreamble = "7bb.6104.";

    double dcVolt = 0; // holds the DC voltage, so we can calculate the power when the amps come in
    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        subscribedFields = new ArrayList<>();
        addListener(SID_SoC);
        addListener(SID_RangeEstimate);
        addListener(SID_RealSpeed);
        addListener(SID_Pedal);
        addListener(SID_CcPedal);
        addListener(SID_Torque);
        addListener(SID_TractionBatteryVoltage);
        addListener(SID_TractionBatteryCurrent);

        // Battery compartment temperatures
        for (int i = 32; i <= 296; i += 24) {
            String sid = SID_CompartmentTemperaturesPreamble + i;
            addListener(sid);
        }
    }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        field.addListener(this);
        MainActivity.device.addField(field);
        subscribedFields.add(field);
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
                ProgressBar pb = null;

                // get the text field
                switch (fieldId) {
                    case SID_SoC:
                        tv = (TextView) findViewById(R.id.textSOC);
                        break;
                    case SID_Pedal:
                        pb = (ProgressBar) findViewById(R.id.pedalBar);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_CcPedal:
                        pb = (ProgressBar) findViewById(R.id.ccPedalBar);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_RealSpeed:
                        tv = (TextView) findViewById(R.id.textRealSpeed);
                        break;
                    case SID_Torque:
                        tv = (TextView) findViewById(R.id.textTorque);
                        break;
                    case SID_RangeEstimate:
                        tv = (TextView) findViewById(R.id.textKMA);
                        break;
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
                    case SID_CompartmentTemperaturesPreamble + "32":
                        tv = (TextView) findViewById(R.id.text_comp_1_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "56":
                        tv = (TextView) findViewById(R.id.text_comp_2_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "80":
                        tv = (TextView) findViewById(R.id.text_comp_3_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "104":
                        tv = (TextView) findViewById(R.id.text_comp_4_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "128":
                        tv = (TextView) findViewById(R.id.text_comp_5_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "152":
                        tv = (TextView) findViewById(R.id.text_comp_6_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "176":
                        tv = (TextView) findViewById(R.id.text_comp_7_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "200":
                        tv = (TextView) findViewById(R.id.text_comp_8_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "224":
                        tv = (TextView) findViewById(R.id.text_comp_9_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "248":
                        tv = (TextView) findViewById(R.id.text_comp_10_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "272":
                        tv = (TextView) findViewById(R.id.text_comp_11_temp);
                        break;
                    case SID_CompartmentTemperaturesPreamble + "296":
                        tv = (TextView) findViewById(R.id.text_comp_12_temp);
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