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
import lu.fisch.canze.widgets.Plotter;
import lu.fisch.canze.widgets.WidgetView;

public class PredictionActivity extends AppCompatActivity {

    private Timer timer;
    private WidgetView widgetView;
    private Plotter plotter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

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
                    Random random = new Random();
                    values.add(random.nextDouble()*100);
                    for (int i = 0; i < 100; i++) {
                        values.add(Math.max(Math.min((1+(random.nextDouble()/4-1/8.)) * values.get(values.size()-1),100),0));
                    }

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
