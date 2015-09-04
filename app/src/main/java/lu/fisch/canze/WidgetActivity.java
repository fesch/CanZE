package lu.fisch.canze;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.widgets.WidgetView;

public class WidgetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

        WidgetView wv = (WidgetView) findViewById(R.id.selectedWidget);
        wv.setClickable(false);
        if(WidgetView.selectedDrawable!=null) {
            wv.setDrawable(WidgetView.selectedDrawable);
            setTitle(WidgetView.selectedDrawable.getTitle());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_widget, menu);
        return true;
    }

}
