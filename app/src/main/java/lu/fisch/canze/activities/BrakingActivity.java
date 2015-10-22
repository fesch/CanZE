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
    public static final String SID_HBB_Malfunction                      = "130.11"; //10
    public static final String SID_EB_Malfunction                       = "130.16";
    public static final String SID_EB_In_Progress                       = "130.18";
    public static final String SID_HBA_Activation_Request               = "130.40";
    public static final String SID_Pressure_Buildup                     = "130.42";
    public static final String SID_ElecBrakeWheels_Torque_Request       = "130.20";
    public static final String SID_DriverBrakeWheel_Torque_Request      = "130.44";
    public static final String SID_Friction_Torque                      = "18a.27"; //10
    public static final String SID_Braking_Pressure                     = "352.24"; //40

    // ISO-TP data
    //public static final String SID_EVC_RealSpeed                        = "7ec.622003.24"; //  (EVC) <<-- this one os not used but added to see if a non-free query added improves stability.

    public static final String hbb_Malfunction  [] = {"unavailable", "OK", "Not OK"};
    public static final String eb_Malfunction   [] = {"unavailable", "OK", "Not OK"};
    public static final String eb_Inprogress    [] = {"unavailable", "In progress", "Not in progress"};
    public static final String hba_actReq       [] = {"unavailable", "Activation request", "No activation request"};
    public static final String pressure_buildup [] = {"unavailable", "False", "True"};

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

        // Make sure to add ISO-TP listeners grouped by ID

        addListener(SID_HBB_Malfunction);
        addListener(SID_EB_Malfunction);
        addListener(SID_EB_In_Progress);
        addListener(SID_HBA_Activation_Request);
        addListener(SID_Pressure_Buildup);
        addListener(SID_ElecBrakeWheels_Torque_Request);
        addListener(SID_DriverBrakeWheel_Torque_Request);
        addListener(SID_Friction_Torque);
        addListener(SID_Braking_Pressure);

        //addListener(SID_EVC_RealSpeed); // unhandled
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
                String value = "";
                ProgressBar pb = null;

                // get the text field
                switch (fieldId) {
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
                        break;
                    case SID_ElecBrakeWheels_Torque_Request:
                        pb = (ProgressBar) findViewById(R.id.pb_eb_torque_request);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_DriverBrakeWheel_Torque_Request:
                        pb = (ProgressBar) findViewById(R.id.pb_driver_torque_request);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_Friction_Torque:
                        pb = (ProgressBar) findViewById(R.id.pb_friction_torque);
                        pb.setProgress((int) field.getValue());
                        break;
                    case SID_Braking_Pressure:
                        pb = (ProgressBar) findViewById(R.id.pb_braking_pressure);
                        pb.setProgress((int) field.getValue());
                        break;
                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText(value);
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