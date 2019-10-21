package lu.fisch.canze.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

import android.content.Intent;
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
    private double distanceLast = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedcontrol);

        View.OnClickListener oc = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset
                timeStart = 0;

                TextView tv = findViewById(R.id.speed);
                if (tv != null) tv.setText("...");

                if (MainActivity.milesMode)
                    ((TextView) findViewById(R.id.unit)).setText("mi/h");
                else
                    ((TextView) findViewById(R.id.unit)).setText("km/h");
            }

            ;
        };

        // allow to click on any
        findViewById(R.id.speed).setOnClickListener(oc);
        findViewById(R.id.title).setOnClickListener(oc);
        findViewById(R.id.unit).setOnClickListener(oc);
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
                            if (distanceStart!=distanceEnd) {
                                long timeEnd = System.currentTimeMillis();

                                // only update if distance changed ...
                                if(distanceLast!=distanceEnd) {
                                    // calculate speed
                                    double speed = ((distanceEnd - distanceStart) * 3600000.0) / (timeEnd - timeStart);
                                    // show it
                                    TextView tv = findViewById(R.id.speed);
                                    if (tv != null)
                                        tv.setText(String.format(Locale.getDefault(), "%.1f", speed));
                                }

                                ((TextView) findViewById(R.id.textDebug)).setText("Distance: "+
                                        (distanceEnd - distanceStart)+
                                        (MainActivity.milesMode?"mi/h":"km/h")+
                                        " - "+
                                        "Time: "+timeToStr(timeEnd - timeStart)
                                        );

                            }
                        } else {
                            // set starting distance as long as starting time is not set
                            distanceStart = distanceEnd;
                            // set start time
                            timeStart = System.currentTimeMillis();
                        }
                        distanceLast = distanceEnd;
                        break;
                }
            }
        });
    }

    public String timeToStr(long time)
    {
        String r = "";

        long h = time/3600;
        long m = (time%3600)/60;
        long s = (time % 60);

        if(h<10)r+="0";
        r+=h;
        if(m<10)r+="0";
        r+=m;
        if(s<10)r+="0";
        r+=s;

        return r;
    }
}
