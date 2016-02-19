package lu.fisch.canze.activities;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lu.fisch.canze.R;
import lu.fisch.canze.classes.GPSTracker;

/**
 * Created by chris on 16/02/16.
 */
public class GPSTestActivity extends Activity {
    private EditText editTextShowLocation;
    private Button buttonGetLocation;
    // Button btnShowLocation;

    // GPSTracker class
    private GPSTracker gps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpstest);

        buttonGetLocation = (Button) findViewById(R.id.buttonGetLocation);

        // show location button click event
        buttonGetLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(GPSTestActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line

                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        // locationManager.removeUpdates(this);
        gps.stopUsingGPS();
    }

    protected void onDestroy(){
        super.onDestroy();
        // locationManager.removeUpdates(this);
        gps.stopUsingGPS();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this );
    }
}
