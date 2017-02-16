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


/*
 * This class manages all know fields.
 * Actually only the simple fields from the free CAN stream are handled.
 */
package lu.fisch.canze.actors;

import android.os.Environment;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.interfaces.VirtualFieldAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author robertfisch test
 */
public class Fields {

    private static final int FIELD_ID           = 0; // to be stated in HEX, no leading 0x
    private static final int FIELD_FROM         = 1; // decimal
    private static final int FIELD_TO           = 2; // decimal
    private static final int FIELD_RESOLUTION   = 3; // double
    private static final int FIELD_OFFSET       = 4; // double
    private static final int FIELD_DECIMALS     = 5; // decimal
    private static final int FIELD_UNIT         = 6;
    private static final int FIELD_REQUEST_ID   = 7; // to be stated in HEX, no leading 0x
    private static final int FIELD_RESPONSE_ID  = 8; // to be stated in HEX, no leading 0x
    private static final int FIELD_OPTIONS      = 9; // to be stated in HEX, no leading 0x
    private static final int FIELD_NAME         = 10; // can be displayed/saved. Now only used for Diag ISO-TP
    private static final int FIELD_LIST         = 11; // same

    public static final int TOAST_NONE          = 0;
    public static final int TOAST_DEVICE        = 1;
    public static final int TOAST_ALL           = 2;

    private final ArrayList<Field> fields = new ArrayList<>();
    private final HashMap<String, Field> fieldsBySid = new HashMap<>();

    private static Fields instance = null;
    private double runningUsage = 0;

    //private int car = CAR_ANY;

    private Fields() {
        // the will be called by load(), and only after we know (or have changed) the car
        //fillStatic();
        //addVirtualFields();
    }

    public static boolean initialised()
    {
        return (instance==null);
    }

    public static Fields getInstance()
    {
        if(instance==null) instance=new Fields();
        return instance;
    }

    private void addVirtualFields() {
        addVirtualFieldUsage();
        addVirtualFieldUsageLpf();
        addVirtualFieldFrictionTorque();
        addVirtualFieldFrictionPower();
        addVirtualFieldDcPower();
        addVirtualFieldHeaterSetpoint();
    }


    private void addVirtualFieldUsage() {
        final String SID_EVC_TractionBatteryVoltage = "7ec.623203.24";  // unit = V
        final String SID_EVC_TractionBatteryCurrent = "7ec.623204.24";  // unit = A
        final String SID_RealSpeed = "5d7.0";                           // unit = km/h

        // It would be easier use SID_Consumption = "1fd.48" (dash kWh) instead of V*A, but dash is not precise and includes other consumers

        addVirtualFieldCommon ("6100", "kWh/100km", SID_EVC_TractionBatteryVoltage+";"+SID_EVC_TractionBatteryCurrent+";"+SID_RealSpeed, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                // get voltage
                double dcVolt = dependantFields.get(SID_EVC_TractionBatteryVoltage).getValue();
                // get current
                double dcPwr = dcVolt * dependantFields.get(SID_EVC_TractionBatteryCurrent).getValue() / 1000.0;
                // get real speed
                double realSpeed = dependantFields.get(SID_RealSpeed).getValue();

                if (realSpeed >= 5)
                    return -(Math.round(1000.0 * dcPwr / realSpeed) / 10.0);
                else
                    return 0;
            }
        });
    }

    private void addVirtualFieldFrictionTorque() {
        final String SID_DriverBrakeWheel_Torque_Request        = "130.44"; //UBP braking wheel torque the driver wants
        final String SID_ElecBrakeWheelsTorqueApplied           = "1f8.28"; //10ms

        addVirtualFieldCommon ("6101", "Nm", SID_DriverBrakeWheel_Torque_Request+";"+SID_ElecBrakeWheelsTorqueApplied, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String,Field> dependantFields) {

                return dependantFields.get(SID_DriverBrakeWheel_Torque_Request).getValue() - dependantFields.get(SID_ElecBrakeWheelsTorqueApplied).getValue();
            }
        });
    }

    private void addVirtualFieldFrictionPower() {
        final String SID_DriverBrakeWheel_Torque_Request        = "130.44"; //UBP braking wheel torque the driver wants
        final String SID_ElecBrakeWheelsTorqueApplied           = "1f8.28"; //10ms
        final String SID_ElecEngineRPM                          = "1f8.40"; //10ms

        addVirtualFieldCommon ("6102", "kW", SID_DriverBrakeWheel_Torque_Request+";"+SID_ElecBrakeWheelsTorqueApplied+";"+SID_ElecEngineRPM, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String,Field> dependantFields) {

                return (dependantFields.get(SID_DriverBrakeWheel_Torque_Request).getValue() - dependantFields.get(SID_ElecBrakeWheelsTorqueApplied).getValue()) * dependantFields.get(SID_ElecEngineRPM).getValue() / MainActivity.reduction;
            }
        });
    }

    private void addVirtualFieldDcPower() {
        final String SID_TractionBatteryVoltage             = "7ec.623203.24";
        final String SID_TractionBatteryCurrent             = "7ec.623204.24";

        addVirtualFieldCommon ("6103", "kW", SID_TractionBatteryVoltage+";"+SID_TractionBatteryCurrent, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String,Field> dependantFields) {

                return dependantFields.get(SID_TractionBatteryVoltage).getValue() * dependantFields.get(SID_TractionBatteryCurrent).getValue() / 1000;
            }
        });

    }

    private void addVirtualFieldUsageLpf() {
        final String SID_VirtualUsage               = "800.6100.24";

        addVirtualFieldCommon ("6104", "kWh/100km", SID_VirtualUsage, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                double value = dependantFields.get(SID_VirtualUsage).getValue();
                if (value != 0) {
                    runningUsage = runningUsage * 0.95 + value * 0.05;
                }
                return runningUsage;
            }
        });
    }

    private void addVirtualFieldHeaterSetpoint() {
        final String SID_VirtualUsage               = "699.8";

        addVirtualFieldCommon ("6105", "°C", SID_VirtualUsage, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                double value = dependantFields.get(SID_VirtualUsage).getValue();
                if (value == 0) {
                    return Double.NaN;
                } else if (value == 4) {
                    return -10.0;
                } else if (value == 5) {
                    return 40.0;
                }
                return value;
            }
        });
    }

    private void addVirtualFieldCommon (String virtualId, String unit, String dependantIds, VirtualFieldAction virtualFieldAction) {
        // create a list of field this new virtual field will depend on
        HashMap<String, Field> dependantFields = new HashMap<>();
        boolean allOk = true;
        for (String idStr: dependantIds.split(";")){
            Field field = getBySID(idStr);
            if (field != null) {
                dependantFields.put(idStr, field);
            } else {
                allOk = false;
            }
        }
        if (allOk) {
            VirtualField virtualField = new VirtualField(virtualId, dependantFields, unit, virtualFieldAction);
            // add it to the list of fields
            add(virtualField);

        }
    }

    private void fillStatic() {
        String fieldDef = // ID (hex), startBit, endBit, resolution, offset (aplied BEFORE resolution multiplication), decimals, unit, requestID (hex string), responseID (hex string),
                // options (hex, see MainActivity for definitions), optional name, optional list
                ""

                        // 2016-01-27

                        + "130,44,55,-3,4094,0,Nm,,,ff\n" // DriverBrakeWheelTq_Req
                        + "186,16,27,0.5,800,1,Nm,,,ff\n" // MeanEffectiveTorque
                        + "186,40,49,0.125,0,1,%,,,ff\n" // Throttle
                        + "18a,27,38,0.5,800,1,Nm,,,ff\n" // Coasting Torque
                        + "1f8,16,27,1,4096,0,Nm,,,ff\n" // TotalPotentialResistiveWheelsTorque
                        + "1f8,28,39,-1,4096,0,Nm,,,ff\n" // ElecBrakeWheelsTorqueApplied
                        + "1f8,40,50,10,0,0,Rpm,,,ff\n" // ElecEngineRPM
                        + "1fd,0,7,0.390625,0,1,%,,,ff\n" // 12V Battery Current?
                        + "1fd,48,55,1,80,0,kW,,,ff\n" // Consumption
                        + "427,40,47,0.3,0,0,kW,,,e2\n" // Available Charging Power
                        + "427,49,57,0.1,0,1,kWh,,,e2\n" // Available Energy
                        + "42a,30,39,0.1,400,1,°C,,,ff\n" // Evaporator Temp Measure
                        + "42a,48,50,1,0,0,,,,e2\n" // ClimLoopMode
                        + "42e,0,12,0.02,0,2,%,,,e3\n" // State of Charge
                        + "42e,20,24,5,0,0,%,,,e2\n" // Engine Fan Speed
                        + "42e,38,43,1,0,1,A,,,e3\n" // Charging Pilot Current
                        + "42e,44,50,1,40,0,°C,,,e3\n" // HV Battery Temp
                        + "42e,56,63,0.3,0,1,kW,,,ff\n" // Charging Power
                        + "430,24,33,0.5,30,1,°C,,,e2\n" // Comp Temperature Discharge
                        + "430,38,39,1,0,0,,,,e2\n" // HV Battery Cooling State
                        + "430,40,49,0.1,400,1,°C,,,e2\n" // HV Battery Evaporator Temp
                        + "432,36,37,1,0,0,,,,e2\n" // HV Bat Conditionning Mode
                        + "4f8,4,5,1,0,0,,,,ff\n" // Parking brake
                        + "534,32,40,1,40,0,°C,,,e5\n" // Temp out
                        + "5d7,0,15,0.01,0,2,km/h,,,ff\n" // Speed
                        + "5da,0,7,1,40,0,ºC,,,e7\n" // Water temperature
                        + "654,2,2,1,0,0,,,,ff\n" // Charging Plug Connected
                        + "654,25,31,1,0,0,,,,ff\n" // State of Charge
                        + "654,32,41,1,0,0,min,,,ff\n" // Time to Full
                        + "654,42,51,1,0,0,km,,,ff\n" // Available Distance
                        + "656,48,55,1,40,0,°C,,,e2\n" // External Temp
                        + "658,33,39,1,0,0,%,,,ff\n" // Battery Health
                        + "65b,41,43,1,0,0,,,,ff\n" // Charging Status Display
                        + "673,0,0,1,0,0,,,,ff\n" // Speed pressure misadaptation
                        + "673,2,4,1,0,0,,,,ff\n" // Rear right wheel state
                        + "673,5,7,1,0,0,,,,ff\n" // Rear left wheel state
                        + "673,8,10,1,0,0,,,,ff\n" // Front right wheel state
                        + "673,11,13,1,0,0,,,,ff\n" // Front left wheel state
                        + "673,16,23,13.725,0,0,mbar,,,ff\n" // Rear right wheel pressure
                        + "673,24,31,13.725,0,0,mbar,,,ff\n" // Rear left wheel pressure
                        + "673,32,39,13.725,0,0,mbar,,,ff\n" // Front right wheel pressure
                        + "673,40,47,13.725,0,0,mbar,,,ff\n" // Front left wheel pressure
                        + "699,8,15,0.5,0,0,°C,,,e2\n" // Temperature
                        + "760,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "760,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "760,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "760,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "762,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "762,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "762,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "762,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "763,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "763,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "763,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "763,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "764,26,35,0.1,400,0,°C,2121,6121,e2\n" // IH_InCarTemp
                        + "764,8,15,0.4,100,0,°C,2121,6121,5\n" // IH_InCarTemp
                        + "764,36,43,1,0,0,%,2121,6121,e2\n" // IH_RHumidity
                        + "764,16,23,1,0,0,%,2121,6121,5\n" // IH_RHumidity
                        + "764,86,87,1,0,0,,2143,6143,ff\n" // Eco mode requested
                        + "764,110,117,1,40,0,%,2143,6143,ff\n" // IH_ExternalTemp
                      //+ "764,88,92,100,0,0,W,2143,6143,ff\n" // Compressor power
                        + "764,134,142,.1,0,0,bar,2143,6143,ff\n" // IH_ACHighPressureSensor
                        + "764,107,116,10,0,0,rpm,2144,6144,ff\n" // Compressor RPM
                        + "764,40,41,1,0,0,,2167,6167,ff\n" // IH_ExternalTemp
                        + "764,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "764,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "764,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "764,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "765,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "765,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "765,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "765,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "76d,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "76d,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "76d,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "76d,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "76e,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "76e,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "76e,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "76e,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "772,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "772,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "772,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "772,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "77e,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "77e,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "77e,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "77e,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "77e,24,39,0.015625,0,2,ºC,223018,623018,ff\n" // DCDC converter temperature
                        + "77e,24,31,0.015625,0,2,°C,22302b,62302b,ff\n" // inverter temperature
                        + "793,0,0,1,0,0,,1081,5081,ff\n" // start diag
                        + "793,0,0,1,0,0,,10c0,50c0,ff\n" // start diag
                        + "793,0,0,1,0,0,,3e01,7e01,ff\n" // Tester present
                        + "793,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "793,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "793,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "793,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "7b6,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "7b6,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "7b6,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "7b6,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "7bb,336,351,0.01,0,2,kW,2101,6101,e2\n" // Maximum battery input power
                        + "7bb,56,71,10,0,0,°C,2103,6103,5\n" // Mean battery compartment temp
                        + "7bb,192,207,0.01,0,2,%,2103,6103,e2\n" // Real State of Charge
                        + "7bb,32,39,1,40,0,°C,2104,6104,e2\n" // Cell 1 Temperature
                        + "7bb,56,63,1,40,0,°C,2104,6104,e2\n" // Cell 2 Temperature
                        + "7bb,80,87,1,40,0,°C,2104,6104,e2\n" // Cell 3 Temperature
                        + "7bb,104,111,1,40,0,°C,2104,6104,e2\n" // Cell 4 Temperature
                        + "7bb,128,135,1,40,0,°C,2104,6104,e2\n" // Cell 5 Temperature
                        + "7bb,152,159,1,40,0,°C,2104,6104,e2\n" // Cell 6 Temperature
                        + "7bb,176,183,1,40,0,°C,2104,6104,e2\n" // Cell 7 Temperature
                        + "7bb,200,207,1,40,0,°C,2104,6104,e2\n" // Cell 8 Temperature
                        + "7bb,224,231,1,40,0,°C,2104,6104,e2\n" // Cell 9 Temperature
                        + "7bb,248,255,1,40,0,°C,2104,6104,e2\n" // Cell 10 Temperature
                        + "7bb,272,279,1,40,0,°C,2104,6104,e2\n" // Cell 11 Temperature
                        + "7bb,296,303,1,40,0,°C,2104,6104,e2\n" // Cell 12 Temperature
                        + "7bb,600,607,1,40,0,degC,2104,6104,ff\n" // 21_04_#76_Average_Battery_Temperature
                        + "7bb,16,23,1,0,0,,2107,6107,e2\n" // Cell 1 Balancing switch
                        + "7bb,24,31,1,0,0,,2107,6107,e2\n" // Cell 2 Balancing switch
                        + "7bb,32,39,1,0,0,,2107,6107,e2\n" // Cell 3 Balancing switch
                        + "7bb,40,47,1,0,0,,2107,6107,e2\n" // Cell 4 Balancing switch
                        + "7bb,48,55,1,0,0,,2107,6107,e2\n" // Cell 5 Balancing switch
                        + "7bb,56,63,1,0,0,,2107,6107,e2\n" // Cell 6 Balancing switch
                        + "7bb,64,71,1,0,0,,2107,6107,e2\n" // Cell 7 Balancing switch
                        + "7bb,72,79,1,0,0,,2107,6107,e2\n" // Cell 8 Balancing switch
                        + "7bb,80,87,1,0,0,,2107,6107,e2\n" // Cell 9 Balancing switch
                        + "7bb,88,95,1,0,0,,2107,6107,e2\n" // Cell 10 Balancing switch
                        + "7bb,96,102,1,0,0,,2107,6107,e2\n" // Cell 11 Balancing switch
                        + "7bb,104,111,1,0,0,,2107,6107,e2\n" // Cell 12 Balancing switch
                        + "7bb,32,39,1,0,0,°C,2104,6104,105\n" // Cell 1 Balancing switch
                        + "7bb,56,63,1,0,0,°C,2104,6104,105\n" // Cell 2 Balancing switch
                        + "7bb,80,87,1,0,0,°C,2104,6104,105\n" // Cell 3 Balancing switch
                        + "7bb,104,111,1,0,0,°C,2104,6104,105\n" // Cell 4 Balancing switch
                        + "7bb,16,31,0.001,0,3,V,2141,6141,ff\n" // Cell 01 V
                        + "7bb,32,47,0.001,0,3,V,2141,6141,ff\n" // Cell 02 V
                        + "7bb,48,63,0.001,0,3,V,2141,6141,ff\n" // Cell 03 V
                        + "7bb,64,79,0.001,0,3,V,2141,6141,ff\n" // Cell 04 V
                        + "7bb,80,95,0.001,0,3,V,2141,6141,ff\n" // Cell 05 V
                        + "7bb,96,111,0.001,0,3,V,2141,6141,ff\n" // Cell 06 V
                        + "7bb,112,127,0.001,0,3,V,2141,6141,ff\n" // Cell 07 V
                        + "7bb,128,143,0.001,0,3,V,2141,6141,ff\n" // Cell 08 V
                        + "7bb,144,159,0.001,0,3,V,2141,6141,ff\n" // Cell 09 V
                        + "7bb,160,175,0.001,0,3,V,2141,6141,ff\n" // Cell 10 V
                        + "7bb,176,191,0.001,0,3,V,2141,6141,ff\n" // Cell 11 V
                        + "7bb,192,207,0.001,0,3,V,2141,6141,ff\n" // Cell 12 V
                        + "7bb,208,223,0.001,0,3,V,2141,6141,ff\n" // Cell 13 V
                        + "7bb,224,239,0.001,0,3,V,2141,6141,ff\n" // Cell 14 V
                        + "7bb,240,255,0.001,0,3,V,2141,6141,ff\n" // Cell 15 V
                        + "7bb,256,271,0.001,0,3,V,2141,6141,ff\n" // Cell 16 V
                        + "7bb,272,287,0.001,0,3,V,2141,6141,ff\n" // Cell 17 V
                        + "7bb,288,303,0.001,0,3,V,2141,6141,ff\n" // Cell 18 V
                        + "7bb,304,319,0.001,0,3,V,2141,6141,ff\n" // Cell 19 V
                        + "7bb,320,335,0.001,0,3,V,2141,6141,ff\n" // Cell 20 V
                        + "7bb,336,351,0.001,0,3,V,2141,6141,ff\n" // Cell 21 V
                        + "7bb,352,367,0.001,0,3,V,2141,6141,ff\n" // Cell 22 V
                        + "7bb,368,383,0.001,0,3,V,2141,6141,ff\n" // Cell 23 V
                        + "7bb,384,399,0.001,0,3,V,2141,6141,ff\n" // Cell 24 V
                        + "7bb,400,415,0.001,0,3,V,2141,6141,ff\n" // Cell 25 V
                        + "7bb,416,431,0.001,0,3,V,2141,6141,ff\n" // Cell 26 V
                        + "7bb,432,447,0.001,0,3,V,2141,6141,ff\n" // Cell 27 V
                        + "7bb,448,463,0.001,0,3,V,2141,6141,ff\n" // Cell 28 V
                        + "7bb,464,479,0.001,0,3,V,2141,6141,ff\n" // Cell 29 V
                        + "7bb,480,495,0.001,0,3,V,2141,6141,ff\n" // Cell 30 V
                        + "7bb,496,511,0.001,0,3,V,2141,6141,ff\n" // Cell 31 V
                        + "7bb,512,527,0.001,0,3,V,2141,6141,ff\n" // Cell 32 V
                        + "7bb,528,543,0.001,0,3,V,2141,6141,ff\n" // Cell 33 V
                        + "7bb,544,559,0.001,0,3,V,2141,6141,ff\n" // Cell 34 V
                        + "7bb,560,575,0.001,0,3,V,2141,6141,ff\n" // Cell 35 V
                        + "7bb,576,591,0.001,0,3,V,2141,6141,ff\n" // Cell 36 V
                        + "7bb,592,607,0.001,0,3,V,2141,6141,ff\n" // Cell 37 V
                        + "7bb,608,623,0.001,0,3,V,2141,6141,ff\n" // Cell 38 V
                        + "7bb,624,639,0.001,0,3,V,2141,6141,ff\n" // Cell 39 V
                        + "7bb,640,655,0.001,0,3,V,2141,6141,ff\n" // Cell 40 V
                        + "7bb,656,671,0.001,0,3,V,2141,6141,ff\n" // Cell 41 V
                        + "7bb,672,687,0.001,0,3,V,2141,6141,ff\n" // Cell 42 V
                        + "7bb,688,703,0.001,0,3,V,2141,6141,ff\n" // Cell 43 V
                        + "7bb,704,719,0.001,0,3,V,2141,6141,ff\n" // Cell 44 V
                        + "7bb,720,735,0.001,0,3,V,2141,6141,ff\n" // Cell 45 V
                        + "7bb,736,751,0.001,0,3,V,2141,6141,ff\n" // Cell 46 V
                        + "7bb,752,767,0.001,0,3,V,2141,6141,ff\n" // Cell 47 V
                        + "7bb,768,783,0.001,0,3,V,2141,6141,ff\n" // Cell 48 V
                        + "7bb,784,799,0.001,0,3,V,2141,6141,ff\n" // Cell 49 V
                        + "7bb,800,815,0.001,0,3,V,2141,6141,ff\n" // Cell 50 V
                        + "7bb,816,831,0.001,0,3,V,2141,6141,ff\n" // Cell 51 V
                        + "7bb,832,847,0.001,0,3,V,2141,6141,ff\n" // Cell 52 V
                        + "7bb,848,863,0.001,0,3,V,2141,6141,ff\n" // Cell 53 V
                        + "7bb,864,879,0.001,0,3,V,2141,6141,ff\n" // Cell 54 V
                        + "7bb,880,895,0.001,0,3,V,2141,6141,ff\n" // Cell 55 V
                        + "7bb,896,911,0.001,0,3,V,2141,6141,ff\n" // Cell 56 V
                        + "7bb,912,927,0.001,0,3,V,2141,6141,ff\n" // Cell 57 V
                        + "7bb,928,943,0.001,0,3,V,2141,6141,ff\n" // Cell 58 V
                        + "7bb,944,959,0.001,0,3,V,2141,6141,ff\n" // Cell 59 V
                        + "7bb,960,975,0.001,0,3,V,2141,6141,ff\n" // Cell 60 V
                        + "7bb,976,991,0.001,0,3,V,2141,6141,ff\n" // Cell 61 V
                        + "7bb,992,1007,0.001,0,3,V,2141,6141,ff\n" // Cell 62 V
                        + "7bb,16,31,0.001,0,3,V,2142,6142,ff\n" // Cell 63 V
                        + "7bb,32,47,0.001,0,3,V,2142,6142,ff\n" // Cell 64 Vl
                        + "7bb,48,63,0.001,0,3,V,2142,6142,ff\n" // Cell 65 V
                        + "7bb,64,79,0.001,0,3,V,2142,6142,ff\n" // Cell 66 V
                        + "7bb,80,95,0.001,0,3,V,2142,6142,ff\n" // Cell 67 V
                        + "7bb,96,111,0.001,0,3,V,2142,6142,ff\n" // Cell 68 V
                        + "7bb,112,127,0.001,0,3,V,2142,6142,ff\n" // Cell 69 V
                        + "7bb,128,143,0.001,0,3,V,2142,6142,ff\n" // Cell 70 V
                        + "7bb,144,159,0.001,0,3,V,2142,6142,ff\n" // Cell 71 V
                        + "7bb,160,175,0.001,0,3,V,2142,6142,ff\n" // Cell 72 V
                        + "7bb,176,191,0.001,0,3,V,2142,6142,ff\n" // Cell 73 V
                        + "7bb,192,207,0.001,0,3,V,2142,6142,ff\n" // Cell 74 V
                        + "7bb,208,223,0.001,0,3,V,2142,6142,ff\n" // Cell 75 V
                        + "7bb,224,239,0.001,0,3,V,2142,6142,ff\n" // Cell 76 V
                        + "7bb,240,255,0.001,0,3,V,2142,6142,ff\n" // Cell 77 V
                        + "7bb,256,271,0.001,0,3,V,2142,6142,ff\n" // Cell 78 V
                        + "7bb,272,287,0.001,0,3,V,2142,6142,ff\n" // Cell 79 V
                        + "7bb,288,303,0.001,0,3,V,2142,6142,ff\n" // Cell 80 V
                        + "7bb,304,319,0.001,0,3,V,2142,6142,ff\n" // Cell 81 V
                        + "7bb,320,335,0.001,0,3,V,2142,6142,ff\n" // Cell 82 V
                        + "7bb,336,351,0.001,0,3,V,2142,6142,ff\n" // Cell 83 V
                        + "7bb,352,367,0.001,0,3,V,2142,6142,ff\n" // Cell 84 V
                        + "7bb,368,383,0.001,0,3,V,2142,6142,ff\n" // Cell 85 V
                        + "7bb,384,399,0.001,0,3,V,2142,6142,ff\n" // Cell 86 V
                        + "7bb,400,415,0.001,0,3,V,2142,6142,ff\n" // Cell 87 V
                        + "7bb,416,431,0.001,0,3,V,2142,6142,ff\n" // Cell 88 V
                        + "7bb,432,447,0.001,0,3,V,2142,6142,ff\n" // Cell 89 V
                        + "7bb,448,463,0.001,0,3,V,2142,6142,ff\n" // Cell 90 V
                        + "7bb,464,479,0.001,0,3,V,2142,6142,ff\n" // Cell 91 V
                        + "7bb,480,495,0.001,0,3,V,2142,6142,ff\n" // Cell 92 V
                        + "7bb,496,511,0.001,0,3,V,2142,6142,ff\n" // Cell 93 V
                        + "7bb,512,527,0.001,0,3,V,2142,6142,ff\n" // Cell 94 V
                        + "7bb,528,543,0.001,0,3,V,2142,6142,ff\n" // Cell 95 V
                        + "7bb,544,559,0.001,0,3,V,2142,6142,ff\n" // Cell 96 V
                        + "7bb,96,119,1,0,0,km,2161,6161,ff\n" // Battery mileage in km
                        + "7bb,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "7bb,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "7bb,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "7bb,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "7bc,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "7bc,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "7bc,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "7bc,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "7da,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "7da,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "7da,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "7da,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "7ec,56,63,1,0,0,,2180,6180,ff\n" // diagVersion
                        + "7ec,64,87,1,0,0,,2180,6180,2ff\n" // Supplier (string!)
                        + "7ec,128,143,1,0,0,,2180,6180,ff\n" // Soft
                        + "7ec,144,159,1,0,0,,2180,6180,ff\n" // Version
                        + "7ec,24,39,2,0,2,%,222002,622002,e2\n" // SOC
                        + "7ec,24,39,2.083333333,0,2,%,222002,622002,e5\n" // SOC
                        + "7ec,24,39,0.01,0,2,V,222005,622005,ff\n" // 12V battery voltage
                        + "7ec,24,47,1,0,0,km,222006,622006,ff\n" // Odometer
                        + "7ec,24,31,0.5,0,1,A,223028,623028,ff\n" // 14V current?
                        + "7ec,24,39,0.5,0,2,V,223203,623203,ff\n" // HV Battery voltage
                        + "7ec,24,39,0.25,32768,2,A,223204,623204,ff\n" // HV Battery current
                        + "7ec,24,31,1,0,0,%,223206,623206,ff\n" // Battery health in %
                        + "7ec,24,31,1,1,0,,223318,623318,ff\n" // Motor Water pump speed
                        + "7ec,24,31,1,1,0,,223319,623319,ff\n" // Charger pump speed
                        + "7ec,24,31,1,1,0,,22331a,62331a,ff\n" // Heater water pump speed
                        + "7ec,24,47,0.001,1,0,kWh,2233dc,6233dc,ff\n" // Consumed domestic energy
                        +"7ec,240,263,1,0,0,km,2233d4,6233d4,ff\n" //
                        +"7ec,216,239,1,0,0,km,2233d4,6233d4,ff\n" //
                        +"7ec,192,215,1,0,0,km,2233d4,6233d4,ff\n" //
                        +"7ec,168,191,1,0,0,km,2233d4,6233d4,ff\n" //
                        +"7ec,144,167,1,0,0,km,2233d4,6233d4,ff\n" //
                        +"7ec,120,143,1,0,0,km,2233d4,6233d4,ff\n" //
                        +"7ec,96,119,1,0,0,km,2233d4,6233d4,ff\n" //
                        +"7ec,72,95,1,0,0,km,2233d4,6233d4,ff\n" //
                        +"7ec,48,71,1,0,0,km,2233d4,6233d4,ff\n" //
                        +"7ec,24,47,1,0,0,km,2233d4,6233d4,ff\n" //
                        +"7ec,96,103,1,0,0,,2233d5,6233d5,ff,\n" //
                        +"7ec,88,95,1,0,0,,2233d5,6233d5,ff\n" //
                        +"7ec,80,87,1,0,0,,2233d5,6233d5,ff\n" //
                        +"7ec,72,79,1,0,0,,2233d5,6233d5,ff\n" //
                        +"7ec,64,71,1,0,0,,2233d5,6233d5,ff\n" //
                        +"7ec,56,63,1,0,0,,2233d5,6233d5,ff\n" //
                        +"7ec,48,55,1,0,0,,2233d5,6233d5,ff\n" //
                        +"7ec,40,47,1,0,0,,2233d5,6233d5,ff\n" //
                        +"7ec,32,39,1,0,0,,2233d5,6233d5,ff\n" //
                        +"7ec,24,31,1,0,0,,2233d5,6233d5,ff\n" //
                        +"7ec,96,103,1,0,0,,2233d6,6233d6,ff\n" //
                        +"7ec,88,95,1,0,0,,2233d6,6233d6,ff\n" //
                        +"7ec,80,87,1,0,0,,2233d6,6233d6,ff\n" //
                        +"7ec,72,79,1,0,0,,2233d6,6233d6,ff\n" //
                        +"7ec,64,71,1,0,0,,2233d6,6233d6,ff\n" //
                        +"7ec,56,63,1,0,0,,2233d6,6233d6,ff\n" //
                        +"7ec,48,55,1,0,0,,2233d6,6233d6,ff\n" //
                        +"7ec,40,47,1,0,0,,2233d6,6233d6,ff\n" //
                        +"7ec,32,39,1,0,0,,2233d6,6233d6,ff\n" //
                        +"7ec,24,31,1,0,0,,2233d6,6233d6,ff\n" //
                        +"7ec,168,183,.2,0,0,%,2233d7,6233d7,ff\n" //
                        +"7ec,152,167,.2,0,0,%,2233d7,6233d7,ff\n" //
                        +"7ec,136,151,.2,0,0,%,2233d7,6233d7,ff\n" //
                        +"7ec,120,135,.2,0,0,%,2233d7,6233d7,ff\n" //
                        +"7ec,104,119,.2,0,0,%,2233d7,6233d7,ff\n" //
                        +"7ec,88,103,.2,0,0,%,2233d7,6233d7,ff\n" //
                        +"7ec,72,87,.2,0,0,%,2233d7,6233d7,ff\n" //
                        +"7ec,56,71,.2,0,0,%,2233d7,6233d7,ff\n" //
                        +"7ec,40,55,.2,0,0,%,2233d7,6233d7,ff\n" //
                        +"7ec,24,39,.2,0,0,%,2233d7,6233d7,ff\n" //
                        +"7ec,96,103,1,40,0,°C,2233d8,6233d8,ff\n" //
                        +"7ec,88,95,1,40,0,°C,2233d8,6233d8,ff\n" //
                        +"7ec,80,87,1,40,0,°C,2233d8,6233d8,ff\n" //
                        +"7ec,72,79,1,40,0,°C,2233d8,6233d8,ff\n" //
                        +"7ec,64,71,1,40,0,°C,2233d8,6233d8,ff\n" //
                        +"7ec,56,63,1,40,0,°C,2233d8,6233d8,ff\n" //
                        +"7ec,48,55,1,40,0,°C,2233d8,6233d8,ff\n" //
                        +"7ec,40,47,1,40,0,°C,2233d8,6233d8,ff\n" //
                        +"7ec,32,39,1,40,0,°C,2233d8,6233d8,ff\n" //
                        +"7ec,24,31,1,40,0,°C,2233d8,6233d8,ff\n" //
                        +"7ec,168,183,1,0,0,min,2233d9,6233d9,ff\n" //
                        +"7ec,152,167,1,0,0,min,2233d9,6233d9,ff\n" //
                        +"7ec,136,151,1,0,0,min,2233d9,6233d9,ff\n" //
                        +"7ec,120,135,1,0,0,min,2233d9,6233d9,ff\n" //
                        +"7ec,104,119,1,0,0,min,2233d9,6233d9,ff\n" //
                        +"7ec,88,103,1,0,0,min,2233d9,6233d9,ff\n" //
                        +"7ec,72,87,1,0,0,min,2233d9,6233d9,ff\n" //
                        +"7ec,56,71,1,0,0,min,2233d9,6233d9,ff\n" //
                        +"7ec,40,55,1,0,0,min,2233d9,6233d9,ff\n" //
                        +"7ec,24,39,1,0,0,min,2233d9,6233d9,ff\n" //

                ;

        fillDynamic (fieldDef);
    }

    private void fillDynamic(String fieldDef)
    {

//        try {
//            //fieldDef += readFromLocalFile();
//        }
//        catch(Exception e)
//        {
//            // ignore
//        }

        String[] lines = fieldDef.split("\n");
        for (String line : lines) {
            //MainActivity.debug("Fields: Reading > "+line);
            //Get all tokens available in line
            String[] tokens = line.split(",");
            if (tokens.length >= 10) {
                int frameId = Integer.parseInt(tokens[FIELD_ID].trim(), 16);
                Frame frame = Frames.getInstance().getById(frameId);
                if (frame == null) {
                    MainActivity.debug("frame does not exist:" + tokens[FIELD_ID].trim());
                } else {
                    short options = Short.parseShort(tokens[FIELD_OPTIONS].trim(), 16);
                    // ensure this field matches the selected car
                    if ((options & MainActivity.car) != 0) {
                        //Create a new field object and fill his  data
                        MainActivity.debug(tokens[FIELD_ID] + "." + tokens[FIELD_RESPONSE_ID] + "." + tokens[FIELD_FROM]);
                        Field field = new Field(
                                frame,
                                Short.parseShort(tokens[FIELD_FROM].trim()),
                                Short.parseShort(tokens[FIELD_TO].trim()),
                                Double.parseDouble(tokens[FIELD_RESOLUTION].trim()),
                                Integer.parseInt(tokens[FIELD_DECIMALS].trim()),
                                Integer.parseInt(tokens[FIELD_OFFSET].trim()),
                                tokens[FIELD_UNIT].trim(),
                                //tokens[FIELD_REQUEST_ID].trim(),
                                tokens[FIELD_RESPONSE_ID].trim(),
                                options,
                                tokens.length >= 11 ? tokens[FIELD_NAME] : null,
                                tokens.length >= 12 ? tokens[FIELD_LIST] : null
                        );

                        // we are maintaining a list of all fields in a frame so we can very
                        // quickly update all fields when a message (=frame data) comes in
                        // note that for free frames a frame is identified by it's ID and itś definition
                        // is entirely given
                        // for an ISOTP frame (diagnostics) frame, the frame is just a skeleton and
                        // the definition is entirely dependant on the responseID. Therefor, when an
                        // ISOTP field is defined, new frames are created dynamically
                        if (field.isIsoTp()) {
                            Frame subFrame = Frames.getInstance().getById(frameId, field.getResponseId());
                            if (subFrame == null) {
                                subFrame = new Frame(frame.getId(),frame.getInterval(),frame.getSendingEcu(),field.getResponseId(),frame);
                                Frames.getInstance().add (subFrame);
                            }
                            subFrame.addField(field);
                            field.setFrame(subFrame);
                        } else {
                            frame.addField(field);
                        }

                        // add the field to the list of available fields
                        add(field);
                    }
                }
            }
        }
    }

    private String readFromLocalFile()
    {
        //*Don't* hardcode "/sdcard"
        File sdcard = Environment.getExternalStorageDirectory();
        MainActivity.debug("SD: "+sdcard.getAbsolutePath());

        //Get the text file
        File file = new File(sdcard,"fields.csv");

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }
    /*
        private void readFromFile(String filename) throws IOException // FileNotFoundException,
        {
            BufferedReader fileReader = new BufferedReader(new FileReader(filename));
            String line;
            //Read the file line by line starting from the second line
            while ((line = fileReader.readLine()) != null)
            {
                //Get all tokens available in line
                String[] tokens = line.split(",");
                if (tokens.length == 15)
                {
                    int divider = Integer.parseInt(tokens[FIELD_DIVIDER].trim());
                    int multiplier = Integer.parseInt(tokens[FIELD_MULTIPLIER].trim());
                    int decimals = Integer.parseInt(tokens[FIELD_DECIMALS].trim());
                    double multi = ((double) multiplier/divider)/(decimals==0?1:decimals); // <<<<<< Probably wrong, as decimals have completely changed, and that should be in file too
                    int frameId = Integer.parseInt(tokens[FIELD_ID].trim().replace("0x", ""), 16);

                    Frame frame = Frames.getInstance().getById(frameId);
                    Field field = new Field(
                            frame,
                            Integer.parseInt(tokens[FIELD_FROM].trim()),
                            Integer.parseInt(tokens[FIELD_TO].trim()),
                            multi,
                            Integer.parseInt(tokens[FIELD_DECIMALS].trim()),
                            (
                                    tokens[FIELD_OFFSET].trim().contains("0x")
                                            ?
                                            Integer.parseInt(tokens[FIELD_OFFSET].trim().replace("0x", ""), 16)
                                            :
                                            Integer.parseInt(tokens[FIELD_OFFSET].trim())
                            ),
                            tokens[FIELD_UNIT].intern(),
                            tokens[FIELD_REQUEST_ID].trim().replace("0x", "").intern(),
                            tokens[FIELD_RESPONSE_ID].trim().replace("0x", "").intern(),
                            Integer.parseInt(tokens[FIELD_CAR].trim())
                            //Integer.parseInt(tokens[FIELD_SKIPS].trim())
                            //Integer.parseInt(tokens[FIELD_FREQ].trim())
                    );
                    // add the field to the list of available fields
                    add(field);
                }
            }
        }
    */
    public Field getBySID(String sid) {
        sid=sid.toLowerCase();

/*
        // first let's try to get the field that is bound to the selected car
        Field tryField = fieldsBySid.get(MainActivity.car + "."+sid);
        if(tryField!=null) return tryField;

        // if none is found, try the other one, starting with 0 = CAR_ANY
        for(int i=0; i<5; i++) {
            tryField = fieldsBySid.get(i + "." + sid);
            if (tryField != null) return tryField;
        }
*/
        // since we changed logic to initialize the hashmaps with only the current car's fields, we're always fine just looking for the SID
        Field tryField = fieldsBySid.get(sid);
        if(tryField!=null) return tryField;


        return null;
    }

    public int size() {
        return fields.size();
    }

    public Field get(int index) {
        return fields.get(index);
    }

    public Object[] toArray() {
        return fields.toArray();
    }


    public void add(Field field) {
        fields.add(field);
        fieldsBySid.put(field.getSID(),field);
        //fieldsBySid.put(field.getCar()+"."+field.getSID(),field);
    }

    /*
    // this should never be done :-)
    public void notifyAllFieldListeners()
    {
        for(int i=0; i< fields.size(); i++) {
            fields.get(i).notifyFieldListeners();
        }
    }
    */

    public void clearAllFields()
    {
        for(int i=0; i< fields.size(); i++) {
            fields.get(i).setValue(0);
        }
    }

    public ArrayList<Field> getAllFields () {
        return fields;
    }

    public void load ()
    {
        fields.clear();
        fieldsBySid.clear();
        fillStatic();
        addVirtualFields();
        MainActivity.getInstance().registerApplicationFields(); // this registers i.e. speed for save driving mode
    }

    public void load (String initString)
    {
        fields.clear();
        fieldsBySid.clear();
        fillDynamic(initString);
        //addVirtualFields();
        MainActivity.getInstance().registerApplicationFields(); // this registers i.e. speed for save driving mode
    }

    public void loadMore (String initString)
    {
        fillDynamic(initString);
    }

    /* --------------------------------
     * Tests ...
     \ ------------------------------ */

    public static void main(String[] args)
    {

    }

}
