package lu.fisch.canze.activities;

import androidx.appcompat.app.AppCompatActivity;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

public class SpeedcontrolActivity extends CanzeActivity implements FieldListener, DebugListener {

    private static final String SID_Odometer = "7ec.6233de.24";

    private long timeStart = 0;
    private double distanceStart = 0;
    private double distanceEnd = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedcontrol);

        TextView tv = findViewById(R.id.speed);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distanceEnd != 0) { // avoid setting invalid start value (0)
                    // reset starting time
                    timeStart = System.currentTimeMillis();
                    // take last distance as starting distance
                    distanceStart = distanceEnd;

                    TextView tv = findViewById(R.id.speed);
                    if (tv != null) tv.setText("...");
                }
            }
        });
    }

    @Override
    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_Odometer, 100);

    }

    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();

                switch (fieldId) {
                    // positive torque
                    case SID_Odometer:
                        distanceEnd = field.getValue();
                        // if starting time has been set
                        if (timeStart != 0) {
                            // some distance has been traveled
                            if (distanceStart != distanceEnd) {
                                // calculate speed
                                long timeEnd = System.currentTimeMillis();
                                double speed = ((distanceEnd - distanceStart) * 3600000.0) / (timeEnd - timeStart);
                                // show it
                                TextView tv = findViewById(R.id.speed);
                                if (tv != null)
                                    tv.setText(String.format(Locale.getDefault(), "%.1f", speed));
                            }
                        } else {
                            // set starting distance as long as starting time is not set
                            // maybe change to also set start time here after a tap for precision
                            distanceStart = field.getValue();
                        }
                        break;
                }
            }
        });
    }

}
