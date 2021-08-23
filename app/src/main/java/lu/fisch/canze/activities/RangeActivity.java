package lu.fisch.canze.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

public class RangeActivity extends CanzeActivity implements FieldListener, DebugListener {

    private double distance = 0;
    private double range = 0;
    private double rangeWorst = 0;
    private double rangeBest = 0;
    private double energy = 0;
    private double consumption = 1;
    private double consumptionWorst = 1;
    private double consumptionBest = 1;

    private double loss = 0.1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_range);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        loss = settings.getInt("loss", 10) / 100.0;
        MainActivity.debug("LOSS (load): " + loss);

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress((int) (loss * 100) + 50);
        //((TextView) findViewById(R.id.lossView)).setText(seekBar.getProgress()+"%");
        //((TextView) findViewById(R.id.lossView)).setText(String.format(Locale.getDefault(), "%d%%", seekBar.getProgress()));
        updateSeekBar(seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(seekBar);

                // save value
                SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("loss", (int) (loss * 100));
                editor.apply();
                MainActivity.debug("LOSS (save): " + loss);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                updateSeekBar(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSeekBar(seekBar);
            }
        });
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(Sid.RangeEstimate, 2000);
        addField(Sid.AvailableEnergy, 2000);
        addField(Sid.AverageConsumption, 2000);
        addField(Sid.WorstAverageConsumption, 8000);
        addField(Sid.BestAverageConsumption, 8000);
    }

    private void updateSeekBar(SeekBar seekBar) {
        int progressPosition = seekBar.getProgress();
        loss = (progressPosition - 50) / 100.;
        ((TextView) findViewById(R.id.lossView)).setText(String.format(Locale.getDefault(), "%d%%", progressPosition - 50));
        updateRange();
    }

    private void updateRange() {
        TextView tv;
        tv = findViewById(R.id.canzeRange);
        if (tv != null)
            tv.setText(String.format(Locale.getDefault(), "%.1f - %.1f - %.1f", rangeWorst, range * (1 - loss), rangeBest));
/*
        ((TextView) findViewById(R.id.canzeRange)).setText(
                (Math.floor(rangeWorst*100))/100. +" > "+
                (Math.floor(range*100*(1-loss)))/100.+" > "+
                (Math.floor(rangeBest*100))/100.
        ); */
    }


    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                // ProgressBar pb;
                TextView tv;

                switch (fieldId) {
                    case Sid.RangeEstimate:
                        distance = field.getValue();
                        tv = findViewById(R.id.carRange);
                        if (tv != null)
                            tv.setText(String.format(Locale.getDefault(), "%.1f", distance));
                        break;
                    case Sid.AvailableEnergy:
                        energy = field.getValue();
                        range = energy / consumption * 100;
                        rangeWorst = energy / consumptionWorst * 100;
                        rangeBest = energy / consumptionBest * 100;
                        updateRange();
                        break;
                    case Sid.AverageConsumption:
                        consumption = field.getValue();
                        range = energy / consumption * 100;
                        updateRange();
                        break;
                    case Sid.WorstAverageConsumption:
                        consumptionWorst = field.getValue();
                        rangeWorst = energy / consumptionWorst * 100;
                        updateRange();
                        break;
                    case Sid.BestAverageConsumption:
                        consumptionBest = field.getValue();
                        rangeBest = energy / consumptionBest * 100;
                        updateRange();
                        break;
                }
            }
        });

    }
}
