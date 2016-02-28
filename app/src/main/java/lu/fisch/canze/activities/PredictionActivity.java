package lu.fisch.canze.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Battery;
import lu.fisch.canze.widgets.Plotter;
import lu.fisch.canze.widgets.WidgetView;

public class PredictionActivity extends AppCompatActivity {

    private Timer timer;
    private WidgetView widgetView;
    private Plotter plotter;

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
        plotter = (Plotter) widgetView.getDrawable();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (plotter != null) {
                    // generate some values
                    ArrayList<Double> values = new ArrayList<>();

                    battery.setTemperature (10.0);
                    battery.setStateOfCharge(5.0);
                    for (int t = 1; t <=100; t++) { // 100 ticks
                        battery.iterateCharging((int)(1000 / battery.getChargerPower())); // @ 43kW a tick is roughly 23 seconds, total time 2300 sec = 38 min
                        switch (graphToShow) {
                            case 0:
                                values.add(battery.getDcPower() * 2.5);
                                break;
                            case 1:
                                values.add(battery.getMaxDcPower() * 2.5);
                                break;
                            case 2:
                                values.add(battery.getStateOfCharge() * 4.0);
                                break;
                            case 3:
                                values.add(battery.getTemperature() * 4.0 - 30);
                                break;
                        }
                        //draw (battery, t); // imaginary method that plots SOC, range, time
                    }

                    if (++graphToShow > 3) graphToShow = 0;
//
//                    Random random = new Random();
//                    values.add(random.nextDouble()*100);
//                    for (int i = 0; i < 100; i++) {
//                        values.add(Math.max(Math.min((1+(random.nextDouble()/4-1/8.)) * values.get(values.size()-1),100),0));
//                    }

                    // set the values
                    plotter.setValues(values);

                    // request repaint
                    widgetView.repaint();
                }
            }
        }, 5000, 5000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

}
