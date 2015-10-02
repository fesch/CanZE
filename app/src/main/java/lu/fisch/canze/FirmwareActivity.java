package lu.fisch.canze;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;


// Jeroen

public class FirmwareActivity extends CanzeActivity implements FieldListener {

    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware);

        subscribedFields = new ArrayList<>();

        addListener("7ec.6180.128"); // SCH
        addListener("7da.6180.128"); // TCU
        addListener("7bb.6180.128"); // LBC
        addListener("77e.6180.128"); // PEB
        addListener("772.6180.128"); // AIRBAG
        addListener("76d.6180.128"); // USM not for Fluence
        addListener("763.6180.128"); // CLUSTER
        addListener("762.6180.128"); // EPS
        addListener("760.6180.128"); // ABS
        addListener("7bc.6180.128"); // UBP not for Fluence
        addListener("765.6180.128"); // BCM
        addListener("764.6180.128"); // CLIM
        addListener("76e.6180.128"); // UPA not for Zoe
        addListener("793.6180.128"); // BCB not for Fluence or Zoe
        addListener("7b6.6180.128"); // LBC2
        addListener("722.6180.128"); // LINSCH not for FLuence or Zoe
    }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            MainActivity.device.addField(field);
            subscribedFields.add(field);
        } else {
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

                // get the text field
                switch (fieldId) {

                    case "7ec.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "7da.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7da);
                        break;
                    case "7bb.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7bb);
                        break;
                    case "77e.6180.128":
                        tv = (TextView) findViewById(R.id.ecu77e);
                        break;
                    case "772.6180.128":
                        tv = (TextView) findViewById(R.id.ecu772);
                        break;
                    case "76d.6180.128":
                        tv = (TextView) findViewById(R.id.ecu76d);
                        break;
                    case "763.6180.128":
                        tv = (TextView) findViewById(R.id.ecu763);
                        break;
                    case "762.6180.128":
                        tv = (TextView) findViewById(R.id.ecu762);
                        break;
                    case "760.6180.128":
                        tv = (TextView) findViewById(R.id.ecu760);
                        break;
                    case "7bc.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7bc);
                        break;
                    case "765.6180.128":
                        tv = (TextView) findViewById(R.id.ecu765);
                        break;
                    case "764.6180.128":
                        tv = (TextView) findViewById(R.id.ecu764);
                        break;
                    case "76e.6180.128":
                        tv = (TextView) findViewById(R.id.ecu76e);
                        break;
                    case "793.6180.128":
                        tv = (TextView) findViewById(R.id.ecu793);
                        break;
                    case "7b6.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7b6);
                        break;
                    case "722.6180.128":
                        tv = (TextView) findViewById(R.id.ecu722);
                        break;
                }

                // set regular new content, all exeptions handled above
                if (tv != null) {
                    String version = Integer.toHexString((int) field.getValue());
                    version = ("0000" + version).substring(version.length());
                    tv.setText(version);
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
