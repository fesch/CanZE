package lu.fisch.canze.activities;

import lu.fisch.canze.BuildConfig;
import lu.fisch.canze.R;
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SpeedcontrolActivity extends CanzeActivity implements FieldListener, DebugListener {

    private long timeStart = 0;
    private long timeLast = 0;
    private double distanceStart = 0;
    private double distanceEnd = 0;
    private double distanceLast = 0;
    private double distanceInterpolated = 0;
    private double speed = 0;
    private boolean go = false;
    private final String km = MainActivity.getStringSingle(R.string.unit_Km);
    private final String mi = MainActivity.getStringSingle(R.string.unit_Mi);
    private final String kmh = MainActivity.getStringSingle(R.string.unit_SpeedKm);
    private final String mih = MainActivity.getStringSingle(R.string.unit_SpeedMi);
    private final String speedformat = "%.0f"; // feel free to change back. Experiment to make less jumpy
    private final SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHM), Locale.getDefault());

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
                CharSequence speedNow = tv.getText();
                tv.setText("...");
                if (!speedNow.equals("-") && !speedNow.equals("...")) {
                    tv = findViewById(R.id.textLog);
                    speedNow = sdf.format(Calendar.getInstance().getTime()) + ": " + speedNow + (MainActivity.milesMode ? mih : kmh) + "\n" + tv.getText();
                    tv.setText(speedNow);
                }

                tv = findViewById(R.id.unit);
                tv.setText(MainActivity.milesMode ? mih : kmh);
            }

        };

        // allow to click on any
        //findViewById(R.id.speed).setOnClickListener(oc);
        //findViewById(R.id.title).setOnClickListener(oc);
        //findViewById(R.id.unit).setOnClickListener(oc);
        findViewById(R.id.screen).setOnClickListener(oc);
    }

    @Override
    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(Sid.TripMeterB, 100);
        addField(Sid.RealSpeed, 100);
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
                    case Sid.RealSpeed:
                        speed = field.getValue();
                        break;
                    case Sid.TripMeterB:
                        distanceEnd = field.getValue();
                        long timeEnd = System.currentTimeMillis();
                        // if starting time has been set
                        if (go) {
                            TextView tv;
                            // speed measuring has been started normally
                            if (timeStart != 0) {
                                double speedCalc = 0;

                                // only update if distance changed ...
                                if (distanceLast != distanceEnd) {
                                    distanceInterpolated = distanceEnd;
                                    // calculate avg speed
                                    double speed = ((distanceEnd - distanceStart) * 3600000.0) / (timeEnd - timeStart);
                                    // show it
                                    tv = findViewById(R.id.speed);
                                    if (tv != null)
                                        tv.setText(String.format(Locale.getDefault(), speedformat, speed));
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
                                        speedCalc = ((distanceInterpolated - distanceStart) * 3600000.0) / (timeEnd - timeStart);
                                        // show it
                                        tv = findViewById(R.id.speed);
                                        if (tv != null)
                                            tv.setText(String.format(Locale.getDefault(), speedformat, speedCalc));
                                    }
                                }

                                // clear the clutter
                                if (!BuildConfig.BRANCH.equals("master")) {
                                    tv = findViewById(R.id.textDetails);
                                    tv.setText(
                                            String.format(Locale.getDefault(),
                                                    "Distance:%.2f%s - Time:%s > %s = %s - Speed:%.2f%s - SpeedCalc:%.2f%s - SDK:%s",
                                                    distanceEnd - distanceStart, MainActivity.milesMode ? mi : km,
                                                    timeToStr(timeStart), timeToStr(timeEnd), timeToStr(timeEnd - timeStart),
                                                    speed, MainActivity.milesMode ? mih : kmh,
                                                    speedCalc, MainActivity.milesMode ? mih : kmh,
                                                    Build.VERSION.SDK_INT
                                            )
                                    );
                                } else {
                                    // added to remove "waiting for second value" when on master branch
                                    tv = findViewById(R.id.textDetails);
                                    tv.setText("");
                                }

                                distanceLast = distanceEnd;
                                timeLast = timeEnd;
                            } else if(distanceLast == 0) {
                                // do nothing ...
                                tv = findViewById(R.id.textDetails);
                                tv.setText(R.string.message_gotfirst);
                                distanceLast = distanceEnd;
                            } else if (distanceLast != distanceEnd) {
                                // set starting distance as long as starting time is not set
                                distanceStart = distanceEnd;
                                distanceLast = distanceEnd;
                                distanceInterpolated = distanceEnd;
                                // set start time
                                timeStart = timeEnd;
                                timeLast = timeEnd;
                                tv = findViewById(R.id.textDetails);
                                tv.setText(R.string.message_gotsecond);
                            } else {
                                tv = findViewById(R.id.textDetails);
                                tv.setText(R.string.message_waitsecond);
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
