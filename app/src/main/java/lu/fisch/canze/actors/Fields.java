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
import lu.fisch.canze.interfaces.MessageListener;

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

    // next version we can condense the init string to 11 fields
    // need to redefine the CSV tab and at the same time modify the init code here

/*
    private static final int FIELD_ID           = 0;
    private static final int FIELD_FROM         = 1;    
    private static final int FIELD_TO           = 2;    
    private static final int FIELD_DIVIDER      = 3; // should be pre-calculated in the spreadsheet to resolution and folden in one
    private static final int FIELD_MULTIPLIER   = 4; // should be pre-calculated in the spreadsheet to resolution and folden in one
    private static final int FIELD_OFFSET       = 5;    
    private static final int FIELD_DECIMALS     = 6;
    private static final int FIELD_FORMAT       = 7;
    private static final int FIELD_UNIT         = 8;
    private static final int FIELD_REQUEST_ID   = 9;
    private static final int FIELD_RESPONSE_ID  = 10;
    private static final int FIELD_DESCRIPTION  = 11;
    private static final int FIELD_CAR          = 12;
    private static final int FIELD_SKIPS        = 13;
    private static final int FIELD_FREQ         = 14; // not needed anymore
*/

    private static final int FIELD_ID           = 0;
    private static final int FIELD_FROM         = 1;
    private static final int FIELD_TO           = 2;
    private static final int FIELD_RESOLUTION   = 3;
    private static final int FIELD_OFFSET       = 4;
    private static final int FIELD_DECIMALS     = 5;
    private static final int FIELD_UNIT         = 6;
    private static final int FIELD_REQUEST_ID   = 7;
    private static final int FIELD_RESPONSE_ID  = 8;
    private static final int FIELD_CAR          = 9;


    public static final int CAR_ANY             = 0;
    public static final int CAR_FLUENCE         = 1;
    public static final int CAR_ZOE             = 2;
    public static final int CAR_KANGOO          = 3;
    public static final int CAR_TWIZY           = 4;    // you'll never know ;-)
    public static final int CAR_X10             = 5;

    public static final int TOAST_NONE          = 0;
    public static final int TOAST_DEVICE        = 1;
    public static final int TOAST_ALL           = 2;

    private final ArrayList<Field> fields = new ArrayList<>();
    private final HashMap<String, Field> fieldsBySid = new HashMap<>();

    private static Fields instance = null;

    private int car = CAR_ANY;

    private Fields() {
        fillStatic();
    }

    public static Fields getInstance()
    {
        if(instance==null) instance=new Fields();
        return instance;
    }

    private void fillStatic()
    {
        String fieldDef = // startBit, endBit, divider, multiplier, offset, decimals, format, requestID, responseID
                ""
/*
                // 2015.11.07
                        +"0x0c6, 0, 15, 1, 1, 0x8000, 1, Steering pos:%5ld, °, , , Steering Position, 0, 0, 10\n"
                        +"0x0c6, 16, 31, 1, 1, 0x8000, 1, Steering ac: %5ld, °/s, , , Steering Acceleration, 0, 0, 10\n"
                        +"0x0c6, 32, 47, 1, 1, 0x8000, 1, Steering offset %5ld, °, , , SteeringWheelAngle_Offset, 0, 0, 10\n"
                        +"0x0c6, 48, 50, 1, 1, 0, 0, SwaStatus %1ld, , , , SwaSensorInternalStatus, 0, 0, 10\n"
                        +"0x0c6, 51, 54, 1, 1, 0, 0, SwaClock %2ld, , , , SwaClock, 0, 0, 10\n"
                        +"0x0c6, 56, 53, 1, 1, 0, 0, , , , , SwaChecksum, 0, 0, 10\n"
                        +"0x12e, 0, 7, 1, 1, 198, 0, Accel F/R: %4ld, , , , LongitudinalAccelerationProc, 0, 0, 10\n"
                        +"0x12e, 24, 35, 10, 1, 2047, 1, Yaw rate: %4ld, deg/s, , , Yaw rate, 0, 0, 10\n"
                        +"0x12e, 8, 23, 1, 1, 0x8000, 0, Accel L/R: %4ld, , , , TransversalAcceleration, 0, 0, 10\n"
                        +"0x130, 11, 12, 1, 1, 0, 0, HBB_Malfunction %1ld, , , , HBB_Malfunction, 0, 0, 10\n"
                        +"0x130, 16, 17, 1, 1, 0, 0, EB_Malfunction %ld, , , , EB_Malfunction, 0, 0, 10\n"
                        +"0x130, 18, 19, 1, 1, 0, 0, EB_inProgress %1ld, , , , EB_inProgress, 0, 0, 10\n"
                        +"0x130, 20, 31, 1, 1, 4094, 0, ElBrkWhTqReq %4d , Nm, , , ElecBrakeWheelsTorqueRequest, 0, 0, 10\n"
                        +"0x130, 32, 38, 1, 1, 0, 0, BrakePedalDriverWill %3ld, %, , , BrakePedalDriverWill, 0, 0, 10\n"
                        +"0x130, 40, 41, 1, 1, 0, 0, HBA_ActivationRequest %1ld, , , , HBA_ActivationRequest, 0, 0, 10\n"
                        +"0x130, 42, 43, 1, 1, 0, 0, PressureBuildUp %1ld, , , , PressureBuildUp, 0, 0, 10\n"
                        +"0x130, 44, 55, 1, -3, 4094, 0, DriverBrakeWheelTq_Req %4ld, Nm, , , DriverBrakeWheelTq_Req , 0, 0, 10\n"
                        +"0x130, 56, 63, 1, 1, 0, 0, CheckSum_UBP %3ld, , , , CheckSum_UBP, 0, 0, 10\n"
                        +"0x130, 8, 10, 1, 1, 0, 0, UBP_Clock %1ld, , , , UBP_Clock, 0, 0, 10\n"
                        +"0x17e, 40, 41, 1, 1, 0, 0, , , , , CrankingAuthorisation_AT, 0, 0, 10\n"
                        +"0x17e, 48, 51, 1, 1, 0, 0, Gear: %4ld, , , , GearLeverPosition, 0, 0, 10\n"
                        +"0x186, 0, 15, 1000, 125, 0, 2, Engine RPM: %3ld.%02ld, rpm, , , Speed, 0, 0, 10\n"
                        +"0x186, 16, 27, 10, 5, 800, 1, Mean torque: %4ld, Nm, , , MeanEffectiveTorque, 0, 0, 10\n"
                        +"0x186, 28, 39, 10, 5, 800, 0, Req torque: %4ld, Nm, , , RequestedTorqueAfterProc, 0, 0, 10\n"
                        +"0x186, 40, 49, 10, 1, 0, 1, Pedal: %3ld.%01ld, %, , , Throttle, 0, 0, 10\n"
                        +"0x18a, 0, 11, 1, 1, 0, 0, RawEngineTorque: %4ld, %, , , RawEngineTorque_WithoutTMReq, 0, 0, 10\n"
                        +"0x18a, 12, 12, 1, 1, 0, 0, torq ack %1ld, , , , AT_TorqueAcknowledgement, 0, 0, 10\n"
                        +"0x18a, 13, 14, 1, 1, 0, 0, cc status %1ld, , , , CruiseControlStatus_forTM, 0, 0, 10\n"
                        +"0x18a, 16, 25, 800, 100, 0, 2, PowerTrainSetPoint: %4ld, %, , , Throttle, 0, 0, 10\n"
                        +"0x18a, 26, 26, 1, 1, 0, 0, Kickdown: %1ld, , , , KickDownActivated, 0, 0, 10\n"
                        +"0x18a, 27, 38, 10, 5, 800, 1, FrictionTorque: %4ld, Nm, , , FrictionTorque, 0, 0, 10\n"
                        +"0x1f6, 20, 20, 10, 5, 0, 1, Break pedal: %4ld, , , , Break Pedal, 0, 0, 10\n"
                        +"0x1f8, 0, 7, 1, 1, 0, 0, , , , , Checksum EVC, 0, 0, 10\n"
                        +"0x1f8, 12, 13, 1, 1, 0, 0, , , , , EVCReadyAsActuator, 0, 0, 10\n"
                        +"0x1f8, 16, 27, 1, 1, 2048, 0, , Nm, , , TotalPotentialResistiveWheelsTorque, 0, 0, 10\n"
                        +"0x1f8, 28, 39, 1, -1, 4096, 0, , Nm, , , ElecBrakeWheelsTorqueApplied, 0, 0, 10\n"
                        +"0x1f8, 40, 50, 1, 10, 0, 0, , Rpm, , , ElecEngineRPM, 0, 0, 10\n"
                        +"0x1f8, 52, 54, 1, 1, 0, 0, , , , , EVC_Clock, 0, 0, 10\n"
                        +"0x1f8, 56, 58, 1, 1, 0, 0, , , , , GearRangeEngagedCurrent, 0, 0, 10\n"
                        +"0x1f8, 60, 61, 1, 1, 0, 0, , , , , UBPRequestAckbyEVC, 0, 0, 10\n"
                        +"0x1f8, 62, 63, 1, 1, 0, 0, , , , , DeclutchInProgress, 0, 0, 10\n"
                        +"0x1fd, 0, 7, 10, 5, 0, 1, Amp 12V: %2ld.%01ld, A, , , 12V Battery Current, 0, 0, 100\n"
                        +"0x1fd, 48, 55, 1, 1, 0x50, 0, KwDash: %4ld, kW, , , Consumption, 0, 0, 100\n"
                        +"0x29a, 0, 15, 1, 1, 0, 0, Speed FR: %5ld, , , , Speed Front Right, 0, 0, 20\n"
                        +"0x29a, 16, 31, 1, 1, 0, 0, Speed FL: %5ld, , , , Speed Front Left, 0, 0, 20\n"
                        +"0x29a, 32, 47, 100, 1, 0, 2, , , , , , 0, 0, 20\n"
                        +"0x29c, 0, 15, 1, 1, 0, 0, Speed RR: %5ld, , , , Speed Rear Right, 0, 0, 20\n"
                        +"0x29c, 16, 31, 1, 1, 0, 0, Speed RL: %5ld, , , , Speed Rear Left, 0, 0, 20\n"
                        +"0x29c, 48, 63, 100, 1, 0, 2, Speed R100: %3ld.%02ld, , , , , 0, 0, 20\n"
                        +"0x352, 0, 1, 1, 1, 0, 0, ABS warn req %1ld, , , , ABS Warning Request, 0, 0, 40\n"
                        +"0x352, 2, 3, 1, 1, 0, 0, StopLReq %1ld, , , , ESP_StopLampRequest, 0, 0, 40\n"
                        +"0x352, 24, 31, 1, 1, 0, 0, Brake pressure %3ld, , , , Break pressure, 0, 0, 40\n"
                        +"0x35c, 16, 39, 1, 1, 0, 0, Minutes: %7ld, min, , , AbsoluteTimeSince1rstIgnition, 0, 0, 100\n"
                        +"0x35c, 4, 15, 1, 1, 0, 0, Key-Start: %5ld, , , , , 0, 0, 100\n"
                        +"0x3f7, 2, 3, 1, 1, 0, 0, Gear??: %4ld, , , , , 2, 0, 60\n"
                        +"0x427, 0, 1, 1, 1, 0, 0, , , , , HVConnectionStatus, 0, 0, 100\n"
                        +"0x427, 2, 3, 1, 1, 0, 0, , , , , ChargingAlert, 0, 0, 100\n"
                        +"0x427, 26, 28, 1, 1, 0, 0, , , , , PreHeatingProgress, 0, 0, 100\n"
                        +"0x427, 4, 5, 1, 1, 0, 0, , , , , HVBatteryLocked, 0, 0, 100\n"
                        +"0x427, 40, 47, 10, 3, 0, 0, , kW, , , AvailableChargingPower, 2, 0, 100\n"
                        +"0x427, 49, 57, 10, 1, 0, 1, , kWh, , , AvailableEnergy, 0, 0, 100\n"
                        +"0x427, 58, 58, 1, 1, 0, 0, , , , , ChargeAvailable, 0, 0, 100\n"
                        +"0x42a, 0, 0, 1, 1, 0, 0, Temp set: %5ld, , , , PreHeatingRequest, 0, 0, 100\n"
                        +"0x42a, 24, 29, 1, 1, 0, 0, , %, , , ClimAirFlow, 0, 0, 100\n"
                        +"0x42a, 30, 39, 10, 1, 40, 1, , C, , , EvaporatorTempMeasure, 0, 0, 100\n"
                        +"0x42a, 45, 46, 1, 1, 0, 0, , , , , ImmediatePreheatingAuthorizationStatus, 0, 0, 100\n"
                        +"0x42a, 48, 49, 1, 1, 0, 0, , , , , ClimLoopMode, 0, 0, 100\n"
                        +"0x42a, 51, 52, 1, 1, 0, 0, , , , , PTCActivationRequest, 0, 0, 100\n"
                        +"0x42a, 56, 60, 1, 5, 0, 0, , %, , , EngineFanSpeedRequestPWM, 0, 0, 100\n"
                        +"0x42a, 6, 15, 10, 1, 40, 1, , C, , , EvaporatorTempSetPoint, 0, 0, 100\n"
                        +"0x42e, 0, 12, 100, 2, 0, 2, SOC(a): %3ld.%02ld, %, , , State of Charge, 0, 0, 100\n"
                        +"0x42e, 18, 19, 1, 1, 0, 0, , , , , HVBatLevel2Failure, 0, 0, 100\n"
                        +"0x42e, 20, 24, 1, 5, 0, 0, EngineFanSpeed %3ld, %, , , EngineFanSpeed, 0, 0, 100\n"
                        +"0x42e, 25, 34, 2, 1, 0, 0, DC V: %3ld.%02ld, V, , , HVNetworkVoltage, 0, 0, 100\n"
                        +"0x42e, 38, 43, 1, 1, 0, 1, AC pilot current: %3ld, A, , , Charging Pilot Current, 0, 0, 100\n"
                        +"0x42e, 44, 50, 1, 1, 40, 0, HVBatteryTemp, C, , , HVBatteryTemp, 0, 0, 100\n"
                        +"0x42e, 56, 63, 10, 3, 0, 1, ChargingPower, kW, , , ChargingPower, 0, 0, 100\n"
                        +"0x4f8, 0, 1, 1, -1, -2, 0, Start: %4ld, , , , , 0, 0, 100\n"
                        +"0x4f8, 24, 39, 100, 1, 0, 2, Speed(d): %3ld.%02ld, , , , Speed on Display, 2, 0, 100\n"
                        +"0x4f8, 4, 5, 1, -1, -2, 0, Park.break: %1ld, , , , Parking Break, 0, 0, 100\n"
                        +"0x5d7, 0, 15, 100, 1, 0, 2, Speed(a): %3ld.%02ld, km/h, , , Speed, 0, 0, 100\n"
                        +"0x5d7, 16, 43, 100, 1, 0, 2, Odo: %5ld.%02ld, km, , , Odometer, 0, 0, 100\n"
                        +"0x5d7, 44, 45, 1, 1, 0, 0, WheelsLockingState %1ld, ?, , , WheelsLockingState, 0, 0, 100\n"
                        +"0x5d7, 48, 49, 1, 1, 0, 0, VehicleSpeedSign %1ld, ?, , , VehicleSpeedSign, 0, 0, 100\n"
                        +"0x5d7, 50, 54, 100, 4, 0, 2, Fine dist: %5ld.%02ld, cm, , , , 0, 0, 100\n"
                        +"0x5de, 1, 1, 1, 1, 0, 0, Right: %5ld, , , , Right Indicator, 0, 0, 100\n"
                        +"0x5de, 12, 12, 1, 1, 0, 0, FL door open: %1ld, , , , Door Front Left, 0, 0, 100\n"
                        +"0x5de, 14, 14, 1, 1, 0, 0, FR door open: %1ld, , , , Dort Front Right, 0, 0, 100\n"
                        +"0x5de, 17, 17, 1, 1, 0, 0, RL door open: %1ld, , , , Door Rear Left, 0, 0, 100\n"
                        +"0x5de, 19, 19, 1, 1, 0, 0, RR door open: %1ld, , , , Door Rear Right, 0, 0, 100\n"
                        +"0x5de, 2, 2, 1, 1, 0, 0, Left: %5ld, , , , Left Indicator, 0, 0, 100\n"
                        +"0x5de, 5, 5, 1, 1, 0, 0, Park light: %5ld, , , , Park Light, 0, 0, 100\n"
                        +"0x5de, 59, 59, 1, 1, 0, 0, Hatch door open: %1ld, , , , Door Hatch, 0, 0, 100\n"
                        +"0x5de, 6, 6, 1, 1, 0, 0, Head light: %5ld, , , , Head Light, 0, 0, 100\n"
                        +"0x5de, 7, 7, 1, 1, 0, 0, Beam light: %5ld, , , , Beam Light, 0, 0, 100\n"
                        +"0x5ee, 0, 0, 1, 1, 0, 0, Park light: %5ld, , , , Park Light, 0, 0, 100\n"
                        +"0x5ee, 1, 1, 1, 1, 0, 0, Head light: %5ld, , , , Head Light, 0, 0, 100\n"
                        +"0x5ee, 16, 19, 1, 1, 0, 0, Door locks %4ld, , , , Door Locks, 0, 0, 100\n"
                        +"0x5ee, 2, 2, 1, 1, 0, 0, Beam light: %5ld, , , , Beam Light, 0, 0, 100\n"
                        +"0x5ee, 20, 24, 1, 1, 0, 0, Flashers %4ld, , , , Indicators, 0, 0, 100\n"
                        +"0x5ee, 24, 27, 1, 1, 0, 0, Doors %4ld, , , , Doors, 0, 0, 100\n"
                        +"0x646, 16, 32, 10, 1, 0, 1, trB dist %5ld.%01ld, km, , , , 0, 0, 500\n"
                        +"0x646, 33, 47, 10, 1, 0, 1, trB cons %5ld.%01ld, kWh, , , , 0, 0, 500\n"
                        +"0x646, 48, 59, 10, 1, 0, 1, avg trB spd %5ld.%01ld, km/h, , , , 0, 0, 500\n"
                        +"0x646, 8, 15, 10, 1, 0, 1, avg trB cons %2ld.%01ld, kWh/100km, , , , 0, 0, 500\n"
                        +"0x653, 9, 9, 1, 1, 0, 0, dr seatbelt %1ld, , , , , 0, 0, 100\n"
                        +"0x654, 2, 2, 1, 1, 0, 0, ChargingPlugConnected, , , , ChargingPlugConnected, 0, 0, 500\n"
                        +"0x654, 25, 31, 1, 1, 0, 0, SOC(b): %4ld, , , , State of Charge, 0, 0, 500\n"
                        +"0x654, 3, 3, 1, 1, 0, 0, DriverWalkAwayEngineON, , , , DriverWalkAwayEngineON, 0, 0, 500\n"
                        +"0x654, 32, 41, 1, 1, 0, 0, Time to full %4ld, min, , , Time to Full, 0, 0, 500\n"
                        +"0x654, 4, 4, 1, 1, 0, 0, HVBatteryUnballastAlert, , , , HVBatteryUnballastAlert, 0, 0, 500\n"
                        +"0x654, 42, 51, 1, 1, 0, 0, Km avail: %4ld, km, , , Available Distance, 0, 0, 500\n"
                        +"0x654, 52, 61, 10, 1, 0, 1, kw/100Km %2ld.%01ld, , , , AverageConsumption, 0, 0, 500\n"
                        +"0x654, 62, 62, 1, 1, 0, 0, HVBatteryLow %1ld, , , , HVBatteryLow, 0, 0, 500\n"
                        +"0x658, 0, 31, 1, 1, 0, 0, S# batt:%10ld, , , , Battery Serial N°, 0, 0, 3000\n"
                        +"0x658, 33, 39, 1, 1, 0, 0, Bat health %4ld, %, , , Battery Health, 0, 0, 3000\n"
                        +"0x658, 42, 42, 1, 1, 0, 0, Charging %1ld, , , , Charging, 0, 0, 3000\n"
                        +"0x65b, 0, 10, 1, 1, 0, 0, Sched min1 %3ld, min, , , Schedule timer 1 min, 0, 0, 100\n"
                        +"0x65b, 12, 22, 1, 1, 0, 0, Sched min2 %3ld, min, , , Schedule timer 2 min, 0, 0, 100\n"
                        +"0x65b, 24, 30, 1, 1, 0, 0, FluentDrivingIndicator, %, , , Fluent driver, 0, 0, 100\n"
                        +"0x65b, 25, 26, 1, 1, 0, 0, ECO Mode: %1ld, , , , Economy Mode, 0, 0, 100\n"
                        +"0x65b, 33, 34, 1, 1, 0, 0, EcoModeStatusDisplay, , , , Economy Mode displayed, 0, 0, 100\n"
                        +"0x65b, 39, 40, 1, 1, 0, 0, ModeEcoIncitationPrompting , , , , Consider eco mode, 0, 0, 100\n"
                        +"0x65b, 41, 43, 1, 1, 0, 0, ChargingStatusDisplay, , , , , 0, 0, 100\n"
                        +"0x65b, 44, 45, 1, 1, 0, 0, ParkPositionRequestedDisplay, , , , Set park for charging, 0, 0, 100\n"
                        +"0x66a, 42, 42, 1, 1, 0, 0, CruisC ? %4ld, , , , , 0, 0, 100\n"
                        +"0x66a, 5, 7, 1, 1, 0, 0, CruisC mode %4ld, , , , Cruise Control Mode, 0, 0, 100\n"
                        +"0x66a, 8, 15, 1, 1, 0, 0, CruisC spd %4ld, km/h, , , Cruise Control Speed, 0, 0, 100\n"
                        +"0x68b, 0, 3, 1, 1, 0, 0, # presses MM %2ld, , , , , 0, 0, 100\n"
                        +"0x699, 10, 14, 2, 1, 0, 0, Clima temp %4ld, C, , , , 2, 0, 1000\n"
                        +"0x699, 16, 16, 1, 1, 0, 0, Clima wshld %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 18, 18, 1, 1, 0, 0, Clima face %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 19, 19, 1, 1, 0, 0, Clima feet %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 2, 3, 1, 1, 0, 0, Clima rear dfr %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 20, 21, 1, 1, 0, 0, Clima recycling %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 22, 23, 1, 1, 0, 0, ECO mode %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 24, 27, 1, 1, 0, 0, Clima fan %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 28, 31, 1, 1, 0, 0, Clima chg %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 4, 4, 1, -1, -1, 0, Clima auto %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 5, 5, 1, 1, 0, 0, Clima Maxdfr %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 52, 53, 1, 1, 0, 0, Clima AUTO %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 54, 55, 1, 1, 0, 0, Clima AC %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 56, 56, 1, 1, 0, 0, Clima fan %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 6, 6, 1, 1, 0, 0, Clima autofan %4ld, , , , , 2, 0, 1000\n"
                        +"0x69f, 0, 31, 1, 1, 0, 0, S# car: %10ld, , , , Car Serial N°, 0, 0, 1000\n"
                        +"0x6f8, 16, 23, 16, 100, 0, 2, Bat12: %2ld.%02ld, V, , , 12V Battery Voltage, 0, 0, 100\n"
                        +"0x760, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x760, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x760, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x760, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x760, 24, 31, 1, 1, 0, 0, Mas cyl pr %3ld, bar, 0x224b0e, 0x624b0e, , 2, 0, 0\n"
                        +"0x762, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x762, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x762, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x762, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        + "0x762, 24, 39, 256, 100, 100, 0, Bat12: %2ld.%02ld, V, 0x22012f, 0x62012f, 12V Battery Voltage, 0, 0, 0\n"
                        +"0x763, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x763, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x763, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x763, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x763, 2, 2, 1, 1, 0, 0, mute %1ld, , 0x2220f0, 0x6220f0, , 0, 0, 0\n"
                        +"0x763, 24, 31, 1, 1, 0, 0, Parking brake %1ld, , 0x222001, 0x622001, Parking Break, 0, 0, 0\n"
                        +"0x763, 3, 3, 1, 1, 0, 0, vol+ %1ld, , 0x2220f0, 0x6220f0, << UDS replies start at bit 24 - need to recheck, 0, 0, 0\n"
                        +"0x763, 4, 4, 1, 1, 0, 0, vol- %1ld, , 0x2220f0, 0x6220f0, , 0, 0, 0\n"
                        +"0x763, 5, 5, 1, 1, 0, 0, media %1ld, , 0x2220f0, 0x6220f0, , 0, 0, 0\n"
                        +"0x763, 6, 6, 1, 1, 0, 0, radio %1ld, , 0x2220f0, 0x6220f0, , 0, 0, 0\n"
                        +"0x764, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x764, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x764, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x764, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x765, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x765, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x765, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x765, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x76d, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x76d, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x76d, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x76d, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x76e, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x76e, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x76e, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x76e, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x772, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x772, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x772, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x772, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x77e, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x77e, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x77e, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x77e, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x77e, 24, 31, 1, 1, 0, 0, dcdc state %2ld, , 0x22300f, 0x62300f, , 0, 0, 0\n"
                        +"0x77e, 24, 31, 16, 100, 0, 2, Current %3ld.%02ld, A, 0x22301d, 0x62301d, , 0, 0, 0\n"
                        +"0x77e, 24, 31, 32, 1, 0, 0, Trq app. %5ld, Nm, 0x223025, 0x623025, , 0, 0, 0\n"
                        +"0x77e, 24, 31, 32, 1, 0, 0, Trq req. %5ld, Nm, 0x223024, 0x623024, , 0, 0, 0\n"
                        +"0x77e, 24, 31, 32, 1000, 0, 3, Batt volt %3ld.%03ld, V, 0x22300e, 0x62300e, Battery Voltage, 0, 0, 0\n"
                        +"0x77e, 24, 31, 64, 100, 0, 2, Inv temp %2ld.%02ld, C, 0x22302b, 0x62302b, , 0, 0, 0\n"
                        +"0x793, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x793, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x793, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x793, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7b6, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x7b6, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x7b6, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7b6, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7bb, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x7bb, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x7bb, 104, 111, 1, 1, 0, 0, T 4: %4ld, C, 0x2104, 0x6104, Cell 4 Temperature, 1, 10, 0\n"
                        +"0x7bb, 104, 111, 1, 1, 40, 0, T 4: %4ld, C, 0x2104, 0x6104, Cell 4 Temperature, 2, 10, 0\n"
                        +"0x7bb, 104, 119, 1, 1, 0, 0, Bat km %5ld, km, 0x2161, 0x6161, , 1, 0, 0\n"
                        +"0x7bb, 112, 127, 1, 1, 0, 0, Raw t c05: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 112, 127, 1000, 1, 0, 3, Cell 07 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 112, 127, 1000, 1, 0, 3, Cell 69 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 128, 135, 1, 1, 40, 0, T 5: %4ld, C, 0x2104, 0x6104, Cell 5 Temperature, 2, 10, 0\n"
                        +"0x7bb, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7bb, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7bb, 128, 143, 1000, 1, 0, 3, Cell 08 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 128, 143, 1000, 1, 0, 3, Cell 70 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 136, 151, 1, 1, 0, 0, Bat kWh %5ld, kWh, 0x2161, 0x6161, , 1, 0, 0\n"
                        +"0x7bb, 136, 151, 1, 1, 0, 0, Raw t c06: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 144, 159, 1000, 1, 0, 3, Cell 09 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 144, 159, 1000, 1, 0, 3, Cell 71 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 152, 159, 1, 1, 40, 0, T 6: %4ld, C, 0x2104, 0x6104, Cell 6 Temperature, 2, 10, 0\n"
                        +"0x7bb, 16, 31, 1, 1, 0, 0, Raw t c01: %4ld, unknown, 0x2104, 0x6104, , 1, 10, 0\n"
                        +"0x7bb, 16, 31, 1, 1, 0, 0, Raw t c01: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 16, 31, 1000, 1, 0, 3, Cell 01 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 16, 31, 1000, 1, 0, 3, Cell 63 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 160, 175, 1, 1, 0, 0, Raw t c07: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 160, 175, 1000, 1, 0, 3, Cell 10 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 160, 175, 1000, 1, 0, 3, Cell 72 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 176, 183, 1, 1, 40, 0, T 7: %4ld, C, 0x2104, 0x6104, Cell 7 Temperature, 2, 10, 0\n"
                        +"0x7bb, 176, 191, 1000, 1, 0, 3, Cell 11 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 176, 191, 1000, 1, 0, 3, Cell 73 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 184, 199, 1, 1, 0, 0, Raw t c08: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 192, 207, 100, 1, 0, 2, Max batt in pw:%2ld.%02ld, kW, 0x2101, 0x6101, , 1, 0, 0\n"
                        +"0x7bb, 192, 207, 1000, 1, 0, 3, Cell 12 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 192, 207, 1000, 1, 0, 3, Cell 74 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 200, 207, 1, 1, 40, 0, T 8: %4ld, C, 0x2104, 0x6104, Cell 8 Temperature, 2, 10, 0\n"
                        +"0x7bb, 208, 223, 1, 1, 0, 0, Raw t c09: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 208, 223, 100, 1, 0, 2, Max batt out pw:%2ld.%02ld, kW, 0x2101, 0x6101, , 1, 0, 0\n"
                        +"0x7bb, 208, 223, 1000, 1, 0, 3, Cell 13 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 208, 223, 1000, 1, 0, 3, Cell 75 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 224, 231, 1, 1, 40, 0, T 9: %4ld, C, 0x2104, 0x6104, Cell 9 Temperature, 2, 10, 0\n"
                        +"0x7bb, 224, 239, 1000, 1, 0, 3, Cell 14 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 224, 239, 1000, 1, 0, 3, Cell 76 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 232, 247, 1, 1, 0, 0, Raw t c10: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 240, 255, 1000, 1, 0, 3, Cell 15 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 240, 255, 1000, 1, 0, 3, Cell 77 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 248, 255, 1, 1, 40, 0, T 10: %4ld, C, 0x2104, 0x6104, Cell 10 Temperature, 2, 10, 0\n"
                        +"0x7bb, 256, 271, 1, 1, 0, 0, Raw t c11: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 256, 271, 1000, 1, 0, 3, Cell 16 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 256, 271, 1000, 1, 0, 3, Cell 78 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 272, 279, 1, 1, 40, 0, T 11: %4ld, C, 0x2104, 0x6104, Cell 11 Temperature, 2, 10, 0\n"
                        +"0x7bb, 272, 287, 1000, 1, 0, 3, Cell 17 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 272, 287, 1000, 1, 0, 3, Cell 79 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 280, 295, 1, 1, 0, 0, Raw t c12: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 288, 303, 1000, 1, 0, 3, Cell 18 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 288, 303, 1000, 1, 0, 3, Cell 80 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 296, 303, 1, 1, 40, 0, T 12: %4ld, C, 0x2104, 0x6104, Cell 12 Temperature, 2, 10, 0\n"
                        +"0x7bb, 304, 319, 1000, 1, 0, 3, Cell 19 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 304, 319, 1000, 1, 0, 3, Cell 81 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 316, 335, 10000, 1, 0, 4, SOC(real) %2ld.%04ld, %, 0x2101, 0x6101, Real State of Charge, 1, 0, 0\n"
                        +"0x7bb, 32, 39, 1, 1, 0, 0, T 1: %4ld, C, 0x2104, 0x6104, Cell 1 Temperature, 1, 10, 0\n"
                        +"0x7bb, 32, 39, 1, 1, 40, 0, T 1: %4ld, C, 0x2104, 0x6104, Cell 1 Temperature, 2, 10, 0\n"
                        +"0x7bb, 32, 47, 1000, 1, 0, 3, Cell 02 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 32, 47, 1000, 1, 0, 3, Cell 64 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 320, 335, 1000, 1, 0, 3, Cell 20 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 320, 335, 1000, 1, 0, 3, Cell 82 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 336, 351, 100, 1, 0, 2, Max batt in pw:%2ld.%02ld, kW, 0x2101, 0x6101, , 2, 0, 0\n"
                        +"0x7bb, 336, 351, 1000, 1, 0, 3, Cell 21 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 336, 351, 1000, 1, 0, 3, Cell 83 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 348, 367, 10000, 1, 0, 4, Bat Ah %2ld.%04ld, Ah, 0x2101, 0x6101, , 1, 0, 0\n"
                        +"0x7bb, 352, 367, 1000, 1, 0, 3, Cell 22 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 352, 367, 1000, 1, 0, 3, Cell 84 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 368, 383, 1000, 1, 0, 3, Cell 23 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 368, 383, 1000, 1, 0, 3, Cell 85 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 384, 399, 1000, 1, 0, 3, Cell 24 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 384, 399, 1000, 1, 0, 3, Cell 86 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 40, 55, 1, 1, 0, 0, Raw t c02: %4ld, unknown, 0x2104, 0x6104, , 1, 10, 0\n"
                        +"0x7bb, 40, 55, 1, 1, 0, 0, Raw t c02: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 400, 415, 1000, 1, 0, 3, Cell 25 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 400, 415, 1000, 1, 0, 3, Cell 87 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 416, 431, 1000, 1, 0, 3, Cell 26 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 416, 431, 1000, 1, 0, 3, Cell 88 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 432, 447, 1000, 1, 0, 3, Cell 27 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 432, 447, 1000, 1, 0, 3, Cell 89 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 448, 463, 1000, 1, 0, 3, Cell 28 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 448, 463, 1000, 1, 0, 3, Cell 90 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 464, 479, 1000, 1, 0, 3, Cell 29 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 464, 479, 1000, 1, 0, 3, Cell 91 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 48, 63, 1000, 1, 0, 3, Cell 03 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 48, 63, 1000, 1, 0, 3, Cell 65 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 480, 495, 1000, 1, 0, 3, Cell 30 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 480, 495, 1000, 1, 0, 3, Cell 92 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 496, 511, 1000, 1, 0, 3, Cell 31 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 496, 511, 1000, 1, 0, 3, Cell 93 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 512, 527, 1000, 1, 0, 3, Cell 32 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 512, 527, 1000, 1, 0, 3, Cell 94 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 528, 543, 1000, 1, 0, 3, Cell 33 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 528, 543, 1000, 1, 0, 3, Cell 95 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 544, 559, 1000, 1, 0, 3, Cell 34 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 544, 559, 1000, 1, 0, 3, Cell 96 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 56, 63, 1, 1, 0, 0, T 2: %4ld, C, 0x2104, 0x6104, Cell 2 Temperature, 1, 10, 0\n"
                        +"0x7bb, 56, 63, 1, 1, 40, 0, T 2: %4ld, C, 0x2104, 0x6104, Cell 2 Temperature, 2, 10, 0\n"
                        +"0x7bb, 560, 575, 1000, 1, 0, 3, Cell 35 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 576, 591, 1000, 1, 0, 3, Cell 36 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 592, 607, 1000, 1, 0, 3, Cell 37 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 60, 79, 10000, 1, 0, 4, Bat Ah %2ld.%04ld, Ah, 0x2161, 0x6161, , 1, 0, 0\n"
                        +"0x7bb, 608, 623, 1000, 1, 0, 3, Cell 38 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 624, 639, 1000, 1, 0, 3, Cell 39 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 64, 79, 1, 1, 0, 0, Raw t c03: %4ld, unknown, 0x2104, 0x6104, , 1, 10, 0\n"
                        +"0x7bb, 64, 79, 1, 1, 0, 0, Raw t c03: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 64, 79, 1000, 1, 0, 3, Cell 04 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 64, 79, 1000, 1, 0, 3, Cell 66 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 640, 655, 1000, 1, 0, 3, Cell 40 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 656, 671, 1000, 1, 0, 3, Cell 41 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 672, 687, 1000, 1, 0, 3, Cell 42 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 688, 703, 1000, 1, 0, 3, Cell 43 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 704, 719, 1000, 1, 0, 3, Cell 44 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 720, 735, 1000, 1, 0, 3, Cell 45 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 736, 751, 1000, 1, 0, 3, Cell 46 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 752, 767, 1000, 1, 0, 3, Cell 47 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 768, 783, 1000, 1, 0, 3, Cell 48 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 784, 799, 1000, 1, 0, 3, Cell 49 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 80, 87, 1, 1, 0, 0, T 3: %4ld, C, 0x2104, 0x6104, Cell 3 Temperature, 1, 10, 0\n"
                        +"0x7bb, 80, 87, 1, 1, 40, 0, T 3: %4ld, C, 0x2104, 0x6104, Cell 3 Temperature, 2, 10, 0\n"
                        +"0x7bb, 80, 87, 20, 1, 0, 2, Bat SOH%3ld.%01ld, %, 0x2161, 0x6161, , 1, 0, 0\n"
                        +"0x7bb, 80, 95, 1000, 1, 0, 3, Cell 05 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 80, 95, 1000, 1, 0, 3, Cell 67 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 800, 815, 1000, 1, 0, 3, Cell 50 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 816, 831, 1000, 1, 0, 3, Cell 51 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 832, 847, 1000, 1, 0, 3, Cell 52 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 848, 863, 1000, 1, 0, 3, Cell 53 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 864, 879, 1000, 1, 0, 3, Cell 54 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 88, 103, 1, 1, 0, 0, Raw t c04: %4ld, unknown, 0x2104, 0x6104, , 1, 10, 0\n"
                        +"0x7bb, 88, 103, 1, 1, 0, 0, Raw t c04: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 880, 895, 1000, 1, 0, 3, Cell 55 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 896, 911, 1000, 1, 0, 3, Cell 56 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 912, 927, 1000, 1, 0, 3, Cell 57 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 928, 943, 1000, 1, 0, 3, Cell 58 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 944, 959, 1000, 1, 0, 3, Cell 59 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 96, 111, 1000, 1, 0, 3, Cell 06 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 96, 111, 1000, 1, 0, 3, Cell 68 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 960, 975, 1000, 1, 0, 3, Cell 60 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 976, 991, 1000, 1, 0, 3, Cell 61 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 992, 1007, 1000, 1, 0, 3, Cell 62 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bc, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x7bc, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x7bc, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7bc, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7da, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x7da, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x7da, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7da, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7ec, 0, 23, 1, 1, 0, 0, Query DTC, , 0x19023b, 0x5902ff, Variable length!!!!, 0, 0, 0\n"
                        +"0x7ec, 0, 7, 1, 1, 0, 0, Reset DTC, , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x7ec, 128, 143, 1, 1, 0, 0, PG number %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7ec, 144, 159, 1, 1, 0, 0, SW ver %04lx, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 1, 2, 0, 2, SOC, %, 0x222002, 0x622002, , 2, 0, 0\n"
                        +"0x7ec, 24, 39, 100, 1, 0, 2, Speed, km/h, 0x222003, 0x622003, , 1, 0, 0\n"
                        +"0x7ec, 24, 39, 100, 50, 0, 2, Motor Volt, V, 0x222004, 0x622004, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 48, 100, 0, 2, SOC, %, 0x222002, 0x622002, , 1, 0, 0\n"
                        +"0x7ec, 24, 39, 100, 1, 0, 2, Speed, km/h, 0x222003, 0x622003, , 1, 0, 0\n"
                        +"0x7ec, 24, 39, 100, 50, 0, 2, Motor Volt, V, 0x222004, 0x622004, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 100, 1, 0, 2, 12V Volt, V, 0x222005, 0x622005, , 0, 0, 0\n"
                        +"0x7ec, 24, 47, 1, 1, 0, 0, Odometer, km, 0x222006, 0x622006, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 1, 1, 0, 0, Pedal, , 0x22202e, 0x62202e, , 0, 0, 0\n"
                        +"0x7ec, 24, 31, 1, 1, 0, 0, Steering wheel CC/SL buttons, , 0x22204b, 0x62204b, , 1, 0, 0\n"
                        +"0x7ec, 24, 39, 100, 50, 0, 2, Volt, V, 0x223203, 0x623203, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 100, 25, 0x8000, 2, Amp, A, 0x223204, 0x623204, , 0, 0, 0\n"
                        +"0x7ec, 24, 31, 1, 1, 0, 0, Bat Health, %, 0x223206, 0x623206, , 0, 0, 0\n"
                        +"0x7ec, 24, 31, 1, 1, 40, 0, Ext temp, C, 0x2233b1, 0x6233b1, , 0, 0, 0\n"
*/
                        // 2015.11.14

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
                        +"0x4f8,0,1,-1,-2,0,,,,0\n" // Start
                        +"0x4f8,4,5,-1,-2,0,,,,0\n" // Parking Break
                        +"0x4f8,24,39,0.01,0,2,,,,2\n" // Speed on Display
                        +"0x5d7,0,15,0.01,0,2,km/h,,,0\n" // Speed
                        +"0x5d7,16,43,0.01,0,2,km,,,0\n" // Odometer
                        +"0x5d7,44,45,1,0,0,?,,,0\n" // WheelsLockingState
                        +"0x5d7,48,49,1,0,0,?,,,0\n" // VehicleSpeedSign
                        +"0x5d7,50,54,0.04,0,2,cm,,,0\n" // Fine distance
                        +"0x5de,1,1,1,0,0,,,,0\n" // Right Indicator
                        +"0x5de,2,2,1,0,0,,,,0\n" // Left Indicator
                        +"0x5de,5,5,1,0,0,,,,0\n" // Park Light
                        +"0x5de,6,6,1,0,0,,,,0\n" // Head Light
                        +"0x5de,7,7,1,0,0,,,,0\n" // Beam Light
                        +"0x5de,12,12,1,0,0,,,,0\n" // Door Front Left
                        +"0x5de,14,14,1,0,0,,,,0\n" // Dort Front Right
                        +"0x5de,17,17,1,0,0,,,,0\n" // Door Rear Left
                        +"0x5de,19,19,1,0,0,,,,0\n" // Door Rear Right
                        +"0x5de,59,59,1,0,0,,,,0\n" // Door Hatch
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
                        +"0x656,3,3,1,0,0,,,,\n" // Trip Data Reset
                        +"0x656,21,31,1,0,0,min,,,\n" // Cluste rScheduled Time
                        +"0x656,32,42,1,0,0,min,,,\n" // Cluster Scheduled Time 2
                        +"0x656,48,55,1,40,0,°C,,,\n" // External Temp
                        +"0x656,56,57,1,0,0,,,,\n" // ClimPCCustomerActiv
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
                        +"0x763,3,3,1,0,0,,0x2220f0,0x6220f0,0\n" // << UDS replies start at bit 24 - need to recheck
                        +"0x763,4,4,1,0,0,,0x2220f0,0x6220f0,0\n" // VOL-
                        +"0x763,2,2,1,0,0,,0x2220f0,0x6220f0,0\n" // Mute
                        +"0x763,5,5,1,0,0,,0x2220f0,0x6220f0,0\n" // Media
                        +"0x763,6,6,1,0,0,,0x2220f0,0x6220f0,0\n" // Radio
                        +"0x764,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x764,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x764,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x764,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
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
                        +"0x77e,24,31,31.25,0,3,V,0x22300e,0x62300e,0\n" // dcdc state
                        +"0x77e,24,31,0.03125,0,0,Nm,0x223024,0x623024,0\n" // traction battery voltage
                        +"0x77e,24,31,0.03125,0,0,Nm,0x223025,0x623025,0\n" // torque requested
                        +"0x77e,24,31,1.5625,0,2,°C,0x22302b,0x62302b,0\n" // torque applied
                        +"0x77e,24,31,6.25,0,2,A,0x22301d,0x62301d,0\n" // inverter temperature
                        +"0x793,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x793,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number %04lx
                        +"0x793,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x793,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7b6,144,159,1,0,0,,0x2180,0x6180,0\n" // Software version
                        +"0x7b6,128,143,1,0,0,,0x2180,0x6180,0\n" // PG number
                        +"0x7b6,0,7,1,0,0,,0x14ffff,0x54,0\n" // Reset DTC
                        +"0x7b6,0,23,1,0,0,,0x19023b,0x5902ff,0\n" // Query DTC
                        +"0x7bb,336,351,0.01,0,2,kW,0x2101,0x6101,2\n" // Get regen and other battery data.
                        +"0x7bb,192,207,0.01,0,2,kW,0x2101,0x6101,1\n" // Maximum battery input power
                        +"0x7bb,208,223,0.01,0,2,kW,0x2101,0x6101,1\n" // Maximum battery output power
                        +"0x7bb,348,367,0.0001,0,4,Ah,0x2101,0x6101,1\n" // Ah of the battery
                        +"0x7bb,316,335,0.0001,0,4,%,0x2101,0x6101,1\n" // Real State of Charge
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
                        +"0x7bb,136,151,1,0,0,kWh,0x2161,0x6161,1\n" // Energy regenerated into the Battery
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
                    Field field = new Field(
                            frame,
                            Integer.parseInt(tokens[FIELD_FROM].trim()),
                            Integer.parseInt(tokens[FIELD_TO].trim()),
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
                            Integer.parseInt(tokens[FIELD_CAR].trim())
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
        Field tryField = fieldsBySid.get(getCar()+"."+sid);
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
                        int val = Integer.parseInt("0" + binString.substring(field.getFrom(), field.getTo() + 1), 2);
                        //MainActivity.debug("Fields: onMessageCompleteEvent > "+field.getSID()+" = "+val);
                        field.setValue(val);
                        // update the fields last request date
                        field.updateLastRequest();
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

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

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
