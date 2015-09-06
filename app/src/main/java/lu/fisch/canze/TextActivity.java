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
public class TextActivity extends AppCompatActivity implements FieldListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Field field;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        // Add this class ass listener to the field "Pedal"
        // I chose this one, because it can easily be tested ;-)
        field = MainActivity.fields.getBySID("186.40");
        field.addListener(this);
        MainActivity.reader.addField(field);

        field = MainActivity.fields.getBySID("42e.38");
        field.addListener(this);
        MainActivity.reader.addField(field);

        field = MainActivity.fields.getBySID("42e.56");
        field.addListener(this);
        MainActivity.reader.addField(field);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // free up the listener again
        MainActivity.fields.getBySID("186.40").removeListener(this);
        // clear filters
        MainActivity.reader.clearFields();
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
                    case "186.40":
                        tv = (TextView) findViewById(R.id.textPedal);
                        break;
                    case "42e.38":
                        tv = (TextView) findViewById(R.id.text_max_pilot);
                        break;
                    case "42e.56":
                        tv = (TextView) findViewById(R.id.text_max_charge);
                        break;
                }
                // set a new content
                if (tv != null) tv.setText(field.getPrintValue());
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