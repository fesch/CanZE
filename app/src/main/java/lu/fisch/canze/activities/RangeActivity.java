package lu.fisch.canze.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;

public class RangeActivity extends CanzeActivity {

    public static final String SID_AvailableDistance                  = "654.42";
    public static final String SID_AvailableEnvergy                   = "427.49";
    public static final String SID_AverageConsumption                 = "654.52";
    public static final String SID_WorstAverageConsumption            = "62d.0";
    public static final String SID_BestAverageConsumption             = "62d.10";

    public double distance = 0;
    public double range = 0;
    public double rangeWorst = 0;
    public double rangeBest = 0;
    public double energy = 0;
    public double consumption = 1;
    public double consumptionWorst = 1;
    public double consumptionBest = 1;

    public double loss = 0.1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_range);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        int lossSetting = settings.getInt("loss", 10);
        MainActivity.debug("LOSS (load): "+loss);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(lossSetting);
        //((TextView) findViewById(R.id.lossView)).setText(seekBar.getProgress()+"%");
        ((TextView) findViewById(R.id.lossView)).setText(String.format(Locale.getDefault(), "%d%%", seekBar.getProgress()));


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                loss = seekBar.getProgress()/100.;
                //((TextView) findViewById(R.id.lossView)).setText(seekBar.getProgress()+"%");
                ((TextView) findViewById(R.id.lossView)).setText(String.format(Locale.getDefault(), "%d%%", seekBar.getProgress()));
                updateRange();

                // save value
                SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("loss",(int) (loss*100));
                editor.apply();
                MainActivity.debug("LOSS (save): "+loss);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                loss = seekBar.getProgress()/100.;
                //((TextView) findViewById(R.id.lossView)).setText(seekBar.getProgress()+"%");
                ((TextView) findViewById(R.id.lossView)).setText(String.format(Locale.getDefault(), "%d%%", seekBar.getProgress()));
                updateRange();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                loss = seekBar.getProgress()/100.;
                //((TextView) findViewById(R.id.lossView)).setText(seekBar.getProgress()+"%");
                ((TextView) findViewById(R.id.lossView)).setText(String.format(Locale.getDefault(), "%d%%", seekBar.getProgress()));
                updateRange();
            }
        });
    }

    protected void initListeners () {
        addField(SID_AvailableDistance, 0);
        addField(SID_AvailableEnvergy, 0);
        addField(SID_AverageConsumption, 0);
        addField(SID_WorstAverageConsumption, 0);
        addField(SID_BestAverageConsumption, 0);
    }

    private void updateRange()
    {
        ((TextView) findViewById(R.id.canzeRange)).setText(
                (Math.floor(rangeWorst*100))/100. +" > "+
                (Math.floor(range*100*(1-loss)))/100.+" > "+
                (Math.floor(rangeBest*100))/100.
        );
    }


    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                // ProgressBar pb;
                // TextView tv;

                switch (fieldId) {
                    case SID_AvailableDistance:
                        distance = field.getValue();
                        //((TextView) findViewById(R.id.carRange)).setText(((distance*100)/1)/100.+"");
                        ((TextView) findViewById(R.id.carRange)).setText(String.format(Locale.getDefault(), "%.1f", ((distance*100)/1)/100.));
                        break;
                    case SID_AvailableEnvergy:
                        energy = field.getValue();
                        range      = energy/consumption*100;
                        rangeWorst = energy/consumptionWorst*100;
                        rangeBest  = energy/consumptionBest*100;
                        updateRange();
                        break;
                    case SID_AverageConsumption:
                        consumption = field.getValue();
                        range       = energy/consumption*100;
                        updateRange();
                        break;
                    case SID_WorstAverageConsumption:
                        consumptionWorst = field.getValue();
                        rangeWorst       = energy/consumptionWorst*100;
                        updateRange();
                        break;
                    case SID_BestAverageConsumption:
                        consumptionBest = field.getValue();
                        rangeBest       = energy/consumptionBest*100;
                        updateRange();
                        break;
                }
            }
        });

    }
}
