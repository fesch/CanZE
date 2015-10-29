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
 * Created by jeroen on 27-10-15.
 */
public class HeatmapBatcompActivity extends CanzeActivity implements FieldListener {

    public static final String SID_Preamble_CompartmentTemperatures = "7bb.6104."; // (LBC)

    private ArrayList<Field> subscribedFields;

    private double totalTemp = 60;
    private double lastVal [] = {0,15,15,15,15,15,15,15,15,15,15,15,15};
    private int lastCell = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap_batcomp);

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
        if(Fields.getInstance().getCar() == Fields.CAR_ZOE) {
            lastCell = 12;
            totalTemp = 180;
        }
        for (int i = 1; i <= lastCell; i++) {
            String sid = SID_Preamble_CompartmentTemperatures + (8 + i * 24); // remember, first is pos 32, i starts s at 1
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
                TextView tv = null;

                // get the text field

                if (fieldId.startsWith(SID_Preamble_CompartmentTemperatures)) {
                    int cell = (Integer.parseInt(fieldId.split("[.]")[2]) - 8) / 24; // cell is 1-based
                    double value = field.getValue();
                    totalTemp += (value - lastVal[cell]);
                    lastVal[cell] = value;
                    tv = (TextView) findViewById(getResources().getIdentifier("text_comp_" + cell + "_temp", "id", getPackageName()));
                    if (tv != null) {
                        tv.setText("" + Math.round(value));
                        int color = (int) (50 * (value - (totalTemp / lastCell))); // color is temp minus mean
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
                tv.setText(fieldId + ":" + (totalTemp / lastCell));
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