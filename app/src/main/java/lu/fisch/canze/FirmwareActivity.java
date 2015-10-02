package lu.fisch.canze;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

/**
 * Created by jeroen on 2-10-15.
 */
public class FirmwareActivity extends CanzeActivity implements FieldListener {

    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware);

        subscribedFields = new ArrayList<>();

        addListener("7ec.6180.128");
        addListener("7da.6180.128");
        addListener("7bb.6180.128");
        addListener("77e.6180.128");
        addListener("772.6180.128");
        addListener("76d.6180.128");
        addListener("763.6180.128");
        addListener("762.6180.128");
        addListener("760.6180.128");
        addListener("7bc.6180.128");
        addListener("765.6180.128");
        addListener("764.6180.128");
        addListener("76e.6180.128");
        addListener("793.6180.128");
        addListener("7b6.6180.128");
        addListener("722.6180.128");
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

                    case "7ec..6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "7ec.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "7da.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "7bb.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "77e.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "772.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "76d.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "763.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "762.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "760.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "7bc.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "765.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "764.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "76e.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "793.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "7b6.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                    case "722.6180.128":
                        tv = (TextView) findViewById(R.id.ecu7ec);
                        break;
                }

                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText("" + Integer.toHexString((int )field.getValue()));
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
