package lu.fisch.canze;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import lu.fisch.canze.bluetooth.BluetoothManager;

/**
 * Created by robertfisch on 30.09.2015.
 */
public class CanzeActivity extends AppCompatActivity {

    private boolean iLeftMyOwn = false;
    private boolean back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(MainActivity.device.getConnectedBluetoothThread()==null)
            MainActivity.getInstance().reloadBluetooth();

        MainActivity.debug("CanzeActivity: onCreate");
        MainActivity.registerFields();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.debug("CanzeActivity: onPause");
        if(!back)
            MainActivity.getInstance().stopBluetooth();
        iLeftMyOwn=true;
        MainActivity.getInstance().saveFields();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (iLeftMyOwn) {
            MainActivity.debug("CanzeActivity: onResume");
            MainActivity.getInstance().reloadBluetooth();
            iLeftMyOwn=false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.debug("CanzeActivity: onDestroy");
        if(isFinishing()) {
            MainActivity.debug("CanzeActivity: onDestroy (finishing)");
            // clear filters
            MainActivity.device.clearFields();
            MainActivity.registerFields();
        }
    }

    @Override
    public void onBackPressed() {
        if(MainActivity.isSafe()) {
            super.onBackPressed();
            back = true;
        }
    }

}
