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
import android.widget.TextView;
import java.util.ArrayList;
import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
public class ChargingTechActivity extends CanzeActivity implements FieldListener {

    public static final String SID_MaxCharge                        = "7bb.6101.336";
    public static final String SID_SoC                              = "42e.0";          // user SOC, not raw
    public static final String SID_AvChargingPower                  = "427.40";
    public static final String SID_ACPilot                          = "42e.38";
    public static final String SID_AvEnergy                         = "427.49";

    public static final String SID_TimeToFull                       = "654.32";
    public static final String SID_PlugConnected                    = "654.2";
    public static final String SID_SOH                              = "7ec.623206.24";
    public static final String SID_RangeEstimate                    = "654.42";
    public static final String SID_ChargingStatusDisplay            = "65b.41";
    public static final String SID_TractionBatteryVoltage           = "7ec.623203.24";
    public static final String SID_TractionBatteryCurrent           = "7ec.623204.24";
    public static final String SID_Preamble_CompartmentTemperatures = "7bb.6104."; // (LBC)

    public static final String cha_Status [] = {"No charge", "Waiting (planned)", "Ended", "In progress", "Failure", "Waiting", "Flap open", "Unavailable"};
    public static final String plu_Status [] = {"Not connected", "Connected"};
    double dcVolt       = 0; // holds the DC voltage, so we can calculate the power when the amps come in
    double pilot        = 0;
    int chargingStatus  = 7;
    double soc          = 0;

    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargingtech);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // initialise the widgets
        initListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListeners();
    }

    private void initListeners() {

        subscribedFields = new ArrayList<>();

        addListener(SID_MaxCharge);
        addListener(SID_ACPilot);
        addListener(SID_TimeToFull);
        addListener(SID_PlugConnected);
        addListener(SID_SoC);
        if (MainActivity.car==MainActivity.CAR_ZOE) {
            addListener(SID_AvChargingPower);
        }
        addListener(SID_AvEnergy);
        addListener(SID_SOH); // state of health gives continious timeouts. This frame is send at a very low rate
        addListener(SID_RangeEstimate);
        addListener(SID_ChargingStatusDisplay);
        addListener(SID_TractionBatteryVoltage);
        addListener(SID_TractionBatteryCurrent);

        // Battery compartment temperatures
        int lastCell = (MainActivity.car==MainActivity.CAR_ZOE) ? 296 : 104;
        for (int i = 32; i <= lastCell; i += 24) {
            String sid = SID_Preamble_CompartmentTemperatures + i;
            addListener(sid);
        }
    }

    private void removeListeners () {
        // empty the query loop
        MainActivity.device.clearFields();
        // free up the listeners again
        for (Field field : subscribedFields) {
            field.removeListener(this);
        }
        subscribedFields.clear();
    }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            MainActivity.device.addActivityField(field);
            subscribedFields.add(field);
        } else {
            MainActivity.toast("sid " + sid + " does not exist in class Fields");
        }
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
                        tv = (TextView) findViewById(R.id.text_max_charge);
                        break;
                    case SID_ACPilot:
                        // save pilot amps
                        pilot = field.getValue();
                        if (MainActivity.car != MainActivity.CAR_ZOE) { // for FluKan, aprocimate pgases (always 1) and AvChPwr (amps * 0.225)
                            double avChPwr = (double) Math.round(pilot * 2.25) / 10.0;
                            tv = (TextView) findViewById(R.id.textPhases);
                            tv.setText("1");
                            tv = (TextView) findViewById(R.id.textAvChPwr);
                            tv.setText("" + (avChPwr));
                        }
                        // continue
                        tv = (TextView) findViewById(R.id.text_max_pilot);
                        if (chargingStatus != 3 && MainActivity.car != MainActivity.CAR_ZOE) {
                            tv.setText("-");
                            tv = null;
                        }
                        break;
                    case SID_TimeToFull: // time to full
                        tv = (TextView) findViewById(R.id.textTTF);
                        if (field.getValue() >= 1023) {
                            tv.setText("--:--");
                            tv = null;
                        }
                        break;
                    case SID_SoC:
                        soc = field.getValue() / 100.0;
                        tv = (TextView) findViewById(R.id.textSOC);
                        break;
                    case SID_SOH:
                        tv = (TextView) findViewById(R.id.textSOH);
                        break;
                    case SID_RangeEstimate:
                        tv = (TextView) findViewById(R.id.textKMA);
                        if (field.getValue() >= 1023) {
                            tv.setText("---");
                        } else {
                            tv.setText("" + field.getValue());
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
                    case SID_AvChargingPower: // won't be called for FluKan
                        double avChPwr = field.getValue();
                        tv = (TextView) findViewById(R.id.textPhases);
                        if (pilot == 0) {
                            tv.setText("-");
                        } else if (avChPwr > (pilot * 0.250)) {
                            tv.setText("3");
                        } else {
                            tv.setText("1");
                        }
                        tv = (TextView) findViewById(R.id.textAvChPwr);
                        break;
                    case SID_AvEnergy:
                        if (soc > 0) {
                            tv = (TextView) findViewById(R.id.textETF);
                            tv.setText("" + (Math.round((field.getValue() * (1-soc) / soc) * 10.0) / 10.0));
                        }
                        tv = (TextView) findViewById(R.id.textAvEner);
                        break;
                    case SID_ChargingStatusDisplay:
                        chargingStatus = (int) field.getValue();
                        tv = (TextView) findViewById(R.id.textChaStatus);
                        tv.setText(cha_Status[chargingStatus]);
                        tv = null;
                        break;
                    case SID_PlugConnected:
                        tv = (TextView) findViewById(R.id.textPlug);
                        tv.setText(plu_Status[(int) field.getValue()]);
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "32":
                        tv = (TextView) findViewById(R.id.text_comp_1_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "56":
                        tv = (TextView) findViewById(R.id.text_comp_2_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "80":
                        tv = (TextView) findViewById(R.id.text_comp_3_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "104":
                        tv = (TextView) findViewById(R.id.text_comp_4_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "128":
                        tv = (TextView) findViewById(R.id.text_comp_5_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "152":
                        tv = (TextView) findViewById(R.id.text_comp_6_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "176":
                        tv = (TextView) findViewById(R.id.text_comp_7_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "200":
                        tv = (TextView) findViewById(R.id.text_comp_8_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "224":
                        tv = (TextView) findViewById(R.id.text_comp_9_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "248":
                        tv = (TextView) findViewById(R.id.text_comp_10_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "272":
                        tv = (TextView) findViewById(R.id.text_comp_11_temp);
                        break;
                    case SID_Preamble_CompartmentTemperatures + "296":
                        tv = (TextView) findViewById(R.id.text_comp_12_temp);
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
}