package lu.fisch.canze;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
//import android.widget.ProgressBar;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

public class DrivingActivity extends CanzeActivity implements FieldListener {

    // for ISO-TP optimization to work, group all identical CAN ID's together when calling addListener

    // free data
    public static final String SID_Pedal                                = "186.40";
    public static final String SID_CcPedal                              = "18a.16";
    public static final String SID_RealSpeed                            = "5d7.0";
    public static final String SID_SoC                                  = "654.24";

    // ISO-TP data
    public static final String SID_PEB_Torque                           = "77e.623025.24"; //  (PEB)
    public static final String SID_LBC_KmInBatt                         = "7bb.6161.136";  //  (LBC)
    public static final String SID_EVC_SoC                              = "7ec.622002.24"; //  (EVC)
    public static final String SID_EVC_RealSpeed                        = "7ec.622003.24"; //  (EVC)
    public static final String SID_EVC_Odometer                         = "7ec.622006.24"; //  (EVC)
    public static final String SID_EVC_Pedal                            = "7ec.62202e.24"; //  (EVC)
    public static final String SID_EVC_TractionBatteryVoltage           = "7ec.623203.24"; //  (EVC)
    public static final String SID_EVC_TractionBatteryCurrent           = "7ec.623204.24"; //  (EVC)

    double dcVolt = 0; // holds the DC voltage, so we can calculate the power when the amps come in
    int lastOdo = 0;
    int kmInBat = 0;
    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        subscribedFields = new ArrayList<>();

        // Make sure to add ISO-TP listeners grouped by ID

        addListener(SID_Pedal);
        addListener(SID_CcPedal);
        addListener(SID_RealSpeed);

        addListener(SID_EVC_SoC);
        addListener(SID_EVC_Odometer);
        addListener(SID_EVC_TractionBatteryVoltage);
        addListener(SID_EVC_TractionBatteryCurrent);
        addListener(SID_PEB_Torque);
        addListener(SID_LBC_KmInBatt);

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
                    case SID_EVC_SoC:
                        tv = (TextView) findViewById(R.id.textSOC);
                        break;
                    case SID_Pedal:
                    case SID_EVC_Pedal:
                        pb = (ProgressBar) findViewById(R.id.pedalBar);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_CcPedal:
                        pb = (ProgressBar) findViewById(R.id.ccPedalBar);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_EVC_Odometer:
                        int odo = (int)field.getValue();
                        if (lastOdo < 1) lastOdo = odo;
                        if (odo != lastOdo) {
                            tv = (TextView) findViewById(R.id.textKmToDest);
                            int newKmToDest = Integer.parseInt("" + tv.getText());
                            newKmToDest -= (odo - lastOdo);
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
                        break;
                    case SID_RealSpeed:
                    case SID_EVC_RealSpeed:
                        tv = (TextView) findViewById(R.id.textRealSpeed);
                        break;
                    case SID_PEB_Torque:
                        tv = (TextView) findViewById(R.id.textTorque);
                        break;
                    case SID_EVC_TractionBatteryVoltage: // DC volts
                        // save DC voltage for DC power purposes
                        dcVolt = field.getValue();
                        break;
                    case SID_EVC_TractionBatteryCurrent: // DC amps
                        // calculate DC power
                        double dcPwr = (double) Math.round(dcVolt * field.getValue());
                        tv = (TextView) findViewById(R.id.textDcPwr);
                        tv.setText("" + (dcPwr));
                        break;
                    case SID_LBC_KmInBatt:
                        kmInBat = (int) field.getValue();
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