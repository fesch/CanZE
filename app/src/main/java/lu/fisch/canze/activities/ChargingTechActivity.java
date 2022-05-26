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
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.devices.Device;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

public class ChargingTechActivity extends CanzeActivity implements FieldListener, DebugListener {

    // Bcbtesterinit and -awake are hardcoded here, but shoud of course be optionally taken from
    // the ECU getSessionRequired and getStartDiag. However, since the BCB fields are aliassed in
    // Ph2 to the 11 bits address, we leave it like this for now

    /* Todo
         Add unaliased settings for BCB tester fields in _FieldsPh2, and make tester logic here
         use the formal ECU settings
     */

    public static final String DefaultFormatTemperature = "%3.0f";
    public static final String DefaultFormatBalancing = "%02X";

    private final String[] plug_Status = MainActivity.getStringList(R.array.list_PlugStatus);
    private final String[] mains_Current_Type = MainActivity.getStringList(R.array.list_MainsCurrentType);
    private final String[] supervisor_State = MainActivity.getStringList(MainActivity.isPh2() ? R.array.list_SupervisorStatePh2
                                                                                              : R.array.list_SupervisorState);
    private final String[] completion_Status = MainActivity.getStringList(R.array.list_CompletionStatus);
    private final String[] evse_status = MainActivity.getStringList(R.array.list_EVSEStatus);
    private final String[] evse_failure_status = MainActivity.getStringList(R.array.list_EVSEFailureStatus);
    private final String[] ev_ready_status = MainActivity.getStringList(R.array.list_EVReady);
    private final String[] cplc_com_status = MainActivity.getStringList(R.array.list_CPLCComStatus);
    private final String[] ev_request_state = MainActivity.getStringList(R.array.list_EVRequestState);
    private final String[] evse_state = MainActivity.getStringList(R.array.list_EVSEState);
    private final String[] limit_reached = MainActivity.getStringList(R.array.list_EVSELimitReached);

    private double dcVolt = 0; // holds the DC voltage, so we can calculate the power when the amps come in
    private double pilot = 0;
    private double usoc = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargingtech);
        if (MainActivity.isPh2()){
            TableLayout tl;
            tl = findViewById(R.id.tableLayoutEVSE);
            tl.setVisibility(View.VISIBLE);
        }
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(Sid.BcbTesterInit, Device.INTERVAL_ONCE);
        addField(Sid.MaxCharge, 5000);
        addField(Sid.ACPilot, 5000);
        addField(Sid.PlugConnected, 5000);
        addField(Sid.UserSoC, 5000);
        addField(Sid.RealSoC, 5000);
        addField(Sid.AvailableChargingPower, 5000);
        addField(Sid.AvailableEnergy, 5000);
        addField(Sid.SOH, 5000); // state of health gives continuous timeouts. This frame is send at a very low rate
        addField(Sid.RangeEstimate, 5000);
        addField(Sid.HvKilometers, 5000);
        addField(Sid.TractionBatteryVoltage, 5000);
        addField(Sid.TractionBatteryCurrent, 5000);

        // Battery compartment temperatures
        int lastCell = 12;
        for (int i = 0; i < lastCell; i++) {
            String sid = Sid.Preamble_CompartmentTemperatures + (32 + i * 24);
            addField(sid, 5000);
            sid = Sid.Preamble_BalancingBytes + (16 + i * 8);
            addField(sid, 5000);
        }

        addField(Sid.BcbTesterAwake, 1500);
        addField(Sid.MainsCurrentType);
        addField(Sid.Phase1currentRMS);
        addField(Sid.Phase2CurrentRMS);
        addField(Sid.Phase3CurrentRMS);
        if (!MainActivity.isPh2() && !MainActivity.isSpring()) {
            addField(Sid.PhaseVoltage1);
            addField(Sid.PhaseVoltage2);
            addField(Sid.PhaseVoltage3);
        }
        if (!MainActivity.isSpring()) {
            addField(Sid.InterPhaseVoltage12);
            addField(Sid.InterPhaseVoltage23);
            addField(Sid.InterPhaseVoltage31);
        }
        addField(Sid.MainsActivePower);
        addField(Sid.GroundResistance);
        addField(Sid.SupervisorState);
        addField(Sid.CompletionStatus);

        // TODO: Add variable holding information if CCS charging is available for the car
        if (MainActivity.isPh2()){
            addField(Sid.CCSEVSEStatus);
            addField(Sid.CCSFailureStatus);
            addField(Sid.CCSEVReady);
            addField(Sid.CCSCPLCComStatus);
            addField(Sid.CCSEVRequestState);
            addField(Sid.CCSEVSEState);
            addField(Sid.CCSEVSEMaxPower);
            addField(Sid.CCSEVSEPowerLimitReached);
            addField(Sid.CCSEVSEMaxVoltage);
            addField(Sid.CCSEVSEPresentVoltage);
            addField(Sid.CCSEVSEVoltageLimitReaced);
            addField(Sid.CCSEVSEMaxCurrent);
            addField(Sid.CCSEVSEPresentCurrent);
            addField(Sid.CCSEVSECurrentLimitReached);
        }

        if (MainActivity.altFieldsMode) {
            addField(Sid.BcbVersion); // pre 0x0800 versions have a pilot PWM resolution of 1
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

                    case Sid.MaxCharge:
                        tv = findViewById(R.id.text_max_charge);
                        break;

                    case Sid.ACPilot:
                        pilot = field.getValue(); // save pilot amps
                        tv = findViewById(R.id.text_max_pilot);
                        tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                        tv = null;
                        break;

                    case Sid.UserSoC:
                        usoc = field.getValue() / 100.0;
                        tv = findViewById(R.id.textUserSOC);
                        break;

                    case Sid.RealSoC:
                        tv = findViewById(R.id.textRealSOC);
                        break;

                    case Sid.SOH:
                        tv = findViewById(R.id.textSOH);
                        tv.setText(String.format(Locale.getDefault(), "%.1f", field.getValue()));
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

                    case Sid.TractionBatteryVoltage: // DC volts
                        // save DC voltage for DC power purposes
                        dcVolt = field.getValue();
                        // continue
                        tv = findViewById(R.id.textVolt);
                        break;

                    case Sid.TractionBatteryCurrent: // DC amps
                        // calculate DC power
                        double dcPwr = dcVolt * field.getValue() / 1000.0;
                        tv = findViewById(R.id.textDcPwr);
                        tv.setText(String.format(Locale.getDefault(), "%.1f", dcPwr));
                        // continue
                        tv = findViewById(R.id.textAmps);
                        break;

                    case Sid.AvailableChargingPower:
                        double avChPwr = field.getValue();
                        tv = findViewById(R.id.textAvChPwr);
                        if (avChPwr > 45.0) {
                            tv.setText("-");
                            tv = null;
                        }
                        break;

                    case Sid.AvailableEnergy:
                        if (usoc > 0) {
                            tv = findViewById(R.id.textETF);
                            tv.setText(String.format(Locale.getDefault(), "%.1f", field.getValue() * (1 - usoc) / usoc));
                        }
                        tv = findViewById(R.id.textAvEner);
                        break;

                    case Sid.HvKilometers:
                        tv = findViewById(R.id.textHKM);
                        tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                        tv = null;
                        break;

                    case Sid.PlugConnected:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.textPlug);
                        if (tv != null && plug_Status != null && value >= 0 && value < plug_Status.length)
                            tv.setText(plug_Status[value]);
                        tv = null;
                        break;

                    case Sid.Preamble_CompartmentTemperatures + "32":
                        tv = findViewById(R.id.text_comp_1_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "56":
                        tv = findViewById(R.id.text_comp_2_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "80":
                        tv = findViewById(R.id.text_comp_3_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "104":
                        tv = findViewById(R.id.text_comp_4_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "128":
                        tv = findViewById(R.id.text_comp_5_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "152":
                        tv = findViewById(R.id.text_comp_6_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "176":
                        tv = findViewById(R.id.text_comp_7_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "200":
                        tv = findViewById(R.id.text_comp_8_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "224":
                        tv = findViewById(R.id.text_comp_9_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "248":
                        tv = findViewById(R.id.text_comp_10_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "272":
                        tv = findViewById(R.id.text_comp_11_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_CompartmentTemperatures + "296":
                        tv = findViewById(R.id.text_comp_12_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatTemperature, field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "16":
                        tv = findViewById(R.id.text_bala_1_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "24":
                        tv = findViewById(R.id.text_bala_2_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "32":
                        tv = findViewById(R.id.text_bala_3_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "40":
                        tv = findViewById(R.id.text_bala_4_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "48":
                        tv = findViewById(R.id.text_bala_5_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "56":
                        tv = findViewById(R.id.text_bala_6_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "64":
                        tv = findViewById(R.id.text_bala_7_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "72":
                        tv = findViewById(R.id.text_bala_8_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "80":
                        tv = findViewById(R.id.text_bala_9_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "88":
                        tv = findViewById(R.id.text_bala_10_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "96":
                        tv = findViewById(R.id.text_bala_11_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.Preamble_BalancingBytes + "104":
                        tv = findViewById(R.id.text_bala_12_temp);
                        tv.setText(String.format(Locale.getDefault(), DefaultFormatBalancing, (int) field.getValue()));
                        tv = null;
                        break;
                    case Sid.MainsCurrentType:
                        tv = findViewById(R.id.textMainsCurrentType);
                        value = (int) field.getValue();
                        if (tv != null && mains_Current_Type != null && value >= 0 && value < mains_Current_Type.length)
                            tv.setText(mains_Current_Type[value]);
                        tv = null;
                        break;
                    case Sid.Phase1currentRMS:
                        tv = findViewById(R.id.textPhase1CurrentRMS);
                        break;
                    case Sid.Phase2CurrentRMS:
                        tv = findViewById(R.id.textPhase2CurrentRMS);
                        break;
                    case Sid.Phase3CurrentRMS:
                        tv = findViewById(R.id.textPhase3CurrentRMS);
                        break;
                    case Sid.PhaseVoltage1:
                        tv = findViewById(R.id.textPhaseVoltage1);
                        break;
                    case Sid.PhaseVoltage2:
                        tv = findViewById(R.id.textPhaseVoltage2);
                        break;
                    case Sid.PhaseVoltage3:
                        tv = findViewById(R.id.textPhaseVoltage3);
                        break;
                    case Sid.InterPhaseVoltage12:
                        tv = findViewById(R.id.textInterPhaseVoltage12);
                        break;
                    case Sid.InterPhaseVoltage23:
                        tv = findViewById(R.id.textInterPhaseVoltage23);
                        break;
                    case Sid.InterPhaseVoltage31:
                        tv = findViewById(R.id.textInterPhaseVoltage31);
                        break;
                    case Sid.MainsActivePower:
                        tv = findViewById(R.id.textMainsActivePower);
                        break;
                    case Sid.GroundResistance:
                        tv = findViewById(R.id.textGroundResistance);
                        break;
                    case Sid.SupervisorState:
                        tv = findViewById(R.id.textSupervisorState);
                        value = (int) field.getValue();
                        if (tv != null && supervisor_State != null && value >= 0 && value < supervisor_State.length)
                            tv.setText(supervisor_State[value]);
                        tv = null;
                        break;
                    case Sid.CompletionStatus:
                        tv = findViewById(R.id.textCompletionStatus);
                        value = (int) field.getValue();
                        if (tv != null && completion_Status != null && value >= 0 && value < completion_Status.length)
                            tv.setText(completion_Status[value]);
                        tv = null;
                        break;
                    case Sid.CCSEVSEStatus:
                        tv = findViewById(R.id.textEVSEStatus);
                        value = (int) field.getValue();
                        if (tv != null && evse_status != null && value >= 0 && value < evse_status.length)
                            tv.setText(evse_status[value]);
                        tv = null;
                        break;
                    case Sid.CCSFailureStatus:
                        tv = findViewById(R.id.textEVSEFailureStatus);
                        value = (int) field.getValue();
                        if (tv != null && evse_failure_status != null && value >= 0 && value < evse_failure_status.length)
                            tv.setText(evse_failure_status[value]);
                        tv = null;
                        break;
                    case Sid.CCSEVReady:
                        tv = findViewById(R.id.textEVReady);
                        value = (int) field.getValue();
                        if (tv != null && ev_ready_status != null && value >= 0 && value < ev_ready_status.length)
                            tv.setText(ev_ready_status[value]);
                        tv = null;
                        break;
                    case Sid.CCSCPLCComStatus:
                        tv = findViewById(R.id.textCPLCComStatus);
                        value = (int) field.getValue();
                        if (tv != null && cplc_com_status != null && value >= 0 && value < cplc_com_status.length)
                            tv.setText(cplc_com_status[value]);
                        tv = null;
                        break;
                    case Sid.CCSEVRequestState:
                        tv = findViewById(R.id.textEVRequestState);
                        value = (int) field.getValue();
                        if (tv != null && ev_request_state != null && value >= 0 && value < ev_request_state.length)
                            tv.setText(ev_request_state[value]);
                        tv = null;
                        break;
                    case Sid.CCSEVSEState:
                        tv = findViewById(R.id.textEVSEState);
                        value = (int) field.getValue();
                        if (tv != null && evse_state != null && value >= 0 && value < evse_state.length)
                            tv.setText(evse_state[value]);
                        tv = null;
                        break;
                    case Sid.CCSEVSEMaxPower:
                        tv = findViewById(R.id.textEVSEMaxPower);
                        break;
                    case Sid.CCSEVSEPowerLimitReached:
                        tv = findViewById(R.id.textEVSEPowerLimitReached);
                        value = (int) field.getValue();
                        if (tv != null && limit_reached != null && value >= 0 && value < limit_reached.length)
                            tv.setText(limit_reached[value]);
                        tv = null;
                        break;
                    case Sid.CCSEVSEMaxVoltage:
                        tv = findViewById(R.id.textEVSEMaxVoltage);
                        break;
                    case Sid.CCSEVSEPresentVoltage:
                        tv = findViewById(R.id.textEVSEPresentVoltage);
                        break;
                    case Sid.CCSEVSEVoltageLimitReaced:
                        tv = findViewById(R.id.textEVSEVoltageLimitReached);
                        value = (int) field.getValue();
                        if (tv != null && limit_reached != null && value >= 0 && value < limit_reached.length)
                            tv.setText(limit_reached[value]);
                        tv = null;
                        break;
                    case Sid.CCSEVSEMaxCurrent:
                        tv = findViewById(R.id.textEVSEMaxCurrent);
                        break;
                    case Sid.CCSEVSEPresentCurrent:
                        tv = findViewById(R.id.textEVSEPresentCurrent);
                        break;
                    case Sid.CCSEVSECurrentLimitReached:
                        tv = findViewById(R.id.textEVSECurrentLimitReached);
                        value = (int) field.getValue();
                        if (tv != null && limit_reached != null && value >= 0 && value < limit_reached.length)
                            tv.setText(limit_reached[value]);
                        tv = null;
                        break;
                    case Sid.BcbVersion:
                        Field bcbVersionField = Fields.getInstance().getBySID(Sid.ACPilotDutyCycle);
                        if (bcbVersionField != null)
                            bcbVersionField.setResolution(((int)field.getValue() < 0x0800) ? 1.0 : 0.5);
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