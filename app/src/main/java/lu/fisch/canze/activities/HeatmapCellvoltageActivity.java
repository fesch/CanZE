package lu.fisch.canze.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.interfaces.FieldListener;

/**
 * Heatmap by jeroen on 27-10-15.
 */
public class HeatmapCellvoltageActivity extends CanzeActivity implements FieldListener {

    public static final String SID_Preamble_CellVoltages1 = "7bb.6141."; // (LBC)
    public static final String SID_Preamble_CellVoltages2 = "7bb.6142."; // (LBC)

    private ArrayList<Field> subscribedFields;

    private double mean = 4;
    private double lastVoltage[] = {0,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4};
    private int lastCell = 96;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap_cellvoltage);

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
        for(Field field : subscribedFields)
        {
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

        // Battery compartment temperatures
        for (int i = 1; i <= 62; i++) {
            String sid = SID_Preamble_CellVoltages1 + (i * 16); // remember, first is pos 16, i starts s at 1
            addListener(sid);
        }
        for (int i = 63; i <= 96; i++) {
            String sid = SID_Preamble_CellVoltages2 + ((i - 62) * 16); // remember, first is pos 16, i starts s at 1
            addListener(sid);
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
                TextView tv;

                // get the text field
                int cell = 0;
                if (fieldId.startsWith(SID_Preamble_CellVoltages1)) {
                    cell = (Integer.parseInt(fieldId.split("[.]")[2])) / 16; // cell is 1-based
                } else if (fieldId.startsWith(SID_Preamble_CellVoltages2)) {
                    cell = (Integer.parseInt(fieldId.split("[.]")[2])) / 16 + 62; // cell is 1-based
                }
                if (cell > 0) {
                    if (cell == 1) {
                        mean = 0;
                        for (int i = 1; i <= lastCell; i++) {
                            mean += lastVoltage[i];
                        }
                        mean /= lastCell;
                    }
                    double value = field.getValue();
                    lastVoltage[cell] = value;
                    tv = (TextView) findViewById(getResources().getIdentifier("text_cell_" + cell + "_voltage", "id", getPackageName()));
                    if (tv != null) {
                        tv.setText("" + value);
                        int color = (int) (5000 * (value - mean)); // color is temp minus mean. 1mV difference is 5 color ticks
                        if (color > 62) {
                            color = 0xffffc0c0;
                        } else if (color > 0) {
                            color = 0xffc0c0c0 + (color * 0x010000); // one tick is one red
                        } else if (color >= -62 ){
                            color = 0xffc0c0c0 - color; // one degree below is a 16th blue added
                        } else {
                            color = 0xffc0c0ff;
                        }
                        tv.setBackgroundColor(color);
                    }
                }
                tv = (TextView) findViewById(R.id.textDebug);
                tv.setText(fieldId + ":" + mean);
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