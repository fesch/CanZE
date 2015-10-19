package lu.fisch.canze;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.widgets.WidgetView;

/**
 * Created by robertfisch on 30.09.2015.
 */
public class CanzeActivity extends AppCompatActivity {

    private boolean iLeftMyOwn = false;
    private boolean back = false;

    protected boolean widgetView = false;
    protected boolean widgetClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.device!=null)
            if(MainActivity.device.getConnectedBluetoothThread()==null)
                // restart Bluetooth
                MainActivity.getInstance().reloadBluetooth();
        MainActivity.debug("CanzeActivity: onCreate");
        if(!widgetView) {
            // register all fields
            MainActivity.registerFields();
            // initialise the widgets (if any present)
            initWidgets();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.debug("CanzeActivity: onPause");
        // if we are not coming back from somewhere, stop Bluetooth
        if(!back && !widgetClicked) {
            MainActivity.debug("CanzeActivity: onPause > stopBluetooth");
            MainActivity.getInstance().stopBluetooth();
        }
        if(!widgetClicked) {
            // remember we paused ourselves
            iLeftMyOwn=true;
            // save all fields
            MainActivity.debug("CanzeActivity: onPause > saveFields");
            MainActivity.getInstance().saveFields();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.debug("CanzeActivity: onResume");
        if(!widgetClicked) {
            MainActivity.debug("CanzeActivity: onResume > initWidgets");
            // initialise the widgets (if any present)
            initWidgets();
        }
        // if we paused ourselvers
        if (iLeftMyOwn && !widgetClicked) {
            MainActivity.debug("CanzeActivity: onResume > reloadBluetooth");
            // restart Bluetooth
            MainActivity.getInstance().reloadBluetooth();
            iLeftMyOwn=false;
        }
        widgetClicked=false;
    }

    @Override
    protected void onDestroy() {
        MainActivity.debug("CanzeActivity: onDestroy");
        if(!widgetView) {
            // free the widget listerners
            freeWidgetListeners();
            if (isFinishing()) {
                MainActivity.debug("CanzeActivity: onDestroy (finishing)");
                // clear filters
                MainActivity.device.clearFields();
                MainActivity.registerFields();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.isSafe()) {
            super.onBackPressed();
            back = true;
        }
    }

    /********************************************************/

    protected void initWidgets()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // sleep a bit to give the UI time to initialise
                try { Thread.sleep(1000); }
                catch (Exception e) {}

                // connect the widgets to the respective fields
                // and add the filters to the reader
                ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(android.R.id.content));
                for (int i = 0; i < widgets.size(); i++) {
                    final WidgetView wv = widgets.get(i);
                    // connect widgets to fields
                    if (wv == null) {
                        throw new ExceptionInInitializerError("Widget <" + wv.getId() + "> is NULL!");
                    }
                    String sid = wv.getFieldSID();
                    String[] sids = sid.split(",");
                    for(int s=0; s<sids.length; s++) {
                        Field field = MainActivity.fields.getBySID(sids[s]);
                        if (field == null) {
                            MainActivity.debug("!!! >> Field with following SID <" + sids[s] + "> not found!");
//                            Toast.makeText(CanzeActivity.this, "Field with following SID <" + sids[s] + "> not found!", Toast.LENGTH_SHORT).show();
                            //throw new ExceptionInInitializerError("Field with following SID <" + wv.getFieldSID() + "> not found!");
                        }
                        else {
                            // add field to list of registered sids for this widget
                            wv.getDrawable().addField(field.getSID());
                            // add listener
                            field.addListener(wv.getDrawable());
                            // add filter to reader
                            MainActivity.device.addField(field);
                        }
                    }

                    // touching a widget makes a "bigger" version appear
                    wv.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(((WidgetView) v).isClickable()) {
                                // get pointer index from the event object
                                int pointerIndex = event.getActionIndex();

                                // get pointer ID
                                int pointerId = event.getPointerId(pointerIndex);

                                // get masked (not specific to a pointer) action
                                int maskedAction = event.getActionMasked();

                                switch (maskedAction) {
                                    case MotionEvent.ACTION_DOWN:
                                    case MotionEvent.ACTION_POINTER_DOWN: {
                                        widgetClicked=true;
                                        Intent intent = new Intent(CanzeActivity.this, WidgetActivity.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        WidgetView.selectedDrawable = wv.getDrawable();
                                        CanzeActivity.this.startActivity(intent);
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
                            else return false;
                        }
                    });
                }
            }
        }).start();
    }

    protected void freeWidgetListeners()
    {
        // free up the listener again
        ArrayList<WidgetView> widgets = getWidgetViewArrayList((ViewGroup) findViewById(R.id.table));
        for(int i=0; i<widgets.size(); i++) {
            final WidgetView wv = widgets.get(i);

            String sid = wv.getFieldSID();
            String[] sids = sid.split(",");
            for(int s=0; s<sids.length; s++) {
                Field field = MainActivity.fields.getBySID(sids[s]);
                if (field != null) {
                    field.removeListener(wv.getDrawable());
                }
            }

        }
    }


    protected ArrayList<WidgetView> getWidgetViewArrayList(ViewGroup viewGroup)
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
