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
                // reset starting time
                timeStart = System.currentTimeMillis();
                // take last distance as starting distance
                distanceStart = distanceEnd;

                TextView tv = findViewById(R.id.speed);
                if (tv != null) tv.setText("...");
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
                        // if starting time has been set
                        if (timeStart != 0) {
                            // calculate speed
                            long timeEnd = System.currentTimeMillis();
                            distanceEnd = field.getValue();
                            double speed = (distanceEnd - distanceStart) / ((timeEnd - timeStart) / (1000 * 60 * 60));
                            // show it
                            TextView tv = findViewById(R.id.speed);
                            if (tv != null) tv.setText(speed + "");
                        } else {
                            // update starting distance
                            distanceStart = field.getValue();
                        }

                        break;
                }
            }
        });
    }

}
