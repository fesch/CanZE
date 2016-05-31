package lu.fisch.canze.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Battery;
import lu.fisch.canze.actors.Field;
//import lu.fisch.canze.classes.ColorRange;
import lu.fisch.canze.classes.ColorRanges;
import lu.fisch.canze.classes.TimePoint;
import lu.fisch.canze.interfaces.FieldListener;
//import lu.fisch.canze.widgets.Plotter;
import lu.fisch.canze.widgets.Timeplot;
import lu.fisch.canze.widgets.WidgetView;

public class PredictionActivity extends AppCompatActivity implements FieldListener {

    public static final String SID_AvChargingPower                  = "427.40";
    public static final String SID_UserSoC                          = "42e.0";          // user SOC, not raw
    public static final String SID_Preamble_CompartmentTemperatures = "7bb.6104."; // (LBC)
    public static final String SID_RangeEstimate                    = "654.42";


    private Timer timer;
    //private WidgetView widgetView;
    //private Timeplot plotter;

    private Battery battery;
    //private int graphToShow = 0;

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
        battery.setChargerPower (22.0);

        // get the widget view
        // widgetView = (WidgetView) findViewById(R.id.predictionPlotter);

        // new get the plotter
        // plotter = (Timeplot) widgetView.getDrawable();

        // add a color range to the SOC
        ColorRanges colorRanges = new ColorRanges("[" +
                "{'sid':'SOC','color':'#cc00ff','from':0,'to':20}," +
                "{'sid':'SOC','color':'#3ee9ff','from':20,'to':40}," +
                "{'sid':'SOC','color':'#008a1d','from':40,'to':60}," +
                "{'sid':'SOC','color':'#ffaa17','from':60,'to':80}," +
                "{'sid':'SOC','color':'#FF0000','from':80,'to':100}]");
        //plotter.setColorRanges(colorRanges);

        // plotter.setTimeScale(4); //2); // 4 = 40 minutes in the past
        // plotter.setBackward(false);

        // fix the titles
        final String[] titles = {"DC Power","Max DC Power","SOC","Temperature"};
        // generate some values
        //ArrayList<Double> values = new ArrayList<>();
        final HashMap<String,ArrayList<TimePoint>> values = new HashMap<String,ArrayList<TimePoint>>();
        // init arrays & register them in the plotter
        for(int i=0; i< titles.length; i++) {
            values.put(titles[i], new ArrayList<TimePoint>());
        }

        // start collecting data from the car
        initListeners();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                // TextView tv;

                // the calculations and plotter redraws will only be performed when there is a plotter and when all data has been collected from the car
                //if (plotter != null && car_status == 0x07) {
                if (car_status == 0x0f) {

                    // init the array to fill
                    //values.put(titles[graphToShow],new ArrayList<TimePoint>());

                    car_bat_temp = 0;
                    for (int temp_index = (MainActivity.car==MainActivity.CAR_ZOE) ? 12 : 4; temp_index > 0; temp_index--) {
                        car_bat_temp += car_bat_temp_ar [temp_index];
                    }
                    car_bat_temp /= (MainActivity.car==MainActivity.CAR_ZOE) ? 12 : 4;

                    // force debug
                    //car_bat_temp = 0;
                    //car_soc = 10;
                    //car_charger_ac_power = 43;
                    //car_range_est = 14;

                    // set the battery object to an initial state equal to the real battery (
                    battery.setTemperature (car_bat_temp);
                    battery.setStateOfChargePerc(car_soc);

                    // set the external maximum charger capacity
                    battery.setChargerPower(car_charger_ac_power);

                    seconds_per_tick = 60; // (int )(1000 / battery.getChargerPower()); // @ 43kW a tick is roughly 23 seconds, total time 2300 sec = 38 min
                    //long actual = Calendar.getInstance().getTimeInMillis() - 100000 * seconds_per_tick;
                    long actual = Calendar.getInstance().getTimeInMillis();

                    // now start iterating over time
                    for (int t = 1; t <=100; t++) { // 100 ticks
                        battery.iterateCharging(seconds_per_tick);

                        // optimization
                        if ((t % 10) == 0) {
                            double soc = battery.getStateOfChargePerc();
                            updatePrediction("textSOC" + t, "" + ((int)soc));
                            updatePrediction("textRAN" + t, "" + ((int)(car_range_est * soc / car_soc)));
                        }

/*                      switch (graphToShow) {
                            case 0:
                                //values.add(battery.getDcPower() * 2.5);
                                //values.get(titles[graphToShow]).add(new TimePoint(actual, battery.getDcPower() * 2.5));
                                values.get(titles[graphToShow]).add(new TimePoint(actual, t));
                                break;
                          case 1:
                                values.get(titles[graphToShow]).add(new TimePoint(actual, battery.getMaxDcPower() * 2.5));
                                break;
                            case 2:
                                values.get(titles[graphToShow]).add(new TimePoint(actual, battery.getStateOfCharge() * 4.0));
                                break;
                            case 3:
                                values.get(titles[graphToShow]).add(new TimePoint(actual, battery.getTemperature() * 4.0 - 30));
                                break;
                        } */
                        actual += seconds_per_tick * 1000;
                    }

                    //if (++graphToShow > 3) graphToShow = 0;

                    // set the plotters title
                    //plotter.setTitle(PredictionActivity.implode(" / ",titles));

                        // set the values
                    //plotter.setValues(values);

                    // request repaint
                    //widgetView.repaint();
                }
                updatePrediction("textacpwr", "" + ((int)(car_charger_ac_power * 10)) / 10 + " kW");
                updatePrediction("textsoc",   "" + (int)car_soc + "%");
                updatePrediction("texttemp",  "" + (int)car_bat_temp + "°C");
            }
        }, 5000, 5000);

    }

    public static String implode(String separator, Object[] data) {
        String result = "";
        for(int i=0; i<data.length; i++)
        {
            if(i==0) result=data[i].toString();
            else result+=separator+data[i].toString();
        }
        return result;
    }


    private void initListeners() {

        subscribedFields = new ArrayList<>();

        addListener(SID_RangeEstimate, 15000);
        addListener(SID_AvChargingPower, 15000);
        addListener(SID_UserSoC, 15000);
        // Battery compartment temperatures
        int lastCell = (MainActivity.car==MainActivity.CAR_ZOE) ? 296 : 104;
        for (int i = 32; i <= lastCell; i += 24) {
            String sid = SID_Preamble_CompartmentTemperatures + i;
            addListener(sid, 15000);
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
                        car_status |= 0x01;
                        break;
                    case SID_UserSoC:
                        car_soc = field.getValue();
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
                        if (MainActivity.car != MainActivity.CAR_ZOE) car_status |= 0x04;
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
                        // set temp to valid when the last module temperature is in
                        car_status |= 0x04;
                        break;
                    case SID_RangeEstimate:
                        car_range_est = field.getValue();
                        car_status |= 0x08;
                        break;
                }
                tv = (TextView) findViewById(R.id.textDebug);
                tv.setText(fieldId + ", status:" + car_status);
                // display the debug values

            }
        });
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

}
