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

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the activity itself.
public class ChargingTechActivity extends CanzeActivity implements FieldListener, DebugListener {

    public static final String SID_MaxCharge                        = "7bb.6101.336";
    public static final String SID_UserSoC                          = "42e.0";          // user SOC, not raw
//  public static final String SID_RealSoC                          = "654.25";         // real SOC
    public static final String SID_AvailableChargingPower           = "427.40";
    public static final String SID_ACPilot                          = "42e.38";
    public static final String SID_AvEnergy                         = "427.49";

    public static final String SID_TimeToFull                       = "654.32";
    public static final String SID_PlugConnected                    = "654.2";
    public static final String SID_SOH                              = "7ec.623206.24";
    public static final String SID_RangeEstimate                    = "654.42";
    public static final String SID_ChargingStatusDisplay            = "65b.41";
    public static final String SID_TractionBatteryVoltage           = "7ec.623203.24";
    public static final String SID_TractionBatteryCurrent           = "7ec.623204.24";
    public static final String SID_EnergyConsumed                   = "7ec.6233dc.24";
    // public static final String SID_CapacityFluKan                   = "7bb.6101.348";
    // public static final String SID_CapacityZoe                      = "";
    public static final String SID_RealSoC                          = "7bb.6103.192";
    public static final String SID_12V                              = "7ec.622005.24";
    public static final String SID_12A                              = "7ec.623028.24";
    public static final String SID_DcLoad                           = "1fd.0";
    public static final String SID_HvKilometers                     = "7bb.6161.96";
    public static final String SID_Preamble_CompartmentTemperatures = "7bb.6104."; // (LBC)
    public static final String SID_Preamble_BalancingBytes          = "7bb.6107.";

    public static final String DefaultFormatTemperature             = "%3.0f";
    public static final String DefaultFormatBalancing               = "%02X";

    //public static final String cha_Status [] = {"No charge", "Waiting (planned)", "Ended", "In progress", "Failure", "Waiting", "Flap open", "Unavailable"};
    final String charging_Status [] = MainActivity.getStringList(R.array.list_ChargingStatus);
    //public static final String plu_Status [] = {"Not connected", "Connected"};
    final String plug_Status [] = MainActivity.getStringList(R.array.list_PlugStatus);

    double dcVolt       = 0; // holds the DC voltage, so we can calculate the power when the amps come in
    double pilot        = 0;
    int chargingStatus  = 7;
    double usoc          = 0;
    //double rsoc          = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargingtech);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_MaxCharge, 5000);
        addField(SID_ACPilot, 5000);
        addField(SID_TimeToFull, 5000);
        addField(SID_PlugConnected, 5000);
        addField(SID_UserSoC, 5000);
        addField(SID_RealSoC, 5000);
        if (MainActivity.isZOE()) {
            addField(SID_AvailableChargingPower, 5000);
        //} else {
        //    addFields(SID_CapacityFluKan, 5000);
        }
        addField(SID_AvEnergy, 5000);
        addField(SID_SOH, 5000); // state of health gives continuous timeouts. This frame is send at a very low rate
        addField(SID_RangeEstimate, 5000);
        addField(SID_12V, 5000);
        addField(SID_12A, 5000);
        addField(SID_DcLoad, 5000);
        addField(SID_HvKilometers, 5000);
        addField(SID_EnergyConsumed, 5000);
        addField(SID_ChargingStatusDisplay, 5000);
        addField(SID_TractionBatteryVoltage, 5000);
        addField(SID_TractionBatteryCurrent, 5000);

        // Battery compartment temperatures
        int lastCell = MainActivity.isZOE() ? 12 : 4;
        for (int i = 0; i < lastCell; i++) {
            String sid = SID_Preamble_CompartmentTemperatures + (32 + i * 24);
            addField(sid, 5000);
            if (MainActivity.isZOE()) sid = SID_Preamble_BalancingBytes + (16 + i * 8);
            addField(sid, 5000);
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
                int value;

                // get the text field
                switch (fieldId) {

                    case SID_MaxCharge:
                        tv = findViewById(R.id.text_max_charge);
                        break;
                    case SID_ACPilot:
                        // save pilot amps
                        pilot = field.getValue();
                        if (MainActivity.isFluKan ()) { // for FluKan, approximate phases (always 1) and AvChPwr (amps * 0.225)
                            double avChPwr = (double) Math.round(pilot * 2.25) / 10.0;
                            tv = findViewById(R.id.textPhases);
                            tv.setText("1");
                            tv = findViewById(R.id.textAvChPwr);
                            tv.setText(String.format(Locale.getDefault(), "%.1f", avChPwr));
                        }
                        // continue
                        tv = findViewById(R.id.text_max_pilot);
                        if (chargingStatus != 3 && MainActivity.isZOE()) {
                            tv.setText("-");
                            tv = null;
                        }
                        break;
                    case SID_TimeToFull: // time to full
                        tv = findViewById(R.id.textTTF);
                        if (field.getValue() >= 1023) {
                            tv.setText("--:--");
                        } else {
                            tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                        }
                        tv = null;
                        break;
                    case SID_UserSoC:
                        usoc = field.getValue() / 100.0;
                        tv = findViewById(R.id.textUserSOC);
                        break;
                    case SID_RealSoC:
                        //rsoc = field.getValue() / 100.0;
                        tv = findViewById(R.id.textRealSOC);
                        break;
                    case SID_SOH:
                        tv = findViewById(R.id.textSOH);
                        tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                        tv = null;
                        break;
                    case SID_RangeEstimate:
                        tv = findViewById(R.id.textKMA);
                        if (field.getValue() >= 1023) {
                            tv.setText("---");
                        } else {
                            tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                        }
                        tv = null;
                        break;
                    case SID_TractionBatteryVoltage: // DC volts
                        // save DC voltage for DC power purposes
                        dcVolt = field.getValue();
                        // continue
                        tv = findViewById(R.id.textVolt);
                        break;
                    case SID_TractionBatteryCurrent: // DC amps
                        // calculate DC power
                        double dcPwr = (double)Math.round(dcVolt * field.getValue() / 100.0) / 10.0;
                        tv = findViewById(R.id.textDcPwr);
                        tv.setText(String.format(Locale.getDefault(), "%.1f", dcPwr));
                        // continue
                        tv = findViewById(R.id.textAmps);
                        break;
                    case SID_AvailableChargingPower: // won't be called for FluKan
                        double avChPwr = field.getValue();
                        tv = findViewById(R.id.textPhases);
                        if (pilot == 0 || avChPwr > 45.0) {
                            tv.setText("-");
                        } else if (avChPwr > (pilot * 0.250)) {
                            tv.setText("3");
                        } else {
                            tv.setText("1");
                        }
                        tv = findViewById(R.id.textAvChPwr);
                        break;
                    case SID_AvEnergy:
                        if (usoc > 0) {
                            tv = findViewById(R.id.textETF);
                            tv.setText(String.format(Locale.getDefault(), "%.1f", field.getValue() * (1-usoc) / usoc));
                            //tv.setText("" + (Math.round((field.getValue() * (1-soc) / soc) * 10.0) / 10.0));
                        }
                        tv = findViewById(R.id.textAvEner);
                        break;
                    case SID_12V:
                        tv = findViewById(R.id.text12V);
                        break;
                    case SID_12A:
                        tv = findViewById(R.id.text12A);
                        break;
                    case SID_DcLoad:
                        tv = findViewById(R.id.textDcLoad);
                        break;
                    case SID_HvKilometers:
                        tv = findViewById(R.id.textHKM);
                        tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                        tv = null;
                        break;
                    case SID_EnergyConsumed: // see http://canze.fisch.lu/qa/#comment-1074. Seems another field is needed for R240
                        tv = findViewById(R.id.textENC);
                        break;
                    //case SID_CapacityFluKan:
                    //case SID_CapacityZoe:
                    //    tv = findViewById(R.id.textCapacity);
                    //    tv.setText("" + field.getValue());
                    //    tv = null;
                    //    break;
                    case SID_ChargingStatusDisplay:
                        chargingStatus = (int) field.getValue();
                        tv = findViewById(R.id.textChaStatus);
                        if (tv != null && charging_Status != null && chargingStatus >= 0 && chargingStatus < charging_Status.length)
                            tv.setText(charging_Status[chargingStatus]);
                        tv = null;
                        break;
                    case SID_PlugConnected:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.textPlug);
                        if (tv != null && plug_Status != null && value >= 0 && value < plug_Status.length)
                            tv.setText(plug_Status[value]);
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "32":
                        tv = findViewById(R.id.text_comp_1_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "56":
                        tv = findViewById(R.id.text_comp_2_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "80":
                        tv = findViewById(R.id.text_comp_3_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "104":
                        tv = findViewById(R.id.text_comp_4_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "128":
                        tv = findViewById(R.id.text_comp_5_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "152":
                        tv = findViewById(R.id.text_comp_6_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "176":
                        tv = findViewById(R.id.text_comp_7_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "200":
                        tv = findViewById(R.id.text_comp_8_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "224":
                        tv = findViewById(R.id.text_comp_9_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "248":
                        tv = findViewById(R.id.text_comp_10_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "272":
                        tv = findViewById(R.id.text_comp_11_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_CompartmentTemperatures + "296":
                        tv = findViewById(R.id.text_comp_12_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "16":
                        tv = findViewById(R.id.text_bala_1_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "24":
                        tv = findViewById(R.id.text_bala_2_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "32":
                        tv = findViewById(R.id.text_bala_3_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "40":
                        tv = findViewById(R.id.text_bala_4_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "48":
                        tv = findViewById(R.id.text_bala_5_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "56":
                        tv = findViewById(R.id.text_bala_6_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "64":
                        tv = findViewById(R.id.text_bala_7_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "72":
                        tv = findViewById(R.id.text_bala_8_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "80":
                        tv = findViewById(R.id.text_bala_9_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "88":
                        tv = findViewById(R.id.text_bala_10_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "96":
                        tv = findViewById(R.id.text_bala_11_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "104":
                        tv = findViewById(R.id.text_bala_12_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int)field.getValue()));
                        tv = null;
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