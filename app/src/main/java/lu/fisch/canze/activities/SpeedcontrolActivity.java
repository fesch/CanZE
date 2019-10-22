package lu.fisch.canze.activities;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class SpeedcontrolActivity extends CanzeActivity implements FieldListener, DebugListener {

    private static final String SID_Odometer = "7ec.6233de.24";
    private static final String SID_RealSpeed = "5d7.0";  //ESC-ABS

    private long timeStart = 0;
    private long timeLast = 0;
    private double distanceStart = 0;
    private double distanceEnd = 0;
    private double distanceLast = 0;
    private double distanceInterpolated = 0;
    private double speed = 0;
    private boolean go = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedcontrol);

        View.OnClickListener oc = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset
                timeStart = 0;
                distanceLast = 0;
                go=true;

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
        addField(SID_RealSpeed, 100);
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
                    case SID_RealSpeed:
                        speed = field.getValue();
                        break;
                    case SID_Odometer:
                        distanceEnd = field.getValue();
                        long timeEnd = System.currentTimeMillis();
                        // if starting time has been set
                        if (go) {
                            // speed measuring has been started normally
                            if (timeStart != 0) {
                                // only update if distance changed ...
                                if (distanceLast != distanceEnd) {
                                    distanceInterpolated = distanceEnd;
                                    // calculate avg speed
                                    double speed = ((distanceEnd - distanceStart) * 3600000.0) / (timeEnd - timeStart);
                                    // show it
                                    TextView tv = findViewById(R.id.speed);
                                    if (tv != null)
                                        tv.setText(String.format(Locale.getDefault(), "%.1f", speed));
                                }
                                else // interpolate distance using the speed
                                {
                                    // get difference in distance
                                    double distanceDelta = speed * (timeEnd - timeLast) / 3600000.0;
                                    // add it to the last interpolated distance
                                    distanceInterpolated += distanceDelta;

                                    // at least 100m should have been driven in order to
                                    // avoid "jumping" values
                                    if(distanceEnd - distanceStart>0.1) {
                                        // calculate avg speed
                                        double speed = ((distanceInterpolated - distanceStart) * 3600000.0) / (timeEnd - timeStart);
                                        // show it
                                        TextView tv = findViewById(R.id.speed);
                                        if (tv != null)
                                            tv.setText(String.format(Locale.getDefault(), "%.1f", speed));
                                    }
                                }

                                ((TextView) findViewById(R.id.textDetails)).setText("Distance: " +
                                        String.format(Locale.getDefault(), "%.1f", (distanceEnd - distanceStart)) +
                                        (MainActivity.milesMode ? "mi" : "km") +
                                        " - " +
                                        "Time: " + timeToStr(timeEnd - timeStart)
                                );

                                distanceLast = distanceEnd;
                                timeLast = timeEnd;
                            } else if(distanceLast == 0) {
                                // do nothing ...
                                ((TextView) findViewById(R.id.textDetails)).setText("Got first value ...");
                                distanceLast = distanceEnd;
                            } else if (distanceLast != distanceEnd) {
                                // set starting distance as long as starting time is not set
                                distanceStart = distanceEnd;
                                distanceLast = distanceEnd;
                                distanceInterpolated = distanceEnd;
                                // set start time
                                timeStart = timeEnd;
                                timeLast = timeEnd;
                                ((TextView) findViewById(R.id.textDetails)).setText("Got second value ...");
                            } else {
                                ((TextView) findViewById(R.id.textDetails)).setText("Waiting for second value ...");
                            }
                        }
                        break;
                }
            }
        });
    }

    public String timeToStr(long time)
    {
        String r = "";

        time=time/1000;

        long h = time/3600;
        long m = (time%3600)/60;
        long s = (time % 60);

        if(h<10)r+="0";
        r+=h;
        r+=":";
        if(m<10)r+="0";
        r+=m;
        r+=":";
        if(s<10)r+="0";
        r+=s;

        return r;
    }
}
