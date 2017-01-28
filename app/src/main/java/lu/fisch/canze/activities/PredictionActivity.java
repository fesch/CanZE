package lu.fisch.canze.activities;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Battery;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

public class PredictionActivity extends CanzeActivity implements FieldListener, DebugListener {

    public static final String SID_AvChargingPower = "427.40";
    public static final String SID_UserSoC = "42e.0";          // user SOC, not raw
    public static final String SID_AverageBatteryTemperature = "7bb.6104.600"; // (LBC)
    public static final String SID_RangeEstimate = "654.42";
    public static final String SID_ChargingStatusDisplay = "65b.41";


    private Battery battery;

    private double car_soc = 5;
    private double car_bat_temp = 10;
    private double car_charger_ac_power = 22;
    private int car_status = 0;
    private int charging_status = 0;
    private int seconds_per_tick = 288; // time 100 iterations = 8 hours
    private double car_range_est = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        // initialize the battery model
        battery = new Battery();
        // more adjustments are needed her for other car types than ZOE Q210
        if (MainActivity.car == MainActivity.CAR_ZOE_R240 || MainActivity.car == MainActivity.CAR_ZOE_R90) {
            battery.setDcPowerLowerLimit(1.0);
            battery.setDcPowerUpperLimit(20.0);
        }
        if (MainActivity.car == MainActivity.CAR_ZOE_Q90 || MainActivity.car == MainActivity.CAR_ZOE_R90) {
            battery.setRawCapacity(41.0);
        }
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_RangeEstimate, 10000);
        addField(SID_AvChargingPower, 10000);
        addField(SID_UserSoC, 10000);
        addField(SID_ChargingStatusDisplay, 10000);
        addField(SID_AverageBatteryTemperature, 10000);
    }

    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
    @Override
    public void onFieldUpdateEvent(final Field field) {
        String fieldId = field.getSID();
        Double fieldVal = field.getValue();

        if (fieldVal.isNaN()) return;
        // get the text field
        switch (fieldId) {

            case SID_AvChargingPower:
                car_charger_ac_power = fieldVal;
                car_status |= 0x01;
                break;
            case SID_UserSoC:
                car_soc = fieldVal;
                car_status |= 0x02;
                break;
            case SID_AverageBatteryTemperature:
                car_bat_temp  = fieldVal;
                car_status |= 0x04;
                break;
            case SID_RangeEstimate:
                car_range_est = fieldVal;
                car_status |= 0x08;
                break;
            case SID_ChargingStatusDisplay:
                charging_status = (fieldVal == 3) ? 1 : 0;
                    car_status |= 0x10;
        }
        // display the debug values
        if (car_status == 0x1f) {
            runPrediction();
            car_status = 0;
        }
    }

    private void runPrediction() {

        // set the battery object to an initial state equal to the real battery (
        battery.setTimeRunning(0);

        // set the internal battery temperature
        updatePrediction("texttemp", "" + (int) car_bat_temp + "°C");
        battery.setTemperature(car_bat_temp);

        // set the internal state of charge
        updatePrediction("textsoc", (int) car_soc + "%");
        battery.setStateOfChargePerc(car_soc);

        if (charging_status == 0) {
            updatePrediction("textacpwr", "Not charging");
            for (int t = 10; t <= 100; t = t + 10) {
                updatePrediction("textTIM" + t, "00:00");
                updatePrediction("textSOC" + t, "-");
                updatePrediction("textRAN" + t, "-");
                updatePrediction("textPWR" + t, "-");
            }
            return;
        }

        // set the external maximum charger capacity
        updatePrediction("textacpwr", ((int) (car_charger_ac_power * 10)) / 10 + " kW");
        battery.setChargerPower(car_charger_ac_power);

        // now start iterating over time
        int iter_at_99 = 100; // tick when the battery is full
        for (int t = 1; t <= 100; t++) { // 100 ticks
            battery.iterateCharging(seconds_per_tick);
            double soc = battery.getStateOfChargePerc();
            // save the earliest tick when the battery is full
            if (soc >= 99 && t < iter_at_99) iter_at_99 = t;
            // optimization
            if ((t % 10) == 0) {
                updatePrediction("textTIM" + t, "" + formatTime(battery.getTimeRunning()));
                updatePrediction("textSOC" + t, "" + ((int) soc));
                if (car_soc > 0.0) updatePrediction("textRAN" + t, "" + ((int) (car_range_est * soc / car_soc)));
                updatePrediction("textPWR" + t, "" + ((int) battery.getDcPower()));
            }
        }

        // adjust the tick time if neccesary. Note that this is
        // effective on th next iteration

        if (iter_at_99 == 100 && seconds_per_tick < 288) {
            // if we were unable to go to 99% and below 8 hours, double tick step
            seconds_per_tick *= 2;
        } else if (iter_at_99 > 50) {
            // if we were full after half the table size
            // do nothing
            seconds_per_tick *= 1;
        } else if (iter_at_99 > 25 && seconds_per_tick > 18) {
            // if we were full after a quarter of the table size
            // and over half an hour, half the tick step
            seconds_per_tick /= 2;
        } else if (seconds_per_tick > 18) {
            // if we were full before or equal a quarter of the table size
            // and over half an hour, quarter the tick step
            seconds_per_tick /= 4;
        }
    }

    public void updatePrediction(final String id, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv;
                tv = (TextView) findViewById(getResources().getIdentifier(id, "id", getPackageName()));
                if (tv != null) {
                    tv.setText(msg);
                }
            }
        });
    }

    private String formatTime(int t) {
        // t is in seconds
        t /= 60;
        // t is in minutes
        return "" + format2Digit(t / 60) + ":" + format2Digit(t % 60);
    }

    private String format2Digit(int t) {
        return ("00" + t).substring(t > 9 ? 2 : 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

}
