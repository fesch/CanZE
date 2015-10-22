package lu.fisch.canze.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.interfaces.FieldListener;

public class StatsActivity extends CanzeActivity implements FieldListener {
    // ISO-TP data
    public static final String SID_EVC_SoC                              = "7ec.622002.24"; //  (EVC)
    public static final String SID_EVC_Odometer                         = "7ec.622006.24"; //  (EVC)
    public static final String SID_Preamble_CompartmentTemperatures = "7bb.6104."; // (LBC)

    int lastOdo = 0;
    int kmInBat = 0;
    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        subscribedFields = new ArrayList<>();

        // Make sure to add ISO-TP listeners grouped by ID


        addListener(SID_EVC_SoC);
        addListener(SID_EVC_Odometer);
        // Battery compartment temperatures
        int car = Fields.getInstance().getCar();
        int lastCell;
        if(car==Fields.CAR_ZOE) {
            lastCell = 296;
        }
        else
        {
            lastCell = 128;
        }
        for (int i = 32; i <= lastCell; i += 24) {
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
                    case SID_EVC_Odometer:
                        int odo = (int) field.getValue();

                        tv = null;
                        break;

                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText("" + field.getValue());
                }

                //tv = (TextView) findViewById(R.id.textDebug);
                //tv.setText(fieldId);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
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
