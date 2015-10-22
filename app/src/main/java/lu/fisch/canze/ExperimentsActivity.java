package lu.fisch.canze;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import lu.fisch.canze.bluetooth.BluetoothManager;

public class ExperimentsActivity extends CanzeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiments);

        Button button;

        button = (Button) findViewById(R.id.buttonPgJm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.getInstance(), ElmTestActivity.class);
                MainActivity.getInstance().startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) findViewById(R.id.buttonDtc);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.getInstance(), DtcActivity.class);
                MainActivity.getInstance().startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

}
