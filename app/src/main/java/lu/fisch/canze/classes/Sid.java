package lu.fisch.canze.classes;

public class Sid {
    public static final String DriverBrakeWheel_Torque_Request      = "130.44"; //UBP braking wheel torque the driver wants
    public static final String MeanEffectiveTorque                  = "186.16";
    public static final String Pedal                                = "186.40"; //EVC
    public static final String Coasting_Torque                      = "18a.27";
    public static final String TotalPotentialResistiveWheelsTorque  = "1f8.16"; // UBP 10ms
    public static final String ElecBrakeWheelsTorqueApplied         = "1f8.28"; //10ms
    public static final String Aux12A                               = "1fd.0";
    public static final String VehicleState                         = "35c.5";
    public static final String SoC                                  = "42e.0"; //EVC
    public static final String EngineFanSpeed                       = "42e.20";
    public static final String ACPilotAmps                          = "42e.38";
    public static final String ChargingPower                        = "42e.56";
    public static final String HvCoolingState                       = "430.38";
    public static final String HvEvaporationTemp                    = "430.40";
    public static final String BatteryConditioningMode              = "432.36";
    public static final String ClimaLoopMode                        = "42a.48";
    public static final String UserSoC                              = "42e.0";
    public static final String AvailableChargingPower               = "427.40";
    public static final String AvailableEnergy                      = "427.49";
    public static final String HvTemp                               = "42e.44";
    public static final String RealSpeed                            = "5d7.0";  //ESC-ABS
    public static final String AuxStatus                            = "638.37";
    public static final String WorstAverageConsumption              = "62d.0";
    public static final String BestAverageConsumption               = "62d.10";
    public static final String PlugConnected                        = "654.2";
    public static final String RangeEstimate                        = "654.42";
    public static final String AverageConsumption                   = "654.52";
    public static final String ChargingStatusDisplay                = "65b.41";
    public static final String TireSpdPresMisadaption               = "673.0";
    public static final String TireRRState                          = "673.2";
    public static final String TireRLState                          = "673.5";
    public static final String TireFRState                          = "673.8";
    public static final String TireFLState                          = "673.11";
    public static final String TireRRPressure                       = "673.16";
    public static final String TireRLPressure                       = "673.24";
    public static final String TireFRPressure                       = "673.32";
    public static final String TireFLPressure                       = "673.40";
    public static final String HeaterSetpoint                       = "699.8";

    public static final String ThermalComfortPower                  = "764.6143.88";
    public static final String Pressure                             = "764.6143.134";
    public static final String OH_ClimTempDisplay                   = "764.6145.29";
    public static final String OHS_ClimTempDisplay                  = "7ec.62352e.24"; // Spring,7ec,24,31,.5,0,1,,22352E,62352E,ff,($352E) ClimTempDisplay,,,
    public static final String TpmsState                            = "765.6171.16";

    public static final String BcbTesterInit                        = "793.50c0.0";
    public static final String BcbTesterAwake                       = "793.7e01.0";
    public static final String BcbVersion                           = "793.6180.144";
    public static final String Phase1currentRMS                     = "793.622001.24";
    public static final String MainsCurrentType                     = "793.625017.29";
    public static final String ACPilotDutyCycle                     = "793.625026.24";
    public static final String ACSPilotDutyCycle                    = "776.625026.24"; // ,776,24,39,.1,0,1,%,225026,625026,ff,CP_DutyCycle_raw,
    public static final String PhaseVoltage1                        = "793.62502c.24"; // Raw
    public static final String PhaseVoltage2                        = "793.62502d.24";
    public static final String PhaseVoltage3                        = "793.62502e.24";
    public static final String Phase2CurrentRMS                     = "793.62503a.24"; // Raw <= this seems to be instant DC coupled value
    public static final String Phase3CurrentRMS                     = "793.62503b.24";
    public static final String InterPhaseVoltage12                  = "793.62503f.24"; // Measured
    public static final String InterPhaseVoltage23                  = "793.625041.24";
    public static final String InterPhaseVoltage31                  = "793.625042.24";
    public static final String MainsActivePower                     = "793.62504a.24";
    public static final String GroundResistance                     = "793.625062.24";
    public static final String SupervisorState                      = "793.625063.24";
    public static final String CompletionStatus                     = "793.625064.24";

    public static final String MaxCharge                            = "7bb.6101.336";
    public static final String HVSoC                                = "7ec.622002.24";
    public static final String RealSoC                              = "7bb.6103.192";
    public static final String AverageBatteryTemperature            = "7bb.6104.600";   // (LBC)
    public static final String Preamble_CompartmentTemperatures     = "7bb.6104."; // (LBC)
    public static final String Preamble_BalancingBytes              = "7bb.6107.";
    public static final String Preamble_CellVoltages1               = "7bb.6141."; // (LBC)
    public static final String Preamble_CellVoltages2               = "7bb.6142."; // (LBC)
    public static final String HvKilometers                         = "7bb.6161.96";
    public static final String Total_kWh                            = "7bb.6161.120";
    public static final String BatterySerial                        = "7bb.6162.16"; //EVC
    public static final String Counter_Full                         = "7bb.6166.48";
    public static final String Counter_Partial                      = "7bb.6166.64";

    public static final String HydraulicTorqueRequest               = "7bc.624b7d.28"; // Total Hydraulic brake wheels torque request

    public static final String CCSEVRequestState                    = "7c8.620326.28";
    public static final String CCSEVReady                           = "7c8.620329.31";
    public static final String CCSEVSEState                         = "7c8.62032c.28";
    public static final String CCSEVSECurrentLimitReached           = "7c8.62032d.31";
    public static final String CCSEVSEPowerLimitReached             = "7c8.620334.31";
    public static final String CCSEVSEPresentCurrent                = "7c8.620335.24";
    public static final String CCSEVSEPresentVoltage                = "7c8.620336.24";
    public static final String CCSEVSEVoltageLimitReaced            = "7c8.620337.31";
    public static final String CCSFailureStatus                     = "7c8.62033b.24";
    public static final String CCSCPLCComStatus                     = "7c8.62033a.28";
    public static final String CCSEVSEStatus                        = "7c8.62033c.24";

    public static final String EVC                                  = "7ec.5003.0"; // EVC open Note we use 7ec as the EVC has custom SID codes for older model compatilbility
    public static final String Aux12V                               = "7ec.622005.24";
    public static final String EVC_Odometer                         = "7ec.622006.24";
    public static final String TorqueRequest                        = "7ec.622243.24";
    public static final String DcLoad                               = "7ec.623028.24";
    public static final String TractionBatteryVoltage               = "7ec.623203.24";
    public static final String TractionBatteryCurrent               = "7ec.623204.24";
    public static final String SOH                                  = "7ec.623206.24";
    public static final String SOHAvg                               = "658.33"; // 658.33,7bb,24,39,.01,0,2,%,229003,629003,ff,Battery SOHZxx_sohe_avg
    public static final String Preamble_KM                          = "7ec.6233d4."; // 240 - 24
    public static final String Preamble_END                         = "7ec.6233d5."; //  96 -  8
    public static final String Preamble_TYP                         = "7ec.6233d6."; //  96 -  8
    public static final String Preamble_SOC                         = "7ec.6233d7."; // 168 - 16
    public static final String Preamble_TMP                         = "7ec.6233d8."; //  96 -  8
    public static final String Preamble_DUR                         = "7ec.6233d9."; // 168 - 16
    public static final String TripMeterB                           = "7ec.6233de.24"; // ,7ec,24,55,.0001,0,3,km,2233DE,6233DE,1ff,($33DE) This variable allows to memorize the reseted distance,,,
    public static final String TripEnergyB                          = "7ec.6233dd.24"; //,7ec,24,47,.001,0,3,kWh,2233DD,6233DD,ff,($33DD) This variable allows to memorize the reseted energy consumed,,,
    public static final String CurrentUnderLoad                     = "7ec.623484.24"; // Current measurement given by BCS Battery Current Sensor
    public static final String VoltageUnderLoad                     = "7ec.623485.24"; // Voltage measurement given by BCS Battery Current Sensor
    public static final String PtcRelay1                            = "7ec.623498.31";
    public static final String PtcRelay2                            = "7ec.62349a.31";
    public static final String PtcRelay3                            = "7ec.62349c.31";

    public static final String PEBTorque                            = "77e.623025.24";

    public static final String Instant_Consumption                  = "800.6100.24";
    public static final String FrictionTorque                       = "800.6101.24";
    public static final String DcPowerIn                            = "800.6103.24"; // Virtual field
    public static final String DcPowerOut                           = "800.6109.24";
    public static final String ElecBrakeTorque                      = "800.610a.24";
    public static final String TotalPositiveTorque                  = "800.610b.24";
    public static final String TotalNegativeTorque                  = "800.610c.24";
    public static final String ACPilot                              = "800.610d.24";

    public static final String CCSEVSEMaxPower                      = "18daf1da.623006.24";
    public static final String CCSEVSEMaxVoltage                    = "18daf1da.623008.24";
    public static final String CCSEVSEMaxCurrent                    = "18daf1da.62300a.24";

    public static final String Total_Regen_kWh                      = "18daf1db.629247.24"; // Ph2 only (for now)
    public static final String Total_Spring_Regen_kWh               = "7bb.629247.24"; // Dacia Spring

    // data from instrument cluster TDB for Spring
    public static final String ExternalTemp                         = "763.62200c.24"; // ,763,24,31,1,40,0,°C,22200C,62200C,ff,External_Temperature.Temp,
    public static final String RealOdometer                        = "763.62f0d0.24"; //,763,24,47,1,0,0,km,22F0D0,62F0D0,1ff,Odometer of the last occurence,

    //clim
    public static final String ClimBlowerSpeed                      = "7ec.6233a4.24"; // Spring,7ec,24,31,2,0,0,%,2233A4,6233A4,ff,($33A4) Measure of the speed of the cabin blower,,, OK,,
    public static final String ClimACConsumption                    = "7ec.6233a7.24"; // Spring,7ec,24,31,25,0,0,W,2233A7,6233A7,ff,($33A7) AC compressor Power Consumption Estimation,,,
    public static final String ClimACCompressorState                = "7ec.62343b.30"; // Spring,7ec,30,31,1,0,0,,22343B,62343B,ff,($343B) AC compressor State,0:Signal Invalid;1:Compressor Off;2:Compressor is starting;3:Compressor On,,
    public static final String ClimACCompressorAuth                 = "7ec.623435.30"; // Spring,7ec,30,31,1,0,0,,223435,623435,ff,($3435) AC compressor authorization command,0:Disable;1:Enable;2:Not used;3:Signal invalid,,
    public static final String ClimACCompressorRPM                  = "7ec.623436.24"; // Spring,7ec,24,39,1,0,0,rpm,223436,623436,ff,($3436) AC compressor RPM real value,,,

    public static final String ClimACCompressorVoltage              = "7ec.623437.24"; // Spring,7ec,24,31,2,0,0,V,223437,623437,ff,($3437) AC compressor High Voltage value,,,
    public static final String ClimACCompressorTemp                 = "7ec.623438.24"; // Spring,7ec,24,31,1,40,0,°C,223438,623438,ff,($3438) AC compressor Power Module Temperature,,,
    public static final String ClimACCompressorError                = "7ec.623439.30"; // Spring,7ec,30,31,1,0,0,,223439,623439,ff,($3439) AC compressor Error Status,0:No problem;1:Degraded mode;2:Compressor shut-off safety reasons;3:Compressor has to te changed,,

    public static final String BCBWaterTemp                         = "7ec.623109.25"; // Spring,7ec,25,31,1,0,0,°C,223109,623109,ff,($3109) BCB water temperature,,,
    public static final String BCBInternalTemp                      = "7ec.62310a.24"; // Spring,7ec,24,31,1,0,0,%,22310A,62310A,ff,($310A) BCB internal temperature,,,
    public static final String HeatWaterTemp                        = "7ec.623307.24"; // Spring,7ec,24,31,1,40,0,°C,223307,623307,ff,($3307) Heat water temperature,,,
    public static final String FanCabinStatus                       = "7ec.62332a.31"; // Spring,7ec,31,31,1,0,0,,22332A,62332A,ff,($332A) Fan cabin status,0:Blower OK;1:Blower not OK,,
    public static final String ETSState                             = "7ec.62332b.29"; // Spring,7ec,29,31,1,0,0,,22332B,62332B,ff,($332B) ETS state,0:ETS_Standby;1:ETS_WakeUp;2:ETS_Comfort;3:ETS_Driving;4:ETS_QuickDrop,,
//    public static final String FreonPressure                        = "7ec.623376.24"; // Spring,7ec,24,39,.1,0,1,bar,223376,623376,ff,($3376) Measure of the freon pressure,,,
    public static final String FreonPressure                        = "7ec.623372.24"; // Spring,7ec,24,31,5,0,0,mbar,223372,623372,ff,($3372) Current pressure in the mastervac,,,
    public static final String FreonPressureVoltage                 = "7ec.62200d.24"; // Spring,7ec,24,39,.01,0,2,V,22200D,62200D,ff,($200D) Freon pressure sensor voltage,,,
    public static final String FreonPressureSensor                  = "7ec.622043.31"; // Spring,7ec,31,31,1,0,0,,222043,622043,ff,($2043) Configuration _ Freon pressure sensor,0:without;1:with,,

    public static final String ClimACCompressorInterlock            = "7ec.623377.30"; // Spring,7ec,30,31,1,0,0,,223377,623377,ff,($3377) AC compressor Interlock State,0:Unvailable Value;1:Open;2:Closed;3:Sensor Unavailable,,
    public static final String FanActivation                        = "7ec.6233a8.24"; // Spring,7ec,24,31,1,0,0,,2233A8,6233A8,ff,($33A8) Request of FAN activation from the thermal comfort,0:no FAN requested;1:(61- low- 38- very low) speed fan request;2:61- not used- 38- low speed fan request;3:high speed fan request,,
    public static final String EngineFanSpeedCounter                = "7ec.6233f7.24"; // Spring,7ec,24,55,1,0,0,,2233F7,6233F7,1ff,($33F7) Counter of Efan speed on Low and very low speed,,,
}
