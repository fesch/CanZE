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
import android.widget.TextView;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.classes.FieldLogger;
import lu.fisch.canze.interfaces.MessageListener;
import lu.fisch.canze.interfaces.VirtualFieldAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author robertfisch test
 */
public class Fields implements MessageListener {

    private static final int FIELD_ID           = 0;
    private static final int FIELD_FROM         = 1;
    private static final int FIELD_TO           = 2;
    private static final int FIELD_RESOLUTION   = 3;
    private static final int FIELD_OFFSET       = 4;
    private static final int FIELD_DECIMALS     = 5;
    private static final int FIELD_UNIT         = 6;
    private static final int FIELD_REQUEST_ID   = 7;
    private static final int FIELD_RESPONSE_ID  = 8;
    private static final int FIELD_OPTIONS      = 9; // to be stated in HEX, no leading 0x

    public static final int TOAST_NONE          = 0;
    public static final int TOAST_DEVICE        = 1;
    public static final int TOAST_ALL           = 2;

    private final ArrayList<Field> fields = new ArrayList<>();
    private final HashMap<String, Field> fieldsBySid = new HashMap<>();

    private static Fields instance = null;

    //private int car = CAR_ANY;

    private Fields() {
        fillStatic();
        addVirtualFields();
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
        addVirtualFieldFrictionTorque();
        addVirtualFieldFrictionPower();
        addVirtualFieldDcPower();
    }


    private void addVirtualFieldUsage() {

        // It would be easier use SID_Consumption = "1fd.48" (dash kWh) instead of V*A

        // create a list of field this new virtual field will depend on
        HashMap<String, Field> dependantFields = new HashMap<>();
        final String SID_EVC_TractionBatteryVoltage = "7ec.623203.24";  // unit = V
        final String SID_EVC_TractionBatteryCurrent = "7ec.623204.24";  // unit = A
        final String SID_RealSpeed = "5d7.0";                           // unit = km/h
        dependantFields.put(SID_EVC_TractionBatteryVoltage, getBySID(SID_EVC_TractionBatteryVoltage));
        dependantFields.put(SID_EVC_TractionBatteryCurrent, getBySID(SID_EVC_TractionBatteryCurrent));
        dependantFields.put(SID_RealSpeed, getBySID(SID_RealSpeed));
        // create a new virtual field. Define it's ID and how it is being calculated
        VirtualField virtualField = new VirtualField("6100", dependantFields, "kWh/100km", new VirtualFieldAction() {
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
        // add it to the list of fields
        add(virtualField);
    }


    private void addVirtualFieldFrictionTorque() {
        // create a list of field this new virtual field will depend on
        HashMap<String, Field> dependantFields = new HashMap<>();
        final String SID_DriverBrakeWheel_Torque_Request        = "130.44"; //UBP braking wheel torque the driver wants
        final String SID_ElecBrakeWheelsTorqueApplied           = "1f8.28"; //10ms
        dependantFields.put(SID_DriverBrakeWheel_Torque_Request,getBySID(SID_DriverBrakeWheel_Torque_Request));
        dependantFields.put(SID_ElecBrakeWheelsTorqueApplied,getBySID(SID_ElecBrakeWheelsTorqueApplied));
        // create a new virtual field. Define it's ID and how it is being calculated
        VirtualField virtualField = new VirtualField("6101", dependantFields, "Nm", new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String,Field> dependantFields) {

                return dependantFields.get(SID_DriverBrakeWheel_Torque_Request).getValue() - dependantFields.get(SID_ElecBrakeWheelsTorqueApplied).getValue();
            }
        });
        // add it to the list of fields
        add(virtualField);
    }

    private void addVirtualFieldFrictionPower() {
        // create a list of field this new virtual field will depend on
        HashMap<String, Field> dependantFields = new HashMap<>();
        final String SID_DriverBrakeWheel_Torque_Request        = "130.44"; //UBP braking wheel torque the driver wants
        final String SID_ElecBrakeWheelsTorqueApplied           = "1f8.28"; //10ms
        final String SID_ElecEngineRPM                          = "1f8.40"; //10ms

        dependantFields.put(SID_DriverBrakeWheel_Torque_Request,getBySID(SID_DriverBrakeWheel_Torque_Request));
        dependantFields.put(SID_ElecBrakeWheelsTorqueApplied,getBySID(SID_ElecBrakeWheelsTorqueApplied));
        dependantFields.put(SID_ElecEngineRPM,getBySID(SID_ElecEngineRPM));
        // create a new virtual field. Define it's ID and how it is being calculated
        VirtualField virtualField = new VirtualField("6102", dependantFields, "kW", new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String,Field> dependantFields) {

                return (dependantFields.get(SID_DriverBrakeWheel_Torque_Request).getValue() - dependantFields.get(SID_ElecBrakeWheelsTorqueApplied).getValue()) * dependantFields.get(SID_ElecEngineRPM).getValue() / 9.3;
            }
        });
        // add it to the list of fields
        add(virtualField);
    }

    private void addVirtualFieldDcPower() {
        // create a list of field this new virtual field will depend on
        HashMap<String, Field> dependantFields = new HashMap<>();
        final String SID_TractionBatteryVoltage             = "7ec.623203.24";
        final String SID_TractionBatteryCurrent             = "7ec.623204.24";
        dependantFields.put(SID_TractionBatteryVoltage,getBySID(SID_TractionBatteryVoltage));
        dependantFields.put(SID_TractionBatteryCurrent,getBySID(SID_TractionBatteryCurrent));
        // create a new virtual field. Define it's ID and how it is being calculated
        VirtualField virtualField = new VirtualField("6103", dependantFields, "kW", new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String,Field> dependantFields) {

                return dependantFields.get(SID_TractionBatteryVoltage).getValue() * dependantFields.get(SID_TractionBatteryCurrent).getValue() / 1000;
            }
        });
        // add it to the list of fields
        add(virtualField);
    }

    private void fillStatic()
    {
        String fieldDef = // ID, startBit, endBit, resolution, offset, decimals, unit, requestID, responseID, options is HEX
                ""
/*
                        // 2015.11.21

                        +"0x0c6,0,15,1,0x8000,1,°,,,0\n" // Steering Position
                        +"0x0c6,16,31,1,0x8000,1,°/s,,,0\n" // Steering Acceleration
                        +"0x0c6,32,47,1,0x8000,1,°,,,0\n" // SteeringWheelAngle_Offset
                        +"0x0c6,48,50,1,0,0,,,,0\n" // SwaSensorInternalStatus
                        +"0x0c6,51,54,1,0,0,,,,0\n" // SwaClock
                        +"0x0c6,56,53,1,0,0,,,,0\n" // SwaChecksum
                        +"0x12e,0,7,1,198,0,,,,0\n" // LongitudinalAccelerationProc
                        +"0x12e,8,23,1,0x8000,0,,,,0\n" // TransversalAcceleration
                        +"0x12e,24,35,0.1,2047,1,deg/s,,,0\n" // Yaw rate
                        +"0x130,8,10,1,0,0,,,,0\n" // UBP_Clock
                        +"0x130,11,12,1,0,0,,,,0\n" // HBB_Malfunction
                        +"0x130,16,17,1,0,0,,,,0\n" // EB_Malfunction
                        +"0x130,18,19,1,0,0,,,,0\n" // EB_inProgress
                        +"0x130,20,31,1,4094,0,Nm,,,0\n" // ElecBrakeWheelsTorqueRequest
                        +"0x130,32,38,1,0,0,%,,,0\n" // BrakePedalDriverWill
                        +"0x130,40,41,1,0,0,,,,0\n" // HBA_ActivationRequest
                        +"0x130,42,43,1,0,0,,,,0\n" // PressureBuildUp
                        +"0x130,44,55,-3,4094,0,Nm,,,0\n" // DriverBrakeWheelTq_Req
                        +"0x130,56,63,1,0,0,,,,0\n" // CheckSum_UBP
                        +"0x17e,40,41,1,0,0,,,,0\n" // CrankingAuthorisation_AT
                        +"0x17e,48,51,1,0,0,,,,0\n" // GearLeverPosition
                        +"0x186,0,15,0.125,0,2,rpm,,,0\n" // Speed
                        +"0x186,16,27,0.5,800,1,Nm,,,0\n" // MeanEffectiveTorque
                        +"0x186,28,39,0.5,800,0,Nm,,,0\n" // RequestedTorqueAfterProc
                        +"0x186,40,49,0.1,0,1,%,,,0\n" // Throttle
                        +"0x18a,0,11,1,0,0,%,,,0\n" // RawEngineTorque_WithoutTMReq
                        +"0x18a,12,12,1,0,0,,,,0\n" // AT_TorqueAcknowledgement
                        +"0x18a,13,14,1,0,0,,,,0\n" // CruiseControlStatus_forTM
                        +"0x18a,16,25,0.125,0,2,%,,,0\n" // Throttle
                        +"0x18a,26,26,1,0,0,,,,0\n" // KickDownActivated
                        +"0x18a,27,38,0.5,800,1,Nm,,,0\n" // FrictionTorque
                        +"0x1f8,0,7,1,0,0,,,,0\n" // Checksum EVC
                        +"0x1f8,12,13,1,0,0,,,,0\n" // EVCReadyAsActuator
                        +"0x1f8,16,27,1,4096,0,Nm,,,0\n" // TotalPotentialResistiveWheelsTorque
                        +"0x1f8,28,39,-1,4096,0,Nm,,,0\n" // ElecBrakeWheelsTorqueApplied
                        +"0x1f8,40,50,10,0,0,Rpm,,,0\n" // ElecEngineRPM
                        +"0x1f8,52,54,1,0,0,,,,0\n" // EVC_Clock
                        +"0x1f8,56,58,1,0,0,,,,0\n" // GearRangeEngagedCurrent
                        +"0x1f8,62,63,1,0,0,,,,0\n" // DeclutchInProgress
                        +"0x1f6,20,20,0.5,0,1,,,,0\n" // Break Pedal
                        +"0x1fd,0,7,0.5,0,1,A,,,0\n" // 12V Battery Current
                        +"0x1fd,48,55,1,0x50,0,kW,,,0\n" // Consumption
                        +"0x29a,0,15,1,0,0,,,,0\n" // Speed Front Right
                        +"0x29a,16,31,1,0,0,,,,0\n" // Speed Front Left
                        +"0x29a,32,47,0.01,0,2,,,,0\n" //
                        +"0x29c,0,15,1,0,0,,,,0\n" // Speed Rear Right
                        +"0x29c,16,31,1,0,0,,,,0\n" // Speed Rear Left
                        +"0x29c,48,63,0.01,0,2,,,,0\n" //
                        +"0x352,0,1,1,0,0,,,,0\n" // ABS Warning Request
                        +"0x352,2,3,1,0,0,,,,0\n" // ESP_StopLampRequest
                        +"0x352,24,31,1,0,0,,,,0\n" // Break pressure
                        +"0x35c,4,15,1,0,0,,,,0\n" // Key-Start
                        +"0x35c,16,39,1,0,0,min,,,0\n" // AbsoluteTimeSince1rstIgnition
                        +"0x3f7,2,3,1,0,0,,,,2\n" // Gear?
                        +"0x427,0,1,1,0,0,,,,0\n" // HVConnectionStatus
                        +"0x427,2,3,1,0,0,,,,0\n" // ChargingAlert
                        +"0x427,4,5,1,0,0,,,,0\n" // HVBatteryLocked
                        +"0x427,26,28,1,0,0,,,,0\n" // PreHeatingProgress
                        +"0x427,40,47,0.3,0,0,kW,,,2\n" // AvailableChargingPower
                        +"0x427,49,57,0.1,0,1,kWh,,,0\n" // AvailableEnergy
                        +"0x427,58,58,1,0,0,,,,0\n" // ChargeAvailable
                        +"0x42a,0,0,1,0,0,,,,0\n" // PreHeatingRequest
                        +"0x42a,6,15,0.1,40,1,°C,,,0\n" // EvaporatorTempSetPoint
                        +"0x42a,24,29,1,0,0,%,,,0\n" // ClimAirFlow
                        +"0x42a,30,39,0.1,40,1,°C,,,0\n" // EvaporatorTempMeasure
                        +"0x42a,45,46,1,0,0,,,,0\n" // ImmediatePreheatingAuthorizationStatus
                        +"0x42a,48,49,1,0,0,,,,0\n" // ClimLoopMode
                        +"0x42a,51,52,1,0,0,,,,0\n" // PTCActivationRequest
                        +"0x42a,56,60,5,0,0,%,,,0\n" // EngineFanSpeedRequestPWM
                        +"0x42e,0,12,0.02,0,2,%,,,0\n" // State of Charge
                        +"0x42e,18,19,1,0,0,,,,0\n" // HVBatLevel2Failure
                        +"0x42e,20,24,5,0,0,%,,,0\n" // EngineFanSpeed
                        +"0x42e,25,34,0.5,0,0,V,,,0\n" // HVNetworkVoltage
                        +"0x42e,38,43,1,0,1,A,,,0\n" // Charging Pilot Current
                        +"0x42e,44,50,1,40,0,°C,,,0\n" // HVBatteryTemp
                        +"0x42e,56,63,0.3,0,1,kW,,,0\n" // ChargingPower
                        +"0x430,40,49,0.1,40,1,°C,,,2\n" // HV Battery Evaporator Temp
                        +"0x430,50,59,0.1,40,1,°C,,,2\n" // HV Battery Evaporator Setpoint
                        +"0x4f8,0,1,-1,-2,0,,,,0\n" // Start
                        +"0x4f8,4,5,-1,-2,0,,,,0\n" // Parking Break
                        +"0x4f8,24,39,0.01,0,2,,,,2\n" // Speed on Display
                        +"0x534,32,40,1,40,0,°C,,,1\n" // Temp out
                        +"0x5d7,0,15,0.01,0,2,km/h,,,0\n" // Speed
                        +"0x5d7,16,43,0.01,0,2,km,,,0\n" // Odometer
                        +"0x5d7,44,45,1,0,0,?,,,0\n" // WheelsLockingState
                        +"0x5d7,48,49,1,0,0,?,,,0\n" // VehicleSpeedSign
                        +"0x5d7,50,54,0.04,0,2,cm,,,0\n" // Fine distance
                        +"0x5da,0,7,1,40,0,ºC,,,1\n" // Water temperature
                        +"0x5de,1,1,1,0,0,,,,0\n" // Right Indicator
                        +"0x5de,2,2,1,0,0,,,,0\n" // Left Indicator
                        +"0x5de,3,3,1,0,0,,,,0\n" // Rear Fog Light
                        +"0x5de,5,5,1,0,0,,,,0\n" // Park Light
                        +"0x5de,6,6,1,0,0,,,,0\n" // Head Light
                        +"0x5de,7,7,1,0,0,,,,0\n" // Beam Light
                        +"0x5de,8,9,1,0,0,,,,0\n" // PositionLightsOmissionWarning
                        +"0x5de,10,10,1,0,0,,,,0\n" // ALS malfunction
                        +"0x5de,11,12,1,0,0,,,,0\n" // Door Front Left
                        +"0x5de,13,14,1,0,0,,,,0\n" // Dort Front Right
                        +"0x5de,16,17,1,0,0,,,,0\n" // Door Rear Left
                        +"0x5de,18,19,1,0,0,,,,0\n" // Door Rear Right
                        +"0x5de,21,22,1,0,0,,,,\n" // Steering Lock Failure
                        +"0x5de,23,23,1,0,0,,,,\n" // Unlocking Steering Column Warning
                        +"0x5de,24,24,1,0,0,,,,\n" // Automatic Lock Up Activation State
                        +"0x5de,25,25,1,0,0,,,,\n" // Badge Battery Low
                        +"0x5de,28,29,1,0,0,,,,\n" // Trip Display Scrolling Request
                        +"0x5de,32,35,1,0,0,,,,\n" // Smart Keyless Information Display
                        +"0x5de,36,36,1,0,0,,,,\n" // Keyless Info Reemission Request
                        +"0x5de,37,37,1,0,0,,,,\n" // Keyless Card Reader Failure Display
                        +"0x5de,47,47,1,0,0,,,,\n" // Brake Switch Fault Display
                        +"0x5de,49,49,1,0,0,,,,\n" // Stop Lamp Failure Display
                        +"0x5de,56,57,1,0,0,,,,\n" // Rear Wiper Status
                        +"0x5de,58,59,1,0,0,,,,0\n" // Boot Open Warning
                        +"0x5ee,0,0,1,0,0,,,,0\n" // Park Light
                        +"0x5ee,1,1,1,0,0,,,,0\n" // Head Light
                        +"0x5ee,2,2,1,0,0,,,,0\n" // Beam Light
                        +"0x5ee,16,19,1,0,0,,,,0\n" // Door Locks
                        +"0x5ee,20,24,1,0,0,,,,0\n" // Indicators
                        +"0x5ee,24,27,1,0,0,,,,0\n" // Doors
                        +"0x646,8,15,0.1,0,1,kWh/100km,,,0\n" // Average trip B consumpion
                        +"0x646,16,32,0.1,0,1,km,,,0\n" // Trip B distance
                        +"0x646,33,47,0.1,0,1,kWh,,,0\n" // trip B consumption
                        +"0x646,48,59,0.1,0,1,km/h,,,0\n" // Averahe trip B speed
                        +"0x653,9,9,1,0,0,,,,0\n" // Driver seatbelt
                        +"0x654,2,2,1,0,0,,,,0\n" // ChargingPlugConnected
                        +"0x654,3,3,1,0,0,,,,0\n" // DriverWalkAwayEngineON
                        +"0x654,4,4,1,0,0,,,,0\n" // HVBatteryUnballastAlert
                        +"0x654,25,31,1,0,0,,,,0\n" // State of Charge
                        +"0x654,32,41,1,0,0,min,,,0\n" // Time to Full
                        +"0x654,42,51,1,0,0,km,,,0\n" // Available Distance
                        +"0x654,52,61,0.1,0,1,,,,0\n" // AverageConsumption
                        +"0x654,62,62,1,0,0,,,,0\n" // HVBatteryLow
                        +"0x656,3,3,1,0,0,,,,0\n" // Trip Data Reset
                        +"0x656,21,31,1,0,0,min,,,0\n" // Cluste rScheduled Time
                        +"0x656,32,42,1,0,0,min,,,0\n" // Cluster Scheduled Time 2
                        +"0x656,48,55,1,40,0,°C,,,2\n" // External Temp
                        +"0x656,56,57,1,0,0,,,,2\n" // ClimPCCustomerActiv
                        +"0x658,0,31,1,0,0,,,,0\n" // Battery Serial N°
                        +"0x658,33,39,1,0,0,%,,,0\n" // Battery Health
                        +"0x658,42,42,1,0,0,,,,0\n" // Charging
                        +"0x65b,0,10,1,0,0,min,,,0\n" // Schedule timer 1 min
                        +"0x65b,12,22,1,0,0,min,,,0\n" // Schedule timer 2 min
                        +"0x65b,24,30,1,0,0,%,,,0\n" // Fluent driver
                        +"0x65b,25,26,1,0,0,,,,0\n" // Economy Mode
                        +"0x65b,33,34,1,0,0,,,,0\n" // Economy Mode displayed
                        +"0x65b,39,40,1,0,0,,,,0\n" // Consider eco mode
                        +"0x65b,41,43,1,0,0,,,,0\n" // Charging Status Display
                        +"0x65b,44,45,1,0,0,,,,0\n" // Set park for charging
                        +"0x66a,5,7,1,0,0,,,,0\n" // Cruise Control Mode
                        +"0x66a,8,15,1,0,0,km/h,,,0\n" // Cruise Control Speed
                        +"0x66a,16,16,1,0,0,,,,0\n" // Cruise Control OverSpeed
                        +"0x68b,0,3,1,0,0,,,,0\n" // MM action counter
                        +"0x699,0,1,1,0,0,,,,2\n" // Clima off Request display
                        +"0x699,2,3,1,0,0,,,,2\n" // Clima read defrost Reuqest display
                        +"0x699,4,4,-1,-1,0,,,,2\n" //
                        +"0x699,5,5,1,0,0,,,,2\n" // Maximum defrost
                        +"0x699,6,6,1,0,0,,,,2\n" // Autofan
                        +"0x699,10,14,0.5,0,0,°C,,,2\n" // Temperature
                        +"0x699,16,16,1,0,0,,,,2\n" // Windshield
                        +"0x699,18,18,1,0,0,,,,2\n" // Face
                        +"0x699,19,19,1,0,0,,,,2\n" // Feet
                        +"0x699,20,21,1,0,0,,,,2\n" // Forced recycling
                        +"0x699,22,23,1,0,0,,,,2\n" //
                        +"0x699,24,27,1,0,0,,,,2\n" //
                        +"0x699,28,31,1,0,0,,,,2\n" //
                        +"0x699,52,53,1,0,0,,,,2\n" //
                        +"0x699,54,55,1,0,0,,,,2\n" //
                        +"0x699,56,56,1,0,0,,,,2\n" //
                        +"0x69f,0,31,1,0,0,,,,0\n" // Car Serial N°
                        +"0x6f8,16,23,6.25,0,2,V,,,0\n" // 12V Battery Voltage
                        +"0x760,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x760,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x760,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x760,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x760,24,31,1,0,0,bar,0x224b0e,0x624b0e,2\n" // Master cylinder pressure
                        +"0x762,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x762,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x762,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x762,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x762,24,39,0.390625,100,0,V,0x22012f,0x62012f,0\n" // 12V Battery Voltage
                        +"0x763,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x763,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x763,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x763,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x763,24,31,1,0,0,,0x222001,0x622001,0\n" // Parking Break
                        +"0x763,3,3,1,0,0,,0x2220f0,0x6220f0,0\n" // VOL+
                        +"0x763,4,4,1,0,0,,0x2220f0,0x6220f0,0\n" // VOL-
                        +"0x763,2,2,1,0,0,,0x2220f0,0x6220f0,0\n" // Mute
                        +"0x763,5,5,1,0,0,,0x2220f0,0x6220f0,0\n" // Media
                        +"0x763,6,6,1,0,0,,0x2220f0,0x6220f0,0\n" // Radio
                        +"0x764,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x764,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x764,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x764,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x764,8,15,0.4,40,1,,0x2121,0x6121,1\n" // Interior temperature
                        +"0x765,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x765,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x765,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x765,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x76d,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x76d,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x76d,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x76d,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x76e,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x76e,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x76e,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x76e,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x772,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x772,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x772,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x772,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x77e,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x77e,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x77e,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x77e,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x77e,24,31,1,0,0,,0x22300f,0x62300f,0\n" // dcdc state
                        +"0x77e,24,31,31.25,0,3,V,0x22300e,0x62300e,0\n" // traction battery voltage
                        +"0x77e,24,39,0.015625,0,2,ºC,0x223018,0x623018,1\n" // DCDC converter temperature
                        +"0x77e,24,31,0.03125,0,0,Nm,0x223024,0x623024,0\n" // torque requested
                        +"0x77e,24,31,0.03125,0,0,Nm,0x223025,0x623025,0\n" // torque applied
                        +"0x77e,24,31,0.015625,0,2,°C,0x22302b,0x62302b,0\n" // inverter temperature
                        +"0x77e,24,31,6.25,0,2,A,0x22301d,0x62301d,0\n" // Current
                        +"0x793,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x793,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x793,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x793,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7b6,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x7b6,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x7b6,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7b6,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7bb,192,207,0.01,0,2,kW,0x2101,0x6101,1\n" // Maximum battery input power
                        +"0x7bb,208,223,0.01,0,2,kW,0x2101,0x6101,1\n" // Maximum battery output power
                        +"0x7bb,348,367,0.0001,0,4,Ah,0x2101,0x6101,1\n" // Ah of the battery
                        +"0x7bb,316,335,0.0001,0,4,%,0x2101,0x6101,1\n" // Real State of Charge
                        +"0x7bb,336,351,0.01,0,2,kW,0x2101,0x6101,2\n" // Maximum battery input power
                        +"0x7bb,16,31,1,0,0,unknown,0x2104,0x6104,2\n" // Module 1 raw NTC
                        +"0x7bb,32,39,1,40,0,°C,0x2104,0x6104,2\n" // Cell 1 Temperature
                        +"0x7bb,40,55,1,0,0,unknown,0x2104,0x6104,2\n" // Module 2 raw NTC
                        +"0x7bb,56,63,1,40,0,°C,0x2104,0x6104,2\n" // Cell 2 Temperature
                        +"0x7bb,64,79,1,0,0,unknown,0x2104,0x6104,2\n" // Module 3 raw NTC
                        +"0x7bb,80,87,1,40,0,°C,0x2104,0x6104,2\n" // Cell 3 Temperature
                        +"0x7bb,88,103,1,0,0,unknown,0x2104,0x6104,2\n" // Module 4 raw NTC
                        +"0x7bb,104,111,1,40,0,°C,0x2104,0x6104,2\n" // Cell 4 Temperature
                        +"0x7bb,112,127,1,0,0,unknown,0x2104,0x6104,2\n" // Module 5 raw NTC
                        +"0x7bb,128,135,1,40,0,°C,0x2104,0x6104,2\n" // Cell 5 Temperature
                        +"0x7bb,136,151,1,0,0,unknown,0x2104,0x6104,2\n" // Module 6 raw NTC
                        +"0x7bb,152,159,1,40,0,°C,0x2104,0x6104,2\n" // Cell 6 Temperature
                        +"0x7bb,160,175,1,0,0,unknown,0x2104,0x6104,2\n" // Module 7 raw NTC
                        +"0x7bb,176,183,1,40,0,°C,0x2104,0x6104,2\n" // Cell 7 Temperature
                        +"0x7bb,184,199,1,0,0,unknown,0x2104,0x6104,2\n" // Module 8 raw NTC
                        +"0x7bb,200,207,1,40,0,°C,0x2104,0x6104,2\n" // Cell 8 Temperature
                        +"0x7bb,208,223,1,0,0,unknown,0x2104,0x6104,2\n" // Module 9 raw NTC
                        +"0x7bb,224,231,1,40,0,°C,0x2104,0x6104,2\n" // Cell 9 Temperature
                        +"0x7bb,232,247,1,0,0,unknown,0x2104,0x6104,2\n" // Module 10 raw NTC
                        +"0x7bb,248,255,1,40,0,°C,0x2104,0x6104,2\n" // Cell 10 Temperature
                        +"0x7bb,256,271,1,0,0,unknown,0x2104,0x6104,2\n" // Module 11 raw NTC
                        +"0x7bb,272,279,1,40,0,°C,0x2104,0x6104,2\n" // Cell 11 Temperature
                        +"0x7bb,280,295,1,0,0,unknown,0x2104,0x6104,2\n" // Module 12 raw NTC
                        +"0x7bb,296,303,1,40,0,°C,0x2104,0x6104,2\n" // Cell 12 Temperature
                        +"0x7bb,16,31,1,0,0,unknown,0x2104,0x6104,1\n" // Module 1 raw NTC
                        +"0x7bb,32,39,1,0,0,°C,0x2104,0x6104,1\n" // Cell 1 Temperature
                        +"0x7bb,40,55,1,0,0,unknown,0x2104,0x6104,1\n" // Module 2 raw NTC
                        +"0x7bb,56,63,1,0,0,°C,0x2104,0x6104,1\n" // Cell 2 Temperature
                        +"0x7bb,64,79,1,0,0,unknown,0x2104,0x6104,1\n" // Module 3 raw NTC
                        +"0x7bb,80,87,1,0,0,°C,0x2104,0x6104,1\n" // Cell 3 Temperature
                        +"0x7bb,88,103,1,0,0,unknown,0x2104,0x6104,1\n" // Module 4 raw NTC
                        +"0x7bb,104,111,1,0,0,°C,0x2104,0x6104,1\n" // Cell 4 Temperature
                        +"0x7bb,64,79,0.001,0,3,V,0x2105,0x6105,1\n" // Threshold bad cell
                        +"0x7bb,80,95,0.001,0,3,V,0x2105,0x6105,1\n" // Threshol weak cell
                        +"0x7bb,16,31,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 01 V
                        +"0x7bb,32,47,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 02 V
                        +"0x7bb,48,63,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 03 V
                        +"0x7bb,64,79,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 04 V
                        +"0x7bb,80,95,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 05 V
                        +"0x7bb,96,111,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 06 V
                        +"0x7bb,112,127,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 07 V
                        +"0x7bb,128,143,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 08 V
                        +"0x7bb,144,159,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 09 V
                        +"0x7bb,160,175,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 10 V
                        +"0x7bb,176,191,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 11 V
                        +"0x7bb,192,207,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 12 V
                        +"0x7bb,208,223,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 13 V
                        +"0x7bb,224,239,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 14 V
                        +"0x7bb,240,255,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 15 V
                        +"0x7bb,256,271,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 16 V
                        +"0x7bb,272,287,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 17 V
                        +"0x7bb,288,303,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 18 V
                        +"0x7bb,304,319,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 19 V
                        +"0x7bb,320,335,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 20 V
                        +"0x7bb,336,351,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 21 V
                        +"0x7bb,352,367,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 22 V
                        +"0x7bb,368,383,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 23 V
                        +"0x7bb,384,399,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 24 V
                        +"0x7bb,400,415,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 25 V
                        +"0x7bb,416,431,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 26 V
                        +"0x7bb,432,447,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 27 V
                        +"0x7bb,448,463,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 28 V
                        +"0x7bb,464,479,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 29 V
                        +"0x7bb,480,495,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 30 V
                        +"0x7bb,496,511,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 31 V
                        +"0x7bb,512,527,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 32 V
                        +"0x7bb,528,543,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 33 V
                        +"0x7bb,544,559,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 34 V
                        +"0x7bb,560,575,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 35 V
                        +"0x7bb,576,591,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 36 V
                        +"0x7bb,592,607,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 37 V
                        +"0x7bb,608,623,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 38 V
                        +"0x7bb,624,639,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 39 V
                        +"0x7bb,640,655,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 40 V
                        +"0x7bb,656,671,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 41 V
                        +"0x7bb,672,687,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 42 V
                        +"0x7bb,688,703,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 43 V
                        +"0x7bb,704,719,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 44 V
                        +"0x7bb,720,735,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 45 V
                        +"0x7bb,736,751,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 46 V
                        +"0x7bb,752,767,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 47 V
                        +"0x7bb,768,783,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 48 V
                        +"0x7bb,784,799,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 49 V
                        +"0x7bb,800,815,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 50 V
                        +"0x7bb,816,831,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 51 V
                        +"0x7bb,832,847,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 52 V
                        +"0x7bb,848,863,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 53 V
                        +"0x7bb,864,879,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 54 V
                        +"0x7bb,880,895,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 55 V
                        +"0x7bb,896,911,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 56 V
                        +"0x7bb,912,927,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 57 V
                        +"0x7bb,928,943,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 58 V
                        +"0x7bb,944,959,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 59 V
                        +"0x7bb,960,975,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 60 V
                        +"0x7bb,976,991,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 61 V
                        +"0x7bb,992,1007,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 62 V
                        +"0x7bb,16,31,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 63 V
                        +"0x7bb,32,47,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 64 V
                        +"0x7bb,48,63,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 65 V
                        +"0x7bb,64,79,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 66 V
                        +"0x7bb,80,95,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 67 V
                        +"0x7bb,96,111,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 68 V
                        +"0x7bb,112,127,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 69 V
                        +"0x7bb,128,143,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 70 V
                        +"0x7bb,144,159,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 71 V
                        +"0x7bb,160,175,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 72 V
                        +"0x7bb,176,191,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 73 V
                        +"0x7bb,192,207,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 74 V
                        +"0x7bb,208,223,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 75 V
                        +"0x7bb,224,239,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 76 V
                        +"0x7bb,240,255,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 77 V
                        +"0x7bb,256,271,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 78 V
                        +"0x7bb,272,287,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 79 V
                        +"0x7bb,288,303,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 80 V
                        +"0x7bb,304,319,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 81 V
                        +"0x7bb,320,335,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 82 V
                        +"0x7bb,336,351,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 83 V
                        +"0x7bb,352,367,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 84 V
                        +"0x7bb,368,383,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 85 V
                        +"0x7bb,384,399,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 86 V
                        +"0x7bb,400,415,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 87 V
                        +"0x7bb,416,431,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 88 V
                        +"0x7bb,432,447,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 89 V
                        +"0x7bb,448,463,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 90 V
                        +"0x7bb,464,479,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 91 V
                        +"0x7bb,480,495,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 92 V
                        +"0x7bb,496,511,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 93 V
                        +"0x7bb,512,527,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 94 V
                        +"0x7bb,528,543,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 95 V
                        +"0x7bb,544,559,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 96 V
                        +"0x7bb,60,79,0.0001,0,4,Ah,0x2161,0x6161,1\n" // Ah of the battery
                        +"0x7bb,80,87,0.05,0,2,%,0x2161,0x6161,1\n" // Battery State of Health
                        +"0x7bb,104,119,1,0,0,km,0x2161,0x6161,1\n" // Battery mileage in km
                        +"0x7bb,136,151,1,0,0,kWh,0x2161,0x6161,1\n" // Total energy output of battery?
                        +"0x7bb,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x7bb,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x7bb,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7bb,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7bc,144,159,1,0,0,,0x2180,0x6180,0\n" // Request firmware version
                        +"0x7bc,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x7bc,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7bc,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7da,144,159,1,0,0,,0x2180,0x6180,0\n" // Request firmware version
                        +"0x7da,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x7da,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7da,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7ec,144,159,1,0,0,,0x2180,0x6180,0\n" // Request firmware version
                        +"0x7ec,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x7ec,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7ec,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7ec,24,39,2,0,2,%,0x222002,0x622002,2\n" // SOC
                        +"0x7ec,24,39,2.083333333,0,2,%,0x222002,0x622002,1\n" // SOC
                        +"0x7ec,24,39,0.01,0,2,km/h,0x222003,0x622003,1\n" // Speed
                        +"0x7ec,24,39,0.5,0,2,V,0x222004,0x622004,0\n" // Motor Voltage
                        +"0x7ec,24,39,0.01,0,2,V,0x222005,0x622005,0\n" // 12V battery voltage
                        +"0x7ec,24,47,1,0,0,km,0x222006,0x622006,0\n" // Odometer
                        +"0x7ec,24,39,1,0,0,,0x22202e,0x62202e,0\n" // Pedal
                        +"0x7ec,24,31,1,0,0,,0x22204b,0x62204b,1\n" // Steering wheel CC/SL buttons
                        +"0x7ec,24,39,0.5,0,2,V,0x223203,0x623203,0\n" // Battery voltage
                        +"0x7ec,24,39,0.25,0x8000,2,A,0x223204,0x623204,0\n" // Battery current
                        +"0x7ec,24,31,1,0,0,%,0x223206,0x623206,0\n" // Battery health in %
                        +"0x7ec,24,31,1,1,0,,0x223318,0x623318,1\n" // Motor Water pump speed
                        +"0x7ec,24,31,1,1,0,,0x223319,0x623319,1\n" // Charger pump speed
                        +"0x7ec,24,31,1,1,0,,0x22331A,0x62331A,1\n" // Heater water pump speed
                        +"0x7ec,24,31,1,40,0,°C,0x2233b1,0x6233b1,0\n" // Ext temp
                        */

                        // 2015-11-28

                        +"0x023,0,15,1,0,0,,,,0\n" // AIRBAGCrash
                        +"0x0c6,0,15,1,0x8000,1,°,,,0\n" // Steering Position
                        +"0x0c6,16,31,1,0x8000,1,°/s,,,0\n" // Steering Acceleration
                        +"0x0c6,32,47,1,0x8000,1,°,,,0\n" // SteeringWheelAngle_Offset
                        +"0x0c6,48,50,1,0,0,,,,0\n" // SwaSensorInternalStatus
                        +"0x0c6,51,54,1,0,0,,,,0\n" // SwaClock
                        +"0x0c6,56,53,1,0,0,,,,0\n" // SwaChecksum
                        +"0x12e,0,7,1,198,0,,,,0\n" // LongitudinalAccelerationProc
                        +"0x12e,8,23,1,0x8000,0,,,,0\n" // TransversalAcceleration
                        +"0x12e,24,35,0.1,2047,1,deg/s,,,0\n" // Yaw rate
                        +"0x130,8,10,1,0,0,,,,0\n" // UBP_Clock
                        +"0x130,11,12,1,0,0,,,,0\n" // HBB_Malfunction
                        +"0x130,16,17,1,0,0,,,,0\n" // EB_Malfunction
                        +"0x130,18,19,1,0,0,,,,0\n" // EB_inProgress
                        +"0x130,20,31,1,4094,0,Nm,,,0\n" // ElecBrakeWheelsTorqueRequest
                        +"0x130,32,38,1,0,0,%,,,0\n" // BrakePedalDriverWill
                        +"0x130,40,41,1,0,0,,,,0\n" // HBA_ActivationRequest
                        +"0x130,42,43,1,0,0,,,,0\n" // PressureBuildUp
                        +"0x130,44,55,-3,4094,0,Nm,,,0\n" // DriverBrakeWheelTq_Req
                        +"0x130,56,63,1,0,0,,,,0\n" // CheckSum_UBP
                        +"0x17a,24,27,1,0,0,,,,0\n" // Transmission Range
                        +"0x17a,48,63,0.5,12800,1,Nm,,,\n" // Estimated Wheen Torque
                        +"0x17e,40,41,1,0,0,,,,0\n" // CrankingAuthorisation_AT
                        +"0x17e,48,51,1,0,0,,,,0\n" // GearLeverPosition
                        +"0x186,0,15,0.125,0,2,rpm,,,0\n" // Speed
                        +"0x186,16,27,0.5,800,1,Nm,,,0\n" // MeanEffectiveTorque
                        +"0x186,28,39,0.5,800,0,Nm,,,0\n" // RequestedTorqueAfterProc
                        +"0x186,40,49,0.125,0,1,%,,,0\n" // Throttle
                        +"0x186,50,50,1,0,0,,,,0\n" // ASR_MSRAcknowledgement
                        +"0x186,51,52,1,0,0,,,,0\n" // ECM_TorqueRequestStatus
                        +"0x18a,16,25,0.125,0,2,%,,,0\n" // Throttle
                        +"0x18a,27,38,0.5,800,1,Nm,,,0\n" // Coasting Torque
                        +"0x1f6,0,1,1,0,0,,,,0\n" // Engine Fan Speed
                        +"0x1f6,3,7,100,0,0,W,,,0\n" // Max Electrical Power Allowed
                        +"0x1f6,8,9,1,0,0,,,,0\n" // ElectricalPowerCutFreeze
                        +"0x1f6,10,11,1,0,0,,,,0\n" // EngineStatus_R
                        +"0x1f6,12,15,1,0,0,,,,0\n" // EngineStopRequestOrigine
                        +"0x1f6,16,17,1,0,0,,,,0\n" // CrankingAuthorization_ECM
                        +"0x1f6,19,20,1,0,1,,,,0\n" // Break Pedal
                        +"0x1f6,23,31,0.1,0,1,bar,,,0\n" // AC High Pressure Sensor
                        +"0x1f8,0,7,1,0,0,,,,0\n" // Checksum EVC
                        +"0x1f8,12,13,1,0,0,,,,0\n" // EVCReadyAsActuator
                        +"0x1f8,16,27,1,4096,0,Nm,,,0\n" // TotalPotentialResistiveWheelsTorque
                        +"0x1f8,28,39,-1,4096,0,Nm,,,0\n" // ElecBrakeWheelsTorqueApplied
                        +"0x1f8,40,50,10,0,0,Rpm,,,0\n" // ElecEngineRPM
                        +"0x1f8,52,54,1,0,0,,,,0\n" // EVC_Clock
                        +"0x1f8,56,58,1,0,0,,,,0\n" // GearRangeEngagedCurrent
                        +"0x1f8,62,63,1,0,0,,,,0\n" // DeclutchInProgress
                        +"0x1fd,0,7,0.390625,0,1,%,,,0\n" // 12V Battery Current?
                        +"0x1fd,8,9,1,0,0,,,,0\n" // SCH Refuse to Sleep
                        +"0x1fd,17,18,1,0,0,,,,0\n" // Stop Preheating Counter
                        +"0x1fd,19,20,1,0,0,,,,0\n" // Start Preheating Counter
                        +"0x1fd,21,31,1,0,0,min,,,0\n" // Time left before vehicle wakeup
                        +"0x1fd,32,32,1,0,0,,,,0\n" // Pre heating activation
                        +"0x1fd,33,39,1,0,0,min,,,0\n" // LeftTimeToScheduledTime
                        +"0x1fd,40,47,25,0,0,W,,,0\n" // ClimAvailablePower
                        +"0x1fd,48,55,1,0x50,0,kW,,,0\n" // Consumption
                        +"0x212,8,9,1,0,0,,,,0\n" // StarterStatus
                        +"0x212,10,11,1,0,0,,,,0\n" // RearGearEngaged
                        +"0x242,0,0,1,0,0,,,,\n" // ABSinRegulation
                        +"0x242,1,1,1,0,0,,,,\n" // ABSMalfunction
                        +"0x242,2,2,1,0,0,,,,\n" // ASRinRegulation
                        +"0x242,3,3,1,0,0,,,,\n" // ASRMalfunction
                        +"0x242,5,5,1,0,0,,,,\n" // AYCinRegulation
                        +"0x242,6,6,1,0,0,,,,\n" // AYCMalfunction
                        +"0x242,7,7,1,0,0,,,,\n" // MSRinRegulation
                        +"0x242,8,8,1,0,0,,,,\n" // MSRMalfunction
                        +"0x242,9,12,1,0,0,,,,\n" // ESP_Clock
                        +"0x242,13,15,1,0,0,,,,\n" // ESP_TorqueControlType
                        +"0x242,16,27,0.5,800,1,Nm,,,\n" // ASRDynamicTorqueRequest
                        +"0x242,28,39,0.5,800,1,Nm,,,\n" // ASRStaticTorqueRequest
                        +"0x242,40,51,0.5,800,1,Nm,,,\n" // MSRTorqueRequest
                        +"0x29a,0,15,0.04166666667,0,2,rpm,,,0\n" // Rpm Front Right
                        +"0x29a,16,31,0.04166666667,0,2,rpm,,,0\n" // Rpm Front Left
                        +"0x29a,32,47,0.01,0,2,km/h,,,0\n" // Vehicle Speed
                        +"0x29a,52,55,1,0,0,,,,0\n" // Vehicle Speed Clock
                        +"0x29a,56,63,1,0,0,,,,0\n" // Vehicle Speed Checksum
                        +"0x29c,0,15,0.04166666667,0,2,rpm,,,0\n" // Rpm Rear Right
                        +"0x29c,16,31,0.04166666667,0,2,rpm,,,0\n" // Rpm Rear Left
                        +"0x29c,48,63,0.01,0,2,km/h,,,0\n" // Vehicle Speed
                        +"0x2b7,32,33,1,0,0,,,,0\n" // EBD Active
                        +"0x2b7,34,35,1,0,0,,,,0\n" // HBA Active
                        +"0x2b7,36,37,1,0,0,,,,0\n" // ESC HBB Malfunction
                        +"0x352,0,1,1,0,0,,,,0\n" // ABS Warning Request
                        +"0x352,2,3,1,0,0,,,,0\n" // ESP_StopLampRequest
                        +"0x352,24,31,1,0,0,,,,0\n" // Break pressure
                        +"0x35c,0,1,1,0,0,,,,0\n" // BCM_WakeUpSleepCommand
                        +"0x35c,4,4,1,0,0,,,,0\n" // WakeUpType
                        +"0x35c,5,7,1,0,0,,,,0\n" // VehicleState
                        +"0x35c,8,8,1,0,0,,,,0\n" // DiagMuxOn_BCM
                        +"0x35c,9,10,1,0,0,,,,0\n" // StartingMode_BCM_R
                        +"0x35c,11,11,1,0,0,,,,0\n" // EngineStopDriverRequested
                        +"0x35c,12,12,1,0,0,,,,0\n" // SwitchOffSESDisturbers
                        +"0x35c,15,15,1,0,0,,,,0\n" // DeliveryModeInformation
                        +"0x35c,16,39,1,0,0,min,,,0\n" // AbsoluteTimeSince1rstIgnition
                        +"0x35c,40,42,1,0,0,,,,0\n" // BrakeInfoStatus
                        +"0x35c,47,47,1,0,0,,,,0\n" // ProbableCustomerFeedBackNeed
                        +"0x35c,48,51,1,0,0,,,,0\n" // EmergencyEngineStop
                        +"0x35c,52,52,1,0,0,,,,0\n" // WelcomePhaseState
                        +"0x35c,53,54,1,0,0,,,,0\n" // SupposedCustomerDeparture
                        +"0x35c,55,55,1,0,0,,,,0\n" // VehicleOutsideLockedState
                        +"0x35c,58,59,1,0,0,,,,0\n" // GenericApplicativeDiagEnable
                        +"0x35c,60,61,1,0,0,,,,0\n" // ParkingBrakeStatus
                        +"0x3f7,2,3,1,0,0,,,,2\n" // Gear?
                        +"0x427,0,1,1,0,0,,,,0\n" // HVConnectionStatus
                        +"0x427,2,3,1,0,0,,,,0\n" // ChargingAlert
                        +"0x427,4,5,1,0,0,,,,0\n" // HVBatteryLocked
                        +"0x427,26,28,1,0,0,,,,0\n" // PreHeatingProgress
                        +"0x427,40,47,0.3,0,0,kW,,,2\n" // AvailableChargingPower
                        +"0x427,49,57,0.1,0,1,kWh,,,0\n" // AvailableEnergy
                        +"0x427,58,58,1,0,0,,,,0\n" // ChargeAvailable
                        +"0x42a,0,0,1,0,0,,,,0\n" // PreHeatingRequest
                        +"0x42a,6,15,0.1,40,1,°C,,,0\n" // EvaporatorTempSetPoint
                        +"0x42a,24,29,1,0,0,%,,,0\n" // ClimAirFlow
                        +"0x42a,30,39,0.1,40,1,°C,,,0\n" // EvaporatorTempMeasure
                        +"0x42a,45,46,1,0,0,,,,0\n" // ImmediatePreheatingAuthorizationStatus
                        +"0x42a,48,49,1,0,0,,,,0\n" // ClimLoopMode
                        +"0x42a,51,52,1,0,0,,,,0\n" // PTCActivationRequest
                        +"0x42a,56,60,5,0,0,%,,,0\n" // EngineFanSpeedRequestPWM
                        +"0x42e,0,12,0.02,0,2,%,,,0\n" // State of Charge
                        +"0x42e,18,19,1,0,0,,,,0\n" // HVBatLevel2Failure
                        +"0x42e,20,24,5,0,0,%,,,0\n" // EngineFanSpeed
                        +"0x42e,25,34,0.5,0,0,V,,,0\n" // HVNetworkVoltage
                        +"0x42e,38,43,1,0,1,A,,,0\n" // Charging Pilot Current
                        +"0x42e,44,50,1,40,0,°C,,,0\n" // HVBatteryTemp
                        +"0x42e,56,63,0.3,0,1,kW,,,0\n" // ChargingPower
                        +"0x430,40,49,0.1,40,1,°C,,,2\n" // HV Battery Evaporator Temp
                        +"0x430,50,59,0.1,40,1,°C,,,2\n" // HV Battery Evaporator Setpoint
                        +"0x4f8,0,1,-1,-2,0,,,,0\n" // Start
                        +"0x4f8,4,5,-1,-2,0,,,,0\n" // Parking Break
                        +"0x4f8,8,9,1,0,0,,,,0\n" // AIRBAGMalfunctionLampState
                        +"0x4f8,12,12,1,0,0,,,,0\n" // ClusterDrivenLampsAutoCheck
                        +"0x4f8,13,13,1,0,0,,,,0\n" // DisplayedSpeedUnit
                        +"0x4f8,24,39,0.01,0,2,,,,2\n" // Speed on Display
                        +"0x534,32,40,1,40,0,°C,,,1\n" // Temp out
                        +"0x5d7,0,15,0.01,0,2,km/h,,,0\n" // Speed
                        +"0x5d7,16,43,0.01,0,2,km,,,0\n" // Odometer
                        +"0x5d7,44,45,1,0,0,?,,,0\n" // WheelsLockingState
                        +"0x5d7,48,49,1,0,0,?,,,0\n" // VehicleSpeedSign
                        +"0x5d7,50,54,0.04,0,2,cm,,,0\n" // Fine distance
                        +"0x5da,0,7,1,40,0,ºC,,,1\n" // Water temperature
                        +"0x5de,1,1,1,0,0,,,,0\n" // Right Indicator
                        +"0x5de,2,2,1,0,0,,,,0\n" // Left Indicator
                        +"0x5de,3,3,1,0,0,,,,0\n" // Rear Fog Light
                        +"0x5de,5,5,1,0,0,,,,0\n" // Park Light
                        +"0x5de,6,6,1,0,0,,,,0\n" // Head Light
                        +"0x5de,7,7,1,0,0,,,,0\n" // Beam Light
                        +"0x5de,8,9,1,0,0,,,,0\n" // PositionLightsOmissionWarning
                        +"0x5de,10,10,1,0,0,,,,0\n" // ALS malfunction
                        +"0x5de,11,12,1,0,0,,,,0\n" // Door Front Left
                        +"0x5de,13,14,1,0,0,,,,0\n" // Dort Front Right
                        +"0x5de,16,17,1,0,0,,,,0\n" // Door Rear Left
                        +"0x5de,18,19,1,0,0,,,,0\n" // Door Rear Right
                        +"0x5de,21,22,1,0,0,,,,\n" // Steering Lock Failure
                        +"0x5de,23,23,1,0,0,,,,\n" // Unlocking Steering Column Warning
                        +"0x5de,24,24,1,0,0,,,,\n" // Automatic Lock Up Activation State
                        +"0x5de,25,25,1,0,0,,,,\n" // Badge Battery Low
                        +"0x5de,28,29,1,0,0,,,,\n" // Trip Display Scrolling Request
                        +"0x5de,32,35,1,0,0,,,,\n" // Smart Keyless Information Display
                        +"0x5de,36,36,1,0,0,,,,\n" // Keyless Info Reemission Request
                        +"0x5de,37,37,1,0,0,,,,\n" // Keyless Card Reader Failure Display
                        +"0x5de,47,47,1,0,0,,,,\n" // Brake Switch Fault Display
                        +"0x5de,49,49,1,0,0,,,,\n" // Stop Lamp Failure Display
                        +"0x5de,56,57,1,0,0,,,,\n" // Rear Wiper Status
                        +"0x5de,58,59,1,0,0,,,,0\n" // Boot Open Warning
                        +"0x5ee,0,0,1,0,0,,,,0\n" // Park Light
                        +"0x5ee,1,1,1,0,0,,,,0\n" // Head Light
                        +"0x5ee,2,2,1,0,0,,,,0\n" // Beam Light
                        +"0x5ee,16,19,1,0,0,,,,0\n" // Door Locks
                        +"0x5ee,20,24,1,0,0,,,,0\n" // Indicators
                        +"0x5ee,24,27,1,0,0,,,,0\n" // Doors
                        +"0x5ee,40,40,1,0,0,,,,0\n" // LightSensorStatus
                        +"0x646,8,15,0.1,0,1,kWh/100km,,,0\n" // Average trip B consumpion
                        +"0x646,16,32,0.1,0,1,km,,,0\n" // Trip B distance
                        +"0x646,33,47,0.1,0,1,kWh,,,0\n" // trip B consumption
                        +"0x646,48,59,0.1,0,1,km/h,,,0\n" // Averahe trip B speed
                        +"0x653,9,9,1,0,0,,,,0\n" // Driver seatbelt
                        +"0x654,2,2,1,0,0,,,,0\n" // ChargingPlugConnected
                        +"0x654,3,3,1,0,0,,,,0\n" // DriverWalkAwayEngineON
                        +"0x654,4,4,1,0,0,,,,0\n" // HVBatteryUnballastAlert
                        +"0x654,25,31,1,0,0,,,,0\n" // State of Charge
                        +"0x654,32,41,1,0,0,min,,,0\n" // Time to Full
                        +"0x654,42,51,1,0,0,km,,,0\n" // Available Distance
                        +"0x654,52,61,0.1,0,1,,,,0\n" // AverageConsumption
                        +"0x654,62,62,1,0,0,,,,0\n" // HVBatteryLow
                        +"0x656,3,3,1,0,0,,,,0\n" // Trip Data Reset
                        +"0x656,21,31,1,0,0,min,,,0\n" // Cluste rScheduled Time
                        +"0x656,32,42,1,0,0,min,,,0\n" // Cluster Scheduled Time 2
                        +"0x656,48,55,1,40,0,°C,,,2\n" // External Temp
                        +"0x656,56,57,1,0,0,,,,2\n" // ClimPCCustomerActiv
                        +"0x658,0,31,1,0,0,,,,0\n" // Battery Serial N°
                        +"0x658,33,39,1,0,0,%,,,0\n" // Battery Health
                        +"0x658,42,42,1,0,0,,,,0\n" // Charging
                        +"0x65b,0,10,1,0,0,min,,,0\n" // Schedule timer 1 min
                        +"0x65b,12,22,1,0,0,min,,,0\n" // Schedule timer 2 min
                        +"0x65b,24,30,1,0,0,%,,,0\n" // Fluent driver
                        +"0x65b,25,26,1,0,0,,,,0\n" // Economy Mode
                        +"0x65b,33,34,1,0,0,,,,0\n" // Economy Mode displayed
                        +"0x65b,39,40,1,0,0,,,,0\n" // Consider eco mode
                        +"0x65b,41,43,1,0,0,,,,0\n" // Charging Status Display
                        +"0x65b,44,45,1,0,0,,,,0\n" // Set park for charging
                        +"0x66a,5,7,1,0,0,,,,0\n" // Cruise Control Mode
                        +"0x66a,8,15,1,0,0,km/h,,,0\n" // Cruise Control Speed
                        +"0x66a,16,16,1,0,0,,,,0\n" // Cruise Control OverSpeed
                        +"0x673,0,0,1,0,0,,,,0\n" // Speed pressure misadaptation
                        +"0x673,2,4,1,0,0,,,,0\n" // Rear right wheel state
                        +"0x673,5,7,1,0,0,,,,0\n" // Rear left wheel state
                        +"0x673,8,10,1,0,0,,,,0\n" // Front right wheel state
                        +"0x673,11,13,1,0,0,,,,0\n" // Front left wheel state
                        +"0x673,16,23,13.725,0,0,mbar,,,0\n" // Rear right wheel pressure
                        +"0x673,24,31,13.725,0,0,mbar,,,0\n" // Rear left wheel pressure
                        +"0x673,32,39,13.725,0,0,mbar,,,0\n" // Front right wheel pressure
                        +"0x673,40,47,13.725,0,0,mbar,,,0\n" // Front left wheel pressure
                        +"0x68b,0,3,1,0,0,,,,0\n" // MM action counter
                        +"0x699,0,1,1,0,0,,,,2\n" // Clima off Request display
                        +"0x699,2,3,1,0,0,,,,2\n" // Clima read defrost Reuqest display
                        +"0x699,4,4,-1,-1,0,,,,2\n" //
                        +"0x699,5,5,1,0,0,,,,2\n" // Maximum defrost
                        +"0x699,6,6,1,0,0,,,,2\n" // Autofan
                        +"0x699,10,14,0.5,0,0,°C,,,2\n" // Temperature
                        +"0x699,16,16,1,0,0,,,,2\n" // Windshield
                        +"0x699,18,18,1,0,0,,,,2\n" // Face
                        +"0x699,19,19,1,0,0,,,,2\n" // Feet
                        +"0x699,20,21,1,0,0,,,,2\n" // Forced recycling
                        +"0x699,22,23,1,0,0,,,,2\n" //
                        +"0x699,24,27,1,0,0,,,,2\n" //
                        +"0x699,28,31,1,0,0,,,,2\n" //
                        +"0x699,52,53,1,0,0,,,,2\n" //
                        +"0x699,54,55,1,0,0,,,,2\n" //
                        +"0x699,56,56,1,0,0,,,,2\n" //
                        +"0x69f,0,31,1,0,0,,,,0\n" // Car Serial N°
                        +"0x6f8,16,23,6.25,0,2,V,,,0\n" // 12V Battery Voltage
                        +"0x760,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x760,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x760,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x760,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x760,24,31,1,0,0,bar,0x224b0e,0x624b0e,2\n" // Master cylinder pressure
                        +"0x762,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x762,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x762,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x762,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x762,24,39,0.390625,100,0,V,0x22012f,0x62012f,0\n" // 12V Battery Voltage
                        +"0x763,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x763,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x763,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x763,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x763,24,31,1,0,0,,0x222001,0x622001,0\n" // Parking Break
                        +"0x763,3,3,1,0,0,,0x2220f0,0x6220f0,0\n" // VOL+
                        +"0x763,4,4,1,0,0,,0x2220f0,0x6220f0,0\n" // VOL-
                        +"0x763,2,2,1,0,0,,0x2220f0,0x6220f0,0\n" // Mute
                        +"0x763,5,5,1,0,0,,0x2220f0,0x6220f0,0\n" // Media
                        +"0x763,6,6,1,0,0,,0x2220f0,0x6220f0,0\n" // Radio
                        +"0x764,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x764,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x764,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x764,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x764,8,15,0.4,40,1,,0x2121,0x6121,1\n" // Interior temperature
                        +"0x765,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x765,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x765,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x765,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x76d,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x76d,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x76d,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x76d,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x76e,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x76e,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x76e,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x76e,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x772,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x772,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x772,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x772,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x77e,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x77e,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x77e,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x77e,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x77e,24,31,1,0,0,,0x22300f,0x62300f,0\n" // dcdc state
                        +"0x77e,24,31,31.25,0,3,V,0x22300e,0x62300e,0\n" // traction battery voltage
                        +"0x77e,24,39,0.015625,0,2,ºC,0x223018,0x623018,1\n" // DCDC converter temperature
                        +"0x77e,24,31,0.03125,0,0,Nm,0x223024,0x623024,0\n" // torque requested
                        +"0x77e,24,31,0.03125,0,0,Nm,0x223025,0x623025,0\n" // torque applied
                        +"0x77e,24,31,0.015625,0,2,°C,0x22302b,0x62302b,0\n" // inverter temperature
                        +"0x77e,24,31,6.25,0,2,A,0x22301d,0x62301d,0\n" // Current
                        +"0x793,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x793,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x793,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x793,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7b6,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x7b6,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x7b6,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7b6,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7bb,192,207,0.01,0,2,kW,0x2101,0x6101,1\n" // Maximum battery input power
                        +"0x7bb,208,223,0.01,0,2,kW,0x2101,0x6101,1\n" // Maximum battery output power
                        +"0x7bb,348,367,0.0001,0,4,Ah,0x2101,0x6101,1\n" // Ah of the battery
                        +"0x7bb,316,335,0.0001,0,4,%,0x2101,0x6101,1\n" // Real State of Charge
                        +"0x7bb,336,351,0.01,0,2,kW,0x2101,0x6101,2\n" // Maximum battery input power
                        +"0x7bb,56,71,0.1,0,1,°C,0x2103,0x6103,1\n" // Mean compartment temp
                        +"0x7bb,16,31,1,0,0,stravinsky1124,0x2104,0x6104,2\n" // Module 1 raw NTC
                        +"0x7bb,32,39,1,40,0,°C,0x2104,0x6104,2\n" // Cell 1 Temperature
                        +"0x7bb,40,55,1,0,0,,0x2104,0x6104,2\n" // Module 2 raw NTC
                        +"0x7bb,56,63,1,40,0,°C,0x2104,0x6104,2\n" // Cell 2 Temperature
                        +"0x7bb,64,79,1,0,0,,0x2104,0x6104,2\n" // Module 3 raw NTC
                        +"0x7bb,80,87,1,40,0,°C,0x2104,0x6104,2\n" // Cell 3 Temperature
                        +"0x7bb,88,103,1,0,0,,0x2104,0x6104,2\n" // Module 4 raw NTC
                        +"0x7bb,104,111,1,40,0,°C,0x2104,0x6104,2\n" // Cell 4 Temperature
                        +"0x7bb,112,127,1,0,0,,0x2104,0x6104,2\n" // Module 5 raw NTC
                        +"0x7bb,128,135,1,40,0,°C,0x2104,0x6104,2\n" // Cell 5 Temperature
                        +"0x7bb,136,151,1,0,0,,0x2104,0x6104,2\n" // Module 6 raw NTC
                        +"0x7bb,152,159,1,40,0,°C,0x2104,0x6104,2\n" // Cell 6 Temperature
                        +"0x7bb,160,175,1,0,0,,0x2104,0x6104,2\n" // Module 7 raw NTC
                        +"0x7bb,176,183,1,40,0,°C,0x2104,0x6104,2\n" // Cell 7 Temperature
                        +"0x7bb,184,199,1,0,0,,0x2104,0x6104,2\n" // Module 8 raw NTC
                        +"0x7bb,200,207,1,40,0,°C,0x2104,0x6104,2\n" // Cell 8 Temperature
                        +"0x7bb,208,223,1,0,0,,0x2104,0x6104,2\n" // Module 9 raw NTC
                        +"0x7bb,224,231,1,40,0,°C,0x2104,0x6104,2\n" // Cell 9 Temperature
                        +"0x7bb,232,247,1,0,0,,0x2104,0x6104,2\n" // Module 10 raw NTC
                        +"0x7bb,248,255,1,40,0,°C,0x2104,0x6104,2\n" // Cell 10 Temperature
                        +"0x7bb,256,271,1,0,0,,0x2104,0x6104,2\n" // Module 11 raw NTC
                        +"0x7bb,272,279,1,40,0,°C,0x2104,0x6104,2\n" // Cell 11 Temperature
                        +"0x7bb,280,295,1,0,0,,0x2104,0x6104,2\n" // Module 12 raw NTC
                        +"0x7bb,296,303,1,40,0,°C,0x2104,0x6104,2\n" // Cell 12 Temperature
                        +"0x7bb,16,31,1,0,0,,0x2104,0x6104,1\n" // Module 1 raw NTC
                        +"0x7bb,32,39,1,0,0,°C,0x2104,0x6104,11\n" // Cell 1 Temperature
                        +"0x7bb,40,55,1,0,0,,0x2104,0x6104,1\n" // Module 2 raw NTC
                        +"0x7bb,56,63,1,0,0,°C,0x2104,0x6104,11\n" // Cell 2 Temperature
                        +"0x7bb,64,79,1,0,0,,0x2104,0x6104,1\n" // Module 3 raw NTC
                        +"0x7bb,80,87,1,0,0,°C,0x2104,0x6104,11\n" // Cell 3 Temperature
                        +"0x7bb,88,103,1,0,0,,0x2104,0x6104,1\n" // Module 4 raw NTC
                        +"0x7bb,104,111,1,0,0,°C,0x2104,0x6104,11\n" // Cell 4 Temperature
                        +"0x7bb,64,79,0.001,0,3,V,0x2105,0x6105,1\n" // Threshold bad cell
                        +"0x7bb,80,95,0.001,0,3,V,0x2105,0x6105,1\n" // Threshol weak cell
                        +"0x7bb,16,31,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 01 V
                        +"0x7bb,32,47,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 02 V
                        +"0x7bb,48,63,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 03 V
                        +"0x7bb,64,79,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 04 V
                        +"0x7bb,80,95,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 05 V
                        +"0x7bb,96,111,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 06 V
                        +"0x7bb,112,127,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 07 V
                        +"0x7bb,128,143,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 08 V
                        +"0x7bb,144,159,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 09 V
                        +"0x7bb,160,175,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 10 V
                        +"0x7bb,176,191,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 11 V
                        +"0x7bb,192,207,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 12 V
                        +"0x7bb,208,223,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 13 V
                        +"0x7bb,224,239,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 14 V
                        +"0x7bb,240,255,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 15 V
                        +"0x7bb,256,271,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 16 V
                        +"0x7bb,272,287,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 17 V
                        +"0x7bb,288,303,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 18 V
                        +"0x7bb,304,319,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 19 V
                        +"0x7bb,320,335,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 20 V
                        +"0x7bb,336,351,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 21 V
                        +"0x7bb,352,367,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 22 V
                        +"0x7bb,368,383,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 23 V
                        +"0x7bb,384,399,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 24 V
                        +"0x7bb,400,415,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 25 V
                        +"0x7bb,416,431,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 26 V
                        +"0x7bb,432,447,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 27 V
                        +"0x7bb,448,463,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 28 V
                        +"0x7bb,464,479,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 29 V
                        +"0x7bb,480,495,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 30 V
                        +"0x7bb,496,511,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 31 V
                        +"0x7bb,512,527,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 32 V
                        +"0x7bb,528,543,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 33 V
                        +"0x7bb,544,559,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 34 V
                        +"0x7bb,560,575,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 35 V
                        +"0x7bb,576,591,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 36 V
                        +"0x7bb,592,607,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 37 V
                        +"0x7bb,608,623,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 38 V
                        +"0x7bb,624,639,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 39 V
                        +"0x7bb,640,655,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 40 V
                        +"0x7bb,656,671,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 41 V
                        +"0x7bb,672,687,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 42 V
                        +"0x7bb,688,703,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 43 V
                        +"0x7bb,704,719,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 44 V
                        +"0x7bb,720,735,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 45 V
                        +"0x7bb,736,751,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 46 V
                        +"0x7bb,752,767,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 47 V
                        +"0x7bb,768,783,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 48 V
                        +"0x7bb,784,799,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 49 V
                        +"0x7bb,800,815,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 50 V
                        +"0x7bb,816,831,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 51 V
                        +"0x7bb,832,847,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 52 V
                        +"0x7bb,848,863,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 53 V
                        +"0x7bb,864,879,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 54 V
                        +"0x7bb,880,895,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 55 V
                        +"0x7bb,896,911,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 56 V
                        +"0x7bb,912,927,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 57 V
                        +"0x7bb,928,943,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 58 V
                        +"0x7bb,944,959,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 59 V
                        +"0x7bb,960,975,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 60 V
                        +"0x7bb,976,991,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 61 V
                        +"0x7bb,992,1007,0.001,0,3,V,0x2141,0x6141,0\n" // Cell 62 V
                        +"0x7bb,16,31,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 63 V
                        +"0x7bb,32,47,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 64 V
                        +"0x7bb,48,63,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 65 V
                        +"0x7bb,64,79,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 66 V
                        +"0x7bb,80,95,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 67 V
                        +"0x7bb,96,111,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 68 V
                        +"0x7bb,112,127,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 69 V
                        +"0x7bb,128,143,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 70 V
                        +"0x7bb,144,159,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 71 V
                        +"0x7bb,160,175,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 72 V
                        +"0x7bb,176,191,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 73 V
                        +"0x7bb,192,207,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 74 V
                        +"0x7bb,208,223,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 75 V
                        +"0x7bb,224,239,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 76 V
                        +"0x7bb,240,255,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 77 V
                        +"0x7bb,256,271,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 78 V
                        +"0x7bb,272,287,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 79 V
                        +"0x7bb,288,303,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 80 V
                        +"0x7bb,304,319,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 81 V
                        +"0x7bb,320,335,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 82 V
                        +"0x7bb,336,351,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 83 V
                        +"0x7bb,352,367,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 84 V
                        +"0x7bb,368,383,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 85 V
                        +"0x7bb,384,399,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 86 V
                        +"0x7bb,400,415,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 87 V
                        +"0x7bb,416,431,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 88 V
                        +"0x7bb,432,447,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 89 V
                        +"0x7bb,448,463,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 90 V
                        +"0x7bb,464,479,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 91 V
                        +"0x7bb,480,495,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 92 V
                        +"0x7bb,496,511,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 93 V
                        +"0x7bb,512,527,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 94 V
                        +"0x7bb,528,543,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 95 V
                        +"0x7bb,544,559,0.001,0,3,V,0x2142,0x6142,0\n" // Cell 96 V
                        +"0x7bb,60,79,0.0001,0,4,Ah,0x2161,0x6161,1\n" // Ah of the battery
                        +"0x7bb,80,87,0.05,0,2,%,0x2161,0x6161,1\n" // Battery State of Health
                        +"0x7bb,104,119,1,0,0,km,0x2161,0x6161,1\n" // Battery mileage in km
                        +"0x7bb,136,151,1,0,0,kWh,0x2161,0x6161,1\n" // Total energy output of battery?
                        +"0x7bb,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x7bb,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x7bb,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7bb,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7bc,144,159,1,0,0,,0x2180,0x6180,0\n" // Request firmware version
                        +"0x7bc,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x7bc,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7bc,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7da,144,159,1,0,0,,0x2180,0x6180,0\n" // Request firmware version
                        +"0x7da,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x7da,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7da,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7ec,144,159,1,0,0,,0x2180,0x6180,0\n" // Request firmware version
                        +"0x7ec,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x7ec,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7ec,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7ec,24,39,2,0,2,%,0x222002,0x622002,2\n" // SOC
                        +"0x7ec,24,39,2.083333333,0,2,%,0x222002,0x622002,1\n" // SOC
                        +"0x7ec,24,39,0.01,0,2,km/h,0x222003,0x622003,1\n" // Speed
                        +"0x7ec,24,39,0.5,0,2,V,0x222004,0x622004,0\n" // Motor Voltage
                        +"0x7ec,24,39,0.01,0,2,V,0x222005,0x622005,0\n" // 12V battery voltage
                        +"0x7ec,24,47,1,0,0,km,0x222006,0x622006,0\n" // Odometer
                        +"0x7ec,24,39,1,0,0,,0x22202e,0x62202e,0\n" // Pedal
                        +"0x7ec,24,31,1,0,0,,0x22204b,0x62204b,1\n" // Steering wheel CC/SL buttons
                        +"0x7ec,24,39,0.5,0,2,V,0x223203,0x623203,0\n" // Battery voltage
                        +"0x7ec,24,39,0.25,0x8000,2,A,0x223204,0x623204,0\n" // Battery current
                        +"0x7ec,24,31,1,0,0,%,0x223206,0x623206,0\n" // Battery health in %
                        +"0x7ec,24,31,1,1,0,,0x223318,0x623318,1\n" // Motor Water pump speed
                        +"0x7ec,24,31,1,1,0,,0x223319,0x623319,1\n" // Charger pump speed
                        +"0x7ec,24,31,1,1,0,,0x22331A,0x62331A,1\n" // Heater water pump speed
                        +"0x7ec,24,31,1,40,0,°C,0x2233b1,0x6233b1,0\n" // Ext temp
                ;

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
            if (tokens.length == 10) { // was 15
                /* below 3 lines can be removed with new format */
                //int divider = Integer.parseInt(tokens[FIELD_DIVIDER].trim());
                //int multiplier = Integer.parseInt(tokens[FIELD_MULTIPLIER].trim());
                //double multi = ((double) multiplier / divider);

                int frameId = Integer.parseInt(tokens[FIELD_ID].trim().replace("0x", ""), 16);
                Frame frame = Frames.getInstance().getById(frameId);
                if (frame == null) {
                    MainActivity.debug("frame does not exist:" + tokens[FIELD_ID].trim());
                } else {
                    //Create a new field object and fill his  data
                    MainActivity.debug(tokens[FIELD_ID]+","+tokens[FIELD_FROM]);
                    Field field = new Field(
                            frame,
                            Short.parseShort(tokens[FIELD_FROM].trim()),
                            Short.parseShort(tokens[FIELD_TO].trim()),
                            Double.parseDouble(tokens[FIELD_RESOLUTION].trim()),
                            Integer.parseInt(tokens[FIELD_DECIMALS].trim()),
                            (
                                    tokens[FIELD_OFFSET].trim().contains("0x")
                                            ?
                                            Integer.parseInt(tokens[FIELD_OFFSET].trim().replace("0x", ""), 16)
                                            :
                                            Double.parseDouble(tokens[FIELD_OFFSET].trim())
                            ),
                            tokens[FIELD_UNIT].trim(),
                            tokens[FIELD_REQUEST_ID].trim().replace("0x", ""),
                            tokens[FIELD_RESPONSE_ID].trim().replace("0x", ""),
                            Short.parseShort(tokens[FIELD_OPTIONS].trim(), 16)
/*
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
                                            Double.parseDouble(tokens[FIELD_OFFSET].trim())
                            ),
                            tokens[FIELD_UNIT].trim(),
                            tokens[FIELD_REQUEST_ID].trim().replace("0x", ""),
                            tokens[FIELD_RESPONSE_ID].trim().replace("0x", ""),
                            Integer.parseInt(tokens[FIELD_CAR].trim())
                            //Integer.parseInt(tokens[FIELD_SKIPS].trim())
                            //Integer.parseInt(tokens[FIELD_FREQ].trim())
*/


                    );
                    // add the field to the list of available fields
                    add(field);
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

        // first let's try to get the field that is bound to the selected car
        Field tryField = fieldsBySid.get(MainActivity.car + "."+sid);
        if(tryField!=null) return tryField;

        // if none is found, try the other one, starting with 0 = CAR_ANY
        for(int i=0; i<5; i++) {
            tryField = fieldsBySid.get(i + "." + sid);
            if (tryField != null) return tryField;
        }

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


    @Override
    public void onMessageCompleteEvent(Message message) {
        for(int i=0; i< fields.size(); i++)
        {
            Field field = fields.get(i);

            if(field.getId()== message.getField().getId() &&
                    (
                            message.getField().getResponseId()==null
                            ||
                            message.getField().getResponseId().trim().equals(field.getResponseId().trim())
                    ))
            {
                String binString = message.getAsBinaryString();
                if(binString.length()>= field.getTo()) {
                    // parseInt --> signed, so the first bit is "cut-off"!
                    try {
                        // experiment with unavailable: any field >= 5 bits whose value contains only 1's
                        binString = binString.substring(field.getFrom(), field.getTo() + 1);
                        if (binString.length() <= 4 || binString.contains("0")) {
                            int val;
                            if (field.isSigned() && binString.startsWith("1")) {
                                // ugly :-(
                                val = Integer.parseInt("-" + binString.replace('0', 'q').replace('1','0').replace('q','1'), 2) - 1;
                            } else {
                                val = Integer.parseInt("0" + binString, 2);
                            }
                            //MainActivity.debug("Value of " + field.getHexId() + "." + field.getResponseId() + "." + field.getFrom()+" = "+val);
                            //MainActivity.debug("Fields: onMessageCompleteEvent > "+field.getSID()+" = "+val);
                            field.setValue(val);
                            // update the fields last request date
                            field.updateLastRequest();
                            // do field logging
                            if(MainActivity.fieldLogMode)
                                FieldLogger.getInstance().log(field.getSID()+","+val);
                        } else {
                            field.setValue(Double.NaN);
                        }
/*
                        int val = Integer.parseInt("0" + binString.substring(field.getFrom(), field.getTo() + 1), 2);
                        //MainActivity.debug("Value of " + field.getHexId() + "." + field.getResponseId() + "." + field.getFrom()+" = "+val);
                        //MainActivity.debug("Fields: onMessageCompleteEvent > "+field.getSID()+" = "+val);
                        field.setValue(val);
                        // update the fields last request date
                        field.updateLastRequest();
*/
                    } catch (Exception e)
                    {
                        // ignore
                    }
                }
            }
        }
    }


    /*
    @Override
    public void onMessageCompleteEvent(int msgId, String msgData, String responseId) {
        for(int i=0; i< fields.size(); i++)
        {
            Field field = fields.get(i);

            if(field.getId()== msgId &&
                    (
                            responseId==null
                            ||
                                    responseId.trim().equals(field.getResponseId().trim())
                    ))
            {
                String binString = "";
                for(int j=0; j<msgData.length(); j+=2)
                {
                    binString += String.format("%8s", Integer.toBinaryString(Integer.parseInt(msgData.substring(j,j+2),16) & 0xFF)).replace(' ', '0');
                }

                if(binString.length()>= field.getTo()) {
                    // parseInt --> signed, so the first bit is "cut-off"!
                    try {
                        int val = Integer.parseInt("0" + binString.substring(field.getFrom(), field.getTo() + 1), 2);
                        //MainActivity.debug("Fields: onMessageCompleteEvent > "+field.getSID()+" = "+val);
                        field.setValue(val);
                    } catch (Exception e)
                    {
                        // ignore
                    }
                }
            }
        }
    }
    */

    public void add(Field field) {
        fields.add(field);
        fieldsBySid.put(field.getCar()+"."+field.getSID(),field);
    }
/*
    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }
*/
    public void notifyAllFieldListeners()
    {
        for(int i=0; i< fields.size(); i++) {
            fields.get(i).notifyFieldListeners();
        }
    }

    public void clearAllFields()
    {
        for(int i=0; i< fields.size(); i++) {
            fields.get(i).setValue(0);
        }
    }

    /* --------------------------------
     * Tests ...
     \ ------------------------------ */
    
    public static void main(String[] args)
    {
        
    }
    
}
