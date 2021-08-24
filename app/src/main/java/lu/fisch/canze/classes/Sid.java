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

    public static final String TpmsState                            = "765.6171.16";

    public static final String BcbTesterInit                        = "793.50c0.0";
    public static final String BcbTesterAwake                       = "793.7e01.0";
    public static final String BcbVersion                           = "793.6180.144";
    public static final String Phase1currentRMS                     = "793.622001.24";
    public static final String MainsCurrentType                     = "793.625017.29";
    public static final String ACPilotDutyCycle                     = "793.625026.24";
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
    public static final String Preamble_KM                          = "7ec.6233d4."; // 240 - 24
    public static final String Preamble_END                         = "7ec.6233d5."; //  96 -  8
    public static final String Preamble_TYP                         = "7ec.6233d6."; //  96 -  8
    public static final String Preamble_SOC                         = "7ec.6233d7."; // 168 - 16
    public static final String Preamble_TMP                         = "7ec.6233d8."; //  96 -  8
    public static final String Preamble_DUR                         = "7ec.6233d9."; // 168 - 16
    public static final String TripMeterB                           = "7ec.6233de.24";
    public static final String TripEnergyB                          = "7ec.6233dd.24";
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
}
