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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.interfaces.FieldListener;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
public class ChargingActivity extends CanzeActivity implements FieldListener {

    public static final String SID_MaxCharge                        = "7bb.6101.336";
    public static final String SID_SoC                              = "42e.0";          // user SOC, not raw
    public static final String SID_AvChargingPower                  = "427.40";
    public static final String SID_HvTemp                           = "42e.44";
//  public static final String SID_SOH                              = "658.33";
    public static final String SID_RangeEstimate                    = "654.42";
    public static final String SID_TractionBatteryVoltage           = "7ec.623203.24";
    public static final String SID_TractionBatteryCurrent           = "7ec.623204.24";
    public static final String SID_SOH                              = "7ec.623206.24";
    double dcVolt       = 0; // holds the DC voltage, so we can calculate the power when the amps come in

    private ArrayList<Field> subscribedFields;
    double avChPwr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging);

        initListeners();

    }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            MainActivity.device.addActivityField(field);
            subscribedFields.add(field);
        }
        else
        {
            MainActivity.toast("sid " + sid + " does not exist in class Fields");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // free up the listeners again
        for(Field field : subscribedFields)
        {
            field.removeListener(this);
        }
        subscribedFields.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // initialise the widgets
        initListeners();
    }

    private void initListeners() {

        subscribedFields = new ArrayList<>();

        addListener(SID_MaxCharge);
        addListener(SID_SoC);
        addListener(SID_HvTemp);
        if(MainActivity.car==MainActivity.CAR_ZOE) addListener(SID_AvChargingPower);
        addListener(SID_SOH); // state of health gives continious timeouts. This frame is send at a very low rate
        addListener(SID_RangeEstimate);
        addListener(SID_TractionBatteryVoltage);
        addListener(SID_TractionBatteryCurrent);
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

                // get the text field
                switch (fieldId) {

                    case SID_MaxCharge:
                        double maxCharge = field.getValue();
                        int color = 0xffc0c0c0; // standard grey
                        if (maxCharge < (avChPwr * 0.8)) {
                            color = 0xffffc0c0;
                        }
                        tv = (TextView) findViewById(R.id.text_max_charge);
                        tv.setBackgroundColor(color);
                        break;
                    case SID_SoC:
                        tv = (TextView) findViewById(R.id.textSOC);
                        break;
                    case SID_HvTemp:
                        tv = (TextView) findViewById(R.id.textHvTemp);
                        break;
                    case SID_SOH:
                        tv = (TextView) findViewById(R.id.textSOH);
                        break;
                    case SID_RangeEstimate:
                        tv = (TextView) findViewById(R.id.textKMA);
                        if (field.getValue() >= 1023) {
                            tv.setText("---");
                        } else {
                            tv.setText("" + Math.round(field.getValue()));
                        }
                        tv = null;
                        break;
                    case SID_TractionBatteryVoltage: // DC volts
                        // save DC voltage for DC power purposes
                        dcVolt = field.getValue();
                        // continue
                        tv = (TextView) findViewById(R.id.textVolt);
                        break;
                    case SID_TractionBatteryCurrent: // DC amps
                        // calculate DC power
                        double dcPwr = (double)Math.round(dcVolt * field.getValue() / 100.0) / 10.0;
                        tv = (TextView) findViewById(R.id.textDcPwr);
                        tv.setText("" + (dcPwr));
                        // continue
                        tv = (TextView) findViewById(R.id.textAmps);
                        break;
                    case SID_AvChargingPower:
                        avChPwr = field.getValue();
                        tv = (TextView) findViewById(R.id.textAvChPwr);
                        break;
                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText("" + (Math.round(field.getValue() * 10.0) / 10.0));
                }

                tv = (TextView) findViewById(R.id.textDebug);
                tv.setText(fieldId);
            }
        });

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
}