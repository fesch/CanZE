package lu.fisch.canze;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by robertfisch on 30.09.2015.
 */
public class CanzeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity.registerFields();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // clear filters
        MainActivity.device.clearFields();
        MainActivity.registerFields();
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.isSafe())
            super.onBackPressed();
    }

}
