package lu.fisch.canze.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Battery;
import lu.fisch.canze.classes.ColorRange;
import lu.fisch.canze.classes.ColorRanges;
import lu.fisch.canze.classes.TimePoint;
import lu.fisch.canze.widgets.Plotter;
import lu.fisch.canze.widgets.Timeplot;
import lu.fisch.canze.widgets.WidgetView;

public class PredictionActivity extends AppCompatActivity {

    private Timer timer;
    private WidgetView widgetView;
    private Timeplot plotter;

    private Battery battery;
    private int graphToShow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        // initialize the battery, now with fixed values
        // need to change this to get parameters from the car of course

        battery = new Battery();
        battery.setChargerPower (22.0);

        // get the widget view
        widgetView = (WidgetView) findViewById(R.id.predictionPlotter);

        // new get the plotter
        plotter = (Timeplot) widgetView.getDrawable();

        // add a color range to the SOC
        ColorRanges colorRanges = new ColorRanges("[" +
                "{'sid':'SOC','color':'#cc00ff','from':0,'to':20}," +
                "{'sid':'SOC','color':'#3ee9ff','from':20,'to':40}," +
                "{'sid':'SOC','color':'#008a1d','from':40,'to':60}," +
                "{'sid':'SOC','color':'#ffaa17','from':60,'to':80}," +
                "{'sid':'SOC','color':'#FF0000','from':80,'to':100}]");
        plotter.setColorRanges(colorRanges);

        plotter.setTimeScale(2);

        // fix the titles
        final String[] titles = {"DC Power","Max DC Power","SOC","Temperature"};
        // generate some values
        //ArrayList<Double> values = new ArrayList<>();
        final HashMap<String,ArrayList<TimePoint>> values = new HashMap<String,ArrayList<TimePoint>>();
        // init arrays & register them in the plotter
        for(int i=0; i< titles.length; i++) {
            values.put(titles[i], new ArrayList<TimePoint>());
        }


        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (plotter != null) {

                    // init the array to fille
                    values.put(titles[graphToShow],new ArrayList<TimePoint>());

                    long actual = Calendar.getInstance().getTimeInMillis()-100*23*1000;

                    battery.setTemperature (10.0);
                    battery.setStateOfCharge(5.0);
                    for (int t = 1; t <=100; t++) { // 100 ticks
                        battery.iterateCharging((int)(1000 / battery.getChargerPower())); // @ 43kW a tick is roughly 23 seconds, total time 2300 sec = 38 min
                        switch (graphToShow) {
                            case 0:
                                //values.add(battery.getDcPower() * 2.5);
                                values.get(titles[graphToShow]).add(new TimePoint(actual, battery.getDcPower() * 2.5));
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
                        }
                        actual += 23*1000;
                        //draw (battery, t); // imaginary method that plots SOC, range, time
                    }

                    if (++graphToShow > 3) graphToShow = 0;

//
//                    Random random = new Random();
//                    values.add(random.nextDouble()*100);
//                    for (int i = 0; i < 100; i++) {
//                        values.add(Math.max(Math.min((1+(random.nextDouble()/4-1/8.)) * values.get(values.size()-1),100),0));
//                    }

                    //for(int i=0; i< titles.length; i++)
                    //    MainActivity.debug("Values "+i+" > "+values.get(titles[i]).toString());

                    // set the plotters title
                    plotter.setTitle(PredictionActivity.implode(" / ",titles));

                        // set the values
                    plotter.setValues(values);

                    // request repaint
                    widgetView.repaint();
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

}
