package lu.fisch.canze;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lu.fisch.canze.widgets.WidgetView;

public class BatteryTempActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_temp);

        // initialise the widgets
        initWidgets();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // free up the listener again
        ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
        for(int i=0; i<widgets.size(); i++) {
            final WidgetView wv = widgets.get(i);
            MainActivity.fields.getBySID(wv.getFieldSID()).removeListener(wv.getDrawable());
        }
        // clear filters
        // OLD: MainActivity.reader.clearFields();
        MainActivity.device.clearFields();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        // initialise the widgets
        initWidgets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_battery_temp, menu);
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

    // ----------------------------------------------------

    private void initWidgets()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // connect the widgets to the respective fields
                // and add the filters to the reader
                ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
                for (int i = 0; i < widgets.size(); i++) {
                    final WidgetView wv = widgets.get(i);
                    // connect widgets to fields
                    MainActivity.fields.getBySID(wv.getFieldSID()).addListener(wv.getDrawable());
                    // add filter to reader
                    // OLD: MainActivity.reader.addField(wv.getDrawable().getField());
                    MainActivity.device.addField(wv.getDrawable().getField());

                    // touching a widget makes a "bigger" version appear
                    wv.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // get pointer index from the event object
                            int pointerIndex = event.getActionIndex();

                            // get pointer ID
                            int pointerId = event.getPointerId(pointerIndex);

                            // get masked (not specific to a pointer) action
                            int maskedAction = event.getActionMasked();

                            switch (maskedAction) {
                                case MotionEvent.ACTION_DOWN:
                                case MotionEvent.ACTION_POINTER_DOWN: {
                                    Intent intent = new Intent(BatteryTempActivity.this, WidgetActivity.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    WidgetView.selectedDrawable = wv.getDrawable();
                                    BatteryTempActivity.this.startActivity(intent);
                                    break;
                                }
                                case MotionEvent.ACTION_MOVE:
                                case MotionEvent.ACTION_UP:
                                case MotionEvent.ACTION_POINTER_UP:
                                case MotionEvent.ACTION_CANCEL: {
                                    break;
                                }
                            }

                            wv.invalidate();

                            return true;
                        }
                    });
                }
            }
        });
    }

    private ArrayList<WidgetView> getWidgetViewArrayList(ViewGroup viewGroup)
    {
        ArrayList<WidgetView> result = new ArrayList<WidgetView>();

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                result.addAll(getWidgetViewArrayList((ViewGroup) v));
            }
            else if (v instanceof WidgetView)
            {
                result.add((WidgetView)v);
            }
        }

        return result;
    }
}
