package lu.fisch.canze;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
public class ChargingActivity extends AppCompatActivity implements FieldListener {

    double dcVolt = 0; // holds the DC voltage, so we can calculate the power when the amps come in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Field field;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging);

        field = MainActivity.fields.getBySID("7bb.6101.336"); // Max charge
        field.addListener(this);
        MainActivity.device.addField(field);

        field = MainActivity.fields.getBySID("42e.38"); // AC pilot
        field.addListener(this);
        MainActivity.device.addField(field);

        field = MainActivity.fields.getBySID("42e.56"); // Energy to full
        field.addListener(this);
        MainActivity.device.addField(field);

        field = MainActivity.fields.getBySID("654.32"); // Time to full
        field.addListener(this);
        MainActivity.device.addField(field);

        field = MainActivity.fields.getBySID("654.24"); // SOC
        field.addListener(this);
        MainActivity.device.addField(field);

        field = MainActivity.fields.getBySID("658.32"); // SOH
        field.addListener(this);
        MainActivity.device.addField(field);

        field = MainActivity.fields.getBySID("654.42"); // Kilometers Available
        field.addListener(this);
        MainActivity.device.addField(field);

        field = MainActivity.fields.getBySID("7ec.623203.16"); // HV Voltage
        field.addListener(this);
        MainActivity.device.addField(field);

        field = MainActivity.fields.getBySID("7ec.623204.16"); // HV Current
        field.addListener(this);
        MainActivity.device.addField(field);

        // Battery compartment temperatures
        for (int i=32; i<= 296; i+=24) {
            field = MainActivity.fields.getBySID("7bb.6104." + i);
            field.addListener(this);
            MainActivity.device.addField(field);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // free up the listener again
        MainActivity.fields.getBySID("186.40").removeListener(this);
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

                // get the text field
                switch (fieldId) {

                    case "7bb.6101.336":
                        tv = (TextView) findViewById(R.id.text_max_charge);
                        break;
                    case "42e.38":
                        tv = (TextView) findViewById(R.id.text_max_pilot);
                        break;
                    case "42e.56":
                        tv = (TextView) findViewById(R.id.textETF);
                        break;
                    case "654.32":
                        tv = (TextView) findViewById(R.id.textTTF);
                        break;
                    case "654.24":
                        tv = (TextView) findViewById(R.id.textSOC);
                        break;
                    case "658.32":
                        tv = (TextView) findViewById(R.id.textSOH);
                        break;
                    case "654.42":
                        tv = (TextView) findViewById(R.id.textKMA);
                        break;
                    case "7ec.623203.16":
                        tv = (TextView) findViewById(R.id.textVolt);
                        break;
                    case "7ec.623204.16":
                        tv = (TextView) findViewById(R.id.textAmps);
                        break;
                    case "7bb.6104.32":
                        tv = (TextView) findViewById(R.id.text_comp_1_temp);
                        break;
                    case "7bb.6104.56":
                        tv = (TextView) findViewById(R.id.text_comp_2_temp);
                        break;
                    case "7bb.6104.80":
                        tv = (TextView) findViewById(R.id.text_comp_3_temp);
                        break;
                    case "7bb.6104.104":
                        tv = (TextView) findViewById(R.id.text_comp_4_temp);
                        break;
                    case "7bb.6104.128":
                        tv = (TextView) findViewById(R.id.text_comp_5_temp);
                        break;
                    case "7bb.6104.152":
                        tv = (TextView) findViewById(R.id.text_comp_6_temp);
                        break;
                    case "7bb.6104.176":
                        tv = (TextView) findViewById(R.id.text_comp_7_temp);
                        break;
                    case "7bb.6104.200":
                        tv = (TextView) findViewById(R.id.text_comp_8_temp);
                        break;
                    case "7bb.6104.224":
                        tv = (TextView) findViewById(R.id.text_comp_9_temp);
                        break;
                    case "7bb.6104.248":
                        tv = (TextView) findViewById(R.id.text_comp_10_temp);
                        break;
                    case "7bb.6104.272":
                        tv = (TextView) findViewById(R.id.text_comp_11_temp);
                        break;
                    case "7bb.6104.296":
                        tv = (TextView) findViewById(R.id.text_comp_12_temp);
                        break;
                }
                // set a new content
                if (tv != null) {
                    tv.setText("" + field.getValue());
                    // special cases
                    switch (fieldId) {

                        case "7ec.623203.16":
                            dcVolt = field.getValue();
                            break;
                        case "7ec.623204.16":
                            tv = (TextView) findViewById(R.id.textDcPwr);
                            tv.setText("" + (dcVolt * field.getValue()));
                            break;
                    }
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