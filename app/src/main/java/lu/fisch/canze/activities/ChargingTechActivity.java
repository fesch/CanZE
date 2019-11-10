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

public class ChargingTechActivity extends CanzeActivity implements FieldListener, DebugListener {

    private static final String SID_MaxCharge = "7bb.6101.336";
    private static final String SID_UserSoC = "42e.0";          // user SOC, not raw
    private static final String SID_AvailableChargingPower = "427.40";
    private static final String SID_ACPilot = "800.610d.24";
    private static final String SID_AvEnergy = "427.49";

    private static final String SID_PlugConnected = "654.2";
    private static final String SID_SOH = "7ec.623206.24";
    private static final String SID_RangeEstimate = "654.42";
    private static final String SID_TractionBatteryVoltage = "7ec.623203.24";
    private static final String SID_TractionBatteryCurrent = "7ec.623204.24";
    private static final String SID_RealSoC = "7bb.6103.192";
    private static final String SID_12V = "7ec.622005.24";
    private static final String SID_12A = "7ec.623028.24";
    private static final String SID_DcLoad = "1fd.0";
    private static final String SID_HvKilometers = "7bb.6161.96";
    private static final String SID_Preamble_CompartmentTemperatures = "7bb.6104."; // (LBC)
    private static final String SID_Preamble_BalancingBytes = "7bb.6107.";

    private static final String DefaultFormatTemperature = "%3.0f";
    private static final String DefaultFormatBalancing = "%02X";

    private static final String SID_TesterInit = "793.50c0.0";
    private static final String SID_TesterAwake = "793.7e01.0";

    private static final String SID_MainsCurrentType = "793.625017.29";
    private static final String SID_Phase1currentRMS = "793.622001.24";
    private static final String SID_Phase2CurrentRMS = "793.62503a.24"; // Raw <= this seems to be instant DC coupled value
    private static final String SID_Phase3CurrentRMS = "793.62503b.24";
    private static final String SID_PhaseVoltage1 = "793.62502c.24"; // Raw
    private static final String SID_PhaseVoltage2 = "793.62502d.24";
    private static final String SID_PhaseVoltage3 = "793.62502e.24";
    private static final String SID_InterPhaseVoltage12 = "793.62503f.24"; // Measured
    private static final String SID_InterPhaseVoltage23 = "793.625041.24";
    private static final String SID_InterPhaseVoltage31 = "793.625042.24";
    private static final String SID_MainsActivePower = "793.62504a.24";
    private static final String SID_GroundResistance = "793.625062.24";
    private static final String SID_SupervisorState = "793.625063.24";
    private static final String SID_CompletionStatus = "793.625064.24";
    private final String[] plug_Status = MainActivity.getStringList(R.array.list_PlugStatus);
    private final String[] mains_Current_Type = MainActivity.getStringList(R.array.list_MainsCurrentType);
    private final String[] supervisor_State = MainActivity.getStringList(R.array.list_SupervisorState);
    private final String[] completion_Status = MainActivity.getStringList(R.array.list_CompletionStatus);

    private double dcVolt = 0; // holds the DC voltage, so we can calculate the power when the amps come in
    private double pilot = 0;
    private double usoc = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargingtech);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_TesterInit, lu.fisch.canze.devices.Device.INTERVAL_ONCE);
        addField(SID_MaxCharge, 5000);
        addField(SID_ACPilot, 5000);
        addField(SID_PlugConnected, 5000);
        addField(SID_UserSoC, 5000);
        addField(SID_RealSoC, 5000);
        addField(SID_AvailableChargingPower, 5000);
        addField(SID_AvEnergy, 5000);
        addField(SID_SOH, 5000); // state of health gives continuous timeouts. This frame is send at a very low rate
        addField(SID_RangeEstimate, 5000);
        addField(SID_12V, 5000);
        addField(SID_12A, 5000);
        addField(SID_DcLoad, 5000);
        addField(SID_HvKilometers, 5000);
        addField(SID_TractionBatteryVoltage, 5000);
        addField(SID_TractionBatteryCurrent, 5000);

        // Battery compartment temperatures
        int lastCell = 12;
        for (int i = 0; i < lastCell; i++) {
            String sid = SID_Preamble_CompartmentTemperatures + (32 + i * 24);
            addField(sid, 5000);
            sid = SID_Preamble_BalancingBytes + (16 + i * 8);
            addField(sid, 5000);
        }

        addField(SID_TesterAwake, 1500);
        addField(SID_MainsCurrentType);
        addField(SID_Phase1currentRMS);
        addField(SID_Phase2CurrentRMS);
        addField(SID_Phase3CurrentRMS);
        addField(SID_PhaseVoltage1);
        addField(SID_PhaseVoltage2);
        addField(SID_PhaseVoltage3);
        addField(SID_InterPhaseVoltage12);
        addField(SID_InterPhaseVoltage23);
        addField(SID_InterPhaseVoltage31);
        addField(SID_MainsActivePower);
        addField(SID_GroundResistance);
        addField(SID_SupervisorState);
        addField(SID_CompletionStatus);
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
                        tv = findViewById(R.id.text_max_pilot);
                        break;

                    case SID_UserSoC:
                        usoc = field.getValue() / 100.0;
                        tv = findViewById(R.id.textUserSOC);
                        break;

                    case SID_RealSoC:
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
                        double dcPwr = dcVolt * field.getValue() / 1000.0;
                        tv = findViewById(R.id.textDcPwr);
                        tv.setText(String.format(Locale.getDefault(), "%.1f", dcPwr));
                        // continue
                        tv = findViewById(R.id.textAmps);
                        break;

                    case SID_AvailableChargingPower:
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
                        if (avChPwr > 45.0) {
                            tv.setText("-");
                            tv = null;
                        }
                        break;

                    case SID_AvEnergy:
                        if (usoc > 0) {
                            tv = findViewById(R.id.textETF);
                            tv.setText(String.format(Locale.getDefault(), "%.1f", field.getValue() * (1 - usoc) / usoc));
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
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "24":
                        tv = findViewById(R.id.text_bala_2_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "32":
                        tv = findViewById(R.id.text_bala_3_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "40":
                        tv = findViewById(R.id.text_bala_4_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "48":
                        tv = findViewById(R.id.text_bala_5_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "56":
                        tv = findViewById(R.id.text_bala_6_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "64":
                        tv = findViewById(R.id.text_bala_7_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "72":
                        tv = findViewById(R.id.text_bala_8_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "80":
                        tv = findViewById(R.id.text_bala_9_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "88":
                        tv = findViewById(R.id.text_bala_10_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "96":
                        tv = findViewById(R.id.text_bala_11_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_Preamble_BalancingBytes + "104":
                        tv = findViewById(R.id.text_bala_12_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case SID_MainsCurrentType:
                        tv = findViewById(R.id.textMainsCurrentType);
                        value = (int) field.getValue();
                        if (tv != null && mains_Current_Type != null && value >= 0 && value < mains_Current_Type.length)
                            tv.setText(mains_Current_Type[value]);
                        tv = null;
                        break;
                    case SID_Phase1currentRMS:
                        tv = findViewById(R.id.textPhase1CurrentRMS);
                        break;
                    case SID_Phase2CurrentRMS:
                        tv = findViewById(R.id.textPhase2CurrentRMS);
                        break;
                    case SID_Phase3CurrentRMS:
                        tv = findViewById(R.id.textPhase3CurrentRMS);
                        break;
                    case SID_PhaseVoltage1:
                        tv = findViewById(R.id.textPhaseVoltage1);
                        break;
                    case SID_PhaseVoltage2:
                        tv = findViewById(R.id.textPhaseVoltage2);
                        break;
                    case SID_PhaseVoltage3:
                        tv = findViewById(R.id.textPhaseVoltage3);
                        break;
                    case SID_InterPhaseVoltage12:
                        tv = findViewById(R.id.textInterPhaseVoltage12);
                        break;
                    case SID_InterPhaseVoltage23:
                        tv = findViewById(R.id.textInterPhaseVoltage23);
                        break;
                    case SID_InterPhaseVoltage31:
                        tv = findViewById(R.id.textInterPhaseVoltage31);
                        break;
                    case SID_MainsActivePower:
                        tv = findViewById(R.id.textMainsActivePower);
                        break;
                    case SID_GroundResistance:
                        tv = findViewById(R.id.textGroundResistance);
                        break;
                    case SID_SupervisorState:
                        tv = findViewById(R.id.textSupervisorState);
                        value = (int) field.getValue();
                        if (tv != null && supervisor_State != null && value >= 0 && value < supervisor_State.length)
                            tv.setText(supervisor_State[value]);
                        tv = null;
                        break;
                    case SID_CompletionStatus:
                        tv = findViewById(R.id.textCompletionStatus);
                        value = (int) field.getValue();
                        if (tv != null && completion_Status != null && value >= 0 && value < completion_Status.length)
                            tv.setText(completion_Status[value]);
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