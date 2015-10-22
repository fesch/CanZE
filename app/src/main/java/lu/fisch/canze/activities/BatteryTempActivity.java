package lu.fisch.canze.activities;

import android.os.Bundle;
import android.view.Menu;

import lu.fisch.canze.R;

public class BatteryTempActivity extends CanzeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_temp);

        setTitle("Cell Compartiment Temperatures");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

}
