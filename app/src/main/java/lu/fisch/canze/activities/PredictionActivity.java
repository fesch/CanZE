package lu.fisch.canze.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Battery;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

public class PredictionActivity extends AppCompatActivity implements FieldListener {

    public static final String SID_AvChargingPower                  = "427.40";
    public static final String SID_UserSoC                          = "42e.0";          // user SOC, not raw
    public static final String SID_Preamble_CompartmentTemperatures = "7bb.6104."; // (LBC)
    public static final String SID_RangeEstimate                    = "654.42";

    private Battery battery;

    private double car_soc                  = 5;
    private double car_bat_temp             = 10;
    private double car_bat_temp_ar []       = {0,15,15,15,15,15,15,15,15,15,15,15,15};
    private double car_charger_ac_power     = 22;
    private int car_status                  = 0;
    private int seconds_per_tick            = 1;
    private double car_range_est            = 1;

    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        // initialize the battery, now with fixed values
        // need to change this to get parameters from the car of course

        battery = new Battery();

        // start collecting data from the car
        initListeners();

    }

    private void initListeners() {

        subscribedFields = new ArrayList<>();

        addListener(SID_RangeEstimate, 10000);
        addListener(SID_AvChargingPower, 10000);
        addListener(SID_UserSoC, 10000);
        // Battery compartment temperatures
        int lastCell = (MainActivity.car==MainActivity.CAR_ZOE) ? 296 : 104;
        for (int i = 32; i <= lastCell; i += 24) {
            String sid = SID_Preamble_CompartmentTemperatures + i;
            addListener(sid, 10000);
        }
    }


    private void addListener(String sid, int intervalMs) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            MainActivity.device.addActivityField(field, intervalMs);
            subscribedFields.add(field);
        } else {
            MainActivity.toast("sid " + sid + " does not exist in class Fields");
        }

    }


    public void onFieldUpdateEvent(final Field field) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                TextView tv;
                // get the text field
               switch (fieldId) {

                    case SID_AvChargingPower:
                        car_charger_ac_power = field.getValue();
                        tv = (TextView) findViewById(R.id.textacpwr);
                        tv.setText("" + ((int)(car_charger_ac_power * 10)) / 10 + " kW");
                        car_status |= 0x01;
                        break;
                    case SID_UserSoC:
                        car_soc = field.getValue();
                        tv = (TextView) findViewById(R.id.textsoc);
                        tv.setText("" + (int)car_soc + "%");
                        car_status |= 0x02;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "32":
                        car_bat_temp_ar[1] = field.getValue();
                        break;
                    case SID_Preamble_CompartmentTemperatures + "56":
                        car_bat_temp_ar[2] = field.getValue();
                        break;
                    case SID_Preamble_CompartmentTemperatures + "80":
                        car_bat_temp_ar[3] = field.getValue();
                        break;
                    case SID_Preamble_CompartmentTemperatures + "104":
                        car_bat_temp_ar[4] = field.getValue();
                        // set temp to valid when the last module temperature is in
                        if (MainActivity.car != MainActivity.CAR_ZOE) {
                            car_bat_temp = 0;
                            for (int temp_index = 4; temp_index > 0; temp_index--) {
                                car_bat_temp += car_bat_temp_ar [temp_index];
                            }
                            car_bat_temp /= 4;
                            tv = (TextView) findViewById(R.id.texttemp);
                            tv.setText("" + (int)car_bat_temp + "°C");
                            car_status |= 0x04;
                        }
                        break;
                    case SID_Preamble_CompartmentTemperatures + "128":
                        car_bat_temp_ar[5] = field.getValue();
                        break;
                    case SID_Preamble_CompartmentTemperatures + "152":
                        car_bat_temp_ar[6] = field.getValue();
                        break;
                    case SID_Preamble_CompartmentTemperatures + "176":
                        car_bat_temp_ar[7] = field.getValue();
                        break;
                    case SID_Preamble_CompartmentTemperatures + "200":
                        car_bat_temp_ar[8] = field.getValue();
                        break;
                    case SID_Preamble_CompartmentTemperatures + "224":
                        car_bat_temp_ar[9] = field.getValue();
                        break;
                    case SID_Preamble_CompartmentTemperatures + "248":
                        car_bat_temp_ar[10] = field.getValue();
                        break;
                    case SID_Preamble_CompartmentTemperatures + "272":
                        car_bat_temp_ar[11] = field.getValue();
                        break;
                    case SID_Preamble_CompartmentTemperatures + "296":
                        car_bat_temp_ar[12] = field.getValue();
                        car_bat_temp = 0;
                        for (int temp_index = 12; temp_index > 0; temp_index--) {
                            car_bat_temp += car_bat_temp_ar [temp_index];
                        }
                        car_bat_temp /= 12;
                        tv = (TextView) findViewById(R.id.texttemp);
                        tv.setText("" + (int)car_bat_temp + "°C");
                        car_status |= 0x04;
                        break;
                    case SID_RangeEstimate:
                        car_range_est = field.getValue();
                        car_status |= 0x08;
                        break;
                }
                // display the debug values
                tv = (TextView) findViewById(R.id.textDebug);
                tv.setText(fieldId + ", status:" + car_status);

            }
        });

        if (car_status == 0x0f) {
            // force debug
            // car_bat_temp = 0;
            // car_soc = 10;
            // car_charger_ac_power = 3;
            // car_range_est = 14;

            // set the battery object to an initial state equal to the real battery (
            battery.setTemperature (car_bat_temp);
            battery.setStateOfChargePerc(car_soc);
            battery.setTimeRunning(0);

            // set the external maximum charger capacity
            battery.setChargerPower(car_charger_ac_power);

            if (car_charger_ac_power > 20) {
                seconds_per_tick = 60; // 100 minutes = 1:40
            } else if (car_charger_ac_power > 6) {
                seconds_per_tick = 120; // 200 minutes = 3:20
            } else {
                seconds_per_tick = 300; // 500 minutes = 8:20
            }

            //long actual = Calendar.getInstance().getTimeInMillis() - 100000 * seconds_per_tick;
            long actual = Calendar.getInstance().getTimeInMillis();

            // now start iterating over time
            for (int t = 1; t <=100; t++) { // 100 ticks
                battery.iterateCharging(seconds_per_tick);

                // optimization
                if ((t % 10) == 0) {
                    double soc = battery.getStateOfChargePerc();
                    updatePrediction("textTIM" + t, "" + formatTime (battery.getTimeRunning()));
                    updatePrediction("textSOC" + t, "" + ((int)soc));
                    updatePrediction("textRAN" + t, "" + ((int)(car_range_est * soc / car_soc)));
                }

                actual += seconds_per_tick * 1000;
            }
            car_status = 0;
        }
    }

    public void updatePrediction (final String id, final String msg) {
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


    private String formatTime (int t) {
        // t is in seconds
        t /= 60;
        // t is in minutes
        return "" + format2Digit(t/60) + ":" + format2Digit(t % 60);
    }

    private String format2Digit (int t) {
        return ("00" + t).substring(t>9 ? 2 : 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

}
