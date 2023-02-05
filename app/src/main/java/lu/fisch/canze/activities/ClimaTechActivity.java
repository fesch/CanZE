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
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
public class ClimaTechActivity extends CanzeActivity implements FieldListener, DebugListener {
    
    private final String[] cooling_Status = MainActivity.getStringList(R.array.list_CoolingStatus);
    private final String[] conditioning_Status = MainActivity.getStringList (MainActivity.isPh2() ? R.array.list_ConditioningStatusPh2
                                                                                                  : R.array.list_ConditioningStatus);
    private final String[] climate_Status = MainActivity.getStringList(MainActivity.isPh2() ? R.array.list_ClimateStatusPh2
                                                                                            : R.array.list_ClimateStatus);
    private final String[] ptc_Relay = MainActivity.getStringList(R.array.list_PtcRelay);
    private final String[] AC_Status = MainActivity.getStringList(R.array.list_ACStatus);
    private final String[] ACAuth = MainActivity.getStringList(R.array.list_ACAuth);
    private final String[] ACError = MainActivity.getStringList(R.array.list_ACError);
    private final String[] ETSState = MainActivity.getStringList(R.array.list_ETSState);
    private final String[] ACInterlock = MainActivity.getStringList(R.array.list_ACInterlock);
    private final String[] FanActivation = MainActivity.getStringList(R.array.list_FanActivation);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainActivity.isSpring()) {
            setContentView(R.layout.activity_climatechspring);
        } else
        {
            setContentView(R.layout.activity_climatech);
        }
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        if (MainActivity.isZOE()) {
        addField(Sid.EngineFanSpeed, 0);
        addField(Sid.HvCoolingState, 0);
        addField(Sid.HvEvaporationTemp, 10000);
        addField(Sid.Pressure, 1000);
        addField(Sid.BatteryConditioningMode, 0);
        addField(Sid.ClimaLoopMode, 0);
        }
        if (MainActivity.isSpring()) {
            addField(Sid.ClimBlowerSpeed, 1000);
            addField(Sid.ClimACConsumption, 1000);
            addField(Sid.ClimACCompressorState, 3000);
            addField(Sid.ClimACCompressorAuth, 3000);
            addField(Sid.ClimACCompressorRPM, 1000);
            addField(Sid.ClimACCompressorVoltage, 3000);
            addField(Sid.ClimACCompressorTemp, 3000);
            addField(Sid.ClimACCompressorError, 3000);
            addField(Sid.BCBWaterTemp, 3000);
            addField(Sid.BCBInternalTemp, 3000);
            addField(Sid.HeatWaterTemp, 3000);
            addField(Sid.HeaterSetpoint, 3000);
            addField(Sid.FanCabinStatus, 1000);
            addField(Sid.ETSState, 1000);
            addField(Sid.FreonPressure, 3000);
            addField(Sid.FreonPressureVoltage, 3000);
            addField(Sid.FreonPressureSensor, 3000);
            addField(Sid.ClimACCompressorInterlock, 3000);
            addField(Sid.FanActivation, 1000);
            addField(Sid.EngineFanSpeedCounter, 1000);
        }

        TextView tv = findViewById(R.id.textLabel_climatePower);
        if (MainActivity.isPh2()) {
            addField(Sid.ThermalComfortPower, 0);
            tv.setText(getResources().getString(R.string.label_ThermalComfortPower));
        } else {
            addField(Sid.DcPowerOut, 0);
            tv.setText(getResources().getString(R.string.label_DcPwr));
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

                    case Sid.EngineFanSpeed:
                        tv = findViewById(R.id.text_EFS);
                        break;
                    case Sid.DcPowerOut:
                        tv = findViewById(R.id.text_ClimatePower);
                        break;
                    // case Sid.ChargingPower:
                    //     tv = (TextView) findViewById(R.id.text_CPO);
                    //     break;
                    case Sid.HvCoolingState:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_HCS);
                        if (tv != null && cooling_Status != null && value >= 0 && value < cooling_Status.length)
                            tv.setText(cooling_Status[value]);
                        tv = null;
                        break;
                    case Sid.HvEvaporationTemp:
                        tv = findViewById(R.id.text_HET);
                        break;
                    case Sid.Pressure:
                        tv = findViewById(R.id.text_PRE);
                        break;
                    case Sid.BatteryConditioningMode:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_HCM);
                        if (tv != null && conditioning_Status != null && value >= 0 && value < conditioning_Status.length)
                            tv.setText(conditioning_Status[value]);
                        tv = null;
                        break;
                    case Sid.ClimaLoopMode:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_CLM);
                        if (tv != null && climate_Status != null && value >= 0 && value < climate_Status.length)
                            tv.setText(climate_Status[value]);
                        tv = null;
                        break;
                    case Sid.ThermalComfortPower:
                         tv = findViewById(R.id.text_ClimatePower);
                         break;
                    //case Sid.PtcRelay1:
                    //    value = (int) field.getValue();
                    //    tv = findViewById(R.id.text_PTC1);
                    //    if (tv != null && ptc_Relay != null && value >= 0 && value < ptc_Relay.length)
                    //        tv.setText(ptc_Relay[value]);
                    //    tv = null;
                    //    break;
                    //case Sid.PtcRelay2:
                    //    value = (int) field.getValue();
                    //    tv = findViewById(R.id.text_PTC2);
                    //    if (tv != null && ptc_Relay != null && value >= 0 && value < ptc_Relay.length)
                    //        tv.setText(ptc_Relay[value]);
                    //    tv = null;
                    //    break;
                    //case Sid.PtcRelay3:
                    //    value = (int) field.getValue();
                    //    tv = findViewById(R.id.text_PTC3);
                    //    if (tv != null && ptc_Relay != null && value >= 0 && value < ptc_Relay.length)
                    //        tv.setText(ptc_Relay[value]);
                    //    tv = null;
                    //    break;

                    case Sid.ClimBlowerSpeed:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_ClimBlowerSpeed);
                        if (value == 0)
                            tv.setText("Fan Off");
                        if (value == 26)
                            tv.setText("Fan level 1");
                        if (value == 50)
                            tv.setText("Fan level 2");
                        if (value == 76)
                            tv.setText("Fan level 3");
                        if (value == 100)
                            tv.setText("Fan level 4 (max)");
                        tv = null;
                        break;
                    case Sid.ClimACConsumption:
                        tv = findViewById(R.id.text_ClimACConsumption);
                        break;
                    case Sid.ClimACCompressorState:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_ClimACCompressorState);
                        if (tv != null && AC_Status != null && value >= 0 && value < AC_Status.length)
                            tv.setText(AC_Status[value]);
                        tv = null;
                        break;
                    case Sid.ClimACCompressorAuth:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_ClimACCompressorAuth);
                        if (tv != null && ACAuth != null && value >= 0 && value < ACAuth.length)
                            tv.setText(ACAuth[value]);
                        tv = null;
                        break;
                    case Sid.ClimACCompressorRPM:
                        tv = findViewById(R.id.text_ClimACCompressorRPM);
                        break;
                    case Sid.ClimACCompressorVoltage:
                        tv = findViewById(R.id.text_ClimACCompressorVoltage);
                        break;
                    case Sid.ClimACCompressorTemp:
                        tv = findViewById(R.id.text_ClimACCompressorTemp);
                        break;
                    case Sid.ClimACCompressorError:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_ClimACCompressorError);
                        if (tv != null && ACError != null && value >= 0 && value < ACError.length)
                            tv.setText(ACError[value]);
                        tv = null;
                        break;
                    case Sid.BCBWaterTemp:
                        tv = findViewById(R.id.text_BCBWaterTemp);
                        break;
                    case Sid.BCBInternalTemp:
                        tv = findViewById(R.id.text_BCBInternalTemp);
                        break;
                    case Sid.HeatWaterTemp:
                        tv = findViewById(R.id.text_HeatWaterTemp);
                        break;
                    case Sid.HeaterSetpoint:
                        tv = findViewById(R.id.text_HeaterSetpoint);
                        break;
                    case Sid.FanCabinStatus:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_FanCabinStatus);
                        if (value == 0)
                            tv.setText("Blower Ok");
                        if (value == 1)
                            tv.setText("Blower Not Ok");
                        tv = null;
                        break;
                    case Sid.ETSState:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_ETSState);
                        if (tv != null && ETSState != null && value >= 0 && value < ETSState.length)
                            tv.setText(ETSState[value]);
                        tv = null;
                        break;
                    case Sid.FreonPressure:
                        tv = findViewById(R.id.text_FreonPressure);
                        break;
                    case Sid.FreonPressureVoltage:
                        tv = findViewById(R.id.text_FreonPressureVoltage);
                        break;
                    case Sid.FreonPressureSensor:
                        tv = findViewById(R.id.text_FreonPressureSensor);
                        break;
                    case Sid.ClimACCompressorInterlock:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_ClimACCompressorInterlock);
                        if (tv != null && ACInterlock != null && value >= 0 && value < ACInterlock.length)
                            tv.setText(ACInterlock[value]);
                        tv = null;
                        break;
                    case Sid.FanActivation:
                        value = (int) field.getValue();
                        tv = findViewById(R.id.text_FanActivation);
                        if (tv != null && FanActivation != null && value >= 0 && value < FanActivation.length)
                            tv.setText(FanActivation[value]);
                        tv = null;
                        break;
                    case Sid.EngineFanSpeedCounter:
                        tv = findViewById(R.id.text_EngineFanSpeedCounter);
                        break;




                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText(String.format(Locale.getDefault(), "%.0f", field.getValue()));
                }

                //tv = (TextView) findViewById(R.id.textDebug);
                //tv.setText(fieldId);
            }
        });

    }

}