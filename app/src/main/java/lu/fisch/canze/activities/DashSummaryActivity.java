/*
    CanZE
    Take a closer look at your ZE car

    Copyright (C) 2015 - The CanZE Team
    http://canze.fisch.lu

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.canze.activities;

        import android.os.Bundle;
        import android.view.View;
        import android.widget.ProgressBar;
        import android.widget.TableLayout;
        import android.widget.TextView;

        import java.util.Locale;

        import lu.fisch.canze.R;
        import lu.fisch.canze.actors.Fields;
        import lu.fisch.canze.classes.Sid;
        import lu.fisch.canze.actors.Field;
        import lu.fisch.canze.interfaces.DebugListener;
        import lu.fisch.canze.interfaces.FieldListener;

public class DashSummaryActivity extends CanzeActivity implements FieldListener, DebugListener {


    private double usoc = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashsummary);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);

        addField(Sid.UserSoC, 5000);
        addField(Sid.AvailableEnergy, 5000);
        addField(Sid.SOH, 5000); // state of health gives continuous timeouts. This frame is send at a very low rate
        addField(Sid.RangeEstimate, 5000);
        addField(Sid.HvTemp, 5000);
        addField(Sid.Instant_Consumption, 5000);

    }

    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                TextView tv = null;
                int value;

                switch (fieldId) {
                    case Sid.UserSoC:
                        usoc = field.getValue() / 100.0;
                        tv = findViewById(R.id.textUserSOC);
                        break;
                    case Sid.AvailableEnergy:
                        tv = findViewById(R.id.textAvEner);
                        break;
                    case Sid.SOH:
                        tv = findViewById(R.id.textSOH);
                        tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                        tv = null;
                        break;
                    case Sid.RangeEstimate:
                        tv = findViewById(R.id.textKMA);
                        if (field.getValue() >= 1023) {
                            tv.setText("---");
                        } else {
                            tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                        }
                        tv = null;
                        break;
                    case Sid.HvTemp:
                        tv = findViewById(R.id.textHvTemp);
                        break;
                    case Sid.Instant_Consumption:
                        tv = findViewById(R.id.text_instant_consumption);
                        break;





                }
                // set regular new content, all exceptions handled above
                if (tv != null) {
                    double val = field.getValue();
                    tv.setText(Double.isNaN(val) ? "" : String.format(Locale.getDefault(), "%.1f", val));
                }



            }
        });
    }
}